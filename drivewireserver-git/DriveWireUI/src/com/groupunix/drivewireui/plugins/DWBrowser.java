package com.groupunix.drivewireui.plugins;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.VFS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationAdapter;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.browser.TitleEvent;
import org.eclipse.swt.browser.TitleListener;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.wb.swt.SWTResourceManager;

import swing2swt.layout.BorderLayout;

import com.groupunix.drivewireui.GradientHelper;
import com.groupunix.drivewireui.MainWin;
import com.groupunix.drivewireui.SendCommandWin;

public class DWBrowser extends Composite
{
	public final static int LTYPE_LOCAL_ROOT = 0;
	public final static int LTYPE_LOCAL_FOLDER = 1;
	public final static int LTYPE_LOCAL_ENTRY = 2;
	public final static int LTYPE_NET_ROOT = 10;
	public final static int LTYPE_NET_FOLDER = 11;
	public final static int LTYPE_NET_ENTRY = 12;
	public final static int LTYPE_CLOUD_ROOT = 20;
	public final static int LTYPE_CLOUD_FOLDER = 21;
	public final static int LTYPE_CLOUD_ENTRY = 22;
	
	private Browser browser;
	private Composite header;


	private ToolItem tltmBack;
	private ToolItem tltmForward;
	private ToolItem tltmReload;
	private Combo comboURL;
	private Spinner spinnerDrive;

	private CTabItem ourtab;
	private Cursor handcursor;
	private Cursor normcursor;
	private ToolBar toolBar_1;
	private ToolItem toolBookmarks;
	private ToolBar toolBar_2;
	private ToolItem toolDrive;
	
	private boolean append_mode = false;
	
	
	public DWBrowser(final Composite parent, String url, final CTabItem ourtab)
	{
		super(parent, SWT.BORDER);
		this.setOurtab(ourtab);
		
		handcursor = new Cursor( MainWin.getDisplay(), SWT.CURSOR_HAND);
		setNormcursor(new Cursor( MainWin.getDisplay(), SWT.CURSOR_ARROW));
		
		setLayout(new BorderLayout(0, 0));
		
		//setBounds(comp.getBounds());
		
		header = new Composite(this, SWT.NONE);
		header.setLayoutData(BorderLayout.NORTH);
		header.setLayout(new FormLayout());
		
		GradientHelper.applyVerticalGradientBG(header, MainWin.getDisplay().getSystemColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT),MainWin.getDisplay().getSystemColor(SWT.COLOR_TITLE_BACKGROUND));
		
		header.addListener(SWT.Resize, new Listener() {

			@Override
			public void handleEvent(Event event)
			{
				GradientHelper.applyVerticalGradientBG(header, MainWin.getDisplay().getSystemColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT),MainWin.getDisplay().getSystemColor(SWT.COLOR_TITLE_BACKGROUND));
				
				
			} } );
		
		header.setBackgroundMode(SWT.INHERIT_FORCE);
		
		ToolBar toolBar = new ToolBar(header, SWT.FLAT | SWT.RIGHT);
		
		FormData fd_toolBar = new FormData();
		fd_toolBar.left = new FormAttachment(0, 10);
		fd_toolBar.top = new FormAttachment(0, 8);
		toolBar.setLayoutData(fd_toolBar);
		
		tltmBack = new ToolItem(toolBar, SWT.NONE);
		tltmBack.setToolTipText("Back");
		tltmBack.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				browser.back();
			}
		});
		tltmBack.setWidth(30);
		tltmBack.setImage(SWTResourceManager.getImage(DWBrowser.class, "/toolbar/arrow-left-3.png"));
		
		tltmForward = new ToolItem(toolBar, SWT.NONE);
		tltmForward.setToolTipText("Forward");
		tltmForward.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				browser.forward();
			}
		});
		tltmForward.setWidth(30);
		tltmForward.setImage(SWTResourceManager.getImage(DWBrowser.class, "/toolbar/arrow-right-3.png"));
		
		tltmReload = new ToolItem(toolBar, SWT.NONE);
		tltmReload.setToolTipText("Reload page");
		tltmReload.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				browser.refresh();
			}
		});
		tltmReload.setWidth(30);
		tltmReload.setImage(SWTResourceManager.getImage(DWBrowser.class, "/menu/view-refresh-7.png"));
		
		comboURL = new Combo(header, SWT.NONE);
		comboURL.setToolTipText("");
		fd_toolBar.right = new FormAttachment(comboURL, -6);
		comboURL.setBackground(new Color(MainWin.getDisplay(), 255,255,255));
		comboURL.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				
				
				toggleBookmarkIcon(comboURL.getText());
				
				
				if (e.keyCode == 13)
				{
					browser.setUrl(comboURL.getText());
				}
				else if ((e.keyCode == 16777217) || (e.keyCode == 16777218))
				{
					e.doit = false;
				}
			}
		});
		comboURL.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				browser.setUrl(comboURL.getText());
			}
		});
		
		FormData fd_comboURL = new FormData();
		fd_comboURL.left = new FormAttachment(0, 90);
		fd_comboURL.bottom = new FormAttachment(100, -5);
		fd_comboURL.top = new FormAttachment(2, 8);
		comboURL.setLayoutData(fd_comboURL);
		
		
		
		
		spinnerDrive = new Spinner(header, SWT.BORDER);
		spinnerDrive.setBackground(new Color(MainWin.getDisplay(), 255,255,255));
		spinnerDrive.setToolTipText("Working drive");
		FormData fd_spinnerDrive = new FormData();
	
		fd_spinnerDrive.right = new FormAttachment(100, -45);
		fd_spinnerDrive.left = new FormAttachment(100, -90);
		fd_spinnerDrive.top = new FormAttachment(0, 8);
		spinnerDrive.setLayoutData(fd_spinnerDrive);
		spinnerDrive.setMaximum(255);
		
		final Label lblHelp = new Label(header, SWT.NONE);
		
		lblHelp.setToolTipText("Show browser help");
		lblHelp.setCursor(handcursor);
		lblHelp.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent arg0) {
				
				browser.setUrl(MainWin.config.getString("Browser_helppage","http://cococoding.com/dw4/browserhelp"));
				
			}
		});
		lblHelp.setImage(SWTResourceManager.getImage(DWBrowser.class, "/menu/help-about-3.png"));
		FormData fd_lblHelp = new FormData();
		fd_lblHelp.bottom = new FormAttachment(toolBar, 0, SWT.BOTTOM);
		fd_lblHelp.top = new FormAttachment(toolBar, 0, SWT.TOP);
		fd_lblHelp.right = new FormAttachment(100, -10);
		lblHelp.setLayoutData(fd_lblHelp);
		
		
		toolBar_1 = new ToolBar(header, SWT.FLAT | SWT.RIGHT);
		fd_comboURL.right = new FormAttachment(toolBar_1, -6);
		
		FormData fd_toolBar_1 = new FormData();
		fd_toolBar_1.right = new FormAttachment(spinnerDrive, -32);
		fd_toolBar_1.left = new FormAttachment(spinnerDrive, -100);
		
		fd_toolBar_1.bottom = new FormAttachment(toolBar, 0, SWT.BOTTOM);
		
		toolBar_1.setLayoutData(fd_toolBar_1);
		
		toolBookmarks = new ToolItem(toolBar_1, SWT.NONE);
		toolBookmarks.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				
				if (isBookmark(comboURL.getText()))
				{
					removeBookmark(comboURL.getText());
				}
				else
				{
					addBookmark(comboURL.getText());
				}
				
			}
		});
		toolBookmarks.setToolTipText("Add/Remove bookmark");
		toolBookmarks.setImage(SWTResourceManager.getImage(DWBrowser.class, "/menu/bookmark-new-2.png"));
		
		
		toolBar_2 = new ToolBar(header, SWT.FLAT | SWT.RIGHT);
		fd_comboURL.right = new FormAttachment(toolBar_1, -6);
		
		FormData fd_toolBar_2 = new FormData();
		fd_toolBar_2.right = new FormAttachment(spinnerDrive);
		fd_toolBar_2.left = new FormAttachment(spinnerDrive, -70);
		fd_toolBar_2.bottom = new FormAttachment(toolBar, 0, SWT.BOTTOM);
		
		toolBar_2.setLayoutData(fd_toolBar_2);
		
		toolDrive = new ToolItem(toolBar_2, SWT.NONE);
		toolDrive.setToolTipText("Toggle append mode");
		toolDrive.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				
				if (append_mode)
				{
					toolDrive.setImage(SWTResourceManager.getImage(DWBrowser.class, "/menu/disk-insert.png"));
					append_mode = false;
				}
				else
				{
					toolDrive.setImage(SWTResourceManager.getImage(DWBrowser.class, "/menu/new-disk-16.png"));
					append_mode = true;
				}
				
				
			}
		});
		toolDrive.setImage(SWTResourceManager.getImage(DWBrowser.class, "/menu/disk-insert.png"));
		
		
		
		browser = null;
		
		UncaughtExceptionHandler uncex =  Thread.currentThread().getUncaughtExceptionHandler();
		
		Thread.currentThread().setUncaughtExceptionHandler(new UncaughtExceptionHandler() {

			@Override
			public void uncaughtException(Thread t, final Throwable e)
			{
				
				System.out.println("It seems we cannot open a browser window on this system.");
				System.out.println();
				System.out.println("The error message is: " + e.getMessage());
				System.out.println();
				
				MainWin.config.setProperty("NoBrowsers", true);
				
				System.out.println("I've disabled opening browsers in the configuration.  You will have to restart DriveWire.");
				System.exit(1);
			}
		
			});
		
		
			
		
			browser = new Browser(this, SWT.NONE);
			
			Thread.currentThread().setUncaughtExceptionHandler(uncex);
			
			
			browser.addLocationListener(new LocationAdapter() {
				@Override
				public void changed(LocationEvent event) {
					
					comboURL.setText(event.location);
					
					toggleBookmarkIcon(event.location);
					
					
					if (browser.isBackEnabled())
						tltmBack.setEnabled(true);
					else
						tltmBack.setEnabled(false);
					
					if (browser.isForwardEnabled())
						tltmForward.setEnabled(true);
					else
						tltmForward.setEnabled(false);
					
					tltmReload.setImage(SWTResourceManager.getImage(DWBrowser.class, "/menu/view-refresh-7.png"));
					
				}
				
				@Override
				public void changing(LocationEvent event) 
				{
					if (isCocoLink(event.location))
					{
						event.doit = false;
						
						doCoCoLink(event.location);
					}
				}
				
				
				
			});
			
			browser.addTitleListener( new TitleListener() {
		         public void changed(TitleEvent event) {
		        	 ourtab.setText(event.title);
		        	 ourtab.setImage(SWTResourceManager.getImage(MainWin.class, "/menu/www.png"));
		        	 
		          }
		       });
			
			
			
			browser.addProgressListener(new ProgressListener() 
			{
				 public void changed(ProgressEvent event) 
				 {
					 		
			          if (event.total == 0) return;                            
			
			          tltmReload.setImage(SWTResourceManager.getImage(DWBrowser.class, "/menu/network.png"));
						
			          
			      }
				 
			      public void completed(ProgressEvent event) 
			      {
			      
			        tltmReload.setImage(SWTResourceManager.getImage(DWBrowser.class, "/menu/view-refresh-7.png"));
					
			      }
			      
			});
			
			
			
			if (url != null)
			{
				loadBookmarkList();
				browser.setUrl(url);
			}
			else
			{
				// browser.setUrl(MainWin.config.getString("Browser_homepage", "http://cococoding.com/cloud") );
			}
			
		
		
		
		
	}

	
	protected void toggleBookmarkIcon(String url)
	{
		if (isBookmark(url))
		{
			toolBookmarks.setImage(SWTResourceManager.getImage(DWBrowser.class, "/menu/bookmark-del-2.png"));
		}
		else
		{
			toolBookmarks.setImage(SWTResourceManager.getImage(DWBrowser.class, "/menu/bookmark-new-2.png"));
		}
		
		
	}


	@SuppressWarnings("unchecked")
	protected void addBookmark(String url) 
	{
		
		List<String> bm = MainWin.config.getList("Bookmark");
		
		if (!bm.contains(url))
			MainWin.config.addProperty("Bookmark", url);
	
		
		
		toggleBookmarkIcon(url);
		
		String tmp = comboURL.getText();
		comboURL.removeAll();
		
		bm = MainWin.config.getList("Bookmark");
		
		for (String b : bm)
			comboURL.add(b);
		
		comboURL.setText(tmp);
	}





	protected void removeBookmark(String url) 
	{
		int bms = MainWin.config.getMaxIndex("Bookmark");
		
		for (int x = 0;x<=bms;x++)
		{
			if (MainWin.config.getString("Bookmark(" + x + ")").equalsIgnoreCase(url) )
			{
				MainWin.config.clearProperty("Bookmark(" + x + ")");
		
				break;
			}
		}
		
		
		toggleBookmarkIcon(url);
		
		loadBookmarkList();
	}





	@SuppressWarnings("unchecked")
	private void loadBookmarkList() 
	{
		String tmp = comboURL.getText();
		comboURL.removeAll();
		
		List<String> bm = MainWin.config.getList("Bookmark");
		
		for (String b : bm)
			comboURL.add(b);
	
		comboURL.setText(tmp);
	}


	@SuppressWarnings("unchecked")
	protected boolean isBookmark(String url) 
	{
		List<String> bm = MainWin.config.getList("Bookmark");
		
		if (bm.contains(url))
			return(true);
		
		return false;
	}





	public void openURL(String url)
	{
		
		browser.setUrl(url);
		
	}

	
	
	



	protected void doCoCoLink(String url)
	{
		String filename = getFilename(url);
		
		if (filename != null)
		{
			String extension = getExtension(filename);
			
			if (extension != null)
			{
				// handle archives
				if (isExtension("Archive", extension))
				{
					FileObject fileobj;
					try
					{
						fileobj = VFS.getManager().resolveFile("zip:" + url + "!/");
						
						int disk = 0;
						int dos = 0;
						int os9 = 0;
						
						if (fileobj.exists() && fileobj.isReadable())
						{
							for (FileObject f : fileobj.getChildren())
							{
								String ext = this.getExtension(f.getName().getURI());
								
								if (this.isExtension("Disk", ext))
									disk++;
								
								if (this.isExtension("DOS", ext))
									dos++;
								
								//if (this.isExtension("0S9", ext))
								//	os9++;
								
								
							}

							if (disk+dos+os9 == 0)
							{
								showError("No usable files found", "The archive does not contain any files with known extensions.");
							}
							else if ((disk > 0) && (dos == 0) && (os9 == 0))
							{
								if (disk > 1)
								{
									// more than one dsk.. prompt
									MessageBox messageBox = new MessageBox(MainWin.getDisplay().getActiveShell(), SWT.ICON_QUESTION | SWT.YES | SWT.NO );
								    messageBox.setMessage("There are " + disk + " images in this archive.  Would you like to load them into drives " + this.spinnerDrive.getSelection() + " through " + (this.spinnerDrive.getSelection() + disk - 1) + "?");
								    messageBox.setText("Multiple disk images found");
								    int rc = messageBox.open();
								    
								    if (rc == SWT.YES)
								    {
								    	List<String> cmds = new ArrayList<String>();
										int off = 0;
										
								    	for (FileObject f : fileobj.getChildren())
										{
								    		
											String ext = this.getExtension(f.getName().getURI());
											
											if (this.isExtension("Disk", ext))
											{
												cmds.add("dw disk insert " + (this.spinnerDrive.getSelection()+off) + " " + f.getName().getURI());
												off++;
												
											}
											
										}
								    	
										sendCommandDialog(cmds, "Loading disk image..", "Please wait while the server loads the disk image.");
										
								    }
								}
								else
								{
									// just the one.. do it
									for (FileObject f : fileobj.getChildren())
									{
										String ext = this.getExtension(f.getName().getURI());
										
										if (this.isExtension("Disk", ext))
										{
											List<String> cmds = new ArrayList<String>();
											cmds.add("dw disk insert " + this.spinnerDrive.getSelection() + " " + f.getName().getURI());
											
											sendCommandDialog(cmds, "Loading disk image..", "Please wait while the server loads the disk image.");
										}
										
									}
								}
							}
							else if ((disk == 0) && (dos > 0) && (os9 == 0))
							{
								// all dos files
								
								List<String> cmds = new ArrayList<String>();
								String title;
								String msg;
								
								if (this.append_mode == true)
								{
									title = "Appending file(s) to image...";
									msg = "Please wait while the server appends to the image in drive " + this.spinnerDrive.getSelection() + ".";
									
								}
								else
								{
									title = "Creating disk image...";
									msg = "Please wait while the server creates a new DOS disk image and then adds the file(s).";
									cmds.add("dw disk create " + this.spinnerDrive.getSelection());
									cmds.add("dw disk dos format " + this.spinnerDrive.getSelection());
								}
								
								
								for (FileObject f : fileobj.getChildren())
								{
									String ext = this.getExtension(f.getName().getURI());
									
									if (this.isExtension("DOS", ext))
									{
										cmds.add("dw disk dos add " + this.spinnerDrive.getSelection() + " " + f.getName().getURI());
									}
									
								}
								
								sendCommandDialog(cmds, title, msg);
								
								
							}
							
							
						}
					} 
					catch (FileSystemException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					
				}
				// handle disks
				else if (isExtension("Disk", extension))
				{
					List<String> cmds = new ArrayList<String>();
					cmds.add("dw disk insert " + this.spinnerDrive.getSelection() + " " + url);
					
					sendCommandDialog(cmds, "Loading disk image..", "Please wait while the server loads the disk image.");
					
				}
				// DOS
				else if (isExtension("DOS", extension))
				{
					String title = "Appending file to image...";
					String msg = "Please wait while the server appends the file to the image in drive " + this.spinnerDrive.getSelection() + ".";
					List<String> cmds = new ArrayList<String>();
					
					
					if (!this.append_mode)
					{
						title = "Creating disk image...";
						msg = "Please wait while the server creates a new DOS disk image and then adds the file.";
						cmds.add("dw disk create " + this.spinnerDrive.getSelection());
						cmds.add("dw disk dos format " + this.spinnerDrive.getSelection());
					}
					
					
					cmds.add("dw disk dos add " + this.spinnerDrive.getSelection() + " " + url);
					
					sendCommandDialog(cmds, title, msg);
					
				}
				
				// OS9
				
			}
		}
	}


	private void showError(String title, String msg)
	{
		MessageBox messageBox = new MessageBox(MainWin.getDisplay().getActiveShell(), SWT.ICON_ERROR | SWT.OK);
	    messageBox.setMessage(msg);
	    messageBox.setText(title);
	    messageBox.open();
	}





	protected boolean isCocoLink(String location)
	{
		String filename = getFilename(location);
		
		if (filename != null)
		{
			String extension = getExtension(filename);
		
			if ((extension != null) && isCoCoExtension(extension))
			{
				return(true);
			}
			
		}
		
		return false;
	}

	
	private String getExtension(String filename)
	{
		String ext = null;
	
		int lastdot = filename.lastIndexOf('.');
		
		// extension?
		if ((lastdot > 0) && (lastdot < filename.length()-2))
		{
			ext = filename.substring(lastdot+1);
		}
		
		return ext;	
	}

	private String getFilename(String location)
	{
		String filename = null;
	
		int lastslash = location.lastIndexOf('/');
		
		// parse up url
		if ((lastslash > 0) && (lastslash < location.length()-2))
		{
			filename = location.substring(lastslash+1);
		}
		
		return filename;
	}
	

	
	

	private boolean isCoCoExtension(String ext)
	{
		if (isExtension("Disk", ext))
			return true;
		
		if (isExtension("DOS", ext))
			return true;
		
		if (isExtension("OS9", ext))
			return true;
		
		if (isExtension("Archive",ext))
			return true;
		
		return false;
	}

	
	

	private boolean isExtension(String exttype, String ext)
	{
		if (MainWin.config.containsKey(exttype + "Extensions"))
		{
			@SuppressWarnings("unchecked")
			List<String> exts = MainWin.config.getList(exttype + "Extensions");
			
			if (exts.contains(ext.toLowerCase()))
				return(true);
		}
		return false;
	}


	@Override
	protected void checkSubclass()
	{
		// Disable the check that prevents subclassing of SWT components
	}
	
	
	protected void sendCommandDialog(final List<String> cmd, final String title, final String message) 
	{
		final Shell shell = MainWin.getDisplay().getActiveShell();
		
		this.getDisplay().asyncExec(
				  new Runnable() {
					  public void run()
					  {
						  SendCommandWin win = new SendCommandWin(shell, SWT.DIALOG_TRIM, cmd,title, message);
						  win.open();
		
					  }
				  });
	}


	public CTabItem getOurtab() {
		return ourtab;
	}


	public void setOurtab(CTabItem ourtab) {
		this.ourtab = ourtab;
	}


	public Cursor getNormcursor() {
		return normcursor;
	}


	public void setNormcursor(Cursor normcursor) {
		this.normcursor = normcursor;
	}
	
	
	
	
	
}




