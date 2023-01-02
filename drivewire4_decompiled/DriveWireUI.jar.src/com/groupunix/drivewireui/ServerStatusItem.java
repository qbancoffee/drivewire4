/*     */ package com.groupunix.drivewireui;
/*     */ 
/*     */ public class ServerStatusItem
/*     */ {
/*   5 */   private int interval = 0;
/*   6 */   private long memtotal = 0L;
/*   7 */   private long memfree = 0L;
/*   8 */   private long ops = 0L;
/*   9 */   private long diskops = 0L;
/*  10 */   private long vserialops = 0L;
/*  11 */   private int instances = 0;
/*  12 */   private int instancesalive = 0;
/*  13 */   private int threads = 0;
/*  14 */   private int uiclients = 0;
/*  15 */   private long magic = 0L;
/*     */ 
/*     */ 
/*     */   
/*     */   public void setInterval(int interval) {
/*  20 */     this.interval = interval;
/*     */   }
/*     */   
/*     */   public int getInterval() {
/*  24 */     return this.interval;
/*     */   }
/*     */   
/*     */   public void setMemtotal(long memtotal) {
/*  28 */     this.memtotal = memtotal;
/*     */   }
/*     */   
/*     */   public long getMemtotal() {
/*  32 */     return this.memtotal;
/*     */   }
/*     */   
/*     */   public void setMemfree(long memfree) {
/*  36 */     this.memfree = memfree;
/*     */   }
/*     */   
/*     */   public long getMemfree() {
/*  40 */     return this.memfree;
/*     */   }
/*     */   
/*     */   public void setOps(long ops) {
/*  44 */     this.ops = ops;
/*     */   }
/*     */   
/*     */   public long getOps() {
/*  48 */     return this.ops;
/*     */   }
/*     */   
/*     */   public void setDiskops(long diskops) {
/*  52 */     this.diskops = diskops;
/*     */   }
/*     */   
/*     */   public long getDiskops() {
/*  56 */     return this.diskops;
/*     */   }
/*     */   
/*     */   public void setVserialops(long vserialops) {
/*  60 */     this.vserialops = vserialops;
/*     */   }
/*     */   
/*     */   public long getVserialops() {
/*  64 */     return this.vserialops;
/*     */   }
/*     */   
/*     */   public void setInstances(int instances) {
/*  68 */     this.instances = instances;
/*     */   }
/*     */   
/*     */   public int getInstances() {
/*  72 */     return this.instances;
/*     */   }
/*     */   
/*     */   public void setInstancesalive(int instancesalive) {
/*  76 */     this.instancesalive = instancesalive;
/*     */   }
/*     */   
/*     */   public int getInstancesalive() {
/*  80 */     return this.instancesalive;
/*     */   }
/*     */   
/*     */   public void setThreads(int threads) {
/*  84 */     this.threads = threads;
/*     */   }
/*     */   
/*     */   public int getThreads() {
/*  88 */     return this.threads;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/*  93 */     String res = "";
/*     */     
/*  95 */     res = "Interval: " + this.interval + "  MemTot: " + this.memtotal + "  MemFree: " + this.memfree + "  Ops: " + this.ops + "  DiskOps: " + this.diskops;
/*  96 */     res = res + "  VSOps: " + this.vserialops + "  Instances: " + this.instances + "  InstAlive: " + this.instancesalive + "  Threads: " + this.threads + "  UIClients: " + this.uiclients;
/*     */     
/*  98 */     return res;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setUIClients(int uiclients) {
/* 103 */     this.uiclients = uiclients;
/*     */   }
/*     */   
/*     */   public int getUIClients() {
/* 107 */     return this.uiclients;
/*     */   }
/*     */   
/*     */   public void setMagic(long l) {
/* 111 */     this.magic = l;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getMagic() {
/* 116 */     return this.magic;
/*     */   }
/*     */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireui/ServerStatusItem.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */