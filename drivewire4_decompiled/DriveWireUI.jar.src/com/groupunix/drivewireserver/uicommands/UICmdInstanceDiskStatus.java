/*     */ package com.groupunix.drivewireserver.uicommands;
/*     */ 
/*     */ import com.groupunix.drivewireserver.DWUIClientThread;
/*     */ import com.groupunix.drivewireserver.DriveWireServer;
/*     */ import com.groupunix.drivewireserver.dwcommands.DWCommand;
/*     */ import com.groupunix.drivewireserver.dwcommands.DWCommandResponse;
/*     */ import com.groupunix.drivewireserver.dwexceptions.DWDriveNotLoadedException;
/*     */ import com.groupunix.drivewireserver.dwexceptions.DWDriveNotValidException;
/*     */ import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocolHandler;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class UICmdInstanceDiskStatus
/*     */   extends DWCommand
/*     */ {
/*     */   static final String command = "status";
/*     */   private DWUIClientThread uiref;
/*     */   
/*     */   public UICmdInstanceDiskStatus(DWUIClientThread dwuiClientThread) {
/*  21 */     this.uiref = dwuiClientThread;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getCommand() {
/*  26 */     return "status";
/*     */   }
/*     */ 
/*     */   
/*     */   public DWCommandResponse parse(String cmdline) {
/*  31 */     String res = new String();
/*     */ 
/*     */     
/*  34 */     DWProtocolHandler dwProto = (DWProtocolHandler)DriveWireServer.getHandler(this.uiref.getInstance());
/*     */     
/*  36 */     if (cmdline.length() > 0) {
/*     */       
/*  38 */       if (dwProto != null && dwProto.getDiskDrives() != null) {
/*     */         
/*  40 */         res = "serial: " + dwProto.getDiskDrives().getDiskDriveSerial() + "\n";
/*     */ 
/*     */ 
/*     */ 
/*     */         
/*     */         try {
/*  46 */           int driveno = Integer.parseInt(cmdline);
/*     */           
/*  48 */           if (dwProto.getDiskDrives() != null && dwProto.getDiskDrives().isLoaded(driveno))
/*     */           {
/*  50 */             res = res + "loaded: true\n";
/*  51 */             res = res + "sectors: " + dwProto.getDiskDrives().getDisk(driveno).getParams().getInt("_sectors", 0) + "\n";
/*  52 */             res = res + "dirty: " + dwProto.getDiskDrives().getDisk(driveno).getParams().getInt("_dirty", 0) + "\n";
/*  53 */             res = res + "lsn: " + dwProto.getDiskDrives().getDisk(driveno).getParams().getInt("_lsn", 0) + "\n";
/*  54 */             res = res + "reads: " + dwProto.getDiskDrives().getDisk(driveno).getParams().getInt("_reads", 0) + "\n";
/*  55 */             res = res + "writes: " + dwProto.getDiskDrives().getDisk(driveno).getParams().getInt("_writes", 0) + "\n";
/*     */           
/*     */           }
/*     */           else
/*     */           {
/*  60 */             res = res + "loaded: false\n";
/*     */           }
/*     */         
/*  63 */         } catch (NumberFormatException e) {
/*     */           
/*  65 */           return new DWCommandResponse(false, (byte)10, "Non numeric drive number");
/*     */         }
/*  67 */         catch (DWDriveNotLoadedException e) {
/*     */           
/*  69 */           res = res + "loaded: false\n";
/*     */         }
/*  71 */         catch (DWDriveNotValidException e) {
/*     */           
/*  73 */           return new DWCommandResponse(false, (byte)101, e.getMessage());
/*     */         }
/*     */       
/*     */       } else {
/*     */         
/*  78 */         return new DWCommandResponse(false, (byte)-115, "Null handler or diskset (is server restarting?)");
/*     */       }
/*     */     
/*     */     }
/*     */     else {
/*     */       
/*  84 */       return new DWCommandResponse(false, (byte)10, "Must specify drive number");
/*     */     } 
/*     */     
/*  87 */     return new DWCommandResponse(res);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getShortHelp() {
/*  94 */     return "Show current disks";
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String getUsage() {
/* 100 */     return "ui instance disk show";
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean validate(String cmdline) {
/* 105 */     return true;
/*     */   }
/*     */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/uicommands/UICmdInstanceDiskStatus.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */