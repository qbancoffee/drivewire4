package com.groupunix.drivewireserver;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;

import com.groupunix.drivewireserver.dwcommands.DWCmd;
import com.groupunix.drivewireserver.dwcommands.DWCommandList;
import com.groupunix.drivewireserver.dwcommands.DWCommandResponse;
import com.groupunix.drivewireserver.uicommands.UICmd;

public class DWUIClientThread implements Runnable {

	private static final Logger logger = Logger.getLogger("DWUIClientThread");

	private Socket skt;
	private boolean wanttodie = false;
	private int instance = -1;
	
	private DWCommandList commands;
	
	private LinkedBlockingQueue<DWEvent> eventQueue = new LinkedBlockingQueue<DWEvent>();

	private LinkedList<DWUIClientThread> clientThreads;

	private BufferedOutputStream bufferedout;
	
	private boolean droplog = true;

	private String tname = "not set";

	private String curcmd = "not set";

	private String state = "not set";

	
	public DWUIClientThread(Socket skt, LinkedList<DWUIClientThread> clientThreads) 
	{
		this.skt = skt;
		this.clientThreads = clientThreads;
		
		commands = new DWCommandList(null);
		commands.addcommand(new UICmd(this));
		
	}

	
	
	public void run() 
	{
		this.state  = "add to client threads";
		synchronized(this.clientThreads)
		{
			this.clientThreads.add(this);
		}
		

		this.tname = "dwUIcliIn-" + Thread.currentThread().getId();
	
		Thread.currentThread().setName(tname);
		Thread.currentThread().setPriority(Thread.NORM_PRIORITY);
		
		// if (DriveWireServer.serverconfig.getBoolean("LogUIConnections", false))
		//	logger.debug("run for client at " + skt.getInetAddress().getHostAddress());
		
		
		try 
		{
			this.state  = "get output stream";
			this.bufferedout = new BufferedOutputStream(skt.getOutputStream());
			
			// cmd loop
			
			String cmd = new String();
			
			while ((!skt.isClosed()) && (!wanttodie))
			{
				this.state  = "read from output stream";
				int databyte = skt.getInputStream().read();
			
				if (databyte == -1)
				{	
					//logger.debug("got -1 in input stream");
					wanttodie = true;
				}
				else
				{
					if (databyte == 10)
					{
						if (cmd.length() > 0)
						{
							this.state  = "do cmd";
							doCmd(cmd.trim());
							wanttodie = true;
							cmd = "";
						}
					}
					else if ((databyte > -1) && (databyte != 13))
					{
						cmd += Character.toString((char) databyte);
					}
					
				}
			}
			
			this.bufferedout.close();
			skt.close();
			
			this.state  = "close socket";
			
		} 
		catch (IOException e) 
		{
			logger.debug("IO Exception: " + e.getMessage());
		}
		
		this.state  = "remove from client threads";
		synchronized(this.clientThreads)
		{
			this.clientThreads.remove(this);
		}
		
		//if (DriveWireServer.serverconfig.getBoolean("LogUIConnections", false))
		//	logger.debug("exit");
		
		this.state  = "exit";
	}

	
	
	private void doCmd(String cmd) throws IOException 
	{
		this.curcmd = cmd;
		
		// grab instance
		int div = cmd.indexOf(0);
		
		// malformed command
		if (div < 1)
		{
			sendUIresponse(new DWCommandResponse(false, DWDefs.RC_UI_MALFORMED_REQUEST, "Malformed UI request (no instance.. old UI?)"));
			return;
		}
		else
		{
			// non numeric instance..
			try
			{
				this.setInstance(Integer.parseInt(cmd.substring(0, div)));
			}
			catch (NumberFormatException e)
			{
				sendUIresponse(new DWCommandResponse(false, DWDefs.RC_UI_MALFORMED_REQUEST, "Malformed UI request (bad instance)"));
				return;
			}
			
			// invalid length
			if (cmd.length() < div+2)
			{
				sendUIresponse(new DWCommandResponse(false, DWDefs.RC_UI_MALFORMED_REQUEST, "Malformed UI request (no command)"));
				return;
			}
			
		}
		
		// strip instance 
		cmd = cmd.substring(div+1);
		
		if (DriveWireServer.serverconfig.getBoolean("LogUIConnections", false))
			logger.debug("UI command '" + cmd + "' for instance " + this.instance);
		
		// wait for server/instance ready
		int waits = 0;
		while (!DriveWireServer.isReady() && (waits < DWDefs.UITHREAD_SERVER_WAIT_TIME))
		{
			try 
			{
				Thread.sleep(DWDefs.UITHREAD_WAIT_TICK);
				waits += DWDefs.UITHREAD_WAIT_TICK;
			} 
			catch (InterruptedException e) 
			{
				logger.warn("Interrupted while waiting for server to be ready");
				sendUIresponse(new DWCommandResponse(false, DWDefs.RC_SERVER_NOT_READY, "Interrupted while waiting for server to be ready"));
				return;
			}
		}
		
		if (!DriveWireServer.isReady())
		{
			logger.warn("Timed out waiting for server to be ready");
			sendUIresponse(new DWCommandResponse(false, DWDefs.RC_SERVER_NOT_READY, "Timed out waiting for server to be ready"));
			return;
		}
		
		
		// wait for instance ready..
		/*
		if (this.instance > -1)
		{
			waits = 0;
			while (!DriveWireServer.getHandler(instance).isReady() && (waits < DWDefs.UITHREAD_INSTANCE_WAIT_TIME))
			{
				try 
				{
					Thread.sleep(DWDefs.UITHREAD_WAIT_TICK);
					waits += DWDefs.UITHREAD_WAIT_TICK;
				} 
				catch (InterruptedException e) 
				{
					logger.warn("Interrupted while waiting for instance ready");
					sendUIresponse(new DWCommandResponse(false, DWDefs.RC_INSTANCE_NOT_READY, "Interrupted while waiting for instance ready"));
					return;
				}
			}
			
			if (!DriveWireServer.getHandler(instance).isReady())
			{
				logger.warn("Timed out waiting for instance #" + instance + " to be ready");
				sendUIresponse(new DWCommandResponse(false, DWDefs.RC_INSTANCE_NOT_READY, "Timed out waiting for instance #" + instance + " to be ready"));
				return;
			}
		}
		*/
		
		DWCommandResponse resp = this.commands.parse(cmd);
		
		sendUIresponse(resp);

		this.bufferedout.flush();
	}


	private void sendUIresponse(DWCommandResponse resp) throws IOException 
	{
		if (DriveWireServer.serverconfig.getBoolean("LogUIConnections", false))
		{
			if (resp.getResponseCode() == 0)
				logger.debug("UI command success");
			else
				logger.debug("UI command failed: #" + (0xFF + resp.getResponseCode()) + ": " + resp.getResponseText() );
		}
		
		// response header 0, (single byte RC), 0
		this.bufferedout.write(0);
		this.bufferedout.write(resp.getResponseCode() & 0xFF);
		this.bufferedout.write(0);
		
		// data
		if (resp.isUsebytes() && (resp.getResponseBytes() != null))
			this.bufferedout.write(resp.getResponseBytes());
		else if (resp.getResponseText() != null)
			this.bufferedout.write(resp.getResponseText().getBytes());
	}



	public void setInstance(int handler) 
	{
		this.instance = handler;
		
		// valid instances get a dw cmd mapping
		if (DriveWireServer.isValidHandlerNo(handler))
		{
			if (!this.commands.validate("dw"))
				this.commands.addcommand(new DWCmd(DriveWireServer.getHandler(handler)));
		}
	}

	public int getInstance() 
	{
		return this.instance;
	}

	public BufferedOutputStream getOutputStream() throws IOException 
	{
		return this.bufferedout;
	}
	
	public BufferedInputStream getInputStream() throws IOException
	{
		return new BufferedInputStream(skt.getInputStream());
	}
	

	public Socket getSocket() {
		
		return skt;
	}



	public synchronized void die() 
	{
		wanttodie = true;
		if (this.skt != null)
		{
			try 
			{
				this.skt.close();
			} 
			catch (IOException e) 
			{
			}
		}
	}
	
	public LinkedBlockingQueue<DWEvent> getEventQueue()
	{
		return(this.eventQueue);
	}



	public boolean isDropLog()
	{
	
		return this.droplog;
	}
	
	public void setDropLog(boolean b)
	{
		this.droplog = b;
	}



	public String getThreadName()
	{
		return this.tname;
	}

	public String getCurCmd()
	{
		return this.curcmd;
	}

	public String getState()
	{
		return this.state;
	}
	
	


}
