/*     */ package com.groupunix.drivewireui;
/*     */ 
/*     */ import java.io.BufferedReader;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.StringReader;
/*     */ import java.net.Socket;
/*     */ import java.net.UnknownHostException;
/*     */ import java.util.Arrays;
/*     */ import java.util.List;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Connection
/*     */ {
/*     */   private static final int BUFFER_SIZE = 2048;
/*     */   private int port;
/*     */   private String host;
/*     */   private int instance;
/*     */   private Socket sock;
/*     */   private BufferedReader in;
/*     */   
/*     */   public Connection(String host, int port, int instance) {
/*  28 */     setHost(host);
/*  29 */     setPort(port);
/*  30 */     setInstance(instance);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void Connect() throws UnknownHostException, IOException {
/*  36 */     MainWin.setConStatusConnect();
/*  37 */     this.sock = new Socket(this.host, this.port);
/*  38 */     this.sock.setSoTimeout(MainWin.config.getInt("TCPTimeout", 15000));
/*  39 */     this.in = new BufferedReader(new InputStreamReader(this.sock.getInputStream()));
/*     */   }
/*     */ 
/*     */   
/*     */   public void setPort(int port) {
/*  44 */     this.port = port;
/*     */   }
/*     */   public int getPort() {
/*  47 */     return this.port;
/*     */   }
/*     */   public void setHost(String host) {
/*  50 */     this.host = host;
/*     */   }
/*     */   public String getHost() {
/*  53 */     return this.host;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/*  59 */     this.sock.close();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean connected() {
/*  65 */     if (this.sock.isClosed())
/*     */     {
/*  67 */       return false;
/*     */     }
/*     */     
/*  70 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void sendCommand(int tid, String cmd, int instance) throws IOException, DWUIOperationFailedException {
/*  77 */     sendCommand(tid, cmd, instance, true);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void sendCommand(int tid, String cmd, int instance, boolean markComplete) throws IOException, DWUIOperationFailedException {
/*  85 */     List<String> resp = loadList(instance, cmd);
/*     */     
/*  87 */     if (resp.size() > 0 && ((String)resp.get(0)).startsWith("FAIL"))
/*     */     {
/*  89 */       throw new DWUIOperationFailedException(((String)resp.get(0)).trim());
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*  94 */     String txt = "";
/*  95 */     for (int i = 0; i < resp.size(); i++)
/*     */     {
/*  97 */       txt = txt + (String)resp.get(i) + "\n";
/*     */     }
/*     */     
/* 100 */     if (markComplete) {
/* 101 */       MainWin.taskman.updateTask(tid, 1, txt);
/*     */     } else {
/* 103 */       MainWin.taskman.updateTask(tid, 0, txt);
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
/*     */   public void setInstance(int instance) {
/* 115 */     this.instance = instance;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getInstance() {
/* 120 */     return this.instance;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public StringReader loadReader(int instance, String arg) throws IOException, DWUIOperationFailedException {
/* 127 */     this.sock.getOutputStream().write((instance + "").getBytes());
/* 128 */     this.sock.getOutputStream().write(0);
/* 129 */     this.sock.getOutputStream().write((arg + "\n").getBytes());
/*     */     
/* 131 */     return new StringReader(getResponse());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private String getResponse() throws IOException, DWUIOperationFailedException {
/* 139 */     StringBuilder buffer = new StringBuilder(4096);
/* 140 */     char[] cbuf = new char[2048];
/*     */     
/* 142 */     int readres = this.in.read(cbuf, 0, 2048);
/*     */     
/* 144 */     while (!this.sock.isClosed() && readres > -1) {
/*     */       
/* 146 */       buffer.append(cbuf, 0, readres);
/* 147 */       readres = this.in.read(cbuf, 0, 2048);
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 153 */     if (buffer.length() < 3) {
/* 154 */       throw new DWUIOperationFailedException((byte)-34, "Incomplete or missing header");
/*     */     }
/* 156 */     if (buffer.charAt(0) == '\000' && buffer.charAt(2) == '\000') {
/*     */ 
/*     */       
/* 159 */       if (buffer.charAt(1) != '\000') {
/* 160 */         throw new DWUIOperationFailedException((byte)buffer.charAt(1), buffer.substring(3));
/*     */       }
/*     */     }
/*     */     else {
/*     */       
/* 165 */       throw new DWUIOperationFailedException((byte)-34, "Corrupt header (Old server version?)");
/*     */     } 
/*     */ 
/*     */     
/* 169 */     buffer.delete(0, 3);
/* 170 */     return buffer.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<String> loadList(int instance, String arg) throws IOException, DWUIOperationFailedException {
/* 177 */     this.sock.getOutputStream().write((instance + "").getBytes());
/* 178 */     this.sock.getOutputStream().write(0);
/* 179 */     this.sock.getOutputStream().write((arg + "\n").getBytes());
/*     */     
/* 181 */     List<String> res = Arrays.asList(getResponse().split("\n"));
/*     */     
/* 183 */     return res;
/*     */   }
/*     */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireui/Connection.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */