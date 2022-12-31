package com.groupunix.drivewireserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;


public class DWUIThread implements Runnable {

	private static final Logger logger = Logger.getLogger("DWUIThread");


	private boolean wanttodie = false;
	private ServerSocket srvr = null;
	
	private LinkedList<DWUIClientThread> clientThreads = new LinkedList<DWUIClientThread>();

	private int dropppedevents = 0;

	private int lastQueueSize;
	
	public DWUIThread(ServerSocket ss) 
	{
		this.srvr = ss;
	}

	
	public void die()
	{
		this.wanttodie = true;
		try 
		{
			for (DWUIClientThread ct : this.clientThreads)
			{

					ct.die();
				
			}
			
			if (this.srvr != null)
			{
				this.srvr.close();
			}
		} 
		catch (IOException e) 
		{
			logger.warn("IO Error closing socket: " + e.getMessage());
		}
		catch (ConcurrentModificationException e)
		{
			// TODO whatever, we are dying, but should do this right
		}
		
	}
	
	
	public void run() 
	{
		Thread.currentThread().setName("dwUIserver-" + Thread.currentThread().getId());
		Thread.currentThread().setPriority(Thread.NORM_PRIORITY);
		
		// open server socket
		
		
				
		while ((wanttodie == false) && (srvr.isClosed() == false))
		{
			//logger.debug("UI waiting for connection");
			Socket skt = null;
			try 
			{
				skt = srvr.accept();
				
				if (DriveWireServer.serverconfig.getBoolean("LogUIConnections", false))
					logger.debug("new UI connection from " + skt.getInetAddress().getHostAddress());
				
				Thread uiclientthread = new Thread(new DWUIClientThread(skt, this.clientThreads));
				uiclientthread.setDaemon(true);
				uiclientthread.start();
			
				
			} 
			catch (IOException e1) 
			{
				if (wanttodie)
					logger.debug("IO error (while dying): " + e1.getMessage());
				else
					logger.warn("IO error: " + e1.getMessage());
				
				wanttodie = true;
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


	public void submitEvent(DWEvent evt) 
	{
		
		//System.out.println("Event " + evt.getEventType() + " " + evt.getParam("k") + " " + evt.getParam("v"));

		
		synchronized(this.clientThreads)
		{
			Iterator<DWUIClientThread> itr = this.clientThreads.iterator(); 
			
			while(itr.hasNext()) 
			{	
				DWUIClientThread client = itr.next();
				
				// filter event
				if (client.wantsEvent(evt.getEventType()))
				
					// filter for instance
					if ((client.getInstance() == -1) || (client.getInstance() == evt.getEventInstance()) || (evt.getEventInstance() == -1))
					{
						
						LinkedBlockingQueue<DWEvent> queue = (LinkedBlockingQueue<DWEvent>) client.getEventQueue(); 
						
						if (queue != null)
						{
							synchronized(queue)
							{
							
								this.lastQueueSize = queue.size();
								if (queue.size() < DWDefs.EVENT_QUEUE_LOGDROP_SIZE)
								{
									queue.add(evt);
								}
								else if ((queue.size() < DWDefs.EVENT_MAX_QUEUE_SIZE) && (evt.getEventType() != DWDefs.EVENT_TYPE_LOG))
								{
									queue.add(evt);
								}
								else
								{
									this.dropppedevents++; 
									//System.out.println("queue drop: " + queue.size() + "/" + this.dropppedevents + "  " + evt.getEventType() + " thr " + client.getThreadName() + " cmd " + client.getCurCmd() + " state " + client.getState() );
								}
							}
						}
					}
			}

		} 
	}

	
	public int getNumUIClients()
	{
		return this.clientThreads.size();
	}


	public int getQueueSize()
	{
	
		
		return lastQueueSize;
	}
	
	public LinkedList<DWUIClientThread> getUIClientThreads()
	{
		return this.clientThreads;
	}


	public int getDropppedevents() {
		return dropppedevents;
	}


	
}
