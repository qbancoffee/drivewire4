/*     */ package com.groupunix.drivewireserver.dwcommands;
/*     */ 
/*     */ import com.groupunix.drivewireserver.dwdisk.DWDECBFileSystem;
/*     */ import com.groupunix.drivewireserver.dwexceptions.DWDECBFileSystemBadFilenameException;
/*     */ import com.groupunix.drivewireserver.dwexceptions.DWDECBFileSystemFileNotFoundException;
/*     */ import com.groupunix.drivewireserver.dwexceptions.DWDECBFileSystemFullException;
/*     */ import com.groupunix.drivewireserver.dwexceptions.DWDECBFileSystemInvalidFATException;
/*     */ import com.groupunix.drivewireserver.dwexceptions.DWDriveNotLoadedException;
/*     */ import com.groupunix.drivewireserver.dwexceptions.DWDriveNotValidException;
/*     */ import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocolHandler;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import org.apache.commons.vfs.FileContent;
/*     */ import org.apache.commons.vfs.FileObject;
/*     */ import org.apache.commons.vfs.VFS;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DWCmdDiskDosAdd
/*     */   extends DWCommand
/*     */ {
/*     */   private DWProtocolHandler dwProto;
/*     */   
/*     */   public DWCmdDiskDosAdd(DWProtocolHandler dwProto, DWCommand parent) {
/*  26 */     setParentCmd(parent);
/*  27 */     this.dwProto = dwProto;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getCommand() {
/*  32 */     return "add";
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public DWCommandResponse parse(String cmdline) {
/*  38 */     String[] args = cmdline.split(" ");
/*     */     
/*  40 */     if (args.length == 2) {
/*     */       
/*     */       try {
/*     */         
/*  44 */         return doDiskDosAdd(this.dwProto.getDiskDrives().getDriveNoFromString(args[0]), args[1]);
/*     */       
/*     */       }
/*  47 */       catch (DWDriveNotValidException e) {
/*     */         
/*  49 */         return new DWCommandResponse(false, (byte)101, e.getMessage());
/*     */       }
/*  51 */       catch (DWDriveNotLoadedException e) {
/*     */         
/*  53 */         return new DWCommandResponse(false, (byte)102, e.getMessage());
/*     */       }
/*  55 */       catch (DWDECBFileSystemFullException e) {
/*     */         
/*  57 */         return new DWCommandResponse(false, (byte)-55, e.getMessage());
/*     */       }
/*  59 */       catch (DWDECBFileSystemBadFilenameException e) {
/*     */         
/*  61 */         return new DWCommandResponse(false, (byte)-55, e.getMessage());
/*     */       }
/*  63 */       catch (IOException e) {
/*     */         
/*  65 */         return new DWCommandResponse(false, (byte)-54, e.getMessage());
/*     */       }
/*  67 */       catch (DWDECBFileSystemFileNotFoundException e) {
/*     */         
/*  69 */         return new DWCommandResponse(false, (byte)-53, e.getMessage());
/*     */       }
/*  71 */       catch (DWDECBFileSystemInvalidFATException e) {
/*     */         
/*  73 */         return new DWCommandResponse(false, (byte)-55, e.getMessage());
/*     */       } 
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*  79 */     return new DWCommandResponse(false, (byte)10, "Syntax error");
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private DWCommandResponse doDiskDosAdd(int driveno, String path) throws DWDriveNotLoadedException, DWDriveNotValidException, DWDECBFileSystemFullException, DWDECBFileSystemBadFilenameException, IOException, DWDECBFileSystemFileNotFoundException, DWDECBFileSystemInvalidFATException {
/*  85 */     DWDECBFileSystem decbfs = new DWDECBFileSystem(this.dwProto.getDiskDrives().getDisk(driveno));
/*     */     
/*  87 */     FileObject fileobj = VFS.getManager().resolveFile(path);
/*     */     
/*  89 */     if (fileobj.exists() && fileobj.isReadable()) {
/*     */ 
/*     */       
/*  92 */       FileContent fc = fileobj.getContent();
/*  93 */       long fobjsize = fc.getSize();
/*     */ 
/*     */       
/*  96 */       if (fobjsize > 2147483647L) {
/*  97 */         throw new DWDECBFileSystemFullException("File too big, maximum size is 2147483647 bytes.");
/*     */       }
/*     */ 
/*     */       
/* 101 */       byte[] content = new byte[(int)fobjsize];
/*     */       
/* 103 */       if (content.length > 0) {
/*     */         
/* 105 */         int readres = 0;
/* 106 */         InputStream fis = fc.getInputStream();
/*     */         
/* 108 */         while (readres < content.length) {
/* 109 */           readres += fis.read(content, readres, content.length - readres);
/*     */         }
/* 111 */         fis.close();
/*     */       } 
/*     */       
/* 114 */       decbfs.addFile(fileobj.getName().getBaseName().toUpperCase(), content);
/*     */     }
/*     */     else {
/*     */       
/* 118 */       throw new IOException("Unreadable source path");
/*     */     } 
/*     */     
/* 121 */     return new DWCommandResponse("File added to DOS disk.");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getShortHelp() {
/* 129 */     return "Add file to disk image with DOS filesystem";
/*     */   }
/*     */ 
/*     */   
/*     */   public String getUsage() {
/* 134 */     return "dw disk dos add # path";
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean validate(String cmdline) {
/* 139 */     return true;
/*     */   }
/*     */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/dwcommands/DWCmdDiskDosAdd.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */