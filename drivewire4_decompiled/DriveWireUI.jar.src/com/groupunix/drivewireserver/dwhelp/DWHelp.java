/*     */ package com.groupunix.drivewireserver.dwhelp;
/*     */ 
/*     */ import com.groupunix.drivewireserver.dwcommands.DWCmd;
/*     */ import com.groupunix.drivewireserver.dwcommands.DWCommand;
/*     */ import com.groupunix.drivewireserver.dwexceptions.DWHelpTopicNotFoundException;
/*     */ import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocol;
/*     */ import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocolHandler;
/*     */ import java.io.IOException;
/*     */ import java.net.URL;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import net.htmlparser.jericho.Source;
/*     */ import org.apache.commons.configuration.ConfigurationException;
/*     */ import org.apache.commons.configuration.XMLConfiguration;
/*     */ import org.apache.log4j.Logger;
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DWHelp
/*     */ {
/*     */   private XMLConfiguration help;
/*  22 */   private String helpfile = null;
/*     */   
/*  24 */   private static final Logger logger = Logger.getLogger("DWHelp");
/*     */ 
/*     */   
/*     */   public DWHelp(String helpfile) {
/*  28 */     this.helpfile = helpfile;
/*     */     
/*  30 */     reload();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DWHelp(DWProtocolHandler dwProto) {
/*  38 */     this.help = new XMLConfiguration();
/*  39 */     addAllTopics((DWCommand)new DWCmd((DWProtocol)dwProto), "");
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
/*     */   public void reload() {
/*  53 */     logger.info("reading help from '" + this.helpfile + "'");
/*     */ 
/*     */ 
/*     */     
/*     */     try {
/*  58 */       this.help = new XMLConfiguration(this.helpfile);
/*     */ 
/*     */ 
/*     */       
/*  62 */       this.help.setListDelimiter('\n');
/*     */ 
/*     */ 
/*     */     
/*     */     }
/*  67 */     catch (ConfigurationException e1) {
/*     */       
/*  69 */       logger.warn("Error loading help file: " + e1.getMessage());
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void loadWikiTopics(String sourceUrlString) throws IOException {
/*  78 */     Source source = new Source(new URL(sourceUrlString));
/*  79 */     source.getRenderer().setMaxLineLength(32);
/*  80 */     String renderedText = source.getRenderer().toString();
/*     */     
/*  82 */     String[] lines = renderedText.split("\n");
/*     */     
/*  84 */     String curkey = null;
/*  85 */     int blanks = 0;
/*     */     
/*  87 */     for (int i = 0; i < lines.length; i++) {
/*     */       
/*  89 */       lines[i] = lines[i].trim();
/*     */ 
/*     */       
/*  92 */       if (hasTopic(lines[i])) {
/*     */         
/*  94 */         System.out.println("Topic: " + lines[i]);
/*     */         
/*  96 */         curkey = lines[i];
/*  97 */         clearTopic(lines[i]);
/*  98 */         blanks = 0;
/*     */       }
/*     */       else {
/*     */         
/* 102 */         System.out.println("maybe: " + lines[i]);
/* 103 */         if (lines[i].equals("")) {
/*     */           
/* 105 */           blanks++;
/*     */         }
/*     */         else {
/*     */           
/* 109 */           blanks = 0;
/*     */         } 
/*     */         
/* 112 */         if (blanks < 2 && curkey != null) {
/*     */           
/* 114 */           System.out.println("Line: " + lines[i]);
/*     */           
/* 116 */           this.help.addProperty("topics." + spaceToDot(curkey) + ".text", lines[i]);
/*     */         } 
/*     */         
/* 119 */         if (blanks == 3)
/*     */         {
/* 121 */           curkey = null;
/*     */         }
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void clearTopic(String topic) {
/* 133 */     this.help.clearTree("topics." + spaceToDot(topic) + ".text");
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean hasTopic(String topic) {
/* 138 */     if (this.help != null)
/* 139 */       return this.help.containsKey("topics." + spaceToDot(topic) + ".text"); 
/* 140 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getTopicText(String topic) throws DWHelpTopicNotFoundException {
/* 147 */     if (hasTopic(topic)) {
/*     */       
/* 149 */       String text = new String();
/*     */       
/* 151 */       topic = spaceToDot(topic);
/*     */ 
/*     */       
/* 154 */       String[] txts = this.help.getStringArray("topics." + topic + ".text");
/*     */       
/* 156 */       for (int i = 0; i < txts.length; i++)
/*     */       {
/* 158 */         text = text + txts[i] + "\r\n";
/*     */       }
/*     */       
/* 161 */       return text;
/*     */     } 
/*     */ 
/*     */     
/* 165 */     throw new DWHelpTopicNotFoundException("There is no help available for the topic '" + topic + "'.");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ArrayList<String> getTopics(String topic) {
/* 172 */     ArrayList<String> res = new ArrayList<String>();
/*     */     
/* 174 */     if (this.help != null)
/*     */     {
/* 176 */       for (Iterator<String> itk = this.help.configurationAt("topics").getKeys(); itk.hasNext(); ) {
/*     */         
/* 178 */         String key = itk.next();
/* 179 */         if (key.endsWith(".text")) {
/* 180 */           res.add(dotToSpace(key.substring(0, key.length() - 5)));
/*     */         }
/*     */       } 
/*     */     }
/*     */     
/* 185 */     return res;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private String spaceToDot(String topic) {
/* 191 */     return topic.replaceAll(" ", "\\.");
/*     */   }
/*     */ 
/*     */   
/*     */   private String dotToSpace(String topic) {
/* 196 */     return topic.replaceAll("\\.", " ");
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void addAllTopics(DWCommand dwc, String prefix) {
/* 202 */     String key = "topics.";
/*     */     
/* 204 */     if (!prefix.equals("")) {
/* 205 */       key = key + spaceToDot(prefix) + ".";
/*     */     }
/* 207 */     key = key + spaceToDot(dwc.getCommand()) + ".text";
/*     */     
/* 209 */     this.help.addProperty(key, dwc.getUsage());
/* 210 */     this.help.addProperty(key, "");
/* 211 */     this.help.addProperty(key, dwc.getShortHelp());
/*     */     
/* 213 */     if (dwc.getCommandList() != null)
/*     */     {
/* 215 */       for (DWCommand dwsc : dwc.getCommandList().getCommands()) {
/*     */         
/* 217 */         if (!prefix.equals("")) {
/* 218 */           addAllTopics(dwsc, prefix + " " + dwc.getCommand()); continue;
/*     */         } 
/* 220 */         addAllTopics(dwsc, dwc.getCommand());
/*     */       } 
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ArrayList<String> getSectionTopics(String section) {
/* 230 */     ArrayList<String> res = new ArrayList<String>();
/* 231 */     if (this.help != null)
/*     */     {
/* 233 */       for (Iterator<String> itk = this.help.configurationAt("topics." + section).getKeys(); itk.hasNext(); ) {
/*     */         
/* 235 */         String key = itk.next();
/* 236 */         if (key.endsWith(".text")) {
/* 237 */           res.add(dotToSpace(key.substring(0, key.length() - 5)));
/*     */         }
/*     */       } 
/*     */     }
/* 241 */     return res;
/*     */   }
/*     */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/dwhelp/DWHelp.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */