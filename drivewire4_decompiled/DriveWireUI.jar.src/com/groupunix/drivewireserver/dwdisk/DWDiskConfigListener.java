/*    */ package com.groupunix.drivewireserver.dwdisk;
/*    */ 
/*    */ import org.apache.commons.configuration.event.ConfigurationEvent;
/*    */ import org.apache.commons.configuration.event.ConfigurationListener;
/*    */ 
/*    */ 
/*    */ public class DWDiskConfigListener
/*    */   implements ConfigurationListener
/*    */ {
/*    */   private DWDisk disk;
/*    */   
/*    */   public DWDiskConfigListener(DWDisk disk) {
/* 13 */     this.disk = disk;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void configurationChanged(ConfigurationEvent event) {
/* 19 */     if (!event.isBeforeUpdate())
/*    */     {
/* 21 */       if (event.getPropertyName() != null)
/*    */       {
/*    */         
/* 24 */         if (event.getPropertyValue() != null) {
/* 25 */           this.disk.submitEvent(event.getPropertyName(), event.getPropertyValue().toString());
/*    */         } else {
/* 27 */           this.disk.submitEvent(event.getPropertyName(), "");
/*    */         } 
/*    */       }
/*    */     }
/*    */   }
/*    */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/dwdisk/DWDiskConfigListener.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */