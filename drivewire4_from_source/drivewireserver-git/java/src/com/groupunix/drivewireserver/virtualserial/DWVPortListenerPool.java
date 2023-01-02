package com.groupunix.drivewireserver.virtualserial;

import java.io.IOException;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import org.apache.log4j.Logger;

import com.groupunix.drivewireserver.dwexceptions.DWConnectionNotValidException;

public class DWVPortListenerPool {

	public static final int MAX_CONN = 256;
	public static final int MAX_LISTEN = 64;
	private SocketChannel[] sockets = new SocketChannel[MAX_CONN];
	private ServerSocketChannel[] server_sockets = new ServerSocketChannel[MAX_LISTEN];
	
	private int[] serversocket_ports = new int[MAX_LISTEN];
	private int[] socket_ports = new int[MAX_CONN];
	private int[] modes = new int[MAX_CONN];
	
	private static final Logger logger = Logger.getLogger("DWServer.DWVPortListenerPool");
	
	public int addConn(int port, SocketChannel sktchan, int mode) 
	{
		
		logger.debug("add connection entry for port " + port + " mode " + mode);
		
		for (int i = 0; i< MAX_CONN;i++)
		{
			if (sockets[i] == null)
			{
				sockets[i] = sktchan;
				modes[i] = mode;
				socket_ports[i] = port;
				return(i);
			}
		}
		
		return(-1);
	}

	public SocketChannel getConn(int conno) throws DWConnectionNotValidException
	{
		validateConn(conno);
		return(sockets[conno]);
	}
	
	public void validateConn(int conno) throws DWConnectionNotValidException 
	{
		if ((conno < 0) || (conno > DWVPortListenerPool.MAX_CONN) || (this.sockets[conno] == null))
		{
			throw(new DWConnectionNotValidException("Invalid connection #" + conno));
		}
	}

	public void setConnPort(int conno, int port) throws DWConnectionNotValidException
	{
		validateConn(conno);
		socket_ports[conno] = port;
	}
	
	public int addListener(int port, ServerSocketChannel srvr)
	{
		
		
		for (int i = 0; i< MAX_LISTEN;i++)
		{
			if (server_sockets[i] == null)
			{
				
				serversocket_ports[i] = port;
				server_sockets[i] = srvr;
				logger.debug("add listener entry for port " + port + " id " + i);
				
				return(i);

			}
		}
	 	
		return(-1);
	}
	
	public ServerSocketChannel getListener(int conno)
	{
		return(server_sockets[conno]);
	}
	
	
	public void closePortServerSockets(int port)
	{
		
		for (int i = 0;i<MAX_LISTEN;i++)
		{
			if (this.getListener(i) != null)
			{
				if (serversocket_ports[i] == port)
				{
					try 
					{
						logger.debug("closing listener sockets for port " + port + "...");
						this.killListener(i);
					} 
					catch (DWConnectionNotValidException e) 
					{
						logger.error(e.getMessage());
					}
				}
			}
		}
	}
	
	public void closePortConnectionSockets(int port)
	{
		
		for (int i = 0;i<DWVPortListenerPool.MAX_CONN;i++)
		{
	
			try {
				if (this.sockets[i] != null)
				{
					// don't reset term
					if (this.getMode(i) != DWVSerialPorts.MODE_TERM)
					{
						
						this.killConn(i);
					}
				}
			} 
			catch (DWConnectionNotValidException e) 
			{
				logger.error("close sockets: " + e.getMessage());
			}
		}

		
	}
	
	// temporary crap to make telnetd work
	public int getMode(int conno) throws DWConnectionNotValidException
	{
		validateConn(conno);
		return(modes[conno]);
	}
	
	public void clearConn(int conno) throws DWConnectionNotValidException
	{
		validateConn(conno);
		sockets[conno] = null;
		socket_ports[conno] = -1;
	}

	public void clearListener(int conno)
	{
		server_sockets[conno] = null;
		serversocket_ports[conno] = -1;
	}
	
	public void killConn(int conno) throws DWConnectionNotValidException
	{
		validateConn(conno);
		
		try
		{
			sockets[conno].close();
			logger.debug("killed conn #" + conno);
		} 
		catch (IOException e)
		{
			logger.debug("IO error closing conn #" + conno + ": " + e.getMessage());
		}
		
		clearConn(conno);
		
	}

	
	public void killListener(int conno) throws DWConnectionNotValidException
	{
		
		try
		{
			server_sockets[conno].close();
			logger.debug("killed listener #" + conno);
		} 
		catch (IOException e)
		{
			logger.debug("IO error closing listener #" + conno + ": " + e.getMessage());
		}
		
		clearListener(conno);
		
	}

	public int getListenerPort(int i)
	{
		return serversocket_ports[i];
	}

	public int getConnPort(int i)
	{
		return socket_ports[i];
	}
	
}
