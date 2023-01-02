/*    */ package com.groupunix.drivewireserver.uicommands;
/*    */ 
/*    */ import com.groupunix.drivewireserver.DriveWireServer;
/*    */ import com.groupunix.drivewireserver.dwcommands.DWCommand;
/*    */ import com.groupunix.drivewireserver.dwcommands.DWCommandResponse;
/*    */ import java.io.IOException;
/*    */ import org.apache.commons.vfs.FileObject;
/*    */ import org.apache.commons.vfs.FileSystemManager;
/*    */ import org.apache.commons.vfs.FileType;
/*    */ import org.apache.commons.vfs.VFS;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class UICmdServerShowLocalDisks
/*    */   extends DWCommand
/*    */ {
/*    */   public String getCommand() {
/* 21 */     return "localdisks";
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getShortHelp() {
/* 29 */     return "show server local disks";
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getUsage() {
/* 35 */     return "ui server show localdisks";
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public DWCommandResponse parse(String cmdline) {
/* 43 */     String res = new String();
/*    */ 
/*    */     
/*    */     try {
/* 47 */       if (!DriveWireServer.serverconfig.containsKey("LocalDiskDir")) {
/* 48 */         return new DWCommandResponse(false, (byte)-114, "LocalDiskDir must be defined in configuration");
/*    */       }
/* 50 */       String path = DriveWireServer.serverconfig.getString("LocalDiskDir");
/*    */ 
/*    */ 
/*    */       
/* 54 */       FileSystemManager fsManager = VFS.getManager();
/*    */       
/* 56 */       FileObject dirobj = fsManager.resolveFile(path);
/*    */       
/* 58 */       FileObject[] children = dirobj.getChildren();
/*    */       
/* 60 */       for (int i = 0; i < children.length; i++)
/*    */       {
/* 62 */         if (children[i].getType() == FileType.FILE) {
/* 63 */           res = res + children[i].getName() + "\n";
/*    */         }
/*    */       }
/*    */     
/* 67 */     } catch (IOException e) {
/*    */       
/* 69 */       return new DWCommandResponse(false, (byte)-54, e.getMessage());
/*    */     } 
/*    */     
/* 72 */     return new DWCommandResponse(res);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean validate(String cmdline) {
/* 78 */     return true;
/*    */   }
/*    */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/uicommands/UICmdServerShowLocalDisks.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */