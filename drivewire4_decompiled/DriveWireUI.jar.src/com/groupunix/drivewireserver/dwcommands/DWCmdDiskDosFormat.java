/*    */ package com.groupunix.drivewireserver.dwcommands;
/*    */ 
/*    */ import com.groupunix.drivewireserver.dwexceptions.DWDriveNotLoadedException;
/*    */ import com.groupunix.drivewireserver.dwexceptions.DWDriveNotValidException;
/*    */ import com.groupunix.drivewireserver.dwexceptions.DWDriveWriteProtectedException;
/*    */ import com.groupunix.drivewireserver.dwexceptions.DWInvalidSectorException;
/*    */ import com.groupunix.drivewireserver.dwexceptions.DWSeekPastEndOfDeviceException;
/*    */ import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocolHandler;
/*    */ import java.io.IOException;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class DWCmdDiskDosFormat
/*    */   extends DWCommand
/*    */ {
/*    */   private DWProtocolHandler dwProto;
/*    */   
/*    */   public DWCmdDiskDosFormat(DWProtocolHandler dwProto, DWCommand parent) {
/* 19 */     setParentCmd(parent);
/* 20 */     this.dwProto = dwProto;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getCommand() {
/* 25 */     return "format";
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public DWCommandResponse parse(String cmdline) {
/* 31 */     String[] args = cmdline.split(" ");
/*    */     
/* 33 */     if (args.length == 1) {
/*    */       
/*    */       try {
/*    */         
/* 37 */         return doDiskDosCreate(this.dwProto.getDiskDrives().getDriveNoFromString(args[0]));
/*    */       }
/* 39 */       catch (DWDriveNotValidException e) {
/*    */         
/* 41 */         return new DWCommandResponse(false, (byte)101, e.getMessage());
/*    */       }
/* 43 */       catch (DWDriveNotLoadedException e) {
/*    */         
/* 45 */         return new DWCommandResponse(false, (byte)102, e.getMessage());
/*    */       }
/* 47 */       catch (DWInvalidSectorException e) {
/*    */         
/* 49 */         return new DWCommandResponse(false, (byte)100, e.getMessage());
/*    */       }
/* 51 */       catch (DWSeekPastEndOfDeviceException e) {
/*    */         
/* 53 */         return new DWCommandResponse(false, (byte)100, e.getMessage());
/*    */       }
/* 55 */       catch (DWDriveWriteProtectedException e) {
/*    */         
/* 57 */         return new DWCommandResponse(false, (byte)100, e.getMessage());
/*    */       }
/* 59 */       catch (IOException e) {
/*    */         
/* 61 */         return new DWCommandResponse(false, (byte)-54, e.getMessage());
/*    */       } 
/*    */     }
/*    */ 
/*    */     
/* 66 */     return new DWCommandResponse(false, (byte)10, "Syntax error");
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   private DWCommandResponse doDiskDosCreate(int driveno) throws DWDriveNotValidException, DWDriveNotLoadedException, DWInvalidSectorException, DWSeekPastEndOfDeviceException, DWDriveWriteProtectedException, IOException {
/* 72 */     this.dwProto.getDiskDrives().formatDOSFS(driveno);
/*    */     
/* 74 */     return new DWCommandResponse("Formatted new DOS disk image in drive " + driveno + ".");
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getShortHelp() {
/* 83 */     return "Format disk image with DOS filesystem";
/*    */   }
/*    */ 
/*    */   
/*    */   public String getUsage() {
/* 88 */     return "dw disk dos format #";
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean validate(String cmdline) {
/* 93 */     return true;
/*    */   }
/*    */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/dwcommands/DWCmdDiskDosFormat.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */