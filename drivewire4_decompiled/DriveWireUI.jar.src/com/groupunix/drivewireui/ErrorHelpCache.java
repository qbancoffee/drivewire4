/*    */ package com.groupunix.drivewireui;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.util.List;
/*    */ import java.util.Vector;
/*    */ 
/*    */ public class ErrorHelpCache
/*    */ {
/*  9 */   private Vector<String> errhelp = new Vector<String>();
/*    */ 
/*    */   
/*    */   public ErrorHelpCache() {
/* 13 */     this.errhelp.setSize(256);
/*    */   }
/*    */ 
/*    */   
/*    */   public String getErrMessage(int err) {
/* 18 */     if (err < this.errhelp.size() && this.errhelp.get(err) != null)
/*    */     {
/* 20 */       return this.errhelp.get(err);
/*    */     }
/*    */     
/* 23 */     return "Error " + err;
/*    */   }
/*    */ 
/*    */   
/*    */   public void setErrMessage(int err, String msg) {
/* 28 */     if (err > -1 && err < this.errhelp.size()) {
/* 29 */       this.errhelp.set(err, msg);
/*    */     }
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void load() throws IOException, DWUIOperationFailedException {
/* 36 */     List<String> res = UIUtils.loadList(MainWin.getInstance(), "ui server show errors");
/*    */     
/* 38 */     for (String r : res) {
/*    */ 
/*    */       
/* 41 */       String[] parts = r.trim().split("\\|");
/*    */       
/* 43 */       if (parts.length == 2) {
/*    */         
/*    */         try {
/*    */           
/* 47 */           setErrMessage(Integer.parseInt(parts[0]), parts[1]);
/*    */         }
/* 49 */         catch (NumberFormatException e) {}
/*    */       }
/*    */     } 
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String dump() {
/* 61 */     String res = "errorHelpCache contents:\n\n";
/* 62 */     for (int i = 0; i < this.errhelp.size(); i++) {
/*    */       
/* 64 */       if (this.errhelp.get(i) != null)
/*    */       {
/* 66 */         res = res + i + ":" + (String)this.errhelp.get(i) + "\n";
/*    */       }
/*    */     } 
/*    */     
/* 70 */     return res;
/*    */   }
/*    */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireui/ErrorHelpCache.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */