package com.groupunix.drivewireui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;

import com.groupunix.drivewireui.exceptions.DWUIOperationFailedException;


public class SyncThread implements Runnable 
{
	private static final int READ_BUFFER_SIZE = 2048;
	private static final String LINE_END = Character.toString((char) 13);
	private String host =  new String();
	private int port = -1;
	private Socket sock = null;
	private boolean wanttodie = false;
	private OutputStream out;
	private BufferedReader in;
	
	private HashMap<String, String> params = new HashMap<String, String>();
	private StringBuilder buffer = new StringBuilder(READ_BUFFER_SIZE * 2);


	
	public SyncThread()
	{
		
	}
	

	public void run() 
	{
		Thread.currentThread().setName("dwuiSync-" + Thread.currentThread().getId());
		
		char[] cbuf = new char[READ_BUFFER_SIZE];
		
		// initial sleep
		while (!MainWin.isReady())
		{
			// let GUI open up..
			try
			{
				//System.out.println("sleep");
				Thread.sleep(100);
			} 
			catch (InterruptedException e)
			{
				wanttodie = true;
			}
		}
		
		while (!wanttodie)
		{
			
			// change/establish connection
			if (!wanttodie && !(MainWin.getHost() == null) && !this.host.equals(MainWin.getHost()) || !(this.port == MainWin.getPort()) || (this.sock == null))		
			{
				
				
				if (!(sock == null))
				{
					try 
					{
						sock.close();
					} 
					catch (IOException e) 
					{
						// TODO MainWin.addToDisplay("Sync: " + e.getMessage());
					}
				}
				
				this.host = MainWin.getHost();
				this.port = MainWin.getPort();
								
				try 
				{
					MainWin.setConStatusTrying();
					// TODO MainWin.addToDisplay("Sync: Connecting to server..");
					MainWin.debug("Sync: Connecting...");
					
				    
				    // get initial state 
				    
				  
				    
				    
				    // load full config - required
				    MainWin.setServerConfig(UIUtils.getServerConfig());
				  
				 
				    
				    sock = new Socket(host, port);
					
					this.out = sock.getOutputStream();
				    this.in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
				    
				    MainWin.setConStatusConnect();
				    
				    MainWin.debug("Sync: Connected.");
				    
				    // start sync feed
				    
				    this.out.write(( MainWin.getInstance()+"").getBytes());
				    this.out.write((byte) 0);
				    this.out.write("ui sync\n".getBytes());
				} 
				catch (Exception e) 
				{
					if (MainWin.debugging == true)
						e.printStackTrace();
					
					MainWin.setConStatusError();
					// TODO MainWin.addToDisplay("Sync: " + e.getMessage());
					
					
					sock = null;
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e1) 
					{
						// the show must go on?
					}
				} 
					
			}
			
			
			if (!wanttodie && (sock != null) && !sock.isInputShutdown())
			{
				MainWin.setConStatusConnect();

				try 
				{
					int thisread = in.read(cbuf,0,READ_BUFFER_SIZE);
					
					if (thisread < 0)
					{
						try 
						{
							sock.close();
						} 
						catch (IOException e1) 
						{
							// TODO MainWin.addToDisplay("Sync: " + e1.getMessage());
						}
						
						sock = null;
					}
					else
					{
						buffer.append(cbuf, 0, thisread);
						eatData(buffer);
					}
					
					
				
				} 
				catch (IOException e) 
				{
					// TODO MainWin.addToDisplay("Sync: " + e.getMessage());
					
					if (sock != null)
					try 
					{
						sock.close();
					} 
					catch (IOException e1) 
					{
						// TODO MainWin.addToDisplay("Sync: " + e.getMessage());
					}
					
					sock = null;
					
				}
				
			}
		}
	
	}
	
	
	
	private void eatData(StringBuilder buf) 
	{
		int le = buf.indexOf(LINE_END);
		
		while (le > -1)
		{
			if (le > 0)
				processLine(buf.substring(0, le));
			
			buf.delete(0, le+1);
			le = buf.indexOf(LINE_END);
		}
		
	}

	
	private void processLine(String line) 
	{
		// drives
		if (line.equals("D"))
		{
			//System.out.println("D " + this.params.get("d") + ": " + this.params.get("k") + " = " + this.params.get("v"));
			
			if (this.params.containsKey("d") && (this.params.get("d") != null))
			{
				try
				{
					MainWin.submitDiskEvent(Integer.parseInt(this.params.get("d")), this.params.get("k"), this.params.get("v") );
				}
				catch (NumberFormatException e)
				{
					// ignore
				}
				
			}
		}
		// server status
		else if (line.equals("@"))
		{
			try
			{
				/*
				if (this.params.containsKey("0"))
				{
					this.ssbuf.setInterval(Integer.parseInt(params.get("0")));
				}
				
				if (this.params.containsKey("1"))
				{
					this.ssbuf.setMemtotal(Long.parseLong(params.get("1")));
				}
				
				if (this.params.containsKey("2"))
				{
					this.ssbuf.setMemfree(Long.parseLong(params.get("2")));
				}
				
				if (this.params.containsKey("3"))
				{
					this.ssbuf.setOps(Long.parseLong(params.get("3")));
				}
				
				if (this.params.containsKey("4"))
				{
					this.ssbuf.setDiskops(Long.parseLong(params.get("4")));
				}
				
				if (this.params.containsKey("5"))
				{
					this.ssbuf.setVserialops(Long.parseLong(params.get("5")));
				}
				
				if (this.params.containsKey("6"))
				{
					this.ssbuf.setInstances(Integer.parseInt(params.get("6")));
				}
				
				if (this.params.containsKey("7"))
				{
					this.ssbuf.setInstancesalive(Integer.parseInt(params.get("7")));
				}
				
				if (this.params.containsKey("8"))
				{
					this.ssbuf.setThreads(Integer.parseInt(params.get("8")));
				}
				
				if (this.params.containsKey("9"))
				{
					this.ssbuf.setUIClients(Integer.parseInt(params.get("9")));
				}
				
				if (this.params.containsKey("!"))
				{
					this.ssbuf.setMagic(Long.parseLong(params.get("!")));
				}
				
				MainWin.submitServerStatusEvent(ssbuf);
				*/
				
			}
			catch (NumberFormatException e)
			{
				
			}
			
			
			
		}
		// logging
		else if (line.equals("L"))
		{
			/*
			if (this.params.containsKey("l"))
				logbuf.setLevel(this.params.get("l"));
			
			if (this.params.containsKey("t"))
				logbuf.setTimestamp(Long.valueOf(this.params.get("t")));
			
			if (this.params.containsKey("m"))
				logbuf.setMessage(this.params.get("m"));
			
			if (this.params.containsKey("r"))
				logbuf.setThread(this.params.get("r"));
			
			if (this.params.containsKey("s"))
				logbuf.setSource(this.params.get("s"));
			
			MainWin.addToServerLog(logbuf.clone());
			*/
				
		}
		// instance config
		else if (line.equals("I"))
		{
			//System.out.println("I" + ": " + this.params.get("k") + " = " + this.params.get("v"));
			
			if (this.params.containsKey("k") && (this.params.get("k") != null))
			{
				if (this.params.containsKey("v"))
				{
					if (this.params.get("v") == null)
					{
						MainWin.getInstanceConfig().clearProperty(this.params.get("k"));
					}
					else
					{
						MainWin.getInstanceConfig().setProperty(this.params.get("k"), this.params.get("v"));
					}
					
					
				}
					
			}
		}
		// server config
		else if (line.equals("C"))
		{
			//System.out.println("C" + ": " + this.params.get("k") + " = " + this.params.get("v"));
			
			
			if (this.params.containsKey("k") && (this.params.get("k") != null))
			{
				if (this.params.containsKey("v"))
				{
					MainWin.submitServerConfigEvent(this.params.get("k"), this.params.get("v"));
				}
					
			}
		}
		// MIDI
		else if (line.equals("M"))
		{
			/*
			if (this.params.containsKey("k") && (this.params.get("k") != null))
			{
				if (this.params.get("k").equals("device") )
				{
					MainWin.getMidiStatus().setCurrentDevice(this.params.get("v"));
				}
				else if (this.params.get("k").equals("profile") )
				{
					MainWin.getMidiStatus().setCurrentProfile(this.params.get("v"));
				}
				else if (this.params.get("k").equals("soundbank") )
				{
					
				}
				else if (this.params.get("k").equals("voicelock") )
				{
					MainWin.getMidiStatus().setVoiceLock(Boolean.valueOf(this.params.get("v")));
				}
				
				
				MainWin.applyMIDIStatus();
			}
			*/
		}
		
		
		
		
		// params
		if (line.length()>1)
		{
			if (line.charAt(1) == ':') 
			{
				String val = null;
				if (line.length() > 2)
				{
					val = line.substring(2);
				}

				this.params.put(line.substring(0,1), val);
			}
		}
		
	}

	public void die()
	{
		this.wanttodie = true;
		
		if (this.sock != null)
		{
			try 
			{
				this.sock.close();
			} 
			catch (IOException e) 
			{
			}
			
			sock = null;
		}
	}

}
