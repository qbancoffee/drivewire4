package com.groupunix.drivewireserver.virtualserial;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import org.apache.log4j.Logger;

import com.groupunix.drivewireserver.DWDefs;
import com.groupunix.drivewireserver.dwexceptions.DWPortNotValidException;
import com.groupunix.drivewireserver.dwprotocolhandler.DWVSerialProtocol;

public class DWVPortTCPListenerThread implements Runnable 
{

	private static final Logger logger = Logger.getLogger("DWServer.DWVPortTCPListenerThread");
	
	private int vport;
	private int tcpport;

	private DWVSerialPorts dwVSerialPorts;
	
	private int mode = 0;
	private boolean do_banner = false;
	private boolean do_telnet = false;
	private boolean wanttodie = false;
	
	private static int BACKLOG = 20;
	private DWVSerialProtocol dwProto;
	
	
	
	
	public DWVPortTCPListenerThread(DWVSerialProtocol dwProto2, int vport, int tcpport)
	{
		logger.debug("init tcp listener thread on port "+ tcpport);	
		this.vport = vport;
		this.tcpport = tcpport;
		this.dwProto = dwProto2;
		this.dwVSerialPorts = dwProto2.getVPorts();
		
	}
	
	
	
	public void run() 
	{
		
		Thread.currentThread().setName("tcplisten-" + Thread.currentThread().getId());
		Thread.currentThread().setPriority(Thread.NORM_PRIORITY);
		
		logger.debug("run");
		
		
		try 
		{
			// startup server 
			ServerSocketChannel srvr = ServerSocketChannel.open();
			
			
			try
			{
				InetSocketAddress sktaddr = new InetSocketAddress(this.tcpport);
				
				srvr.socket().setReuseAddress(true);
				srvr.socket().bind(sktaddr, BACKLOG);
				
				/*
				if (dwProto.getConfig().containsKey("ListenAddress"))
				{
					srvr = new ServerSocket(this.tcpport, BACKLOG, InetAddress.getByName(dwProto.getConfig().getString("ListenAddress")) );
				}
				else
				{
					srvr = new ServerSocket(this.tcpport, BACKLOG);
				}
				*/
				
				this.dwVSerialPorts.getListenerPool().addListener(this.vport, srvr);
				
				logger.info("tcp listening on port " + srvr.socket().getLocalPort());
			}
			catch (IOException e2) 
			{
				logger.error(e2.getMessage());
				dwVSerialPorts.sendUtilityFailResponse(this.vport, DWDefs.RC_NET_IO_ERROR, e2.getMessage());
				return;
			} 
			
			dwVSerialPorts.writeToCoco(vport, "OK listening on port " + this.tcpport + (char) 10 + (char) 13);

			this.dwVSerialPorts.setUtilMode(vport, DWDefs.UTILMODE_TCPLISTEN);
			
			
		
			while ((wanttodie == false) && dwVSerialPorts.isOpen(this.vport) && (srvr.isOpen()) && (srvr.socket().isClosed() == false))
			{
				logger.debug("waiting for connection");
				SocketChannel skt = srvr.accept();
				
			
				logger.info("new connection from " + skt.socket().getInetAddress());
			
				this.dwVSerialPorts.getListenerPool().addConn(this.vport, skt, mode);
				
				
				if (mode == 2)
				{
					// http mode
					logger.error("HTTP MODE NO LONGER SUPPORTED");
			
				}
				else
				{
					// run telnet preflight, let it add the connection to the pool if things work out
					Thread pfthread = new Thread(new DWVPortTelnetPreflightThread(this.dwProto, this.vport, skt, this.do_telnet, this.do_banner));
					pfthread.start();
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
			
		} 
		catch (IOException e2) 
		{
			logger.error(e2.getMessage());
		} 
		catch (DWPortNotValidException e) 
		{
			logger.error(e.getMessage());
		}
	
		
		logger.debug("tcp listener thread exiting");
	}

	

	




	

	public void setDo_banner(boolean do_banner)
	{
		this.do_banner = do_banner;
	}

	public boolean isDo_banner()
	{
		return do_banner;
	}

	
	public void setMode(int mode)
	{
		this.mode = mode;
	}
	
	public int getMode()
	{
		return(this.mode);
	}

	public void setDo_telnet(boolean b)
	{
		this.do_telnet = b;
		
	}
	
	public boolean isDo_telnet()
	{
		return do_telnet;
	}
	
}
