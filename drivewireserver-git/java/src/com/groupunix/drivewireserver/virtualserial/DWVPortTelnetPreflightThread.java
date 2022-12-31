package com.groupunix.drivewireserver.virtualserial;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.channels.SocketChannel;

import org.apache.log4j.Logger;

import com.groupunix.drivewireserver.DriveWireServer;
import com.groupunix.drivewireserver.dwexceptions.DWPortNotValidException;
import com.groupunix.drivewireserver.dwprotocolhandler.DWVSerialProtocol;

public class DWVPortTelnetPreflightThread implements Runnable
{

	private static final Logger logger = Logger.getLogger("DWServer.DWVPortTelnetPreflightThread");
	
	
	private int vport;

	private boolean banner = false;
	private boolean telnet = false;
	

	private DWVSerialPorts dwVSerialPorts;
	private DWVSerialProtocol dwProto;


	private SocketChannel sktchan;
	
	public DWVPortTelnetPreflightThread(DWVSerialProtocol dwProto2, int vport, SocketChannel sktchan, boolean doTelnet, boolean doBanner)
	{
		this.vport = vport;
		this.sktchan = sktchan;

		this.banner = doBanner;
		this.telnet = doTelnet;
		this.dwProto = dwProto2;
		this.dwVSerialPorts = dwProto2.getVPorts();
		
	}

	public void run()
	{
		Thread.currentThread().setName("tcppre-" + Thread.currentThread().getId());
		Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
		
		logger.debug("preflight checks for new connection from " + sktchan.socket().getInetAddress().getHostName());
		
		try
		{
			// hello
			if (this.telnet)
				sktchan.socket().getOutputStream().write(("DriveWire Telnet Server\r\n\n").getBytes());


			if (telnet == true)
			{
				// ask telnet to turn off echo, should probably be a setting or left to the client
				byte[] buf = new byte[9];
		
				buf[0] = (byte) 255;
				buf[1] = (byte) 251;
				buf[2] = (byte) 1;
				buf[3] = (byte) 255;
				buf[4] = (byte) 251;
				buf[5] = (byte) 3;
				buf[6] = (byte) 255;
				buf[7] = (byte) 253;
				buf[8] = (byte) 243;
		
		
				sktchan.socket().getOutputStream().write(buf, 0, 9);
		
				// 	read back the echoed controls - TODO has issues
		
				for (int i = 0; i<9; i++)
				{
					sktchan.socket().getInputStream().read();
				}
			}
				
			
			if (sktchan.socket().isClosed())
			{
				// bail out
				logger.debug("thread exiting after auth");
				return;
			}
			
			
			if ((dwProto.getConfig().containsKey("TelnetBannerFile")) && (banner == true))
			{
				displayFile(sktchan.socket().getOutputStream(), dwProto.getConfig().getString("TelnetBannerFile"));
			}
			
		} 
		catch (IOException e)
		{
			logger.warn("IOException: " + e.getMessage());
			
			if (sktchan.isConnected())
			{
				logger.debug("closing socket");
				try
				{
					sktchan.close();
				} catch (IOException e1)
				{
					logger.warn(e1.getMessage());
				}
			
			}
			
		}
			
		
		if (sktchan.isConnected())
		{
			
			//add connection to pool
			int conno = this.dwVSerialPorts.getListenerPool().addConn(this.vport, sktchan, 1);

			
			// announce new connection to listener
			try 
			{
				dwVSerialPorts.sendConnectionAnnouncement(this.vport, conno, sktchan.socket().getLocalPort(), sktchan.socket().getInetAddress().getHostAddress());
			} 
			catch (DWPortNotValidException e) 
			{
				logger.error("in announce: " + e.getMessage());
			}
					
		}
		
		logger.debug("exiting");
	}

	

	
	

	


	


	private void displayFile(OutputStream outputStream, String fname) 
	{
		FileInputStream fstream;
		
		try 
		{
			fstream = new FileInputStream(fname);
		
			DataInputStream in = new DataInputStream(fstream);
				
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
				
			String strLine;
			   
			logger.debug("sending file '" + fname + "' to telnet client");
			
			while ((strLine = br.readLine()) != null)
			{
				  outputStream.write(strLine.getBytes());
				  outputStream.write("\r\n".getBytes());
			}
			
			fstream.close();
			
		} 
		catch (FileNotFoundException e) 
		{
			logger.warn("File not found: " + fname);
		} 
		catch (IOException e1) 
		{
			logger.warn(e1.getMessage());
		}
		 
		 
	}
	
	
	
	
	
}
