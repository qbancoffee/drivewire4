/*    */ package com.groupunix.drivewireserver;
/*    */ 
/*    */ import java.util.HashMap;
/*    */ import java.util.Set;
/*    */ 
/*    */ public class DWEvent
/*    */ {
/*    */   private byte eventType;
/*  9 */   private HashMap<String, String> params = new HashMap<String, String>();
/*    */ 
/*    */   
/*    */   public DWEvent(byte eventType) {
/* 13 */     setEventType(eventType);
/*    */   }
/*    */ 
/*    */   
/*    */   public void setParam(String key, String val) {
/* 18 */     this.params.put(key, val);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean hasParam(String key) {
/* 23 */     return this.params.containsKey(key);
/*    */   }
/*    */ 
/*    */   
/*    */   public String getParam(String key) {
/* 28 */     if (this.params.containsKey(key))
/* 29 */       return this.params.get(key); 
/* 30 */     return null;
/*    */   }
/*    */ 
/*    */   
/*    */   public Set<String> getParamKeys() {
/* 35 */     return this.params.keySet();
/*    */   }
/*    */   
/*    */   public void setEventType(byte eventType) {
/* 39 */     this.eventType = eventType;
/*    */   }
/*    */   
/*    */   public byte getEventType() {
/* 43 */     return this.eventType;
/*    */   }
/*    */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/DWEvent.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */