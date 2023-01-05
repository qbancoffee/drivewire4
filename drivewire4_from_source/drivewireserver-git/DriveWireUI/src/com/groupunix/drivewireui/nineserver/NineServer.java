package com.groupunix.drivewireui.nineserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class NineServer implements Runnable
{

	private ServerSocket srvr = null;
	private int port;
	
	public NineServer(int port)
	{
		this.port = port;
	}

	@Override
	public void run()
	{
		try 
		{
			// check for listen address
			
			srvr = new ServerSocket(port);
			
			while (srvr.isClosed() == false)
			{

				Socket skt = null;
				
				skt = srvr.accept();
				
				Thread ct = new Thread(new NineServerClientHandler(skt));
				ct.start();	
			
				
			}
		} 
		catch (java.net.BindException e1)
		{
			System.out.println("Warning: NineServer port already in use.  Is another DW4UI running?");
		}
		catch (IOException e2) 
		{
			
			e2.printStackTrace();
		}
			
	}

}
