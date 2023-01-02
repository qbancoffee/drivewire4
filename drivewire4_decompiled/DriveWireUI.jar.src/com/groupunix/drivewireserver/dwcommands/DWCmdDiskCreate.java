/*     */ package com.groupunix.drivewireserver.dwcommands;
/*     */ 
/*     */ import com.groupunix.drivewireserver.dwexceptions.DWDriveAlreadyLoadedException;
/*     */ import com.groupunix.drivewireserver.dwexceptions.DWDriveNotLoadedException;
/*     */ import com.groupunix.drivewireserver.dwexceptions.DWDriveNotValidException;
/*     */ import com.groupunix.drivewireserver.dwexceptions.DWImageFormatException;
/*     */ import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocolHandler;
/*     */ import com.groupunix.drivewireserver.dwprotocolhandler.DWUtils;
/*     */ import java.io.IOException;
/*     */ import org.apache.commons.vfs.FileObject;
/*     */ import org.apache.commons.vfs.FileSystemManager;
/*     */ import org.apache.commons.vfs.VFS;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DWCmdDiskCreate
/*     */   extends DWCommand
/*     */ {
/*     */   private DWProtocolHandler dwProto;
/*     */   
/*     */   public DWCmdDiskCreate(DWProtocolHandler dwProto, DWCommand parent) {
/*  23 */     setParentCmd(parent);
/*  24 */     this.dwProto = dwProto;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getCommand() {
/*  29 */     return "create";
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getShortHelp() {
/*  36 */     return "Create new disk image";
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String getUsage() {
/*  42 */     return "dw disk create # [path]";
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public DWCommandResponse parse(String cmdline) {
/*  48 */     String[] args = cmdline.split(" ");
/*     */     
/*  50 */     if (args.length == 1) {
/*     */       
/*     */       try {
/*     */         
/*  54 */         return doDiskCreate(this.dwProto.getDiskDrives().getDriveNoFromString(args[0]));
/*     */       }
/*  56 */       catch (DWDriveNotValidException e) {
/*     */         
/*  58 */         return new DWCommandResponse(false, (byte)101, e.getMessage());
/*     */       } 
/*     */     }
/*     */     
/*  62 */     if (args.length > 1) {
/*     */       
/*     */       try {
/*     */ 
/*     */ 
/*     */         
/*  68 */         return doDiskCreate(this.dwProto.getDiskDrives().getDriveNoFromString(args[0]), DWUtils.dropFirstToken(cmdline));
/*     */       }
/*  70 */       catch (DWDriveNotValidException e) {
/*     */         
/*  72 */         return new DWCommandResponse(false, (byte)101, e.getMessage());
/*     */       } 
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*  78 */     return new DWCommandResponse(false, (byte)10, "Syntax error");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private DWCommandResponse doDiskCreate(int driveno, String filepath) {
/*     */     try {
/*  91 */       FileSystemManager fsManager = VFS.getManager();
/*  92 */       FileObject fileobj = fsManager.resolveFile(filepath);
/*     */       
/*  94 */       if (fileobj.exists()) {
/*     */         
/*  96 */         fileobj.close();
/*  97 */         throw new IOException("File already exists");
/*     */       } 
/*     */       
/* 100 */       fileobj.createFile();
/*     */       
/* 102 */       if (this.dwProto.getDiskDrives().isLoaded(driveno)) {
/* 103 */         this.dwProto.getDiskDrives().EjectDisk(driveno);
/*     */       }
/* 105 */       this.dwProto.getDiskDrives().LoadDiskFromFile(driveno, filepath);
/*     */       
/* 107 */       return new DWCommandResponse("New disk image created for drive " + driveno + ".");
/*     */     
/*     */     }
/* 110 */     catch (IOException e1) {
/*     */       
/* 112 */       return new DWCommandResponse(false, (byte)-54, e1.getMessage());
/*     */     
/*     */     }
/* 115 */     catch (DWDriveNotValidException e) {
/*     */       
/* 117 */       return new DWCommandResponse(false, (byte)101, e.getMessage());
/*     */     }
/* 119 */     catch (DWDriveAlreadyLoadedException e) {
/*     */       
/* 121 */       return new DWCommandResponse(false, (byte)103, e.getMessage());
/*     */     }
/* 123 */     catch (DWDriveNotLoadedException e) {
/*     */       
/* 125 */       return new DWCommandResponse(false, (byte)102, e.getMessage());
/*     */     }
/* 127 */     catch (DWImageFormatException e) {
/*     */       
/* 129 */       return new DWCommandResponse(false, (byte)104, e.getMessage());
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private DWCommandResponse doDiskCreate(int driveno) {
/*     */     try {
/* 141 */       if (this.dwProto.getDiskDrives().isLoaded(driveno)) {
/* 142 */         this.dwProto.getDiskDrives().EjectDisk(driveno);
/*     */       }
/* 144 */       this.dwProto.getDiskDrives().createDisk(driveno);
/*     */     
/*     */     }
/* 147 */     catch (DWDriveNotValidException e) {
/*     */       
/* 149 */       return new DWCommandResponse(false, (byte)101, e.getMessage());
/*     */     }
/* 151 */     catch (DWDriveNotLoadedException e) {
/*     */ 
/*     */     
/*     */     }
/* 155 */     catch (DWDriveAlreadyLoadedException e) {
/*     */       
/* 157 */       return new DWCommandResponse(false, (byte)103, e.getMessage());
/*     */     } 
/*     */     
/* 160 */     return new DWCommandResponse("New image created for drive " + driveno + ".");
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean validate(String cmdline) {
/* 166 */     return true;
/*     */   }
/*     */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/dwcommands/DWCmdDiskCreate.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */