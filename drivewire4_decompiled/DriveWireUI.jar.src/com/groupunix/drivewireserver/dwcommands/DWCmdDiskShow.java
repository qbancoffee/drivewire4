/*     */ package com.groupunix.drivewireserver.dwcommands;
/*     */ 
/*     */ import com.groupunix.drivewireserver.dwdisk.DWDisk;
/*     */ import com.groupunix.drivewireserver.dwexceptions.DWDriveNotLoadedException;
/*     */ import com.groupunix.drivewireserver.dwexceptions.DWDriveNotValidException;
/*     */ import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocolHandler;
/*     */ import com.groupunix.drivewireserver.dwprotocolhandler.DWUtils;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.Iterator;
/*     */ import org.apache.commons.configuration.HierarchicalConfiguration;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DWCmdDiskShow
/*     */   extends DWCommand
/*     */ {
/*     */   private DWProtocolHandler dwProto;
/*     */   
/*     */   public DWCmdDiskShow(DWProtocolHandler dwProto, DWCommand parent) {
/*  23 */     setParentCmd(parent);
/*  24 */     this.dwProto = dwProto;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getCommand() {
/*  29 */     return "show";
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getShortHelp() {
/*  36 */     return "Show current disk details";
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String getUsage() {
/*  42 */     return "dw disk show [#]";
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DWCommandResponse parse(String cmdline) {
/*  49 */     if (cmdline.length() == 0)
/*     */     {
/*  51 */       return doDiskShow();
/*     */     }
/*     */ 
/*     */     
/*  55 */     String[] args = cmdline.split(" ");
/*     */     
/*  57 */     if (args.length == 1) {
/*     */       
/*     */       try {
/*     */         
/*  61 */         return doDiskShow(this.dwProto.getDiskDrives().getDriveNoFromString(args[0]));
/*     */       }
/*  63 */       catch (DWDriveNotValidException e) {
/*     */         
/*  65 */         return new DWCommandResponse(false, (byte)101, e.getMessage());
/*     */       } 
/*     */     }
/*     */     
/*  69 */     return new DWCommandResponse(false, (byte)10, "Syntax error");
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
/*     */ 
/*     */ 
/*     */   
/*     */   private DWCommandResponse doDiskShow(int driveno) {
/*     */     try {
/*  85 */       DWDisk disk = this.dwProto.getDiskDrives().getDisk(driveno);
/*     */       
/*  87 */       String text = "Details for disk in drive #" + driveno + ":\r\n\r\n";
/*     */       
/*  89 */       text = text + DWUtils.shortenLocalURI(disk.getFilePath()) + "\r\n";
/*     */       
/*  91 */       text = text + "\r\n";
/*     */ 
/*     */ 
/*     */       
/*  95 */       if (disk.getParams().containsKey("_readerrors"))
/*     */       {
/*  97 */         text = text + "This drive reports " + disk.getParams().getInt("_readerrors") + " read errors.\r\n";
/*     */       }
/*     */       
/* 100 */       if (disk.getParams().containsKey("_writeerrors"))
/*     */       {
/* 102 */         text = text + "This drive reports " + disk.getParams().getInt("_writeerrors") + " write errors.\r\n";
/*     */       }
/*     */       
/* 105 */       if (disk.getDirtySectors() > 0)
/*     */       {
/* 107 */         text = text + "This drive reports " + disk.getDirtySectors() + " dirty sectors.\r\n";
/*     */       }
/*     */ 
/*     */       
/* 111 */       HierarchicalConfiguration params = disk.getParams();
/*     */       
/* 113 */       ArrayList<String> ignores = new ArrayList<String>();
/* 114 */       ArrayList<String> syss = new ArrayList<String>();
/* 115 */       ArrayList<String> usrs = new ArrayList<String>();
/*     */       
/* 117 */       ignores.add("_readerrors");
/* 118 */       ignores.add("_writeerrors");
/* 119 */       ignores.add("_path");
/* 120 */       ignores.add("_last_modified");
/*     */ 
/*     */       
/* 123 */       for (Iterator<String> itk = params.getKeys(); itk.hasNext(); ) {
/*     */         
/* 125 */         String p = itk.next();
/*     */         
/* 127 */         if (!ignores.contains(p)) {
/*     */           
/* 129 */           if (p.startsWith("_")) {
/* 130 */             syss.add(p.substring(1) + ": " + params.getProperty(p)); continue;
/*     */           } 
/* 132 */           usrs.add(p + ": " + params.getProperty(p));
/*     */         } 
/*     */       } 
/*     */       
/* 136 */       Collections.sort(syss);
/* 137 */       Collections.sort(usrs);
/*     */       
/* 139 */       text = text + "System params:\r\n";
/* 140 */       text = text + DWCommandList.colLayout(syss, this.dwProto.getCMDCols());
/*     */       
/* 142 */       text = text + "\r\nUser params:\r\n";
/* 143 */       text = text + DWCommandList.colLayout(usrs, this.dwProto.getCMDCols());
/*     */       
/* 145 */       return new DWCommandResponse(text);
/*     */ 
/*     */     
/*     */     }
/* 149 */     catch (DWDriveNotValidException e) {
/*     */       
/* 151 */       return new DWCommandResponse(false, (byte)101, e.getMessage());
/*     */     
/*     */     }
/* 154 */     catch (DWDriveNotLoadedException e) {
/*     */       
/* 156 */       return new DWCommandResponse(false, (byte)102, e.getMessage());
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private DWCommandResponse doDiskShow() {
/* 166 */     String text = new String();
/*     */     
/* 168 */     text = "\r\nCurrent DriveWire disks:\r\n\r\n";
/*     */ 
/*     */     
/* 171 */     for (int i = 0; i < this.dwProto.getDiskDrives().getMaxDrives(); i++) {
/*     */ 
/*     */ 
/*     */       
/* 175 */       if (this.dwProto.getDiskDrives().isLoaded(i)) {
/*     */         
/*     */         try {
/*     */           
/* 179 */           text = text + String.format("X%-3d", new Object[] { Integer.valueOf(i) });
/* 180 */           if (this.dwProto.getDiskDrives().getDisk(i).getWriteProtect()) {
/* 181 */             text = text + "*";
/*     */           } else {
/* 183 */             text = text + " ";
/* 184 */           }  text = text + DWUtils.shortenLocalURI(this.dwProto.getDiskDrives().getDisk(i).getFilePath()) + "\r\n";
/*     */         }
/* 186 */         catch (DWDriveNotLoadedException e) {
/*     */ 
/*     */         
/*     */         }
/* 190 */         catch (DWDriveNotValidException e) {}
/*     */       }
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 198 */     return new DWCommandResponse(text);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean validate(String cmdline) {
/* 206 */     return true;
/*     */   }
/*     */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/dwcommands/DWCmdDiskShow.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */