package com.groupunix.drivewireserver.virtualserial;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.ShortMessage;

import org.apache.log4j.Logger;

import com.groupunix.drivewireserver.DWDefs;
import com.groupunix.drivewireserver.DWEvent;
import com.groupunix.drivewireserver.DriveWireServer;
import com.groupunix.drivewireserver.dwprotocolhandler.DWUtils;
import com.groupunix.drivewireserver.dwprotocolhandler.DWVSerialProtocol;

public class DWVSerialPort {

	private static final Logger logger = Logger.getLogger("DWServer.DWVSerialPort");
	
	private static final int INPUT_BUFFER_SIZE = -1;  //infinite
	private static final int OUTPUT_BUFFER_SIZE = 65535; // huge
	
	private int port = -1;
	private DWVSerialProtocol dwProto;
	
	private boolean connected = false;
	private int opens = 0;

	private DWVPortHandler porthandler = null;
	private DWVSerialPorts vports;
	
	private byte PD_INT = 0;
	private byte PD_QUT = 0;
	private byte[] DD = new byte[26];
	
	private	DWVSerialCircularBuffer inputBuffer = new DWVSerialCircularBuffer(INPUT_BUFFER_SIZE, true);
	
	private byte[] outbuf = new byte[OUTPUT_BUFFER_SIZE];
	private ByteBuffer outputBuffer = ByteBuffer.wrap(outbuf);
	
	private boolean wanttodie = false;
	
	private int conno = -1;
	
	// midi message stuff
	@SuppressWarnings("unused")
	private ShortMessage mmsg;
	private int mmsg_pos = 0;
	private int mmsg_data1;
	private int mmsg_status;
	@SuppressWarnings("unused")
	private int last_mmsg_status;
	private int mmsg_databytes = 2;
	
	private boolean midi_seen = false;
	private boolean log_midi_bytes = false;
	private boolean midi_in_sysex = false;
	private String midi_sysex = new String();
	
	private int utilmode = 0;

	private SocketChannel sktchan;
	
	public DWVSerialPort(DWVSerialPorts vps, DWVSerialProtocol dwProto, int port)
	{
		logger.debug("New DWVSerialPort for port " + port + " in handler #" + dwProto.getHandlerNo());
		this.vports = vps;
		this.port = port;
		this.dwProto = dwProto;
		
		
		if ((port != vps.getNTermPort()) && (port < (vps.getMaxPorts())))
		{
			this.porthandler = new DWVPortHandler(dwProto, port);
		
			if (dwProto.getConfig().getBoolean("LogMIDIBytes", false))
			{
				this.log_midi_bytes = true;
			}
			
		}
		
		DWEvent evt = new DWEvent(DWDefs.EVENT_TYPE_VSERIAL, this.dwProto.getHandlerNo());
		evt.setParam(DWDefs.EVENT_ITEM_VPORT_ACTION, DWDefs.EVENT_ACTION_CREATE + "");
		evt.setParam(DWDefs.EVENT_ITEM_VPORT, this.port + "");
		
		DriveWireServer.submitEvent(evt);

	}
	
	
	
	public int bytesWaiting() 
	{
		int bytes = inputBuffer.getAvailable();
		
		// never admit to client about having more than 255 bytes
		if (bytes < 256)
			return(bytes);
		else
			return(255);
	}

	
	public void write(int databyte) 
	{
		
		
		if (this.port == vports.getMIDIPort())
		{
			if (!midi_seen)
			{
				logger.debug("MIDI data on port " + this.port);
				midi_seen = true;
			}
			
			
			// incomplete, but enough to make most things work for now
			
			
			databyte = (int)(databyte & 0xFF);
			
			if (midi_in_sysex)
			{
				if (databyte == 247)
				{
					midi_in_sysex = false;
					
					if (log_midi_bytes)
					{
						logger.info("midi sysex: " + midi_sysex);
					}
					
					midi_sysex = "";
				}
				else
				{
					midi_sysex = midi_sysex + " " + databyte;
				}
			}
			else
			{
				if (databyte == 240)
				{
					midi_in_sysex = true;
				}
				else if (databyte == 248)   // We ignore other status stuff for now
				{
					sendMIDI(databyte);
				}
				else if ((databyte >= 192) && (databyte < 224))  // Program change and channel pressure have 1 data byte
				{
					mmsg_databytes = 1;
					last_mmsg_status = mmsg_status;
					mmsg_status = databyte;
					mmsg_pos = 0;
				}
				else if ((databyte > 127) && (databyte < 240))  // Note on/off, key pressure, controller change, pitch bend have 2 data bytes
				{
					mmsg_databytes = 2;				
					last_mmsg_status = mmsg_status;
					mmsg_status = databyte;
					mmsg_pos = 0;
				}
				else
				{
					// data bytes
				
					if (mmsg_pos == 0)
					{
						//data1
					
						if (mmsg_databytes == 2)
						{
							// store databyte 1
							mmsg_data1 = databyte;
							mmsg_pos = 1;
						}
						else 
						{
							// send midimsg with 1 data byte
						
							if ((mmsg_status >= 192) && (databyte < 208)) 
							{
								if (dwProto.getVPorts().getMidiVoicelock())
								{
									// ignore program change
									logger.debug("MIDI: ignored program change due to instrument lock.");
								}
								else
								{
									// translate program changes
									int xinstr = dwProto.getVPorts().getGMInstrument(databyte);
									sendMIDI(mmsg_status, xinstr, 0);
								
									// sendMIDI(mmsg_status, databyte, 0);
								
									// set cache
									dwProto.getVPorts().setGMInstrumentCache(mmsg_status - 192, databyte);
								}
							}
							else
							{
								sendMIDI(mmsg_status, databyte, 0);
							}
							mmsg_pos = 0;
						}
					}
					else
					{
						//data2
						sendMIDI(mmsg_status,mmsg_data1,databyte);
						mmsg_pos = 0;
					}
				}
			}
		}
		else
		{	
			// if we are connected, pass the data
			if ((this.connected) || (this.port == vports.getNTermPort()) ||  ((this.port >= vports.getMaxNPorts()) && (this.port < vports.getMaxPorts())))
			{
				if (sktchan == null)
				{
					 logger.debug("write to null io channel on port " + this.port);
				}
				else
				{
					try 
					{
						
						while (outputBuffer.remaining() < 1)
						{
							System.out.println("FULL buffer " + outputBuffer.position() + " " + outputBuffer.remaining() + " " + outputBuffer.limit() + " " + outputBuffer.capacity());
							
							
							outputBuffer.flip();
							int wrote = sktchan.write(outputBuffer);
							outputBuffer.compact();
							
							System.out.println("full wrote " + wrote);
							
							if (wrote == 0)
							{
								try
								{
									Thread.sleep(100);
								} 
								catch (InterruptedException e)
								{
									// dont care
								}
							}
							
						}
						
						
						
						
						outputBuffer.put((byte) databyte);
						outputBuffer.flip();
							
						
						sktchan.write(outputBuffer);
						
						outputBuffer.compact();
							
					} 
					catch (IOException e) 
					{
						logger.error("in write: " + e.getMessage());
					}
				}
			}
			// otherwise process as command
			else
			{
				this.porthandler.takeInput(databyte);
			}
		}
		
		DWEvent evt = new DWEvent(DWDefs.EVENT_TYPE_VSERIAL, this.dwProto.getHandlerNo());
		evt.setParam(DWDefs.EVENT_ITEM_VPORT_ACTION, DWDefs.EVENT_ACTION_WRITE + "");
		evt.setParam(DWDefs.EVENT_ITEM_VPORT, this.port + "");
		evt.setParam(DWDefs.EVENT_ITEM_VPORT_DATA, (byte)databyte );
				
		DriveWireServer.submitEvent(evt);
		
		
	}

	private void sendMIDI(int statusbyte) 
	{
		ShortMessage mmsg = new ShortMessage();
				
		try 
		{
			
			mmsg.setMessage(statusbyte);
			dwProto.getVPorts().sendMIDIMsg(mmsg, -1);
		} 
		catch (InvalidMidiDataException e) 
		{
			logger.warn("MIDI: " + e.getMessage());
		}
		
		if (log_midi_bytes)
		{
			byte[] tmpb = {(byte) statusbyte };
			logger.info("midimsg: " + DWUtils.byteArrayToHexString( tmpb ));
		}
		
	}


	private void sendMIDI(int statusbyte, int data1, int data2) 
	{
		ShortMessage mmsg = new ShortMessage();
				
		try 
		{
			
			mmsg.setMessage(statusbyte, data1, data2);
			dwProto.getVPorts().sendMIDIMsg(mmsg, -1);
		} 
		catch (InvalidMidiDataException e) 
		{
			logger.warn("MIDI: " + e.getMessage());
		}
		
		if (log_midi_bytes)
		{
		
			logger.info("midimsg: " + DWUtils.midimsgToText(statusbyte, data1, data2));
		}
		
	}

	

	public void writeM(String str)
	{
		for (int i = 0;i<str.length();i++)
		{
			write(str.charAt(i));
		}
		
		DWEvent evt = new DWEvent(DWDefs.EVENT_TYPE_VSERIAL, this.dwProto.getHandlerNo());
		evt.setParam(DWDefs.EVENT_ITEM_VPORT_ACTION, DWDefs.EVENT_ACTION_WRITE + "");
		evt.setParam(DWDefs.EVENT_ITEM_VPORT, this.port + "");
		evt.setParam(DWDefs.EVENT_ITEM_VPORT_DATA, str.getBytes());
				
		DriveWireServer.submitEvent(evt);
		
	}
	
	
	public void writeToCoco(String str)
	{
		try 
		{
			inputBuffer.getOutputStream().write(str.getBytes());
		} 
		catch (IOException e) 
		{
			logger.warn(e.getMessage());
		}
	}
	
	
	public void writeToCoco(byte[] databytes) 
	{
		try 
		{
			inputBuffer.getOutputStream().write(databytes);
			
			
			
		} catch (IOException e) {
			logger.warn(e.getMessage());
		}
	}
	
	
	public void writeToCoco(byte[] databytes, int offset, int length) 
	{
		try 
		{
			inputBuffer.getOutputStream().write(databytes, offset, length);
			
		} catch (IOException e) {
			logger.warn(e.getMessage());
		}
	}
	
	
	
	public void writeToCoco(byte databyte)
	{
		try {
			inputBuffer.getOutputStream().write(databyte);
		} catch (IOException e) {
			logger.warn(e.getMessage());
		}
	}
	
	

	public OutputStream getPortInput()
	{
		return(inputBuffer.getOutputStream());
	}
	
	public InputStream getPortOutput()
	{
		return(inputBuffer.getInputStream());
	}
	
	
	
	public byte read1() 
	{
	
		int databyte;
		
		try 
		{
			databyte = inputBuffer.getInputStream().read();
			
			DWEvent evt = new DWEvent(DWDefs.EVENT_TYPE_VSERIAL, this.dwProto.getHandlerNo());
			evt.setParam(DWDefs.EVENT_ITEM_VPORT_ACTION, DWDefs.EVENT_ACTION_READ + "");
			evt.setParam(DWDefs.EVENT_ITEM_VPORT, this.port + "");
			evt.setParam(DWDefs.EVENT_ITEM_VPORT_DATA, (byte)databyte);
			evt.setParam(DWDefs.EVENT_ITEM_VPORT_BYTES, inputBuffer.getAvailable() + "");
			
			DriveWireServer.submitEvent(evt);
			
			return((byte) databyte);
		} 
		catch (IOException e) 
		{
			logger.error("in read1: " + e.getMessage());
		}
		
		return(-1);
		
	}


	public byte[] readM(int tmplen) 
	{
		byte[] buf = new byte[tmplen];
		
		try 
		{
			inputBuffer.getInputStream().read(buf, 0, tmplen);
			
			DWEvent evt = new DWEvent(DWDefs.EVENT_TYPE_VSERIAL, this.dwProto.getHandlerNo());
			evt.setParam(DWDefs.EVENT_ITEM_VPORT_ACTION, DWDefs.EVENT_ACTION_READ + "");
			evt.setParam(DWDefs.EVENT_ITEM_VPORT, this.port + "");
			evt.setParam(DWDefs.EVENT_ITEM_VPORT_DATA, buf);
			evt.setParam(DWDefs.EVENT_ITEM_VPORT_BYTES, inputBuffer.getAvailable() + "");
			
			DriveWireServer.submitEvent(evt);
			
			return(buf);
			
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
			logger.error("Failed to read " + tmplen + " bytes in SERREADM... not good");
		}
		return null;
	}


	public void setConnected(boolean connected) 
	{
		this.connected = connected;
	}



	public boolean isConnected() 
	{
		return connected;
	}

	
	public boolean isOpen()
	{
		if ((this.opens > 0) || (this.utilmode == DWDefs.UTILMODE_NINESERVER))
		{
			return(true);
		}
		else
		{
			return(false);
		}
	}


	public void open()
	{
		this.opens++;
		logger.debug("open port " + this.port + ", total opens: " + this.opens);
		
		// fire off NineServer thread if we are a window device
		
		if ((this.port >= vports.getMaxNPorts()) && (this.port < vports.getMaxPorts()))
		{
			String tcphost = this.dwProto.getConfig().getString("NineServer"+this.port,  this.dwProto.getConfig().getString("NineServer", "127.0.0.1"));
			int tcpport = this.dwProto.getConfig().getInt("NineServerPort"+this.port,  this.dwProto.getConfig().getInt("NineServerPort", 6309));
			
			this.setUtilMode(DWDefs.UTILMODE_NINESERVER);
			
			// device id cmd
			byte[] wcdata = new byte[8];
			
			wcdata[0] = 0x1b;
			wcdata[1] = 0x7f;
			wcdata[2] = 0x01;
			
			String pname = this.dwProto.getVPorts().prettyPort(this.port);
			
			for (int i = 3;i<wcdata.length;i++)
			{
				if (i-3 < pname.length())
					wcdata[i] = (byte) pname.charAt(i-3);
				else
					wcdata[i] = 0x20;
			}
			
			
			
			// start TCP thread
			
			Thread utilthread = new Thread(new DWVPortTCPConnectionThread(this.dwProto, this.port, tcphost, tcpport, false, wcdata));
			utilthread.setDaemon(true);
			utilthread.start();
			
			logger.debug("Started NineServer comm thread for port " + port);
		
		}
		
		DWEvent evt = new DWEvent(DWDefs.EVENT_TYPE_VSERIAL, this.dwProto.getHandlerNo());
		evt.setParam(DWDefs.EVENT_ITEM_VPORT_ACTION, DWDefs.EVENT_ACTION_OPEN + "");
		evt.setParam(DWDefs.EVENT_ITEM_VPORT, this.port + "");
		
		DriveWireServer.submitEvent(evt);
		
		
	}
	
	public void close()
	{
		if (this.opens > 0)
		{
			this.opens--;
			logger.debug("close port " + this.port + ", total opens: " + this.opens + " data in buffer: " + this.inputBuffer.getAvailable());
			
			DWEvent evt = new DWEvent(DWDefs.EVENT_TYPE_VSERIAL, this.dwProto.getHandlerNo());
			evt.setParam(DWDefs.EVENT_ITEM_VPORT_ACTION, DWDefs.EVENT_ACTION_CLOSE + "");
			evt.setParam(DWDefs.EVENT_ITEM_VPORT, this.port + "");
			
			DriveWireServer.submitEvent(evt);
			
			// send term if last open and not window
			if ((this.opens == 0) && (this.getUtilMode() != DWDefs.UTILMODE_NINESERVER))
			{
				logger.debug("setting term on port " + this.port);
				this.wanttodie = true;
			
				// close socket channel if connected
				if ((this.sktchan != null) && (this.sktchan.isOpen()))
				{
					logger.debug("closing io channel on port " + this.port);
					
					try {
						this.sktchan.close();
					} catch (IOException e) {
						logger.warn(e.getMessage());
					}
				
					this.sktchan = null;
				}
				
				// close listeners if this was their control port
				this.dwProto.getVPorts().getListenerPool().closePortServerSockets(this.port);
				
			}
		}
		else
		{
			// this actually happens in normal operation, when both sides have code to
			// close port on exit.. probably not worth an error message
			
			// logger.debug("close port " + this.port + " with no opens? might be fine");
		}
		
	}
	
	public boolean isTerm()
	{
		return(wanttodie);
	}
	

	public void setPortChannel(SocketChannel sc)
	{
		this.sktchan = sc;
	}
	


	
	public void setUtilMode(int mode)
	{
		DWEvent evt = new DWEvent(DWDefs.EVENT_TYPE_VSERIAL, this.dwProto.getHandlerNo());
		evt.setParam(DWDefs.EVENT_ITEM_VPORT_MODE, mode + "");
		evt.setParam(DWDefs.EVENT_ITEM_VPORT, this.port + "");
		
		DriveWireServer.submitEvent(evt);
		
		this.utilmode = mode;
	}

	public int getUtilMode()
	{
		return(this.utilmode);
	}


	public void setPD_INT(byte pD_INT) 
	{
		PD_INT = pD_INT;
		this.inputBuffer.setDW_PD_INT(PD_INT);
		
		DWEvent evt = new DWEvent(DWDefs.EVENT_TYPE_VSERIAL, this.dwProto.getHandlerNo());
		evt.setParam(DWDefs.EVENT_ITEM_VPORT, this.port + "");
		evt.setParam(DWDefs.EVENT_ITEM_VPORT_PDINT, pD_INT + "");
		
		DriveWireServer.submitEvent(evt);
	}



	public byte getPD_INT() {
		return PD_INT;
	}



	public void setPD_QUT(byte pD_QUT) 
	{
		PD_QUT = pD_QUT;
		this.inputBuffer.setDW_PD_QUT(PD_QUT);
		
		DWEvent evt = new DWEvent(DWDefs.EVENT_TYPE_VSERIAL, this.dwProto.getHandlerNo());
		evt.setParam(DWDefs.EVENT_ITEM_VPORT, this.port + "");
		evt.setParam(DWDefs.EVENT_ITEM_VPORT_PDQUT, pD_QUT + "");
		
		DriveWireServer.submitEvent(evt);
		
	}



	public byte getPD_QUT() 
	{
		return PD_QUT;
	}




	public void sendUtilityFailResponse(byte errno, String txt) 
	{
		String perrno = String.format("%03d", (errno & 0xFF));
		logger.debug("command failed: " + perrno + " " + txt);
		try 
		{
			String r = "FAIL " + perrno + " " + txt + (char) 10 + (char) 13;
			inputBuffer.getOutputStream().write(r.getBytes());
			
			DWEvent evt = new DWEvent(DWDefs.EVENT_TYPE_VSERIAL, this.dwProto.getHandlerNo());
			evt.setParam(DWDefs.EVENT_ITEM_VPORT_ACTION, DWDefs.EVENT_ACTION_WRITE + "");
			evt.setParam(DWDefs.EVENT_ITEM_VPORT, this.port + "");
			evt.setParam(DWDefs.EVENT_ITEM_VPORT_DATA, r.getBytes() );
					
			DriveWireServer.submitEvent(evt);
			
			
			
		} 
		catch (IOException e) 
		{
			logger.warn(e.getMessage());
		}
		//this.utilhandler.respondFail(code, txt);
	}
	
	public void sendUtilityOKResponse(String txt) 
	{
		try 
		{
			String r = "OK " + txt + (char) 10 + (char) 13;
			
			inputBuffer.getOutputStream().write(r.getBytes());
			
			DWEvent evt = new DWEvent(DWDefs.EVENT_TYPE_VSERIAL, this.dwProto.getHandlerNo());
			evt.setParam(DWDefs.EVENT_ITEM_VPORT_ACTION, DWDefs.EVENT_ACTION_WRITE + "");
			evt.setParam(DWDefs.EVENT_ITEM_VPORT, this.port + "");
			evt.setParam(DWDefs.EVENT_ITEM_VPORT_DATA, r.getBytes() );
			
		} 
		catch (IOException e) 
		{
			logger.warn(e.getMessage());
		}
		//this.utilhandler.respondOk(txt);
	}



	public void setDD(byte[] devdescr) 
	{
		this.DD = devdescr;
	}

	public byte[] getDD()
	{
		return(this.DD);
	}

	public int getOpen() 
	{
		return(this.opens);
	}

	

	public void sendConnectionAnnouncement(int conno, int localport, String hostaddr)
	{
		this.porthandler.announceConnection(conno, localport, hostaddr);
		
	}

	public void setConn(int conno)
	{
		this.conno = conno;
	}
	
	public int getConn()
	{
		return(this.conno);
	}

	public void shutdown()
	{
		// close this port
		this.connected = false;
		this.opens = 0;
		this.sktchan = null;
		this.porthandler = null;
		this.wanttodie = true;
		
		DWEvent evt = new DWEvent(DWDefs.EVENT_TYPE_VSERIAL, this.dwProto.getHandlerNo());
		evt.setParam(DWDefs.EVENT_ITEM_VPORT, this.port + "");
		evt.setParam(DWDefs.EVENT_ITEM_VPORT_ACTION, DWDefs.EVENT_ACTION_DESTROY + "");
		
		DriveWireServer.submitEvent(evt);
		
	}



	public DWVModem getVModem() 
	{
		return this.porthandler.getVModem();
	}


	
}

