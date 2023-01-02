/*    */ package com.groupunix.drivewireserver.dwprotocolhandler;
/*    */ 
/*    */ import org.apache.commons.configuration.event.ConfigurationEvent;
/*    */ import org.apache.commons.configuration.event.ConfigurationListener;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class DWProtocolConfigListener
/*    */   implements ConfigurationListener
/*    */ {
/*    */   private DWProtocol dwProto;
/*    */   
/*    */   public DWProtocolConfigListener(DWProtocol dwProto) {
/* 15 */     this.dwProto = dwProto;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void configurationChanged(ConfigurationEvent event) {
/* 21 */     if (!event.isBeforeUpdate())
/*    */     {
/* 23 */       if (event.getPropertyName() != null)
/*    */       {
/* 25 */         if (event.getPropertyValue() == null) {
/*    */           
/* 27 */           this.dwProto.submitConfigEvent(event.getPropertyName(), "");
/*    */         }
/*    */         else {
/*    */           
/* 31 */           this.dwProto.submitConfigEvent(event.getPropertyName(), event.getPropertyValue().toString());
/*    */         } 
/*    */       }
/*    */     }
/*    */   }
/*    */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/dwprotocolhandler/DWProtocolConfigListener.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */