/*    */ package com.groupunix.drivewireserver;
/*    */ 
/*    */ import org.apache.commons.configuration.event.ConfigurationEvent;
/*    */ import org.apache.commons.configuration.event.ConfigurationListener;
/*    */ import org.apache.log4j.Logger;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class DWServerConfigListener
/*    */   implements ConfigurationListener
/*    */ {
/* 12 */   private static final Logger logger = Logger.getLogger("DWServer.DWServerConfigListener");
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void configurationChanged(ConfigurationEvent event) {
/* 19 */     if (!event.isBeforeUpdate()) {
/*    */ 
/*    */       
/* 22 */       DriveWireServer.configserial++;
/*    */ 
/*    */       
/* 25 */       if (event.getPropertyName() != null) {
/*    */         
/* 27 */         DriveWireServer.submitServerConfigEvent(event.getPropertyName(), event.getPropertyValue().toString());
/*    */ 
/*    */         
/* 30 */         if (event.getPropertyName().startsWith("Log"))
/*    */         {
/* 32 */           DriveWireServer.setLoggingRestart();
/*    */         }
/*    */ 
/*    */         
/* 36 */         if (event.getPropertyName().startsWith("UI"))
/*    */         {
/* 38 */           if (!DriveWireServer.isConfigFreeze())
/*    */           {
/*    */ 
/*    */ 
/*    */ 
/*    */             
/* 44 */             DriveWireServer.setUIRestart();
/*    */           }
/*    */         }
/*    */       } 
/*    */     } 
/*    */   }
/*    */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/DWServerConfigListener.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */