/*     */ package com.groupunix.drivewireui;
/*     */ 
/*     */ import java.text.Collator;
/*     */ import java.util.Locale;
/*     */ import java.util.regex.Pattern;
/*     */ import org.eclipse.swt.events.SelectionEvent;
/*     */ import org.eclipse.swt.events.SelectionListener;
/*     */ import org.eclipse.swt.widgets.Tree;
/*     */ import org.eclipse.swt.widgets.TreeColumn;
/*     */ import org.eclipse.swt.widgets.TreeItem;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SortTreeListener
/*     */   implements SelectionListener
/*     */ {
/*     */   public void widgetDefaultSelected(SelectionEvent e) {}
/*     */   
/*     */   public void widgetSelected(SelectionEvent e) {
/*  22 */     sortTree(e);
/*     */   }
/*     */   
/*     */   private void sortTree(SelectionEvent e) {
/*  26 */     TreeColumn column = (TreeColumn)e.widget;
/*  27 */     Tree tree = column.getParent();
/*  28 */     TreeItem[] treeItems = tree.getItems();
/*  29 */     TreeColumn sortColumn = tree.getSortColumn();
/*  30 */     TreeColumn[] columns = tree.getColumns();
/*  31 */     tree.setSortColumn(column);
/*  32 */     int numOfColumns = columns.length;
/*  33 */     int columnIndex = findColumnIndex(columns, column, numOfColumns);
/*  34 */     Collator collator = Collator.getInstance(Locale.getDefault());
/*  35 */     Boolean sort = Boolean.valueOf(false);
/*  36 */     Pattern pattern = Pattern.compile("([\\+]*|[\\-]*)\\d+");
/*  37 */     if (column.equals(sortColumn) && tree.getSortDirection() == 128) {
/*     */       
/*  39 */       tree.setSortDirection(1024);
/*  40 */       for (int i = 1; i < treeItems.length; i++) {
/*  41 */         String value1 = treeItems[i].getText(columnIndex).trim();
/*  42 */         for (int j = 0; j < i; j++) {
/*  43 */           String value2 = treeItems[j].getText(columnIndex).trim();
/*  44 */           if (pattern.matcher(value1).matches() && pattern.matcher(value2).matches()) {
/*     */             
/*  46 */             double d1 = getDouble(value1);
/*  47 */             double d2 = getDouble(value2);
/*  48 */             if (d1 > d2) {
/*  49 */               sort = Boolean.valueOf(true);
/*     */             }
/*  51 */           } else if (collator.compare(value1, value2) > 0) {
/*  52 */             sort = Boolean.valueOf(true);
/*     */           } 
/*  54 */           if (sort.booleanValue()) {
/*  55 */             String[] values = getColumnValues(treeItems[i], numOfColumns);
/*     */             
/*  57 */             TreeItem[] subItems = treeItems[i].getItems();
/*  58 */             TreeItem item = new TreeItem(tree, 0, j);
/*  59 */             item.setText(values);
/*  60 */             for (TreeItem si : subItems) {
/*  61 */               TreeItem[] subSubItems = si.getItems();
/*  62 */               TreeItem subItem = new TreeItem(item, 0);
/*  63 */               subItem.setText(getColumnValues(si, numOfColumns));
/*  64 */               for (TreeItem ssi : subSubItems) {
/*  65 */                 TreeItem subSubItem = new TreeItem(subItem, 0);
/*     */                 
/*  67 */                 subSubItem.setText(getColumnValues(ssi, numOfColumns));
/*     */               } 
/*     */             } 
/*     */             
/*  71 */             treeItems[i].dispose();
/*  72 */             treeItems = tree.getItems();
/*  73 */             sort = Boolean.valueOf(false);
/*     */             break;
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     } else {
/*  79 */       tree.setSortDirection(128);
/*  80 */       for (int i = 1; i < treeItems.length; i++) {
/*  81 */         String value1 = treeItems[i].getText(columnIndex).trim();
/*  82 */         for (int j = 0; j < i; j++) {
/*  83 */           String value2 = treeItems[j].getText(columnIndex).trim();
/*  84 */           if (pattern.matcher(value1).matches() && pattern.matcher(value2).matches()) {
/*     */             
/*  86 */             double d1 = getDouble(value1);
/*  87 */             double d2 = getDouble(value2);
/*  88 */             if (d1 < d2) {
/*  89 */               sort = Boolean.valueOf(true);
/*     */             }
/*  91 */           } else if (collator.compare(value1, value2) < 0) {
/*  92 */             sort = Boolean.valueOf(true);
/*     */           } 
/*  94 */           if (sort.booleanValue()) {
/*  95 */             String[] values = getColumnValues(treeItems[i], numOfColumns);
/*     */             
/*  97 */             TreeItem[] subItems = treeItems[i].getItems();
/*  98 */             TreeItem item = new TreeItem(tree, 0, j);
/*  99 */             item.setText(values);
/* 100 */             for (TreeItem si : subItems) {
/* 101 */               TreeItem[] subSubItems = si.getItems();
/* 102 */               TreeItem subItem = new TreeItem(item, 0);
/* 103 */               subItem.setText(getColumnValues(si, numOfColumns));
/* 104 */               for (TreeItem ssi : subSubItems) {
/* 105 */                 TreeItem subSubItem = new TreeItem(subItem, 0);
/*     */                 
/* 107 */                 subSubItem.setText(getColumnValues(ssi, numOfColumns));
/*     */               } 
/*     */             } 
/*     */             
/* 111 */             treeItems[i].dispose();
/* 112 */             treeItems = tree.getItems();
/* 113 */             sort = Boolean.valueOf(false);
/*     */             break;
/*     */           } 
/*     */         } 
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
/*     */   
/*     */   private int findColumnIndex(TreeColumn[] columns, TreeColumn column, int numOfColumns) {
/* 130 */     int index = 0;
/* 131 */     for (int i = 0; i < numOfColumns; i++) {
/* 132 */       if (column.equals(columns[i])) {
/* 133 */         index = i;
/*     */         break;
/*     */       } 
/*     */     } 
/* 137 */     return index;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private double getDouble(String str) {
/*     */     double d;
/* 148 */     if (str.startsWith("+")) {
/* 149 */       d = Double.parseDouble(str.split("\\+")[1]);
/*     */     } else {
/* 151 */       d = Double.parseDouble(str);
/*     */     } 
/* 153 */     return d;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private String[] getColumnValues(TreeItem treeItem, int numOfColumns) {
/* 164 */     String[] values = new String[numOfColumns];
/* 165 */     for (int i = 0; i < numOfColumns; i++) {
/* 166 */       values[i] = treeItem.getText(i);
/*     */     }
/* 168 */     return values;
/*     */   }
/*     */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireui/SortTreeListener.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */