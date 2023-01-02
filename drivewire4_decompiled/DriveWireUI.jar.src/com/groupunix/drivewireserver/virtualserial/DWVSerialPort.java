/*     */ package com.groupunix.drivewireserver.virtualserial;
/*     */ 
/*     */ import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocolHandler;
/*     */ import com.groupunix.drivewireserver.dwprotocolhandler.DWUtils;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.net.Socket;
/*     */ import javax.sound.midi.InvalidMidiDataException;
/*     */ import javax.sound.midi.ShortMessage;
/*     */ import org.apache.log4j.Logger;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DWVSerialPort
/*     */ {
/*  18 */   private static final Logger logger = Logger.getLogger("DWServer.DWVSerialPort");
/*     */   
/*     */   private static final int BUFFER_SIZE = -1;
/*     */   
/*  22 */   private int port = -1;
/*     */   
/*     */   private DWProtocolHandler dwProto;
/*     */   private boolean connected = false;
/*  26 */   private int opens = 0;
/*     */   
/*  28 */   private DWVPortHandler porthandler = null;
/*     */ 
/*     */   
/*  31 */   private byte PD_INT = 0;
/*  32 */   private byte PD_QUT = 0;
/*  33 */   private byte[] DD = new byte[26];
/*     */   
/*  35 */   private DWVSerialCircularBuffer inputBuffer = new DWVSerialCircularBuffer(-1, true);
/*     */   
/*     */   private OutputStream output;
/*  38 */   private String hostIP = null;
/*  39 */   private int hostPort = -1;
/*     */   
/*  41 */   private int userGroup = -1;
/*  42 */   private String userName = "unknown";
/*     */   
/*     */   private boolean wanttodie = false;
/*  45 */   private Socket socket = null;
/*     */   
/*  47 */   private int conno = -1;
/*     */ 
/*     */   
/*     */   private ShortMessage mmsg;
/*     */   
/*  52 */   private int mmsg_pos = 0;
/*     */   
/*     */   private int mmsg_data1;
/*     */   private int mmsg_status;
/*     */   private int last_mmsg_status;
/*  57 */   private int mmsg_databytes = 2;
/*     */   
/*     */   private boolean midi_seen = false;
/*     */   private boolean log_midi_bytes = false;
/*     */   private boolean midi_in_sysex = false;
/*  62 */   private String midi_sysex = new String();
/*     */   
/*  64 */   private int utilmode = 0;
/*     */ 
/*     */   
/*     */   public DWVSerialPort(DWProtocolHandler dwProto, int port) {
/*  68 */     logger.debug("New DWVSerialPort for port " + port + " in handler '" + dwProto.getName() + "'");
/*  69 */     this.port = port;
/*  70 */     this.dwProto = dwProto;
/*     */     
/*  72 */     if (port != 0)
/*     */     {
/*  74 */       this.porthandler = new DWVPortHandler(dwProto, port);
/*     */     }
/*     */     
/*  77 */     if (dwProto.getConfig().getBoolean("LogMIDIBytes", false))
/*     */     {
/*  79 */       this.log_midi_bytes = true;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int bytesWaiting() {
/*  88 */     int bytes = this.inputBuffer.getAvailable();
/*     */     
/*  90 */     if (bytes < 2)
/*     */     {
/*     */       
/*  93 */       return bytes;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*  98 */     if (bytes < 256) {
/*  99 */       return bytes;
/*     */     }
/* 101 */     return 255;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void write(int databyte) {
/* 109 */     if (this.port == 14) {
/*     */       
/* 111 */       if (!this.midi_seen) {
/*     */         
/* 113 */         logger.debug("MIDI data on port " + this.port);
/* 114 */         this.midi_seen = true;
/*     */       } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 121 */       databyte &= 0xFF;
/*     */       
/* 123 */       if (this.midi_in_sysex)
/*     */       {
/* 125 */         if (databyte == 247)
/*     */         {
/* 127 */           this.midi_in_sysex = false;
/*     */           
/* 129 */           if (this.log_midi_bytes)
/*     */           {
/* 131 */             logger.info("midi sysex: " + this.midi_sysex);
/*     */           }
/*     */           
/* 134 */           this.midi_sysex = "";
/*     */         }
/*     */         else
/*     */         {
/* 138 */           this.midi_sysex += " " + databyte;
/*     */         
/*     */         }
/*     */       
/*     */       }
/* 143 */       else if (databyte == 240)
/*     */       {
/* 145 */         this.midi_in_sysex = true;
/*     */       }
/* 147 */       else if (databyte == 248)
/*     */       {
/* 149 */         sendMIDI(databyte);
/*     */       }
/* 151 */       else if (databyte >= 192 && databyte < 224)
/*     */       {
/* 153 */         this.mmsg_databytes = 1;
/* 154 */         this.last_mmsg_status = this.mmsg_status;
/* 155 */         this.mmsg_status = databyte;
/* 156 */         this.mmsg_pos = 0;
/*     */       }
/* 158 */       else if (databyte > 127 && databyte < 240)
/*     */       {
/* 160 */         this.mmsg_databytes = 2;
/* 161 */         this.last_mmsg_status = this.mmsg_status;
/* 162 */         this.mmsg_status = databyte;
/* 163 */         this.mmsg_pos = 0;
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*     */       }
/* 169 */       else if (this.mmsg_pos == 0)
/*     */       {
/*     */ 
/*     */         
/* 173 */         if (this.mmsg_databytes == 2)
/*     */         {
/*     */           
/* 176 */           this.mmsg_data1 = databyte;
/* 177 */           this.mmsg_pos = 1;
/*     */         
/*     */         }
/*     */         else
/*     */         {
/*     */           
/* 183 */           if (this.mmsg_status >= 192 && databyte < 208) {
/*     */             
/* 185 */             if (this.dwProto.getVPorts().getMidiVoicelock())
/*     */             {
/*     */               
/* 188 */               logger.debug("MIDI: ignored program change due to instrument lock.");
/*     */             
/*     */             }
/*     */             else
/*     */             {
/* 193 */               int xinstr = this.dwProto.getVPorts().getGMInstrument(databyte);
/* 194 */               sendMIDI(this.mmsg_status, xinstr, 0);
/*     */ 
/*     */ 
/*     */ 
/*     */               
/* 199 */               this.dwProto.getVPorts().setGMInstrumentCache(this.mmsg_status - 192, databyte);
/*     */             }
/*     */           
/*     */           } else {
/*     */             
/* 204 */             sendMIDI(this.mmsg_status, databyte, 0);
/*     */           } 
/* 206 */           this.mmsg_pos = 0;
/*     */         }
/*     */       
/*     */       }
/*     */       else
/*     */       {
/* 212 */         sendMIDI(this.mmsg_status, this.mmsg_data1, databyte);
/* 213 */         this.mmsg_pos = 0;
/*     */ 
/*     */       
/*     */       }
/*     */ 
/*     */ 
/*     */     
/*     */     }
/* 221 */     else if (this.connected || this.port == 0) {
/*     */       
/* 223 */       if (this.output != null) {
/*     */         try
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 231 */           this.output.write((byte)databyte);
/*     */         
/*     */         }
/* 234 */         catch (IOException e)
/*     */         {
/* 236 */           logger.error("in write: " + e.getMessage());
/*     */         }
/*     */       
/*     */       }
/*     */     }
/*     */     else {
/*     */       
/* 243 */       this.porthandler.takeInput(databyte);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void sendMIDI(int statusbyte) {
/* 251 */     ShortMessage mmsg = new ShortMessage();
/*     */ 
/*     */ 
/*     */     
/*     */     try {
/* 256 */       mmsg.setMessage(statusbyte);
/* 257 */       this.dwProto.getVPorts().sendMIDIMsg(mmsg, -1);
/*     */     }
/* 259 */     catch (InvalidMidiDataException e) {
/*     */       
/* 261 */       logger.warn("MIDI: " + e.getMessage());
/*     */     } 
/*     */     
/* 264 */     if (this.log_midi_bytes) {
/*     */       
/* 266 */       byte[] tmpb = { (byte)statusbyte };
/* 267 */       logger.info("midimsg: " + DWUtils.byteArrayToHexString(tmpb));
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void sendMIDI(int statusbyte, int data1, int data2) {
/* 275 */     ShortMessage mmsg = new ShortMessage();
/*     */ 
/*     */ 
/*     */     
/*     */     try {
/* 280 */       mmsg.setMessage(statusbyte, data1, data2);
/* 281 */       this.dwProto.getVPorts().sendMIDIMsg(mmsg, -1);
/*     */     }
/* 283 */     catch (InvalidMidiDataException e) {
/*     */       
/* 285 */       logger.warn("MIDI: " + e.getMessage());
/*     */     } 
/*     */     
/* 288 */     if (this.log_midi_bytes)
/*     */     {
/*     */       
/* 291 */       logger.info("midimsg: " + DWUtils.midimsgToText(statusbyte, data1, data2));
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void writeM(String str) {
/* 300 */     for (int i = 0; i < str.length(); i++)
/*     */     {
/* 302 */       write(str.charAt(i));
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void writeToCoco(String str) {
/*     */     try {
/* 311 */       this.inputBuffer.getOutputStream().write(str.getBytes());
/*     */     }
/* 313 */     catch (IOException e) {
/*     */       
/* 315 */       logger.warn(e.getMessage());
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void writeToCoco(byte[] databytes) {
/*     */     try {
/* 323 */       this.inputBuffer.getOutputStream().write(databytes);
/* 324 */     } catch (IOException e) {
/* 325 */       logger.warn(e.getMessage());
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void writeToCoco(byte databyte) {
/*     */     try {
/* 334 */       this.inputBuffer.getOutputStream().write(databyte);
/* 335 */     } catch (IOException e) {
/* 336 */       logger.warn(e.getMessage());
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public OutputStream getPortInput() {
/* 344 */     return this.inputBuffer.getOutputStream();
/*     */   }
/*     */ 
/*     */   
/*     */   public InputStream getPortOutput() {
/* 349 */     return this.inputBuffer.getInputStream();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public byte read1() {
/*     */     try {
/* 361 */       int databyte = this.inputBuffer.getInputStream().read();
/* 362 */       return (byte)databyte;
/*     */     }
/* 364 */     catch (IOException e) {
/*     */       
/* 366 */       logger.error("in read1: " + e.getMessage());
/*     */ 
/*     */       
/* 369 */       return -1;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public byte[] readM(int tmplen) {
/* 376 */     byte[] buf = new byte[tmplen];
/*     */     
/*     */     try {
/* 379 */       this.inputBuffer.getInputStream().read(buf, 0, tmplen);
/* 380 */       return buf;
/*     */     
/*     */     }
/* 383 */     catch (IOException e) {
/*     */       
/* 385 */       e.printStackTrace();
/* 386 */       logger.error("Failed to read " + tmplen + " bytes in SERREADM... not good");
/*     */       
/* 388 */       return null;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void setConnected(boolean connected) {
/* 394 */     this.connected = connected;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isConnected() {
/* 401 */     return this.connected;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isOpen() {
/* 407 */     if (this.opens > 0)
/*     */     {
/* 409 */       return true;
/*     */     }
/*     */ 
/*     */     
/* 413 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void open() {
/* 420 */     this.opens++;
/* 421 */     logger.debug("open port " + this.port + ", total opens: " + this.opens);
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() {
/* 426 */     if (this.opens > 0) {
/*     */       
/* 428 */       this.opens--;
/* 429 */       logger.debug("close port " + this.port + ", total opens: " + this.opens + " data in buffer: " + this.inputBuffer.getAvailable());
/*     */ 
/*     */       
/* 432 */       if (this.opens == 0) {
/*     */         
/* 434 */         logger.debug("setting term on port " + this.port);
/* 435 */         this.wanttodie = true;
/*     */         
/* 437 */         if (this.output != null) {
/*     */           
/* 439 */           logger.debug("closing output on port " + this.port);
/*     */           try {
/* 441 */             this.output.close();
/* 442 */           } catch (IOException e) {
/* 443 */             logger.warn(e.getMessage());
/*     */           } 
/*     */         } 
/*     */ 
/*     */ 
/*     */         
/* 449 */         if (this.socket != null) {
/*     */           
/* 451 */           logger.debug("closing socket on port " + this.port);
/*     */           
/*     */           try {
/* 454 */             this.socket.close();
/* 455 */           } catch (IOException e) {
/* 456 */             logger.warn(e.getMessage());
/*     */           } 
/*     */           
/* 459 */           this.socket = null;
/*     */         } 
/*     */ 
/*     */ 
/*     */         
/* 464 */         this.dwProto.getVPorts().getListenerPool().closePortServerSockets(this.port);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isTerm() {
/* 480 */     return this.wanttodie;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void setPortOutput(OutputStream output) {
/* 486 */     this.output = output;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getHostIP() {
/* 493 */     return this.hostIP;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setHostIP(String ip) {
/* 498 */     this.hostIP = ip;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getHostPort() {
/* 503 */     return this.hostPort;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setHostPort(int port) {
/* 508 */     this.hostPort = port;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void setUtilMode(int mode) {
/* 514 */     this.utilmode = mode;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getUtilMode() {
/* 519 */     return this.utilmode;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void setPD_INT(byte pD_INT) {
/* 525 */     this.PD_INT = pD_INT;
/* 526 */     this.inputBuffer.setDW_PD_INT(this.PD_INT);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public byte getPD_INT() {
/* 532 */     return this.PD_INT;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setPD_QUT(byte pD_QUT) {
/* 539 */     this.PD_QUT = pD_QUT;
/* 540 */     this.inputBuffer.setDW_PD_QUT(this.PD_QUT);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public byte getPD_QUT() {
/* 547 */     return this.PD_QUT;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void sendUtilityFailResponse(byte errno, String txt) {
/* 555 */     String perrno = String.format("%03d", new Object[] { Integer.valueOf(errno & 0xFF) });
/* 556 */     logger.debug("command failed: " + perrno + " " + txt);
/*     */     
/*     */     try {
/* 559 */       this.inputBuffer.getOutputStream().write(("FAIL " + perrno + " " + txt + '\n' + '\r').getBytes());
/*     */     }
/* 561 */     catch (IOException e) {
/*     */       
/* 563 */       logger.warn(e.getMessage());
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void sendUtilityOKResponse(String txt) {
/*     */     try {
/* 572 */       this.inputBuffer.getOutputStream().write(("OK " + txt + '\n' + '\r').getBytes());
/*     */     }
/* 574 */     catch (IOException e) {
/*     */       
/* 576 */       logger.warn(e.getMessage());
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setDD(byte[] devdescr) {
/* 585 */     this.DD = devdescr;
/*     */   }
/*     */ 
/*     */   
/*     */   public byte[] getDD() {
/* 590 */     return this.DD;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getOpen() {
/* 595 */     return this.opens;
/*     */   }
/*     */   
/*     */   public void setUserGroup(int userGroup) {
/* 599 */     this.userGroup = userGroup;
/*     */   }
/*     */   
/*     */   public int getUserGroup() {
/* 603 */     return this.userGroup;
/*     */   }
/*     */   
/*     */   public void setUserName(String userName) {
/* 607 */     this.userName = userName;
/*     */   }
/*     */   
/*     */   public String getUserName() {
/* 611 */     return this.userName;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void setSocket(Socket skt) {
/* 617 */     this.socket = skt;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean hasOutput() {
/* 622 */     if (this.output == null)
/*     */     {
/* 624 */       return false;
/*     */     }
/*     */ 
/*     */     
/* 628 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void sendConnectionAnnouncement(int conno, int localport, String hostaddr) {
/* 635 */     this.porthandler.announceConnection(conno, localport, hostaddr);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void setConn(int conno) {
/* 641 */     this.conno = conno;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getConn() {
/* 646 */     return this.conno;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void shutdown() {
/* 652 */     this.connected = false;
/* 653 */     this.opens = 0;
/* 654 */     this.output = null;
/* 655 */     this.porthandler = null;
/* 656 */     this.wanttodie = true;
/*     */   }
/*     */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/virtualserial/DWVSerialPort.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */