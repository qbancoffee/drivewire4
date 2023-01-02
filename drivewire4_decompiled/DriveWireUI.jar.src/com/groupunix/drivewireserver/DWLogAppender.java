/*     */ package com.groupunix.drivewireserver;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.LinkedList;
/*     */ import org.apache.log4j.AppenderSkeleton;
/*     */ import org.apache.log4j.Layout;
/*     */ import org.apache.log4j.Level;
/*     */ import org.apache.log4j.spi.LoggingEvent;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DWLogAppender
/*     */   extends AppenderSkeleton
/*     */ {
/*  18 */   private LinkedList<LoggingEvent> events = new LinkedList<LoggingEvent>();
/*     */ 
/*     */   
/*     */   public DWLogAppender(Layout layout) {
/*  22 */     setLayout(layout);
/*     */   }
/*     */   
/*     */   public void setLayout(Layout layout) {
/*  26 */     this.layout = layout;
/*     */   }
/*     */   
/*     */   public Layout getLayout() {
/*  30 */     return this.layout;
/*     */   }
/*     */   
/*     */   public boolean requiresLayout() {
/*  34 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized void shutdown() {}
/*     */ 
/*     */ 
/*     */   
/*     */   protected void append(LoggingEvent event) {
/*  45 */     if (event.getLevel() == Level.FATAL && !DriveWireServer.isConsoleLogging() && !DriveWireServer.isDebug())
/*     */     {
/*     */       
/*  48 */       System.out.println("FATAL: " + event.getRenderedMessage());
/*     */     }
/*     */ 
/*     */     
/*  52 */     if (!event.getMessage().equals("ConfigurationUtils.locate(): base is null, name is null") && !event.getLocationInformation().getClassName().startsWith("org.apache.commons.httpclient")) {
/*     */ 
/*     */       
/*  55 */       DriveWireServer.submitLogEvent(event);
/*     */ 
/*     */       
/*  58 */       synchronized (this.events) {
/*     */         
/*  60 */         if (this.events.size() == 500)
/*     */         {
/*  62 */           this.events.removeFirst();
/*     */         }
/*     */         
/*  65 */         this.events.addLast(event);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ArrayList<String> getLastEvents(int num) {
/*  74 */     ArrayList<String> eventstxt = new ArrayList<String>();
/*  75 */     int start = 0;
/*     */     
/*  77 */     synchronized (this.events) {
/*     */       
/*  79 */       if (num > this.events.size()) {
/*  80 */         num = this.events.size();
/*     */       }
/*  82 */       if (this.events.size() > num)
/*     */       {
/*  84 */         start = this.events.size() - num;
/*     */       }
/*     */       
/*  87 */       for (int i = start; i < this.events.size(); i++)
/*     */       {
/*     */         
/*  90 */         eventstxt.add(this.layout.format(this.events.get(i)));
/*     */       }
/*     */     } 
/*     */     
/*  94 */     return eventstxt;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void close() {}
/*     */ 
/*     */ 
/*     */   
/*     */   public int getEventsSize() {
/* 104 */     return this.events.size();
/*     */   }
/*     */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/DWLogAppender.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */