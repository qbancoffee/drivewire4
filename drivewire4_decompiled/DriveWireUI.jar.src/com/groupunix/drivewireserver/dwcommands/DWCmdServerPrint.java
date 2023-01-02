/*     */ package com.groupunix.drivewireserver.dwcommands;
/*     */ 
/*     */ import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocol;
/*     */ import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocolHandler;
/*     */ import com.groupunix.drivewireserver.dwprotocolhandler.DWUtils;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DWCmdServerPrint
/*     */   extends DWCommand
/*     */ {
/*     */   private DWProtocol dwProto;
/*     */   
/*     */   public DWCmdServerPrint(DWProtocol dwProto, DWCommand parent) {
/*  27 */     setParentCmd(parent);
/*  28 */     this.dwProto = dwProto;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String getCommand() {
/*  34 */     return "print";
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String getShortHelp() {
/*  40 */     return "Print contents of file on server";
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String getUsage() {
/*  46 */     return "dw server print URI/path";
/*     */   }
/*     */ 
/*     */   
/*     */   public DWCommandResponse parse(String cmdline) {
/*  51 */     if (cmdline.length() == 0)
/*     */     {
/*  53 */       return new DWCommandResponse(false, (byte)10, "dw server print requires a URI or local file path as an argument");
/*     */     }
/*  55 */     return doPrint(cmdline);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private DWCommandResponse doPrint(String path) {
/*  63 */     InputStream ins = null;
/*  64 */     FileObject fileobj = null;
/*  65 */     FileContent fc = null;
/*     */ 
/*     */     
/*     */     try {
/*  69 */       FileSystemManager fsManager = VFS.getManager();
/*     */       
/*  71 */       path = DWUtils.convertStarToBang(path);
/*     */       
/*  73 */       fileobj = fsManager.resolveFile(path);
/*     */       
/*  75 */       fc = fileobj.getContent();
/*     */       
/*  77 */       ins = fc.getInputStream();
/*     */       
/*  79 */       byte data = (byte)ins.read();
/*     */       
/*  81 */       while (data >= 0) {
/*     */         
/*  83 */         ((DWProtocolHandler)this.dwProto).getVPrinter().addByte(data);
/*  84 */         data = (byte)ins.read();
/*     */       } 
/*     */       
/*  87 */       ins.close();
/*  88 */       ((DWProtocolHandler)this.dwProto).getVPrinter().flush();
/*     */ 
/*     */     
/*     */     }
/*  92 */     catch (FileSystemException e) {
/*     */       
/*  94 */       return new DWCommandResponse(false, (byte)-55, e.getMessage());
/*     */     }
/*  96 */     catch (IOException e) {
/*     */       
/*  98 */       return new DWCommandResponse(false, (byte)-54, e.getMessage());
/*     */     } finally {
/*     */ 
/*     */       
/*     */       try {
/*     */         
/* 104 */         if (ins != null) {
/* 105 */           ins.close();
/*     */         }
/* 107 */         if (fc != null) {
/* 108 */           fc.close();
/*     */         }
/* 110 */         if (fileobj != null) {
/* 111 */           fileobj.close();
/*     */         }
/*     */       }
/* 114 */       catch (IOException e) {
/*     */ 
/*     */         
/* 117 */         e.printStackTrace();
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 122 */     return new DWCommandResponse("Sent item to printer");
/*     */   }
/*     */   
/*     */   public boolean validate(String cmdline) {
/* 126 */     return true;
/*     */   }
/*     */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/dwcommands/DWCmdServerPrint.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */