/*    */ package com.groupunix.drivewireui;
/*    */ 
/*    */ import org.eclipse.swt.graphics.Image;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class DiskTableUpdate
/*    */ {
/*    */   private Object value;
/*    */   private int disk;
/*    */   private String key;
/*    */   
/*    */   public DiskTableUpdate(int disk, String key, String val) {
/* 14 */     this.disk = disk;
/* 15 */     this.key = key;
/*    */     
/* 17 */     if (val == null) {
/* 18 */       this.value = "";
/*    */     } else {
/* 20 */       this.value = val;
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public DiskTableUpdate(int disk, String key, Image img) {
/* 26 */     this.disk = disk;
/* 27 */     this.key = key;
/*    */     
/* 29 */     this.value = img;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getKey() {
/* 36 */     return this.key;
/*    */   }
/*    */ 
/*    */   
/*    */   public Object getValue() {
/* 41 */     return this.value;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public int getDisk() {
/* 47 */     return this.disk;
/*    */   }
/*    */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireui/DiskTableUpdate.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */