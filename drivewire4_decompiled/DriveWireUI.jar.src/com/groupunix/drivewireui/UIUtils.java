/*     */ package com.groupunix.drivewireui;
/*     */ 
/*     */ import com.groupunix.drivewireserver.DWDefs;
/*     */ import java.io.BufferedWriter;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.FileWriter;
/*     */ import java.io.FilenameFilter;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.io.PrintWriter;
/*     */ import java.io.StringReader;
/*     */ import java.io.StringWriter;
/*     */ import java.io.Writer;
/*     */ import java.net.UnknownHostException;
/*     */ import java.sql.Timestamp;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Calendar;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ import org.apache.commons.configuration.ConfigurationException;
/*     */ import org.apache.commons.configuration.HierarchicalConfiguration;
/*     */ import org.apache.commons.configuration.XMLConfiguration;
/*     */ import org.eclipse.swt.graphics.Device;
/*     */ import org.eclipse.swt.graphics.Drawable;
/*     */ import org.eclipse.swt.graphics.Font;
/*     */ import org.eclipse.swt.graphics.FontData;
/*     */ import org.eclipse.swt.graphics.GC;
/*     */ import org.eclipse.swt.graphics.Image;
/*     */ import org.eclipse.swt.widgets.Display;
/*     */ 
/*     */ 
/*     */ 
/*     */ public class UIUtils
/*     */ {
/*     */   public static List<String> loadList(String arg) throws IOException, DWUIOperationFailedException {
/*  44 */     return loadList(-1, arg);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static List<String> loadList(int instance, String arg) throws IOException, DWUIOperationFailedException {
/*  51 */     Connection conn = new Connection(MainWin.getHost(), MainWin.getPort(), MainWin.getInstance());
/*     */     
/*  53 */     List<String> res = new ArrayList<String>();
/*     */     
/*  55 */     conn.Connect();
/*     */     
/*  57 */     res = conn.loadList(instance, arg);
/*     */     
/*  59 */     conn.close();
/*     */     
/*  61 */     return res;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static String getStackTrace(Throwable aThrowable) {
/*  67 */     Writer result = new StringWriter();
/*  68 */     PrintWriter printWriter = new PrintWriter(result);
/*  69 */     aThrowable.printStackTrace(printWriter);
/*  70 */     return result.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static HashMap<String, String> getServerSettings(ArrayList<String> settings) throws DWUIOperationFailedException, IOException {
/*  78 */     HashMap<String, String> values = new HashMap<String, String>();
/*     */     
/*  80 */     Connection conn = new Connection(MainWin.getHost(), MainWin.getPort(), MainWin.getInstance());
/*     */     
/*  82 */     conn.Connect();
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  87 */     for (int i = 0; i < settings.size(); i++) {
/*     */       
/*  89 */       List<String> res = conn.loadList(-1, "ui server config show " + (String)settings.get(i));
/*  90 */       values.put(settings.get(i), res.get(0));
/*     */     } 
/*     */     
/*  93 */     conn.close();
/*     */ 
/*     */     
/*  96 */     return values;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean sTob(String val) {
/* 103 */     if (val != null && val.equalsIgnoreCase("true")) {
/* 104 */       return true;
/*     */     }
/* 106 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static void setServerSettings(HashMap<String, String> values) throws IOException, DWUIOperationFailedException {
/* 112 */     if (values.size() > 0) {
/*     */       
/* 114 */       int tid = MainWin.taskman.addTask("Server settings dump");
/*     */       
/* 116 */       MainWin.taskman.updateTask(tid, 0, "Connecting to server...");
/*     */       
/* 118 */       Connection conn = new Connection(MainWin.getHost(), MainWin.getPort(), MainWin.getInstance());
/*     */       
/* 120 */       conn.Connect();
/*     */       
/* 122 */       Collection<String> c = values.keySet();
/* 123 */       Iterator<String> itr = c.iterator();
/*     */       
/* 125 */       while (itr.hasNext()) {
/*     */         
/* 127 */         String val = itr.next();
/* 128 */         conn.sendCommand(tid, "ui server config set " + val + " " + (String)values.get(val), 0);
/*     */       } 
/*     */       
/* 131 */       conn.close();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static String bTos(boolean selection) {
/* 138 */     if (selection) {
/* 139 */       return "true";
/*     */     }
/* 141 */     return "false";
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean validateNum(String data) {
/*     */     try {
/* 148 */       Integer.parseInt(data);
/*     */     
/*     */     }
/* 151 */     catch (NumberFormatException e) {
/*     */       
/* 153 */       return false;
/*     */     } 
/*     */     
/* 156 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean validateNum(String data, int min) {
/*     */     try {
/* 165 */       int val = Integer.parseInt(data);
/* 166 */       if (val < min) {
/* 167 */         return false;
/*     */       }
/* 169 */     } catch (NumberFormatException e) {
/*     */       
/* 171 */       return false;
/*     */     } 
/*     */     
/* 174 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean validateNum(String data, int min, int max) {
/*     */     try {
/* 181 */       int val = Integer.parseInt(data);
/* 182 */       if (val < min || val > max) {
/* 183 */         return false;
/*     */       }
/*     */     }
/* 186 */     catch (NumberFormatException e) {
/*     */       
/* 188 */       return false;
/*     */     } 
/*     */     
/* 191 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static HashMap<String, String> getInstanceSettings(int instance, ArrayList<String> settings) throws DWUIOperationFailedException, IOException {
/* 199 */     HashMap<String, String> values = new HashMap<String, String>();
/*     */     
/* 201 */     Connection conn = new Connection(MainWin.getHost(), MainWin.getPort(), instance);
/*     */     
/* 203 */     conn.Connect();
/*     */ 
/*     */     
/* 206 */     for (int i = 0; i < settings.size(); i++) {
/*     */       
/* 208 */       List<String> res = conn.loadList(instance, "ui instance config show " + (String)settings.get(i));
/* 209 */       values.put(settings.get(i), res.get(0));
/*     */     } 
/*     */ 
/*     */     
/* 213 */     conn.close();
/*     */ 
/*     */     
/* 216 */     return values;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean validTCPPort(int port) {
/* 222 */     if (port > 0 && port < 65536) {
/* 223 */       return true;
/*     */     }
/* 225 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static void setInstanceSettings(int instance, HashMap<String, String> values) throws UnknownHostException, IOException, DWUIOperationFailedException {
/* 231 */     if (values.size() > 0) {
/*     */       
/* 233 */       int tid = MainWin.taskman.addTask("Instance settings dump");
/*     */       
/* 235 */       MainWin.taskman.updateTask(tid, 0, "Connecting to server...");
/*     */       
/* 237 */       Connection conn = new Connection(MainWin.getHost(), MainWin.getPort(), MainWin.getInstance());
/*     */       
/* 239 */       conn.Connect();
/*     */       
/* 241 */       Collection<String> c = values.keySet();
/* 242 */       Iterator<String> itr = c.iterator();
/*     */       
/* 244 */       while (itr.hasNext()) {
/*     */         
/* 246 */         String val = itr.next();
/* 247 */         conn.sendCommand(tid, "dw config set " + val + " " + (String)values.get(val), instance);
/*     */       } 
/*     */       
/* 250 */       conn.close();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static DiskDef getDiskDef(int instance, int diskno) throws IOException, DWUIOperationFailedException {
/* 260 */     DiskDef disk = new DiskDef(diskno);
/*     */ 
/*     */ 
/*     */     
/*     */     try {
/* 265 */       List<String> res = loadList(instance, "ui instance disk show " + diskno);
/*     */ 
/*     */       
/* 268 */       for (int i = 0; i < res.size(); i++)
/*     */       {
/* 270 */         Pattern p_item = Pattern.compile("^(.+):\\s(.+)");
/* 271 */         Matcher m = p_item.matcher(res.get(i));
/*     */         
/* 273 */         if (m.find())
/*     */         {
/* 275 */           if (m.group(1).startsWith("*")) {
/*     */             
/* 277 */             if (m.group(1).equals("*loaded")) {
/* 278 */               disk.setLoaded(Boolean.parseBoolean(m.group(2)));
/*     */             }
/*     */           } else {
/*     */             
/* 282 */             disk.setParam(m.group(1), m.group(2));
/*     */           
/*     */           }
/*     */         
/*     */         }
/*     */       }
/*     */     
/*     */     }
/* 290 */     catch (NumberFormatException e) {
/*     */       
/* 292 */       throw new DWUIOperationFailedException("Error parsing disk set results: " + e.getMessage());
/*     */     } 
/*     */ 
/*     */     
/* 296 */     return disk;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static HierarchicalConfiguration getServerConfig() throws UnknownHostException, IOException, ConfigurationException, DWUIOperationFailedException {
/* 302 */     Connection conn = new Connection(MainWin.getHost(), MainWin.getPort(), MainWin.getInstance());
/*     */     
/* 304 */     conn.Connect();
/*     */     
/* 306 */     StringReader sr = conn.loadReader(-1, "ui server config write");
/* 307 */     conn.close();
/*     */     
/* 309 */     XMLConfiguration res = new XMLConfiguration();
/* 310 */     res.load(sr);
/* 311 */     return (HierarchicalConfiguration)res.clone();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static MIDIStatus getServerMidiStatus() throws IOException, DWUIOperationFailedException {
/* 318 */     List<String> res = loadList(MainWin.getInstance(), "ui instance midi");
/*     */     
/* 320 */     MIDIStatus st = new MIDIStatus();
/*     */     
/* 322 */     for (String l : res) {
/*     */       
/* 324 */       String[] kv = l.trim().split("\\|");
/*     */ 
/*     */       
/* 327 */       if (kv.length > 1) {
/*     */         
/* 329 */         String key = kv[0];
/*     */         
/* 331 */         if (key.equals("cprofile")) {
/*     */           
/* 333 */           st.setCurrentProfile(kv[1]); continue;
/*     */         } 
/* 335 */         if (key.equals("cdevice")) {
/*     */           
/* 337 */           st.setCurrentDevice(kv[1]); continue;
/*     */         } 
/* 339 */         if (key.equals("enabled")) {
/*     */           
/* 341 */           st.setEnabled(Boolean.parseBoolean(kv[1])); continue;
/*     */         } 
/* 343 */         if (key.equals("profile")) {
/*     */           
/* 345 */           if (kv.length == 3)
/* 346 */             st.addProfile(kv[1], kv[2]);  continue;
/*     */         } 
/* 348 */         if (key.equals("device"))
/*     */         {
/* 350 */           if (kv.length == 7) {
/* 351 */             st.addDevice(Integer.parseInt(kv[1]), kv[2], kv[3], kv[4], kv[5], kv[6]);
/*     */           }
/*     */         }
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 358 */     return st;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static DiskDef[] getServerDisks() throws IOException, DWUIOperationFailedException {
/* 365 */     DiskDef[] disks = new DiskDef[256];
/*     */     
/* 367 */     List<String> res = loadList(MainWin.getInstance(), "ui instance disk show");
/*     */ 
/*     */     
/* 370 */     for (String l : res) {
/*     */       
/* 372 */       String[] parts = l.trim().split("\\|");
/*     */       
/* 374 */       if (parts.length == 2) {
/*     */         
/* 376 */         int drive = Integer.parseInt(parts[0]);
/* 377 */         disks[drive] = getDiskDef(MainWin.getInstance(), drive);
/*     */       } 
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 383 */     for (int i = 0; i < 256; i++) {
/*     */       
/* 385 */       if (disks[i] == null) {
/* 386 */         disks[i] = new DiskDef(i);
/*     */       }
/*     */     } 
/* 389 */     return disks;
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
/*     */   
/*     */   public static DWServerFile[] getFileArray(String uicmd) throws IOException, DWUIOperationFailedException {
/* 405 */     DWServerFile[] res = null;
/*     */     
/* 407 */     List<String> roots = loadList(uicmd);
/*     */     
/* 409 */     res = new DWServerFile[roots.size()];
/*     */     
/* 411 */     for (int i = 0; i < roots.size(); i++) {
/*     */       
/* 413 */       res[i] = new DWServerFile(".");
/* 414 */       res[i].setVals(roots.get(i));
/*     */     } 
/*     */ 
/*     */     
/* 418 */     return res;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static void fileCopy(String infile, String outfile) throws IOException {
/* 424 */     File f1 = new File(infile);
/* 425 */     File f2 = new File(outfile);
/* 426 */     InputStream in = new FileInputStream(f1);
/*     */     
/* 428 */     OutputStream out = new FileOutputStream(f2);
/*     */     
/* 430 */     byte[] buf = new byte[1024];
/*     */     int len;
/* 432 */     while ((len = in.read(buf)) > 0)
/*     */     {
/* 434 */       out.write(buf, 0, len);
/*     */     }
/* 436 */     in.close();
/* 437 */     out.close();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String getFilenameFromURI(String string) {
/* 448 */     String res = string;
/*     */     
/* 450 */     if (res.indexOf('/') > -1 && res.indexOf('/') < res.length() - 1) {
/* 451 */       res = res.substring(res.lastIndexOf('/') + 1);
/*     */     }
/* 453 */     return res;
/*     */   }
/*     */ 
/*     */   
/*     */   public static String shortenLocalURI(String df) {
/* 458 */     if (df != null && df.startsWith("file:///")) {
/*     */       
/* 460 */       if (df.charAt(9) == ':')
/*     */       {
/* 462 */         return df.substring(8);
/*     */       }
/*     */ 
/*     */       
/* 466 */       return df.substring(7);
/*     */     } 
/*     */     
/* 469 */     return df;
/*     */   }
/*     */ 
/*     */   
/*     */   public static String getLocationFromURI(String string) {
/* 474 */     String res = string;
/*     */     
/* 476 */     if (res.indexOf('/') > -1 && res.indexOf('/') < res.length() - 1) {
/* 477 */       res = res.substring(0, res.lastIndexOf('/'));
/*     */     }
/*     */     
/* 480 */     return shortenLocalURI(res);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void loadFonts() {
/* 487 */     MainWin.debug("load fonts");
/*     */     class OnlyExt
/*     */       implements FilenameFilter
/*     */     {
/*     */       String ext;
/*     */       
/*     */       public OnlyExt(String ext) {
/* 494 */         this.ext = "." + ext;
/*     */       }
/*     */ 
/*     */ 
/*     */       
/*     */       public boolean accept(File dir, String name) {
/* 500 */         return name.endsWith(this.ext);
/*     */       }
/*     */     };
/*     */ 
/*     */     
/* 505 */     File fontdir = new File("fonts");
/*     */     
/* 507 */     if (fontdir.exists() && fontdir.isDirectory()) {
/*     */       
/* 509 */       String[] files = fontdir.list(new OnlyExt("ttf"));
/*     */       
/* 511 */       for (int i = 0; i < files.length; i++)
/*     */       {
/* 513 */         if (!MainWin.getDisplay().loadFont("fonts/" + files[i]))
/*     */         {
/* 515 */           MainWin.debug("Failed to load font from " + files[i]);
/*     */         
/*     */         }
/*     */         else
/*     */         {
/*     */           
/* 521 */           MainWin.debug("Loaded font from " + files[i]);
/*     */         
/*     */         }
/*     */       
/*     */       }
/*     */     
/*     */     }
/*     */     else {
/*     */       
/* 530 */       MainWin.debug("No font dir");
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Font findSizedFont(String fname, String txt, int maxw, int maxh, int style) {
/* 539 */     int size = 8;
/* 540 */     int width = 0;
/* 541 */     int height = 0;
/*     */     
/* 543 */     Image test = new Image(null, maxw * 2, maxh * 2);
/* 544 */     GC gc = new GC((Drawable)test);
/* 545 */     Font font = null;
/*     */     
/* 547 */     while (width < maxw && height < maxh) {
/*     */       
/* 549 */       if (font != null) {
/* 550 */         font.dispose();
/*     */       }
/* 552 */       font = new Font((Device)MainWin.getDisplay(), fname, size, style);
/*     */       
/* 554 */       gc.setFont(font);
/* 555 */       width = (gc.textExtent(txt)).x;
/* 556 */       height = (gc.textExtent(txt)).y;
/*     */       
/* 558 */       size++;
/*     */     } 
/*     */     
/* 561 */     gc.dispose();
/*     */     
/* 563 */     MainWin.debug("chose font " + fname + " @ " + size + " = " + width + " x " + height);
/*     */ 
/*     */     
/* 566 */     return font;
/*     */   }
/*     */ 
/*     */   
/*     */   public static Font findFont(Display display, HashMap<String, Integer> candidates, String text, int maxw, int maxh) {
/* 571 */     FontData[] fd = MainWin.getDisplay().getFontList(null, true);
/*     */ 
/*     */     
/* 574 */     for (FontData f : fd) {
/*     */       
/* 576 */       for (Map.Entry<String, Integer> e : candidates.entrySet()) {
/*     */         
/* 578 */         if (f.getName().equals(e.getKey()) && f.getStyle() == ((Integer)e.getValue()).intValue())
/*     */         {
/* 580 */           return findSizedFont(f.getName(), text, maxw, maxh, f.getStyle());
/*     */         }
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 586 */     MainWin.debug("Failed to find a font");
/* 587 */     return Display.getCurrent().getSystemFont();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String listFonts() {
/* 595 */     String res = "";
/*     */     
/* 597 */     FontData[] fd = Display.getDefault().getFontList(null, true);
/*     */     
/* 599 */     for (FontData f : fd)
/*     */     {
/* 601 */       res = res + f.getName() + " gh:" + f.getHeight() + " st: " + f.getStyle() + " ht: " + f.height + "\n";
/*     */     }
/*     */ 
/*     */     
/* 605 */     return res;
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
/*     */   public static void simpleConfigServer(int rate, String devname, String device, boolean usemidi, String printertype, String printerdir) throws IOException, DWUIOperationFailedException {
/* 617 */     ArrayList<String> cmds = new ArrayList<String>();
/*     */     
/* 619 */     cmds.add("dw config set DeviceType serial");
/* 620 */     cmds.add("dw config set SerialDevice " + device);
/* 621 */     cmds.add("dw config set SerialRate " + rate);
/* 622 */     cmds.add("dw config set [@name] " + devname + " on " + device);
/* 623 */     cmds.add("dw config set [@desc] Autocreated " + (new Timestamp(Calendar.getInstance().getTime().getTime())).toString());
/*     */     
/* 625 */     cmds.add("dw config set UseMIDI " + usemidi);
/*     */ 
/*     */     
/* 628 */     for (int i = 0; i <= MainWin.getInstanceConfig().getMaxIndex("Printer"); i++) {
/*     */       
/* 630 */       if (MainWin.getInstanceConfig().getString("Printer(" + i + ")[@name]").equals(printertype)) {
/* 631 */         cmds.add("dw config set CurrentPrinter " + printertype);
/*     */       }
/* 633 */       if (MainWin.getInstanceConfig().getString("Printer(" + i + ")[@name]").equals("Text")) {
/* 634 */         cmds.add("dw config set Printer(" + i + ").OutputDir " + printerdir);
/*     */       }
/* 636 */       if (MainWin.getInstanceConfig().getString("Printer(" + i + ")[@name]").equals("FX80")) {
/* 637 */         cmds.add("dw config set Printer(" + i + ").OutputDir " + printerdir);
/*     */       }
/*     */     } 
/*     */     
/* 641 */     int tid = MainWin.taskman.addTask("Configure server for " + devname + " on " + device);
/* 642 */     String res = "";
/*     */ 
/*     */ 
/*     */     
/*     */     try {
/* 647 */       for (String cmd : cmds) {
/*     */         
/* 649 */         res = res + "Sending command: " + cmd;
/* 650 */         MainWin.taskman.updateTask(tid, 0, res);
/*     */         
/* 652 */         loadList(MainWin.getInstance(), cmd);
/* 653 */         res = res + "\tOK\n";
/*     */       } 
/*     */       
/* 656 */       res = res + "\nRestarting device handler...";
/* 657 */       MainWin.taskman.updateTask(tid, 0, res);
/* 658 */       loadList(MainWin.getInstance(), "ui instance reset protodev");
/* 659 */       res = res + "\tOK\n";
/* 660 */       MainWin.taskman.updateTask(tid, 1, res);
/*     */ 
/*     */     
/*     */     }
/* 664 */     catch (IOException e) {
/*     */       
/* 666 */       res = res + "\tFAIL\n\n" + e.getMessage();
/*     */       
/* 668 */       MainWin.taskman.updateTask(tid, 2, res);
/* 669 */       throw new IOException(e);
/*     */     
/*     */     }
/* 672 */     catch (DWUIOperationFailedException e) {
/*     */       
/* 674 */       res = res + "\tFAIL\n\n" + e.getMessage();
/*     */       
/* 676 */       MainWin.taskman.updateTask(tid, 2, res);
/*     */       
/* 678 */       throw new DWUIOperationFailedException(e.getMessage());
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String dumpMIDIStatus(MIDIStatus midiStatus) {
/* 688 */     String res = "";
/*     */     
/* 690 */     res = res + "curdev: " + midiStatus.getCurrentDevice() + "\n";
/* 691 */     res = res + "curprof: " + midiStatus.getCurrentProfile() + "\n";
/* 692 */     for (String s : midiStatus.getProfiles())
/*     */     {
/* 694 */       res = res + "profile: " + s + "\n";
/*     */     }
/*     */     
/* 697 */     return res;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static void saveLogItemsToFile() {
/* 703 */     Runnable doit = new Runnable()
/*     */       {
/*     */         
/*     */         public void run()
/*     */         {
/* 708 */           String fn = MainWin.getFile(true, false, "", "Save log to...", "Save");
/*     */           
/* 710 */           if (fn != null) {
/*     */             
/*     */             try {
/*     */               
/* 714 */               FileWriter fstream = new FileWriter(fn);
/*     */               
/* 716 */               BufferedWriter out = new BufferedWriter(fstream);
/*     */               
/* 718 */               synchronized (MainWin.logItems) {
/*     */                 
/* 720 */                 for (LogItem li : MainWin.logItems)
/*     */                 {
/* 722 */                   out.write(li.toString() + System.getProperty("line.separator"));
/*     */                 }
/*     */               } 
/*     */               
/* 726 */               out.close();
/*     */             
/*     */             }
/* 729 */             catch (Exception e) {
/*     */               
/* 731 */               MainWin.showError("Error saving log items", e.getClass().getSimpleName() + ": " + e.getMessage(), UIUtils.getStackTrace(e), false);
/*     */             } 
/*     */           }
/*     */         }
/*     */       };
/*     */ 
/*     */ 
/*     */     
/* 739 */     Thread t = new Thread(doit);
/* 740 */     t.start();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean hasArg(String[] args, String arg) {
/* 746 */     for (String a : args) {
/*     */       
/* 748 */       if (a.endsWith("-" + arg)) {
/* 749 */         return true;
/*     */       }
/*     */     } 
/* 752 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static int getLogLevelVal(String level) {
/* 758 */     int res = 0;
/*     */     
/* 760 */     for (int i = 0; i < DWDefs.LOG_LEVELS.length; i++) {
/*     */       
/* 762 */       if (DWDefs.LOG_LEVELS[i].equals(level))
/*     */       {
/* 764 */         res = i;
/*     */       }
/*     */     } 
/*     */     
/* 768 */     return res;
/*     */   }
/*     */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireui/UIUtils.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */