/*     */ package com.groupunix.drivewireserver.dwcommands;
/*     */ 
/*     */ import com.groupunix.drivewireserver.dwexceptions.DWDriveNotLoadedException;
/*     */ import com.groupunix.drivewireserver.dwexceptions.DWDriveNotValidException;
/*     */ import com.groupunix.drivewireserver.dwexceptions.DWImageHasNoSourceException;
/*     */ import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocolHandler;
/*     */ import com.groupunix.drivewireserver.dwprotocolhandler.DWUtils;
/*     */ import java.io.IOException;
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DWCmdDiskWrite
/*     */   extends DWCommand
/*     */ {
/*     */   private DWProtocolHandler dwProto;
/*     */   
/*     */   public DWCmdDiskWrite(DWProtocolHandler dwProto, DWCommand parent) {
/*  18 */     setParentCmd(parent);
/*  19 */     this.dwProto = dwProto;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String getCommand() {
/*  25 */     return "write";
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getShortHelp() {
/*  32 */     return "Write disk image in drive #";
/*     */   }
/*     */ 
/*     */   
/*     */   public String getUsage() {
/*  37 */     return "dw disk write # [path]";
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public DWCommandResponse parse(String cmdline) {
/*  43 */     if (cmdline.length() == 0) {
/*  44 */       return new DWCommandResponse(false, (byte)10, "Syntax error");
/*     */     }
/*  46 */     String[] args = cmdline.split(" ");
/*     */     
/*  48 */     if (args.length == 1) {
/*     */       
/*     */       try {
/*     */         
/*  52 */         return doDiskWrite(this.dwProto.getDiskDrives().getDriveNoFromString(args[0]));
/*     */       }
/*  54 */       catch (DWDriveNotValidException e) {
/*     */         
/*  56 */         return new DWCommandResponse(false, (byte)101, e.getMessage());
/*     */       } 
/*     */     }
/*  59 */     if (args.length == 2) {
/*     */       
/*     */       try {
/*     */ 
/*     */         
/*  64 */         return doDiskWrite(this.dwProto.getDiskDrives().getDriveNoFromString(args[0]), args[1]);
/*     */       }
/*  66 */       catch (DWDriveNotValidException e) {
/*     */ 
/*     */         
/*  69 */         return new DWCommandResponse(false, (byte)101, "Invalid drive number.");
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*  74 */     return new DWCommandResponse(false, (byte)10, "Syntax error");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private DWCommandResponse doDiskWrite(int driveno) {
/*     */     try {
/*  86 */       this.dwProto.getDiskDrives().writeDisk(driveno);
/*     */       
/*  88 */       return new DWCommandResponse("Wrote disk #" + driveno + " to source image.");
/*     */     
/*     */     }
/*  91 */     catch (IOException e1) {
/*     */       
/*  93 */       return new DWCommandResponse(false, (byte)-54, e1.getMessage());
/*     */     }
/*  95 */     catch (DWDriveNotLoadedException e) {
/*     */       
/*  97 */       return new DWCommandResponse(false, (byte)102, e.getMessage());
/*     */     }
/*  99 */     catch (DWDriveNotValidException e) {
/*     */       
/* 101 */       return new DWCommandResponse(false, (byte)101, e.getMessage());
/*     */     }
/* 103 */     catch (DWImageHasNoSourceException e) {
/*     */       
/* 105 */       return new DWCommandResponse(false, (byte)-53, e.getMessage());
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private DWCommandResponse doDiskWrite(int driveno, String path) {
/* 113 */     path = DWUtils.convertStarToBang(path);
/*     */ 
/*     */     
/*     */     try {
/* 117 */       System.out.println("write " + path);
/*     */       
/* 119 */       this.dwProto.getDiskDrives().writeDisk(driveno, path);
/*     */       
/* 121 */       return new DWCommandResponse("Wrote disk #" + driveno + " to '" + path + "'");
/*     */     
/*     */     }
/* 124 */     catch (IOException e1) {
/*     */       
/* 126 */       return new DWCommandResponse(false, (byte)-54, e1.getMessage());
/*     */     }
/* 128 */     catch (DWDriveNotLoadedException e) {
/*     */       
/* 130 */       return new DWCommandResponse(false, (byte)102, e.getMessage());
/*     */     }
/* 132 */     catch (DWDriveNotValidException e) {
/*     */       
/* 134 */       return new DWCommandResponse(false, (byte)101, e.getMessage());
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean validate(String cmdline) {
/* 140 */     return true;
/*     */   }
/*     */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/dwcommands/DWCmdDiskWrite.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */