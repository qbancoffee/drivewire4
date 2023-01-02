/*     */ package com.groupunix.drivewireui;
/*     */ 
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.Date;
/*     */ 
/*     */ public class LogItem
/*     */ {
/*   8 */   private static final SimpleDateFormat shortFormat = new SimpleDateFormat("HH:mm:ss.SSS");
/*   9 */   private static final SimpleDateFormat longFormat = new SimpleDateFormat("EEE MMM dd yyyy HH:mm:ss.SSS");
/*     */   
/*     */   private String message;
/*     */   
/*     */   private long timestamp;
/*     */   private String thread;
/*     */   private String level;
/*     */   private String source;
/*     */   
/*     */   public void setMessage(String message) {
/*  19 */     this.message = message;
/*     */   }
/*     */   
/*     */   public String getMessage() {
/*  23 */     return this.message;
/*     */   }
/*     */   
/*     */   public void setTimestamp(long timestamp) {
/*  27 */     this.timestamp = timestamp;
/*     */   }
/*     */   
/*     */   public long getTimestamp() {
/*  31 */     return this.timestamp;
/*     */   }
/*     */   
/*     */   public void setThread(String thread) {
/*  35 */     this.thread = thread;
/*     */   }
/*     */   
/*     */   public String getThread() {
/*  39 */     return this.thread;
/*     */   }
/*     */   
/*     */   public void setLevel(String level) {
/*  43 */     this.level = level;
/*     */   }
/*     */   
/*     */   public String getLevel() {
/*  47 */     return this.level;
/*     */   }
/*     */   
/*     */   public void setSource(String source) {
/*  51 */     this.source = source;
/*     */   }
/*     */   
/*     */   public String getSource() {
/*  55 */     return this.source;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public LogItem clone() {
/*  61 */     LogItem res = new LogItem();
/*     */     
/*  63 */     res.setLevel(new String(this.level));
/*  64 */     res.setMessage(new String(this.message));
/*  65 */     res.setSource(new String(this.source));
/*  66 */     res.setThread(new String(this.thread));
/*  67 */     res.setTimestamp(this.timestamp);
/*     */     
/*  69 */     return res;
/*     */   }
/*     */   
/*     */   public String getShortTimestamp() {
/*  73 */     return shortFormat.format(new Date(this.timestamp));
/*     */   }
/*     */   
/*     */   public String getLongTimestamp() {
/*  77 */     return longFormat.format(new Date(this.timestamp));
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String getShortSource() {
/*  83 */     if (this.source.lastIndexOf(".") > -1 && this.source.lastIndexOf(".") < this.source.length() - 2)
/*  84 */       return this.source.substring(this.source.lastIndexOf(".") + 1); 
/*  85 */     return this.source;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/*  92 */     String res = getLongTimestamp() + "  " + String.format("%-5s  %-18s  %-18s  ", new Object[] { getLevel(), getShortSource(), getThread() });
/*  93 */     res = res + getMessage();
/*     */     
/*  95 */     return res;
/*     */   }
/*     */   
/*     */   public boolean isImportant() {
/*  99 */     if (getLevel().equals("WARN") || getLevel().equals("ERROR") || getLevel().equals("FATAL")) {
/* 100 */       return true;
/*     */     }
/* 102 */     return false;
/*     */   }
/*     */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireui/LogItem.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */