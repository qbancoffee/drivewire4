/*    */ package com.groupunix.drivewireui;
/*    */ 
/*    */ public class BasicLine
/*    */ {
/*    */   private String line;
/*  6 */   private int lineno = -1;
/*    */ 
/*    */   
/*    */   BasicLine(String ln) {
/* 10 */     if (ln.matches("^\\d+\\s.+")) {
/*    */       
/* 12 */       setLine(ln.substring(ln.indexOf(" ")));
/* 13 */       setLineno(Integer.parseInt(ln.substring(0, ln.indexOf(" "))));
/*    */     }
/*    */     else {
/*    */       
/* 17 */       setLine(ln);
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public void setLine(String line) {
/* 23 */     this.line = line;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getLine() {
/* 28 */     return this.line;
/*    */   }
/*    */ 
/*    */   
/*    */   public void setLineno(int lineno) {
/* 33 */     this.lineno = lineno;
/*    */   }
/*    */ 
/*    */   
/*    */   public int getLineno() {
/* 38 */     return this.lineno;
/*    */   }
/*    */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireui/BasicLine.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */