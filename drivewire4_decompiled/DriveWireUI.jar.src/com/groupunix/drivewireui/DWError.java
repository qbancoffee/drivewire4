/*    */ package com.groupunix.drivewireui;
/*    */ 
/*    */ import java.util.regex.Matcher;
/*    */ import java.util.regex.Pattern;
/*    */ 
/*    */ public class DWError
/*    */ {
/*  8 */   private int errno = -1;
/*  9 */   private String title = null;
/* 10 */   private String summary = null;
/* 11 */   private String detail = null;
/*    */   
/*    */   private boolean gui = false;
/*    */   
/*    */   public DWError(String t, String s, String d, boolean gui) {
/* 16 */     this.title = t;
/* 17 */     this.summary = s;
/* 18 */     this.detail = d;
/* 19 */     this.gui = gui;
/*    */ 
/*    */ 
/*    */     
/* 23 */     StringBuffer myStringBuffer = new StringBuffer();
/* 24 */     Matcher m = Pattern.compile("FAIL \\d+").matcher(this.summary);
/* 25 */     while (m.find())
/*    */     {
/* 27 */       m.appendReplacement(myStringBuffer, MainWin.errorHelpCache.getErrMessage(Integer.parseInt(this.summary.substring(m.start() + 5, m.end()))) + ": ");
/*    */     }
/* 29 */     m.appendTail(myStringBuffer);
/*    */     
/* 31 */     this.summary = myStringBuffer.toString();
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public int getErrno() {
/* 37 */     return this.errno;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getTitle() {
/* 43 */     return this.title;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getSummary() {
/* 49 */     return this.summary;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getDetail() {
/* 55 */     return this.detail;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isGui() {
/* 60 */     return this.gui;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getTextError() {
/* 67 */     return this.summary;
/*    */   }
/*    */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireui/DWError.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */