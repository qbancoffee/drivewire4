/*      */ package com.groupunix.drivewireserver.virtualserial;
/*      */ 
/*      */ import com.groupunix.drivewireserver.DriveWireServer;
/*      */ import com.groupunix.drivewireserver.dwexceptions.DWConnectionNotValidException;
/*      */ import com.groupunix.drivewireserver.dwexceptions.DWPortNotOpenException;
/*      */ import com.groupunix.drivewireserver.dwexceptions.DWPortNotValidException;
/*      */ import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocolHandler;
/*      */ import java.io.File;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.OutputStream;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import javax.sound.midi.InvalidMidiDataException;
/*      */ import javax.sound.midi.MidiChannel;
/*      */ import javax.sound.midi.MidiDevice;
/*      */ import javax.sound.midi.MidiSystem;
/*      */ import javax.sound.midi.MidiUnavailableException;
/*      */ import javax.sound.midi.Receiver;
/*      */ import javax.sound.midi.ShortMessage;
/*      */ import javax.sound.midi.Soundbank;
/*      */ import javax.sound.midi.Synthesizer;
/*      */ import org.apache.commons.configuration.HierarchicalConfiguration;
/*      */ import org.apache.log4j.Logger;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class DWVSerialPorts
/*      */ {
/*   31 */   private static final Logger logger = Logger.getLogger("DWServer.DWVSerialPorts");
/*      */   
/*      */   public static final int MULTIREAD_LIMIT = 3;
/*      */   
/*      */   public static final int TERM_PORT = 0;
/*      */   
/*      */   public static final int MODE_TERM = 3;
/*      */   
/*      */   public static final int MAX_COCO_PORTS = 15;
/*      */   
/*      */   public static final int MAX_PORTS = 15;
/*      */   
/*      */   public static final int MIDI_PORT = 14;
/*      */   private DWProtocolHandler dwProto;
/*      */   private boolean bytelog = false;
/*   46 */   private DWVSerialPort[] vserialPorts = new DWVSerialPort[15];
/*   47 */   private DWVPortListenerPool listenerpool = new DWVPortListenerPool();
/*      */   
/*   49 */   private int[] dataWait = new int[15];
/*      */   
/*      */   private MidiDevice midiDevice;
/*      */   
/*      */   private Synthesizer midiSynth;
/*   54 */   private String soundbankfilename = null;
/*      */   private boolean midiVoicelock = false;
/*   56 */   private HierarchicalConfiguration midiProfConf = null;
/*      */   
/*      */   private int[] GMInstrumentCache;
/*      */ 
/*      */   
/*      */   public DWVSerialPorts(DWProtocolHandler dwProto) {
/*   62 */     this.dwProto = dwProto;
/*   63 */     this.bytelog = dwProto.getConfig().getBoolean("LogVPortBytes", false);
/*      */ 
/*      */     
/*   66 */     if (dwProto.getConfig().getBoolean("UseMIDI", false) && !DriveWireServer.getNoMIDI()) {
/*      */ 
/*      */       
/*   69 */       logger.debug("initialize internal midi synth");
/*      */       
/*   71 */       clearGMInstrumentCache();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       try {
/*   78 */         this.midiSynth = MidiSystem.getSynthesizer();
/*      */         
/*   80 */         setMIDIDevice(this.midiSynth);
/*      */         
/*   82 */         if (dwProto.getConfig().containsKey("MIDISynthDefaultSoundbank"))
/*      */         {
/*   84 */           loadSoundbank(dwProto.getConfig().getString("MIDISynthDefaultSoundbank"));
/*      */         }
/*      */         
/*   87 */         if (dwProto.getConfig().containsKey("MIDISynthDefaultProfile"))
/*      */         {
/*   89 */           if (!setMidiProfile(dwProto.getConfig().getString("MIDISynthDefaultProfile")))
/*      */           {
/*   91 */             logger.warn("Invalid MIDI profile specified in config file.");
/*      */           
/*      */           }
/*      */         }
/*      */       }
/*   96 */       catch (MidiUnavailableException e) {
/*      */         
/*   98 */         logger.warn("MIDI is not available");
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void openPort(int port) throws DWPortNotValidException {
/*  109 */     validateport(port);
/*  110 */     if (this.vserialPorts[port] == null)
/*      */     {
/*  112 */       resetPort(port);
/*      */     }
/*      */     
/*  115 */     this.vserialPorts[port].open();
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public String prettyPort(int port) {
/*  121 */     if (port == 0)
/*      */     {
/*  123 */       return "Term";
/*      */     }
/*  125 */     if (port == 14)
/*      */     {
/*  127 */       return "/MIDI";
/*      */     }
/*  129 */     if (port < 15)
/*      */     {
/*  131 */       return "/N" + port;
/*      */     }
/*      */ 
/*      */     
/*  135 */     return "NA:" + port;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void closePort(int port) throws DWPortNotValidException {
/*  142 */     if (port < this.vserialPorts.length) {
/*      */       
/*  144 */       if (this.vserialPorts[port] != null)
/*      */       {
/*  146 */         this.vserialPorts[port].close();
/*      */       
/*      */       }
/*      */     }
/*      */     else {
/*      */       
/*  152 */       throw new DWPortNotValidException("Valid port range is 0 - " + (this.vserialPorts.length - 1));
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public byte[] serRead() {
/*  159 */     byte[] response = new byte[2];
/*      */ 
/*      */     
/*      */     int i;
/*      */     
/*  164 */     for (i = 0; i < 15; i++) {
/*      */       
/*  166 */       if (this.vserialPorts[i] != null)
/*      */       {
/*  168 */         if (this.vserialPorts[i].isTerm()) {
/*      */           
/*  170 */           response[0] = 16;
/*  171 */           response[1] = (byte)i;
/*      */           
/*  173 */           logger.debug("sending terminated status to coco for port " + i);
/*      */           
/*  175 */           this.vserialPorts[i] = new DWVSerialPort(this.dwProto, i);
/*      */           
/*  177 */           return response;
/*      */         } 
/*      */       }
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  186 */     for (i = 0; i < 15; i++) {
/*      */       
/*  188 */       if (this.vserialPorts[i] != null)
/*      */       {
/*  190 */         if (this.vserialPorts[i].bytesWaiting() > 0)
/*      */         {
/*      */           
/*  193 */           this.dataWait[i] = this.dataWait[i] + 1;
/*      */         }
/*      */       }
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/*  200 */     int oldest1 = 0;
/*  201 */     int oldest1port = -1;
/*  202 */     int oldestM = 0;
/*  203 */     int oldestMport = -1;
/*      */     
/*  205 */     for (int j = 0; j < 15; j++) {
/*      */       
/*  207 */       if (this.vserialPorts[j] != null)
/*      */       {
/*  209 */         if (this.vserialPorts[j].bytesWaiting() < 3) {
/*      */           
/*  211 */           if (this.dataWait[j] > oldest1)
/*      */           {
/*  213 */             oldest1 = this.dataWait[j];
/*  214 */             oldest1port = j;
/*      */           
/*      */           }
/*      */         
/*      */         }
/*  219 */         else if (this.dataWait[j] > oldestM) {
/*      */           
/*  221 */           oldestM = this.dataWait[j];
/*  222 */           oldestMport = j;
/*      */         } 
/*      */       }
/*      */     } 
/*      */ 
/*      */     
/*  228 */     if (oldest1port > -1) {
/*      */ 
/*      */ 
/*      */       
/*  232 */       this.dataWait[oldest1port] = 0;
/*  233 */       response[0] = (byte)(oldest1port + 1);
/*  234 */       response[1] = this.vserialPorts[oldest1port].read1();
/*      */     }
/*  236 */     else if (oldestMport > -1) {
/*      */ 
/*      */ 
/*      */       
/*  240 */       this.dataWait[oldestMport] = 0;
/*  241 */       response[0] = (byte)(oldestMport + 16 + 1);
/*  242 */       response[1] = (byte)this.vserialPorts[oldestMport].bytesWaiting();
/*      */ 
/*      */     
/*      */     }
/*      */     else {
/*      */ 
/*      */ 
/*      */       
/*  250 */       response[0] = 0;
/*  251 */       response[1] = 0;
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/*  256 */     return response;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void serWriteM(int port, byte[] data) throws DWPortNotOpenException, DWPortNotValidException {
/*  262 */     for (int i = 0; i < data.length; i++)
/*      */     {
/*      */       
/*  265 */       serWrite(port, data[i]);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void serWrite(int port, int databyte) throws DWPortNotOpenException, DWPortNotValidException {
/*  274 */     if (port < 15 && port >= 0) {
/*      */       
/*  276 */       if (this.vserialPorts[port] != null)
/*      */       {
/*  278 */         if (this.vserialPorts[port].isOpen())
/*      */         {
/*  280 */           if (this.bytelog)
/*      */           {
/*      */             
/*  283 */             logger.debug("write to port " + port + ": " + databyte + " (" + (char)databyte + ")");
/*      */           }
/*      */ 
/*      */           
/*  287 */           this.vserialPorts[port].write(databyte);
/*      */         }
/*      */         else
/*      */         {
/*  291 */           throw new DWPortNotOpenException("Port " + port + " is not open");
/*      */         }
/*      */       
/*      */       }
/*      */       else
/*      */       {
/*  297 */         throw new DWPortNotOpenException("Port " + port + " is not open");
/*      */       }
/*      */     
/*      */     } else {
/*      */       
/*  302 */       throw new DWPortNotValidException(port + " is not a valid port number");
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public byte[] serReadM(int port, int len) throws DWPortNotOpenException, DWPortNotValidException {
/*  313 */     if (port < 15 && port >= 0) {
/*      */       
/*  315 */       if (this.vserialPorts[port].isOpen()) {
/*      */         
/*  317 */         byte[] data = new byte[len];
/*  318 */         data = this.vserialPorts[port].readM(len);
/*  319 */         return data;
/*      */       } 
/*      */ 
/*      */       
/*  323 */       throw new DWPortNotOpenException("Port " + port + " is not open");
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/*  328 */     throw new DWPortNotValidException(port + " is not a valid port number");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public OutputStream getPortInput(int vport) throws DWPortNotValidException {
/*  336 */     validateport(vport);
/*  337 */     return this.vserialPorts[vport].getPortInput();
/*      */   }
/*      */ 
/*      */   
/*      */   public InputStream getPortOutput(int vport) throws DWPortNotValidException {
/*  342 */     validateport(vport);
/*  343 */     return this.vserialPorts[vport].getPortOutput();
/*      */   }
/*      */ 
/*      */   
/*      */   public void setPortOutput(int vport, OutputStream output) {
/*  348 */     if (isNull(vport)) {
/*      */       
/*  350 */       logger.debug("attempt to set output on null port " + vport);
/*      */     }
/*      */     else {
/*      */       
/*  354 */       this.vserialPorts[vport].setPortOutput(output);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void markConnected(int port) {
/*  361 */     if (port < this.vserialPorts.length && this.vserialPorts[port] != null) {
/*      */       
/*  363 */       this.vserialPorts[port].setConnected(true);
/*      */     }
/*      */     else {
/*      */       
/*  367 */       logger.warn("mark connected on invalid port " + port);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void markDisconnected(int port) {
/*  374 */     if (port < this.vserialPorts.length && this.vserialPorts[port] != null) {
/*      */       
/*  376 */       this.vserialPorts[port].setConnected(false);
/*      */     }
/*      */     else {
/*      */       
/*  380 */       logger.warn("mark disconnected on invalid port " + port);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isConnected(int port) {
/*  387 */     if (port < this.vserialPorts.length && this.vserialPorts[port] != null)
/*      */     {
/*  389 */       return this.vserialPorts[port].isConnected();
/*      */     }
/*  391 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setUtilMode(int port, int mode) throws DWPortNotValidException {
/*  398 */     validateport(port);
/*  399 */     this.vserialPorts[port].setUtilMode(mode);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void write1(int port, byte data) throws IOException, DWPortNotValidException {
/*  407 */     validateport(port);
/*  408 */     getPortInput(port).write(data);
/*      */   }
/*      */ 
/*      */   
/*      */   public void write(int port, String str) throws DWPortNotValidException {
/*  413 */     validateport(port);
/*  414 */     this.vserialPorts[port].writeM(str);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setPD_INT(int port, byte pD_INT) throws DWPortNotValidException {
/*  422 */     validateport(port);
/*  423 */     this.vserialPorts[port].setPD_INT(pD_INT);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public byte getPD_INT(int port) throws DWPortNotValidException {
/*  430 */     validateport(port);
/*  431 */     return this.vserialPorts[port].getPD_INT();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setPD_QUT(int port, byte pD_QUT) throws DWPortNotValidException {
/*  438 */     validateport(port);
/*  439 */     this.vserialPorts[port].setPD_QUT(pD_QUT);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public byte getPD_QUT(int port) throws DWPortNotValidException {
/*  446 */     validateport(port);
/*  447 */     return this.vserialPorts[port].getPD_QUT();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void sendUtilityFailResponse(int vport, byte code, String txt) throws DWPortNotValidException {
/*  457 */     validateport(vport);
/*  458 */     logger.info("API FAIL: port " + vport + " code " + code + ": " + txt);
/*  459 */     this.vserialPorts[vport].sendUtilityFailResponse(code, txt);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void sendUtilityOKResponse(int vport, String txt) throws DWPortNotValidException {
/*  465 */     validateport(vport);
/*  466 */     logger.debug("API OK: port " + vport + ": command successful");
/*  467 */     this.vserialPorts[vport].sendUtilityOKResponse("command successful");
/*  468 */     this.vserialPorts[vport].writeToCoco(txt);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void sendUtilityOKResponse(int vport, byte[] responseBytes) throws DWPortNotValidException {
/*  474 */     validateport(vport);
/*  475 */     logger.debug("API OK: port " + vport + ": command successful (byte mode)");
/*  476 */     this.vserialPorts[vport].sendUtilityOKResponse("command successful");
/*  477 */     this.vserialPorts[vport].writeToCoco(responseBytes);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public int bytesWaiting(int vport) throws DWPortNotValidException {
/*  483 */     validateport(vport);
/*  484 */     return this.vserialPorts[vport].bytesWaiting();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setDD(byte vport, byte[] devdescr) throws DWPortNotValidException {
/*  491 */     validateport(vport);
/*  492 */     this.vserialPorts[vport].setDD(devdescr);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void resetAllPorts() {
/*  498 */     logger.debug("Resetting all virtual serial ports - part 1, close all sockets");
/*      */     
/*      */     int i;
/*  501 */     for (i = 0; i < 15; i++) {
/*      */       
/*  503 */       this.listenerpool.closePortConnectionSockets(i);
/*  504 */       this.listenerpool.closePortServerSockets(i);
/*      */     } 
/*      */     
/*  507 */     logger.debug("Resetting all virtual serial ports - part 2, init all ports");
/*      */ 
/*      */     
/*  510 */     for (i = 0; i < 15; i++) {
/*      */ 
/*      */       
/*  513 */       if (i != 0) {
/*      */         
/*      */         try {
/*      */           
/*  517 */           resetPort(i);
/*      */         }
/*  519 */         catch (DWPortNotValidException e) {
/*      */           
/*  521 */           logger.warn(e.getMessage());
/*      */         } 
/*      */       }
/*      */     } 
/*      */ 
/*      */     
/*  527 */     if (this.vserialPorts[0] == null) {
/*      */       
/*      */       try {
/*      */         
/*  531 */         resetPort(0);
/*      */       }
/*  533 */       catch (DWPortNotValidException e) {
/*      */         
/*  535 */         logger.warn(e.getMessage());
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void resetPort(int i) throws DWPortNotValidException {
/*  543 */     if (i >= 0 && i < this.vserialPorts.length) {
/*      */       
/*  545 */       this.vserialPorts[i] = new DWVSerialPort(this.dwProto, i);
/*      */     }
/*      */     else {
/*      */       
/*  549 */       throw new DWPortNotValidException("Invalid port # " + i);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isOpen(int vport) {
/*  556 */     if (vport >= 0 && vport < this.vserialPorts.length && this.vserialPorts[vport] != null) {
/*  557 */       return this.vserialPorts[vport].isOpen();
/*      */     }
/*  559 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public int getOpen(int i) throws DWPortNotValidException {
/*  565 */     validateport(i);
/*  566 */     return this.vserialPorts[i].getOpen();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public byte[] getDD(int i) throws DWPortNotValidException {
/*  573 */     validateport(i);
/*  574 */     return this.vserialPorts[i].getDD();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeToCoco(int i, byte databyte) throws DWPortNotValidException {
/*  588 */     validateport(i);
/*  589 */     this.vserialPorts[i].writeToCoco(databyte);
/*      */   }
/*      */ 
/*      */   
/*      */   public void writeToCoco(int vport, String str) throws DWPortNotValidException {
/*  594 */     validateport(vport);
/*  595 */     this.vserialPorts[vport].writeToCoco(str);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean hasOutput(int vport) {
/*  603 */     if (vport >= 0 && vport < this.vserialPorts.length && this.vserialPorts[vport] != null)
/*      */     {
/*  605 */       return this.vserialPorts[vport].hasOutput();
/*      */     }
/*      */     
/*  608 */     return false;
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean isNull(int vport) {
/*  613 */     if (vport >= 0 && vport < this.vserialPorts.length && this.vserialPorts[vport] == null) {
/*  614 */       return true;
/*      */     }
/*  616 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isValid(int vport) {
/*  622 */     if (vport >= 0 && vport < 15) {
/*  623 */       return true;
/*      */     }
/*  625 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private void validateport(int vport) throws DWPortNotValidException {
/*  631 */     if (!isValid(vport) || isNull(vport))
/*      */     {
/*  633 */       throw new DWPortNotValidException("Invalid port #" + vport);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void sendConnectionAnnouncement(int vport, int conno, int localport, String hostaddr) throws DWPortNotValidException {
/*  640 */     validateport(vport);
/*  641 */     this.vserialPorts[vport].sendConnectionAnnouncement(conno, localport, hostaddr);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void setConn(int vport, int conno) throws DWPortNotValidException {
/*  647 */     validateport(vport);
/*  648 */     this.vserialPorts[vport].setConn(conno);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getConn(int vport) throws DWPortNotValidException {
/*  659 */     validateport(vport);
/*  660 */     return this.vserialPorts[vport].getConn();
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public String getHostIP(int vport) throws DWPortNotValidException, DWConnectionNotValidException {
/*  666 */     validateport(vport);
/*  667 */     return this.listenerpool.getConn(this.vserialPorts[vport].getConn()).getInetAddress().getHostAddress();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getHostPort(int vport) throws DWPortNotValidException, DWConnectionNotValidException {
/*  674 */     validateport(vport);
/*  675 */     return this.listenerpool.getConn(this.vserialPorts[vport].getConn()).getPort();
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void shutdown() {
/*  681 */     logger.debug("shutting down");
/*      */     
/*  683 */     for (int i = 0; i < 15; i++) {
/*      */       
/*  685 */       this.listenerpool.closePortConnectionSockets(i);
/*  686 */       this.listenerpool.closePortServerSockets(i);
/*  687 */       if (this.vserialPorts[i] != null)
/*      */       {
/*  689 */         this.vserialPorts[i].shutdown();
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public MidiDevice.Info getMidiDeviceInfo() {
/*  700 */     if (this.midiDevice != null) {
/*  701 */       return this.midiDevice.getDeviceInfo();
/*      */     }
/*  703 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void setMIDIDevice(MidiDevice device) throws MidiUnavailableException, IllegalArgumentException {
/*  709 */     if (this.midiDevice != null)
/*      */     {
/*  711 */       if (this.midiDevice.isOpen()) {
/*      */         
/*  713 */         logger.info("midi: closing " + this.midiDevice.getDeviceInfo().getName());
/*  714 */         this.midiDevice.close();
/*      */       } 
/*      */     }
/*      */     
/*  718 */     device.open();
/*      */     
/*  720 */     this.midiDevice = device;
/*      */     
/*  722 */     DriveWireServer.submitMIDIEvent(this.dwProto.getHandlerNo(), "device", this.midiDevice.getDeviceInfo().getName());
/*      */     
/*  724 */     logger.info("midi: opened " + this.midiDevice.getDeviceInfo().getName());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void sendMIDIMsg(ShortMessage mmsg, int timestamp) {
/*  732 */     if (this.midiDevice != null) {
/*      */       
/*      */       try
/*      */       {
/*  736 */         this.midiDevice.getReceiver().send(mmsg, timestamp);
/*      */       }
/*  738 */       catch (MidiUnavailableException e)
/*      */       {
/*  740 */         logger.warn(e.getMessage());
/*      */       }
/*  742 */       catch (IllegalStateException e)
/*      */       {
/*  744 */         logger.warn(e.getMessage());
/*      */       }
/*      */     
/*      */     } else {
/*      */       
/*  749 */       logger.warn("No MIDI device for MIDI msg");
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public Receiver getMidiReceiver() throws MidiUnavailableException {
/*  756 */     return this.midiDevice.getReceiver();
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public Synthesizer getMidiSynth() {
/*  762 */     return this.midiSynth;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isSoundbankSupported(Soundbank soundbank) {
/*  768 */     return this.midiSynth.isSoundbankSupported(soundbank);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean setMidiSoundbank(Soundbank soundbank, String fname) {
/*  775 */     if (this.midiSynth.loadAllInstruments(soundbank)) {
/*      */       
/*  777 */       logger.debug("loaded soundbank file '" + fname + "'");
/*  778 */       this.soundbankfilename = fname;
/*  779 */       return true;
/*      */     } 
/*      */     
/*  782 */     return false;
/*      */   }
/*      */ 
/*      */   
/*      */   public String getMidiSoundbankFilename() {
/*  787 */     return this.soundbankfilename;
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean getMidiVoicelock() {
/*  792 */     return this.midiVoicelock;
/*      */   }
/*      */ 
/*      */   
/*      */   public void setMidiVoicelock(boolean lock) {
/*  797 */     this.midiVoicelock = lock;
/*  798 */     DriveWireServer.submitMIDIEvent(this.dwProto.getHandlerNo(), "voicelock", String.valueOf(lock));
/*      */     
/*  800 */     logger.debug("MIDI: synth voicelock = " + lock);
/*      */   }
/*      */ 
/*      */   
/*      */   private void loadSoundbank(String filename) {
/*  805 */     Soundbank soundbank = null;
/*      */     
/*  807 */     File file = new File(filename);
/*      */     
/*      */     try {
/*  810 */       soundbank = MidiSystem.getSoundbank(file);
/*      */     }
/*  812 */     catch (InvalidMidiDataException e) {
/*      */       
/*  814 */       logger.warn("Error loading soundbank: " + e.getMessage());
/*      */       
/*      */       return;
/*  817 */     } catch (IOException e) {
/*      */       
/*  819 */       logger.warn("Error loading soundbank: " + e.getMessage());
/*      */       
/*      */       return;
/*      */     } 
/*  823 */     if (isSoundbankSupported(soundbank)) {
/*      */       
/*  825 */       if (!setMidiSoundbank(soundbank, filename)) {
/*      */         
/*  827 */         logger.warn("Failed to set soundbank '" + filename + "'");
/*      */         
/*      */         return;
/*      */       } 
/*  831 */       DriveWireServer.submitMIDIEvent(this.dwProto.getHandlerNo(), "soundbank", filename);
/*      */     
/*      */     }
/*      */     else {
/*      */ 
/*      */       
/*  837 */       logger.warn("Unsupported soundbank '" + filename + "'");
/*      */       return;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getMidiProfileName() {
/*  847 */     if (this.midiProfConf != null)
/*      */     {
/*  849 */       return this.midiProfConf.getString("[@name]", "none");
/*      */     }
/*      */ 
/*      */     
/*  853 */     return "none";
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public HierarchicalConfiguration getMidiProfile() {
/*  859 */     return this.midiProfConf;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean setMidiProfile(String profile) {
/*  868 */     List<HierarchicalConfiguration> profiles = DriveWireServer.serverconfig.configurationsAt("midisynthprofile");
/*      */     
/*  870 */     for (Iterator<HierarchicalConfiguration> it = profiles.iterator(); it.hasNext(); ) {
/*      */       
/*  872 */       HierarchicalConfiguration mprof = it.next();
/*      */       
/*  874 */       if (mprof.containsKey("[@name]") && mprof.getString("[@name]").equalsIgnoreCase(profile)) {
/*      */ 
/*      */         
/*  877 */         this.midiProfConf = (HierarchicalConfiguration)mprof.clone();
/*  878 */         doMidiTranslateCurrentVoices();
/*      */         
/*  880 */         DriveWireServer.submitMIDIEvent(this.dwProto.getHandlerNo(), "profile", profile);
/*      */         
/*  882 */         logger.debug("MIDI: set profile to '" + profile + "'");
/*  883 */         return true;
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/*  888 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void doMidiTranslateCurrentVoices() {
/*  897 */     MidiChannel[] chans = this.midiSynth.getChannels();
/*      */     
/*  899 */     for (int i = 0; i < chans.length; i++) {
/*      */       
/*  901 */       if (chans[i] != null)
/*      */       {
/*  903 */         chans[i].programChange(getGMInstrument(this.GMInstrumentCache[i]));
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getGMInstrument(int voice) {
/*  915 */     if (this.midiProfConf == null)
/*      */     {
/*  917 */       return voice;
/*      */     }
/*      */     
/*  920 */     int xvoice = voice;
/*      */     
/*  922 */     List<HierarchicalConfiguration> mappings = this.midiProfConf.configurationsAt("mapping");
/*      */     
/*  924 */     for (Iterator<HierarchicalConfiguration> it = mappings.iterator(); it.hasNext(); ) {
/*      */       
/*  926 */       HierarchicalConfiguration sub = it.next();
/*      */       
/*  928 */       if (sub.getInt("[@dev]") == voice) {
/*      */         
/*  930 */         xvoice = sub.getInt("[@gm]");
/*  931 */         logger.debug("MIDI: profile '" + this.midiProfConf.getString("[@name]") + "' translates device inst " + voice + " to GM instr " + xvoice);
/*  932 */         return xvoice;
/*      */       } 
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/*  938 */     return voice;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean setMIDIInstr(int channel, int instr) {
/*  944 */     MidiChannel[] chans = this.midiSynth.getChannels();
/*      */     
/*  946 */     if (channel < chans.length)
/*      */     {
/*  948 */       if (chans[channel] != null) {
/*      */         
/*  950 */         chans[channel].programChange(instr);
/*  951 */         logger.debug("MIDI: set instrument " + instr + " on channel " + channel);
/*  952 */         return true;
/*      */       } 
/*      */     }
/*      */     
/*  956 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void clearGMInstrumentCache() {
/*  963 */     this.GMInstrumentCache = new int[16];
/*      */     
/*  965 */     for (int i = 0; i < 16; i++)
/*      */     {
/*  967 */       this.GMInstrumentCache[i] = 0;
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void setGMInstrumentCache(int chan, int instr) {
/*  974 */     if (chan >= 0 && chan < this.GMInstrumentCache.length) {
/*      */       
/*  976 */       this.GMInstrumentCache[chan] = instr;
/*      */     }
/*      */     else {
/*      */       
/*  980 */       logger.debug("MIDI: channel out of range on program change: " + chan);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public int getGMInstrumentCache(int chan) {
/*  986 */     return this.GMInstrumentCache[chan];
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public DWVPortListenerPool getListenerPool() {
/*  997 */     return this.listenerpool;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getUtilMode(int i) throws DWPortNotValidException {
/* 1010 */     validateport(i);
/* 1011 */     return this.vserialPorts[i].getUtilMode();
/*      */   }
/*      */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/virtualserial/DWVSerialPorts.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */