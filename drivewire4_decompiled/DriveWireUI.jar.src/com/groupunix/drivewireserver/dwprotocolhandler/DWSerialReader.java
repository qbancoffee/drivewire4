/*    */ package com.groupunix.drivewireserver.dwprotocolhandler;
/*    */ 
/*    */ import gnu.io.SerialPortEvent;
/*    */ import gnu.io.SerialPortEventListener;
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import java.util.concurrent.ArrayBlockingQueue;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class DWSerialReader
/*    */   implements SerialPortEventListener
/*    */ {
/*    */   private ArrayBlockingQueue<Byte> queue;
/*    */   private InputStream in;
/*    */   private boolean wanttodie = false;
/*    */   
/*    */   public DWSerialReader(InputStream in, ArrayBlockingQueue<Byte> q) {
/* 19 */     this.queue = q;
/* 20 */     this.in = in;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void serialEvent(SerialPortEvent arg0) {
/*    */     try {
/*    */       int data;
/* 30 */       while (!this.wanttodie && (data = this.in.read()) > -1)
/*    */       {
/* 32 */         this.queue.add(Byte.valueOf((byte)data));
/*    */       
/*    */       }
/*    */     }
/* 36 */     catch (IOException e) {
/*    */       
/* 38 */       e.printStackTrace();
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public void shutdown() {
/* 44 */     this.wanttodie = true;
/*    */   }
/*    */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/dwprotocolhandler/DWSerialReader.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */