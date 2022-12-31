package com.groupunix.drivewireserver.virtualserial;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import org.apache.log4j.Logger;

import com.groupunix.drivewireserver.dwprotocolhandler.DWVSerialProtocol;



	public class DWVModemListenerThread implements Runnable 
	{

		private static final Logger logger = Logger.getLogger("DWServer.DWVModemListenerThread");
		
		private int vport;
		private int tcpport;

		private DWVModem dwVModem;
		
		private boolean wanttodie = false;
		
		private static int BACKLOG = 20;
		private DWVSerialProtocol dwProto;

		private DWVSerialPorts dwVSerialPorts;
		private Boolean clientConnected = false;
		
		
		public DWVModemListenerThread(DWVModem m)
		{
			this.dwVSerialPorts = m.getVSerialPorts();
			this.dwVModem = m;
			this.tcpport = m.getListenPort();
			this.vport = m.getVPort();
			this.dwProto = m.getVProto();
			
			logger.debug("init modem listener thread on port "+ tcpport);		
		}
		
		
		
		public void run() 
		{
			
			Thread.currentThread().setName("mdmlisten-" + Thread.currentThread().getId());
			Thread.currentThread().setPriority(Thread.NORM_PRIORITY);
			
			logger.debug("run");
			
			// startup server 
			
			try 
			{
				/* check for listen address
				
				try
				{
					if (dwProto.getConfig().containsKey("ListenAddress"))
					{
						srvr = new ServerSocket(this.tcpport, BACKLOG, InetAddress.getByName(dwProto.getConfig().getString("ListenAddress")) );
					}
					else
					{
						srvr = new ServerSocket(this.tcpport, BACKLOG);
					}
					logger.info("vmodem listening on port " + srvr.getLocalPort());
				}
				catch (IOException e2) 
				{
					logger.error(e2.getMessage());
					return;
				} 
				
				*/
				
				//this.dwVSerialPorts.setUtilMode(vport, DWDefs.UTILMODE_TCPLISTEN);
				
				ServerSocketChannel srvr = ServerSocketChannel.open();
				
				
				InetSocketAddress sktaddr = new InetSocketAddress(this.tcpport);
				
				srvr.socket().setReuseAddress(true);
				srvr.socket().bind(sktaddr, BACKLOG);
				
				
				while ((wanttodie == false) && dwVSerialPorts.isOpen(this.vport) && (srvr.isOpen()))
				{
					logger.debug("waiting for connection");
					SocketChannel skt = null;
					
					skt = srvr.accept();
					
					logger.info("new connection from " + skt.socket().getInetAddress().getHostAddress());
					
					synchronized(this.clientConnected)
					{
						if (this.clientConnected)
						{
							logger.info("Rejecting new connection to vmodem #" + this.vport + " because modem is already connected");
							
							try
							{
								skt.socket().getOutputStream().write(dwProto.getConfig().getString("ModemInUseMessage","This DriveWire virtual modem is in use.\r\nPlease try again later.\r\n").getBytes());
								skt.close();
							}
							catch (IOException e) 
							{
								logger.warn("in new modem connection: " + e.getMessage());
							} 
						}
						else
						{
							this.clientConnected = true;
							
							Thread vmthread = new Thread(new DWVModemConnThread(dwVModem, skt, this));
							vmthread.start();
							logger.info("started thread to handle new vmodem connection");
						}
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
			

		
			
			logger.debug("modem listener thread exiting");
		}



		public void setConnected(boolean b)
		{
			synchronized(this.clientConnected)
			{
				this.clientConnected = b;
			}
		}
	
	}

	
