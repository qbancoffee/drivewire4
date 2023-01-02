/*     */ package com.groupunix.drivewireserver.dwcommands;
/*     */ 
/*     */ import com.groupunix.drivewireserver.dwprotocolhandler.DWUtils;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import org.apache.commons.vfs.FileContent;
/*     */ import org.apache.commons.vfs.FileObject;
/*     */ import org.apache.commons.vfs.FileSystemException;
/*     */ import org.apache.commons.vfs.FileSystemManager;
/*     */ import org.apache.commons.vfs.VFS;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DWCmdServerList
/*     */   extends DWCommand
/*     */ {
/*     */   public DWCmdServerList(DWCommand parent) {
/*  20 */     setParentCmd(parent);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String getCommand() {
/*  26 */     return "list";
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String getShortHelp() {
/*  32 */     return "List contents of file on server";
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String getUsage() {
/*  38 */     return "dw server list URI/path";
/*     */   }
/*     */ 
/*     */   
/*     */   public DWCommandResponse parse(String cmdline) {
/*  43 */     if (cmdline.length() == 0)
/*     */     {
/*  45 */       return new DWCommandResponse(false, (byte)10, "dw server list requires a URI or local file path as an argument");
/*     */     }
/*  47 */     return doList(cmdline);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private DWCommandResponse doList(String path) {
/*  55 */     InputStream ins = null;
/*  56 */     FileObject fileobj = null;
/*  57 */     FileContent fc = null;
/*     */     
/*  59 */     ByteArrayOutputStream baos = new ByteArrayOutputStream();
/*     */ 
/*     */     
/*     */     try {
/*  63 */       FileSystemManager fsManager = VFS.getManager();
/*     */       
/*  65 */       path = DWUtils.convertStarToBang(path);
/*     */       
/*  67 */       fileobj = fsManager.resolveFile(path);
/*     */       
/*  69 */       fc = fileobj.getContent();
/*     */       
/*  71 */       ins = fc.getInputStream();
/*     */       
/*  73 */       byte[] buffer = new byte[256];
/*  74 */       int sz = 0;
/*     */       
/*  76 */       while ((sz = ins.read(buffer)) >= 0)
/*     */       {
/*  78 */         baos.write(buffer, 0, sz);
/*     */       }
/*     */       
/*  81 */       ins.close();
/*     */     }
/*  83 */     catch (FileSystemException e) {
/*     */       
/*  85 */       return new DWCommandResponse(false, (byte)-55, e.getMessage());
/*     */     }
/*  87 */     catch (IOException e) {
/*     */       
/*  89 */       return new DWCommandResponse(false, (byte)-54, e.getMessage());
/*     */     } finally {
/*     */ 
/*     */       
/*     */       try {
/*     */         
/*  95 */         if (ins != null) {
/*  96 */           ins.close();
/*     */         }
/*  98 */         if (fc != null) {
/*  99 */           fc.close();
/*     */         }
/* 101 */         if (fileobj != null) {
/* 102 */           fileobj.close();
/*     */         }
/*     */       }
/* 105 */       catch (IOException e) {
/*     */ 
/*     */         
/* 108 */         e.printStackTrace();
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 113 */     return new DWCommandResponse(baos.toByteArray());
/*     */   }
/*     */   
/*     */   public boolean validate(String cmdline) {
/* 117 */     return true;
/*     */   }
/*     */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/dwcommands/DWCmdServerList.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */