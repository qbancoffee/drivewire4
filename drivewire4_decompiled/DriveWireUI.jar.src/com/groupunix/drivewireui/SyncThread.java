/*     */ package com.groupunix.drivewireui;
/*     */ 
/*     */ import java.io.BufferedReader;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.OutputStream;
/*     */ import java.net.Socket;
/*     */ import java.util.HashMap;
/*     */ 
/*     */ public class SyncThread
/*     */   implements Runnable
/*     */ {
/*     */   private static final int READ_BUFFER_SIZE = 2048;
/*  14 */   private static final String LINE_END = Character.toString('\r');
/*  15 */   private String host = new String();
/*  16 */   private int port = -1;
/*  17 */   private Socket sock = null;
/*     */   
/*     */   private boolean wanttodie = false;
/*     */   private OutputStream out;
/*     */   private BufferedReader in;
/*  22 */   private HashMap<String, String> params = new HashMap<String, String>();
/*  23 */   private StringBuilder buffer = new StringBuilder(4096);
/*  24 */   private LogItem logbuf = new LogItem();
/*  25 */   private ServerStatusItem ssbuf = new ServerStatusItem();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void run() {
/*  35 */     Thread.currentThread().setName("dwuiSync-" + Thread.currentThread().getId());
/*     */     
/*  37 */     char[] cbuf = new char[2048];
/*     */ 
/*     */     
/*  40 */     while (!MainWin.isReady()) {
/*     */ 
/*     */       
/*     */       try {
/*     */ 
/*     */         
/*  46 */         Thread.sleep(200L);
/*     */       }
/*  48 */       catch (InterruptedException e) {
/*     */         
/*  50 */         this.wanttodie = true;
/*     */       } 
/*     */     } 
/*     */     
/*  54 */     while (!this.wanttodie) {
/*     */ 
/*     */       
/*  57 */       if ((!this.wanttodie && MainWin.getHost() != null && !this.host.equals(MainWin.getHost())) || this.port != MainWin.getPort() || this.sock == null) {
/*     */ 
/*     */ 
/*     */         
/*  61 */         if (this.sock != null) {
/*     */           
/*     */           try {
/*     */ 
/*     */             
/*  66 */             this.sock.close();
/*     */           }
/*  68 */           catch (IOException e) {}
/*     */         }
/*     */ 
/*     */ 
/*     */ 
/*     */         
/*  74 */         this.host = MainWin.getHost();
/*  75 */         this.port = MainWin.getPort();
/*     */ 
/*     */         
/*     */         try {
/*  79 */           MainWin.setConStatusTrying();
/*     */           
/*  81 */           MainWin.debug("Sync: Connecting...");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/*  87 */           MainWin.errorHelpCache.load();
/*     */ 
/*     */           
/*  90 */           MainWin.setServerConfig(UIUtils.getServerConfig());
/*  91 */           MainWin.applyConfig();
/*     */ 
/*     */ 
/*     */           
/*  95 */           MainWin.setDisks(UIUtils.getServerDisks());
/*  96 */           MainWin.applyDisks();
/*     */ 
/*     */           
/*  99 */           MainWin.setMidiStatus(UIUtils.getServerMidiStatus());
/* 100 */           MainWin.applyMIDIStatus();
/*     */ 
/*     */           
/* 103 */           this.sock = new Socket(this.host, this.port);
/*     */           
/* 105 */           this.out = this.sock.getOutputStream();
/* 106 */           this.in = new BufferedReader(new InputStreamReader(this.sock.getInputStream()));
/*     */           
/* 108 */           MainWin.setConStatusConnect();
/*     */           
/* 110 */           MainWin.debug("Sync: Connected.");
/*     */ 
/*     */ 
/*     */           
/* 114 */           this.out.write((MainWin.getInstance() + "").getBytes());
/* 115 */           this.out.write(0);
/* 116 */           this.out.write("ui sync\n".getBytes());
/*     */         }
/* 118 */         catch (Exception e) {
/*     */ 
/*     */ 
/*     */           
/* 122 */           MainWin.setConStatusError();
/*     */ 
/*     */ 
/*     */           
/* 126 */           this.sock = null;
/*     */           try {
/* 128 */             Thread.sleep(5000L);
/* 129 */           } catch (InterruptedException e1) {}
/*     */         } 
/*     */       } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 138 */       if (!this.wanttodie && this.sock != null && !this.sock.isInputShutdown()) {
/*     */         
/* 140 */         MainWin.setConStatusConnect();
/*     */ 
/*     */         
/*     */         try {
/* 144 */           int thisread = this.in.read(cbuf, 0, 2048);
/*     */           
/* 146 */           if (thisread < 0) {
/*     */ 
/*     */             
/*     */             try {
/* 150 */               this.sock.close();
/*     */             }
/* 152 */             catch (IOException e1) {}
/*     */ 
/*     */ 
/*     */ 
/*     */             
/* 157 */             this.sock = null;
/*     */             
/*     */             continue;
/*     */           } 
/* 161 */           this.buffer.append(cbuf, 0, thisread);
/* 162 */           eatData(this.buffer);
/*     */ 
/*     */ 
/*     */ 
/*     */         
/*     */         }
/* 168 */         catch (IOException e) {
/*     */ 
/*     */ 
/*     */           
/* 172 */           if (this.sock != null) {
/*     */             
/*     */             try {
/* 175 */               this.sock.close();
/*     */             }
/* 177 */             catch (IOException e1) {}
/*     */           }
/*     */ 
/*     */ 
/*     */           
/* 182 */           this.sock = null;
/*     */         } 
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void eatData(StringBuilder buf) {
/* 194 */     int le = buf.indexOf(LINE_END);
/*     */     
/* 196 */     while (le > -1) {
/*     */       
/* 198 */       if (le > 0) {
/* 199 */         processLine(buf.substring(0, le));
/*     */       }
/* 201 */       buf.delete(0, le + 1);
/* 202 */       le = buf.indexOf(LINE_END);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void processLine(String line) {
/* 211 */     if (line.equals("D")) {
/*     */ 
/*     */ 
/*     */       
/* 215 */       if (this.params.containsKey("d") && this.params.get("d") != null) {
/*     */         
/*     */         try {
/*     */           
/* 219 */           MainWin.submitDiskEvent(Integer.parseInt(this.params.get("d")), this.params.get("k"), this.params.get("v"));
/*     */         }
/* 221 */         catch (NumberFormatException e) {}
/*     */ 
/*     */ 
/*     */       
/*     */       }
/*     */ 
/*     */     
/*     */     }
/* 229 */     else if (line.equals("@")) {
/*     */ 
/*     */       
/*     */       try {
/* 233 */         if (this.params.containsKey("0"))
/*     */         {
/* 235 */           this.ssbuf.setInterval(Integer.parseInt(this.params.get("0")));
/*     */         }
/*     */         
/* 238 */         if (this.params.containsKey("1"))
/*     */         {
/* 240 */           this.ssbuf.setMemtotal(Long.parseLong(this.params.get("1")));
/*     */         }
/*     */         
/* 243 */         if (this.params.containsKey("2"))
/*     */         {
/* 245 */           this.ssbuf.setMemfree(Long.parseLong(this.params.get("2")));
/*     */         }
/*     */         
/* 248 */         if (this.params.containsKey("3"))
/*     */         {
/* 250 */           this.ssbuf.setOps(Long.parseLong(this.params.get("3")));
/*     */         }
/*     */         
/* 253 */         if (this.params.containsKey("4"))
/*     */         {
/* 255 */           this.ssbuf.setDiskops(Long.parseLong(this.params.get("4")));
/*     */         }
/*     */         
/* 258 */         if (this.params.containsKey("5"))
/*     */         {
/* 260 */           this.ssbuf.setVserialops(Long.parseLong(this.params.get("5")));
/*     */         }
/*     */         
/* 263 */         if (this.params.containsKey("6"))
/*     */         {
/* 265 */           this.ssbuf.setInstances(Integer.parseInt(this.params.get("6")));
/*     */         }
/*     */         
/* 268 */         if (this.params.containsKey("7"))
/*     */         {
/* 270 */           this.ssbuf.setInstancesalive(Integer.parseInt(this.params.get("7")));
/*     */         }
/*     */         
/* 273 */         if (this.params.containsKey("8"))
/*     */         {
/* 275 */           this.ssbuf.setThreads(Integer.parseInt(this.params.get("8")));
/*     */         }
/*     */         
/* 278 */         if (this.params.containsKey("9"))
/*     */         {
/* 280 */           this.ssbuf.setUIClients(Integer.parseInt(this.params.get("9")));
/*     */         }
/*     */         
/* 283 */         if (this.params.containsKey("!"))
/*     */         {
/* 285 */           this.ssbuf.setMagic(Long.parseLong(this.params.get("!")));
/*     */         }
/*     */         
/* 288 */         MainWin.submitServerStatusEvent(this.ssbuf);
/*     */       }
/* 290 */       catch (NumberFormatException e) {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     }
/* 297 */     else if (line.equals("L")) {
/*     */       
/* 299 */       if (this.params.containsKey("l")) {
/* 300 */         this.logbuf.setLevel(this.params.get("l"));
/*     */       }
/* 302 */       if (this.params.containsKey("t")) {
/* 303 */         this.logbuf.setTimestamp(Long.valueOf(this.params.get("t")).longValue());
/*     */       }
/* 305 */       if (this.params.containsKey("m")) {
/* 306 */         this.logbuf.setMessage(this.params.get("m"));
/*     */       }
/* 308 */       if (this.params.containsKey("r")) {
/* 309 */         this.logbuf.setThread(this.params.get("r"));
/*     */       }
/* 311 */       if (this.params.containsKey("s")) {
/* 312 */         this.logbuf.setSource(this.params.get("s"));
/*     */       }
/* 314 */       MainWin.addToServerLog(this.logbuf.clone());
/*     */ 
/*     */     
/*     */     }
/* 318 */     else if (line.equals("I")) {
/*     */ 
/*     */ 
/*     */       
/* 322 */       if (this.params.containsKey("k") && this.params.get("k") != null)
/*     */       {
/* 324 */         if (this.params.containsKey("v"))
/*     */         {
/* 326 */           if (this.params.get("v") == null) {
/*     */             
/* 328 */             MainWin.getInstanceConfig().clearProperty(this.params.get("k"));
/*     */           }
/*     */           else {
/*     */             
/* 332 */             MainWin.getInstanceConfig().setProperty(this.params.get("k"), this.params.get("v"));
/*     */           } 
/*     */           
/* 335 */           MainWin.applyConfig();
/*     */         
/*     */         }
/*     */       
/*     */       }
/*     */     }
/* 341 */     else if (line.equals("C")) {
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 346 */       if (this.params.containsKey("k") && this.params.get("k") != null)
/*     */       {
/* 348 */         if (this.params.containsKey("v"))
/*     */         {
/* 350 */           MainWin.submitServerConfigEvent(this.params.get("k"), this.params.get("v"));
/*     */         
/*     */         }
/*     */       
/*     */       }
/*     */     }
/* 356 */     else if (line.equals("M")) {
/*     */ 
/*     */       
/* 359 */       if (this.params.containsKey("k") && this.params.get("k") != null) {
/*     */         
/* 361 */         if (((String)this.params.get("k")).equals("device")) {
/*     */           
/* 363 */           MainWin.getMidiStatus().setCurrentDevice(this.params.get("v"));
/*     */         }
/* 365 */         else if (((String)this.params.get("k")).equals("profile")) {
/*     */           
/* 367 */           MainWin.getMidiStatus().setCurrentProfile(this.params.get("v"));
/*     */         }
/* 369 */         else if (!((String)this.params.get("k")).equals("soundbank")) {
/*     */ 
/*     */ 
/*     */           
/* 373 */           if (((String)this.params.get("k")).equals("voicelock"))
/*     */           {
/* 375 */             MainWin.getMidiStatus().setVoiceLock(Boolean.valueOf(this.params.get("v")).booleanValue());
/*     */           }
/*     */         } 
/*     */         
/* 379 */         MainWin.applyMIDIStatus();
/*     */       } 
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 387 */     if (line.length() > 1)
/*     */     {
/* 389 */       if (line.charAt(1) == ':') {
/*     */         
/* 391 */         String val = null;
/* 392 */         if (line.length() > 2)
/*     */         {
/* 394 */           val = line.substring(2);
/*     */         }
/*     */         
/* 397 */         this.params.put(line.substring(0, 1), val);
/*     */       } 
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void die() {
/* 405 */     this.wanttodie = true;
/* 406 */     if (this.sock != null) {
/*     */ 
/*     */       
/*     */       try {
/* 410 */         this.sock.close();
/*     */       }
/* 412 */       catch (IOException e) {}
/*     */ 
/*     */ 
/*     */       
/* 416 */       this.sock = null;
/*     */     } 
/*     */   }
/*     */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireui/SyncThread.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */