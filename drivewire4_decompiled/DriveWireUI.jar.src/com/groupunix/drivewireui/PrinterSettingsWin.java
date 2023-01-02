/*     */ package com.groupunix.drivewireui;
/*     */ 
/*     */ import org.eclipse.swt.events.SelectionAdapter;
/*     */ import org.eclipse.swt.events.SelectionEvent;
/*     */ import org.eclipse.swt.events.SelectionListener;
/*     */ import org.eclipse.swt.widgets.Button;
/*     */ import org.eclipse.swt.widgets.Combo;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Dialog;
/*     */ import org.eclipse.swt.widgets.Display;
/*     */ import org.eclipse.swt.widgets.Label;
/*     */ import org.eclipse.swt.widgets.Shell;
/*     */ import org.eclipse.swt.widgets.Spinner;
/*     */ import org.eclipse.swt.widgets.Text;
/*     */ import com.swtdesigner.SWTResourceManager;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class PrinterSettingsWin
/*     */   extends Dialog
/*     */ {
/*     */   protected Object result;
/*     */   protected Shell shell;
/*     */   private Text text;
/*     */   private Text text_1;
/*     */   private Text text_2;
/*     */   private Text text_3;
/*     */   
/*     */   public PrinterSettingsWin(Shell parent, int style) {
/*  32 */     super(parent, style);
/*  33 */     setText("Printer Settings");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object open() {
/*  41 */     createContents();
/*  42 */     this.shell.open();
/*  43 */     this.shell.layout();
/*  44 */     Display display = getParent().getDisplay();
/*  45 */     while (!this.shell.isDisposed()) {
/*  46 */       if (!display.readAndDispatch()) {
/*  47 */         display.sleep();
/*     */       }
/*     */     } 
/*  50 */     return this.result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void createContents() {
/*  57 */     this.shell = new Shell(getParent(), getStyle());
/*  58 */     this.shell.setSize(450, 322);
/*  59 */     this.shell.setText(getText());
/*     */     
/*  61 */     this.text = new Text((Composite)this.shell, 2048);
/*  62 */     this.text.setBounds(86, 21, 112, 21);
/*     */     
/*  64 */     Combo combo = new Combo((Composite)this.shell, 0);
/*  65 */     combo.setItems(new String[] { "FX80", "TEXT" });
/*  66 */     combo.setBounds(86, 48, 112, 23);
/*     */     
/*  68 */     Label lblName = new Label((Composite)this.shell, 0);
/*  69 */     lblName.setAlignment(131072);
/*  70 */     lblName.setBounds(10, 24, 70, 15);
/*  71 */     lblName.setText("Name:");
/*     */     
/*  73 */     Label lblDriver = new Label((Composite)this.shell, 0);
/*  74 */     lblDriver.setAlignment(131072);
/*  75 */     lblDriver.setBounds(10, 51, 70, 15);
/*  76 */     lblDriver.setText("Driver:");
/*     */     
/*  78 */     Composite compositeFX80 = new Composite((Composite)this.shell, 0);
/*  79 */     compositeFX80.setBounds(10, 136, 424, 108);
/*     */     
/*  81 */     this.text_3 = new Text(compositeFX80, 2048);
/*  82 */     this.text_3.setBounds(110, 10, 277, 21);
/*     */     
/*  84 */     Button button_1 = new Button(compositeFX80, 0);
/*  85 */     button_1.setBounds(393, 8, 30, 25);
/*  86 */     button_1.setText("...");
/*     */     
/*  88 */     Label lblCharacterDefs = new Label(compositeFX80, 0);
/*  89 */     lblCharacterDefs.setAlignment(131072);
/*  90 */     lblCharacterDefs.setBounds(0, 13, 104, 15);
/*  91 */     lblCharacterDefs.setText("Character defs:");
/*     */     
/*  93 */     Spinner spinner = new Spinner(compositeFX80, 2048);
/*  94 */     spinner.setMaximum(240);
/*  95 */     spinner.setBounds(110, 44, 63, 22);
/*     */     
/*  97 */     Label lblColumns = new Label(compositeFX80, 0);
/*  98 */     lblColumns.setAlignment(131072);
/*  99 */     lblColumns.setBounds(29, 47, 75, 15);
/* 100 */     lblColumns.setText("Columns:");
/*     */     
/* 102 */     Spinner spinner_1 = new Spinner(compositeFX80, 2048);
/* 103 */     spinner_1.setMaximum(240);
/* 104 */     spinner_1.setBounds(286, 44, 63, 22);
/*     */     
/* 106 */     Label lblLinesPerPage = new Label(compositeFX80, 0);
/* 107 */     lblLinesPerPage.setAlignment(131072);
/* 108 */     lblLinesPerPage.setBounds(169, 47, 111, 15);
/* 109 */     lblLinesPerPage.setText("Lines per page:");
/*     */     
/* 111 */     Spinner spinner_2 = new Spinner(compositeFX80, 2048);
/* 112 */     spinner_2.setIncrement(25);
/* 113 */     spinner_2.setPageIncrement(100);
/* 114 */     spinner_2.setMaximum(2400);
/* 115 */     spinner_2.setBounds(110, 72, 63, 22);
/*     */     
/* 117 */     Label lblDpi = new Label(compositeFX80, 0);
/* 118 */     lblDpi.setAlignment(131072);
/* 119 */     lblDpi.setBounds(49, 75, 55, 15);
/* 120 */     lblDpi.setText("DPI:");
/*     */     
/* 122 */     Combo combo_1 = new Combo(compositeFX80, 0);
/* 123 */     combo_1.setItems(new String[] { "BMP", "GIF", "JPG", "PNG", "WBMP" });
/* 124 */     combo_1.setBounds(286, 72, 63, 23);
/*     */     
/* 126 */     Label lblImageFormat = new Label(compositeFX80, 0);
/* 127 */     lblImageFormat.setAlignment(131072);
/* 128 */     lblImageFormat.setBounds(179, 75, 101, 15);
/* 129 */     lblImageFormat.setText("Image format:");
/*     */     
/* 131 */     Button btnOk = new Button((Composite)this.shell, 0);
/* 132 */     btnOk.addSelectionListener((SelectionListener)new SelectionAdapter()
/*     */         {
/*     */           public void widgetSelected(SelectionEvent e) {}
/*     */         });
/*     */     
/* 137 */     btnOk.setBounds(278, 260, 75, 25);
/* 138 */     btnOk.setText("Ok");
/*     */     
/* 140 */     Button btnCancel = new Button((Composite)this.shell, 0);
/* 141 */     btnCancel.setBounds(359, 260, 75, 25);
/* 142 */     btnCancel.setText("Cancel");
/*     */     
/* 144 */     Label lblNewLabel = new Label((Composite)this.shell, 0);
/* 145 */     lblNewLabel.setAlignment(131072);
/* 146 */     lblNewLabel.setBounds(10, 83, 69, 15);
/* 147 */     lblNewLabel.setText("Output to:");
/*     */     
/* 149 */     this.text_1 = new Text((Composite)this.shell, 2048);
/* 150 */     this.text_1.setBounds(86, 80, 312, 21);
/*     */     
/* 152 */     Button button = new Button((Composite)this.shell, 0);
/* 153 */     button.setBounds(404, 78, 30, 25);
/* 154 */     button.setText("...");
/*     */     
/* 156 */     this.text_2 = new Text((Composite)this.shell, 2048);
/* 157 */     this.text_2.setBounds(121, 109, 277, 21);
/*     */     
/* 159 */     Label lblFlushCommand = new Label((Composite)this.shell, 0);
/* 160 */     lblFlushCommand.setAlignment(131072);
/* 161 */     lblFlushCommand.setBounds(10, 112, 105, 15);
/* 162 */     lblFlushCommand.setText("Flush command:");
/*     */     
/* 164 */     Label label = new Label((Composite)this.shell, 0);
/* 165 */     label.setAlignment(131072);
/* 166 */     label.setImage(SWTResourceManager.getImage(PrinterSettingsWin.class, "/printers/printer-4.png"));
/* 167 */     label.setBounds(343, 10, 91, 56);
/*     */   }
/*     */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireui/PrinterSettingsWin.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */