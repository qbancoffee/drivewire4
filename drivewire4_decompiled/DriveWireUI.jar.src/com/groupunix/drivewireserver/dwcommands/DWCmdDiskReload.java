/*    */ package com.groupunix.drivewireserver.dwcommands;
/*    */ 
/*    */ import com.groupunix.drivewireserver.dwexceptions.DWDriveNotLoadedException;
/*    */ import com.groupunix.drivewireserver.dwexceptions.DWDriveNotValidException;
/*    */ import com.groupunix.drivewireserver.dwexceptions.DWImageFormatException;
/*    */ import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocolHandler;
/*    */ import java.io.IOException;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class DWCmdDiskReload
/*    */   extends DWCommand
/*    */ {
/*    */   private DWProtocolHandler dwProto;
/*    */   
/*    */   public DWCmdDiskReload(DWProtocolHandler dwProto, DWCommand parent) {
/* 17 */     setParentCmd(parent);
/* 18 */     this.dwProto = dwProto;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getCommand() {
/* 23 */     return "reload";
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getShortHelp() {
/* 30 */     return "Reload disk in drive #";
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getUsage() {
/* 36 */     return "dw disk reload {# | all}";
/*    */   }
/*    */ 
/*    */   
/*    */   public DWCommandResponse parse(String cmdline) {
/* 41 */     if (cmdline.length() == 0)
/*    */     {
/* 43 */       return new DWCommandResponse(false, (byte)10, "dw disk reload requires a drive # or 'all' as an argument");
/*    */     }
/*    */     
/* 46 */     return doDiskReload(cmdline);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   private DWCommandResponse doDiskReload(String drivestr) {
/*    */     try {
/* 55 */       if (drivestr.equals("all")) {
/*    */         
/* 57 */         this.dwProto.getDiskDrives().ReLoadAllDisks();
/*    */         
/* 59 */         return new DWCommandResponse("All disks reloaded.");
/*    */       } 
/*    */ 
/*    */ 
/*    */       
/* 64 */       this.dwProto.getDiskDrives().ReLoadDisk(this.dwProto.getDiskDrives().getDriveNoFromString(drivestr));
/*    */       
/* 66 */       return new DWCommandResponse("Disk in drive #" + drivestr + " reloaded.");
/*    */     
/*    */     }
/* 69 */     catch (NumberFormatException e) {
/*    */       
/* 71 */       return new DWCommandResponse(false, (byte)10, "Syntax error: non numeric drive #");
/*    */     }
/* 73 */     catch (IOException e1) {
/*    */       
/* 75 */       return new DWCommandResponse(false, (byte)-54, e1.getMessage());
/*    */     }
/* 77 */     catch (DWDriveNotLoadedException e) {
/*    */       
/* 79 */       return new DWCommandResponse(false, (byte)102, e.getMessage());
/*    */     }
/* 81 */     catch (DWDriveNotValidException e) {
/*    */       
/* 83 */       return new DWCommandResponse(false, (byte)101, e.getMessage());
/*    */     }
/* 85 */     catch (DWImageFormatException e) {
/*    */       
/* 87 */       return new DWCommandResponse(false, (byte)104, e.getMessage());
/*    */     } 
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean validate(String cmdline) {
/* 96 */     return true;
/*    */   }
/*    */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/dwcommands/DWCmdDiskReload.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */