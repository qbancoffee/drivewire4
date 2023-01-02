/*    */ package com.groupunix.drivewireserver.uicommands;
/*    */ 
/*    */ import com.groupunix.drivewireserver.dwcommands.DWCommand;
/*    */ import com.groupunix.drivewireserver.dwcommands.DWCommandResponse;
/*    */ import java.net.InetAddress;
/*    */ import java.net.NetworkInterface;
/*    */ import java.net.SocketException;
/*    */ import java.util.Collections;
/*    */ import java.util.Enumeration;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class UICmdServerShowNet
/*    */   extends DWCommand
/*    */ {
/*    */   public String getCommand() {
/* 18 */     return "net";
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getShortHelp() {
/* 24 */     return "show available network interfaces";
/*    */   }
/*    */ 
/*    */   
/*    */   public String getUsage() {
/* 29 */     return "ui server show net";
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public DWCommandResponse parse(String cmdline) {
/* 35 */     String res = new String();
/*    */ 
/*    */ 
/*    */     
/*    */     try {
/* 40 */       Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
/* 41 */       for (NetworkInterface netint : Collections.<NetworkInterface>list(nets))
/*    */       {
/*    */         
/* 44 */         Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
/* 45 */         for (InetAddress inetAddress : Collections.<InetAddress>list(inetAddresses))
/*    */         {
/* 47 */           res = res + inetAddress.getHostAddress() + "|" + netint.getName() + "|" + netint.getDisplayName() + "\r\n";
/*    */         
/*    */         }
/*    */       }
/*    */     
/*    */     }
/* 53 */     catch (SocketException e) {
/*    */       
/* 55 */       return new DWCommandResponse(false, (byte)121, e.getMessage());
/*    */     } 
/*    */ 
/*    */     
/* 59 */     return new DWCommandResponse(res);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean validate(String cmdline) {
/* 64 */     return true;
/*    */   }
/*    */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/uicommands/UICmdServerShowNet.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */