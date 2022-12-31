package com.groupunix.drivewireserver.virtualserial;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import org.apache.log4j.Logger;

import com.groupunix.drivewireserver.DWDefs;
import com.groupunix.drivewireserver.dwexceptions.DWPortNotValidException;
import com.groupunix.drivewireserver.dwprotocolhandler.DWVSerialProtocol;

public class DWVPortTCPConnectionThread implements Runnable {

	private static final Logger logger = Logger.getLogger("DWServer.DWVPortTCPConnectionThread");
	
	private int vport = -1;
	private int tcpport = -1;
	private String tcphost = null;
	
	private boolean wanttodie = false;

	private DWVSerialPorts dwVSerialPorts;

	private boolean reportConnect = true;

	private byte[] wcdata = null;

	
	private SocketChannel sktchan;
	private InetSocketAddress sktaddr;
	
	
	public DWVPortTCPConnectionThread(DWVSerialProtocol dwProto, int vport, String tcphostin, int tcpportin)
	{
		logger.debug("init tcp connection thread");	
		this.vport = vport;
		this.tcpport = tcpportin;
		this.tcphost = tcphostin;

		this.dwVSerialPorts = dwProto.getVPorts();
		
	}
	

	public DWVPortTCPConnectionThread(DWVSerialProtocol dwProto, int vport, String tcphostin, int tcpportin, boolean rc)
	{
		logger.debug("init tcp connection thread");	
		this.vport = vport;
		this.tcpport = tcpportin;
		this.tcphost = tcphostin;
		this.reportConnect  = rc;

		this.dwVSerialPorts = dwProto.getVPorts();
		
	}


	public DWVPortTCPConnectionThread(DWVSerialProtocol dwProto, int vport, String tcphostin, int tcpportin, boolean rc, byte[] wcdata)
	{
		logger.debug("init NineServer connection thread");	
		this.vport = vport;
		this.tcpport = tcpportin;
		this.tcphost = tcphostin;
		this.reportConnect  = rc;
		this.wcdata  = wcdata;
		
		this.dwVSerialPorts = dwProto.getVPorts();
	}


	public void run() 
	{
		Thread.currentThread().setName("tcpconn-" + Thread.currentThread().getId());
		Thread.currentThread().setPriority(Thread.NORM_PRIORITY);
		
		logger.debug("run");
		
		
		// try to establish connection
		try 
		{
			sktaddr = new InetSocketAddress(this.tcphost, this.tcpport);
			sktchan = SocketChannel.open();
			sktchan.configureBlocking(true);
			
			sktchan.connect(sktaddr);
			
			if (sktchan.finishConnect())
			{
				dwVSerialPorts.setPortChannel(vport, this.sktchan);
				
				if (this.reportConnect)
					dwVSerialPorts.writeToCoco(this.vport, ("OK Connected to " + this.tcphost + ":" + this.tcpport + (char) 10 + (char) 13));
					
				dwVSerialPorts.markConnected(vport);
				
				logger.debug("Connected to " + this.tcphost + ":" + this.tcpport);
			
				dwVSerialPorts.setUtilMode(vport, DWDefs.UTILMODE_TCPOUT);
				
				if (this.wcdata != null)
				{
					dwVSerialPorts.write(this.vport, new String(this.wcdata) );
				}
				
			}
		} 
		catch (UnknownHostException e) 
		{
			logger.debug("unknown host " + tcphost );
			
			if (this.reportConnect)
				try
				{
					dwVSerialPorts.sendUtilityFailResponse(this.vport, DWDefs.RC_NET_UNKNOWN_HOST,"Unknown host '" + this.tcphost + "'");
				} 
				catch (DWPortNotValidException e1)
				{
					logger.warn(e1.getMessage());
				}
			this.wanttodie = true;
		} 
		catch (IOException e1) 
		{
			logger.debug("IO error: " + e1.getMessage());
			
			if (this.reportConnect)
				try
				{
					dwVSerialPorts.sendUtilityFailResponse(this.vport, DWDefs.RC_NET_IO_ERROR, e1.getMessage());
				} 
				catch (DWPortNotValidException e)
				{
					logger.warn(e1.getMessage());
				}
			this.wanttodie = true;
		} 
		catch (DWPortNotValidException e)
		{
			logger.warn(e.getMessage());
		}
		
		byte[] readbytes = new byte[256];
		ByteBuffer readBuffer = ByteBuffer.wrap(readbytes);
		
		
		while ((wanttodie == false) && (sktchan.isOpen()) && (dwVSerialPorts.isOpen(this.vport)))
		{
			try 
			{
				int readsize = sktchan.read(readBuffer);
				
				if (readsize == -1)
				{
					logger.debug("got end of input stream");
					wanttodie = true;
				}
				else if (readsize > 0)
				{
					
					
					dwVSerialPorts.writeToCoco(this.vport, readbytes, 0, readsize);
					readBuffer.clear();
				}
				
			} 
			catch (IOException e) 
			{
					logger.debug("IO error reading tcp: " + e.getMessage());
					wanttodie = true;
			} 
			catch (DWPortNotValidException e) 
			{
				logger.error(e.getMessage());
				
				wanttodie = true;
			}
			
		}
	
		
		if (wanttodie)
			logger.debug("exit because wanttodie");
		else if (!sktchan.isConnected())
			logger.debug("exit because skt isClosed");
		else if (!dwVSerialPorts.isOpen(this.vport))
			logger.debug("exit because port is not open");			
		
		
		
		// only if we got connected..
		if (sktchan != null)
		{
			if (sktchan.isConnected())
			{
		
				logger.debug("exit stage 1, flush buffer");
		
				// 	flush buffer, term port
				try {
					while ((dwVSerialPorts.bytesWaiting(this.vport) > 0) && (dwVSerialPorts.isOpen(this.vport)))
					{
						logger.debug("pause for the cause: " + dwVSerialPorts.bytesWaiting(this.vport) + " bytes left" );
						Thread.sleep(100);
					}
				} 
				catch (InterruptedException e) 
				{
					logger.error(e.getMessage());
				} 
				catch (DWPortNotValidException e) 
				{
					logger.error(e.getMessage());
				}
		
				logger.debug("exit stage 2, send peer signal");
		
				try 
				{
					dwVSerialPorts.closePort(this.vport);
				} 
				catch (DWPortNotValidException e) 
				{
					logger.error("in close port: " + e.getMessage());
				}
			}
			
			logger.debug("thread exiting");
		}
	}	
}

	