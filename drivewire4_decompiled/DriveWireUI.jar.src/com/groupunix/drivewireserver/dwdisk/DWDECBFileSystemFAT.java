/*     */ package com.groupunix.drivewireserver.dwdisk;
/*     */ 
/*     */ import com.groupunix.drivewireserver.dwexceptions.DWDECBFileSystemFullException;
/*     */ import com.groupunix.drivewireserver.dwexceptions.DWDECBFileSystemInvalidFATException;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.Vector;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DWDECBFileSystemFAT
/*     */ {
/*     */   private DWDiskSector sector;
/*     */   
/*     */   public DWDECBFileSystemFAT(DWDiskSector sector) {
/*  67 */     this.sector = sector;
/*     */   }
/*     */ 
/*     */   
/*     */   public ArrayList<DWDiskSector> getFileSectors(Vector<DWDiskSector> sectors, byte granule) throws DWDECBFileSystemInvalidFATException, IOException {
/*  72 */     ArrayList<DWDiskSector> res = new ArrayList<DWDiskSector>();
/*     */     
/*  74 */     byte entry = getEntry(granule);
/*     */     
/*  76 */     while (!isLastEntry(entry)) {
/*     */       
/*  78 */       res.addAll(getGranuleSectors(sectors, granule));
/*     */       
/*  80 */       granule = entry;
/*  81 */       entry = getEntry(granule);
/*     */     } 
/*     */ 
/*     */     
/*  85 */     for (int i = 0; i < (entry & 0xF); i++)
/*     */     {
/*  87 */       res.add(sectors.get(getFirstSectorNoForGranule(granule) + i));
/*     */     }
/*     */ 
/*     */     
/*  91 */     return res;
/*     */   }
/*     */ 
/*     */   
/*     */   private List<DWDiskSector> getGranuleSectors(Vector<DWDiskSector> sectors, byte granule) {
/*  96 */     List<DWDiskSector> res = new ArrayList<DWDiskSector>();
/*     */     
/*  98 */     for (int i = 0; i < 9; i++)
/*     */     {
/* 100 */       res.add(sectors.get(getFirstSectorNoForGranule(granule) + i));
/*     */     }
/*     */     
/* 103 */     return res;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private int getFirstSectorNoForGranule(byte granule) {
/* 109 */     if ((granule & 0xFF) < 34) {
/* 110 */       return granule * 9;
/*     */     }
/*     */     
/* 113 */     return (granule + 2) * 9;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public byte getEntry(byte granule) throws DWDECBFileSystemInvalidFATException, IOException {
/* 120 */     if ((granule & 0xFF) <= 68) {
/* 121 */       if ((this.sector.getData()[granule & 0xFF] & 0xFF) == 255) {
/* 122 */         throw new DWDECBFileSystemInvalidFATException("Chain links to unused FAT entry #" + granule);
/*     */       }
/* 124 */       return this.sector.getData()[granule & 0xFF];
/*     */     } 
/* 126 */     throw new DWDECBFileSystemInvalidFATException("Invalid granule #" + granule);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isLastEntry(byte entry) {
/* 131 */     if ((entry & 0xC0) == 192) {
/* 132 */       return true;
/*     */     }
/* 134 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int getFreeGanules() throws IOException {
/* 140 */     int free = 0;
/*     */     
/* 142 */     for (int i = 0; i < 68; i++) {
/* 143 */       if ((this.sector.getData()[i] & 0xFF) == 255)
/* 144 */         free++; 
/*     */     } 
/* 146 */     return free;
/*     */   }
/*     */ 
/*     */   
/*     */   public byte allocate(int bytes) throws DWDECBFileSystemFullException, IOException {
/* 151 */     int allocated = 0;
/*     */     
/* 153 */     int sectorsneeded = bytes / 256 + 1;
/*     */     
/* 155 */     int granulesneeded = sectorsneeded / 9;
/*     */     
/* 157 */     if (sectorsneeded % 9 != 0) {
/* 158 */       granulesneeded++;
/*     */     }
/*     */ 
/*     */     
/* 162 */     if (getFreeGanules() < granulesneeded) {
/* 163 */       throw new DWDECBFileSystemFullException("Need " + granulesneeded + " granules, have only " + getFreeGanules() + " free.");
/*     */     }
/*     */     
/* 166 */     byte lastgran = getFreeGranule();
/* 167 */     byte firstgran = lastgran;
/* 168 */     allocated++;
/*     */     
/* 170 */     while (allocated < granulesneeded) {
/*     */       
/* 172 */       setEntry(lastgran, (byte)-64);
/* 173 */       byte nextgran = getFreeGranule();
/* 174 */       setEntry(lastgran, nextgran);
/*     */       
/* 176 */       lastgran = nextgran;
/* 177 */       allocated++;
/*     */     } 
/*     */     
/* 180 */     setEntry(lastgran, (byte)(sectorsneeded % 9 + 192));
/*     */     
/* 182 */     return firstgran;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void setEntry(byte gran, byte nextgran) throws IOException {
/* 188 */     this.sector.setDataByte(0xFF & gran, nextgran);
/* 189 */     this.sector.makeDirty();
/*     */   }
/*     */ 
/*     */   
/*     */   private byte getFreeGranule() throws IOException {
/* 194 */     for (byte i = 0; i < 68; i = (byte)(i + 1)) {
/*     */       
/* 196 */       if ((this.sector.getData()[i] & 0xFF) == 255)
/* 197 */         return i; 
/*     */     } 
/* 199 */     return -1;
/*     */   }
/*     */ 
/*     */   
/*     */   public String dumpFat() throws IOException {
/* 204 */     String res = "";
/*     */     
/* 206 */     for (int i = 0; i < 68; i++) {
/* 207 */       if (this.sector.getData()[i] != -1)
/* 208 */         res = res + i + ": " + this.sector.getData()[i] + "\t\t"; 
/*     */     } 
/* 210 */     return res;
/*     */   }
/*     */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/dwdisk/DWDECBFileSystemFAT.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */