/*     */ package com.groupunix.drivewireui;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import org.eclipse.swt.graphics.ImageData;
/*     */ import org.eclipse.swt.graphics.PaletteData;
/*     */ import org.eclipse.swt.graphics.RGB;
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
/*     */ public class Blur
/*     */ {
/*     */   public static ImageData blur(ImageData originalImageData, int radius) {
/*  39 */     if (radius < 1) return originalImageData;
/*     */     
/*  41 */     ImageData newImageData = new ImageData(originalImageData.width, originalImageData.height, 24, new PaletteData(255, 65280, 16711680));
/*  42 */     if (radius >= newImageData.height || radius >= newImageData.width) {
/*  43 */       radius = Math.min(newImageData.height, newImageData.width) - 1;
/*     */     }
/*  45 */     ArrayList<RGB[]> rowCache = new ArrayList();
/*  46 */     int cacheSize = (radius * 2 + 1 > newImageData.height) ? newImageData.height : (radius * 2 + 1);
/*     */     
/*  48 */     int cacheStartIndex = 0;
/*  49 */     for (int row = 0; row < cacheSize; row++)
/*     */     {
/*  51 */       rowCache.add(rowCache.size(), blurRow(originalImageData, row, radius));
/*     */     }
/*     */     
/*  54 */     RGB[] rowRGBSums = new RGB[newImageData.width];
/*  55 */     int[] rowRGBAverages = new int[newImageData.width];
/*  56 */     int topSumBoundary = 0;
/*  57 */     int targetRow = 0;
/*  58 */     int bottomSumBoundary = 0;
/*  59 */     int numRows = 0;
/*  60 */     for (int i = 0; i < newImageData.width; i++)
/*  61 */       rowRGBSums[i] = new RGB(0, 0, 0); 
/*  62 */     while (targetRow < newImageData.height) {
/*  63 */       if (bottomSumBoundary < newImageData.height) {
/*     */         do
/*     */         {
/*  66 */           for (int j = 0; j < newImageData.width; j++) {
/*  67 */             (rowRGBSums[j]).red += (((RGB[])rowCache.get(bottomSumBoundary - cacheStartIndex))[j]).red;
/*     */             
/*  69 */             (rowRGBSums[j]).green += (((RGB[])rowCache.get(bottomSumBoundary - cacheStartIndex))[j]).green;
/*     */             
/*  71 */             (rowRGBSums[j]).blue += (((RGB[])rowCache.get(bottomSumBoundary - cacheStartIndex))[j]).blue;
/*     */           } 
/*     */           
/*  74 */           numRows++;
/*  75 */           bottomSumBoundary++;
/*  76 */           if (bottomSumBoundary >= newImageData.height || bottomSumBoundary - cacheStartIndex <= radius * 2) {
/*     */             continue;
/*     */           }
/*  79 */           rowCache.add(rowCache.size(), blurRow(originalImageData, bottomSumBoundary, radius));
/*     */         }
/*  81 */         while (bottomSumBoundary <= radius);
/*     */       }
/*  83 */       if (targetRow - topSumBoundary > radius) {
/*     */         
/*  85 */         for (int j = 0; j < newImageData.width; j++) {
/*  86 */           (rowRGBSums[j]).red -= (((RGB[])rowCache.get(topSumBoundary - cacheStartIndex))[j]).red;
/*     */           
/*  88 */           (rowRGBSums[j]).green -= (((RGB[])rowCache.get(topSumBoundary - cacheStartIndex))[j]).green;
/*     */           
/*  90 */           (rowRGBSums[j]).blue -= (((RGB[])rowCache.get(topSumBoundary - cacheStartIndex))[j]).blue;
/*     */         } 
/*     */         
/*  93 */         numRows--;
/*  94 */         topSumBoundary++;
/*  95 */         rowCache.remove(0);
/*  96 */         cacheStartIndex++;
/*     */       } 
/*     */       
/*  99 */       for (int col = 0; col < newImageData.width; col++) {
/* 100 */         rowRGBAverages[col] = newImageData.palette.getPixel(new RGB((rowRGBSums[col]).red / numRows, (rowRGBSums[col]).green / numRows, (rowRGBSums[col]).blue / numRows));
/*     */       }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 108 */       newImageData.setPixels(0, targetRow, newImageData.width, rowRGBAverages, 0);
/* 109 */       targetRow++;
/*     */     } 
/* 111 */     return newImageData;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static RGB[] blurRow(ImageData originalImageData, int row, int radius) {
/* 119 */     RGB[] rowRGBAverages = new RGB[originalImageData.width];
/* 120 */     int[] lineData = new int[originalImageData.width];
/* 121 */     originalImageData.getPixels(0, row, originalImageData.width, lineData, 0);
/* 122 */     int r = 0, g = 0, b = 0;
/* 123 */     int leftSumBoundary = 0;
/* 124 */     int targetColumn = 0;
/* 125 */     int rightSumBoundary = 0;
/* 126 */     int numCols = 0;
/*     */     
/* 128 */     while (targetColumn < lineData.length) {
/* 129 */       if (rightSumBoundary < lineData.length) {
/*     */         
/*     */         do {
/* 132 */           RGB rgb = originalImageData.palette.getRGB(lineData[rightSumBoundary]);
/* 133 */           r += rgb.red;
/* 134 */           g += rgb.green;
/* 135 */           b += rgb.blue;
/* 136 */           numCols++;
/* 137 */           ++rightSumBoundary;
/* 138 */         } while (rightSumBoundary <= radius);
/*     */       }
/*     */       
/* 141 */       if (targetColumn - leftSumBoundary > radius) {
/* 142 */         RGB rgb = originalImageData.palette.getRGB(lineData[leftSumBoundary]);
/* 143 */         r -= rgb.red;
/* 144 */         g -= rgb.green;
/* 145 */         b -= rgb.blue;
/* 146 */         numCols--;
/* 147 */         leftSumBoundary++;
/*     */       } 
/*     */       
/* 150 */       rowRGBAverages[targetColumn] = new RGB(r / numCols, g / numCols, b / numCols);
/* 151 */       targetColumn++;
/*     */     } 
/* 153 */     return rowRGBAverages;
/*     */   }
/*     */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireui/Blur.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */