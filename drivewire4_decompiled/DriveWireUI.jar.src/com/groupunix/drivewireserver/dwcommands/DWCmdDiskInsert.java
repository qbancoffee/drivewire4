/*     */ package com.groupunix.drivewireserver.dwcommands;
/*     */ 
/*     */ import com.groupunix.drivewireserver.dwexceptions.DWDriveAlreadyLoadedException;
/*     */ import com.groupunix.drivewireserver.dwexceptions.DWDriveNotValidException;
/*     */ import com.groupunix.drivewireserver.dwexceptions.DWImageFormatException;
/*     */ import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocolHandler;
/*     */ import com.groupunix.drivewireserver.dwprotocolhandler.DWUtils;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.IOException;
/*     */ import org.apache.commons.vfs.FileSystemException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DWCmdDiskInsert
/*     */   extends DWCommand
/*     */ {
/*     */   private DWProtocolHandler dwProto;
/*     */   
/*     */   public DWCmdDiskInsert(DWProtocolHandler dwProto, DWCommand parent) {
/*  21 */     setParentCmd(parent);
/*  22 */     this.dwProto = dwProto;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getCommand() {
/*  27 */     return "insert";
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public DWCommandResponse parse(String cmdline) {
/*  33 */     String[] args = cmdline.split(" ");
/*     */     
/*  35 */     if (args.length > 1) {
/*     */       
/*     */       try {
/*     */ 
/*     */         
/*  40 */         return doDiskInsert(this.dwProto.getDiskDrives().getDriveNoFromString(args[0]), DWUtils.dropFirstToken(cmdline));
/*     */       }
/*  42 */       catch (DWDriveNotValidException e) {
/*     */         
/*  44 */         return new DWCommandResponse(false, (byte)101, e.getMessage());
/*     */       } 
/*     */     }
/*     */     
/*  48 */     return new DWCommandResponse(false, (byte)10, "Syntax error");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private DWCommandResponse doDiskInsert(int driveno, String path) {
/*  58 */     path = DWUtils.convertStarToBang(path);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     try {
/*  65 */       this.dwProto.getDiskDrives().LoadDiskFromFile(driveno, path);
/*     */       
/*  67 */       return new DWCommandResponse("Disk inserted in drive " + driveno + ".");
/*     */     
/*     */     }
/*  70 */     catch (DWDriveNotValidException e) {
/*     */       
/*  72 */       return new DWCommandResponse(false, (byte)101, e.getMessage());
/*     */     
/*     */     }
/*  75 */     catch (FileSystemException e) {
/*     */       
/*  77 */       return new DWCommandResponse(false, (byte)-55, e.getMessage());
/*     */     
/*     */     }
/*  80 */     catch (DWDriveAlreadyLoadedException e) {
/*     */       
/*  82 */       return new DWCommandResponse(false, (byte)103, e.getMessage());
/*     */     }
/*  84 */     catch (FileNotFoundException e) {
/*     */       
/*  86 */       return new DWCommandResponse(false, (byte)-53, e.getMessage());
/*     */     }
/*  88 */     catch (IOException e) {
/*     */       
/*  90 */       return new DWCommandResponse(false, (byte)-54, e.getMessage());
/*     */     }
/*  92 */     catch (DWImageFormatException e) {
/*     */       
/*  94 */       return new DWCommandResponse(false, (byte)104, e.getMessage());
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getShortHelp() {
/* 105 */     return "Load disk into drive #";
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String getUsage() {
/* 111 */     return "dw disk insert # path";
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean validate(String cmdline) {
/* 116 */     return true;
/*     */   }
/*     */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/dwcommands/DWCmdDiskInsert.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */