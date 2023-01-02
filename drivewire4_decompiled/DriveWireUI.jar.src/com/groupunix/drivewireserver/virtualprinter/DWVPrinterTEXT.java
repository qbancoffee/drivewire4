/*     */ package com.groupunix.drivewireserver.virtualprinter;
/*     */ 
/*     */ import com.groupunix.drivewireserver.dwexceptions.DWPrinterFileError;
/*     */ import com.groupunix.drivewireserver.dwexceptions.DWPrinterNotDefinedException;
/*     */ import com.groupunix.drivewireserver.dwprotocolhandler.DWUtils;
/*     */ import com.groupunix.drivewireserver.virtualserial.DWVSerialCircularBuffer;
/*     */ import java.io.File;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import org.apache.commons.configuration.HierarchicalConfiguration;
/*     */ import org.apache.log4j.Logger;
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DWVPrinterTEXT
/*     */   implements DWVPrinterDriver
/*     */ {
/*  20 */   private static final Logger logger = Logger.getLogger("DWServer.DWVPrinter.DWVPrinterTEXT");
/*     */   
/*  22 */   private DWVSerialCircularBuffer printBuffer = new DWVSerialCircularBuffer(-1, true);
/*     */ 
/*     */   
/*     */   private HierarchicalConfiguration config;
/*     */ 
/*     */   
/*     */   public DWVPrinterTEXT(HierarchicalConfiguration config) {
/*  29 */     this.config = config;
/*  30 */     this.printBuffer.clear();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addByte(byte data) throws IOException {
/*  37 */     this.printBuffer.getOutputStream().write(data);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getDriverName() {
/*  44 */     return "TEXT";
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void flush() throws IOException, DWPrinterNotDefinedException, DWPrinterFileError {
/*  51 */     File file = getPrinterFile();
/*     */     
/*  53 */     FileOutputStream fos = new FileOutputStream(file);
/*     */     
/*  55 */     while (this.printBuffer.getAvailable() > 0) {
/*     */       
/*  57 */       byte[] buf = new byte[256];
/*  58 */       int read = this.printBuffer.getInputStream().read(buf);
/*  59 */       fos.write(buf, 0, read);
/*     */     } 
/*     */     
/*  62 */     fos.flush();
/*  63 */     fos.close();
/*     */     
/*  65 */     logger.info("Flushed print job to " + file.getCanonicalPath());
/*     */ 
/*     */     
/*  68 */     if (this.config.containsKey("FlushCommand"))
/*     */     {
/*  70 */       doExec(doVarSubst(file, this.config.getString("FlushCommand")));
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void doExec(String cmd) {
/*     */     try {
/*  81 */       logger.info("executing flush command: " + cmd);
/*  82 */       Runtime.getRuntime().exec(cmd);
/*     */     }
/*  84 */     catch (IOException e) {
/*     */       
/*  86 */       logger.warn("Error during flush command: " + e.getMessage());
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private String doVarSubst(File file, String cmd) throws IOException {
/*  97 */     HashMap<String, String> vars = new HashMap<String, String>();
/*  98 */     vars.put("name", this.config.getString("Name"));
/*     */     
/* 100 */     vars.put("file", file.getCanonicalPath().replaceAll("\\\\", "\\\\\\\\"));
/*     */     
/* 102 */     for (Map.Entry<String, String> e : vars.entrySet())
/*     */     {
/* 104 */       cmd = cmd.replaceAll("\\$" + (String)e.getKey(), e.getValue());
/*     */     }
/*     */     
/* 107 */     return cmd;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getFileExtension() {
/* 114 */     return ".txt";
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String getFilePrefix() {
/* 120 */     return "dw_text_";
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public File getPrinterFile() throws IOException, DWPrinterNotDefinedException, DWPrinterFileError {
/* 126 */     if (this.config.containsKey("OutputFile")) {
/*     */       
/* 128 */       if (DWUtils.FileExistsOrCreate(this.config.getString("OutputFile")))
/*     */       {
/* 130 */         return new File(this.config.getString("OutputFile"));
/*     */       }
/*     */ 
/*     */       
/* 134 */       throw new DWPrinterFileError("Cannot find or create the output file '" + this.config.getString("OutputFile") + "'");
/*     */     } 
/*     */ 
/*     */     
/* 138 */     if (this.config.containsKey("OutputDir")) {
/*     */       
/* 140 */       if (DWUtils.DirExistsOrCreate(this.config.getString("OutputDir")))
/*     */       {
/* 142 */         return File.createTempFile(getFilePrefix(), getFileExtension(), new File(this.config.getString("OutputDir")));
/*     */       }
/*     */ 
/*     */ 
/*     */       
/* 147 */       throw new DWPrinterFileError("Cannot find or create the output directory '" + this.config.getString("OutputDir") + "'");
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 154 */     throw new DWPrinterFileError("No OutputFile or OutputDir defined in printer config, don't know where to send output.");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getPrinterName() {
/* 162 */     return this.config.getString("[@name]", "?noname?");
/*     */   }
/*     */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/virtualprinter/DWVPrinterTEXT.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */