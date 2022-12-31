package com.groupunix.drivewireserver.virtualserial;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import org.apache.log4j.Logger;

import com.groupunix.drivewireserver.dwexceptions.DWConnectionNotValidException;
import com.groupunix.drivewireserver.dwexceptions.DWPortNotValidException;
import com.groupunix.drivewireserver.dwprotocolhandler.DWVSerialProtocol;

public class DWVPortTermThread implements Runnable 
{

	private static final Logger logger = Logger.getLogger("DWServer.DWVPortTermThread");
	
	private int tcpport;
	private boolean wanttodie = false;
	private int vport = 0;  // port 0 is always Term now..
	
	private static final int TERM_PORT = 0;
	private static final int MODE_TERM = 3;
	private static final int BACKLOG = 0;
	
	private Thread connthread;
	private DWVPortTCPServerThread connobj;
	private int conno;
	private DWVSerialProtocol dwProto;
	private DWVSerialPorts dwVSerialPorts;
	private ServerSocket srvr;
	
	
	public DWVPortTermThread(DWVSerialProtocol dwProto, int tcpport)
	{
		logger.debug("init term device thread on port "+ tcpport);	
		this.tcpport = tcpport;
		this.dwProto = dwProto;
		this.dwVSerialPorts = dwProto.getVPorts();
		
	}
	
	public void run() 
	{
		
		Thread.currentThread().setName("termdev-" + Thread.currentThread().getId());
		
		logger.debug("run");
		
		// setup port
		try
		{
			dwVSerialPorts.resetPort(TERM_PORT);
		} 
		catch (DWPortNotValidException e3)
		{
			logger.warn("while resetting term port: " + e3.getMessage());
		}
		
		// startup server 
		srvr = null;
		
		try 
		{
			dwVSerialPorts.openPort(TERM_PORT);
			
			
			
			
			if (dwProto.getConfig().containsKey("ListenAddress"))
			{
				srvr = new ServerSocket(this.tcpport, BACKLOG, InetAddress.getByName(dwProto.getConfig().getString("ListenAddress")) );
			}
			else
			{
				srvr = new ServerSocket(this.tcpport, BACKLOG);
			}
			
			
			InetSocketAddress sktaddr = new InetSocketAddress(this.tcpport);
			
			srvr.setReuseAddress(true);
			srvr.bind(sktaddr, BACKLOG);
			
			
			logger.info("listening on port " + srvr.getLocalPort());
		} 
		
		catch (IOException e2) 
		{
			logger.error("Error opening socket on port " + this.tcpport +": " + e2.getMessage());
			return;
		} 
		catch (DWPortNotValidException e) 
		{
			logger.error("Error opening term port: " + e.getMessage());
			return;
		}
		
		while ((wanttodie == false) && (!srvr.isClosed()))
		{
			logger.debug("waiting for connection");
			
			
			Socket skt;
			try 
			{
				skt = srvr.accept();
			} 
			catch (IOException e1) 
			{
				logger.info("IO error: " + e1.getMessage());
				wanttodie = true;
				return;
			}
			
			
			logger.info("new connection from " + skt.getInetAddress().getHostAddress());
			
			if (this.connthread != null)
			{	
				if (this.connthread.isAlive())
				{
					// no room at the inn
					logger.debug("term connection already in use");
					try
					{
						skt.getOutputStream().write(("The term device is already connected to a session (from " + this.dwVSerialPorts.getListenerPool().getConn(conno).socket().getInetAddress().getHostName() + ")\r\n" ).getBytes());
						skt.close();
					} 
					catch (IOException e)
					{
						logger.debug("io error closing socket: " + e.getMessage());
					} 
					catch (DWConnectionNotValidException e) 
					{
						logger.error(e.getMessage());
					}
				}
				else
				{
					startConn(skt);
				}
			}
			else
			{
				startConn(skt);
			}
		}
			
		if (srvr != null)
		{
			try 
			{
				srvr.close();
			} 
			catch (IOException e) 
			{
				logger.error("error closing server socket: " + e.getMessage());
			}
		}
		
		logger.debug("exiting");
	}

	private void startConn(Socket skt)
	{
		// do telnet init stuff
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
	
	
		try
		{
			skt.getOutputStream().write(buf, 0, 9);
			for (int i = 0; i<9; i++)
			{
				skt.getInputStream().read();
			}
		} 
		catch (IOException e)
		{
			logger.error(e.getMessage());
		}
	
			
		try 
		{
			conno = this.dwVSerialPorts.getListenerPool().addConn(this.vport, skt.getChannel(),MODE_TERM);
			connobj = new DWVPortTCPServerThread(dwProto,TERM_PORT, conno);
			connthread = new Thread(connobj);
			connthread.start();
			
		} 
		catch (DWConnectionNotValidException e) 
		{
			logger.error(e.getMessage());
		}
		
	
		
	}
	
	
	public void shutdown()
	{
		logger.debug("shutting down");
		wanttodie = true;
		
		if (connobj != null)
		{
			connobj.shutdown();
			connthread.interrupt();
		}
		
		try
		{
			srvr.close();
			
		} 
		catch (IOException e)
		{
			logger.warn("IOException closing server socket: " + e.getMessage());
		}
		
	}
	
}



