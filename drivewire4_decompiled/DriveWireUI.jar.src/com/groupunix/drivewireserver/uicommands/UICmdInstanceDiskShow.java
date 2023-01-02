/*     */ package com.groupunix.drivewireserver.uicommands;
/*     */ 
/*     */ import com.groupunix.drivewireserver.DWUIClientThread;
/*     */ import com.groupunix.drivewireserver.DriveWireServer;
/*     */ import com.groupunix.drivewireserver.dwcommands.DWCommand;
/*     */ import com.groupunix.drivewireserver.dwcommands.DWCommandResponse;
/*     */ import com.groupunix.drivewireserver.dwexceptions.DWDriveNotLoadedException;
/*     */ import com.groupunix.drivewireserver.dwexceptions.DWDriveNotValidException;
/*     */ import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocolHandler;
/*     */ import java.util.Iterator;
/*     */ import org.apache.commons.configuration.HierarchicalConfiguration;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class UICmdInstanceDiskShow
/*     */   extends DWCommand
/*     */ {
/*     */   static final String command = "show";
/*     */   private DWUIClientThread uiref;
/*     */   
/*     */   public UICmdInstanceDiskShow(DWUIClientThread dwuiClientThread) {
/*  25 */     this.uiref = dwuiClientThread;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getCommand() {
/*  30 */     return "show";
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public DWCommandResponse parse(String cmdline) {
/*  36 */     String res = new String();
/*     */ 
/*     */     
/*  39 */     DWProtocolHandler dwProto = (DWProtocolHandler)DriveWireServer.getHandler(this.uiref.getInstance());
/*     */     
/*  41 */     if (cmdline.length() == 0) {
/*     */       
/*  43 */       if (dwProto.getDiskDrives() == null)
/*     */       {
/*  45 */         return new DWCommandResponse(false, (byte)110, "Disk drives are null, is server restarting?");
/*     */       }
/*     */       
/*  48 */       for (int i = 0; i < dwProto.getDiskDrives().getMaxDrives(); i++) {
/*     */         
/*  50 */         if (dwProto.getDiskDrives().isLoaded(i)) {
/*     */           
/*     */           try {
/*     */             
/*  54 */             res = res + i + "|" + dwProto.getDiskDrives().getDisk(i).getFilePath() + "\n";
/*     */           }
/*  56 */           catch (DWDriveNotLoadedException e) {
/*     */ 
/*     */             
/*  59 */             e.printStackTrace();
/*     */           }
/*  61 */           catch (DWDriveNotValidException e) {
/*     */ 
/*     */             
/*  64 */             e.printStackTrace();
/*     */           } 
/*     */         }
/*     */       } 
/*     */     } else {
/*     */ 
/*     */       
/*     */       try {
/*     */ 
/*     */ 
/*     */         
/*  75 */         int driveno = Integer.parseInt(cmdline);
/*     */         
/*  77 */         if (dwProto.getDiskDrives() != null && dwProto.getDiskDrives().isLoaded(driveno))
/*     */         {
/*  79 */           res = res + "*loaded: true\n";
/*     */           
/*  81 */           HierarchicalConfiguration disk = dwProto.getDiskDrives().getDisk(driveno).getParams();
/*     */           
/*  83 */           for (Iterator<String> itk = disk.getKeys(); itk.hasNext(); )
/*     */           {
/*  85 */             String option = itk.next();
/*     */             
/*  87 */             res = res + option + ": " + disk.getProperty(option) + "\n";
/*     */           }
/*     */         
/*     */         }
/*     */         else
/*     */         {
/*  93 */           res = res + "*loaded: false\n";
/*     */         }
/*     */       
/*  96 */       } catch (NumberFormatException e) {
/*     */         
/*  98 */         return new DWCommandResponse(false, (byte)10, "Non numeric drive number");
/*     */       }
/* 100 */       catch (DWDriveNotLoadedException e) {
/*     */         
/* 102 */         res = res + "*loaded: false\n";
/*     */       }
/* 104 */       catch (DWDriveNotValidException e) {
/*     */         
/* 106 */         return new DWCommandResponse(false, (byte)101, e.getMessage());
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 111 */     return new DWCommandResponse(res);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getShortHelp() {
/* 118 */     return "Show current disks";
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String getUsage() {
/* 124 */     return "ui instance disk show";
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean validate(String cmdline) {
/* 129 */     return true;
/*     */   }
/*     */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/uicommands/UICmdInstanceDiskShow.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */