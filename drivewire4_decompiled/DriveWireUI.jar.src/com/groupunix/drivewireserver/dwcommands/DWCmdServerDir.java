/*    */ package com.groupunix.drivewireserver.dwcommands;
/*    */ 
/*    */ import com.groupunix.drivewireserver.dwprotocolhandler.DWUtils;
/*    */ import org.apache.commons.vfs.FileObject;
/*    */ import org.apache.commons.vfs.FileSystemException;
/*    */ import org.apache.commons.vfs.FileSystemManager;
/*    */ import org.apache.commons.vfs.VFS;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class DWCmdServerDir
/*    */   extends DWCommand
/*    */ {
/*    */   public DWCmdServerDir(DWCommand parent) {
/* 16 */     setParentCmd(parent);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getCommand() {
/* 22 */     return "dir";
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getShortHelp() {
/* 28 */     return "Show directory of URI or local path";
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getUsage() {
/* 34 */     return "dw server dir URI/path";
/*    */   }
/*    */ 
/*    */   
/*    */   public DWCommandResponse parse(String cmdline) {
/* 39 */     if (cmdline.length() == 0)
/*    */     {
/* 41 */       return new DWCommandResponse(false, (byte)10, "dw server dir requires a URI or path as an argument");
/*    */     }
/* 43 */     return doDir(cmdline);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   private DWCommandResponse doDir(String path) {
/* 50 */     String text = new String();
/*    */     
/* 52 */     path = DWUtils.convertStarToBang(path);
/*    */ 
/*    */     
/*    */     try {
/* 56 */       FileSystemManager fsManager = VFS.getManager();
/*    */       
/* 58 */       FileObject dirobj = fsManager.resolveFile(path);
/*    */       
/* 60 */       FileObject[] children = dirobj.getChildren();
/*    */       
/* 62 */       text = text + "Directory of " + dirobj.getName().getURI() + "\r\n\n";
/*    */ 
/*    */       
/* 65 */       int longest = 0;
/*    */       
/* 67 */       for (int i = 0; i < children.length; i++) {
/*    */         
/* 69 */         if (children[i].getName().getBaseName().length() > longest) {
/* 70 */           longest = children[i].getName().getBaseName().length();
/*    */         }
/*    */       } 
/* 73 */       longest++;
/* 74 */       longest++;
/*    */       
/* 76 */       int cols = 80 / longest;
/*    */       
/* 78 */       for (int j = 0; j < children.length; j++)
/*    */       {
/* 80 */         text = text + String.format("%-" + longest + "s", new Object[] { children[j].getName().getBaseName() });
/* 81 */         if ((j + 1) % cols == 0) {
/* 82 */           text = text + "\r\n";
/*    */         }
/*    */       }
/*    */     
/* 86 */     } catch (FileSystemException e) {
/*    */       
/* 88 */       return new DWCommandResponse(false, (byte)-55, e.getMessage());
/*    */     } 
/*    */     
/* 91 */     return new DWCommandResponse(text);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean validate(String cmdline) {
/* 96 */     return true;
/*    */   }
/*    */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/dwcommands/DWCmdServerDir.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */