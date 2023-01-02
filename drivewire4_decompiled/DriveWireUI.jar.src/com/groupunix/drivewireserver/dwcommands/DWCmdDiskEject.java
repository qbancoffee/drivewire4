/*    */ package com.groupunix.drivewireserver.dwcommands;
/*    */ 
/*    */ import com.groupunix.drivewireserver.dwexceptions.DWDriveNotLoadedException;
/*    */ import com.groupunix.drivewireserver.dwexceptions.DWDriveNotValidException;
/*    */ import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocolHandler;
/*    */ 
/*    */ 
/*    */ public class DWCmdDiskEject
/*    */   extends DWCommand
/*    */ {
/*    */   private DWProtocolHandler dwProto;
/*    */   
/*    */   public DWCmdDiskEject(DWProtocolHandler dwProto, DWCommand parent) {
/* 14 */     setParentCmd(parent);
/* 15 */     this.dwProto = dwProto;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getCommand() {
/* 20 */     return "eject";
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public DWCommandResponse parse(String cmdline) {
/* 26 */     String[] args = cmdline.split(" ");
/*    */     
/* 28 */     if (args.length == 1) {
/*    */ 
/*    */ 
/*    */       
/* 32 */       if (args[0].equals("all"))
/*    */       {
/*    */         
/* 35 */         return doDiskEjectAll();
/*    */       }
/*    */ 
/*    */ 
/*    */ 
/*    */       
/*    */       try {
/* 42 */         return doDiskEject(this.dwProto.getDiskDrives().getDriveNoFromString(args[0]));
/*    */       }
/* 44 */       catch (DWDriveNotValidException e) {
/*    */         
/* 46 */         return new DWCommandResponse(false, (byte)101, e.getMessage());
/*    */       } 
/*    */     } 
/*    */ 
/*    */     
/* 51 */     return new DWCommandResponse(false, (byte)10, "Syntax error");
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   private DWCommandResponse doDiskEjectAll() {
/* 58 */     this.dwProto.getDiskDrives().EjectAllDisks();
/* 59 */     return new DWCommandResponse("Ejected all disks.\r\n");
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   private DWCommandResponse doDiskEject(int driveno) {
/*    */     try {
/* 67 */       this.dwProto.getDiskDrives().EjectDisk(driveno);
/*    */       
/* 69 */       return new DWCommandResponse("Disk ejected from drive " + driveno + ".\r\n");
/*    */     }
/* 71 */     catch (DWDriveNotValidException e) {
/*    */       
/* 73 */       return new DWCommandResponse(false, (byte)101, e.getMessage());
/*    */     }
/* 75 */     catch (DWDriveNotLoadedException e) {
/*    */       
/* 77 */       return new DWCommandResponse(false, (byte)102, e.getMessage());
/*    */     } 
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getShortHelp() {
/* 87 */     return "Eject disk from drive #";
/*    */   }
/*    */ 
/*    */   
/*    */   public String getUsage() {
/* 92 */     return "dw disk eject {# | all}";
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean validate(String cmdline) {
/* 97 */     return true;
/*    */   }
/*    */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/dwcommands/DWCmdDiskEject.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */