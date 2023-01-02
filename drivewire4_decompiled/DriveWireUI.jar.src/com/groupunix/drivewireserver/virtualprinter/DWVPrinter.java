/*     */ package com.groupunix.drivewireserver.virtualprinter;
/*     */ 
/*     */ import com.groupunix.drivewireserver.dwexceptions.DWPrinterFileError;
/*     */ import com.groupunix.drivewireserver.dwexceptions.DWPrinterNotDefinedException;
/*     */ import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocol;
/*     */ import java.io.IOException;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.apache.commons.configuration.HierarchicalConfiguration;
/*     */ import org.apache.log4j.Logger;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DWVPrinter
/*     */ {
/*  19 */   private ArrayList<DWVPrinterDriver> drivers = new ArrayList<DWVPrinterDriver>();
/*     */ 
/*     */   
/*  22 */   private static final Logger logger = Logger.getLogger("DWServer.DWVPrinter");
/*     */   
/*     */   private DWProtocol dwProto;
/*     */ 
/*     */   
/*     */   public DWVPrinter(DWProtocol dwProto) {
/*  28 */     this.dwProto = dwProto;
/*     */     
/*  30 */     logger.debug("dwprinter init for handler #" + dwProto.getHandlerNo());
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  35 */     List<HierarchicalConfiguration> printers = dwProto.getConfig().configurationsAt("Printer");
/*     */     
/*  37 */     for (HierarchicalConfiguration printer : printers) {
/*     */       
/*  39 */       if (printer.containsKey("[@name]") && printer.containsKey("Driver")) {
/*     */ 
/*     */         
/*     */         try {
/*     */ 
/*     */ 
/*     */           
/*  46 */           Constructor<DWVPrinterDriver> pconst = (Constructor)Class.forName("com.groupunix.drivewireserver.virtualprinter.DWVPrinter" + printer.getString("Driver"), true, getClass().getClassLoader()).getConstructor(new Class[] { Class.forName("org.apache.commons.configuration.HierarchicalConfiguration") });
/*  47 */           this.drivers.add(pconst.newInstance(new Object[] { printer }));
/*     */           
/*  49 */           logger.info("init printer '" + printer.getString("[@name]") + "' using driver '" + printer.getString("Driver") + "'");
/*     */ 
/*     */         
/*     */         }
/*  53 */         catch (ClassNotFoundException e) {
/*     */           
/*  55 */           logger.warn("Invalid printer definition '" + printer.getString("[@name]") + "' in config, '" + printer.getString("Driver") + "' is not a known driver.");
/*     */         }
/*  57 */         catch (InstantiationException e) {
/*     */           
/*  59 */           logger.warn("InstantiationException on printer '" + printer.getString("[@name]") + "': " + e.getMessage());
/*     */         }
/*  61 */         catch (IllegalAccessException e) {
/*     */           
/*  63 */           logger.warn("IllegalAccessException on printer '" + printer.getString("[@name]") + "': " + e.getMessage());
/*     */         }
/*  65 */         catch (SecurityException e) {
/*     */           
/*  67 */           logger.warn("SecurityException on printer '" + printer.getString("[@name]") + "': " + e.getMessage());
/*     */         }
/*  69 */         catch (NoSuchMethodException e) {
/*     */           
/*  71 */           logger.warn("NoSuchMethodException on printer '" + printer.getString("[@name]") + "': " + e.getMessage());
/*     */         }
/*  73 */         catch (IllegalArgumentException e) {
/*     */           
/*  75 */           logger.warn("IllegalArgumentException on printer '" + printer.getString("[@name]") + "': " + e.getMessage());
/*     */         }
/*  77 */         catch (InvocationTargetException e) {
/*     */           
/*  79 */           logger.warn("InvocationTargetException on printer '" + printer.getString("[@name]") + "': " + e.getMessage());
/*     */         } 
/*     */         
/*     */         continue;
/*     */       } 
/*     */       
/*  85 */       logger.warn("Invalid printer definition in config, name " + printer.getString("[@name]", "not defined") + " driver " + printer.getString("Driver", "not defined"));
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addByte(byte data) {
/*     */     try {
/*  95 */       getCurrentDriver().addByte(data);
/*     */     }
/*  97 */     catch (IOException e) {
/*     */       
/*  99 */       logger.warn("error writing to print buffer: " + e.getMessage());
/*     */     }
/* 101 */     catch (DWPrinterNotDefinedException e) {
/*     */       
/* 103 */       logger.warn("error writing to print buffer: " + e.getMessage());
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private DWVPrinterDriver getCurrentDriver() throws DWPrinterNotDefinedException {
/* 109 */     String curprinter = this.dwProto.getConfig().getString("CurrentPrinter", null);
/*     */     
/* 111 */     if (curprinter == null)
/*     */     {
/* 113 */       throw new DWPrinterNotDefinedException("No current printer is set in the configuration");
/*     */     }
/*     */     
/* 116 */     for (DWVPrinterDriver drv : this.drivers) {
/*     */       
/* 118 */       if (drv.getPrinterName().equals(curprinter)) {
/* 119 */         return drv;
/*     */       }
/*     */     } 
/* 122 */     throw new DWPrinterNotDefinedException("Cannot find printer named '" + curprinter + "'");
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void flush() {
/* 128 */     logger.debug("Printer flush");
/*     */ 
/*     */ 
/*     */     
/* 132 */     Thread flusher = new Thread(new Runnable()
/*     */         {
/*     */ 
/*     */           
/*     */           public void run()
/*     */           {
/* 138 */             Thread.currentThread().setName("printflush-" + Thread.currentThread().getId());
/* 139 */             Thread.currentThread().setPriority(1);
/* 140 */             DWVPrinter.logger.debug("flush thread run");
/*     */             
/*     */             try {
/* 143 */               DWVPrinter.this.getCurrentDriver().flush();
/*     */             }
/* 145 */             catch (DWPrinterNotDefinedException e) {
/*     */               
/* 147 */               DWVPrinter.logger.warn("error flushing print buffer: " + e.getMessage());
/*     */             }
/* 149 */             catch (IOException e) {
/*     */               
/* 151 */               DWVPrinter.logger.warn("error flushing print buffer: " + e.getMessage());
/*     */             }
/* 153 */             catch (DWPrinterFileError e) {
/*     */               
/* 155 */               DWVPrinter.logger.warn("error flushing print buffer: " + e.getMessage());
/*     */             } 
/* 157 */             DWVPrinter.logger.debug("flush thread exit");
/*     */           }
/*     */         });
/*     */ 
/*     */     
/* 162 */     flusher.start();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HierarchicalConfiguration getConfig() {
/* 171 */     return this.dwProto.getConfig();
/*     */   }
/*     */ 
/*     */   
/*     */   public Logger getLogger() {
/* 176 */     return this.dwProto.getLogger();
/*     */   }
/*     */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/virtualprinter/DWVPrinter.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */