package com.groupunix.drivewireui.plugins;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;

import javax.swing.text.NumberFormatter;

import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.FileType;
import org.apache.commons.vfs.VFS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.wb.swt.SWTResourceManager;

import swing2swt.layout.BorderLayout;

import com.groupunix.drivewireserver.dwdisk.DWDisk;
import com.groupunix.drivewireserver.dwdisk.DWDiskDrives;
import com.groupunix.drivewireserver.dwdisk.filesystem.DWDECBFileSystem;
import com.groupunix.drivewireserver.dwdisk.filesystem.DWDECBFileSystemDirEntry;
import com.groupunix.drivewireserver.dwdisk.filesystem.DWFileSystemDirEntry;
import com.groupunix.drivewireserver.dwdisk.filesystem.DWRBFFileDescriptor;
import com.groupunix.drivewireserver.dwdisk.filesystem.DWRBFFileSystem;
import com.groupunix.drivewireserver.dwdisk.filesystem.DWRBFFileSystemDirEntry;
import com.groupunix.drivewireserver.dwdisk.filesystem.RBFFileSystemIDSector;
import com.groupunix.drivewireserver.dwexceptions.DWDiskInvalidSectorNumber;
import com.groupunix.drivewireserver.dwexceptions.DWFileSystemFileNotFoundException;
import com.groupunix.drivewireserver.dwexceptions.DWFileSystemInvalidDirectoryException;
import com.groupunix.drivewireserver.dwexceptions.DWFileSystemInvalidFATException;
import com.groupunix.drivewireserver.dwprotocolhandler.DWUtils;
import com.groupunix.drivewireui.DWLibrary;
import com.groupunix.drivewireui.GradientHelper;
import com.groupunix.drivewireui.MainWin;
import com.groupunix.drivewireui.UIUtils;
import com.groupunix.drivewireui.cococloud.Cloud;
import com.groupunix.drivewireui.library.PathLibraryItem;

public class PathInfoViewer extends Composite
{
	protected static final int DISPLAY_DECIMAL = 0;
	protected static final int DISPLAY_HEX = 1;
	
	private Label lblFileIcon;
	private Label lblFileName;
	private Label lblFilePath;
	private Composite compositeDetail;
	private Composite compositeDECB;
	private Composite compositeRBF;
	private Composite compositeUnknown;
	private Composite compositeDir;
	private Composite compositeCloudInfo;
	
	private Label lblImageType;
	private Label lblImageTypeVal;
	private Label lblSectors;
	private Label lblSectorsVal;
	private Label lblSize;
	private Label lblSizeVal;
	private Label lblFilesystem;
	private Label lblFilesystemVal;
	private StackLayout stackLayout;
	private Table tableDECBFAT;
	private TableColumn tblclmnName;
	private TableColumn tblclmnExt;
	private TableColumn tblclmnType;
	private TableColumn tblclmnFlag;
	private TableColumn tblclmnSize;
	private TableColumn tblclmnChain;
	private Color killedcolor;
	private Color free_color;
	private Color used_color;
	private Color file_color;
	private Color box_color;
	private TableColumn tblclmnContent;
	private Composite compositeFileDetails;
	private Canvas canvasGrans;
	private Label txtFreeGranules;
	private Label lblGranuesInUse;
	private Label lblGranulesUsedBy;
	private Label lblGanuleLegFile;

	private Image granimg;
	private GC grangc;
	private DWDECBFileSystem decbfs = null;
	private DWRBFFileSystem rbfs = null;
	protected ArrayList<Byte> activegranules = new ArrayList<Byte>();
	private Label lblGranulesUsedByDet;
	private Composite compositeToolbar;
	private Composite compositeRBFFSDetail;
	private Label labelInfoIcon;
	private Label lblDdtot;
	private Label lblDdtot_1;
	private Label lblDdtks;
	private Label lblDdmap;
	private Label lblDdnam;
	private Text textDDNAM;
	private Text textDDTOT;
	private Text textDDTKS;
	private Text textDDMAP;
	private Label lblDdbit;
	private Label lblDddir;
	private Label lblDdown;
	private Text textDDBIT;
	private Text textDDDIR;
	private Text textDDOWN;
	private Label lblDdatt;
	private Label lblDddsk;
	private Label lblDdfmt;
	private Label lblDdspt;
	private Text textDDATT;
	private Text textDDDSK;
	private Text textDDFMT;
	private Text textDDSPT;
	private Label lblDdres;
	private Label lblDdbt;
	private Label lblDdbsz;
	private Label lblDddat;
	private Text textDDRES;
	private Text textDDBT;
	private Text textDDBSZ;
	private Text textDDDAT;
	private Group grpValues;
	protected int displayDecHex = DISPLAY_DECIMAL;
	private DWDisk disk;
	private Button btnDecimal;
	private Button btnHex;
	private Composite compositeSectorMap;
	private Canvas canvasSectorMap;
	protected Image sectorMapImage = null;
	private Label lblSectorMap;
	private Label lblUsedSector;
	private Label lblNewLabel;
	private Label lblFreeSector;
	private Label label;
	private CTabFolder tabFolder;
	private CTabItem tbtmDirectory;
	private CTabItem tbtmDetails;
	private Composite compositeRBFDetails;
	private Composite compositeRBFDirectory;
	private Composite compositeRBFFileDetails;
	private Tree treeRBFDir;
	private TreeColumn trclmnPath;
	private TreeColumn trclmnSize;
	private TreeColumn trclmnDateCreated;
	private TreeColumn trclmnAttributes;
	private TreeColumn trclmnOwner;
	private TreeColumn trclmnDateModified;
	private RBFFileSystemIDSector currentRBFIDSec;
	private PathLibraryItem pitem;
	@SuppressWarnings("unused")
	private DWLibrary library = null;
	
	@SuppressWarnings("unused")
	public PathInfoViewer(Composite parent, int style)
	{
		super(parent, style);
		
		setLayout(new BorderLayout(0, 0));
		
		this.killedcolor = new Color(this.getDisplay(), 128,128,255);
		
		this.free_color = new Color(this.getDisplay(), 160,212, 255);
		this.used_color = new Color(this.getDisplay(), 106, 132, 164);
		this.file_color = new Color(this.getDisplay(), 255,255, 128);
		this.box_color = new Color(this.getDisplay(), 64, 96, 128);
		
		granimg = new Image(getDisplay(), 680, 10);
		grangc = new GC(granimg);
		grangc.setAntialias(SWT.ON);
		
		final Composite compositeHeader = new Composite(this, SWT.NONE);
		compositeHeader.setLayoutData(BorderLayout.NORTH);
		compositeHeader.setLayout(new GridLayout(7, false));
		
		compositeToolbar = new Composite(compositeHeader, SWT.NONE);
		compositeToolbar.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 7, 1));
		
		/*
		GradientHelper.applyVerticalGradientBG(compositeToolbar, MainWin.getDisplay().getSystemColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT),MainWin.getDisplay().getSystemColor(SWT.COLOR_TITLE_BACKGROUND));
		
		compositeToolbar.addListener(SWT.Resize, new Listener() {

			@Override
			public void handleEvent(Event event)
			{
				GradientHelper.applyVerticalGradientBG(compositeToolbar, MainWin.getDisplay().getSystemColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT),MainWin.getDisplay().getSystemColor(SWT.COLOR_TITLE_BACKGROUND));
				
				
			} } );
		
		compositeToolbar.setBackgroundMode(SWT.INHERIT_FORCE);
		*/
		
		ToolBar toolBar = new ToolBar(compositeToolbar, SWT.FLAT | SWT.RIGHT);
		toolBar.setSize(426, 28);
		
		ToolItem tltmInsertDisk = new ToolItem(toolBar, SWT.NONE);
		tltmInsertDisk.setImage(SWTResourceManager.getImage(PathInfoViewer.class, "/menu/disk-insert.png"));
		tltmInsertDisk.setText("Insert Disk");
		tltmInsertDisk.addSelectionListener(new SelectionListener(){

			@Override
			public void widgetSelected(SelectionEvent e)
			{
				MainWin.quickInDisk(MainWin.getDisplay().getActiveShell(), pitem.getPath());
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e)
			{
				// TODO Auto-generated method stub
				
			}});
		
		
		//ToolItem toolItem_2 = new ToolItem(toolBar, SWT.SEPARATOR);
		
		ToolItem toolItem = new ToolItem(toolBar, SWT.SEPARATOR);
		
		ToolItem tltmQueryCococloud = new ToolItem(toolBar, SWT.NONE);
		tltmQueryCococloud.setImage(SWTResourceManager.getImage(PathInfoViewer.class, "/menu/help-about-3.png"));
		tltmQueryCococloud.setText("Query CoCoCloud");
		
		tltmQueryCococloud.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e)
			{
				
				String sha1;
				try
				{
					sha1 = UIUtils.getSHA1(disk.getFileObject().getContent().getInputStream());
					
					int diskID = Cloud.lookupDisk(sha1);
					
					if (diskID > -1)
					{
						// display cloud details
						((CloudDiskInfoViewer) compositeCloudInfo).displayDisk(diskID);
						stackLayout.topControl = compositeCloudInfo;
						compositeDetail.layout();
					}
					else
					{
						// offer to add to cloud?
						System.out.println("unknown disk " + sha1);
					}
					
				} 
				catch (FileSystemException e1)
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				
				
			} } );
		
		
		ToolItem toolItem_1 = new ToolItem(toolBar, SWT.SEPARATOR);
		
		ToolItem tltmSectorEditor = new ToolItem(toolBar, SWT.NONE);
		tltmSectorEditor.setImage(SWTResourceManager.getImage(PathInfoViewer.class, "/menu/hex.png"));
		tltmSectorEditor.setText("Sector Editor");
		
		GradientHelper.applyVerticalGradientBG(compositeHeader, MainWin.getDisplay().getSystemColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT),MainWin.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		
		
		compositeHeader.addListener(SWT.Resize, new Listener() {

			@Override
			public void handleEvent(Event event)
			{
				GradientHelper.applyVerticalGradientBG(compositeHeader, MainWin.getDisplay().getSystemColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT),MainWin.getDisplay().getSystemColor(SWT.COLOR_WHITE));
				
			} } );
		
		compositeHeader.setBackgroundMode(SWT.INHERIT_FORCE);
		
		lblFileIcon = new Label(compositeHeader, SWT.NONE);
		
		
		
		
		GridData gd_lblFileIcon = new GridData(SWT.CENTER, SWT.CENTER, false, true, 1, 2);
		gd_lblFileIcon.horizontalIndent = 2;
		gd_lblFileIcon.heightHint = 48;
		gd_lblFileIcon.widthHint = 52;
		gd_lblFileIcon.minimumHeight = 48;
		gd_lblFileIcon.minimumWidth = 48;
		lblFileIcon.setLayoutData(gd_lblFileIcon);
		
		lblFileName = new Label(compositeHeader, SWT.NONE);
		GridData gd_lblFileName = new GridData(SWT.FILL, SWT.BOTTOM, true, false, 1, 1);
		gd_lblFileName.horizontalIndent = 5;
		gd_lblFileName.verticalIndent = 5;
		gd_lblFileName.heightHint = 16;
		gd_lblFileName.minimumWidth = 200;
		gd_lblFileName.minimumHeight = 16;
		lblFileName.setLayoutData(gd_lblFileName);
		lblFileName.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		
		lblImageType = new Label(compositeHeader, SWT.NONE);
		GridData gd_lblImageType = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_lblImageType.verticalIndent = 5;
		lblImageType.setLayoutData(gd_lblImageType);
		lblImageType.setAlignment(SWT.RIGHT);
		lblImageType.setText("Image Type:");
		
		lblImageTypeVal = new Label(compositeHeader, SWT.NONE);
		GridData gd_lblImageTypeVal = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_lblImageTypeVal.verticalIndent = 5;
		gd_lblImageTypeVal.widthHint = 65;
		gd_lblImageTypeVal.minimumWidth = 65;
		lblImageTypeVal.setLayoutData(gd_lblImageTypeVal);
		
		lblSectors = new Label(compositeHeader, SWT.NONE);
		GridData gd_lblSectors = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_lblSectors.verticalIndent = 5;
		lblSectors.setLayoutData(gd_lblSectors);
		lblSectors.setAlignment(SWT.RIGHT);
		lblSectors.setText("Sectors:");
		
		lblSectorsVal = new Label(compositeHeader, SWT.NONE);
		GridData gd_lblSectorsVal = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_lblSectorsVal.verticalIndent = 5;
		gd_lblSectorsVal.widthHint = 65;
		gd_lblSectorsVal.minimumWidth = 65;
		lblSectorsVal.setLayoutData(gd_lblSectorsVal);
		new Label(compositeHeader, SWT.NONE);
		
		lblFilePath = new Label(compositeHeader, SWT.NONE);
		GridData gd_lblFilePath = new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1);
		gd_lblFilePath.horizontalIndent = 5;
		gd_lblFilePath.minimumWidth = 200;
		gd_lblFilePath.minimumHeight = 16;
		gd_lblFilePath.heightHint = 24;
		lblFilePath.setLayoutData(gd_lblFilePath);
		
		lblFilesystem = new Label(compositeHeader, SWT.NONE);
		lblFilesystem.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false, 1, 1));
		lblFilesystem.setText("Filesystem:");
		
		lblFilesystemVal = new Label(compositeHeader, SWT.NONE);
		GridData gd_lblFilesystemVal = new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1);
		gd_lblFilesystemVal.minimumWidth = 65;
		gd_lblFilesystemVal.widthHint = 65;
		lblFilesystemVal.setLayoutData(gd_lblFilesystemVal);
		
		lblSize = new Label(compositeHeader, SWT.NONE);
		lblSize.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false, 1, 1));
		lblSize.setText("Bytes:");
		
		lblSizeVal = new Label(compositeHeader, SWT.NONE);
		GridData gd_lblSizeVal = new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1);
		gd_lblSizeVal.widthHint = 65;
		gd_lblSizeVal.minimumWidth = 65;
		lblSizeVal.setLayoutData(gd_lblSizeVal);
		new Label(compositeHeader, SWT.NONE);
		
		compositeDetail = new Composite(this, SWT.NONE);
		compositeDetail.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		compositeDetail.setLayoutData(BorderLayout.CENTER);
		
		stackLayout = new StackLayout();
		compositeDetail.setLayout(stackLayout);
		
		compositeCloudInfo = new CloudDiskInfoViewer(compositeDetail, SWT.NONE);
		
		
		compositeDECB = new Composite(compositeDetail, SWT.NONE);
		compositeDECB.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		GridLayout gl_compositeDECB = new GridLayout(1, false);
		gl_compositeDECB.marginRight = 5;
		gl_compositeDECB.marginLeft = 5;
		gl_compositeDECB.marginBottom = 5;
		gl_compositeDECB.marginTop = 5;
		compositeDECB.setLayout(gl_compositeDECB);
		
		tableDECBFAT = new Table(compositeDECB, SWT.BORDER | SWT.FULL_SELECTION);
		tableDECBFAT.addSelectionListener(new SelectionAdapter() 
		{
			@Override
			public void widgetSelected(SelectionEvent e) 
			{
				// update file details
				
				TableItem ti = (TableItem) e.item;
				
				try
				{
					String fname = ti.getText(0).trim() + "." + ti.getText(1);
					if (decbfs.hasFile(fname))
					{
						
						activegranules = decbfs.getFAT().getFileGranules(((DWDECBFileSystemDirEntry) decbfs.getDirEntry(fname)).getFirstGranule());
						
						lblGranulesUsedBy.setText(activegranules.size() + " Granules Used By '" + fname + "'");
						
						String granstr = "";
						
						for (Byte b : activegranules)
						{
							if (!granstr.equals(""))
								granstr += ", ";
							granstr += (0xFF & b) +"";
						}
						
						lblGranulesUsedByDet.setText(granstr);
					}
				} 
				catch (IOException e1)
				{
				} 
				catch (DWFileSystemInvalidFATException e1)
				{
				} 
				catch (DWDiskInvalidSectorNumber e1)
				{
				} 
				catch (DWFileSystemFileNotFoundException e1)
				{
				} 
				catch (DWFileSystemInvalidDirectoryException e1)
				{
				}
				
				
				
				canvasGrans.redraw();
				compositeFileDetails.update();
				
			}
		});
		tableDECBFAT.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		tableDECBFAT.setHeaderVisible(true);
		tableDECBFAT.setLinesVisible(true);
		
		tableDECBFAT.setFont(MainWin.logFont);
		
		tblclmnName = new TableColumn(tableDECBFAT, SWT.NONE);
		tblclmnName.setWidth(90);
		tblclmnName.setText("Name");
		
		tableDECBFAT.setSortColumn(tblclmnName);
		
		tblclmnExt = new TableColumn(tableDECBFAT, SWT.NONE);
		tblclmnExt.setWidth(36);
		tblclmnExt.setText("Ext");
		
		tblclmnSize = new TableColumn(tableDECBFAT, SWT.RIGHT);
		tblclmnSize.setWidth(75);
		tblclmnSize.setText("Bytes");
		
		tblclmnContent = new TableColumn(tableDECBFAT, SWT.NONE);
		tblclmnContent.setWidth(135);
		tblclmnContent.setText("Content");
		
		tblclmnType = new TableColumn(tableDECBFAT, SWT.NONE);
		tblclmnType.setWidth(56);
		tblclmnType.setText("Type");
		
		tblclmnFlag = new TableColumn(tableDECBFAT, SWT.NONE);
		tblclmnFlag.setWidth(64);
		tblclmnFlag.setText("Flag");
		
		tblclmnChain = new TableColumn(tableDECBFAT, SWT.RIGHT);
		tblclmnChain.setWidth(210);
		tblclmnChain.setText("Size on disk");
		
		compositeFileDetails = new Composite(compositeDECB, SWT.BORDER);
		compositeFileDetails.setLayout(new GridLayout(4, false));
		
		GridData gd_compositeFileDetails = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_compositeFileDetails.heightHint = 76;
		compositeFileDetails.setLayoutData(gd_compositeFileDetails);
		
		canvasGrans = new Canvas(compositeFileDetails, SWT.DOUBLE_BUFFERED);
		canvasGrans.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) 
			{
				
				grangc.setBackground(free_color);
				grangc.fillRectangle(0, 0, 680, 10);
				
				grangc.setBackground(used_color);
				
				int used = 0;
				
				for (int i = 0; i < 68; i++)
				{
					try
					{
						if ( (decbfs.getFAT().getGranuleByte((byte) i) & 0xFF) != 255)
						{	
							used++;
							grangc.fillRectangle( (i * 10) , 0 , 10, 10);
						}
						
					} 
					catch (IOException e1)
					{
					} 
					catch (DWFileSystemInvalidFATException e1)
					{
					} 
					catch (DWDiskInvalidSectorNumber e1)
					{
					}
					
				}
				
				grangc.setBackground(file_color);
				
				
				for (Byte b : activegranules)
				{
					grangc.fillRectangle( ((b & 0xff) * 10) , 0 , 10, 10);
					
				}
				
				
				grangc.setForeground(box_color);
				
				for (int i = 1; i < 68; i++)
				{
					grangc.drawLine((i * 10), -1, (i * 10), 11);
				}
				
				// try fade
				
				grangc.setAlpha(128);
				grangc.drawLine(0, 0, 680, 0);
				
				grangc.setAlpha(96);
				grangc.drawLine(0, 1, 680, 1);
				
				grangc.setAlpha(64);
				grangc.drawLine(0, 2, 680, 2);
				
				grangc.setAlpha(32);
				grangc.drawLine(0, 3, 680, 3);
				
				grangc.setAlpha(255);
				
				lblGranuesInUse.setText(used + " Total Granules Allocated");
				txtFreeGranules.setText( (68-used) + " Total Granules Free");
				
				if (activegranules.size() > 0)
				{
					lblGranulesUsedBy.setVisible(true);
					lblGanuleLegFile.setVisible(true);
					lblGranulesUsedByDet.setVisible(true); 
				}
				else
				{
					lblGranulesUsedBy.setVisible(false);
					lblGanuleLegFile.setVisible(false);	
					lblGranulesUsedByDet.setVisible(false);
				}
				
				e.gc.setAntialias(SWT.ON);
				e.gc.drawImage(granimg, 0, 0, 680, 10, 0, 0, e.width, e.height);
				
			}
		});
		GridData gd_canvasGrans = new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1);
		gd_canvasGrans.heightHint = 12;
		gd_canvasGrans.minimumHeight = 20;
		canvasGrans.setLayoutData(gd_canvasGrans);
		
		lblGanuleLegFile = new Label(compositeFileDetails, SWT.BORDER | SWT.SHADOW_NONE);
		lblGanuleLegFile.setBackground(this.file_color);
		GridData gd_lblGanuleLegFile = new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1);
		gd_lblGanuleLegFile.verticalIndent = 5;
		gd_lblGanuleLegFile.widthHint = 16;
		gd_lblGanuleLegFile.heightHint = 16;
		lblGanuleLegFile.setLayoutData(gd_lblGanuleLegFile);
		lblGanuleLegFile.setVisible(false);
		
		lblGranulesUsedBy = new Label(compositeFileDetails, SWT.NONE);
		GridData gd_lblGranulesUsedBy = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_lblGranulesUsedBy.verticalIndent = 5;
		lblGranulesUsedBy.setLayoutData(gd_lblGranulesUsedBy);
		lblGranulesUsedBy.setText("");
		
		
		Label lblGranuleLegUsed = new Label(compositeFileDetails, SWT.BORDER | SWT.SHADOW_NONE);
		lblGranuleLegUsed.setBackground(this.used_color);
		GridData gd_lblGranuleLegUsed = new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1);
		gd_lblGranuleLegUsed.verticalIndent = 5;
		gd_lblGranuleLegUsed.heightHint = 16;
		gd_lblGranuleLegUsed.widthHint = 16;
		lblGranuleLegUsed.setLayoutData(gd_lblGranuleLegUsed);
		
		lblGranuesInUse = new Label(compositeFileDetails, SWT.NONE);
		GridData gd_lblGranuesInUse = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_lblGranuesInUse.verticalIndent = 5;
		lblGranuesInUse.setLayoutData(gd_lblGranuesInUse);
		lblGranuesInUse.setText("68 Total Granules Allocated");
		new Label(compositeFileDetails, SWT.NONE);
		
		lblGranulesUsedByDet = new Label(compositeFileDetails, SWT.NONE);
		lblGranulesUsedByDet.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		
		Label lblGranuleLegFree = new Label(compositeFileDetails, SWT.BORDER | SWT.SHADOW_NONE);
		lblGranuleLegFree.setBackground(this.free_color);
		GridData gd_lblGranuleLegFree = new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1);
		gd_lblGranuleLegFree.widthHint = 16;
		gd_lblGranuleLegFree.heightHint = 16;
		lblGranuleLegFree.setLayoutData(gd_lblGranuleLegFree);
		
		txtFreeGranules = new Label(compositeFileDetails, SWT.NONE);
		txtFreeGranules.setText("68 Total Free Granules");
		GridData gd_free = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_free.heightHint = 16;
		txtFreeGranules.setLayoutData(gd_free);
		
		compositeRBF = new Composite(compositeDetail, SWT.NONE);
		compositeRBF.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		compositeRBF.setLayout(new BorderLayout(0, 0));
		
		
		

		
		tabFolder = new CTabFolder(compositeRBF, SWT.BORDER);
		tabFolder.setSelectionBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
		tabFolder.setLayoutData(BorderLayout.CENTER);
		
		tbtmDirectory = new CTabItem(tabFolder, SWT.NONE);
		tbtmDirectory.setText("Directory");
		
		tabFolder.setSelection(tbtmDirectory);
		
		compositeRBFDirectory = new Composite(tabFolder, SWT.NONE);
		tbtmDirectory.setControl(compositeRBFDirectory);
		compositeRBFDirectory.setLayout(new BorderLayout(0, 0));
		
		compositeRBFFileDetails = new Composite(compositeRBFDirectory, SWT.BORDER);
		compositeRBFFileDetails.setLayoutData(BorderLayout.SOUTH);
		
		treeRBFDir = new Tree(compositeRBFDirectory, SWT.BORDER | SWT.FULL_SELECTION);
		treeRBFDir.setHeaderVisible(true);
		treeRBFDir.setLayoutData(BorderLayout.CENTER);
		treeRBFDir.setFont(MainWin.logFont);
		
		
		trclmnPath = new TreeColumn(treeRBFDir, SWT.NONE);
		trclmnPath.setWidth(180);
		trclmnPath.setText("Path");
		
		trclmnSize = new TreeColumn(treeRBFDir, SWT.RIGHT);
		trclmnSize.setWidth(74);
		trclmnSize.setText("Size");
		
		trclmnAttributes = new TreeColumn(treeRBFDir, SWT.NONE);
		trclmnAttributes.setWidth(85);
		trclmnAttributes.setText("Attributes");
		
		trclmnOwner = new TreeColumn(treeRBFDir, SWT.NONE);
		trclmnOwner.setWidth(50);
		trclmnOwner.setText("Owner");
		
		trclmnDateModified = new TreeColumn(treeRBFDir, SWT.NONE);
		trclmnDateModified.setWidth(105);
		trclmnDateModified.setText("Modified");
		
		trclmnDateCreated = new TreeColumn(treeRBFDir, SWT.NONE);
		trclmnDateCreated.setWidth(105);
		trclmnDateCreated.setText("Created");
		
		tbtmDetails = new CTabItem(tabFolder, SWT.NONE);
		tbtmDetails.setText("Details");
		
		compositeRBFDetails = new Composite(tabFolder, SWT.NONE);
		tbtmDetails.setControl(compositeRBFDetails);
		compositeRBFDetails.setLayout(new BorderLayout(0, 0));
		
		
		compositeRBFFSDetail = new Composite(compositeRBFDetails, SWT.BORDER);
		compositeRBFFSDetail.setLayoutData(BorderLayout.SOUTH);
	
		GridLayout gl_compositeRBFFSDetail = new GridLayout(9, false);
		gl_compositeRBFFSDetail.marginBottom = 5;
		gl_compositeRBFFSDetail.marginRight = 5;
		gl_compositeRBFFSDetail.marginLeft = 5;
		gl_compositeRBFFSDetail.marginTop = 5;
		compositeRBFFSDetail.setLayout(gl_compositeRBFFSDetail);
		
		labelInfoIcon = new Label(compositeRBFFSDetail, SWT.NONE);
		labelInfoIcon.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 2));
		labelInfoIcon.setImage(SWTResourceManager.getImage(PathInfoViewer.class, "/path/info.png"));
		
		lblDdtot = new Label(compositeRBFFSDetail, SWT.NONE);
		lblDdtot.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		lblDdtot.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false, 3, 1));
		lblDdtot.setText("RBF Filesystem Details");
		new Label(compositeRBFFSDetail, SWT.NONE);
		new Label(compositeRBFFSDetail, SWT.NONE);
		new Label(compositeRBFFSDetail, SWT.NONE);
		new Label(compositeRBFFSDetail, SWT.NONE);
		new Label(compositeRBFFSDetail, SWT.NONE);
		
		lblDdnam = new Label(compositeRBFFSDetail, SWT.NONE);
		lblDdnam.setToolTipText("Volume name");
		GridData gd_lblDdnam = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_lblDdnam.verticalIndent = 4;
		lblDdnam.setLayoutData(gd_lblDdnam);
		lblDdnam.setText(" DD.NAM:");
		
		textDDNAM = new Text(compositeRBFFSDetail, SWT.BORDER);
		textDDNAM.setEditable(false);
		GridData gd_textDDNAM = new GridData(SWT.FILL, SWT.CENTER, false, false, 3, 1);
		gd_textDDNAM.verticalIndent = 4;
		textDDNAM.setLayoutData(gd_textDDNAM);
		
		
		lblDdatt = new Label(compositeRBFFSDetail, SWT.NONE);
		lblDdatt.setToolTipText("Disk attributes");
		GridData gd_lblDdatt = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_lblDdatt.verticalIndent = 4;
		lblDdatt.setLayoutData(gd_lblDdatt);
		lblDdatt.setText(" DD.ATT:");
		
		textDDATT = new Text(compositeRBFFSDetail, SWT.BORDER);
		textDDATT.setEditable(false);
		GridData gd_textDDATT = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_textDDATT.verticalIndent = 4;
		textDDATT.setLayoutData(gd_textDDATT);
		
		lblDdres = new Label(compositeRBFFSDetail, SWT.NONE);
		lblDdres.setToolTipText("Reserved for future use");
		GridData gd_lblDdres = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_lblDdres.verticalIndent = 4;
		lblDdres.setLayoutData(gd_lblDdres);
		lblDdres.setText(" DD.RES:");
		
		textDDRES = new Text(compositeRBFFSDetail, SWT.BORDER);
		textDDRES.setEditable(false);
		GridData gd_textDDRES = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_textDDRES.verticalIndent = 4;
		textDDRES.setLayoutData(gd_textDDRES);
		
		grpValues = new Group(compositeRBFFSDetail, SWT.NONE);
		
		grpValues.setLayoutData(new GridData(SWT.CENTER, SWT.BOTTOM, false, false, 1, 3));
		grpValues.setText("Display");
		
		btnHex = new Button(grpValues, SWT.RADIO);
		btnHex.setBounds(10, 40, 50, 16);
		btnHex.setText("Hex");
		btnHex.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				displayDecHex = DISPLAY_HEX;
				doDetailsRBFFS();
			}
		});
		
		btnDecimal = new Button(grpValues, SWT.RADIO);
		btnDecimal.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				displayDecHex = DISPLAY_DECIMAL;
				doDetailsRBFFS();
			}
		});
		btnDecimal.setBounds(10, 20, 41, 16);
		btnDecimal.setText("Dec");
		
		lblDdtot_1 = new Label(compositeRBFFSDetail, SWT.NONE);
		lblDdtot_1.setToolTipText("Number of sectors on disk");
		lblDdtot_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblDdtot_1.setText(" DD.TOT:");
		
		textDDTOT = new Text(compositeRBFFSDetail, SWT.BORDER);
		textDDTOT.setEditable(false);
		textDDTOT.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		lblDdbit = new Label(compositeRBFFSDetail, SWT.NONE);
		lblDdbit.setToolTipText("Number of sectors per cluster");
		lblDdbit.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblDdbit.setText(" DD.BIT:");
		
		textDDBIT = new Text(compositeRBFFSDetail, SWT.BORDER);
		textDDBIT.setEditable(false);
		textDDBIT.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		lblDddsk = new Label(compositeRBFFSDetail, SWT.NONE);
		lblDddsk.setToolTipText("Disk identification (for internal use)");
		lblDddsk.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblDddsk.setText(" DD.DSK:");
		
		textDDDSK = new Text(compositeRBFFSDetail, SWT.BORDER);
		textDDDSK.setEditable(false);
		textDDDSK.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		lblDdbt = new Label(compositeRBFFSDetail, SWT.NONE);
		lblDdbt.setToolTipText("Starting sector of the bootstrap file");
		lblDdbt.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblDdbt.setText(" DD.BT:");
		
		textDDBT = new Text(compositeRBFFSDetail, SWT.BORDER);
		textDDBT.setEditable(false);
		textDDBT.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		lblDdtks = new Label(compositeRBFFSDetail, SWT.NONE);
		lblDdtks.setToolTipText("Track size (in sectors)");
		lblDdtks.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblDdtks.setText(" DD.TKS:");
		
		textDDTKS = new Text(compositeRBFFSDetail, SWT.BORDER);
		textDDTKS.setEditable(false);
		textDDTKS.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		lblDddir = new Label(compositeRBFFSDetail, SWT.NONE);
		lblDddir.setToolTipText("Starting sector of the root directory");
		lblDddir.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblDddir.setText(" DD.DIR:");
		
		textDDDIR = new Text(compositeRBFFSDetail, SWT.BORDER);
		textDDDIR.setEditable(false);
		textDDDIR.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		lblDdfmt = new Label(compositeRBFFSDetail, SWT.NONE);
		lblDdfmt.setToolTipText("Disk format, density, number of sides");
		lblDdfmt.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblDdfmt.setText(" DD.FMT:");
		
		textDDFMT = new Text(compositeRBFFSDetail, SWT.BORDER);
		textDDFMT.setEditable(false);
		textDDFMT.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		lblDdbsz = new Label(compositeRBFFSDetail, SWT.NONE);
		lblDdbsz.setToolTipText("Size of the bootstrap file (in bytes)");
		lblDdbsz.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblDdbsz.setText(" DD.BSZ:");
		
		textDDBSZ = new Text(compositeRBFFSDetail, SWT.BORDER);
		textDDBSZ.setEditable(false);
		textDDBSZ.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		lblDdmap = new Label(compositeRBFFSDetail, SWT.NONE);
		lblDdmap.setToolTipText("Number of bytes in the allocation bit map");
		lblDdmap.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblDdmap.setText(" DD.MAP:");
		
		textDDMAP = new Text(compositeRBFFSDetail, SWT.BORDER);
		textDDMAP.setEditable(false);
		textDDMAP.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		lblDdown = new Label(compositeRBFFSDetail, SWT.NONE);
		lblDdown.setToolTipText("Owner\u2019s user number");
		lblDdown.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblDdown.setText(" DD.OWN:");
		
		textDDOWN = new Text(compositeRBFFSDetail, SWT.BORDER);
		textDDOWN.setEditable(false);
		textDDOWN.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		lblDdspt = new Label(compositeRBFFSDetail, SWT.NONE);
		lblDdspt.setToolTipText("Number of sectors per track");
		lblDdspt.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblDdspt.setText(" DD.SPT:");
		
		textDDSPT = new Text(compositeRBFFSDetail, SWT.BORDER);
		textDDSPT.setEditable(false);
		textDDSPT.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		lblDddat = new Label(compositeRBFFSDetail, SWT.NONE);
		lblDddat.setToolTipText("Time of creation (Y:M:D:H:M)");
		lblDddat.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblDddat.setText(" DD.DAT:");
		
		textDDDAT = new Text(compositeRBFFSDetail, SWT.BORDER);
		textDDDAT.setEditable(false);
		textDDDAT.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		
		
		compositeSectorMap = new Composite(compositeRBFDetails, SWT.NONE);
		compositeSectorMap.setLayoutData(BorderLayout.CENTER);
		compositeSectorMap.setLayout(new GridLayout(5, false));
		compositeSectorMap.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		
		lblSectorMap = new Label(compositeSectorMap, SWT.NONE);
		lblSectorMap.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		GridData gd_lblSectorMap = new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1);
		gd_lblSectorMap.verticalIndent = 5;
		lblSectorMap.setLayoutData(gd_lblSectorMap);
		lblSectorMap.setText("Sector Map");
		new Label(compositeSectorMap, SWT.NONE);
		new Label(compositeSectorMap, SWT.NONE);
		
		label = new Label(compositeSectorMap, SWT.NONE);
		label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		
		canvasSectorMap = new Canvas(compositeSectorMap, SWT.NONE);
		canvasSectorMap.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 5, 1));
		
		canvasSectorMap.addPaintListener(new PaintListener() {

			@Override
			public void paintControl(PaintEvent e)
			{
				if (sectorMapImage  == null)
				{
					sectorMapImage = genSectorMapImage();
				}
				
				e.gc.setAntialias(SWT.OFF);
				e.gc.drawImage(sectorMapImage,0, 0, sectorMapImage.getImageData().width, sectorMapImage.getImageData().height, 0, 0, e.width, e.height);
				
			} } );
		
		Label label_1 = new Label(compositeSectorMap, SWT.BORDER);
		label_1.setBackground(this.used_color);
		GridData gd_label_1 = new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1);
		gd_label_1.widthHint = 16;
		gd_label_1.heightHint = 16;
		label_1.setLayoutData(gd_label_1);
		label_1.setText(" ");
		
		lblUsedSector = new Label(compositeSectorMap, SWT.NONE);
		lblUsedSector.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblUsedSector.setText("Used Sector");
		new Label(compositeSectorMap, SWT.NONE);
		
		lblNewLabel = new Label(compositeSectorMap, SWT.BORDER);
		lblNewLabel.setBackground(this.free_color);
		GridData gd_lblNewLabel = new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1);
		gd_lblNewLabel.minimumHeight = 16;
		gd_lblNewLabel.heightHint = 16;
		gd_lblNewLabel.widthHint = 16;
		gd_lblNewLabel.minimumWidth = 16;
		lblNewLabel.setLayoutData(gd_lblNewLabel);
		
		lblFreeSector = new Label(compositeSectorMap, SWT.NONE);
		lblFreeSector.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblFreeSector.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		lblFreeSector.setText("Free Sector");
		
		
		
		compositeUnknown = new Composite(compositeDetail, SWT.NONE);
		compositeUnknown.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		compositeUnknown.setLayout(new BorderLayout(0, 0));
		
		compositeDir = new Composite(compositeDetail, SWT.NONE);
		compositeDir.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		compositeDir.setLayout(new BorderLayout(0, 0));
		
		
	}

		


	



	protected Image genSectorMapImage()
	{
		int xsize = 640;
		int ysize = 320;
		
		Image res = new Image(this.getDisplay(), xsize, ysize);
		
		
		if (this.disk != null)
		{
			DWRBFFileSystem rbffs = new DWRBFFileSystem(this.disk);
			try
			{
				byte[] map = rbffs.getSectorAllocationMap();
				
				xsize = 5 * ((int) Math.sqrt( map.length * 8.0 ) * 2) + 1;
				ysize = 5 * ((int) Math.sqrt( map.length * 8.0 ) / 2 + 2) + 1;
				
				//System.out.println("mapsize: " + map.length + "  xsize: " + xsize + " ysize: " + ysize);
				
				
				res = new Image(this.getDisplay(), xsize, ysize);
				GC gc = new GC(res);
				
				gc.setBackground(new Color(getDisplay(), 255, 255, 255));
				gc.fillRectangle(0, 0, xsize, ysize);
				
				int x = 0;
				int y = 0;
				
				for (int i = 0;i<map.length;i++)
				{
					int data = map[i];
					
					for (int j = 0;j<8;j++)
					{
						gc.setBackground(this.box_color);
						gc.fillRectangle(x*5, y*5, 6, 6);
						
						if ((data & 0x0080) == 0x80)
						{
							gc.setBackground(this.used_color);
							gc.fillRectangle(x*5 + 1, y*5 + 1, 4, 4);
							
						}
						else
						{
							gc.setBackground(this.free_color);
							gc.fillRectangle(x*5 + 1, y*5 + 1, 4, 4);						
						}
						
						data = data << 1;
						x++;
						if (x == xsize/5)
						{
							y++;
							x = 0;
						}
					}
				}
				
				gc.dispose();
			} 
			catch (IOException e)
			{
			} 
			catch (DWDiskInvalidSectorNumber e)
			{
			}
			
			
		}
		
		return res;
	}








	public void displayPath(PathLibraryItem pitem, CTabItem ourtab)
	{
		// header
		
		this.pitem = pitem;
		
		FileObject fobj;
		try
		{
			fobj = VFS.getManager().resolveFile(pitem.getPath());
			
			this.lblFileName.setText(fobj.getName().getBaseName());
			ourtab.setText(fobj.getName().getBaseName() + " ");
			this.lblFilePath.setText(fobj.getName().getFriendlyURI());
			
			if (fobj.getType() == FileType.FOLDER)
			{
				lblFileIcon.setImage(SWTResourceManager.getImage(PathInfoViewer.class, "/path/folder.png"));
				ourtab.setImage(SWTResourceManager.getImage(PathInfoViewer.class, "/menu/folder.png"));
				
				stackLayout.topControl = this.compositeDir;
			}
			else
			{
				
				
				if (fobj.isReadable())
				{
					disk = DWDiskDrives.DiskFromFile(fobj);
					
					this.lblImageTypeVal.setText(DWUtils.prettyFormat(disk.getDiskFormat()));
					this.lblSectorsVal.setText(disk.getDiskSectors() + "");
					
					this.lblSizeVal.setText(fobj.getContent().getSize() + "");
					this.lblFilesystemVal.setText(DWLibrary.prettyFSType(pitem.getFSType()));
					
					
					if (pitem.getFSType() == DWLibrary.FSTYPE_DECB)
					{
						lblFileIcon.setImage(SWTResourceManager.getImage(PathInfoViewer.class, "/path/disk-decb.png"));
						ourtab.setImage(SWTResourceManager.getImage(PathInfoViewer.class, "/fs/decb.png"));
						
						doDetailsDECB(disk);
						
						stackLayout.topControl = this.compositeDECB;
					}
					else if (pitem.getFSType() == DWLibrary.FSTYPE_RBF)
					{
						lblFileIcon.setImage(SWTResourceManager.getImage(PathInfoViewer.class, "/path/disk-rbf.png"));
						ourtab.setImage(SWTResourceManager.getImage(PathInfoViewer.class, "/fs/rbf.png"));
					
						doDetailsRBF();
						this.sectorMapImage = null;
						
						
						stackLayout.topControl = this.compositeRBF;
					}
					else
					{
						lblFileIcon.setImage(SWTResourceManager.getImage(PathInfoViewer.class, "/path/disk-unknown.png"));
						ourtab.setImage(SWTResourceManager.getImage(PathInfoViewer.class, "/fs/unknown.png"));
						
						stackLayout.topControl = this.compositeUnknown;
						
					}
					
					this.compositeDetail.layout();
					
				}
				else
				{
					
				}
				
			}
			
		
			
			
			
		} 
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		
	}
	
	protected void doDetailsRBFFS()
	{
		if (this.currentRBFIDSec != null)
		{
			RBFFileSystemIDSector idsec = this.currentRBFIDSec;
		
	
			this.textDDNAM.setText((String) idsec.getAttrib("DD.NAM"));
			
			if (this.displayDecHex == DISPLAY_DECIMAL)
			{
				this.btnDecimal.setSelection(true);
				this.btnHex.setSelection(false);
				
				this.textDDATT.setText(idsec.getAttrib("DD.ATT").toString());
				this.textDDBIT.setText(idsec.getAttrib("DD.BIT").toString());
				this.textDDBSZ.setText(idsec.getAttrib("DD.BSZ").toString());
				this.textDDBT.setText(idsec.getAttrib("DD.BT").toString());
				this.textDDDAT.setText(idsec.getAttrib("DD.DAT").toString());
				this.textDDDIR.setText(idsec.getAttrib("DD.DIR").toString());
				this.textDDDSK.setText(idsec.getAttrib("DD.DSK").toString());
				this.textDDFMT.setText(idsec.getAttrib("DD.FMT").toString());
				this.textDDMAP.setText(idsec.getAttrib("DD.MAP").toString());
				
				this.textDDOWN.setText(idsec.getAttrib("DD.OWN").toString());
				this.textDDRES.setText(idsec.getAttrib("DD.RES").toString());
				this.textDDSPT.setText(idsec.getAttrib("DD.SPT").toString());
				this.textDDTKS.setText(idsec.getAttrib("DD.TKS").toString());
				this.textDDTOT.setText(idsec.getAttrib("DD.TOT").toString());
			}	
			else if (this.displayDecHex == DISPLAY_HEX)
			{
				this.btnDecimal.setSelection(false);
				this.btnHex.setSelection(true);
				
				this.textDDATT.setText(Integer.toHexString(Integer.parseInt(idsec.getAttrib("DD.ATT").toString())));
				this.textDDBIT.setText(Integer.toHexString(Integer.parseInt(idsec.getAttrib("DD.BIT").toString())));
				this.textDDBSZ.setText(Integer.toHexString(Integer.parseInt(idsec.getAttrib("DD.BSZ").toString())));
				this.textDDBT.setText(Integer.toHexString(Integer.parseInt(idsec.getAttrib("DD.BT").toString())));
				this.textDDDAT.setText(Integer.toHexString(Integer.parseInt(idsec.getAttrib("DD.DAT").toString())));
				this.textDDDIR.setText(Integer.toHexString(Integer.parseInt(idsec.getAttrib("DD.DIR").toString())));
				this.textDDDSK.setText(Integer.toHexString(Integer.parseInt(idsec.getAttrib("DD.DSK").toString())));
				this.textDDFMT.setText(Integer.toHexString(Integer.parseInt(idsec.getAttrib("DD.FMT").toString())));
				this.textDDMAP.setText(Integer.toHexString(Integer.parseInt(idsec.getAttrib("DD.MAP").toString())));
				this.textDDOWN.setText(Integer.toHexString(Integer.parseInt(idsec.getAttrib("DD.OWN").toString())));
				this.textDDRES.setText(Integer.toHexString(Integer.parseInt(idsec.getAttrib("DD.RES").toString())));
				this.textDDSPT.setText(Integer.toHexString(Integer.parseInt(idsec.getAttrib("DD.SPT").toString())));
				this.textDDTKS.setText(Integer.toHexString(Integer.parseInt(idsec.getAttrib("DD.TKS").toString())));
				this.textDDTOT.setText(Integer.toHexString(Integer.parseInt(idsec.getAttrib("DD.TOT").toString())));
			}
		}	
		
	}

	
	
	private void doDetailsRBF()
	{
		if (this.disk != null)
		{
			this.rbfs = new DWRBFFileSystem(this.disk);
			
			
				try
				{
					this.currentRBFIDSec = rbfs.getIDSector();
					
					doDetailsRBFFS();
					
					this.canvasSectorMap.redraw();
				
					doDirectoryRBFFS();
					
				} 
				catch (IOException e)
				{
				}
				catch (DWDiskInvalidSectorNumber e)
				{
				}

			
		}
		
	}




	private void doDirectoryRBFFS()
	{
		treeRBFDir.removeAll();
		addRBFFSItems(null, null);
		
	}








	private void addRBFFSItems(TreeItem parent, DWRBFFileDescriptor fd)
	{
		try
		{
			ArrayList<DWRBFFileSystemDirEntry> rootdir = null;
			
			if (fd == null)
			{
				rootdir = this.rbfs.getRootDirectory();
			}
			else
			{
				rootdir = this.rbfs.getDirectoryFromFD(fd);
			}
			
			for (DWRBFFileSystemDirEntry entry : rootdir)
			{
				if  ((!entry.getFileName().equals(".")) && (!entry.getFileName().equals("..")))
				{
					TreeItem ti = null;
					
					if (parent == null)
					{
						ti = new TreeItem(this.treeRBFDir, SWT.NONE);
					}
					else
					{
						ti = new TreeItem(parent, SWT.NONE);
					}
					
					ti.setText(0, entry.getFileName());
					ti.setText(1, new NumberFormatter().valueToString(entry.getFD().getFilesize()));
					ti.setText(2, UIUtils.prettyRBFAttribs(entry.getFD().getAttributes()));
					ti.setText(3, entry.getFD().getOwner()+"");
					ti.setText(4, entry.getPrettyDateModified());
					ti.setText(5, entry.getPrettyDateCreated());
					
					if (entry.isDirectory())
					{
						ti.setImage(0, SWTResourceManager.getImage(PathInfoViewer.class, "/menu/folder.png"));
						addRBFFSItems(ti, entry.getFD());
					}
					else
					{
						ti.setImage(0, SWTResourceManager.getImage(PathInfoViewer.class, "/filetypes/blank.png"));
					}
				}
			}
			
		} 
		catch (IOException e)
		{
		} 
		catch (DWDiskInvalidSectorNumber e)
		{
		} 
		catch (ParseException e)
		{
		} 
		catch (DWFileSystemInvalidDirectoryException e)
		{
			
		}
		
	}








	private void doDetailsDECB(DWDisk disk)
	{
		tableDECBFAT.removeAll();
		
		boolean showkills = false;
		
		try
		{
			this.decbfs = new DWDECBFileSystem(disk);
			
			this.activegranules = new ArrayList<Byte>();
			
			canvasGrans.redraw();
			compositeFileDetails.update();
			
			
			for (DWFileSystemDirEntry de : decbfs.getDirectory(null))
			{
				if ((((DWDECBFileSystemDirEntry) de).isKilled()  && showkills) || ((DWDECBFileSystemDirEntry) de).isUsed())
				{
					TableItem ti = new TableItem(tableDECBFAT, SWT.NONE);
					
					if (((DWDECBFileSystemDirEntry) de).isKilled())
					{
						ti.setForeground(this.killedcolor);
						ti.setText(0, " ?" + de.getFileName().substring(1));
						ti.setText(2, "Killed");
					}
					else
					{
						ti.setText(0, " " + de.getFileName());
						
						byte[] data = null;
						
						try
						{
							data = decbfs.getFileContents(de.getFileName().trim() + "." + de.getFileExt());
							int ftype = FileTypeDetector.getDECBFileType((DWDECBFileSystemDirEntry) de, data);
							ti.setImage(0, FileTypeDetector.getFileIcon(ftype));
							ti.setText(2, new NumberFormatter().valueToString(data.length));
							ti.setText(3, FileTypeDetector.getFileDescription(ftype));
							
							int gran = (data.length / 2304) + 1; 
							
							ti.setText(6, new NumberFormatter().valueToString(gran * 2304) + " bytes (" + String.format("%2d", gran) + " granules)"); 
							

						}
						catch (Exception e)
						{
							System.out.println("While loading content of " + de.getFileName().trim() + "." + de.getFileExt() + ": " + e.getMessage());
							ti.setText(2, "Error");
							ti.setText(3, e.getMessage());
							ti.setImage(0, SWTResourceManager.getImage(PathInfoViewer.class, "/status/failed_16.png"));
						}
						
						
					}
					
					ti.setText(1, de.getFileExt());
					
					ti.setText(4, de.getPrettyFileType());
					ti.setText(5, ((DWDECBFileSystemDirEntry) de).getPrettyFileFlag());
					
					
					
					
				}
			}
		} 
		catch (IOException e)
		{
			System.out.println("While displaying DECB disk details: " + e.getMessage());
		} 
		catch (DWFileSystemInvalidDirectoryException e)
		{
			System.out.println("While displaying DECB disk details: " + e.getMessage());
		} 
		
	}




	protected Label getLblFileIcon() {
		return lblFileIcon;
	}
	protected Label getLblFileName() {
		return lblFileName;
	}
	public Label getLblFilePath() {
		return lblFilePath;
	}
	protected Composite getCompositeDetail() {
		return compositeDetail;
	}
	protected Composite getCompositeDECB() {
		return compositeDECB;
	}
	protected Composite getCompositeRBF() {
		return compositeRBF;
	}
	protected Composite getCompositeUnknown() {
		return compositeUnknown;
	}
	protected Composite getCompositeDir() {
		return compositeDir;
	}
	protected Label getLblImageTypeVal() {
		return lblImageTypeVal;
	}
	protected Label getLblSectorsVal() {
		return lblSectorsVal;
	}
	protected Label getLblSizeVal() {
		return lblSizeVal;
	}
	protected Label getLblFilesystemVal() {
		return lblFilesystemVal;
	}
	protected Label getLblFilesystem() {
		return lblFilesystem;
	}
	protected Label getLblSize() {
		return lblSize;
	}
	protected Label getLblSectors() {
		return lblSectors;
	}
	protected Label getLblImageType() {
		return lblImageType;
	}
	protected Canvas getCanvasGrans() {
		return canvasGrans;
	}
}
