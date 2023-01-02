/*    */ package com.groupunix.drivewireui;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import org.apache.commons.vfs.FileObject;
/*    */ import org.apache.commons.vfs.FileSystemException;
/*    */ import org.apache.commons.vfs.FileSystemManager;
/*    */ import org.apache.commons.vfs.FileType;
/*    */ import org.apache.commons.vfs.VFS;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class DWBrowserUtils
/*    */ {
/*    */   public static void GenerateHTMLDir(String path) {
/* 16 */     String res = "<HTML><HEAD><TITLE>Disks at " + path + "</TITLE></HEAD>";
/* 17 */     String diskhtml = "";
/* 18 */     String dirhtml = "<table>";
/*    */     
/* 20 */     res = res + "<BODY style='font-family:arial;'>";
/*    */ 
/*    */ 
/*    */     
/*    */     try {
/* 25 */       FileSystemManager fsManager = VFS.getManager();
/* 26 */       FileObject dir = fsManager.resolveFile(path);
/*    */ 
/*    */       
/* 29 */       FileObject[] children = dir.getChildren();
/*    */       
/* 31 */       for (int i = 0; i < children.length; i++) {
/*    */         
/* 33 */         if (children[i].getType() == FileType.FOLDER) {
/*    */           
/* 35 */           dirhtml = dirhtml + "<tr><td><a href='" + children[i].getURL() + "'><img src='file:///" + System.getProperty("user.dir") + "/html/images/folder.png'></a></td>";
/*    */           
/* 37 */           dirhtml = dirhtml + "<td><a href='" + children[i].getURL() + "'>" + children[i].getName().getBaseName() + "</a></td></tr>";
/*    */         
/*    */         }
/*    */         else {
/*    */           
/* 42 */           diskhtml = diskhtml + children[i].getName().toString() + "<br>";
/*    */         } 
/*    */       } 
/*    */       
/* 46 */       res = res + dirhtml + "</table>";
/* 47 */       res = res + "<hr>";
/* 48 */       res = res + diskhtml;
/*    */       
/* 50 */       res = res + "</BODY></HTML>";
/*    */ 
/*    */       
/* 53 */       FileObject out = fsManager.resolveFile(path + "/dw_disks.html");
/*    */       
/* 55 */       if ((out.exists() && out.delete()) || !out.exists())
/*    */       {
/* 57 */         out.createFile();
/* 58 */         out.getContent().getOutputStream().write(res.getBytes());
/* 59 */         out.close();
/*    */       
/*    */       }
/*    */     
/*    */     }
/* 64 */     catch (FileSystemException e) {
/*    */ 
/*    */       
/* 67 */       e.printStackTrace();
/* 68 */     } catch (IOException e) {
/*    */ 
/*    */       
/* 71 */       e.printStackTrace();
/*    */     } 
/*    */   }
/*    */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireui/DWBrowserUtils.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */