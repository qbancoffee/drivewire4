package com.groupunix.drivewireserver.dwprotocolhandler;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import org.apache.log4j.Logger;

import com.groupunix.drivewireserver.DriveWireServer;

public class DWTCPDevice implements DWProtocolDevice {

	private static final Logger logger = Logger.getLogger("DWServer.DWTCPDevice");
	private int tcpport;
	private int handlerno;
	private ServerSocket srvr;
	private Socket skt = null;
	private boolean bytelog = false;
	private String client = null;
	
	public DWTCPDevice(int handlerno, int tcpport) throws IOException 
	{
		this.handlerno = handlerno;
		this.tcpport = tcpport;
		
		bytelog = DriveWireServer.getHandler(this.handlerno).getConfig().getBoolean("LogDeviceBytes",false);
		
		logger.debug("init tcp device server on port " + tcpport + " for handler #" + handlerno + " (logging bytes: " + bytelog + ")");
		
		// check for listen address
			
		if (DriveWireServer.getHandler(this.handlerno).getConfig().containsKey("ListenAddress"))
		{
			srvr = new ServerSocket(this.tcpport, 0, InetAddress.getByName(DriveWireServer.getHandler(this.handlerno).getConfig().getString("ListenAddress")) );
		}
		else
		{
			srvr = new ServerSocket(this.tcpport, 0);
		}
		
		logger.info("listening on port " + srvr.getLocalPort());
		
	}


	public void close() 
	{
		logger.debug("closing tcp device in handler #" + this.handlerno);
		
		closeClient();
		
		try 
		{
			srvr.close();
		} 
		catch (IOException e) 
		{
			logger.debug(e.getMessage());
		}
		
	}

	
	
	private void closeClient() 
	{
		logger.debug("closing client connection");
		
		if ((skt != null) && (!skt.isClosed()))
		{
			try 
			{
				skt.close();
			} 
			catch (IOException e) 
			{
				logger.debug(e.getMessage());
			}
		}
		
		client = null;
		skt = null;
	}


	public byte[] comRead(int len) throws IOException 
	{

		byte[] buf = new byte[len];
		
		for (int i = 0;i<len;i++)
		{
			buf[i] = (byte) comRead1(true);
		}
		
		return(buf);

	}

	
	public int comRead1(boolean timeout) throws IOException 
	{
		int data = -1;
		
		if (skt == null)
		{
			getClientConnection();
		}
		
		if (skt != null)
		{
			try 
			{
				data = skt.getInputStream().read();
			} 
			catch (IOException e) 
			{
				//e.printStackTrace();
				closeClient();
			}
		
			if (data < 0)
			{
				// read problem
			
				logger.debug("socket error reading device");
			
				closeClient();
			
				// call ourselves to get another byte... not sure this is a great idea
				return comRead1(timeout);
			}
			
			if (bytelog)
				logger.debug("TCPREAD: " + data);
		}
		
		return data;
	}

	
	
	public void comWrite(byte[] data, int len, boolean prefix) 
	{
		if ((skt != null) && (!skt.isClosed()))
		try 
		{
			skt.getOutputStream().write(data, 0, len);
			
			if (bytelog)
			{
				String tmps = new String();
			
				for (int i = 0;i< data.length;i++)
				{
					tmps += " " + (int)(data[i] & 0xFF);
				}
			
				logger.debug("WRITE " + data.length + ":" + tmps);
			}
		} 
		catch (IOException e) 
		{
			
			logger.error(e.getMessage());
			
		}
	}

	
	public void comWrite1(int data, boolean prefix) 
	{
		if ((skt != null) && (!skt.isClosed()))
		try 
		{
			skt.getOutputStream().write((byte) data);
			
			if (bytelog)
				logger.debug("TCPWRITE1: " + data);
			
		} 
		catch (IOException e) 
		{
			// problem with comm port, bail out
			logger.error(e.getMessage());
			
		}
		
	}


	public boolean connected() 
	{
		if (skt == null)
			return false;
		
		return true;
	}


	public void shutdown() 
	{
			
		close();
	}

	private void getClientConnection()
	{
		logger.debug("waiting for client...");
		try 
		{
			skt = srvr.accept();
		} 
		catch (IOException e1) 
		{
			logger.debug("IO error while listening for client: " + e1.getMessage());
			return;
		}
		
		logger.debug("new client connect from " + skt.getInetAddress().getCanonicalHostName());
		
		this.client  = skt.getInetAddress().getCanonicalHostName();
		
		try 
		{
			skt.setTcpNoDelay(true);
		} 
		catch (SocketException e) 
		{
			logger.warn(e.getMessage());
		}
	}
	
	public int getRate()
	{
		// doesn't make sense here?
		return(-1);
	}


	
	public String getDeviceName() 
	{
		return("listen:" + this.tcpport);
	}


	
	public String getDeviceType() 
	{
		return("tcp");
	}


	
	public String getClient() 
	{
		return this.client;
	}


	
	public InputStream getInputStream() {
		// TODO Auto-generated method stub
		return null;
	}



	
}
