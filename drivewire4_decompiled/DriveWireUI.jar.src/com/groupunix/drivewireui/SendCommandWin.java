/*     */ package com.groupunix.drivewireui;
/*     */ 
/*     */ import java.util.List;
/*     */ import org.eclipse.swt.events.SelectionAdapter;
/*     */ import org.eclipse.swt.events.SelectionEvent;
/*     */ import org.eclipse.swt.events.SelectionListener;
/*     */ import org.eclipse.swt.graphics.Rectangle;
/*     */ import org.eclipse.swt.widgets.Button;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Dialog;
/*     */ import org.eclipse.swt.widgets.Display;
/*     */ import org.eclipse.swt.widgets.Label;
/*     */ import org.eclipse.swt.widgets.ProgressBar;
/*     */ import org.eclipse.swt.widgets.Shell;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SendCommandWin
/*     */   extends Dialog
/*     */ {
/*     */   protected Object result;
/*     */   protected Shell shlSendingCommandTo;
/*     */   private Label lblMessage;
/*     */   private ProgressBar progressBar;
/*     */   private Label labelStatus;
/*     */   private List<String> commands;
/*     */   private String title;
/*     */   private String message;
/*     */   private Button btnOk;
/*     */   private Shell parshell;
/*     */   
/*     */   public SendCommandWin(Shell parent, int style, List<String> commands, String title, String message) {
/*  38 */     super(parent, style);
/*  39 */     this.parshell = parent;
/*  40 */     this.commands = commands;
/*  41 */     this.title = title;
/*  42 */     this.message = message;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object open() {
/*  52 */     createContents();
/*     */     
/*  54 */     this.btnOk.setVisible(false);
/*     */     
/*  56 */     Rectangle pos = this.parshell.getBounds();
/*     */     
/*  58 */     this.shlSendingCommandTo.setBounds(pos.x + pos.width / 2 - 204, pos.y + pos.height / 2 - 76, 408, 152);
/*     */     
/*  60 */     this.shlSendingCommandTo.open();
/*  61 */     this.shlSendingCommandTo.layout();
/*  62 */     Display display = getParent().getDisplay();
/*     */     
/*  64 */     Thread cmdT = new Thread(new cmdThread(this.commands));
/*  65 */     cmdT.start();
/*     */     
/*  67 */     while (!this.shlSendingCommandTo.isDisposed()) {
/*     */       
/*  69 */       if (!display.readAndDispatch())
/*     */       {
/*  71 */         display.sleep();
/*     */       }
/*     */     } 
/*     */     
/*  75 */     return this.result;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void setErrorStatus(final String err) {
/*  81 */     this.shlSendingCommandTo.getDisplay().asyncExec(new Runnable()
/*     */         {
/*     */           
/*     */           public void run()
/*     */           {
/*  86 */             SendCommandWin.this.labelStatus.setVisible(false);
/*  87 */             SendCommandWin.this.progressBar.setVisible(false);
/*  88 */             SendCommandWin.this.lblMessage.setText(err);
/*  89 */             SendCommandWin.this.shlSendingCommandTo.setText("Error while sending commands");
/*  90 */             SendCommandWin.this.btnOk.setVisible(true);
/*     */           }
/*     */         });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void setStatus(final String msg, final int progress) {
/*  99 */     this.shlSendingCommandTo.getDisplay().asyncExec(new Runnable()
/*     */         {
/*     */           
/*     */           public void run()
/*     */           {
/* 104 */             SendCommandWin.this.progressBar.setSelection(SendCommandWin.this.progressBar.getSelection() + progress);
/* 105 */             SendCommandWin.this.labelStatus.setText(msg);
/*     */             
/* 107 */             SendCommandWin.this.shlSendingCommandTo.redraw();
/*     */           }
/*     */         });
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
/*     */   private void createContents() {
/* 121 */     this.shlSendingCommandTo = new Shell(getParent(), getStyle());
/* 122 */     this.shlSendingCommandTo.setSize(408, 165);
/* 123 */     this.shlSendingCommandTo.setText(this.title);
/*     */     
/* 125 */     this.btnOk = new Button((Composite)this.shlSendingCommandTo, 0);
/* 126 */     this.btnOk.addSelectionListener((SelectionListener)new SelectionAdapter()
/*     */         {
/*     */           public void widgetSelected(SelectionEvent e) {
/* 129 */             SendCommandWin.this.shlSendingCommandTo.close();
/*     */           }
/*     */         });
/* 132 */     this.btnOk.setBounds(298, 89, 75, 25);
/* 133 */     this.btnOk.setText("Ok");
/*     */     
/* 135 */     this.progressBar = new ProgressBar((Composite)this.shlSendingCommandTo, 0);
/* 136 */     this.progressBar.setBounds(23, 66, 350, 17);
/*     */     
/* 138 */     this.lblMessage = new Label((Composite)this.shlSendingCommandTo, 64);
/* 139 */     this.lblMessage.setBounds(23, 23, 343, 40);
/* 140 */     this.lblMessage.setText(this.message);
/*     */     
/* 142 */     this.labelStatus = new Label((Composite)this.shlSendingCommandTo, 0);
/* 143 */     this.labelStatus.setBounds(23, 92, 350, 22);
/*     */   }
/*     */ 
/*     */   
/*     */   protected Label getLblMessage() {
/* 148 */     return this.lblMessage;
/*     */   }
/*     */   protected ProgressBar getProgressBar() {
/* 151 */     return this.progressBar;
/*     */   }
/*     */   protected Label getLabelStatus() {
/* 154 */     return this.labelStatus;
/*     */   }
/*     */   protected Button getBtnOk() {
/* 157 */     return this.btnOk;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   class cmdThread
/*     */     implements Runnable
/*     */   {
/*     */     private List<String> cmds;
/*     */ 
/*     */     
/*     */     public cmdThread(List<String> cmds) {
/* 169 */       this.cmds = cmds;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public void run() {
/* 175 */       Connection connection = new Connection(MainWin.getHost(), MainWin.getPort(), MainWin.getInstance());
/*     */       
/* 177 */       int tid = -1;
/*     */ 
/*     */ 
/*     */       
/*     */       try {
/* 182 */         for (String command : this.cmds) {
/*     */           
/* 184 */           SendCommandWin.this.setStatus("Connecting to server..", 10 / this.cmds.size());
/* 185 */           connection.Connect();
/*     */           
/* 187 */           SendCommandWin.this.setStatus("Send: " + command, 50 / this.cmds.size());
/*     */ 
/*     */           
/* 190 */           tid = MainWin.taskman.addTask(command);
/*     */           
/* 192 */           MainWin.taskman.updateTask(tid, 0, "Connecting to server...");
/*     */           
/* 194 */           connection.sendCommand(tid, command, MainWin.getInstance(), true);
/*     */           
/* 196 */           SendCommandWin.this.setStatus("Closing..", 40 / this.cmds.size());
/*     */ 
/*     */           
/* 199 */           connection.close();
/*     */         } 
/*     */ 
/*     */         
/* 203 */         SendCommandWin.this.setStatus("Finished.", 100);
/*     */         
/* 205 */         SendCommandWin.this.closeWin();
/*     */       }
/* 207 */       catch (Exception e) {
/*     */ 
/*     */         
/* 210 */         SendCommandWin.this.setErrorStatus(e.getMessage());
/* 211 */         MainWin.taskman.updateTask(tid, 2, e.getMessage());
/*     */       } 
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void closeWin() {
/* 220 */     this.shlSendingCommandTo.getDisplay().asyncExec(new Runnable()
/*     */         {
/*     */           
/*     */           public void run()
/*     */           {
/* 225 */             SendCommandWin.this.shlSendingCommandTo.dispose();
/*     */           }
/*     */         });
/*     */   }
/*     */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireui/SendCommandWin.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */