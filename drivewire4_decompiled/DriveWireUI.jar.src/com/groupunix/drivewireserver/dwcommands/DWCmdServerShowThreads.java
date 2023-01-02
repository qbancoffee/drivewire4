/*    */ package com.groupunix.drivewireserver.dwcommands;
/*    */ 
/*    */ import com.groupunix.drivewireserver.dwprotocolhandler.DWUtils;
/*    */ import java.lang.management.ManagementFactory;
/*    */ import java.lang.management.ThreadMXBean;
/*    */ 
/*    */ 
/*    */ public class DWCmdServerShowThreads
/*    */   extends DWCommand
/*    */ {
/*    */   DWCmdServerShowThreads(DWCommand parent) {
/* 12 */     setParentCmd(parent);
/*    */   }
/*    */ 
/*    */   
/*    */   public String getCommand() {
/* 17 */     return "threads";
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getShortHelp() {
/* 23 */     return "Show server threads";
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getUsage() {
/* 29 */     return "dw server show threads";
/*    */   }
/*    */ 
/*    */   
/*    */   public DWCommandResponse parse(String cmdline) {
/* 34 */     String text = new String();
/*    */     
/* 36 */     text = text + "\r\nDriveWire Server Threads:\r\n\n";
/*    */     
/* 38 */     Thread[] threads = getAllThreads();
/*    */     
/* 40 */     for (int i = 0; i < threads.length; i++) {
/*    */       
/* 42 */       if (threads[i] != null)
/*    */       {
/* 44 */         text = text + String.format("%40s %3d %-8s %-14s", new Object[] { shortenname(threads[i].getName()), Integer.valueOf(threads[i].getPriority()), threads[i].getThreadGroup().getName(), threads[i].getState().toString() }) + "\r\n";
/*    */       }
/*    */     } 
/*    */ 
/*    */     
/* 49 */     return new DWCommandResponse(text);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   private Object shortenname(String name) {
/* 56 */     String res = name;
/*    */ 
/*    */ 
/*    */     
/* 60 */     return res;
/*    */   }
/*    */   
/*    */   private Thread[] getAllThreads() {
/* 64 */     ThreadGroup root = DWUtils.getRootThreadGroup();
/* 65 */     ThreadMXBean thbean = ManagementFactory.getThreadMXBean();
/* 66 */     int nAlloc = thbean.getThreadCount();
/* 67 */     int n = 0;
/*    */     
/*    */     while (true) {
/* 70 */       nAlloc *= 2;
/* 71 */       Thread[] threads = new Thread[nAlloc];
/* 72 */       n = root.enumerate(threads, true);
/* 73 */       if (n != nAlloc) {
/* 74 */         Thread[] copy = new Thread[threads.length];
/* 75 */         System.arraycopy(threads, 0, copy, 0, threads.length);
/* 76 */         return copy;
/*    */       } 
/*    */     } 
/*    */   }
/*    */   public boolean validate(String cmdline) {
/* 81 */     return true;
/*    */   }
/*    */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/dwcommands/DWCmdServerShowThreads.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */