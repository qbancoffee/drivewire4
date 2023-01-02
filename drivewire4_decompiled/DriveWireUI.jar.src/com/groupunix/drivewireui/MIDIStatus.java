/*     */ package com.groupunix.drivewireui;
/*     */ 
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.Set;
/*     */ 
/*     */ public class MIDIStatus
/*     */ {
/*   8 */   private String currentProfile = null;
/*   9 */   private String currentDevice = null;
/*     */   
/*     */   private LinkedHashMap<String, MIDIProfile> profiles;
/*     */   
/*     */   private LinkedHashMap<String, MIDIDevice> devices;
/*     */   
/*     */   private boolean enabled = false;
/*     */   private boolean voicelock = false;
/*     */   
/*     */   public MIDIStatus() {
/*  19 */     this.profiles = new LinkedHashMap<String, MIDIProfile>();
/*  20 */     this.devices = new LinkedHashMap<String, MIDIDevice>();
/*     */   }
/*     */ 
/*     */   
/*     */   public Set<String> getProfiles() {
/*  25 */     return this.profiles.keySet();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Set<String> getDevices() {
/*  31 */     return this.devices.keySet();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void setCurrentDevice(String currentDevice) {
/*  37 */     this.currentDevice = currentDevice;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void addDevice(int devno, String type, String name, String desc, String vendor, String version) {
/*  43 */     this.devices.put(name, new MIDIDevice(devno, type, name, desc, vendor, version));
/*     */   }
/*     */ 
/*     */   
/*     */   public MIDIDevice getDevice(String key) {
/*  48 */     if (this.devices.containsKey(key))
/*  49 */       return this.devices.get(key); 
/*  50 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public void addProfile(String name, String desc) {
/*  55 */     this.profiles.put(name, new MIDIProfile(name, desc));
/*     */   }
/*     */ 
/*     */   
/*     */   public MIDIProfile getProfile(String key) {
/*  60 */     if (this.profiles.containsKey(key))
/*     */     {
/*  62 */       return this.profiles.get(key);
/*     */     }
/*     */     
/*  65 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getCurrentDevice() {
/*  70 */     return this.currentDevice;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setCurrentProfile(String currentProfile) {
/*  75 */     this.currentProfile = currentProfile;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getCurrentProfile() {
/*  80 */     return this.currentProfile;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void setEnabled(boolean enabled) {
/*  86 */     this.enabled = enabled;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isEnabled() {
/*  91 */     return this.enabled;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setVoiceLock(boolean b) {
/*  96 */     this.voicelock = b;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isVoiceLock() {
/* 102 */     return this.voicelock;
/*     */   }
/*     */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireui/MIDIStatus.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */