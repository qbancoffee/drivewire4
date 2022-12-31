package com.groupunix.drivewireserver.virtualserial;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

import org.apache.log4j.Logger;

import com.groupunix.drivewireserver.DWDefs;
import com.groupunix.drivewireserver.dwexceptions.DWPortNotValidException;

public class DWVModemConnThread implements Runnable {

	private static final Logger logger = Logger.getLogger("DWServer.DWVModemConnThread");
	
	private SocketChannel sktchan; 
	private int vport = -1;

	private DWVModem vm;

	private String tcphost;
	private int tcpport;

	private DWVModemListenerThread listenerThread = null;

	private boolean wanttodie = false;

	
	// telnet protocol cmds
	public static final int IAC = 255;
	public static final int SE = 240;
	public static final int NOP = 241;
	public static final int DM = 242;
	public static final int BREAK = 243;
	public static final int IP = 244;
	public static final int AO = 245;
	public static final int AYT = 246;
	public static final int EC = 247;
	public static final int EL = 248;
	public static final int GA = 249;
	public static final int SB = 250;
	public static final int WILL = 251;
	public static final int WONT = 252;
	public static final int DO = 253;
	public static final int DONT = 254;
	
	
	
	public DWVModemConnThread(DWVModem vm, SocketChannel skt, DWVModemListenerThread dwvModemListenerThread) 
	{
 		this.vm = vm;
 		this.vport = vm.getVPort();
 		this.sktchan = skt;
 		this.listenerThread  = dwvModemListenerThread;

	}

	public DWVModemConnThread(DWVModem dwvModem, String tcphost, int tcpport)
	{
		this.tcphost = tcphost;
		this.tcpport = tcpport;
		this.vm = dwvModem;
		this.vport = vm.getVPort();
		this.sktchan = null;
	}

	public void run() 
	{
		Thread.currentThread().setName("mdmconn-" + Thread.currentThread().getId());
	
		int telmode = 0;
		
		if (sktchan == null)
		{
			try
			{
				sktchan = SocketChannel.open(new InetSocketAddress(tcphost, tcpport));
				
				
			}
			catch (Exception e)
			{
				logger.warn("while making outgoing vmodem connection: " + e.getMessage());
				wanttodie  = true;
			} 
			
		}
		
		if (!wanttodie)
		{
			try 
			{
				
				if ((sktchan != null) && sktchan.isConnected())
				{
					vm.getVSerialPorts().markConnected(vport);
					vm.getVSerialPorts().setUtilMode(vport, DWDefs.UTILMODE_VMODEMOUT);
					vm.getVSerialPorts().setPortChannel(vport, sktchan);
					vm.getVSerialPorts().getPortInput(vport).write("CONNECT\r\n".getBytes());
				}
				
				while ((sktchan != null) && sktchan.isConnected())
				{
					int data = sktchan.socket().getInputStream().read();
					
					if (data >= 0)
					{
						// telnet stuff
						if (telmode == 1)
						{
							switch(data)
							{
							  	case SE:
							  	case NOP:
							  	case DM:
							  	case BREAK:
							  	case IP:
							  	case AO:
							  	case AYT:
							  	case EC:
							  	case EL:
							  	case GA:
							  	case SB:
							  		
							  		break;
							  		
							  	case WILL:
							  		data = sktchan.socket().getInputStream().read();
							  		sktchan.socket().getOutputStream().write(255);
							  		sktchan.socket().getOutputStream().write(DONT);
							  		sktchan.socket().getOutputStream().write(data);
						        	break;
						        	
						        case WONT:
						        case DONT:
						        	data = sktchan.socket().getInputStream().read();
						        	break;
						        	
						        case DO:
						        	data = sktchan.socket().getInputStream().read();
						        	sktchan.socket().getOutputStream().write(255);
						        	sktchan.socket().getOutputStream().write(WONT);
						        	sktchan.socket().getOutputStream().write(data);
						        	break;
							
						        	
							}
							telmode  = 0;
						}
						switch(data)
						{
							case IAC:
								telmode = 1;
								break;
							
							default:
								// write it to the serial port
								vm.write((byte) data);
				         
						}
					}
					else
					{
						logger.info("connection to " + this.sktchan.socket().getInetAddress().getCanonicalHostName() + ":" + this.sktchan.socket().getPort() + " closed");
						if (sktchan.isConnected())
						{
							logger.debug("closing socket");
							sktchan.close();
						}
							
					}
				}
				
			} 
			catch (IOException e) 
			{
				logger.warn("IO error in connection to " + this.sktchan.socket().getInetAddress().getCanonicalHostName() + ":" + this.sktchan.socket().getPort() + " = " + e.getMessage());
			} 
			catch (DWPortNotValidException e)
			{
				logger.warn("in connection to " + this.sktchan.socket().getInetAddress().getCanonicalHostName() + ":" + this.sktchan.socket().getPort() + " = " + e.getMessage());
			}
		}
	
		if (this.vport > -1)
		{
			vm.getVSerialPorts().markDisconnected(this.vport);
			// TODO: this is all wrong
			vm.write("\r\n\r\nNO CARRIER\r\n");
				
			if (this.listenerThread != null)
				this.listenerThread.setConnected(false);
		}	
			
		logger.debug("thread exiting");
		
	}

}
