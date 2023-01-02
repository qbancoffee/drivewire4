/*    */ package com.groupunix.drivewireui;
/*    */ 
/*    */ import org.eclipse.swt.graphics.Color;
/*    */ import org.eclipse.swt.graphics.Device;
/*    */ import org.eclipse.swt.graphics.Drawable;
/*    */ import org.eclipse.swt.graphics.GC;
/*    */ import org.eclipse.swt.graphics.Image;
/*    */ import org.eclipse.swt.graphics.Rectangle;
/*    */ import org.eclipse.swt.widgets.Composite;
/*    */ 
/*    */ 
/*    */ public class GradientHelper
/*    */ {
/*    */   public static void applyVerticalGradientBG(Composite composite, Color scol, Color ecol) {
/* 15 */     Rectangle rect = composite.getClientArea();
/*    */     
/* 17 */     Image newImage = new Image((Device)composite.getDisplay(), 1, Math.max(1, rect.height));
/*    */     
/* 19 */     GC gc = new GC((Drawable)newImage);
/* 20 */     gc.setForeground(scol);
/* 21 */     gc.setBackground(ecol);
/* 22 */     gc.fillGradientRectangle(0, 0, 1, rect.height, true);
/*    */     
/* 24 */     gc.dispose();
/* 25 */     composite.setBackgroundImage(newImage);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static void applyHorizontalGradientBG(Composite composite, Color scol, Color ecol) {
/* 35 */     Rectangle rect = composite.getClientArea();
/*    */     
/* 37 */     Image newImage = new Image((Device)composite.getDisplay(), Math.max(1, rect.height), 1);
/*    */     
/* 39 */     GC gc = new GC((Drawable)newImage);
/* 40 */     gc.setForeground(scol);
/* 41 */     gc.setBackground(ecol);
/* 42 */     gc.fillGradientRectangle(0, 0, rect.width, 1, false);
/*    */     
/* 44 */     gc.dispose();
/* 45 */     composite.setBackgroundImage(newImage);
/*    */   }
/*    */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireui/GradientHelper.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */