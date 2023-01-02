/*    */ package com.groupunix.drivewireserver.dwcommands;
/*    */ 
/*    */ import com.groupunix.drivewireserver.dwdisk.DWDECBFileSystem;
/*    */ import com.groupunix.drivewireserver.dwexceptions.DWDECBFileSystemFileNotFoundException;
/*    */ import com.groupunix.drivewireserver.dwexceptions.DWDECBFileSystemInvalidFATException;
/*    */ import com.groupunix.drivewireserver.dwexceptions.DWDriveNotLoadedException;
/*    */ import com.groupunix.drivewireserver.dwexceptions.DWDriveNotValidException;
/*    */ import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocolHandler;
/*    */ import java.io.IOException;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class DWCmdDiskDosList
/*    */   extends DWCommand
/*    */ {
/*    */   private DWProtocolHandler dwProto;
/*    */   
/*    */   public DWCmdDiskDosList(DWProtocolHandler dwProto, DWCommand parent) {
/* 19 */     setParentCmd(parent);
/* 20 */     this.dwProto = dwProto;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getCommand() {
/* 25 */     return "list";
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public DWCommandResponse parse(String cmdline) {
/* 31 */     String[] args = cmdline.split(" ");
/*    */     
/* 33 */     if (args.length == 2) {
/*    */       
/*    */       try {
/*    */         
/* 37 */         return doDiskDosList(this.dwProto.getDiskDrives().getDriveNoFromString(args[0]), args[1]);
/*    */       }
/* 39 */       catch (DWDriveNotValidException e) {
/*    */         
/* 41 */         return new DWCommandResponse(false, (byte)101, e.getMessage());
/*    */       }
/* 43 */       catch (DWDriveNotLoadedException e) {
/*    */         
/* 45 */         return new DWCommandResponse(false, (byte)102, e.getMessage());
/*    */       }
/* 47 */       catch (DWDECBFileSystemFileNotFoundException e) {
/*    */         
/* 49 */         return new DWCommandResponse(false, (byte)-53, e.getMessage());
/*    */       }
/* 51 */       catch (DWDECBFileSystemInvalidFATException e) {
/*    */         
/* 53 */         return new DWCommandResponse(false, (byte)-55, e.getMessage());
/*    */       }
/* 55 */       catch (IOException e) {
/*    */         
/* 57 */         return new DWCommandResponse(false, (byte)-54, e.getMessage());
/*    */       } 
/*    */     }
/*    */ 
/*    */     
/* 62 */     return new DWCommandResponse(false, (byte)10, "Syntax error");
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   private DWCommandResponse doDiskDosList(int driveno, String filename) throws DWDriveNotLoadedException, DWDriveNotValidException, DWDECBFileSystemFileNotFoundException, DWDECBFileSystemInvalidFATException, IOException {
/* 68 */     String res = "";
/*    */     
/* 70 */     DWDECBFileSystem tmp = new DWDECBFileSystem(this.dwProto.getDiskDrives().getDisk(driveno));
/*    */     
/* 72 */     res = tmp.getFileContents(filename);
/*    */     
/* 74 */     return new DWCommandResponse(res);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getShortHelp() {
/* 83 */     return "List contents of DOS file";
/*    */   }
/*    */ 
/*    */   
/*    */   public String getUsage() {
/* 88 */     return "dw disk dos list # filename";
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean validate(String cmdline) {
/* 93 */     return true;
/*    */   }
/*    */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/dwcommands/DWCmdDiskDosList.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */