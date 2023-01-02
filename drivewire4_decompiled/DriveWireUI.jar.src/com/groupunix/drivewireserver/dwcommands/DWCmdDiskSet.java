/*    */ package com.groupunix.drivewireserver.dwcommands;
/*    */ 
/*    */ import com.groupunix.drivewireserver.dwexceptions.DWDriveNotLoadedException;
/*    */ import com.groupunix.drivewireserver.dwexceptions.DWDriveNotValidException;
/*    */ import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocolHandler;
/*    */ import com.groupunix.drivewireserver.dwprotocolhandler.DWUtils;
/*    */ 
/*    */ 
/*    */ public class DWCmdDiskSet
/*    */   extends DWCommand
/*    */ {
/*    */   private DWProtocolHandler dwProto;
/*    */   
/*    */   public DWCmdDiskSet(DWProtocolHandler dwProto, DWCommand parent) {
/* 15 */     setParentCmd(parent);
/* 16 */     this.dwProto = dwProto;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getCommand() {
/* 21 */     return "set";
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getShortHelp() {
/* 28 */     return "Set disk parameters";
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getUsage() {
/* 34 */     return "dw disk set # param val";
/*    */   }
/*    */ 
/*    */   
/*    */   public DWCommandResponse parse(String cmdline) {
/* 39 */     String[] args = cmdline.split(" ");
/*    */     
/* 41 */     if (args.length < 3)
/*    */     {
/* 43 */       return new DWCommandResponse(false, (byte)10, "dw disk set requires 3 arguments.");
/*    */     }
/*    */ 
/*    */     
/*    */     try {
/* 48 */       return doDiskSet(this.dwProto.getDiskDrives().getDriveNoFromString(args[0]), DWUtils.dropFirstToken(cmdline));
/*    */     }
/* 50 */     catch (DWDriveNotValidException e) {
/*    */       
/* 52 */       return new DWCommandResponse(false, (byte)101, e.getMessage());
/*    */     } 
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   private DWCommandResponse doDiskSet(int driveno, String cmdline) {
/* 62 */     String[] parts = cmdline.split(" ");
/*    */ 
/*    */ 
/*    */ 
/*    */     
/*    */     try {
/* 68 */       if (this.dwProto.getDiskDrives().getDisk(driveno).getParams().containsKey(parts[0])) {
/*    */         
/* 70 */         this.dwProto.getDiskDrives().getDisk(driveno).getParams().setProperty(parts[0], DWUtils.dropFirstToken(cmdline));
/* 71 */         return new DWCommandResponse("Param '" + parts[0] + "' set for disk " + driveno + ".");
/*    */       } 
/*    */ 
/*    */       
/* 75 */       return new DWCommandResponse(false, (byte)111, "No parameter '" + parts[0] + "' available for disk " + driveno + ".");
/*    */     
/*    */     }
/* 78 */     catch (DWDriveNotLoadedException e) {
/*    */       
/* 80 */       return new DWCommandResponse(false, (byte)102, e.getMessage());
/*    */     }
/* 82 */     catch (DWDriveNotValidException e) {
/*    */       
/* 84 */       return new DWCommandResponse(false, (byte)101, e.getMessage());
/*    */     } 
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean validate(String cmdline) {
/* 95 */     return true;
/*    */   }
/*    */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/dwcommands/DWCmdDiskSet.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */