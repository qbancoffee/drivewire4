/*     */ package com.groupunix.drivewireserver.dwprotocolhandler;
/*     */ 
/*     */ import gnu.io.CommPortIdentifier;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Enumeration;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DWUtils
/*     */ {
/*     */   public static int int4(byte[] data) {
/*  22 */     return ((data[0] & 0xFF) << 32) + ((data[1] & 0xFF) << 16) + ((data[2] & 0xFF) << 8) + (data[3] & 0xFF);
/*     */   }
/*     */ 
/*     */   
/*     */   public static int int3(byte[] data) {
/*  27 */     return ((data[0] & 0xFF) << 16) + ((data[1] & 0xFF) << 8) + (data[2] & 0xFF);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static int int2(byte[] data) {
/*  33 */     return ((data[0] & 0xFF) << 8) + (data[1] & 0xFF);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static byte reverseByte(int b) {
/*  39 */     return (byte)Integer.reverseBytes(Integer.reverse(b));
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static byte[] reverseByteArray(byte[] data) {
/*  45 */     byte[] revdata = new byte[data.length];
/*     */     
/*  47 */     for (int i = 0; i < data.length; i++) {
/*  48 */       revdata[i] = reverseByte(data[i]);
/*     */     }
/*  50 */     return revdata;
/*     */   }
/*     */ 
/*     */   
/*     */   public static byte[] hexStringToByteArray(String hexs) {
/*  55 */     if (hexs.length() > 0)
/*     */     {
/*  57 */       if (hexs.length() % 2 == 0) {
/*     */         
/*  59 */         byte[] res = new byte[hexs.length() / 2];
/*  60 */         for (int i = 0; i < hexs.length() / 2; i++)
/*     */         {
/*  62 */           res[i] = (byte)Integer.parseInt(hexs.substring(i * 2, i * 2 + 1), 16);
/*     */         }
/*     */         
/*  65 */         return res;
/*     */       } 
/*     */     }
/*     */     
/*  69 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public static String byteArrayToHexString(byte[] in) {
/*  74 */     byte ch = 0;
/*     */     
/*  76 */     int i = 0;
/*     */     
/*  78 */     if (in == null || in.length <= 0)
/*     */     {
/*  80 */       return null;
/*     */     }
/*     */ 
/*     */     
/*  84 */     String[] pseudo = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F" };
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  89 */     StringBuffer out = new StringBuffer(in.length * 2);
/*     */ 
/*     */ 
/*     */     
/*  93 */     while (i < in.length) {
/*     */       
/*  95 */       ch = (byte)(in[i] & 0xF0);
/*     */       
/*  97 */       ch = (byte)(ch >>> 4);
/*     */ 
/*     */       
/* 100 */       ch = (byte)(ch & 0xF);
/*     */ 
/*     */       
/* 103 */       out.append(pseudo[ch]);
/*     */       
/* 105 */       ch = (byte)(in[i] & 0xF);
/*     */       
/* 107 */       out.append(pseudo[ch]);
/*     */       
/* 109 */       i++;
/*     */     } 
/*     */ 
/*     */     
/* 113 */     String rslt = new String(out);
/*     */     
/* 115 */     return rslt;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String prettySS(byte statcode) {
/* 124 */     String result = "unknown";
/*     */     
/* 126 */     switch (statcode)
/*     */     
/*     */     { case 0:
/* 129 */         result = "SS.Opt";
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
/*     */         
/* 261 */         return result;case 2: result = "SS.Size"; return result;case 3: result = "SS.Reset"; return result;case 4: result = "SS.WTrk"; return result;case 5: result = "SS.Pos"; return result;case 6: result = "SS.EOF"; return result;case 10: result = "SS.Frz"; return result;case 11: result = "SS.SPT"; return result;case 12: result = "SS.SQD"; return result;case 13: result = "SS.DCmd"; return result;case 14: result = "SS.DevNm"; return result;case 15: result = "SS.FD"; return result;case 16: result = "SS.Ticks"; return result;case 17: result = "SS.Lock"; return result;case 18: result = "SS.VarSect"; return result;case 20: result = "SS.BlkRd"; return result;case 21: result = "SS.BlkWr"; return result;case 22: result = "SS.Reten"; return result;case 23: result = "SS.WFM"; return result;case 24: result = "SS.RFM"; return result;case 26: result = "SS.SSig"; return result;case 27: result = "SS.Relea"; return result;case 28: result = "SS.Attr"; return result;case 30: result = "SS.RsBit"; return result;case 32: result = "SS.FDInf"; return result;case 38: result = "SS.DSize"; return result;case 39: result = "SS.KySns"; return result;case 40: result = "SS.ComSt"; return result;case 41: result = "SS.Open"; return result;case 42: result = "SS.Close"; return result;case 48: result = "SS.HngUp"; return result;case -1: result = "None"; return result; }  result = "Unknown: " + statcode; return result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String prettyUtilMode(int mode) {
/* 268 */     String res = "unset";
/*     */     
/* 270 */     switch (mode) {
/*     */       
/*     */       case 2:
/* 273 */         res = "url";
/*     */         break;
/*     */       
/*     */       case 1:
/* 277 */         res = "dw cmd";
/*     */         break;
/*     */       case 3:
/* 280 */         res = "tcp out";
/*     */         break;
/*     */       case 4:
/* 283 */         res = "vmodem out";
/*     */         break;
/*     */       case 5:
/* 286 */         res = "tcp in";
/*     */         break;
/*     */       case 6:
/* 289 */         res = "vmodem in";
/*     */         break;
/*     */       case 7:
/* 292 */         res = "tcp listen";
/*     */         break;
/*     */     } 
/*     */     
/* 296 */     return res;
/*     */   }
/*     */ 
/*     */   
/*     */   public static String prettyOP(byte opcode) {
/* 301 */     String res = "Unknown";
/*     */     
/* 303 */     if (opcode >= Byte.MIN_VALUE && opcode <= -114)
/*     */     
/* 305 */     { res = "OP_FASTWRITE_" + (opcode - -128); }
/*     */     
/*     */     else
/*     */     
/* 309 */     { switch (opcode)
/*     */       
/*     */       { case 0:
/* 312 */           res = "OP_NOP";
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 410 */           return res;case 73: res = "OP_INIT"; return res;case 82: res = "OP_READ"; return res;case -46: res = "OP_READEX"; return res;case 87: res = "OP_WRITE"; return res;case 114: res = "OP_REREAD"; return res;case -14: res = "OP_REREADEX"; return res;case 119: res = "OP_REWRITE"; return res;case 84: res = "OP_TERM"; return res;case -8: case -2: case -1: res = "OP_RESET"; return res;case 71: res = "OP_GETSTAT"; return res;case 83: res = "OP_SETSTAT"; return res;case 35: res = "OP_TIME"; return res;case 80: res = "OP_PRINT"; return res;case 70: res = "OP_PRINTFLUSH"; return res;case 99: res = "OP_SERREADM"; return res;case 67: res = "OP_SERREAD"; return res;case -61: res = "OP_SERWRITE"; return res;case -60: res = "OP_SERSETSTAT"; return res;case 68: res = "OP_SERGETSTAT"; return res;case 69: res = "OP_SERINIT"; return res;case -59: res = "OP_SERTERM"; return res;case 90: res = "OP_DWINIT"; return res; }  res = "Unknown: " + opcode; }  return res;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ArrayList<String> getPortNames() {
/* 418 */     ArrayList<String> ports = new ArrayList<String>();
/*     */     
/* 420 */     Enumeration<CommPortIdentifier> portEnum = CommPortIdentifier.getPortIdentifiers();
/* 421 */     while (portEnum.hasMoreElements()) {
/*     */       
/* 423 */       CommPortIdentifier portIdentifier = portEnum.nextElement();
/* 424 */       if (portIdentifier.getPortType() == 1)
/*     */       {
/* 426 */         ports.add(portIdentifier.getName());
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 431 */     return ports;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static String midimsgToText(int statusbyte, int data1, int data2) {
/* 437 */     String action = new String();
/* 438 */     String chan = new String();
/* 439 */     String d1 = new String();
/* 440 */     String d2 = new String();
/*     */     
/* 442 */     if (statusbyte >= 128 && statusbyte <= 143) {
/*     */       
/* 444 */       action = "Note off";
/* 445 */       chan = "Chan: " + (statusbyte - 128);
/* 446 */       d1 = "Pitch: " + prettyMidiPitch(data1);
/* 447 */       d2 = "Vel: " + data2;
/*     */     }
/* 449 */     else if (statusbyte >= 144 && statusbyte <= 159) {
/*     */       
/* 451 */       action = "Note on";
/* 452 */       chan = "Chan: " + (statusbyte - 144);
/* 453 */       d1 = "Pitch: " + prettyMidiPitch(data1);
/* 454 */       d2 = "Vel: " + data2;
/*     */     }
/* 456 */     else if (statusbyte >= 160 && statusbyte <= 175) {
/*     */       
/* 458 */       action = "Key press";
/* 459 */       chan = "Chan: " + (statusbyte - 160);
/* 460 */       d1 = "Key: " + data1;
/* 461 */       d2 = "Pressure: " + data2;
/*     */     }
/* 463 */     else if (statusbyte >= 176 && statusbyte <= 191) {
/*     */       
/* 465 */       action = "Ctr change";
/* 466 */       chan = "Chan: " + (statusbyte - 176);
/* 467 */       d1 = "Controller: " + data1;
/* 468 */       d2 = "Value: " + data2;
/*     */     }
/* 470 */     else if (statusbyte >= 192 && statusbyte <= 207) {
/*     */       
/* 472 */       action = "Prg change";
/* 473 */       chan = "Chan: " + (statusbyte - 192);
/* 474 */       d1 = "Preset: " + data1;
/*     */     }
/* 476 */     else if (statusbyte >= 208 && statusbyte <= 223) {
/*     */       
/* 478 */       action = "Chan press";
/* 479 */       chan = "Chan: " + (statusbyte - 208);
/* 480 */       d1 = "Pressure: " + data1;
/*     */     }
/* 482 */     else if (statusbyte >= 224 && statusbyte <= 239) {
/*     */       
/* 484 */       action = "Pitch bend";
/* 485 */       chan = "Chan: " + (statusbyte - 224);
/* 486 */       d1 = "LSB: " + data1;
/* 487 */       d2 = "MSB: " + data2;
/*     */     }
/* 489 */     else if (statusbyte == 248) {
/*     */       
/* 491 */       action = "Timing tick";
/*     */     }
/*     */     else {
/*     */       
/* 495 */       action = "Unknown: " + statusbyte;
/* 496 */       d1 = "Data1: " + data1;
/* 497 */       d2 = "Data2: " + data2;
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 502 */     return String.format("%-10s %-10s %-20s %-20s", new Object[] { action, chan, d1, d2 });
/*     */   }
/*     */ 
/*     */   
/*     */   private static String prettyMidiPitch(int pitch) {
/* 507 */     String[] notes = { "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B" };
/*     */     
/* 509 */     return String.format("%-3d %-2s %d", new Object[] { Integer.valueOf(pitch), notes[pitch % 12], Integer.valueOf(pitch / 12 - 1) });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String dropFirstToken(String txt) {
/* 517 */     String rest = new String();
/*     */     
/* 519 */     String[] tokens = txt.split(" ");
/*     */     
/* 521 */     for (int x = 1; x < tokens.length; x++) {
/*     */       
/* 523 */       if (rest.length() > 0) {
/*     */         
/* 525 */         rest = rest + " " + tokens[x];
/*     */       }
/*     */       else {
/*     */         
/* 529 */         rest = tokens[x];
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 534 */     return rest;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String convertStarToBang(String txt) {
/* 541 */     txt = txt.replaceAll("\\*", "!");
/*     */     
/* 543 */     return txt;
/*     */   }
/*     */ 
/*     */   
/*     */   public static boolean isStringFalse(String tf) {
/* 548 */     if (tf.equalsIgnoreCase("false")) {
/* 549 */       return true;
/*     */     }
/* 551 */     if (tf.equalsIgnoreCase("off")) {
/* 552 */       return true;
/*     */     }
/* 554 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public static boolean isStringTrue(String tf) {
/* 559 */     if (tf.equalsIgnoreCase("true")) {
/* 560 */       return true;
/*     */     }
/* 562 */     if (tf.equalsIgnoreCase("on")) {
/* 563 */       return true;
/*     */     }
/* 565 */     return false;
/*     */   }
/*     */   
/*     */   public static boolean testClassPath(String fullClassName) {
/* 569 */     boolean result = false;
/*     */     try {
/* 571 */       Class.forName(fullClassName);
/* 572 */       result = true;
/* 573 */     } catch (Throwable e) {}
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 578 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean DirExistsOrCreate(String directoryName) {
/* 584 */     File theDir = new File(directoryName);
/*     */ 
/*     */     
/* 587 */     if (!theDir.exists())
/*     */     {
/* 589 */       return theDir.mkdir();
/*     */     }
/*     */ 
/*     */     
/* 593 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean FileExistsOrCreate(String fileName) throws IOException {
/* 599 */     File theFile = new File(fileName);
/*     */ 
/*     */     
/* 602 */     if (!theFile.exists())
/*     */     {
/* 604 */       return theFile.createNewFile();
/*     */     }
/*     */ 
/*     */     
/* 608 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void copyFile(String fromFileName, String toFileName) throws IOException {
/* 616 */     File fromFile = new File(fromFileName);
/* 617 */     File toFile = new File(toFileName);
/*     */     
/* 619 */     if (!fromFile.exists())
/* 620 */       throw new IOException("no source file: " + fromFileName); 
/* 621 */     if (!fromFile.isFile())
/* 622 */       throw new IOException("can't copy directory: " + fromFileName); 
/* 623 */     if (!fromFile.canRead()) {
/* 624 */       throw new IOException("source file is unreadable: " + fromFileName);
/*     */     }
/* 626 */     if (toFile.isDirectory()) {
/* 627 */       toFile = new File(toFile, fromFile.getName());
/*     */     }
/* 629 */     if (toFile.exists()) {
/* 630 */       if (!toFile.canWrite()) {
/* 631 */         throw new IOException("destination file is unwriteable: " + toFileName);
/*     */       }
/* 633 */       String parent = toFile.getParent();
/* 634 */       if (parent == null) {
/* 635 */         parent = System.getProperty("user.dir");
/*     */       }
/* 637 */       File dir = new File(parent);
/* 638 */       if (!dir.exists()) {
/* 639 */         throw new IOException("destination directory doesn't exist: " + parent);
/*     */       }
/* 641 */       if (dir.isFile())
/* 642 */         throw new IOException("destination is not a directory: " + parent); 
/* 643 */       if (!dir.canWrite()) {
/* 644 */         throw new IOException("destination directory is unwriteable: " + parent);
/*     */       }
/*     */     } 
/* 647 */     FileInputStream from = null;
/* 648 */     FileOutputStream to = null;
/*     */     
/*     */     try {
/* 651 */       from = new FileInputStream(fromFile);
/* 652 */       to = new FileOutputStream(toFile);
/* 653 */       byte[] buffer = new byte[4096];
/*     */       
/*     */       int bytesRead;
/* 656 */       while ((bytesRead = from.read(buffer)) != -1) {
/* 657 */         to.write(buffer, 0, bytesRead);
/*     */       }
/*     */     } finally {
/*     */       
/* 661 */       if (from != null) {
/*     */         try {
/* 663 */           from.close();
/* 664 */         } catch (IOException e) {}
/*     */       }
/*     */       
/* 667 */       if (to != null) {
/*     */         try {
/* 669 */           to.close();
/* 670 */         } catch (IOException e) {}
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String shortenLocalURI(String df) {
/* 679 */     if (df.startsWith("file:///")) {
/*     */       
/* 681 */       if (df.charAt(9) == ':')
/*     */       {
/* 683 */         return df.substring(8);
/*     */       }
/*     */ 
/*     */       
/* 687 */       return df.substring(7);
/*     */     } 
/*     */     
/* 690 */     return df;
/*     */   }
/*     */ 
/*     */   
/*     */   public static String getFileDescriptor(File f) {
/* 695 */     String res = "";
/*     */     
/*     */     try {
/* 698 */       res = File.separator;
/* 699 */       res = res + "|" + f.getCanonicalPath();
/* 700 */       res = res + "|" + f.getParent();
/* 701 */       if (f.getParent() != null) {
/*     */ 
/*     */         
/* 704 */         res = res + "|" + f.length();
/* 705 */         res = res + "|" + f.lastModified();
/* 706 */         res = res + "|" + f.isDirectory();
/*     */       } else {
/*     */         
/* 709 */         res = res + "|0|0|true";
/*     */       }
/*     */     
/*     */     }
/* 713 */     catch (IOException e) {
/*     */       
/* 715 */       e.printStackTrace();
/*     */     } 
/*     */     
/* 718 */     return res;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String cocoString(byte[] bytes) {
/* 728 */     String ret = new String();
/*     */     
/* 730 */     int i = 0;
/*     */ 
/*     */     
/* 733 */     while (i < bytes.length - 1 && bytes[i] > 0) {
/*     */       
/* 735 */       ret = ret + Character.toString((char)bytes[i]);
/* 736 */       i++;
/*     */     } 
/*     */     
/* 739 */     ret = ret + Character.toString((char)(bytes[i] + 128));
/*     */     
/* 741 */     return ret;
/*     */   }
/*     */ 
/*     */   
/*     */   public static String prettyFormat(int diskFormat) {
/* 746 */     String res = "unknown";
/*     */     
/* 748 */     switch (diskFormat) {
/*     */       
/*     */       case 2:
/* 751 */         res = "DMK";
/*     */         break;
/*     */       case 3:
/* 754 */         res = "JVC";
/*     */         break;
/*     */       case 1:
/* 757 */         res = "Raw sectors";
/*     */         break;
/*     */       case 4:
/* 760 */         res = "VDK";
/*     */         break;
/*     */       case 0:
/* 763 */         res = "none";
/*     */         break;
/*     */     } 
/*     */ 
/*     */     
/* 768 */     return res;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static String prettyFileSystem(int format) {
/* 774 */     String res = "unknown";
/*     */     
/* 776 */     switch (format) {
/*     */       
/*     */       case 0:
/* 779 */         res = "OS9";
/*     */         break;
/*     */       case 1:
/* 782 */         res = "DECB";
/*     */         break;
/*     */       case 2:
/* 785 */         res = "LWFS";
/*     */         break;
/*     */       case 3:
/* 788 */         res = "CCB";
/*     */         break;
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 794 */     return res;
/*     */   }
/*     */ 
/*     */   
/*     */   public static ThreadGroup getRootThreadGroup() {
/* 799 */     ThreadGroup tg = Thread.currentThread().getThreadGroup();
/*     */     ThreadGroup ptg;
/* 801 */     while ((ptg = tg.getParent()) != null)
/* 802 */       tg = ptg; 
/* 803 */     return tg;
/*     */   }
/*     */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/dwprotocolhandler/DWUtils.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */