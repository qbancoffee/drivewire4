/*     */ package com.groupunix.drivewireui;
/*     */ 
/*     */ import java.io.BufferedReader;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.OutputStreamWriter;
/*     */ import java.io.StringWriter;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.net.MalformedURLException;
/*     */ import java.net.URL;
/*     */ import java.net.URLConnection;
/*     */ import java.net.URLEncoder;
/*     */ import java.net.UnknownHostException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.apache.commons.configuration.ConfigurationException;
/*     */ import org.apache.commons.configuration.XMLConfiguration;
/*     */ import org.eclipse.swt.events.ModifyEvent;
/*     */ import org.eclipse.swt.events.ModifyListener;
/*     */ import org.eclipse.swt.events.PaintEvent;
/*     */ import org.eclipse.swt.events.PaintListener;
/*     */ import org.eclipse.swt.events.SelectionAdapter;
/*     */ import org.eclipse.swt.events.SelectionEvent;
/*     */ import org.eclipse.swt.events.SelectionListener;
/*     */ import org.eclipse.swt.widgets.Button;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Dialog;
/*     */ import org.eclipse.swt.widgets.Display;
/*     */ import org.eclipse.swt.widgets.Label;
/*     */ import org.eclipse.swt.widgets.Link;
/*     */ import org.eclipse.swt.widgets.Shell;
/*     */ import org.eclipse.swt.widgets.Text;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class BugReportWin
/*     */   extends Dialog
/*     */ {
/*     */   protected Object result;
/*     */   protected Shell shlBugReport;
/*     */   private Button btnClose;
/*     */   private String title;
/*     */   private String summary;
/*     */   private String detail;
/*     */   private Text textAdditionalInfo;
/*     */   private Text textEmail;
/*     */   private Button btnErrMsg;
/*     */   private Button btnServerConf;
/*     */   private Button btnUIConf;
/*     */   private Button btnErrDetails;
/*     */   private Button btnJavaInfo;
/*     */   private Button btnUIText;
/*     */   private Button btnServerText;
/*     */   
/*     */   public BugReportWin(Shell parent, int style, String title, String summary, String detail) {
/*  61 */     super(parent, style);
/*  62 */     this.title = title;
/*  63 */     this.summary = summary;
/*  64 */     this.detail = detail;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object open() {
/*  73 */     createContents();
/*     */     
/*  75 */     this.shlBugReport.open();
/*  76 */     this.shlBugReport.layout();
/*  77 */     Display display = getParent().getDisplay();
/*     */     
/*  79 */     int x = (getParent().getBounds()).x + (getParent().getBounds()).width / 2 - (this.shlBugReport.getBounds()).width / 2;
/*  80 */     int y = (getParent().getBounds()).y + (getParent().getBounds()).height / 2 - (this.shlBugReport.getBounds()).height / 2;
/*     */     
/*  82 */     this.shlBugReport.setLocation(x, y);
/*     */     
/*  84 */     this.btnUIText = new Button((Composite)this.shlBugReport, 32);
/*  85 */     this.btnUIText.setBounds(25, 284, 543, 16);
/*  86 */     this.btnUIText.setText("The contents of the 'UI' pane (output from dw commands, etc)");
/*     */     
/*  88 */     this.btnServerText = new Button((Composite)this.shlBugReport, 32);
/*  89 */     this.btnServerText.setBounds(25, 306, 543, 16);
/*  90 */     this.btnServerText.setText("The contents of the 'Server' pane (server log entries)");
/*     */     
/*  92 */     while (!this.shlBugReport.isDisposed()) {
/*  93 */       if (!display.readAndDispatch()) {
/*  94 */         display.sleep();
/*     */       }
/*     */     } 
/*  97 */     return this.result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void createContents() {
/* 107 */     this.shlBugReport = new Shell(getParent(), getStyle());
/* 108 */     this.shlBugReport.addPaintListener(new PaintListener()
/*     */         {
/*     */           public void paintControl(PaintEvent e) {}
/*     */         });
/*     */ 
/*     */ 
/*     */     
/* 115 */     this.shlBugReport.setSize(598, 561);
/* 116 */     this.shlBugReport.setText("Bug Report for '" + this.title + "'");
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 121 */     this.btnClose = new Button((Composite)this.shlBugReport, 0);
/* 122 */     this.btnClose.addSelectionListener((SelectionListener)new SelectionAdapter()
/*     */         {
/*     */           
/*     */           public void widgetSelected(SelectionEvent e)
/*     */           {
/* 127 */             BugReportWin.this.shlBugReport.close();
/*     */           }
/*     */         });
/*     */     
/* 131 */     this.btnClose.setBounds(483, 498, 85, 25);
/* 132 */     this.btnClose.setText("Cancel");
/*     */     
/* 134 */     this.btnErrMsg = new Button((Composite)this.shlBugReport, 32);
/* 135 */     this.btnErrMsg.setSelection(true);
/* 136 */     this.btnErrMsg.setBounds(25, 176, 543, 16);
/* 137 */     this.btnErrMsg.setText("The error message itself, if any");
/*     */     
/* 139 */     this.btnErrDetails = new Button((Composite)this.shlBugReport, 32);
/* 140 */     this.btnErrDetails.setSelection(true);
/* 141 */     this.btnErrDetails.setText("The extended error details, as seen in the lower pane of the error dialog");
/* 142 */     this.btnErrDetails.setBounds(25, 198, 543, 16);
/*     */     
/* 144 */     this.btnUIConf = new Button((Composite)this.shlBugReport, 32);
/* 145 */     this.btnUIConf.setSelection(true);
/* 146 */     this.btnUIConf.setBounds(25, 220, 543, 16);
/* 147 */     this.btnUIConf.setText("The DriveWire UI configuration (the contents of drivewireUI.xml on the client)");
/*     */     
/* 149 */     this.btnServerConf = new Button((Composite)this.shlBugReport, 32);
/* 150 */     this.btnServerConf.setSelection(true);
/* 151 */     this.btnServerConf.setText("The DriveWire server configuration (the contents of config.xml on the server)");
/* 152 */     this.btnServerConf.setBounds(25, 241, 543, 16);
/*     */     
/* 154 */     this.textAdditionalInfo = new Text((Composite)this.shlBugReport, 2626);
/* 155 */     this.textAdditionalInfo.addModifyListener(new ModifyListener()
/*     */         {
/*     */           public void modifyText(ModifyEvent e) {}
/*     */         });
/*     */     
/* 160 */     this.textAdditionalInfo.setBounds(25, 359, 543, 60);
/*     */     
/* 162 */     Button btnNewButton = new Button((Composite)this.shlBugReport, 0);
/* 163 */     btnNewButton.addSelectionListener((SelectionListener)new SelectionAdapter()
/*     */         {
/*     */           public void widgetSelected(SelectionEvent e) {
/* 166 */             if (BugReportWin.this.doSubmit())
/*     */             {
/* 168 */               BugReportWin.this.shlBugReport.close();
/*     */             }
/*     */           }
/*     */         });
/*     */     
/* 173 */     btnNewButton.setBounds(203, 498, 183, 25);
/* 174 */     btnNewButton.setText("Submit Bug Report");
/*     */     
/* 176 */     this.textEmail = new Text((Composite)this.shlBugReport, 2048);
/* 177 */     this.textEmail.setBounds(25, 451, 288, 21);
/*     */     
/* 179 */     Link link = new Link((Composite)this.shlBugReport, 0);
/* 180 */     link.addSelectionListener((SelectionListener)new SelectionAdapter()
/*     */         {
/*     */           public void widgetSelected(SelectionEvent e)
/*     */           {
/* 184 */             MainWin.doDisplayAsync(new Runnable()
/*     */                 {
/*     */                   
/*     */                   public void run()
/*     */                   {
/* 189 */                     JavaInfoWin jiwin = new JavaInfoWin(BugReportWin.this.shlBugReport, 2144);
/* 190 */                     jiwin.open();
/*     */                   }
/*     */                 });
/*     */           }
/*     */         });
/*     */ 
/*     */     
/* 197 */     link.setBounds(43, 263, 525, 15);
/* 198 */     link.setText("Information about your Java environment  (<a>click here to see what is included</a>)");
/*     */     
/* 200 */     this.btnJavaInfo = new Button((Composite)this.shlBugReport, 32);
/* 201 */     this.btnJavaInfo.setSelection(true);
/* 202 */     this.btnJavaInfo.setBounds(25, 263, 20, 16);
/*     */     
/* 204 */     Label lblIfYouBelieve = new Label((Composite)this.shlBugReport, 64);
/* 205 */     lblIfYouBelieve.setBounds(25, 10, 543, 129);
/* 206 */     lblIfYouBelieve.setText("Please submit as much information as you can about this problem.  Internet access is required to submit a bug report.\r\n\r\nFor those concerned with privacy, you should know that all information in this bug report is sent in plain text over the internet.  While it will never intentionally be made public, the author offers absolutely no promise of confidentiality.   On the other hand, it's just some DriveWire configuration data and will normally not contain anything sensitive.\r\n\r\n");
/*     */     
/* 208 */     Label lblEmailAddressoptional = new Label((Composite)this.shlBugReport, 0);
/* 209 */     lblEmailAddressoptional.setBounds(25, 430, 288, 15);
/* 210 */     lblEmailAddressoptional.setText("Email address (optional):");
/*     */     
/* 212 */     Label lblAdditionalInformationbe = new Label((Composite)this.shlBugReport, 0);
/* 213 */     lblAdditionalInformationbe.setBounds(25, 338, 393, 15);
/* 214 */     lblAdditionalInformationbe.setText("Additional information (be verbose!):");
/*     */     
/* 216 */     Label lblDataToInclude = new Label((Composite)this.shlBugReport, 0);
/* 217 */     lblDataToInclude.setBounds(25, 145, 543, 25);
/* 218 */     lblDataToInclude.setText("What data would you like to include in this bug report?");
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
/*     */   protected boolean doSubmit() {
/* 255 */     boolean res = false;
/*     */     
/* 257 */     String err = new String();
/*     */ 
/*     */     
/* 260 */     String surl = new String();
/*     */     
/* 262 */     surl = "br=0";
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     try {
/* 268 */       if (getBtnErrMsg().getSelection())
/*     */       {
/* 270 */         surl = surl + "&" + encv("bugsum", this.summary);
/*     */       }
/*     */       
/* 273 */       if (getBtnErrDetails().getSelection())
/*     */       {
/* 275 */         surl = surl + "&" + encv("bugdet", this.detail);
/*     */       }
/*     */ 
/*     */ 
/*     */       
/* 280 */       if (getBtnUIConf().getSelection()) {
/*     */ 
/*     */         
/* 283 */         surl = surl + "&" + encv("uiv", "4.0.7a");
/*     */ 
/*     */         
/* 286 */         StringWriter sw = new StringWriter();
/*     */         
/*     */         try {
/* 289 */           MainWin.config.save(sw);
/* 290 */           surl = surl + "&" + encv("uiconf", sw.getBuffer().toString());
/*     */         }
/* 292 */         catch (ConfigurationException e) {
/*     */           
/* 294 */           surl = surl + "&" + encv("uiconf", e.getMessage());
/*     */         } 
/*     */       } 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 301 */       if (getBtnServerConf().getSelection()) {
/*     */ 
/*     */         
/* 304 */         Connection conn = new Connection(MainWin.getHost(), MainWin.getPort(), MainWin.getInstance());
/*     */         
/* 306 */         List<String> vres = new ArrayList<String>();
/*     */ 
/*     */         
/*     */         try {
/* 310 */           conn.Connect();
/* 311 */           vres = conn.loadList(-1, "ui server show version");
/* 312 */           conn.close();
/*     */           
/* 314 */           surl = surl + "&" + encv("dwv", vres.get(0));
/*     */         
/*     */         }
/* 317 */         catch (UnknownHostException e) {
/*     */           
/* 319 */           surl = surl + "&" + encv("dwv", e.getMessage());
/*     */         }
/* 321 */         catch (IOException e) {
/*     */           
/* 323 */           surl = surl + "&" + encv("dwv", e.getMessage());
/*     */         }
/* 325 */         catch (DWUIOperationFailedException e) {
/*     */           
/* 327 */           surl = surl + "&" + encv("dwv", e.getMessage());
/*     */         } 
/*     */ 
/*     */         
/* 331 */         XMLConfiguration tmpc = new XMLConfiguration(MainWin.dwconfig);
/* 332 */         StringWriter sw = new StringWriter();
/*     */         
/*     */         try {
/* 335 */           tmpc.save(sw);
/* 336 */           surl = surl + "&" + encv("dwconf", sw.getBuffer().toString());
/*     */         }
/* 338 */         catch (ConfigurationException e) {
/*     */           
/* 340 */           surl = surl + "&" + encv("dwconf", e.getMessage());
/*     */         } 
/*     */       } 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 347 */       if (this.btnJavaInfo.getSelection()) {
/*     */         
/* 349 */         String jitmp = new String();
/* 350 */         for (Map.Entry<Object, Object> e : System.getProperties().entrySet()) {
/* 351 */           jitmp = jitmp + e + "\n";
/*     */         }
/* 353 */         surl = surl + "&" + encv("java", jitmp);
/*     */       } 
/*     */ 
/*     */       
/* 357 */       if (getBtnUIText().getSelection())
/*     */       {
/* 359 */         surl = surl + "&" + encv("uitxt", MainWin.getUIText());
/*     */       }
/*     */       
/* 362 */       if (getBtnServerText().getSelection())
/*     */       {
/* 364 */         surl = surl + "&" + encv("srvtxt", MainWin.getServerText());
/*     */       }
/*     */ 
/*     */ 
/*     */       
/* 369 */       if (!getTextAdditionalInfo().getText().equals(""))
/*     */       {
/* 371 */         surl = surl + "&" + encv("usrinf", getTextAdditionalInfo().getText());
/*     */       }
/* 373 */       if (!getTextEmail().getText().equals(""))
/*     */       {
/* 375 */         surl = surl + "&" + encv("usraddr", getTextEmail().getText());
/*     */       
/*     */       }
/*     */     }
/* 379 */     catch (UnsupportedEncodingException e) {
/*     */       
/* 381 */       err = err + " " + e.getMessage();
/*     */     } 
/*     */ 
/*     */     
/* 385 */     if (!surl.equals("br=0")) {
/*     */ 
/*     */ 
/*     */       
/* 389 */       int tid = MainWin.taskman.addTask("Submit bug report");
/* 390 */       MainWin.taskman.updateTask(tid, 0, "Submitting bug report...");
/*     */ 
/*     */ 
/*     */       
/*     */       try {
/* 395 */         URL url = new URL("http://aaronwolfe.com:80/dw4/br.pl");
/* 396 */         URLConnection conn = url.openConnection();
/* 397 */         conn.setDoOutput(true);
/* 398 */         OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
/* 399 */         wr.write(surl);
/* 400 */         wr.flush();
/*     */         
/* 402 */         BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
/*     */         
/* 404 */         String txt = ""; String line;
/* 405 */         while ((line = rd.readLine()) != null)
/*     */         {
/* 407 */           txt = txt + line;
/*     */         }
/* 409 */         wr.close();
/* 410 */         rd.close();
/*     */         
/* 412 */         MainWin.taskman.updateTask(tid, 1, "Bug report accepted, thank you.");
/*     */         
/* 414 */         res = true;
/*     */       }
/* 416 */       catch (MalformedURLException e) {
/*     */         
/* 418 */         err = err + " " + e.getMessage();
/*     */       }
/* 420 */       catch (IOException e) {
/*     */         
/* 422 */         err = err + " " + e.getMessage();
/*     */       }
/*     */     
/*     */     }
/*     */     else {
/*     */       
/* 428 */       err = err + " No data to send!";
/*     */     } 
/*     */     
/* 431 */     if (!res)
/*     */     {
/* 433 */       MainWin.showError("This just isn't your day...", "We had an error sending the bug report.  Seek alternate routes, consult an exorcist, abort, retry, fail, etc...", "Possible clues: " + err);
/*     */     }
/*     */ 
/*     */     
/* 437 */     return res;
/*     */   }
/*     */ 
/*     */   
/*     */   private String encv(String key, String val) throws UnsupportedEncodingException {
/* 442 */     String res = new String();
/*     */     
/* 444 */     res = URLEncoder.encode(key, "UTF-8") + "=" + URLEncoder.encode(val, "UTF-8");
/*     */     
/* 446 */     return res;
/*     */   }
/*     */   protected Button getBtnErrMsg() {
/* 449 */     return this.btnErrMsg;
/*     */   }
/*     */   protected Text getTextAdditionalInfo() {
/* 452 */     return this.textAdditionalInfo;
/*     */   }
/*     */   protected Text getTextEmail() {
/* 455 */     return this.textEmail;
/*     */   }
/*     */   protected Button getBtnServerConf() {
/* 458 */     return this.btnServerConf;
/*     */   }
/*     */   protected Button getBtnUIConf() {
/* 461 */     return this.btnUIConf;
/*     */   }
/*     */   protected Button getBtnErrDetails() {
/* 464 */     return this.btnErrDetails;
/*     */   }
/*     */   protected Button getBtnJavaInfo() {
/* 467 */     return this.btnJavaInfo;
/*     */   }
/*     */   protected Button getBtnUIText() {
/* 470 */     return this.btnUIText;
/*     */   }
/*     */   protected Button getBtnServerText() {
/* 473 */     return this.btnServerText;
/*     */   }
/*     */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireui/BugReportWin.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */