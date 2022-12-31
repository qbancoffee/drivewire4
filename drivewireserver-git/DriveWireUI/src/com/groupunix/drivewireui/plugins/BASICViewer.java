package com.groupunix.drivewireui.plugins;

import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import swing2swt.layout.BorderLayout;

import com.groupunix.drivewireserver.dwdisk.filesystem.DWFileSystemDirEntry;
import com.groupunix.drivewireui.CoCoView;
import com.groupunix.drivewireui.DWLibrary;
import com.groupunix.drivewireui.GradientHelper;
import com.groupunix.drivewireui.MainWin;
import com.groupunix.drivewireui.UIUtils;

public class BASICViewer extends FileViewer
{

	private static final String TYPENAME = "BASIC Viewer";
	private static final String TYPEIMAGE = "/filetypes/basic.png";
	private Text txtSearch;
	private CoCoView ccview;
	private CCombo comboLine;
	private Text viewAsciiText;
	protected Composite compositeLayers;
	private Font basicFont;
	
	
	public BASICViewer(Composite parent, int style) 
	{
		super(parent, style);
		
		HashMap<String,Integer> fontmap = new HashMap<String,Integer>();
			
		fontmap.put("Droid Sans Mono", SWT.NORMAL);
		
		basicFont = UIUtils.findFont(getDisplay(), fontmap, "WARNING", 60, 22);
		
		
		setLayout(new BorderLayout(0, 0));
		
		final Composite basicHdr = new Composite(this, SWT.NONE);
		basicHdr.setLayoutData(BorderLayout.NORTH);
		
		GradientHelper.applyVerticalGradientBG(basicHdr, MainWin.getDisplay().getSystemColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT),MainWin.getDisplay().getSystemColor(SWT.COLOR_TITLE_BACKGROUND));
		
		basicHdr.addListener(SWT.Resize, new Listener() {

			@Override
			public void handleEvent(Event event)
			{
				GradientHelper.applyVerticalGradientBG(basicHdr, MainWin.getDisplay().getSystemColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT),MainWin.getDisplay().getSystemColor(SWT.COLOR_TITLE_BACKGROUND));
				
			} } );
		
		basicHdr.setBackgroundMode(SWT.INHERIT_FORCE);
		
		
		int cols = MainWin.config.getInt("BasicViewer_columns", 32);
		
		txtSearch = new Text(basicHdr, SWT.BORDER | SWT.SEARCH);
		txtSearch.addSelectionListener(new SelectionAdapter() {
			

			@Override
			public void widgetSelected(SelectionEvent e) 
			{
				
				if ((txtSearch.getText() != null) && (!txtSearch.getText().equals("")))
				{
					ccview.search(txtSearch.getText());
				}
				else
				{
					ccview.search(null);
				}
			}
		});
		txtSearch.setBackground(org.eclipse.wb.swt.SWTResourceManager.getColor(SWT.COLOR_WHITE));
		
		txtSearch.setBounds(5, 4, 148, 21);
		
		
		
		Label btnSearch = new Label(basicHdr, SWT.FLAT | SWT.CENTER);
		btnSearch.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				
				if ((txtSearch.getText() != null) && (!txtSearch.getText().equals("")))
				{
					ccview.search(txtSearch.getText());
				}
				else
				{
					ccview.search(null);
				}
			}
		});
		btnSearch.setImage(org.eclipse.wb.swt.SWTResourceManager.getImage(DWLibrary.class, "/menu/system-search-4.png"));
		btnSearch.setBounds(152, 3, 25, 23);
		
		final ToolBar toolBar_1 = new ToolBar(basicHdr, SWT.FLAT | SWT.RIGHT);
		toolBar_1.setBounds(298, 2, 170, 27);
		
		GradientHelper.applyVerticalGradientBG(toolBar_1, MainWin.getDisplay().getSystemColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT),MainWin.getDisplay().getSystemColor(SWT.COLOR_TITLE_BACKGROUND));
		toolBar_1.addListener(SWT.Resize, new Listener() {

			@Override
			public void handleEvent(Event event)
			{
				GradientHelper.applyVerticalGradientBG(toolBar_1, MainWin.getDisplay().getSystemColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT),MainWin.getDisplay().getSystemColor(SWT.COLOR_TITLE_BACKGROUND));
				
				
			} } );
		
		toolBar_1.setBackgroundMode(SWT.INHERIT_FORCE);
		
		ToolItem tltmCol = new ToolItem(toolBar_1, SWT.RADIO);
		tltmCol.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				MainWin.config.setProperty("BasicViewer_columns", 32);
				MainWin.config.setProperty("BasicViewer_rows", 16);
				ccview.updateImg();
				( (StackLayout) compositeLayers.getLayout()).topControl = ccview;
				compositeLayers.layout();
			}
		});
		tltmCol.setText("32 Col");
		if (cols == 32)
		{
			tltmCol.setSelection(true);
		}
		
		ToolItem tltmCol_1 = new ToolItem(toolBar_1, SWT.RADIO);
		tltmCol_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				MainWin.config.setProperty("BasicViewer_columns", 40);
				MainWin.config.setProperty("BasicViewer_rows", 24);
				ccview.updateImg();
				( (StackLayout) compositeLayers.getLayout()).topControl = ccview;
				compositeLayers.layout();
			}
		});
		tltmCol_1.setText("40 Col");
		if (cols == 40)
		{
			tltmCol_1.setSelection(true);
		}
		
		ToolItem tltmCol_2 = new ToolItem(toolBar_1, SWT.RADIO);
		tltmCol_2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				MainWin.config.setProperty("BasicViewer_columns", 80);
				MainWin.config.setProperty("BasicViewer_rows", 24);
				ccview.updateImg();
				( (StackLayout) compositeLayers.getLayout()).topControl = ccview;
				compositeLayers.layout();
			}
		});
		tltmCol_2.setText("80 Col");
		if (cols == 80)
		{
			tltmCol_2.setSelection(true);
		}
		
		
		ToolItem tltmCol_3 = new ToolItem(toolBar_1, SWT.RADIO);
		tltmCol_3.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) 
			{
				( (StackLayout) compositeLayers.getLayout()).topControl = viewAsciiText;
				compositeLayers.layout();
			}
		});
		tltmCol_3.setText("Text");
		
		
		
		
		final ToolBar toolBar_2 = new ToolBar(basicHdr, SWT.FLAT | SWT.RIGHT);
		toolBar_2.setLocation(485, 2);
		toolBar_2.setSize(100, 27);
		
		
		
		final ToolItem tltmAntialias = new ToolItem(toolBar_2, SWT.CHECK);
		tltmAntialias.setText("Anti-Alias");
		
		tltmAntialias.setSelection( MainWin.config.getBoolean("BasicViewer_antialias",true));
		
		comboLine = new CCombo(basicHdr, SWT.BORDER);
		comboLine.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) 
			{
				try
				{
					ccview.findLine(Integer.parseInt(comboLine.getItem(comboLine.getSelectionIndex())));
				}
				catch (NumberFormatException e1)
				{
				}
			}
		});
		comboLine.setBackground(org.eclipse.wb.swt.SWTResourceManager.getColor(SWT.COLOR_WHITE));
		comboLine.setBounds(192, 4, 88, 21);
		
		Label label_2 = new Label(basicHdr, SWT.SEPARATOR | SWT.VERTICAL);
		label_2.setBounds(473, 5, 2, 19);
		
		Label label = new Label(basicHdr, SWT.SEPARATOR | SWT.VERTICAL);
		label.setBackground(org.eclipse.wb.swt.SWTResourceManager.getColor(SWT.COLOR_WIDGET_BORDER));
		label.setBounds(183, 5, 2, 19);
		
		Label label_1 = new Label(basicHdr, SWT.SEPARATOR | SWT.VERTICAL);
		label_1.setBackground(org.eclipse.wb.swt.SWTResourceManager.getColor(SWT.COLOR_WIDGET_BORDER));
		label_1.setBounds(288, 5, 2, 19);
		
		tltmAntialias.addSelectionListener(new SelectionListener() 
		{

			@Override
			public void widgetSelected(SelectionEvent e)
			{
				MainWin.config.setProperty("BasicViewer_antialias", tltmAntialias.getSelection());
				ccview.updateImg();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e)
			{
				// TODO Auto-generated method stub
				
			} } );
		
		compositeLayers = new Composite(this, SWT.NONE);
		compositeLayers.setLayoutData(BorderLayout.CENTER);
		
		
		compositeLayers.setLayout(new StackLayout());
		
		this.ccview= new CoCoView(compositeLayers, SWT.NONE);
		
		this.viewAsciiText = new Text(compositeLayers, SWT.MULTI | SWT.V_SCROLL | SWT.WRAP);
		viewAsciiText.setEditable(false);
		viewAsciiText.setFont(this.basicFont);
		
		( (StackLayout) compositeLayers.getLayout()).topControl = ccview;
		
	}

	
	@Override
	public void viewFile(DWFileSystemDirEntry direntry, byte[] fc)
	{
		if (this.ccview != null)
		{
			if (!direntry.isAscii())
			{
				fc = DECBTokenize.detokenize(fc);
			}
			
			ccview.setText(new String(fc).split("\r"));
			viewAsciiText.setText(filterAscii(fc));
			
			this.comboLine.removeAll();
			
			if (ccview.getLineRefs() != null)
			{
				for (int i = 0; i < ccview.getLineRefs().size(); i++)
				{
					if (ccview.getLineRefs().get(i) != -1)
					{
						this.comboLine.add("" + ccview.getLineRefs().get(i));
					}
				}
			}
		}
		
	}

	
	
	protected String filterAscii(byte[] fc)
	{
		
		String res = "";
		
		for (int i = 0;i<fc.length;i++)
		{
			if ((fc[i] < 128) && (fc[i] > 0))
			{
				if (fc[i] == 13)
				{
					res += Text.DELIMITER;
				}
				else
				{
					res += (char)fc[i];
				}
			}
		}
		
		
		return res;
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
	



	@Override
	public int getViewable(DWFileSystemDirEntry direntry, byte[] content)
	{
		if (direntry.getFileType() == 0)
			return(3);
		
		return 0;
	}

}
