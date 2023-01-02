package com.groupunix.drivewireui.plugins;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import swing2swt.layout.BorderLayout;

import com.groupunix.drivewireserver.dwdisk.filesystem.DWFileSystemDirEntry;
import com.groupunix.drivewireui.GradientHelper;
import com.groupunix.drivewireui.MainWin;
import com.groupunix.drivewireui.library.FolderLibraryItem;

public class NodeViewer extends FileViewer
{

	private static final String TYPENAME = "Internal/Config Nodes";
	private static final String TYPEIMAGE = "/filetypes/txt.png";
	private Composite compositeToolbar;
	private Label lblIcon;
	private Label lblTitle;
	private Composite compositeBody;
	private Text textNotes;
	private Composite compositeButtons;
	private Button btnSave;
	private Button btnUndo;
	private Label lblNewLabel;
	private String origNotes;
	private FolderLibraryItem item;
	
	public NodeViewer(Composite parent, int style)
	{
		super(parent, style);
		setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		setLayout(new BorderLayout(0, 0));
		
		
		final Composite compositeHeader = new Composite(this, SWT.NONE);
		compositeHeader.setLayoutData(BorderLayout.NORTH);
		compositeHeader.setLayout(new GridLayout(2, false));
		
		GradientHelper.applyVerticalGradientBG(compositeHeader, MainWin.getDisplay().getSystemColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT),MainWin.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		
		
		compositeHeader.addListener(SWT.Resize, new Listener() {

			@Override
			public void handleEvent(Event event)
			{
				GradientHelper.applyVerticalGradientBG(compositeHeader, MainWin.getDisplay().getSystemColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT),MainWin.getDisplay().getSystemColor(SWT.COLOR_WHITE));
				
			} } );
		
		compositeHeader.setBackgroundMode(SWT.INHERIT_FORCE);
			
		lblIcon = new Label(compositeHeader, SWT.NONE);
		
		
		
		
		GridData gd_lblIcon = new GridData(SWT.CENTER, SWT.CENTER, false, true, 1, 3);
		gd_lblIcon.horizontalIndent = 2;
		gd_lblIcon.heightHint = 48;
		gd_lblIcon.widthHint = 52;
		gd_lblIcon.minimumHeight = 48;
		gd_lblIcon.minimumWidth = 48;
		lblIcon.setLayoutData(gd_lblIcon);
		
		lblTitle = new Label(compositeHeader, SWT.NONE);
		GridData gd_lblTitle = new GridData(SWT.FILL, SWT.BOTTOM, true, true, 1, 2);
		gd_lblTitle.horizontalIndent = 5;
		gd_lblTitle.verticalIndent = 5;
		gd_lblTitle.minimumWidth = 200;
		gd_lblTitle.minimumHeight = -1;
		lblTitle.setLayoutData(gd_lblTitle);
		lblTitle.setFont(SWTResourceManager.getFont("Segoe UI", 11, SWT.BOLD));
		
		lblNewLabel = new Label(compositeHeader, SWT.NONE);
		GridData gd_lblNewLabel = new GridData(SWT.LEFT, SWT.BOTTOM, false, false, 1, 1);
		gd_lblNewLabel.minimumHeight = 2;
		gd_lblNewLabel.heightHint = 2;
		lblNewLabel.setLayoutData(gd_lblNewLabel);
		
		compositeBody = new Composite(this, SWT.NONE);
		compositeBody.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		compositeBody.setLayoutData(BorderLayout.CENTER);
		compositeBody.setLayout(new BorderLayout(0, 0));
		
		textNotes = new Text(compositeBody, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL | SWT.MULTI);
		textNotes.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) 
			{
				if (textNotes.getText().equals(origNotes))
				{
					btnUndo.setEnabled(false);
					btnSave.setEnabled(false);
				}
				else
				{
					btnUndo.setEnabled(true);
					btnSave.setEnabled(true);
				}
			}
		});
		
		compositeButtons = new Composite(compositeBody, SWT.NONE);
		compositeButtons.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		compositeButtons.setLayoutData(BorderLayout.SOUTH);
		compositeButtons.setLayout(new GridLayout(2, false));
		
		btnUndo = new Button(compositeButtons, SWT.NONE);
		btnUndo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				textNotes.setText(origNotes);
				btnUndo.setEnabled(false);
				btnSave.setEnabled(false);
			}
		});
		GridData gd_btnUndo = new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1);
		gd_btnUndo.minimumWidth = 50;
		btnUndo.setLayoutData(gd_btnUndo);
		btnUndo.setText("Undo");
		
		btnSave = new Button(compositeButtons, SWT.NONE);
		GridData gd_btnSave = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_btnSave.widthHint = 50;
		gd_btnSave.minimumWidth = 50;
		btnSave.setLayoutData(gd_btnSave);
		btnSave.setText("Save");
		btnSave.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				item.setNotes(textNotes.getText());
				btnUndo.setEnabled(false);
				btnSave.setEnabled(false);
			}
		});
		
	}


	@Override
	public void viewFile(DWFileSystemDirEntry direntry, byte[] content)
	{
		
	}

	@Override
	public String getTypeName()
	{
		return TYPENAME;
	}

	@Override
	public String getTypeIcon()
	{
		return TYPEIMAGE;
	}
	
	
	
	public int getViewable(DWFileSystemDirEntry direntry, byte[] fc)
	{
		return -1;
	}


	public void displayNode(FolderLibraryItem item, CTabItem ourtab)
	{
		this.lblTitle.setText(item.getTitle());
		
		this.lblIcon.setImage(item.getBigIcon());
		
		this.item = item;
		this.origNotes = item.getNotes();
		this.textNotes.setText(item.getNotes());
		
		btnUndo.setEnabled(false);
		btnSave.setEnabled(false);
	}


	public Composite getCompositeToolbar() {
		return compositeToolbar;
	}


	public void setCompositeToolbar(Composite compositeToolbar) {
		this.compositeToolbar = compositeToolbar;
	}
}
