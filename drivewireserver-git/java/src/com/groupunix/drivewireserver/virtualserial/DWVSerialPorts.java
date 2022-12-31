package com.groupunix.drivewireserver.virtualserial;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.List;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Soundbank;
import javax.sound.midi.Synthesizer;

import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.log4j.Logger;

import com.groupunix.drivewireserver.DWDefs;
import com.groupunix.drivewireserver.DWEvent;
import com.groupunix.drivewireserver.DriveWireServer;
import com.groupunix.drivewireserver.dwexceptions.DWConnectionNotValidException;
import com.groupunix.drivewireserver.dwexceptions.DWPortNotOpenException;
import com.groupunix.drivewireserver.dwexceptions.DWPortNotValidException;
import com.groupunix.drivewireserver.dwprotocolhandler.DWVSerialProtocol;

public class DWVSerialPorts {

	private static final Logger logger = Logger.getLogger("DWServer.DWVSerialPorts");
	

	public static final int MODE_TERM = 3;
	
	
	private DWVSerialProtocol dwProto;
	private boolean bytelog = false;
	
	
	private DWVSerialPort[] vserialPorts;
	private DWVPortListenerPool listenerpool = new DWVPortListenerPool();
	
	private int[] dataWait;
	
	// midi stuff
	private MidiDevice midiDevice;
	private Synthesizer midiSynth;
	private String soundbankfilename = null;
	private boolean midiVoicelock = false;
	private  HierarchicalConfiguration midiProfConf = null;
	private int[] GMInstrumentCache;
	private int maxNports = 0;
	private int maxZports = 0;
	private int maxports = 0;
	private int nTermPort = 0;
	private int zTermPort = 0;
	private int MIDIPort = 0;
	private int multiReadLimit = 0;
	private boolean rebootRequested = false;
	
	public DWVSerialPorts(DWVSerialProtocol dwProto)
	{
		this.dwProto = dwProto;
		bytelog = dwProto.getConfig().getBoolean("LogVPortBytes", false);
		
		maxNports = dwProto.getConfig().getInt("VSerial_MaxNDevPorts", 16);
		maxZports = dwProto.getConfig().getInt("VSerial_MaxZDevPorts", 16);
		nTermPort = dwProto.getConfig().getInt("VSerial_NTermPort", 0);
		zTermPort = dwProto.getConfig().getInt("VSerial_ZTermPort", 16);
		MIDIPort = dwProto.getConfig().getInt("VSerial_MIDIPort", 14);
		this.multiReadLimit = dwProto.getConfig().getInt("VSerial_MultiReadLimit", 3);
		
		maxports = maxNports + maxZports;
		
		dataWait = new int[maxports];
		vserialPorts = new DWVSerialPort[maxports];
		
		
		if (dwProto.getConfig().getBoolean("UseMIDI", false) && !DriveWireServer.getNoMIDI())
		{
			
			clearGMInstrumentCache();
		
			try 
			{
				
				// set default output
				if (dwProto.getConfig().containsKey("MIDIDefaultOutput"))
				{
					int devno = dwProto.getConfig().getInt("MIDIDefaultOutput", -1);
					
					MidiDevice.Info[] infos = MidiSystem.getMidiDeviceInfo();
					
					if ((devno < 0) || (devno > infos.length))
					{
						logger.warn("Invalid MIDI output device # " + devno + " specified in MIDIDefaultOutput setting");
					}
					else
					{
						setMIDIDevice(MidiSystem.getMidiDevice(infos[devno]));
					}
				
				}
				else
				{
					midiSynth = MidiSystem.getSynthesizer();
					setMIDIDevice(midiSynth);
				}
				
				// soundbank
				if (dwProto.getConfig().containsKey("MIDISynthDefaultSoundbank"))
				{
					loadSoundbank(dwProto.getConfig().getString("MIDISynthDefaultSoundbank"));
				}
								
				
				// default translation profile
				if (dwProto.getConfig().containsKey("MIDISynthDefaultProfile"))
				{
					if (!setMidiProfile(dwProto.getConfig().getString("MIDISynthDefaultProfile")))
					{
						logger.warn("Invalid MIDI profile specified in config file.");
					}
				}
			
			} 
			catch (MidiUnavailableException e) 
			{
				logger.warn("MIDI is not available");
			}
		}
	}





	public void openPort(int port) throws DWPortNotValidException
	{
		this.validateport(port);
		if (vserialPorts[port] == null)
		{
			resetPort(port);
		}
		
 		vserialPorts[port].open();
	}


	public String prettyPort(int port) 
	{
		if (port == this.nTermPort)
		{
			return("NTerm");
		}
		else if (port == this.zTermPort)
		{
			return("ZTerm");
		}
		else if (port == this.MIDIPort)
		{
			return("MIDI");
		}
		else if (port < this.maxNports)
		{
			return("N" + port);
		}
		else if (port < this.maxNports + this.maxZports)
		{
			return("Z" + (port - this.maxNports));
		}
		else
		{
			return("?" + port);
		}
	}


	public void closePort(int port) throws DWPortNotValidException
	{
		if (port < vserialPorts.length)
		{
			if (vserialPorts[port] != null)
			{
				vserialPorts[port].close();	
				//vserialPorts[port] = null;
			}
		}
		else
		{
			throw new DWPortNotValidException("Valid port range is 0 - " + (vserialPorts.length - 1));
		}
	}
	

	public byte[] serRead() 
	{
		byte[] response = new byte[2];
		
		// reboot req takes absolute priority
		
		if (this.isRebootRequested())
		{
			response[0] = (byte) 16;
			response[1] = (byte) 255;
				
			logger.debug("reboot request pending, sending response " + response[0] + "," + response[1]);
			
			this.setRebootRequested(false);
			return(response);
		}
			
		
		// Z devices go first...
		
		for (int i = this.maxNports;i < this.maxNports+this.maxZports;i++)
		{
			if (vserialPorts[i] != null)
			{
				if (vserialPorts[i].bytesWaiting() > 0)
				{
					// increment wait count
					dataWait[i]++;
					
					logger.debug("waiting Z " +i + ": " + vserialPorts[i].bytesWaiting());
				}
			}
		}
		
		// second pass, look for oldest waiting ports
		
		int oldestZ = 0;
		int oldestZport = -1;
		
		for (int i = this.maxNports;i < this.maxNports+this.maxZports;i++)
		{
			if (vserialPorts[i] != null)
			{
				if (dataWait[i] > oldestZ)
				{
					oldestZ = dataWait[i];
					oldestZport = i;
					
				}
			}
		}
		
		if (oldestZport > -1)
		{
			// if we have a small byte waiter, send serread for it
			
			dataWait[oldestZport] = 0;
			response[0] = (byte) (  (DWDefs.POLL_RESP_MODE_WINDOW << 6)  + (oldestZport) -  this.maxNports );
			response[1] = vserialPorts[oldestZport].read1();
				
			logger.debug("Z poll response " + response[0] + "," + response[1]);
			
			return(response);
		}
		
		
		
		
		
		// N devices
		
		
		
		
		// first look for termed ports
		for (int i = 0;i<this.maxNports;i++)
		{
			if (vserialPorts[i] != null)
			{
				if (vserialPorts[i].isTerm())
				{
					response[0] = (byte) 16;  // port status
					response[1] = (byte) i;   // 000 portnumber
					
					logger.debug("sending terminated status to coco for port " + i);
					
					vserialPorts[i] = new DWVSerialPort(this,this.dwProto, i);
					
					return(response);
				}
			}
		}
		
		// first data pass, increment data waiters
		
		for (int i = 0;i<this.maxNports;i++)
		{
			if (vserialPorts[i] != null)
			{
				if (vserialPorts[i].bytesWaiting() > 0)
				{
					// increment wait count
					dataWait[i]++;
				}
			}
		}
		
		// second pass, look for oldest waiting ports
		
		int oldest1 = 0;
		int oldest1port = -1;
		int oldestM = 0;
		int oldestMport = -1;
		
		for (int i = 0;i<this.maxNports;i++)
		{
			if (vserialPorts[i] != null)
			{
				if (vserialPorts[i].bytesWaiting() < this.multiReadLimit)
				{
					if (dataWait[i] > oldest1)
					{
						oldest1 = dataWait[i];
						oldest1port = i;
					}
				}
				else
				{
					if (dataWait[i] > oldestM)
					{
						oldestM = dataWait[i];
						oldestMport = i;
					}
				}
			}
		}
		
		if (oldest1port > -1)
		{
			// if we have a small byte waiter, send serread for it
			
			dataWait[oldest1port] = 0;
			response[0] = (byte) (oldest1port + 1);     // add one
			response[1] = vserialPorts[oldest1port].read1();  // send data byte
		}
		else if (oldestMport > -1)
		{
			// send serream for oldest bulk
			
			dataWait[oldestMport] = 0;
			response[0] = (byte) (oldestMport + 16 + 1);     // add one and 16 for serreadm
			response[1] = (byte) vserialPorts[oldestMport].bytesWaiting(); //send data size
			// logger.debug("SERREADM RESPONSE: " + Integer.toBinaryString(response[0]) + " " + Integer.toBinaryString(response[1]));

		}
		else
		{
			// no waiting ports
			
			response[0] = (byte) 0;
			response[1] = (byte) 0;
		}
		
		// logger.debug("SERREAD RESPONSE: " + Integer.toBinaryString(response[0]) + " " + Integer.toBinaryString(response[1]));
		
		return(response);
	}


	public void serWriteM(int port, byte[] data) throws DWPortNotOpenException, DWPortNotValidException
	{
		for (int i = 0;i<data.length;i++)
		{
			// inefficient as hell, but serwriteM isn't even implemented in driver anyway
			serWrite(port, data[i]);
		}
	}
	
	public void serWriteM(int port, byte[] data, int bread) throws DWPortNotOpenException, DWPortNotValidException 
	{
		for (int i = 0;(i<data.length) && (i<bread);i++)
		{
			// inefficient as hell, but serwriteM isn't even implemented in driver anyway
			serWrite(port, data[i]);
		}
	}

	

	public void serWrite(int port, int databyte) throws DWPortNotOpenException, DWPortNotValidException 
	{
		
		
		if ((port < this.maxports) && (port >= 0))
		{
			if (vserialPorts[port] != null)
			{
				if (vserialPorts[port].isOpen())
				{
					if (bytelog)
					{
						
						logger.debug("write to port " + port + ": " + databyte + " (" + (char)databyte + ")" );
					}
					
					// normal write
					vserialPorts[port].write(databyte);
				}
				else
				{
					throw new DWPortNotOpenException("Port " + port + " is not open (but coco sent us a byte: " + (0xff & databyte) + " '" + Character.toString((char) databyte) + "')");
				}
			}
			else
			{
				// should port not initialized be different than port not open?
				throw new DWPortNotOpenException("Port " + port + " is not open (but coco sent us a byte: " + (0xff & databyte) + " '" + Character.toString((char) databyte) + "')");
			}
		}
		else
		{
			throw new DWPortNotValidException(port + " is not a valid port number");
		}
		
	}



	public byte[] serReadM(int port, int len) throws DWPortNotOpenException, DWPortNotValidException 
	{
		

		if ((port < this.maxports) && (port >= 0))
		{
			if (vserialPorts[port].isOpen())
			{
				byte[] data = new byte[len];
				data = vserialPorts[port].readM(len);
				return(data);
			}
			else
			{
				throw new DWPortNotOpenException("Port " + port + " is not open");
			}
		}
		else
		{
			throw new DWPortNotValidException(port + " is not a valid port number");
		}
		
	}
	
	public OutputStream getPortInput(int vport) throws DWPortNotValidException 
	{
		validateport(vport);
		return (vserialPorts[vport].getPortInput());
	}

	public InputStream getPortOutput(int vport) throws DWPortNotValidException 
	{
		validateport(vport);
		return (vserialPorts[vport].getPortOutput());
	}
	
	
	/*
	
	public InputStream getPortOutput(int vport) throws DWPortNotValidException 
	{
		validateport(vport);
		return (vserialPorts[vport].getPortOutput());
	}
	
	public void setPortOutput(int vport, OutputStream output)
	{
		if (isNull(vport))
		{
			logger.debug("attempt to set output on null port " + vport);
		}
		else
		{
			vserialPorts[vport].setPortOutput(output);
		}
	}
	*/

	public void setPortChannel(int vport, SocketChannel sc)
	{
		if (isNull(vport))
		{
			logger.debug("attempt to set io channel on null port " + vport);
		}
		else
		{
			vserialPorts[vport].setPortChannel(sc);
		}
	}
	

	public void markConnected(int port) 
	{
		if ((port < vserialPorts.length) && (vserialPorts[port] != null))
		{
			vserialPorts[port].setConnected(true);
		}
		else
		{
			logger.warn("mark connected on invalid port " + port);
		}
	}


	public void markDisconnected(int port) 
	{
		if ((port < vserialPorts.length) && (vserialPorts[port] != null))
		{
			vserialPorts[port].setConnected(false);
		}
		else
		{
			logger.warn("mark disconnected on invalid port " + port);
		}
	}


	public boolean isConnected(int port)
	{
		if ((port < vserialPorts.length) && (vserialPorts[port] != null))
		{
			return(vserialPorts[port].isConnected());
		}
		return(false);
	}

	

	public void setUtilMode(int port, int mode) throws DWPortNotValidException
	{
		validateport(port);
		vserialPorts[port].setUtilMode(mode);
	}
	
		
	
	
	public void wr3ite1(int port, byte data) throws IOException, DWPortNotValidException
	{
		validateport(port);
		getPortInput(port).write(data);
	}

	public void write(int port, String str) throws DWPortNotValidException
	{
		validateport(port);
		vserialPorts[port].writeM(str);
		
	}

	
	
	public void setPD_INT(int port, byte pD_INT) throws DWPortNotValidException 
	{
		validateport(port);
		vserialPorts[port].setPD_INT(pD_INT);
	}



	public byte getPD_INT(int port) throws DWPortNotValidException 
	{
		validateport(port);
		return(vserialPorts[port].getPD_INT());
	}



	public void setPD_QUT(int port, byte pD_QUT) throws DWPortNotValidException 
	{
		validateport(port);
		vserialPorts[port].setPD_QUT(pD_QUT);
	}



	public byte getPD_QUT(int port) throws DWPortNotValidException 
	{
		validateport(port);
		return(vserialPorts[port].getPD_QUT());
	}






	public void sendUtilityFailResponse(int vport, byte code, String txt) throws DWPortNotValidException 
	{
		validateport(vport);
		logger.info("API FAIL: port " + vport + " code " + code + ": " + txt);
		vserialPorts[vport].sendUtilityFailResponse(code, txt);	
	}


	public void sendUtilityOKResponse(int vport, String txt) throws DWPortNotValidException 
	{
		validateport(vport);
		logger.debug("API OK: port " + vport + ": command successful");
		vserialPorts[vport].sendUtilityOKResponse("command successful");
		writeToCoco(vport, txt);	
	}

	
	public void sendUtilityOKResponse(int vport, byte[] responseBytes) throws DWPortNotValidException 
	{
		validateport(vport);
		logger.debug("API OK: port " + vport + ": command successful (byte mode)");
		vserialPorts[vport].sendUtilityOKResponse("command successful");
		writeToCoco(vport, responseBytes);
		
	}

	public int bytesWaiting(int vport) throws DWPortNotValidException 
	{
		validateport(vport);
		return(vserialPorts[vport].bytesWaiting());	
	}

	

	public void setDD(byte vport, byte[] devdescr) throws DWPortNotValidException
	{
		validateport(vport);
		vserialPorts[vport].setDD(devdescr);		
	}


	public void resetAllPorts() 
	{
		logger.debug("Resetting all virtual serial ports - part 1, close all sockets");
		
		
		for (int i = 0;i<this.maxports;i++)
		{
			this.listenerpool.closePortConnectionSockets(i);
			this.listenerpool.closePortServerSockets(i);
		}
		
		logger.debug("Resetting all virtual serial ports - part 2, init all ports");
		
		//vserialPorts = new DWVSerialPort[MAX_PORTS];
		for (int i = 0;i<this.maxports;i++)
		{
			// dont reset term
			if (i != this.nTermPort)
			{
				try
				{
					resetPort(i);
				} 
				catch (DWPortNotValidException e)
				{
					logger.warn(e.getMessage());
				}
			}
		}
		
		// if term is null, init
		if ((this.nTermPort > -1) && (this.vserialPorts[this.nTermPort] == null))
		{
			try
			{
				resetPort(this.nTermPort);
			} 
			catch (DWPortNotValidException e)
			{
				logger.warn(e.getMessage());
			}
		}
		
	}

	public void resetPort(int i) throws DWPortNotValidException
	{
		if ((i >= 0) && (i < vserialPorts.length))
		{
			vserialPorts[i] = new DWVSerialPort(this, this.dwProto, i);
		}
		else
		{
			throw new DWPortNotValidException("Invalid port # " + i);
		}	
		
	}
	
	public boolean isOpen(int vport) 
	{
		if ((vport >= 0) && (vport < vserialPorts.length) && (vserialPorts[vport] != null))
			return(vserialPorts[vport].isOpen());
		
		return(false);
	}


	public int getOpen(int i) throws DWPortNotValidException 
	{
		validateport(i);
		return(vserialPorts[i].getOpen());
		
	}


	public byte[] getDD(int i) throws DWPortNotValidException
	{
		validateport(i);
		return(vserialPorts[i].getDD());
		
	}


	
	//public static void setSocket(int vport, Socket skt) 
	//{
	//	vserialPorts[vport].setSocket(skt);
	//}


	
	public void writeToCoco(int i, byte databyte) throws DWPortNotValidException 
	{
		validateport(i);
		
		vserialPorts[i].writeToCoco(databyte);
		
		DWEvent evt = new DWEvent(DWDefs.EVENT_TYPE_VSERIAL, this.dwProto.getHandlerNo());
		evt.setParam(DWDefs.EVENT_ITEM_VPORT_ACTION, DWDefs.EVENT_ACTION_READ + "");
		evt.setParam(DWDefs.EVENT_ITEM_VPORT, i + "");
		evt.setParam(DWDefs.EVENT_ITEM_VPORT_DATA,  databyte );
		
		//DriveWireServer.submitEvent(evt);
	}
	
	public void writeToCoco(int vport, String str) throws DWPortNotValidException 
	{
		validateport(vport);
		
		vserialPorts[vport].writeToCoco(str);
		
		DWEvent evt = new DWEvent(DWDefs.EVENT_TYPE_VSERIAL, this.dwProto.getHandlerNo());
		evt.setParam(DWDefs.EVENT_ITEM_VPORT_ACTION, DWDefs.EVENT_ACTION_READ + "");
		evt.setParam(DWDefs.EVENT_ITEM_VPORT, vport + "");
		evt.setParam(DWDefs.EVENT_ITEM_VPORT_DATA, str.getBytes() );
		
		//DriveWireServer.submitEvent(evt);
		
		
	}
	
	public void writeToCoco(int vport, byte[] b) throws DWPortNotValidException 
	{
		validateport(vport);
				
		vserialPorts[vport].writeToCoco(b);
		
		DWEvent evt = new DWEvent(DWDefs.EVENT_TYPE_VSERIAL, this.dwProto.getHandlerNo());
		evt.setParam(DWDefs.EVENT_ITEM_VPORT_ACTION, DWDefs.EVENT_ACTION_READ + "");
		evt.setParam(DWDefs.EVENT_ITEM_VPORT, vport + "");
		evt.setParam(DWDefs.EVENT_ITEM_VPORT_DATA, b );
		
		//DriveWireServer.submitEvent(evt);
		
		
	}


	public void writeToCoco(int vport, byte[] b, int offset, int length) throws DWPortNotValidException 
	{
		validateport(vport);
		
		byte[] buf = new byte[length];
		
		System.arraycopy(b, offset, buf, 0, length);
		
		vserialPorts[vport].writeToCoco(buf);
		
		DWEvent evt = new DWEvent(DWDefs.EVENT_TYPE_VSERIAL, this.dwProto.getHandlerNo());
		evt.setParam(DWDefs.EVENT_ITEM_VPORT_ACTION, DWDefs.EVENT_ACTION_READ + "");
		evt.setParam(DWDefs.EVENT_ITEM_VPORT, vport + "");
		evt.setParam(DWDefs.EVENT_ITEM_VPORT_DATA, buf );
		
		//DriveWireServer.submitEvent(evt);
		
	}


	
	public boolean isNull(int vport)
	{
		if ((vport >= 0) && (vport < vserialPorts.length) && (vserialPorts[vport] == null))
			return(true);
		
		return(false);
	}


	public boolean isValid(int vport)
	{
	  if ((vport >= 0) && (vport < this.maxports))
		  return(true);
	  
	  return(false);
		
	}
	
	private void validateport(int vport) throws DWPortNotValidException 
	{
		if (!isValid(vport))
			throw(new DWPortNotValidException("Invalid port #" + vport));
				
		if (isNull(vport) )
			throw(new DWPortNotValidException("Null port #" + vport));
		
	}	


	public void sendConnectionAnnouncement(int vport, int conno, int localport, String hostaddr) throws DWPortNotValidException
	{
		validateport(vport);
		vserialPorts[vport].sendConnectionAnnouncement(conno, localport, hostaddr);
	}


	public void setConn(int vport, int conno) throws DWPortNotValidException
	{
		validateport(vport);
		vserialPorts[vport].setConn(conno);
		
	}
	





	public int getConn(int vport) throws DWPortNotValidException
	{
		validateport(vport);
		return(vserialPorts[vport].getConn());
	}


	public String getHostIP(int vport) throws DWPortNotValidException, DWConnectionNotValidException
	{
		validateport(vport);
		return(this.listenerpool.getConn( vserialPorts[vport].getConn() ).socket().getInetAddress().getHostAddress());
		
	}


	public int getHostPort(int vport) throws DWPortNotValidException, DWConnectionNotValidException
	{
		validateport(vport);
		return(this.listenerpool.getConn(vserialPorts[vport].getConn()).socket().getPort());
	}


	public void shutdown()
	{
		logger.debug("shutting down");
		
		for (int i = 0;i<this.maxports;i++)
		{
			this.listenerpool.closePortConnectionSockets(i);
			this.listenerpool.closePortServerSockets(i);
			if ((this.vserialPorts[i] != null) && (i != MIDIPort))
			{
				this.vserialPorts[i].shutdown();
			}
		}
	}


	
	
	
	public MidiDevice.Info getMidiDeviceInfo()
	{
		if (this.midiDevice != null)
			return(this.midiDevice.getDeviceInfo());
		
		return(null);
	}
	
	
	public void setMIDIDevice(MidiDevice device) throws MidiUnavailableException, IllegalArgumentException
	{
		if (this.midiDevice != null)
		{
			if (this.midiDevice.isOpen())
			{
				logger.debug("midi: closing " + this.midiDevice.getDeviceInfo().getName());
				this.midiDevice.close();
			}
		}
		
		device.open();
		
		this.midiDevice = device;

		DriveWireServer.submitMIDIEvent(this.dwProto.getHandlerNo(), "device", this.midiDevice.getDeviceInfo().getName());
	    	
		logger.info("midi: opened " + this.midiDevice.getDeviceInfo().getName());

		
	}


	public void sendMIDIMsg(ShortMessage mmsg, int timestamp) 
	{
		if (this.midiDevice != null)
		{
			try 
			{
				this.midiDevice.getReceiver().send(mmsg, timestamp);
			} 
			catch (MidiUnavailableException e) 
			{
				logger.warn(e.getMessage());
			}
			catch (IllegalStateException e)
			{
				logger.warn(e.getMessage());
			}
		}
		else
		{
			logger.warn("No MIDI device for MIDI msg");
		}
	}


	public Receiver getMidiReceiver() throws MidiUnavailableException 
	{
		return(this.midiDevice.getReceiver());
	}
	

	public Synthesizer getMidiSynth() 
	{
		return(midiSynth);
	}
	
	
	public boolean isSoundbankSupported(Soundbank soundbank) 
	{
		return(midiSynth.isSoundbankSupported(soundbank));
	}
		
		
	public boolean setMidiSoundbank(Soundbank soundbank, String fname) 
	{
		
		if (midiSynth.loadAllInstruments(soundbank))
		{
			logger.debug("loaded soundbank file '" + fname + "'");
			this.soundbankfilename = fname;
			return(true);
		}
		
		return(false);
	}
	
	public String getMidiSoundbankFilename()
	{
		return(this.soundbankfilename);
	}
	
	public boolean getMidiVoicelock()
	{
		return(this.midiVoicelock);
	}

	public void setMidiVoicelock(boolean lock)
	{
		this.midiVoicelock = lock;
		DriveWireServer.submitMIDIEvent(this.dwProto.getHandlerNo(), "voicelock", String.valueOf(lock));
    	
		logger.debug("MIDI: synth voicelock = " + lock);
	}

	private void loadSoundbank(String filename) 
	{
		Soundbank soundbank = null;
		
		File file = new File(filename);
		try 
		{
			soundbank = MidiSystem.getSoundbank(file);
		} 
		catch (InvalidMidiDataException e) 
		{
			logger.warn("Error loading soundbank: " + e.getMessage());
			return;
		} 
		catch (IOException e) 
		{
			logger.warn("Error loading soundbank: " + e.getMessage());
			return;
		}
		
		if (isSoundbankSupported(soundbank))
		{				
			if (!setMidiSoundbank(soundbank, filename))
			{
				logger.warn("Failed to set soundbank '" + filename + "'");
				return;
			}
			
			DriveWireServer.submitMIDIEvent(this.dwProto.getHandlerNo(), "soundbank", filename);
	    	
			
		}
		else
		{
			logger.warn("Unsupported soundbank '" + filename + "'");	
			return;
		}	
	}




	public String getMidiProfileName() 
	{
		if (this.midiProfConf != null)
		{
			return(this.midiProfConf.getString("[@name]","none"));
		}
		else
		{
			return("none");
		}
	}

	public HierarchicalConfiguration getMidiProfile() 
	{
		return(this.midiProfConf);
	}

	
	
	@SuppressWarnings("unchecked")
	public boolean setMidiProfile(String profile)
	{
		
		List<HierarchicalConfiguration> profiles = DriveWireServer.serverconfig.configurationsAt("midisynthprofile");
    	
		for(Iterator<HierarchicalConfiguration> it = profiles.iterator(); it.hasNext();)
		{
		    HierarchicalConfiguration mprof = it.next();
		    
		    if (mprof.containsKey("[@name]") && mprof.getString("[@name]").equalsIgnoreCase(profile))
		    {
		    	
		    	this.midiProfConf = (HierarchicalConfiguration) mprof.clone();
		    	doMidiTranslateCurrentVoices();
		    	
		    	DriveWireServer.submitMIDIEvent(this.dwProto.getHandlerNo(), "profile", profile);
		    	
		    	logger.debug("MIDI: set profile to '" + profile + "'");
		    	return(true);
		    }
		    
		}
		
		return(false);
	}
	
	
	
	private void doMidiTranslateCurrentVoices() 
	{
		// translate current GM voices to current profile
		
		if (this.midiSynth != null)
		{
			MidiChannel[] chans = this.midiSynth.getChannels();
			
			for (int i = 0;i < chans.length;i++)
			{
				if (chans[i] != null)
				{
					chans[i].programChange(getGMInstrument(this.GMInstrumentCache[i]));
				}
					
			}
		}
	}




	@SuppressWarnings("unchecked")
	public int getGMInstrument(int voice)
	{
		if (this.midiProfConf == null)
		{
			return(voice);
		}
		
		int xvoice = voice;
		
		List<HierarchicalConfiguration> mappings = this.midiProfConf.configurationsAt("mapping");
		
		for(Iterator<HierarchicalConfiguration> it = mappings.iterator(); it.hasNext();)
		{
			HierarchicalConfiguration sub = it.next();
			
			if ((sub.getInt("[@dev]") + this.midiProfConf.getInt("[@dev_adjust]", 0))  == voice)
			{
				xvoice = sub.getInt("[@gm]") + this.midiProfConf.getInt("[@gm_adjust]", 0);
				logger.debug("MIDI: profile '" + this.midiProfConf.getString("[@name]") + "' translates device inst " + voice + " to GM instr " + xvoice);
				return(xvoice);
			}
			
		}
		
		// no translation match
		return(voice);
	}


	public boolean setMIDIInstr(int channel, int instr) 
	{
		MidiChannel[] chans = this.midiSynth.getChannels();
		
		if (channel < chans.length)
		{
			if (chans[channel] != null)
			{
				chans[channel].programChange(instr);
				logger.debug("MIDI: set instrument " + instr + " on channel " + channel);
				return(true);
			}
		}
		
		return(false);
		
	}

	
	public void clearGMInstrumentCache() 
	{
		this.GMInstrumentCache = new int[16];
		
		for (int i = 0;i<16;i++)
		{
			this.GMInstrumentCache[i] = 0;
		}
	}

	
	public void setGMInstrumentCache(int chan,int instr)
	{
		if ((chan >= 0) && (chan < this.GMInstrumentCache.length))
		{
			this.GMInstrumentCache[chan] = instr;
		}
		else
		{
			logger.debug("MIDI: channel out of range on program change: " + chan);
		}
	}

	public int getGMInstrumentCache(int chan)
	{
		return(this.GMInstrumentCache[chan]);
	}







	public DWVPortListenerPool getListenerPool() 
	{
		return(this.listenerpool);
	}









	public int getUtilMode(int i) throws DWPortNotValidException 
	{
		validateport(i);
		return(this.vserialPorts[i].getUtilMode());
	}




	public int getMaxPorts()
	{
		return this.maxports;
	}


	public int getMaxNPorts()
	{
		return this.maxNports;
	}

	public int getMaxZPorts()
	{
		return this.maxZports;
	}
	
	public int getNTermPort()
	{
		return this.nTermPort;
	}
	
	public int getZTermPort()
	{
		return this.zTermPort;
	}
	
	public int getMIDIPort()
	{
		return this.MIDIPort;
	}





	public DWVModem getPortVModem(int port) throws DWPortNotValidException 
	{
		validateport(port);
		return this.vserialPorts[port].getVModem();
	}





	public boolean isRebootRequested() {
		return rebootRequested;
	}





	public void setRebootRequested(boolean rebootRequested) {
		this.rebootRequested = rebootRequested;
	}





	




	
	


}
