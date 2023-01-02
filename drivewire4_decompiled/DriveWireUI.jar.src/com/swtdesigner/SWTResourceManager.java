/*     */ package com.swtdesigner;
/*     */ 
/*     */ import java.io.FileInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import org.eclipse.swt.graphics.Color;
/*     */ import org.eclipse.swt.graphics.Cursor;
/*     */ import org.eclipse.swt.graphics.Device;
/*     */ import org.eclipse.swt.graphics.Drawable;
/*     */ import org.eclipse.swt.graphics.Font;
/*     */ import org.eclipse.swt.graphics.FontData;
/*     */ import org.eclipse.swt.graphics.GC;
/*     */ import org.eclipse.swt.graphics.Image;
/*     */ import org.eclipse.swt.graphics.ImageData;
/*     */ import org.eclipse.swt.graphics.RGB;
/*     */ import org.eclipse.swt.graphics.Rectangle;
/*     */ import org.eclipse.swt.widgets.Display;
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
/*     */ public class SWTResourceManager
/*     */ {
/*  39 */   private static Map<RGB, Color> m_colorMap = new HashMap<RGB, Color>();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Color getColor(int systemColorID) {
/*  48 */     Display display = Display.getCurrent();
/*  49 */     return display.getSystemColor(systemColorID);
/*     */   }
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
/*     */   public static Color getColor(int r, int g, int b) {
/*  63 */     return getColor(new RGB(r, g, b));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Color getColor(RGB rgb) {
/*  73 */     Color color = m_colorMap.get(rgb);
/*  74 */     if (color == null) {
/*  75 */       Display display = Display.getCurrent();
/*  76 */       color = new Color((Device)display, rgb);
/*  77 */       m_colorMap.put(rgb, color);
/*     */     } 
/*  79 */     return color;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static void disposeColors() {
/*  85 */     for (Color color : m_colorMap.values()) {
/*  86 */       color.dispose();
/*     */     }
/*  88 */     m_colorMap.clear();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  98 */   private static Map<String, Image> m_imageMap = new HashMap<String, Image>();
/*     */   private static final int MISSING_IMAGE_SIZE = 10;
/*     */   public static final int TOP_LEFT = 1;
/*     */   public static final int TOP_RIGHT = 2;
/*     */   public static final int BOTTOM_LEFT = 3;
/*     */   public static final int BOTTOM_RIGHT = 4;
/*     */   protected static final int LAST_CORNER_KEY = 5;
/*     */   
/*     */   protected static Image getImage(InputStream stream) throws IOException {
/*     */     try {
/* 108 */       Display display = Display.getCurrent();
/* 109 */       ImageData data = new ImageData(stream);
/* 110 */       if (data.transparentPixel > 0) {
/* 111 */         return new Image((Device)display, data, data.getTransparencyMask());
/*     */       }
/* 113 */       return new Image((Device)display, data);
/*     */     } finally {
/* 115 */       stream.close();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Image getImage(String path) {
/* 126 */     Image image = m_imageMap.get(path);
/* 127 */     if (image == null) {
/*     */       try {
/* 129 */         image = getImage(new FileInputStream(path));
/* 130 */         m_imageMap.put(path, image);
/* 131 */       } catch (Exception e) {
/* 132 */         image = getMissingImage();
/* 133 */         m_imageMap.put(path, image);
/*     */       } 
/*     */     }
/* 136 */     return image;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Image getImage(Class<?> clazz, String path) {
/* 148 */     String key = clazz.getName() + '|' + path;
/* 149 */     Image image = m_imageMap.get(key);
/* 150 */     if (image == null) {
/*     */       try {
/* 152 */         image = getImage(clazz.getResourceAsStream(path));
/* 153 */         m_imageMap.put(key, image);
/* 154 */       } catch (Exception e) {
/* 155 */         image = getMissingImage();
/* 156 */         m_imageMap.put(key, image);
/*     */       } 
/*     */     }
/* 159 */     return image;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static Image getMissingImage() {
/* 166 */     Image image = new Image((Device)Display.getCurrent(), 10, 10);
/*     */     
/* 168 */     GC gc = new GC((Drawable)image);
/* 169 */     gc.setBackground(getColor(3));
/* 170 */     gc.fillRectangle(0, 0, 10, 10);
/* 171 */     gc.dispose();
/*     */     
/* 173 */     return image;
/*     */   }
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
/* 199 */   private static Map<Image, Map<Image, Image>>[] m_decoratedImageMap = (Map<Image, Map<Image, Image>>[])new Map[5];
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Image decorateImage(Image baseImage, Image decorator) {
/* 210 */     return decorateImage(baseImage, decorator, 4);
/*     */   }
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
/*     */   public static Image decorateImage(Image baseImage, Image decorator, int corner) {
/* 224 */     if (corner <= 0 || corner >= 5) {
/* 225 */       throw new IllegalArgumentException("Wrong decorate corner");
/*     */     }
/* 227 */     Map<Image, Map<Image, Image>> cornerDecoratedImageMap = m_decoratedImageMap[corner];
/* 228 */     if (cornerDecoratedImageMap == null) {
/* 229 */       cornerDecoratedImageMap = new HashMap<Image, Map<Image, Image>>();
/* 230 */       m_decoratedImageMap[corner] = cornerDecoratedImageMap;
/*     */     } 
/* 232 */     Map<Image, Image> decoratedMap = cornerDecoratedImageMap.get(baseImage);
/* 233 */     if (decoratedMap == null) {
/* 234 */       decoratedMap = new HashMap<Image, Image>();
/* 235 */       cornerDecoratedImageMap.put(baseImage, decoratedMap);
/*     */     } 
/*     */     
/* 238 */     Image result = decoratedMap.get(decorator);
/* 239 */     if (result == null) {
/* 240 */       Rectangle bib = baseImage.getBounds();
/* 241 */       Rectangle dib = decorator.getBounds();
/*     */       
/* 243 */       result = new Image((Device)Display.getCurrent(), bib.width, bib.height);
/*     */       
/* 245 */       GC gc = new GC((Drawable)result);
/* 246 */       gc.drawImage(baseImage, 0, 0);
/* 247 */       if (corner == 1) {
/* 248 */         gc.drawImage(decorator, 0, 0);
/* 249 */       } else if (corner == 2) {
/* 250 */         gc.drawImage(decorator, bib.width - dib.width, 0);
/* 251 */       } else if (corner == 3) {
/* 252 */         gc.drawImage(decorator, 0, bib.height - dib.height);
/* 253 */       } else if (corner == 4) {
/* 254 */         gc.drawImage(decorator, bib.width - dib.width, bib.height - dib.height);
/*     */       } 
/* 256 */       gc.dispose();
/*     */       
/* 258 */       decoratedMap.put(decorator, result);
/*     */     } 
/* 260 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void disposeImages() {
/* 268 */     for (Image image : m_imageMap.values()) {
/* 269 */       image.dispose();
/*     */     }
/* 271 */     m_imageMap.clear();
/*     */ 
/*     */     
/* 274 */     for (int i = 0; i < m_decoratedImageMap.length; i++) {
/* 275 */       Map<Image, Map<Image, Image>> cornerDecoratedImageMap = m_decoratedImageMap[i];
/* 276 */       if (cornerDecoratedImageMap != null) {
/* 277 */         for (Map<Image, Image> decoratedMap : cornerDecoratedImageMap.values()) {
/* 278 */           for (Image image : decoratedMap.values()) {
/* 279 */             image.dispose();
/*     */           }
/* 281 */           decoratedMap.clear();
/*     */         } 
/* 283 */         cornerDecoratedImageMap.clear();
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 295 */   private static Map<String, Font> m_fontMap = new HashMap<String, Font>();
/*     */ 
/*     */ 
/*     */   
/* 299 */   private static Map<Font, Font> m_fontToBoldFontMap = new HashMap<Font, Font>();
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
/*     */   public static Font getFont(String name, int height, int style) {
/* 312 */     return getFont(name, height, style, false, false);
/*     */   }
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
/*     */   public static Font getFont(String name, int size, int style, boolean strikeout, boolean underline) {
/* 331 */     String fontName = name + '|' + size + '|' + style + '|' + strikeout + '|' + underline;
/* 332 */     Font font = m_fontMap.get(fontName);
/* 333 */     if (font == null) {
/* 334 */       FontData fontData = new FontData(name, size, style);
/* 335 */       if (strikeout || underline) {
/*     */         try {
/* 337 */           Class<?> logFontClass = Class.forName("org.eclipse.swt.internal.win32.LOGFONT");
/* 338 */           Object logFont = FontData.class.getField("data").get(fontData);
/* 339 */           if (logFont != null && logFontClass != null) {
/* 340 */             if (strikeout) {
/* 341 */               logFontClass.getField("lfStrikeOut").set(logFont, Byte.valueOf((byte)1));
/*     */             }
/* 343 */             if (underline) {
/* 344 */               logFontClass.getField("lfUnderline").set(logFont, Byte.valueOf((byte)1));
/*     */             }
/*     */           } 
/* 347 */         } catch (Throwable e) {
/* 348 */           System.err.println("Unable to set underline or strikeout (probably on a non-Windows platform). " + e);
/*     */         } 
/*     */       }
/* 351 */       font = new Font((Device)Display.getCurrent(), fontData);
/* 352 */       m_fontMap.put(fontName, font);
/*     */     } 
/* 354 */     return font;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Font getBoldFont(Font baseFont) {
/* 364 */     Font font = m_fontToBoldFontMap.get(baseFont);
/* 365 */     if (font == null) {
/* 366 */       FontData[] fontDatas = baseFont.getFontData();
/* 367 */       FontData data = fontDatas[0];
/* 368 */       font = new Font((Device)Display.getCurrent(), data.getName(), data.getHeight(), 1);
/* 369 */       m_fontToBoldFontMap.put(baseFont, font);
/*     */     } 
/* 371 */     return font;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void disposeFonts() {
/* 378 */     for (Font font : m_fontMap.values()) {
/* 379 */       font.dispose();
/*     */     }
/* 381 */     m_fontMap.clear();
/*     */     
/* 383 */     for (Font font : m_fontToBoldFontMap.values()) {
/* 384 */       font.dispose();
/*     */     }
/* 386 */     m_fontToBoldFontMap.clear();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 396 */   private static Map<Integer, Cursor> m_idToCursorMap = new HashMap<Integer, Cursor>();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Cursor getCursor(int id) {
/* 405 */     Integer key = Integer.valueOf(id);
/* 406 */     Cursor cursor = m_idToCursorMap.get(key);
/* 407 */     if (cursor == null) {
/* 408 */       cursor = new Cursor((Device)Display.getDefault(), id);
/* 409 */       m_idToCursorMap.put(key, cursor);
/*     */     } 
/* 411 */     return cursor;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static void disposeCursors() {
/* 417 */     for (Cursor cursor : m_idToCursorMap.values()) {
/* 418 */       cursor.dispose();
/*     */     }
/* 420 */     m_idToCursorMap.clear();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void dispose() {
/* 432 */     disposeColors();
/* 433 */     disposeImages();
/* 434 */     disposeFonts();
/* 435 */     disposeCursors();
/*     */   }
/*     */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/swtdesigner/SWTResourceManager.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */