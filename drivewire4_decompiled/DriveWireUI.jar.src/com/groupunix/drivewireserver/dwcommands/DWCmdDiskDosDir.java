/*     */ package com.groupunix.drivewireserver.dwcommands;
/*     */ 
/*     */ import com.groupunix.drivewireserver.dwdisk.DWDECBFileSystem;
/*     */ import com.groupunix.drivewireserver.dwdisk.DWDECBFileSystemDirEntry;
/*     */ import com.groupunix.drivewireserver.dwexceptions.DWDriveNotLoadedException;
/*     */ import com.groupunix.drivewireserver.dwexceptions.DWDriveNotValidException;
/*     */ import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocolHandler;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DWCmdDiskDosDir
/*     */   extends DWCommand
/*     */ {
/*     */   private DWProtocolHandler dwProto;
/*     */   
/*     */   public DWCmdDiskDosDir(DWProtocolHandler dwProto, DWCommand parent) {
/*  20 */     setParentCmd(parent);
/*  21 */     this.dwProto = dwProto;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getCommand() {
/*  26 */     return "dir";
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public DWCommandResponse parse(String cmdline) {
/*  32 */     String[] args = cmdline.split(" ");
/*     */     
/*  34 */     if (args.length == 1) {
/*     */       
/*     */       try {
/*     */         
/*  38 */         return doDiskDosDir(this.dwProto.getDiskDrives().getDriveNoFromString(args[0]));
/*     */       }
/*  40 */       catch (DWDriveNotValidException e) {
/*     */         
/*  42 */         return new DWCommandResponse(false, (byte)101, e.getMessage());
/*     */       }
/*  44 */       catch (DWDriveNotLoadedException e) {
/*     */         
/*  46 */         return new DWCommandResponse(false, (byte)102, e.getMessage());
/*     */       }
/*  48 */       catch (IOException e) {
/*     */         
/*  50 */         return new DWCommandResponse(false, (byte)-54, e.getMessage());
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*  55 */     return new DWCommandResponse(false, (byte)10, "Syntax error");
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private DWCommandResponse doDiskDosDir(int driveno) throws DWDriveNotLoadedException, DWDriveNotValidException, IOException {
/*  61 */     String res = "Directory of drive " + driveno + "\r\n\r\n";
/*     */     
/*  63 */     DWDECBFileSystem tmp = new DWDECBFileSystem(this.dwProto.getDiskDrives().getDisk(driveno));
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  68 */     ArrayList<String> dir = new ArrayList<String>();
/*     */     
/*  70 */     for (DWDECBFileSystemDirEntry e : tmp.getDirectory()) {
/*     */       
/*  72 */       if (e.isUsed()) {
/*  73 */         dir.add(e.getFileName() + "." + e.getFileExt());
/*     */       }
/*     */     } 
/*  76 */     Collections.sort(dir);
/*     */     
/*  78 */     res = res + DWCommandList.colLayout(dir, this.dwProto.getCMDCols());
/*     */ 
/*     */ 
/*     */     
/*  82 */     return new DWCommandResponse(res);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getShortHelp() {
/*  91 */     return "Show DOS directory of disk in drive #";
/*     */   }
/*     */ 
/*     */   
/*     */   public String getUsage() {
/*  96 */     return "dw disk dos dir #";
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean validate(String cmdline) {
/* 101 */     return true;
/*     */   }
/*     */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/dwcommands/DWCmdDiskDosDir.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */