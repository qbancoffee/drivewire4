/*    */ package com.groupunix.drivewireserver.virtualserial;
/*    */ 
/*    */ import com.groupunix.drivewireserver.dwcommands.DWCmd;
/*    */ import com.groupunix.drivewireserver.dwcommands.DWCommand;
/*    */ import com.groupunix.drivewireserver.dwcommands.DWCommandList;
/*    */ import com.groupunix.drivewireserver.dwcommands.DWCommandResponse;
/*    */ import com.groupunix.drivewireserver.dwexceptions.DWPortNotValidException;
/*    */ import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocol;
/*    */ import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocolHandler;
/*    */ import org.apache.log4j.Logger;
/*    */ 
/*    */ public class DWUtilDWThread
/*    */   implements Runnable
/*    */ {
/* 15 */   private static final Logger logger = Logger.getLogger("DWServer.DWUtilDWThread");
/*    */   
/* 17 */   private int vport = -1;
/* 18 */   private String strargs = null;
/*    */   
/*    */   private DWVSerialPorts dwVSerialPorts;
/*    */   
/*    */   private boolean protect = false;
/*    */   private DWCommandList commands;
/*    */   
/*    */   public DWUtilDWThread(DWProtocolHandler dwProto, int vport, String args) {
/* 26 */     this.vport = vport;
/* 27 */     this.strargs = args;
/* 28 */     this.dwVSerialPorts = dwProto.getVPorts();
/*    */     
/* 30 */     if (vport <= 15)
/*    */     {
/* 32 */       this.protect = dwProto.getConfig().getBoolean("ProtectedMode", false);
/*    */     }
/*    */ 
/*    */     
/* 36 */     this.commands = new DWCommandList((DWProtocol)dwProto, dwProto.getCMDCols());
/* 37 */     this.commands.addcommand((DWCommand)new DWCmd((DWProtocol)dwProto));
/*    */     
/* 39 */     logger.debug("init dw util thread (protected mode: " + this.protect + ")");
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void run() {
/* 47 */     Thread.currentThread().setName("dwutil-" + Thread.currentThread().getId());
/* 48 */     Thread.currentThread().setPriority(5);
/*    */     
/* 50 */     logger.debug("run for port " + this.vport);
/*    */ 
/*    */     
/*    */     try {
/* 54 */       this.dwVSerialPorts.markConnected(this.vport);
/* 55 */       this.dwVSerialPorts.setUtilMode(this.vport, 1);
/*    */       
/* 57 */       DWCommandResponse resp = this.commands.parse(this.strargs);
/*    */       
/* 59 */       if (resp.getSuccess()) {
/*    */         
/* 61 */         if (resp.isUsebytes())
/*    */         {
/* 63 */           this.dwVSerialPorts.sendUtilityOKResponse(this.vport, resp.getResponseBytes());
/*    */         }
/*    */         else
/*    */         {
/* 67 */           this.dwVSerialPorts.sendUtilityOKResponse(this.vport, resp.getResponseText());
/*    */         }
/*    */       
/*    */       } else {
/*    */         
/* 72 */         this.dwVSerialPorts.sendUtilityFailResponse(this.vport, resp.getResponseCode(), resp.getResponseText());
/*    */       } 
/*    */ 
/*    */       
/* 76 */       while (this.dwVSerialPorts.bytesWaiting(this.vport) > 0 && this.dwVSerialPorts.isOpen(this.vport)) {
/*    */         
/* 78 */         logger.debug("pause for the cause: " + this.dwVSerialPorts.bytesWaiting(this.vport) + " bytes left");
/* 79 */         Thread.sleep(100L);
/*    */       } 
/*    */       
/* 82 */       if (this.vport < 15)
/*    */       {
/* 84 */         this.dwVSerialPorts.closePort(this.vport);
/*    */       
/*    */       }
/*    */     }
/* 88 */     catch (InterruptedException e) {
/*    */       
/* 90 */       logger.error(e.getMessage());
/*    */     }
/* 92 */     catch (DWPortNotValidException e) {
/*    */       
/* 94 */       logger.error(e.getMessage());
/*    */     } 
/*    */ 
/*    */     
/* 98 */     logger.debug("exiting");
/*    */   }
/*    */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/virtualserial/DWUtilDWThread.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */