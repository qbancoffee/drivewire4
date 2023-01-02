/*     */ package com.groupunix.drivewireserver.dwcommands;
/*     */ 
/*     */ import com.groupunix.drivewireserver.dwexceptions.DWHelpTopicNotFoundException;
/*     */ import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocol;
/*     */ import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocolHandler;
/*     */ import com.groupunix.drivewireserver.dwprotocolhandler.DWUtils;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DWCommandList
/*     */ {
/*  17 */   private List<DWCommand> commands = new ArrayList<DWCommand>();
/*  18 */   private int outputcols = 80;
/*     */   
/*     */   private DWProtocol dwProto;
/*     */   
/*     */   public DWCommandList(DWProtocol dwProto, int outputcols) {
/*  23 */     this.outputcols = outputcols;
/*  24 */     this.dwProto = dwProto;
/*     */   }
/*     */ 
/*     */   
/*     */   public DWCommandList(DWProtocol dwProto) {
/*  29 */     this.dwProto = dwProto;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addcommand(DWCommand dwCommand) {
/*  36 */     this.commands.add(dwCommand);
/*     */   }
/*     */ 
/*     */   
/*     */   public List<DWCommand> getCommands() {
/*  41 */     return this.commands;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DWCommandResponse parse(String cmdline) {
/*  48 */     String[] args = cmdline.split(" ");
/*     */     
/*  50 */     if (cmdline.length() == 0)
/*     */     {
/*     */       
/*  53 */       return new DWCommandResponse(getShortHelp());
/*     */     }
/*     */     
/*  56 */     int matches = numCommandMatches(args[0]);
/*     */     
/*  58 */     if (matches == 0)
/*     */     {
/*  60 */       return new DWCommandResponse(false, (byte)10, "Unknown command '" + args[0] + "'");
/*     */     }
/*  62 */     if (matches > 1)
/*     */     {
/*  64 */       return new DWCommandResponse(false, (byte)10, "Ambiguous command, '" + args[0] + "' matches " + getTextMatches(args[0]));
/*     */     }
/*     */ 
/*     */     
/*  68 */     if (args.length == 2 && args[1].equals("?"))
/*     */     {
/*  70 */       return getLongHelp(getCommandMatch(args[0]));
/*     */     }
/*     */ 
/*     */     
/*  74 */     return getCommandMatch(args[0]).parse(DWUtils.dropFirstToken(cmdline));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private DWCommandResponse getLongHelp(DWCommand cmd) {
/*  83 */     String text = new String();
/*     */ 
/*     */ 
/*     */     
/*  87 */     String cmdline = cmd.getCommand();
/*     */     
/*  89 */     DWCommand tmp = cmd;
/*     */     
/*  91 */     while (tmp.getParentCmd() != null) {
/*     */       
/*  93 */       tmp = tmp.getParentCmd();
/*  94 */       cmdline = tmp.getCommand() + " " + cmdline;
/*     */     } 
/*     */     
/*  97 */     text = cmd.getUsage() + "\r\n\r\n";
/*  98 */     text = text + cmd.getShortHelp() + "\r\n";
/*     */     
/* 100 */     if (this.dwProto != null) {
/*     */       
/*     */       try {
/*     */         
/* 104 */         text = ((DWProtocolHandler)this.dwProto).getHelp().getTopicText(cmdline);
/*     */       }
/* 106 */       catch (DWHelpTopicNotFoundException e) {}
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 111 */     if (this.outputcols <= 32) {
/* 112 */       text = text.toUpperCase();
/*     */     }
/* 114 */     return new DWCommandResponse(text);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String getShortHelp() {
/* 120 */     String txt = new String();
/*     */     
/* 122 */     ArrayList<String> ps = new ArrayList<String>();
/*     */     
/* 124 */     for (Iterator<DWCommand> it = this.commands.iterator(); it.hasNext(); ) {
/*     */       
/* 126 */       DWCommand cmd = it.next();
/* 127 */       ps.add(cmd.getCommand());
/*     */     } 
/*     */ 
/*     */     
/* 131 */     Collections.sort(ps);
/* 132 */     txt = colLayout(ps, this.outputcols);
/*     */     
/* 134 */     txt = "Possible commands:\r\n\r\n" + txt;
/*     */     
/* 136 */     return txt;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private String getTextMatches(String arg) {
/* 143 */     String matchtxt = new String();
/*     */     
/* 145 */     for (Iterator<DWCommand> it = this.commands.iterator(); it.hasNext(); ) {
/*     */       
/* 147 */       DWCommand cmd = it.next();
/*     */       
/* 149 */       if (cmd.getCommand().startsWith(arg.toLowerCase())) {
/*     */         
/* 151 */         if (matchtxt.length() == 0) {
/*     */           
/* 153 */           matchtxt = matchtxt + cmd.getCommand();
/*     */           
/*     */           continue;
/*     */         } 
/* 157 */         matchtxt = matchtxt + " or " + cmd.getCommand();
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 162 */     return matchtxt;
/*     */   }
/*     */ 
/*     */   
/*     */   private int numCommandMatches(String arg) {
/* 167 */     int matches = 0;
/*     */     
/* 169 */     for (Iterator<DWCommand> it = this.commands.iterator(); it.hasNext();) {
/*     */       
/* 171 */       if (((DWCommand)it.next()).getCommand().startsWith(arg.toLowerCase()))
/*     */       {
/* 173 */         matches++;
/*     */       }
/*     */     } 
/*     */     
/* 177 */     return matches;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private DWCommand getCommandMatch(String arg) {
/* 185 */     for (Iterator<DWCommand> it = this.commands.iterator(); it.hasNext(); ) {
/*     */       
/* 187 */       DWCommand cmd = it.next();
/*     */       
/* 189 */       if (cmd.getCommand().startsWith(arg.toLowerCase()))
/*     */       {
/* 191 */         return cmd;
/*     */       }
/*     */     } 
/*     */     
/* 195 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean validate(String cmdline) {
/* 202 */     String[] args = cmdline.split(" ");
/*     */     
/* 204 */     if (cmdline.length() == 0)
/*     */     {
/*     */       
/* 207 */       return true;
/*     */     }
/*     */     
/* 210 */     int matches = numCommandMatches(args[0]);
/*     */     
/* 212 */     if (matches == 0)
/*     */     {
/*     */       
/* 215 */       return false;
/*     */     }
/* 217 */     if (matches > 1)
/*     */     {
/*     */       
/* 220 */       return false;
/*     */     }
/*     */ 
/*     */     
/* 224 */     return getCommandMatch(args[0]).validate(DWUtils.dropFirstToken(cmdline));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String colLayout(ArrayList<String> ps, int cols) {
/* 231 */     cols--;
/* 232 */     String text = new String();
/*     */     
/* 234 */     Iterator<String> it = ps.iterator();
/* 235 */     int maxlen = 1;
/* 236 */     while (it.hasNext()) {
/*     */       
/* 238 */       int curlen = ((String)it.next()).length();
/* 239 */       if (curlen > maxlen) {
/* 240 */         maxlen = curlen;
/*     */       }
/*     */     } 
/*     */     
/* 244 */     if (cols > 32) {
/* 245 */       maxlen += 2;
/*     */     } else {
/* 247 */       maxlen++;
/*     */     } 
/* 249 */     it = ps.iterator();
/* 250 */     int i = 0;
/* 251 */     int ll = cols / maxlen;
/* 252 */     while (it.hasNext()) {
/*     */       
/* 254 */       String itxt = String.format("%-" + maxlen + "s", new Object[] { it.next() });
/* 255 */       if (i > 0 && i % ll == 0) {
/* 256 */         text = text + "\r\n";
/*     */       }
/* 258 */       if (cols <= 32) {
/* 259 */         itxt = itxt.toUpperCase();
/*     */       }
/* 261 */       text = text + itxt;
/*     */       
/* 263 */       i++;
/*     */     } 
/*     */     
/* 266 */     text = text + "\r\n";
/*     */     
/* 268 */     return text;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public ArrayList<String> getCommandStrings() {
/* 274 */     return getCommandStrings(this, "");
/*     */   }
/*     */ 
/*     */   
/*     */   private ArrayList<String> getCommandStrings(DWCommandList commands, String prefix) {
/* 279 */     ArrayList<String> res = new ArrayList<String>();
/*     */     
/* 281 */     for (DWCommand cmd : commands.getCommands()) {
/*     */       
/* 283 */       res.add(prefix + " " + cmd.getCommand());
/* 284 */       if (cmd.getCommandList() != null)
/*     */       {
/* 286 */         res.addAll(getCommandStrings(cmd.getCommandList(), prefix + " " + cmd.getCommand()));
/*     */       }
/*     */     } 
/*     */     
/* 290 */     return res;
/*     */   }
/*     */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/dwcommands/DWCommandList.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */