/*     */ package com.groupunix.drivewireserver.dwprotocolhandler;
/*     */ 
/*     */ import com.groupunix.drivewireserver.dwexceptions.DWCommTimeOutException;
/*     */ import gnu.io.CommPort;
/*     */ import gnu.io.CommPortIdentifier;
/*     */ import gnu.io.NoSuchPortException;
/*     */ import gnu.io.PortInUseException;
/*     */ import gnu.io.SerialPort;
/*     */ import gnu.io.UnsupportedCommOperationException;
/*     */ import java.io.IOException;
/*     */ import java.util.TooManyListenersException;
/*     */ import java.util.concurrent.ArrayBlockingQueue;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import org.apache.log4j.Logger;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DWSerialDevice
/*     */   implements DWProtocolDevice
/*     */ {
/*  25 */   private static final Logger logger = Logger.getLogger("DWServer.DWSerialDevice");
/*     */   
/*  27 */   private SerialPort serialPort = null;
/*     */   
/*     */   private boolean bytelog = false;
/*     */   
/*     */   private String device;
/*     */   
/*     */   private DWProtocol dwProto;
/*     */   
/*     */   private boolean DATurboMode = false;
/*     */   
/*     */   private byte[] prefix;
/*     */   private long readtime;
/*     */   private ArrayBlockingQueue<Byte> queue;
/*     */   private DWSerialReader evtlistener;
/*     */   
/*     */   public DWSerialDevice(DWProtocol dwProto) throws NoSuchPortException, PortInUseException, UnsupportedCommOperationException, IOException, TooManyListenersException {
/*  43 */     this.dwProto = dwProto;
/*     */     
/*  45 */     this.device = dwProto.getConfig().getString("SerialDevice");
/*     */     
/*  47 */     this.prefix = new byte[1];
/*     */     
/*  49 */     this.prefix[0] = -64;
/*     */ 
/*     */ 
/*     */     
/*  53 */     this.bytelog = dwProto.getConfig().getBoolean("LogDeviceBytes", false);
/*     */     
/*  55 */     logger.debug("init " + this.device + " for handler #" + dwProto.getHandlerNo() + " (logging bytes: " + this.bytelog + ")");
/*     */     
/*  57 */     connect(this.device);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean connected() {
/*  64 */     if (this.serialPort != null)
/*     */     {
/*  66 */       return true;
/*     */     }
/*     */ 
/*     */     
/*  70 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void close() {
/*  78 */     logger.debug("closing serial device " + this.device + " in handler #" + this.dwProto.getHandlerNo());
/*     */     
/*  80 */     if (this.evtlistener != null) {
/*     */       
/*  82 */       if (this.serialPort != null)
/*  83 */         this.serialPort.removeEventListener(); 
/*  84 */       this.evtlistener.shutdown();
/*     */     } 
/*     */     
/*  87 */     if (this.serialPort != null) {
/*     */ 
/*     */       
/*     */       try {
/*  91 */         this.serialPort.getInputStream().close();
/*  92 */       } catch (IOException e) {
/*     */ 
/*     */         
/*  95 */         e.printStackTrace();
/*     */       } 
/*     */       
/*  98 */       this.serialPort.close();
/*  99 */       this.serialPort = null;
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
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void shutdown() {
/* 162 */     close();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void reconnect() throws UnsupportedCommOperationException, IOException, TooManyListenersException {
/* 169 */     if (this.serialPort != null) {
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 174 */       this.serialPort.setFlowControlMode(0);
/* 175 */       this.serialPort.enableReceiveThreshold(1);
/*     */ 
/*     */ 
/*     */       
/* 179 */       setSerialParams(this.serialPort);
/*     */       
/* 181 */       if (this.evtlistener != null)
/*     */       {
/* 183 */         this.serialPort.removeEventListener();
/*     */       }
/*     */       
/* 186 */       this.queue = new ArrayBlockingQueue<Byte>(512);
/*     */       
/* 188 */       this.evtlistener = new DWSerialReader(this.serialPort.getInputStream(), this.queue);
/*     */       
/* 190 */       this.serialPort.addEventListener(this.evtlistener);
/* 191 */       this.serialPort.notifyOnDataAvailable(true);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private void connect(String portName) throws NoSuchPortException, PortInUseException, UnsupportedCommOperationException, IOException, TooManyListenersException {
/* 197 */     logger.debug("attempting to open device '" + portName + "'");
/*     */ 
/*     */     
/* 200 */     CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 209 */     CommPort commPort = portIdentifier.open("DriveWire", 2000);
/*     */     
/* 211 */     if (commPort instanceof SerialPort) {
/*     */ 
/*     */       
/* 214 */       this.serialPort = (SerialPort)commPort;
/*     */       
/* 216 */       reconnect();
/*     */       
/* 218 */       logger.info("opened serial device " + portName);
/*     */     }
/*     */     else {
/*     */       
/* 222 */       logger.error("The operating system says '" + portName + "' is not a serial port!");
/* 223 */       throw new NoSuchPortException();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void setSerialParams(SerialPort sport) throws UnsupportedCommOperationException {
/* 234 */     int parity = 0;
/* 235 */     int stopbits = 1;
/* 236 */     int databits = 8;
/*     */     
/* 238 */     int rate = this.dwProto.getConfig().getInt("SerialRate", 115200);
/*     */ 
/*     */     
/* 241 */     if (this.dwProto.getConfig().containsKey("SerialStopbits")) {
/*     */       
/* 243 */       String sb = this.dwProto.getConfig().getString("SerialStopbits");
/*     */       
/* 245 */       if (sb.equals("1")) {
/* 246 */         stopbits = 1;
/* 247 */       } else if (sb.equals("1.5")) {
/* 248 */         stopbits = 3;
/* 249 */       } else if (sb.equals("2")) {
/* 250 */         stopbits = 2;
/*     */       } 
/*     */     } 
/*     */     
/* 254 */     if (this.dwProto.getConfig().containsKey("SerialParity")) {
/*     */       
/* 256 */       String p = this.dwProto.getConfig().getString("SerialParity");
/*     */       
/* 258 */       if (p.equals("none")) {
/* 259 */         parity = 0;
/* 260 */       } else if (p.equals("even")) {
/* 261 */         parity = 2;
/* 262 */       } else if (p.equals("odd")) {
/* 263 */         parity = 1;
/* 264 */       } else if (p.equals("mark")) {
/* 265 */         parity = 3;
/* 266 */       } else if (p.equals("space")) {
/* 267 */         parity = 4;
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 272 */     logger.debug("setting port params to " + rate + " " + databits + ":" + parity + ":" + stopbits);
/* 273 */     sport.setSerialPortParams(rate, databits, stopbits, parity);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getRate() {
/* 280 */     return this.serialPort.getBaudRate();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void comWrite(byte[] data, int len, boolean pfix) {
/*     */     try {
/* 290 */       if (this.dwProto.getConfig().getBoolean("ProtocolFlipOutputBits", false) || this.DATurboMode) {
/* 291 */         data = DWUtils.reverseByteArray(data);
/*     */       }
/*     */       
/* 294 */       if (this.dwProto.getConfig().containsKey("WriteByteDelay"))
/*     */       {
/* 296 */         for (int i = 0; i < len; i++)
/*     */         {
/* 298 */           comWrite1(data[i], pfix);
/*     */         }
/*     */       }
/*     */       else
/*     */       {
/* 303 */         if (pfix && (this.dwProto.getConfig().getBoolean("ProtocolResponsePrefix", false) || this.DATurboMode)) {
/*     */           
/* 305 */           byte[] out = new byte[this.prefix.length + len];
/* 306 */           System.arraycopy(this.prefix, 0, out, 0, this.prefix.length);
/* 307 */           System.arraycopy(data, 0, out, this.prefix.length, len);
/*     */           
/* 309 */           this.serialPort.getOutputStream().write(out);
/*     */         }
/*     */         else {
/*     */           
/* 313 */           this.serialPort.getOutputStream().write(data, 0, len);
/*     */         } 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 319 */         if (this.bytelog)
/*     */         {
/* 321 */           String tmps = new String();
/*     */           
/* 323 */           for (int i = 0; i < data.length; i++)
/*     */           {
/* 325 */             tmps = tmps + " " + (data[i] & 0xFF);
/*     */           }
/*     */           
/* 328 */           logger.debug("WRITE " + data.length + ":" + tmps);
/*     */         }
/*     */       
/*     */       }
/*     */     
/* 333 */     } catch (IOException e) {
/*     */ 
/*     */       
/* 336 */       logger.error(e.getMessage());
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
/*     */   public void comWrite1(int data, boolean pfix) {
/*     */     try {
/* 350 */       if (this.dwProto.getConfig().getBoolean("ProtocolFlipOutputBits", false) || this.DATurboMode) {
/* 351 */         data = DWUtils.reverseByte(data);
/*     */       }
/* 353 */       if (this.dwProto.getConfig().containsKey("WriteByteDelay")) {
/*     */         
/*     */         try {
/*     */           
/* 357 */           Thread.sleep(this.dwProto.getConfig().getLong("WriteByteDelay"));
/*     */         }
/* 359 */         catch (InterruptedException e) {
/*     */           
/* 361 */           logger.warn("interrupted during writebytedelay");
/*     */         } 
/*     */       }
/*     */       
/* 365 */       if (pfix && (this.dwProto.getConfig().getBoolean("ProtocolResponsePrefix", false) || this.DATurboMode)) {
/*     */         
/* 367 */         byte[] out = new byte[this.prefix.length + 1];
/* 368 */         out[out.length - 1] = (byte)data;
/* 369 */         System.arraycopy(this.prefix, 0, out, 0, this.prefix.length);
/*     */         
/* 371 */         this.serialPort.getOutputStream().write(out);
/*     */       }
/*     */       else {
/*     */         
/* 375 */         this.serialPort.getOutputStream().write((byte)data);
/*     */       } 
/*     */       
/* 378 */       if (this.bytelog) {
/* 379 */         logger.debug("WRITE1: " + (0xFF & data));
/*     */       }
/*     */     }
/* 382 */     catch (IOException e) {
/*     */ 
/*     */       
/* 385 */       logger.error(e.getMessage());
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public byte[] comRead(int len) throws IOException, DWCommTimeOutException {
/* 395 */     byte[] buf = new byte[len];
/*     */ 
/*     */     
/* 398 */     for (int i = 0; i < len; i++)
/*     */     {
/* 400 */       buf[i] = (byte)comRead1(true, false);
/*     */     }
/*     */     
/* 403 */     if (this.bytelog) {
/*     */       
/* 405 */       String tmps = new String();
/*     */       
/* 407 */       for (int j = 0; j < buf.length; j++)
/*     */       {
/* 409 */         tmps = tmps + " " + (buf[j] & 0xFF);
/*     */       }
/*     */       
/* 412 */       logger.debug("READ " + len + ": " + tmps);
/*     */     } 
/*     */ 
/*     */     
/* 416 */     return buf;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int comRead1(boolean timeout) throws IOException, DWCommTimeOutException {
/* 422 */     return comRead1(timeout, true);
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
/*     */   public int comRead1(boolean timeout, boolean blog) throws IOException, DWCommTimeOutException {
/* 457 */     int res = -1;
/*     */ 
/*     */     
/*     */     try {
/* 461 */       while (res == -1)
/*     */       {
/* 463 */         long starttime = System.currentTimeMillis();
/* 464 */         Byte read = this.queue.poll(200L, TimeUnit.MILLISECONDS);
/* 465 */         this.readtime += System.currentTimeMillis() - starttime;
/*     */         
/* 467 */         if (read != null) {
/* 468 */           res = 0xFF & read.byteValue(); continue;
/* 469 */         }  if (timeout)
/*     */         {
/* 471 */           throw new DWCommTimeOutException("No data in 200 ms");
/*     */         }
/*     */       }
/*     */     
/*     */     }
/* 476 */     catch (InterruptedException e) {
/*     */       
/* 478 */       logger.debug("interrupted in serial read");
/*     */     } 
/*     */     
/* 481 */     if (blog && this.bytelog) {
/* 482 */       logger.debug("READ1: " + res);
/*     */     }
/* 484 */     return res;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getDeviceName() {
/* 491 */     return this.serialPort.getName();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getDeviceType() {
/* 498 */     return "serial";
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void enableDATurbo() throws UnsupportedCommOperationException {
/* 504 */     this.serialPort.setSerialPortParams(230400, 8, 2, 0);
/* 505 */     this.DATurboMode = true;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getReadtime() {
/* 510 */     return this.readtime;
/*     */   }
/*     */ 
/*     */   
/*     */   public void resetReadtime() {
/* 515 */     this.readtime = 0L;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public SerialPort getSerialPort() {
/* 521 */     return this.serialPort;
/*     */   }
/*     */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/dwprotocolhandler/DWSerialDevice.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */