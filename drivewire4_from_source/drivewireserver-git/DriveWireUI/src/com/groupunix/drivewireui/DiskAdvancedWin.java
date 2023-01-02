package com.groupunix.drivewireui;

import java.util.Iterator;

import org.apache.commons.configuration.HierarchicalConfiguration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

public class DiskAdvancedWin extends Dialog {

	protected Object result;
	protected  Shell shell;
	
	private DiskDef disk;
	private Table tableParams;
	private Text textSys_Title;
	
	private HierarchicalConfiguration paramDefs;
	private Text textSys_Description;
	
	private String wikiurl = "http://sourceforge.net/apps/mediawiki/drivewireserver/index.php?title=Using_DriveWire";
	private Link linkWiki;
	

	private TableColumn tblclmnNewValue;
	private Button btnApply;
	private Display display;
	private Text textInt;
	private Label lblInt;
	
	private MenuItem mntmAddToTable;
	private MenuItem mntmRemoveFromTable;
	private MenuItem mntmSetToDefault;
	private MenuItem mntmWikiHelp;
	private Text textIntHex;
	private boolean whoa = false;
	private SashForm sashForm;
	private Composite compositeParamlist;
	private Composite compositeDetail;
	private Composite compositePanelIntro;
	private Composite compositePanelSystem;
	private Text textToggle_Title;
	private Text textToggle_Description;
	private Button btnToggleItem;
	private Composite compositeIntval;
	private Text textTitle_Intval;
	private Text textDescription_Intval;
	private Label lblIcon;
	private Label lblD;
	private Label lblH;
	private Composite compositeSpinner;
	private Text textSpinner_Title;
	private Label label_1;
	private Text textSpinner_Description;
	private Label textSpinner_label;
	private Spinner spinner;
	private StackLayout detailLayout;
	private Composite compositeToggle;
	private Label label_2;
	private Label lblSystemParametersCannot;
	private Label label_3;
	private Label label_4;
	private TableColumn tblclmnParameter;
	private TableColumn tblclmnValue;
	
	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public DiskAdvancedWin(Shell parent, int style, DiskDef disk) {
		super(parent, SWT.SHELL_TRIM);
		setText("Parameters for drive " + disk.getDriveNo());
		this.disk = disk;
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open() 
	{
		if (MainWin.master == null || MainWin.master.getMaxIndex("diskparams") < 0)
			this.paramDefs = new HierarchicalConfiguration();
		else
			this.paramDefs = MainWin.master.configurationAt("diskparams");
		

		
		createContents();
		
		shell.addDisposeListener(new DisposeListener() {

			public void widgetDisposed(DisposeEvent arg0) 
			{
				// save size/pos
				
				MainWin.config.setProperty("diskadvancedwin_x", shell.getLocation().x);
				MainWin.config.setProperty("diskadvancedwin_y", shell.getLocation().y);
				MainWin.config.setProperty("diskadvancedwin_w", shell.getSize().x);
				MainWin.config.setProperty("diskadvancedwin_h", shell.getSize().y);
				
				MainWin.config.setProperty("diskadvancedwin_col_parameter", tblclmnParameter.getWidth());
				MainWin.config.setProperty("diskadvancedwin_col_value", tblclmnValue.getWidth());
				MainWin.config.setProperty("diskadvancedwin_col_newvalue", tblclmnNewValue.getWidth());
				
				MainWin.config.setProperty("diskadvancedwin_sashweight_top", sashForm.getWeights()[0]);
				MainWin.config.setProperty("diskadvancedwin_sashweight_bottom", sashForm.getWeights()[1]);
			} });
		
		
		display = getParent().getDisplay();
		
		int x = getParent().getBounds().x + (getParent().getBounds().width / 2) - (shell.getBounds().width / 2);
		int y = getParent().getBounds().y + (getParent().getBounds().height / 2) - (shell.getBounds().height / 2);
		
		shell.setLocation(MainWin.config.getInt("diskadvancedwin_x",x), MainWin.config.getInt("diskadvancedwin_y",y));
		shell.setSize(MainWin.config.getInt("diskadvancedwin_w",528), MainWin.config.getInt("diskadvancedwin_h",500));
		
		
		shell.open();
		shell.layout();
		
		
		
		applySettings();
		applyToggle();
		
	
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		
		
		
		return result;
	}

	
	protected void setIntFromHex(String text)
	{
		this.whoa  = true;
		try
		{
			if ((text != null) && (text != ""))
			{
				
				this.textInt.setText( Integer.parseInt(text.toLowerCase(), 16) +"" );
			}
			else
			{
				this.textInt.setText("");
			}
		}
		catch (NumberFormatException e)
		{
			// dont care
		}
		this.whoa = false;
	}

	protected void setHexFromInt(String text)
	{
		this.whoa  = true;
		try
		{
			if ((text != null) && (text != ""))
			{
				
				this.textIntHex.setText(Integer.toString(Integer.parseInt(text) , 16));
				
				
			}
			else
			{
				this.textIntHex.setText("");
			}
		}
		catch (NumberFormatException e)
		{
			// dont care
		}
		this.whoa  = false;
	}

	protected void doToggle(int sel, String newval)
	{ 
		if (this.tableParams.getItem(sel).getText(1).equals(newval))
		{
			this.tableParams.getItem(sel).setText(2,"");
				
		}
		else
		{
			this.tableParams.getItem(sel).setText(2, newval);
			
		}
		
		applyToggle();
	}

	
	protected void applyToggle()
	{
		int i;
		for (i = 0; i < this.tableParams.getItemCount();i++)
		{
			if (!this.tableParams.getItem(i).getText(2).equals(""))
			{
				this.btnApply.setEnabled(true);
				break;
			}
		}
		
		if (i == this.tableParams.getItemCount())
		{
			this.btnApply.setEnabled(false);
		}
	}
	
	protected void displayItem(String key, int index)
	{
		
		
		if (key == null)
		{
			detailLayout.topControl = this.compositePanelSystem;
			this.compositeDetail.layout();
			
			this.textSys_Title.setText("");
			this.textSys_Description.setText("No disk is inserted in drive " + this.disk.getDriveNo());

		}
		else
		{
			String title = key;
			
			String type = this.paramDefs.getString(key + "[@type]", "system");
			
			if (type.equals("system") || key.startsWith("_"))
			{
				detailLayout.topControl = this.compositePanelSystem;
				this.compositeDetail.layout();
				
				this.textSys_Title.setText(title); 
			
				this.textSys_Description.setText(this.paramDefs.getString(key + "[@detail]","No description for this parameter." ));
			}
			else if (type.equals("boolean"))
			{
				detailLayout.topControl = this.compositeToggle;
				this.compositeDetail.layout();
				
				this.textToggle_Title.setText(title); 
				this.textToggle_Description.setText(this.paramDefs.getString(key + "[@detail]","No description for this parameter." ));
				
				this.btnToggleItem.setText(" " + this.paramDefs.getString(key + "[@toggletext]", "Enable option"));
				
				if (this.tableParams.getItem(index).getText(2).equals(""))
					this.btnToggleItem.setSelection(Boolean.parseBoolean(this.disk.getParam(key).toString()));
				else
					this.btnToggleItem.setSelection(Boolean.parseBoolean(this.tableParams.getItem(index).getText(2)));
				
			}
			else if (type.equals("integer"))
			{
				detailLayout.topControl = this.compositeIntval;
				this.compositeDetail.layout();
				
				this.textTitle_Intval.setText(title); 
				this.textDescription_Intval.setText(this.paramDefs.getString(key + "[@detail]","No definition for this parameter." ));
		
				
				this.lblInt.setText(this.paramDefs.getString(key + "[@inputtext]", "Value"));
				
				if (this.tableParams.getItem(index).getText(2).equals(""))
					this.textInt.setText(this.disk.getParam(key).toString());
				else
					this.textInt.setText(this.tableParams.getItem(index).getText(2));

			}
			else if (type.equals("spinner"))
			{
				detailLayout.topControl = this.compositeSpinner;
				this.compositeDetail.layout();
				
				this.textSpinner_Title.setText(title); 
				this.textSpinner_Description.setText(this.paramDefs.getString(key + "[@detail]","No definition for this parameter." ));
		
				
				this.textSpinner_label.setText(this.paramDefs.getString(key + "[@inputtext]", "Value"));
				
				try
				{
					if (this.paramDefs.containsKey(key + "[@min]"))
					{
						this.spinner.setMinimum(this.paramDefs.getInt(key + "[@min]"));
					}
					
					if (this.paramDefs.containsKey(key + "[@max]"))
					{
						this.spinner.setMaximum(this.paramDefs.getInt(key + "[@max]"));
					}
					
					if (this.tableParams.getItem(index).getText(2).equals(""))
						this.spinner.setSelection(Integer.parseInt(this.disk.getParam(key).toString()));
					else
						this.spinner.setSelection(Integer.parseInt(this.tableParams.getItem(index).getText(2)));
					
					
				}
				catch (NumberFormatException e)
				{
					MainWin.showError("Non numeric value?", "Somehow, we've managed to get a non numeric value into the config in a place where only numbers are allowed.", "This is not normal..  why don't you submit a bug report and let me know how this happened.");
				}
				
				this.lblInt.setVisible(true);
				this.spinner.setVisible(true);
			}
			
		}	
		
		if (this.paramDefs.containsKey(key + "[@wikiurl]"))
		{
			this.linkWiki.setText("<a>Wiki help for " + key + "...</a>");
			this.wikiurl = this.paramDefs.getString(key + "[@wikiurl]");
			this.linkWiki.setVisible(true);
		}
		else
		{
			this.linkWiki.setVisible(false);
		}
		
		
	}

	
	
	
	
	
	
	
	
	
	
	
	

	private void applySettings() 
	{

		Iterator<String> itr = this.disk.getParams();
		
		String key;
		
		while (itr.hasNext())
		{
			key = itr.next();
			this.addOrUpdate(key, this.disk.getParam(key).toString());
		}
		
		
	}
	
	
	private void saveChanges()
	{
		for (int i = 0; i < this.tableParams.getItemCount();i++)
		{
			if (!this.tableParams.getItem(i).getText(2).equals(""))
			{
				MainWin.sendCommand("dw disk set " + this.disk.getDriveNo() + " " + this.tableParams.getItem(i).getText(0) + " " + this.tableParams.getItem(i).getText(2));
			}
		}
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shell = new Shell(getParent(), SWT.DIALOG_TRIM | SWT.RESIZE);
		shell.setImage(SWTResourceManager.getImage(DiskAdvancedWin.class, "/fs/unknown.png"));
		shell.setSize(534, 433);
		shell.setText(getText());
		shell.setLayout(new GridLayout(1, false));
		
		
		sashForm = new SashForm(shell, SWT.SMOOTH | SWT.VERTICAL);
		sashForm.setSashWidth(5);
		GridData gd_sashForm = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_sashForm.heightHint = 252;
		sashForm.setLayoutData(gd_sashForm);
		
		compositeParamlist = new Composite(sashForm, SWT.NONE);
			compositeParamlist.setLayout(new FillLayout(SWT.HORIZONTAL));
		
			
			
			
			tableParams = new Table(compositeParamlist, SWT.BORDER | SWT.FULL_SELECTION);
			
		tableParams.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			displayItem(tableParams.getItem(tableParams.getSelectionIndex()).getText(0), tableParams.getSelectionIndex());
			}

			
		});
		tableParams.setHeaderVisible(true);
		tableParams.setLinesVisible(true);
		
		tblclmnParameter = new TableColumn(tableParams, SWT.NONE);
		tblclmnParameter.setWidth(MainWin.config.getInt("diskadvancedwin_col_parameter",135));
		tblclmnParameter.setText("Parameter");
		
		tblclmnValue = new TableColumn(tableParams, SWT.NONE);
		tblclmnValue.setWidth(MainWin.config.getInt("diskadvancedwin_col_value",191));
		tblclmnValue.setText("Current Value");
		
		tblclmnNewValue = new TableColumn(tableParams, SWT.NONE);
		
		tblclmnNewValue.setWidth(MainWin.config.getInt("diskadvancedwin_col_newvalue",153));
		tblclmnNewValue.setText("New Value");
		
		Menu menu = new Menu(tableParams);
		menu.addMenuListener(new MenuAdapter() {
			@Override
			public void menuShown(MenuEvent e) {
				
				
				mntmAddToTable.setEnabled(false);
				mntmRemoveFromTable.setEnabled(false);
				mntmSetToDefault.setEnabled(false);
				mntmWikiHelp.setEnabled(false);
				mntmWikiHelp.setText("Wiki help...");
				mntmAddToTable.setText("Add item to main display");
				
				if (tableParams.getSelectionIndex() > -1)
				{
				
					if (MainWin.getTPIndex(tableParams.getItem(tableParams.getSelectionIndex()).getText(0)) > -1)
					{
						mntmRemoveFromTable.setEnabled(true);
					}
					else
					{
						mntmAddToTable.setEnabled(true);
					}
					
					if (tableParams.getItem(tableParams.getSelectionIndex()).getText(0) != null)
					{
						mntmAddToTable.setText("Add " + tableParams.getItem(tableParams.getSelectionIndex()).getText(0) + " to main display");
						if (!tableParams.getItem(tableParams.getSelectionIndex()).getText(0).startsWith("_") && paramDefs.containsKey(tableParams.getItem(tableParams.getSelectionIndex()).getText(0) + "[@default]"))
						{
							if ((! tableParams.getItem(tableParams.getSelectionIndex()).getText(1).equals(paramDefs.getString(tableParams.getItem(tableParams.getSelectionIndex()).getText(0) + "[@default]") )) || !(tableParams.getItem(tableParams.getSelectionIndex()).getText(2).equals("") ))   
							{
								mntmSetToDefault.setEnabled(true);
							}
						}
					}
					
					if (tableParams.getItem(tableParams.getSelectionIndex()).getText(0) != null)
					{
						if (paramDefs.containsKey(tableParams.getItem(tableParams.getSelectionIndex()).getText(0) + "[@wikiurl]"))
						{
							mntmWikiHelp.setText("Wiki help for " + tableParams.getItem(tableParams.getSelectionIndex()).getText(0) + "...");
							mntmWikiHelp.setEnabled(true);
						}
					}
				}		
			}
		});
		tableParams.setMenu(menu);
		
		
		
		
		mntmSetToDefault = new MenuItem(menu, SWT.NONE);
		mntmSetToDefault.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				doToggle(tableParams.getSelectionIndex(), paramDefs.getString(tableParams.getItem(tableParams.getSelectionIndex()).getText(0) + "[@default]" , ""));
				displayItem(tableParams.getItem(tableParams.getSelectionIndex()).getText(0),tableParams.getSelectionIndex());
			}
		});
		mntmSetToDefault.setText("Set to default value");
		
		@SuppressWarnings("unused")
		MenuItem spacer = new MenuItem(menu, SWT.SEPARATOR);
		
		mntmWikiHelp = new MenuItem(menu, SWT.NONE);
		mntmWikiHelp.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				MainWin.openURL(this.getClass(), paramDefs.getString( tableParams.getItem(tableParams.getSelectionIndex()).getText(0) + "[@wikiurl]" , "")  );
				
			}
		});
		mntmWikiHelp.setText("Wiki help...");
		
		
		@SuppressWarnings("unused")
		MenuItem spacer2 = new MenuItem(menu, SWT.SEPARATOR);
		
		mntmAddToTable = new MenuItem(menu, SWT.NONE);
		mntmAddToTable.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				MainWin.addDiskTableColumn(tableParams.getItem(tableParams.getSelectionIndex()).getText(0));
				
			}
		});
		mntmAddToTable.setText("Add item to main display");
		
		mntmRemoveFromTable = new MenuItem(menu, SWT.NONE);
		mntmRemoveFromTable.setText("Remove from main display");
		mntmRemoveFromTable.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				MainWin.removeDiskTableColumn(tableParams.getItem(tableParams.getSelectionIndex()).getText(0));
				
			}
		});
		
		compositeDetail = new Composite(sashForm, SWT.BORDER);
		
		detailLayout = new StackLayout();
		
		compositeDetail.setLayout(detailLayout);
		
		
		
		compositePanelIntro = new Composite(compositeDetail, SWT.NONE);
		compositePanelIntro.setLayout(new GridLayout(2, false));
		
		Label lblSelectAParameter = new Label(compositePanelIntro, SWT.NONE);
		lblSelectAParameter.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		lblSelectAParameter.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		lblSelectAParameter.setText("Select a parameter from the list above to view details.");
		
		Label lblWhat = new Label(compositePanelIntro, SWT.NONE);
		GridData gd_lblWhat = new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 2);
		gd_lblWhat.widthHint = 48;
		lblWhat.setLayoutData(gd_lblWhat);
		lblWhat.setImage(SWTResourceManager.getImage(DiskAdvancedWin.class, "/path/info.png"));
		
		Label lblNoteYouCan = new Label(compositePanelIntro, SWT.WRAP);
		lblNoteYouCan.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, true, 1, 1));
		lblNoteYouCan.setText("\r\nYou can resize this window and adjust the size of the table above/details below.  \r\nAny changes will be saved and used by default in the future.\r\n\r\nYou can also use this window to add or remove items from the main disk display.\r\nRight click on any parameter in the table above to access these controls.");
		
		compositePanelSystem = new Composite(compositeDetail, SWT.NONE);
		compositePanelSystem.setLayout(new GridLayout(2, false));
		
		
		textSys_Title = new Text(compositePanelSystem, SWT.READ_ONLY);
		textSys_Title.setText("Item Title");
		textSys_Title.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		textSys_Title.setSize(477, 186);
		textSys_Title.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		textSys_Title.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_BACKGROUND));
		
		Label labelSys_Icon = new Label(compositePanelSystem, SWT.NONE);
		labelSys_Icon.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 2));
		labelSys_Icon.setImage(SWTResourceManager.getImage(DiskAdvancedWin.class, "/path/gears.png"));
		
		textSys_Description = new Text(compositePanelSystem, SWT.READ_ONLY | SWT.WRAP | SWT.MULTI);
		textSys_Description.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		textSys_Description.setFont(SWTResourceManager.getFont("Segoe UI", 8, SWT.NORMAL));
		textSys_Description.setText("Item description");
		textSys_Description.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_BACKGROUND));
		
		lblSystemParametersCannot = new Label(compositePanelSystem, SWT.NONE);
		lblSystemParametersCannot.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.ITALIC));
		GridData gd_lblSystemParametersCannot = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_lblSystemParametersCannot.horizontalIndent = 10;
		lblSystemParametersCannot.setLayoutData(gd_lblSystemParametersCannot);
		lblSystemParametersCannot.setText("System parameters cannot be modified");
		new Label(compositePanelSystem, SWT.NONE);
		
		label_3 = new Label(compositePanelSystem, SWT.NONE);
		label_3.setText(" ");
		new Label(compositePanelSystem, SWT.NONE);
		
		compositeToggle = new Composite(compositeDetail, SWT.NONE);
		compositeToggle.setLayout(new GridLayout(2, false));
		
		textToggle_Title = new Text(compositeToggle, SWT.READ_ONLY);
		textToggle_Title.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		textToggle_Title.setSize(496, 165);
		textToggle_Title.setText("Item Title");
		textToggle_Title.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		textToggle_Title.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_BACKGROUND));
		
		Label label = new Label(compositeToggle, SWT.NONE);
		label.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 2));
		label.setImage(SWTResourceManager.getImage(DiskAdvancedWin.class, "/path/tools.png"));
		
		textToggle_Description = new Text(compositeToggle, SWT.READ_ONLY | SWT.WRAP | SWT.MULTI);
		textToggle_Description.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1));
		textToggle_Description.setText("Item description");
		textToggle_Description.setFont(SWTResourceManager.getFont("Segoe UI", 8, SWT.NORMAL));
		textToggle_Description.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_BACKGROUND));
		textToggle_Description.setBounds(0, 0, 433, 135);
		
		btnToggleItem = new Button(compositeToggle, SWT.CHECK);
		GridData gd_btnToggleItem = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_btnToggleItem.horizontalIndent = 10;
		btnToggleItem.setLayoutData(gd_btnToggleItem);
		btnToggleItem.setText("Toggle item");
		
		
		btnToggleItem.addSelectionListener(new SelectionListener() {

		

			public void widgetSelected(SelectionEvent arg0) {
		
				doToggle(tableParams.getSelectionIndex(), btnToggleItem.getSelection()+"");
			}

			public void widgetDefaultSelected(SelectionEvent arg0) {
				
				
			}} );
		
		new Label(compositeToggle, SWT.NONE);
		
		label_2 = new Label(compositeToggle, SWT.NONE);
		label_2.setText(" ");
		new Label(compositeToggle, SWT.NONE);
		
		compositeIntval = new Composite(compositeDetail, SWT.NONE);
		compositeIntval.setLayout(new GridLayout(4, false));
		
		textTitle_Intval = new Text(compositeIntval, SWT.READ_ONLY);
		textTitle_Intval.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 3, 1));
		textTitle_Intval.setText("Item Title");
		textTitle_Intval.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		textTitle_Intval.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_BACKGROUND));
		textTitle_Intval.setBounds(0, 0, 433, 15);
		
		lblIcon = new Label(compositeIntval, SWT.NONE);
		lblIcon.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 2));
		lblIcon.setImage(SWTResourceManager.getImage(DiskAdvancedWin.class, "/path/tools.png"));
		
		textDescription_Intval = new Text(compositeIntval, SWT.READ_ONLY | SWT.WRAP | SWT.MULTI);
		textDescription_Intval.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 3, 1));
		textDescription_Intval.setText("Item description");
		textDescription_Intval.setFont(SWTResourceManager.getFont("Segoe UI", 8, SWT.NORMAL));
		textDescription_Intval.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_BACKGROUND));
		textDescription_Intval.setBounds(0, 0, 433, 135);
		
		textInt = new Text(compositeIntval, SWT.BORDER);
		GridData gd_textInt = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_textInt.minimumWidth = 100;
		gd_textInt.horizontalIndent = 10;
		textInt.setLayoutData(gd_textInt);
		textInt.setSize(496, 165);
		textInt.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				doToggle(tableParams.getSelectionIndex(), textInt.getText());
				if (!whoa)
					setHexFromInt(((Text) e.getSource()).getText());
			}
		});
		textInt.addVerifyListener(new VerifyListener() {
			public void verifyText(VerifyEvent e) {
				 String string = e.text;
			        char[] chars = new char[string.length()];
			        string.getChars(0, chars.length, chars, 0);
			        for (int i = 0; i < chars.length; i++) {
			          if (!(chars[i] >= '0' && chars[i] <= '9')) 
			          {
			         	
			        	if (! ( (i == 0) && (chars[0] == '-') && (e.start == 0) ) )
			        	{
			        		e.doit = false;
			            	return;
			        	}
			          }
			        }
			        
			        
			        
			}
		});
		
		lblD = new Label(compositeIntval, SWT.NONE);
		lblD.setText("d");
		
		lblInt = new Label(compositeIntval, SWT.NONE);
		GridData gd_lblInt = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_lblInt.horizontalIndent = 10;
		lblInt.setLayoutData(gd_lblInt);
		lblInt.setSize(496, 165);
		lblInt.setText("Value");
		lblInt.setVisible(false);
		new Label(compositeIntval, SWT.NONE);
		
		textIntHex = new Text(compositeIntval, SWT.BORDER);
		GridData gd_textIntHex = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_textIntHex.minimumWidth = 100;
		gd_textIntHex.horizontalIndent = 10;
		textIntHex.setLayoutData(gd_textIntHex);
		textIntHex.setSize(496, 165);
		
		textIntHex.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (!whoa)
					setIntFromHex(((Text) e.getSource()).getText());
			}
		});
		textIntHex.addVerifyListener(new VerifyListener() {
			public void verifyText(VerifyEvent e) {
					e.text = e.text.toLowerCase();
				    char[] chars = new char[e.text.length()];
			        e.text.getChars(0, chars.length, chars, 0);
			        for (int i = 0; i < chars.length; i++) {
			          if (!(chars[i] >= '0' && chars[i] <= '9') && !(chars[i] >= 'a' && chars[i] <= 'f')) 
			          {
			        	  if (! ( (i == 0) && (chars[0] == '-') && (e.start == 0) ) )
			        	  {
			        		  e.doit = false;
			        		  return;
			        	  }
			        	
			          }
			        }
			}
		});
		
		lblH = new Label(compositeIntval, SWT.NONE);
		lblH.setText("h");
		new Label(compositeIntval, SWT.NONE);
		new Label(compositeIntval, SWT.NONE);
		
		compositeSpinner = new Composite(compositeDetail, SWT.NONE);
		compositeSpinner.setLayout(new GridLayout(3, false));
		
		textSpinner_Title = new Text(compositeSpinner, SWT.READ_ONLY);
		textSpinner_Title.setText("Item Title");
		textSpinner_Title.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		textSpinner_Title.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_BACKGROUND));
		textSpinner_Title.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		
		label_1 = new Label(compositeSpinner, SWT.NONE);
		GridData gd_label_1 = new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 2);
		gd_label_1.minimumWidth = 100;
		gd_label_1.horizontalIndent = 20;
		label_1.setLayoutData(gd_label_1);
		label_1.setImage(SWTResourceManager.getImage(DiskAdvancedWin.class, "/path/tools.png"));
		
		textSpinner_Description = new Text(compositeSpinner, SWT.READ_ONLY | SWT.WRAP | SWT.MULTI);
		textSpinner_Description.setText("Item description");
		textSpinner_Description.setFont(SWTResourceManager.getFont("Segoe UI", 8, SWT.NORMAL));
		textSpinner_Description.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_BACKGROUND));
		textSpinner_Description.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 2, 1));
		
		spinner = new Spinner(compositeSpinner, SWT.BORDER);
		GridData gd_spinner = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_spinner.minimumWidth = 100;
		gd_spinner.horizontalIndent = 10;
		spinner.setLayoutData(gd_spinner);
		
		spinner.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent e) {
				doToggle(tableParams.getSelectionIndex(), spinner.getSelection() +"");
			}
		});
		
		textSpinner_label = new Label(compositeSpinner, SWT.NONE);
		textSpinner_label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		textSpinner_label.setVisible(false);
		textSpinner_label.setText("Value");
		new Label(compositeSpinner, SWT.NONE);
		
		label_4 = new Label(compositeSpinner, SWT.NONE);
		label_4.setText(" ");
		new Label(compositeSpinner, SWT.NONE);
		new Label(compositeSpinner, SWT.NONE);
		
		int[] weights = new int[] {1, 1};
		
		weights[0] = MainWin.config.getInt("diskadvancedwin_sashweight_top", 160);
		weights[1] = MainWin.config.getInt("diskadvancedwin_sashweight_bottom", 150);
		
		sashForm.setWeights(weights);
		
		Composite compositeControls = new Composite(shell, SWT.NONE);
		GridLayout gl_compositeControls = new GridLayout(4, false);
		gl_compositeControls.verticalSpacing = 0;
		gl_compositeControls.marginWidth = 0;
		gl_compositeControls.marginHeight = 0;
		compositeControls.setLayout(gl_compositeControls);
		compositeControls.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		linkWiki = new Link(compositeControls, SWT.NONE);
		GridData gd_linkWiki = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_linkWiki.horizontalIndent = 8;
		linkWiki.setLayoutData(gd_linkWiki);
		
		linkWiki.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				MainWin.doDisplayAsync(new Runnable() {

					public void run() 
					{
						MainWin.openURL(this.getClass(),wikiurl);
					}
					
				});
			}
		});
		linkWiki.setText("<a>Wiki Help..</a>");
		linkWiki.setVisible(false);
		
		Button btnOk = new Button(compositeControls, SWT.NONE);
		btnOk.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		btnOk.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) 
			{
				saveChanges();
				e.display.getActiveShell().close();
			}
		});
		btnOk.setText("  Ok  ");
		
		Button btnCancel = new Button(compositeControls, SWT.NONE);
		btnCancel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		btnCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) 
			{
				e.display.getActiveShell().close();
			}
		});
		btnCancel.setText("Cancel");
		
		btnApply = new Button(compositeControls, SWT.NONE);
		btnApply.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		btnApply.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				saveChanges();
			}
		});
		btnApply.setEnabled(false);
		btnApply.setText("Apply");
		
		detailLayout.topControl = this.compositePanelIntro;
		
	}

	

	public void submitEvent(String key, String val)
	{
		addOrUpdate(key,val);
		this.applyToggle();
	}
	
	/*
	private void setLayout()
	{
		this.tableParams.removeAll();
		this.textDescription.setText("");
		this.textItemTitle.setText("");
		tableParams.setBackground(MainWin.colorWhite);
  		tableParams.setLinesVisible(true);
  		tableParams.setHeaderVisible(true);
	}
	*/
	
	private void addOrUpdate(String key, String val)
	{
		int i;
		
		if (key.equals("*eject"))
		{
			this.tableParams.removeAll();
			this.displayItem(null,-1);
		}
		
		if ((val!=null) && !key.startsWith("*"))
		{	
			for (i = 0;i<this.tableParams.getItemCount();i++)
			{
			
				if (this.tableParams.getItem(i).getText(0).equals(key))
				{
					// update value
					this.tableParams.getItem(i).setText(1,val);
					this.doToggle(i, this.tableParams.getItem(i).getText(2));
					
					// are we displaying this item right now
					if (i == this.tableParams.getSelectionIndex())
					{
						this.displayItem(key, i);
					}
					break;
				}
				else if (this.tableParams.getItem(i).getText(0).compareTo(key) > 0)
				{
					this.tableParams.setRedraw(false);
					
					// insert.. sort of
					TableItem item = new TableItem(this.tableParams, SWT.NONE);
					item.setText(0, key);
					item.setText(1, val);
					item.setText(2, "");
					
					for (int j = i;j<this.tableParams.getItemCount() - 1;j++)
					{
						item = new TableItem(this.tableParams, SWT.NONE);
						item.setText(0, this.tableParams.getItem(i).getText(0));
						item.setText(1, this.tableParams.getItem(i).getText(1));
						item.setText(2, this.tableParams.getItem(i).getText(2));
						
						this.tableParams.remove(i);
						
					}
					
					this.tableParams.setRedraw(true);
					
					break;
					
				}
			}
		

		
			if (i == this.tableParams.getItemCount())
			{

				TableItem item = new TableItem(this.tableParams, SWT.NONE);
				item.setText(0, key);
				item.setText(1, val);
				item.setText(2, "");
			
			}
		}
	}
	protected Spinner getSpinner() {
		return spinner;
	}
	protected Text getTextIntHex() {
		return textIntHex;
	}
	protected Label getLblD() {
		return lblD;
	}
	protected Label getLblH() {
		return lblH;
	}
	protected SashForm getSashForm() {
		return sashForm;
	}
	protected Button getBtnToggleItem() {
		return btnToggleItem;
	}
	protected Text getTextDescription_Intval() {
		return textDescription_Intval;
	}
	protected Text getTextTitle_Intval() {
		return textTitle_Intval;
	}
	public Text getTextToggle_Title() {
		return textToggle_Title;
	}
	protected Text getTextToggle_Description() {
		return textToggle_Description;
	}
	protected Text getTextSys_Title() {
		return textSys_Title;
	}
	protected Text getTextSys_Description() {
		return textSys_Description;
	}
	protected Label getLblInt() {
		return lblInt;
	}
	protected Text getTextInt() {
		return textInt;
	}
	protected Composite getCompositeToggle() {
		return compositeToggle;
	}
	protected Text getTextSpinner_Title() {
		return textSpinner_Title;
	}
	protected Label getTextSpinner_label() {
		return textSpinner_label;
	}
	protected Text getTextSpinner_Description() {
		return textSpinner_Description;
	}
	protected Composite getCompositeDetail() {
		return compositeDetail;
	}
}
