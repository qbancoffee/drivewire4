/*    */ package com.groupunix.drivewireui;
/*    */ 
/*    */ import org.apache.commons.configuration.HierarchicalConfiguration;
/*    */ 
/*    */ 
/*    */ public class ConfigItem
/*    */   implements Comparable<ConfigItem>
/*    */ {
/*    */   HierarchicalConfiguration.Node node;
/*    */   private int index;
/*    */   
/*    */   public ConfigItem(HierarchicalConfiguration.Node node, int index) {
/* 13 */     this.index = index;
/* 14 */     this.node = node;
/*    */   }
/*    */ 
/*    */   
/*    */   public HierarchicalConfiguration.Node getNode() {
/* 19 */     return this.node;
/*    */   }
/*    */ 
/*    */   
/*    */   public int getIndex() {
/* 24 */     return this.index;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public int compareTo(ConfigItem o) {
/* 30 */     if (o.getNode().hasChildren() && !getNode().hasChildren()) {
/* 31 */       return -1;
/*    */     }
/* 33 */     if (!o.getNode().hasChildren() && getNode().hasChildren()) {
/* 34 */       return 1;
/*    */     }
/* 36 */     return -1 * o.getNode().getName().compareTo(getNode().getName());
/*    */   }
/*    */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireui/ConfigItem.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */