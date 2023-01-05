package com.groupunix.drivewireui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.HierarchicalConfiguration.Node;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

import swing2swt.layout.BorderLayout;

import com.groupunix.drivewireserver.dwdisk.filesystem.DWDECBFileSystemDirEntry;
import com.groupunix.drivewireserver.dwexceptions.DWDiskInvalidSectorNumber;
import com.groupunix.drivewireserver.dwexceptions.DWFileSystemInvalidDirectoryException;
import com.groupunix.drivewireui.configeditor.SortTreeListener;
import com.groupunix.drivewireui.library.AddFolderLibraryItemDialog;
import com.groupunix.drivewireui.library.AddPathLibraryItemDialog;
import com.groupunix.drivewireui.library.AddURLLibraryItemDialog;
import com.groupunix.drivewireui.library.DECBFileLibraryItem;
import com.groupunix.drivewireui.library.FolderLibraryItem;
import com.groupunix.drivewireui.library.LibraryItem;
import com.groupunix.drivewireui.library.PathLibraryItem;
import com.groupunix.drivewireui.library.RBFFileLibraryItem;
import com.groupunix.drivewireui.library.RenameLibraryItemDialog;
import com.groupunix.drivewireui.library.URLLibraryItem;
import com.groupunix.drivewireui.plugins.ASCIIViewer;
import com.groupunix.drivewireui.plugins.BASICViewer;
import com.groupunix.drivewireui.plugins.CloudDiskInfoViewer;
import com.groupunix.drivewireui.plugins.DWBrowser;
import com.groupunix.drivewireui.plugins.FileViewer;
import com.groupunix.drivewireui.plugins.HexViewer;
import com.groupunix.drivewireui.plugins.NIBImageViewer;
import com.groupunix.drivewireui.plugins.NodeViewer;
import com.groupunix.drivewireui.plugins.PMODEImageViewer;
import com.groupunix.drivewireui.plugins.PathInfoViewer;
import com.swtdesigner.SWTResourceManager;

public class DWLibrary extends Composite
{
	private static final String DEFAULT_URL = "http://cococoding.com/dw4";
	
	public static final int TYPE_UNKNOWN = -1;
	public static final int TYPE_FOLDER_MOUNTED = 0;
	public static final int TYPE_FOLDER_LOCAL_PATHS = 1;
	public static final int TYPE_FOLDER_LOCAL_DEVICES = 2;
	public static final int TYPE_FOLDER_CLOUD = 3;
	public static final int TYPE_FOLDER_SERVER = 4;
	
	public static final int TYPE_FOLDER = 5;
	public static final int TYPE_URL = 6;
	public static final int TYPE_PATH = 7;
	public static final int TYPE_DISK = 8;
	public static final int TYPE_DECB_FILE = 9;
	public static final int TYPE_RBF_FILE = 10;
	public static final int TYPE_RBF_DIR = 11;
	public static final int TYPE_DEVICE = 12;
	
	public static final int FSTYPE_UNKNOWN = 0;
	public static final int FSTYPE_RBF = 1;
	public static final int FSTYPE_DECB = 2;
	public static final int FSTYPE_LW16 = 3;
	
	
	public static final int FILETYPE_UNKNOWN = 0;
	public static final int FILETYPE_ASCII = 1;
	public static final int FILETYPE_BASIC_ASCII = 2;
	public static final int FILETYPE_BASIC_TOKENS = 16;
	public static final int FILETYPE_BINARY = 3;
	public static final int FILETYPE_PMODE = 4;
	public static final int FILETYPE_CCMAX = 5;
	public static final int FILETYPE_CCMAX3 = 6;
	public static final int FILETYPE_BASIC_DATA = 7;
	public static final int FILETYPE_SOURCE_ASM = 8;
	public static final int FILETYPE_BIN = 9;
	public static final int FILETYPE_DOC = 10;
	public static final int FILETYPE_FONT = 11;
	public static final int FILETYPE_ARCHIVE = 12;
	public static final int FILETYPE_SOUND = 13;
	public static final int FILETYPE_PRINTDRIVER = 14;
	public static final int FILETYPE_MIDI = 15;
	public static final int FILETYPE_SOURCE_FORTH = 17;
	public static final int FILETYPE_SOURCE_BASIC09 = 18;
	public static final int FILETYPE_SOURCE_C = 19;
	public static final int FILETYPE_SOURCE_C_HEADER = 20;
	public static final int FILETYPE_OS9_MODULE = 21;
	public static final int FILETYPE_OS9_MODULE_6809 = 22;
	public static final int FILETYPE_OS9_MODULE_BASIC09 = 23;
	public static final int FILETYPE_OS9_MODULE_PASCAL = 24;
	public static final int FILETYPE_OS9_MODULE_PRGRM = 25;
	public static final int FILETYPE_OS9_MODULE_SBRTN = 26;
	public static final int FILETYPE_OS9_MODULE_MULTI = 27;
	public static final int FILETYPE_OS9_MODULE_DATA = 28;
	public static final int FILETYPE_OS9_MODULE_USERDEF = 29;
	public static final int FILETYPE_OS9_MODULE_SYSTM = 30;
	public static final int FILETYPE_OS9_MODULE_FLMGR = 31;
	public static final int FILETYPE_OS9_MODULE_DRIVR = 32;
	public static final int FILETYPE_OS9_MODULE_DEVIC = 33;
	public static final int FILETYPE_EXECUTABLE = 255;
	

	
	private Tree tree;


	HierarchicalConfiguration locallib;
	HierarchicalConfiguration cloudlib;
	HierarchicalConfiguration mountedlib;

	ArrayList<FileViewer> viewers;
	Composite compositeViewers;
	Composite compositeFileViewer;
	DWBrowser compositeWebViewer;
	PathInfoViewer compositePathViewer;
	ScrolledComposite compositeFileView;
	NodeViewer compositeNodeViewer;
	private ToolBar toolBarFileViewers;
	StackLayout stackViewersLayout;
	CTabItem ourtab;
	private String currentItemPath = null;

	@SuppressWarnings("unused")
	private CloudDiskInfoViewer compositeCloudDiskViewer;

	protected LibraryItem currentLibraryItem;

	

	public DWLibrary(Composite parent, int style, CTabItem mytab)
	{
		super(parent, style);
		this.ourtab = mytab;
		
		
		
		setLayout(new BorderLayout(0, 0));
		
		loadlibs();
		
		//waitcursor = new Cursor(getDisplay(), SWT.CURSOR_WAIT);
		//normalcursor = new Cursor(getDisplay(), SWT.CURSOR_ARROW);
		
		SashForm sashForm = new SashForm(this, SWT.NONE);
		sashForm.setLayoutData(BorderLayout.CENTER);
		
		Composite compositeTree = new Composite(sashForm, SWT.NONE);
		compositeTree.setLayout(new BorderLayout(0, 0));
		
		//Composite treeHeader = new Composite(compositeTree, SWT.NONE);
		//treeHeader.setLayoutData(BorderLayout.NORTH);
		
		this.tree = new Tree(compositeTree, SWT.VIRTUAL | SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
		tree.setLayoutData(BorderLayout.CENTER);
		
		final Shell shell = this.getShell();
		final Display display = this.getDisplay();
		
		final Listener labelListener = new Listener () 
		{
			public void handleEvent (Event event) 
			{
				Label label = (Label)event.widget;
				Shell shell = label.getShell ();
				switch (event.type) {
					case SWT.MouseDown:
						Event e = new Event ();
						e.item = (TreeItem) label.getData ("_TREEITEM");
						// 	Assuming table is single select, set the selection as if
						// the mouse down event went through to the table
						tree.setSelection (new TreeItem [] {(TreeItem) e.item});
						tree.notifyListeners (SWT.Selection, e);
						shell.dispose ();
						tree.setFocus();
						break;
					case SWT.MouseExit:
						shell.dispose ();
						break;
				}
			}
			
		};

		Listener tableListener = new Listener () 
		{
			Shell tip = null;
			Label label = null;
		
			public void handleEvent (Event event) 
			{
				switch (event.type) 
				{
					case SWT.Dispose:
					case SWT.KeyDown:
					case SWT.MouseMove: 
					{
						if (tip == null) break;
						tip.dispose ();
						tip = null;
						label = null;
						break;
					}
					case SWT.MouseHover: 
					{
						TreeItem item = tree.getItem (new Point (event.x, event.y));
						
						if (item != null)
						{
							if (tip != null && !tip.isDisposed ()) tip.dispose ();
							tip = new Shell (shell, SWT.ON_TOP | SWT.NO_FOCUS | SWT.TOOL);
							tip.setBackground (display.getSystemColor (SWT.COLOR_INFO_BACKGROUND));
							FillLayout layout = new FillLayout ();
							layout.marginWidth = 2;
							tip.setLayout (layout);
							label = new Label (tip, SWT.NONE);
							label.setForeground (display.getSystemColor (SWT.COLOR_INFO_FOREGROUND));
							label.setBackground (display.getSystemColor (SWT.COLOR_INFO_BACKGROUND));
							label.setData ("_TREEITEM", item);
							LibraryItem libitem = (LibraryItem) item.getData();
							if (libitem != null)
								label.setText (libitem.getHoverText());
							else
								label.setText(item.getText());
							
							label.addListener (SWT.MouseExit, labelListener);
							label.addListener (SWT.MouseDown, labelListener);
							Point size = tip.computeSize (SWT.DEFAULT, SWT.DEFAULT);
							Rectangle rect = item.getBounds (0);
							Point pt = tree.toDisplay (rect.x, rect.y);
							tip.setBounds (pt.x, pt.y, size.x, size.y);
							tip.setVisible (true);
						}
					}
				}	
			}
		};
		
		tree.addListener (SWT.Dispose, tableListener);
		tree.addListener (SWT.KeyDown, tableListener);
		tree.addListener (SWT.MouseMove, tableListener);
		tree.addListener (SWT.MouseHover, tableListener);
		
		
		
		
		
		compositeViewers = new Composite(sashForm, SWT.NONE);
		
		stackViewersLayout = new StackLayout();
		compositeViewers.setLayout(stackViewersLayout);
		
		compositeFileViewer = new Composite(compositeViewers, SWT.BORDER);
		compositeFileViewer.setLayout(new BorderLayout(0, 0));
		
		if (! MainWin.config.getBoolean("NoBrowsers", false))
		{
			compositeWebViewer = new DWBrowser(compositeViewers, MainWin.config.getString("Browser_homepage", DWLibrary.DEFAULT_URL), ourtab);
			compositeWebViewer.setLayout(new BorderLayout(0, 0));
		}
		
		compositePathViewer = new PathInfoViewer(compositeViewers, SWT.BORDER);
				
		compositeNodeViewer = new NodeViewer(compositeViewers, SWT.BORDER);
		
		compositeCloudDiskViewer = new CloudDiskInfoViewer(compositeViewers, SWT.BORDER);
		
		compositeFileView = new ScrolledComposite(compositeFileViewer, SWT.NONE);
		compositeFileView.setBackground(org.eclipse.wb.swt.SWTResourceManager.getColor(SWT.COLOR_INFO_BACKGROUND));
		compositeFileView.setExpandVertical(true);
		compositeFileView.setExpandHorizontal(true);
		compositeFileView.setLayoutData(BorderLayout.CENTER);
		
		
	
		loadviewers(compositeFileView);
		
		
		stackViewersLayout.topControl = compositeWebViewer;
		
		
		tree.setHeaderVisible(false);
		tree.addSelectionListener(new SelectionAdapter() 
		{
			@Override
			public void widgetSelected(SelectionEvent e) 
			{
				if (( e != null) && (e.item.getData() != null))
				{
					LibraryItem libitem = (LibraryItem) e.item.getData();
					
					currentItemPath = getItemPath((TreeItem) e.item);
										
					openViewer(libitem);
					
				}						
			}

			
		});
		
		this.tree.setFont(MainWin.logFont);
		
		TreeColumn trclmnItem = new TreeColumn(tree, SWT.LEFT);
		trclmnItem.setWidth(350);
		trclmnItem.setText("Item");
		trclmnItem.addSelectionListener(new SortTreeListener());
		
		final Menu treemenu = new Menu (tree);
		treemenu.addMenuListener(new MenuAdapter() 
		{
			@Override
			public void menuShown(MenuEvent e) 
			{
				
				while (treemenu.getItemCount() > 0)
				{
					treemenu.getItem(0).dispose();
				}
					
				
				
				if ((tree != null) && (tree.getSelectionCount() > 0)  &&  (tree.getSelection()[0].getData()  != null))
				{
					final LibraryItem libitem = (LibraryItem) tree.getSelection()[0].getData();
					
					/*
					if (libitem.getPopupMenuItems() != null)
					{
						for (MenuItemDef mid : libitem.getPopupMenuItems())
						{
							MenuItem mi = new MenuItem(treemenu, SWT.PUSH);
							
							mi.setText(mid.getText());
							mi.setImage(SWTResourceManager.getImage(DWBrowser.class,mid.getImagePath()));
							mi.addSelectionListener(mid.getSelectionAdapter());
							mi.setEnabled(mid.isEnabled());
							
						}
					}
					*/
					
					
					MenuItem mi;
					
					
					switch(libitem.getType() )
					{
					
						case DWLibrary.TYPE_FOLDER:
						
								
							mi = new MenuItem(treemenu, SWT.PUSH);
							mi.setText("Link Local Item...");
							mi.setImage(SWTResourceManager.getImage(DWBrowser.class, "/menu/database-add.png"));
							
							final FolderLibraryItem fi = (FolderLibraryItem) libitem;
							
							mi.addSelectionListener(new SelectionAdapter() {

								@Override
								public void widgetSelected(SelectionEvent e)
								{
									AddPathLibraryItemDialog ap = new AddPathLibraryItemDialog(MainWin.getDisplay().getActiveShell(), SWT.DIALOG_TRIM, fi, tree);
									ap.open();
									
								}

								} );
							
							mi = new MenuItem(treemenu, SWT.PUSH);
							mi.setText("Add URL...");
							mi.setImage(SWTResourceManager.getImage(DWBrowser.class, "/menu/database-add.png"));
							mi.addSelectionListener(new SelectionAdapter() {

								@Override
								public void widgetSelected(SelectionEvent e)
								{
									AddURLLibraryItemDialog ap = new AddURLLibraryItemDialog(MainWin.getDisplay().getActiveShell(), SWT.DIALOG_TRIM, fi, tree);
									ap.open();
									
								}

								} );
							
							
							mi = new MenuItem(treemenu, SWT.PUSH);
							mi.setText("New Folder...");
							mi.setImage(SWTResourceManager.getImage(DWBrowser.class, "/menu/database-add.png"));
							mi.addSelectionListener(new SelectionAdapter() {

								@Override
								public void widgetSelected(SelectionEvent e)
								{
									AddFolderLibraryItemDialog ap = new AddFolderLibraryItemDialog(MainWin.getDisplay().getActiveShell(), SWT.DIALOG_TRIM, fi, tree);
									ap.open();
									
								}

								} );
							
						
							
							if (tree.getSelection()[0].getParentItem() != null) 
							{
								
								mi = new MenuItem(treemenu, SWT.SEPARATOR);
								
								
								mi = new MenuItem(treemenu, SWT.PUSH);
								mi.setText("Rename...");
								mi.setImage(SWTResourceManager.getImage(DWBrowser.class, "/menu/database-edit.png"));
								mi.addSelectionListener(new SelectionAdapter() {

									@Override
									public void widgetSelected(SelectionEvent e)
									{
										RenameLibraryItemDialog ap = new RenameLibraryItemDialog(MainWin.getDisplay().getActiveShell(), SWT.DIALOG_TRIM, libitem, tree);
										ap.open();
										
									}

									} );
								
								final FolderLibraryItem fitem = (FolderLibraryItem) tree.getSelection()[0].getData();
								
								mi = new MenuItem(treemenu, SWT.SEPARATOR);
								
								mi = new MenuItem(treemenu, SWT.PUSH);
								mi.setText("Delete Folder");
								mi.setImage(SWTResourceManager.getImage(DWBrowser.class, "/menu/database-delete.png"));
								
								mi.addSelectionListener(new SelectionAdapter()
								{
									@Override
									public void widgetSelected(SelectionEvent e)
									{
										Node p = fitem.getNode().getParent();
										p.removeChild(fitem.getNode());
										tree.clearAll(true);
									}
										
								});
								
							}
							
							break;
							
						
						case DWLibrary.TYPE_URL:
							
							final URLLibraryItem uitem = (URLLibraryItem) tree.getSelection()[0].getData();
							
							mi = new MenuItem(treemenu, SWT.PUSH);
							mi.setText("Rename...");
							mi.setImage(SWTResourceManager.getImage(DWBrowser.class, "/menu/database-edit.png"));
							
							mi.addSelectionListener(new SelectionAdapter() {
							
								@Override
								public void widgetSelected(SelectionEvent e)
								{
									RenameLibraryItemDialog ap = new RenameLibraryItemDialog(MainWin.getDisplay().getActiveShell(), SWT.DIALOG_TRIM, libitem, tree);
									ap.open();
									
								}

								} );
							
							
							mi = new MenuItem(treemenu, SWT.SEPARATOR);
							
							mi = new MenuItem(treemenu, SWT.PUSH);
							mi.setText("Delete URL");
							mi.setImage(SWTResourceManager.getImage(DWBrowser.class, "/menu/database-delete.png"));
							
							mi.addSelectionListener(new SelectionAdapter()
							{
								@Override
								public void widgetSelected(SelectionEvent e)
								{
									Node p = uitem.getNode().getParent();
									p.removeChild(uitem.getNode());
									tree.clearAll(true);
								}
									
							});
							
							break;
							
						case DWLibrary.TYPE_PATH:
							
							final PathLibraryItem pitem = (PathLibraryItem) tree.getSelection()[0].getData();
							
							if (pitem.isValidDisk())
							{
								

								mi = new MenuItem(treemenu, SWT.PUSH);
								mi.setText("Insert Disk");
								mi.setImage(SWTResourceManager.getImage(DWBrowser.class, "/menu/disk-insert.png"));
								
								mi.addSelectionListener(new SelectionAdapter(){
									
									@Override
									public void widgetSelected(SelectionEvent e)
									{
										MainWin.quickInDisk(MainWin.getDisplay().getActiveShell(), pitem.getPath());
									}
									
								});
								
							}
								
							
							if (pitem.getNode() != null)
							{
								mi = new MenuItem(treemenu, SWT.SEPARATOR);
								
								mi = new MenuItem(treemenu, SWT.PUSH);
								mi.setText("Rename...");
								mi.setImage(SWTResourceManager.getImage(DWBrowser.class, "/menu/database-edit.png"));
								mi.addSelectionListener(new SelectionAdapter() {
	
									@Override
									public void widgetSelected(SelectionEvent e)
									{
										RenameLibraryItemDialog ap = new RenameLibraryItemDialog(MainWin.getDisplay().getActiveShell(), SWT.DIALOG_TRIM, libitem, tree);
										ap.open();
										
									}
	
									} );
								
								if (pitem.isDirectory())
								{
									mi = new MenuItem(treemenu, SWT.SEPARATOR);
									
									mi = new MenuItem(treemenu, SWT.PUSH);
									mi.setText("Remove Directory Link");
									mi.setImage(SWTResourceManager.getImage(DWBrowser.class, "/menu/database-delete.png"));
										
									mi.addSelectionListener(new SelectionAdapter()
									{
										@Override
										public void widgetSelected(SelectionEvent e)
										{
											Node p = pitem.getNode().getParent();
											p.removeChild(pitem.getNode());
											tree.clearAll(true);
										}
											
									});
								}
								else
								{
									if (pitem.getNode() != null)
									{
										mi = new MenuItem(treemenu, SWT.SEPARATOR);
											
										mi = new MenuItem(treemenu, SWT.PUSH);
										mi.setText("Remove File Link");
										mi.setImage(SWTResourceManager.getImage(DWBrowser.class, "/menu/database-delete.png"));
											
										mi.addSelectionListener(new SelectionAdapter()
										{
											@Override
											public void widgetSelected(SelectionEvent e)
											{
												Node p = pitem.getNode().getParent();
												p.removeChild(pitem.getNode());
												tree.clearAll(true);
											}
												
										});
									}
							
								}
							}
							
							break;
							
					}
				
					
				}
			}
		});
		
		tree.setMenu(treemenu);
		
		tree.setData(MainWin.libraryroot);
		
		
		
		tree.addListener(SWT.SetData, new Listener() 
		{
		    public void handleEvent(Event event) 
		    {
		    	
		    	TreeItem item = (TreeItem)event.item;
		    	TreeItem parentItem = item.getParentItem();
		    	
		    	LibraryItem lit = null;
		    	
		    	if (parentItem == null) 
		    	{
		    	
		    		lit = ((LibraryItem[])tree.getData())[event.index];
		    	} 
		    	else 
		    	{
		    		lit = ((LibraryItem) parentItem.getData()).getChildren().get(event.index);
		    	}
		    	
		    	item.setText(0,lit.getTitle());
			    	
		    	item.setImage(lit.getIcon());
		    	item.setData(lit);
		    	
			    item.setItemCount(lit.getChildren().size());
		    }
		    
		});
		
	
		
		tree.setItemCount(MainWin.libraryroot.length);
		sashForm.setWeights(new int[] {350, 700});
		
		//sashForm.setWeights(new int[] {0, 700});
		
	}


	

	protected void openViewer(LibraryItem libitem) 
	{
	
		if (libitem.getType() == DWLibrary.TYPE_DECB_FILE)
		{
			DECBFileLibraryItem decbitem = (DECBFileLibraryItem)libitem;
			
		
			FileViewer bestv = null;
					
			bestv = getBestViewer(decbitem.getEntry(), decbitem.getData());
				
			if (bestv != null)
			{
				
				compositeFileView.setContent(bestv);
				bestv.viewFile(decbitem.getEntry(), decbitem.getData());
				compositeFileView.layout();
					
				ourtab.setText(decbitem.getEntry().getFileName().trim() + "." + decbitem.getEntry().getFileExt() + " ");
				ourtab.setImage(SWTResourceManager.getImage(MainWin.class, bestv.getTypeIcon()));
				stackViewersLayout.topControl = compositeFileViewer;
				compositeViewers.layout();
				
				currentLibraryItem = libitem;
			}
			else
			{
				currentLibraryItem = null;
			}
			
		}
		else if (libitem.getType() == DWLibrary.TYPE_RBF_FILE)
		{
			RBFFileLibraryItem rbfitem = (RBFFileLibraryItem) libitem;
			
			compositeFileView.setContent(viewers.get(1));
			
			try
			{
				viewers.get(1).viewFile(rbfitem.getEntry(), rbfitem.getRBFFS().getFileContentsFromDescriptor(rbfitem.getEntry().getFD()));
				ourtab.setText(rbfitem.getEntry().getFileName() + " ");
				
				stackViewersLayout.topControl = compositeFileViewer;
				compositeViewers.layout();
				
				currentLibraryItem = libitem;
			} 
			catch (IOException e1)
			{
			} 
			catch (DWDiskInvalidSectorNumber e1)
			{
			} 
			catch (DWFileSystemInvalidDirectoryException e1)
			{
				
			}
					
			
		}
		
		else if (libitem.getType() == DWLibrary.TYPE_URL)
		{
			if (MainWin.config.getBoolean("NoBrowsers",false))
			{
				currentLibraryItem = null;
				MainWin.showError("No Browser Support", "Sorry, we don't seem to be able to open a browser on this system.", "The URL we wanted to show you is:  " + ((URLLibraryItem) libitem).getUrl() , false);
			}
			else
			{
				currentLibraryItem = libitem;
				
				if (compositeWebViewer == null)
				{
					compositeWebViewer = new DWBrowser(compositeViewers, MainWin.config.getString("LibraryHomeURL", DWLibrary.DEFAULT_URL), ourtab);
					compositeWebViewer.setLayout(new BorderLayout(0, 0));
				}
				
				compositeWebViewer.openURL( ((URLLibraryItem) libitem).getUrl()  );
			
				stackViewersLayout.topControl = compositeWebViewer;
				compositeViewers.layout();
			}
		}
		else if (libitem.getType() == DWLibrary.TYPE_PATH)
		{
			currentLibraryItem = libitem;
			
			PathLibraryItem pitem = (PathLibraryItem) libitem;
			
			if ((pitem.getFSType() == DWLibrary.FSTYPE_DECB) || (pitem.getFSType() == DWLibrary.FSTYPE_RBF)) 
			{
				compositePathViewer.displayPath(pitem, ourtab);
				stackViewersLayout.topControl = compositePathViewer;
				compositeViewers.layout();
			}
		}
		else if (libitem.getType() == DWLibrary.TYPE_FOLDER)
		{
			currentLibraryItem = libitem;
			
			FolderLibraryItem pitem = (FolderLibraryItem) libitem;
			
			compositeNodeViewer.displayNode(pitem, ourtab);
			stackViewersLayout.topControl = compositeNodeViewer;
			compositeViewers.layout();
			
		}
		else
		{
			currentLibraryItem = null;
		}
		
	}




	protected String getItemPath(TreeItem ti)
	{
		String res = ti.getText();
		
		while (ti.getParentItem() != null)
		{
			ti = ti.getParentItem();
			res = ti.getText() + "|" + res;
		}
		
		return res;
	}




	protected String shortURL(String url)
	{
		String res = url;
	
		if (res.indexOf("//") > -1)
		{
			res = res.substring(res.indexOf("//")+2);
		}
		
		if (res.indexOf("/") > -1)
		{
			res = res.substring(0, res.indexOf("/"));
		}
		
		return res;
	}




	public FileViewer getBestViewer(DWDECBFileSystemDirEntry direntry, byte[] contents)
	{
		int bestvote = -1;
		FileViewer bestv = null;
		
		for (FileViewer fv : viewers)
		{
			
			int vote = fv.getViewable(direntry, contents);
			
			if (vote > 0)
			{
				//
			
				if (vote > bestvote)
				{
					bestv = fv;
					bestvote = vote;
				}
				
				if (toolBarFileViewers != null)
				{
					for (ToolItem ti: toolBarFileViewers.getItems())
					{
						if ((ti != null) && (ti.getToolTipText() != null) && (ti.getToolTipText().equals(fv.getTypeName())))
								ti.setEnabled(true);
					}
				}
				
			}
			else if (toolBarFileViewers != null)
			{
				
				for (ToolItem ti: toolBarFileViewers.getItems())
				{
					if ((ti != null) && (ti.getToolTipText() != null) && (ti.getToolTipText().equals(fv.getTypeName())))
							ti.setEnabled(false);
				}
			}
		} 
		
		
		return bestv;
	}




	private void loadviewers(Composite parent)
	{
		this.viewers = new ArrayList<FileViewer>();
		
		viewers.add(new HexViewer(parent, SWT.NONE));
		viewers.add(new ASCIIViewer(parent, SWT.NONE));
		viewers.add(new BASICViewer(parent, SWT.NONE));
		//viewers.add(new HSCREENImageViewer(parent, SWT.NONE));
		viewers.add(new NIBImageViewer(parent, SWT.NONE));
		viewers.add(new PMODEImageViewer(parent, SWT.NONE));
		
	}



	private void loadlibs()
	{
		
	
		
		if (! MainWin.config.containsKey("Library.Local.updated") )
		{
			MainWin.config.addProperty("Library.Local.autocreated", System.currentTimeMillis());
			MainWin.config.addProperty("Library.Local.updated", 0);
		}
		
		locallib = MainWin.config.configurationAt("Library.Local");
		
		if (! MainWin.config.containsKey("Library.Cloud.updated") )
		{
			MainWin.config.addProperty("Library.Cloud.autocreated", System.currentTimeMillis());
			MainWin.config.addProperty("Library.Cloud.updated", 0);
		}
		
		cloudlib = MainWin.config.configurationAt("Library.Cloud");
		
	}










	public static String prettyFSType(int fsType)
	{
		String res = "Unknown";
		
		if (fsType == DWLibrary.FSTYPE_DECB)
			res = "DECB";
		else if (fsType == DWLibrary.FSTYPE_RBF)
			res = "OS9/RBF";
		else if (fsType == DWLibrary.FSTYPE_LW16)
			res = "LW16";
		return res;
	}




	public void setCurrentItemPath(String cp)
	{
		/*
		if (cp != null)
		{
			String[] pp = cp.split("\\|");
			Vector<String> path = new Vector<String>();
			
			for (String p : pp)
			{
				System.out.println("adding " + p);
				path.add(p);
			}
			
			TreeItem ti = null;
			
			for (int i = 0;i<tree.getItemCount();i++)
			{
				if (tree.getItem(i).getText().equals(path.get(0)))
				{
					System.out.println("root " + path.get(0));
					ti = tree.getItem(i);
					ti.setExpanded(true);
					tree.select(ti);
				}
				else
				{
					System.out.println("root nm " + path.get(0) + " vs " + tree.getItem(i).getText());
				}
			}
			
			path.remove(0);
			
			selectItemPath(ti,path);
			
		}
		
		*/
	}




	@SuppressWarnings("unused")
	private void selectItemPath(TreeItem pti, Vector<String> path)
	{
		if ((path.size()>0) && (pti != null))
		{
			TreeItem ti = null;
			
			for (int i = 0;i<pti.getItemCount();i++)
			{
				
				if (pti.getItem(i).getText().equals(path.get(0)))
				{
					System.out.println("path " + path.get(0));
					ti = pti.getItem(i);
					ti.setExpanded(true);
					tree.select(ti);
				}
			}
			
			path.remove(0);
			selectItemPath(ti,path);
		}
	}




	public String getCurrentItemPath()
	{
		return currentItemPath;
	}




	public CTabItem getOurTab()
	{
		
		return this.ourtab;
	}


	public void updateTree()
	{
		
		if (this.tree != null)
			tree.clearAll(true);
		
		if (this.currentLibraryItem != null)
			this.openViewer(this.currentLibraryItem);
		
	}

	
	
	
}


