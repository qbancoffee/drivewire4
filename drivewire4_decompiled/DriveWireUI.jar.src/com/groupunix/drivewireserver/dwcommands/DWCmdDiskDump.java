/*    */ package com.groupunix.drivewireserver.dwcommands;
/*    */ 
/*    */ import com.groupunix.drivewireserver.dwexceptions.DWDriveNotLoadedException;
/*    */ import com.groupunix.drivewireserver.dwexceptions.DWDriveNotValidException;
/*    */ import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocolHandler;
/*    */ import java.io.IOException;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class DWCmdDiskDump
/*    */   extends DWCommand
/*    */ {
/*    */   private DWProtocolHandler dwProto;
/*    */   
/*    */   public DWCmdDiskDump(DWProtocolHandler dwProto, DWCommand parent) {
/* 16 */     setParentCmd(parent);
/* 17 */     this.dwProto = dwProto;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getCommand() {
/* 22 */     return "dump";
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getShortHelp() {
/* 29 */     return "Dump sector from disk";
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getUsage() {
/* 35 */     return "dw disk dump # sector";
/*    */   }
/*    */ 
/*    */   
/*    */   public DWCommandResponse parse(String cmdline) {
/* 40 */     String[] args = cmdline.split(" ");
/*    */     
/* 42 */     if (cmdline.length() == 0 || args.length < 2)
/*    */     {
/* 44 */       return new DWCommandResponse(false, (byte)10, "dw disk dump requires a drive # and sector # as arguments");
/*    */     }
/* 46 */     return doDiskDump(args[0], args[1]);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   private DWCommandResponse doDiskDump(String drivestr, String sectorstr) {
/*    */     try {
/* 57 */       int driveno = Integer.parseInt(drivestr);
/* 58 */       int sectorno = Integer.parseInt(sectorstr);
/*    */       
/* 60 */       return new DWCommandResponse(new String(this.dwProto.getDiskDrives().getDisk(driveno).getSector(sectorno).getData()));
/*    */     
/*    */     }
/* 63 */     catch (NumberFormatException e) {
/*    */       
/* 65 */       return new DWCommandResponse(false, (byte)10, "Syntax error: non numeric drive # or sector #");
/*    */     }
/* 67 */     catch (DWDriveNotLoadedException e) {
/*    */       
/* 69 */       return new DWCommandResponse(false, (byte)102, e.getMessage());
/*    */     }
/* 71 */     catch (DWDriveNotValidException e) {
/*    */       
/* 73 */       return new DWCommandResponse(false, (byte)101, e.getMessage());
/*    */     }
/* 75 */     catch (IOException e) {
/*    */       
/* 77 */       return new DWCommandResponse(false, (byte)-54, e.getMessage());
/*    */     } 
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean validate(String cmdline) {
/* 84 */     return true;
/*    */   }
/*    */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/dwcommands/DWCmdDiskDump.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */