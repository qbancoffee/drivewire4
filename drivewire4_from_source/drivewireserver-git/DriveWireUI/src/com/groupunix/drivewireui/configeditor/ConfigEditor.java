package com.groupunix.drivewireui.configeditor;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalConfiguration.Node;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.tree.ConfigurationNode;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.wb.swt.SWTResourceManager;

import com.groupunix.drivewireui.ConfigItem;
import com.groupunix.drivewireui.Connection;
import com.groupunix.drivewireui.DWUIOperationFailedException;
import com.groupunix.drivewireui.MainWin;
import com.groupunix.drivewireui.UIUtils;
import com.groupunix.drivewireui.configeditor.SortTreeListener;
import java.util.Iterator;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;

public class ConfigEditor extends Shell
{

	private Tree tree;
	private Label lblItemTitle;
	private Composite scrolledComposite;
	private Composite composite_1;
	private Label textDescription;
	private Button btnToggle;
	private Button btnApply;
	
	private XMLConfiguration wc = new XMLConfiguration(); 
	private XMLConfiguration masterc = new XMLConfiguration();
	
	private ToolBar toolBar;
	private ToolItem tltmMidi;
	private ToolItem tltmLogging;
	private ToolItem tltmDevice;
	private ToolItem tltmPrinting;
	private ToolItem tltmNetworking;
	private ToolItem tltmAll;
	private ToolItem tltmCheckAdvanced;

	
	private Node selected;
	private Label lblIntText;
	private Spinner spinnerInt;
	//private TreeColumn trclmnNewColumn;
	
	private ModifyListener spinnerModifyListener;
	private Combo comboList;
	private Label lblList;
	private ModifyListener comboModifyListener;
	private Text textString;
	private Label lblString;
	private Button btnFileDir;
	private ToolItem tltmDisk;
	private Label lblIntTextS1;
	private Label lblIntTextS2;
	private Spinner spinnerIntS2;
	private Label lblDescriptS1;
	
	private Text textDescriptS1;
	private Text textNameS1;
	private Label lblNameS1;

	
	@Override
	protected void checkSubclass()
	{
		// Disable the check that prevents subclassing of SWT components
	}
	
	
	
	

	
	
	/**
	 * Create the shell.
	 * @param display
	 */
	public ConfigEditor(Display display)
	{
		super(display, SWT.SHELL_TRIM);
	
		createContents();
		loadMaster();
		if (!this.isDisposed())
		{
			loadConfig();
		}
	}

	
	
	

	/**
	 * Create contents of the shell.
	 */
	protected void createContents()
	{
		setText("Configuration Editor");
		setSize(830, 634);
	
		
		/*
		addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(ControlEvent e) {
				setRedraw(false);
				toolBar.setLayoutData(new RowData(getSize().x - 26, -1));
				scrolledComposite.setLayoutData(new RowData(getSize().x - 26, 150));
				composite_1.setLayoutData(new RowData(getSize().x - 26, 36));
				tree.setLayoutData(new RowData(getSize().x - 46, getSize().y-280));
				setRedraw(true);
			}
		});
		*/
		
		setLayout(new GridLayout(1, false));
		
		toolBar = new ToolBar(this, SWT.FLAT | SWT.RIGHT);
		toolBar.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		toolBar.addMouseListener(new MouseAdapter() {
			@SuppressWarnings("unchecked")
			@Override
			public void mouseUp(MouseEvent e) {
				commitNodes();
				tree.removeAll();
				loadConfig(null, wc.getRootNode().getChildren());
			}
		});
		
		tltmAll= new ToolItem(toolBar, SWT.RADIO);
		tltmAll.setImage(SWTResourceManager.getImage(ConfigEditor.class, "/menu/view-list-tree-4.png"));
		tltmAll.setSelection(true);
		tltmAll.setText("All");
		
		tltmDevice = new ToolItem(toolBar, SWT.RADIO);
		tltmDevice.setImage(SWTResourceManager.getImage(ConfigEditor.class, "/menu/connect.png"));
		tltmDevice.setText("Device");
		
		tltmDisk = new ToolItem(toolBar, SWT.RADIO);
		tltmDisk.setImage(SWTResourceManager.getImage(ConfigEditor.class, "/menu/disk-insert.png"));
		tltmDisk.setText("Disk");
		
		tltmPrinting = new ToolItem(toolBar, SWT.RADIO);
		tltmPrinting.setImage(SWTResourceManager.getImage(ConfigEditor.class, "/menu/document-print.png"));
		tltmPrinting.setText("Printing");
		
		tltmMidi = new ToolItem(toolBar, SWT.RADIO);
		tltmMidi.setImage(SWTResourceManager.getImage(ConfigEditor.class, "/menu/music.png"));
		tltmMidi.setText("MIDI");
		
		tltmNetworking = new ToolItem(toolBar, SWT.RADIO);
		tltmNetworking.setImage(SWTResourceManager.getImage(ConfigEditor.class, "/menu/preferences-system-network-2.png"));
		tltmNetworking.setText("Networking");
		
		tltmLogging = new ToolItem(toolBar, SWT.RADIO);
		tltmLogging.setImage(SWTResourceManager.getImage(ConfigEditor.class, "/menu/documentation.png"));
		tltmLogging.setText("Logging");
		
		@SuppressWarnings("unused")
		ToolItem toolItem = new ToolItem(toolBar, SWT.SEPARATOR);
		
		tltmCheckAdvanced = new ToolItem(toolBar, SWT.CHECK);
		tltmCheckAdvanced.setImage(SWTResourceManager.getImage(ConfigEditor.class, "/menu/cog-edit.png"));
		tltmCheckAdvanced.setText("Show Advanced Items");
		

		
		tree = new Tree(this, SWT.BORDER | SWT.FULL_SELECTION);
		tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		tree.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) 
			{
				selected = (Node) tree.getSelection()[0].getData("param");
				displayParam(tree.getSelection()[0]);
			}
		});
		
		
		tree.setHeaderVisible(true);
		
		TreeColumn trclmnItem = new TreeColumn(tree, SWT.LEFT);
		trclmnItem.setWidth(250);
		trclmnItem.setText("Item");
		trclmnItem.addSelectionListener(new SortTreeListener());
		
		TreeColumn trclmnValue = new TreeColumn(tree, SWT.NONE);
		trclmnValue.setWidth(380);
		trclmnValue.setText("Value");
		
		scrolledComposite = new Composite(this, SWT.NONE);
		scrolledComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		
		lblItemTitle = new Label(scrolledComposite, SWT.NONE);
		lblItemTitle.setBounds(10, 10, 239, 24);
		lblItemTitle.setText("");
		lblItemTitle.setFont(SWTResourceManager.getBoldFont(this.getDisplay().getSystemFont()));
		
		
		textDescription = new Label(scrolledComposite, SWT.WRAP);
		textDescription.setBounds(10, 40, 647, 60);
		textDescription.setText("");
		textDescription.setVisible(true);
		
		textNameS1 = new Text(scrolledComposite, SWT.BORDER);
		textNameS1.setBounds(10,60,137,24);
		
		lblNameS1 = new Label(scrolledComposite, SWT.NONE);
		lblNameS1.setBounds(10, 40, 137, 24);
		
		
		textDescriptS1 = new Text(scrolledComposite, SWT.BORDER);
		textDescriptS1.setBounds(155,60,502,24);
		
		lblDescriptS1 = new Label(scrolledComposite, SWT.NONE);
		lblDescriptS1.setBounds(155, 40, 502, 24);
		
		btnToggle = new Button(scrolledComposite, SWT.CHECK);
		btnToggle.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateBoolean(selected,btnToggle.getSelection());
			}
		});
		btnToggle.setBounds(10,113, 300, 24);
		
		composite_1 = new Composite(this, SWT.NONE);
		composite_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		FillLayout fl_composite_1 = new FillLayout(SWT.HORIZONTAL);
		fl_composite_1.spacing = 10;
		fl_composite_1.marginWidth = 5;
		fl_composite_1.marginHeight = 5;
		composite_1.setLayout(fl_composite_1);
		
		Button btnBackup = new Button(composite_1, SWT.NONE);
		btnBackup.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				doBackup();
			}
		});
		btnBackup.setText("Save to file...");
		
		Button btnRestore = new Button(composite_1, SWT.NONE);
		btnRestore.setText("Load from file...");
		btnRestore.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				doRestore();
			}
		});
		
		
		Label lblNewLabel = new Label(composite_1, SWT.NONE);
		lblNewLabel.setText(" ");
		
		btnApply = new Button(composite_1, SWT.NONE);
		btnApply.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				applyChanges();
			}
		});
		btnApply.setText("Write to server");
		btnApply.setEnabled(true);
		
		
		Button btnCancel = new Button(composite_1, SWT.NONE);
		btnCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				close();
			}
		});
		btnCancel.setText("Close");
		
		spinnerInt = new Spinner(scrolledComposite, SWT.BORDER);
		
		spinnerInt.setBounds(10, 114, 100, 28);
		
		
		this.spinnerModifyListener = new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				updateInt(selected,spinnerInt.getSelection());
			}
		};
		
		
		lblIntText = new Label(scrolledComposite, SWT.NONE);
		lblIntText.setBounds(116, 116, 398, 24);
		
		lblIntTextS1 = new Label(scrolledComposite, SWT.NONE);
		lblIntTextS1.setBounds(86, 116, 198, 24);
		
		
		spinnerIntS2 = new Spinner(scrolledComposite, SWT.BORDER);
		spinnerIntS2.setBounds(200, 114, 70, 24);
		
		lblIntTextS2 = new Label(scrolledComposite, SWT.NONE);
		lblIntTextS2.setBounds(276, 116, 198, 24);
		
		
		
		comboList = new Combo(scrolledComposite, SWT.READ_ONLY);
		comboList.setBounds(10, 114, 128, 23);
		
		this.comboModifyListener = new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				updateString(selected, comboList.getText());
			}
		};
		
		lblList = new Label(scrolledComposite, SWT.NONE);
		lblList.setBounds(150, 116, 357, 24);
		
		textString = new Text(scrolledComposite, SWT.BORDER);
		textString.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				updateString(selected, textString.getText());
			}
		});
		textString.setBounds(20, 112, 383, 28);
		
		btnFileDir = new Button(scrolledComposite, SWT.NONE);
		btnFileDir.addSelectionListener(new SelectionAdapter() {
			@SuppressWarnings("unchecked")
			@Override
			public void widgetSelected(SelectionEvent e) 
			{
				String res;
				
				if (getAttributeVal(selected.getAttributes(),"type").equals("directory")) 
					res = MainWin.getFile(false, true, textString.getText(), "Choose " + selected.getName(), "Set directory for " +  selected.getName());
				else
					res = MainWin.getFile(false, false, textString.getText(), "Choose " + selected.getName(), "Choose file for " + selected.getName());
				
				if (res != null)
					textString.setText(res);
			}
		});
		btnFileDir.setBounds(409, 111, 98, 30);
		btnFileDir.setText("Choose...");
		
		lblString = new Label(scrolledComposite, SWT.NONE);
		lblString.setBounds(20, 81, 290, 33);
		
	}
	
	
	




	protected void doBackup()
	{
		String path = MainWin.getFile(true, false, "", "Save current config as...", "Save", new String[] { ".xml" , "*.*"});
		
		if (path != null)
		{
			commitNodes();
			
			try
			{
				wc.save(path);
			} 
			catch (ConfigurationException e)
			{
				MainWin.showError("Error saving configuration", "A configuration exception occured", e.getMessage(), true);
			}
			
		}
	}


	
	

	protected void doRestore()
	{
		
		String path = MainWin.getFile(false, false, "", "Load config from file...", "Open", new String[] { ".xml" , "*.*"});
		
		if (path != null)
		{
			
			loadConfig(path);
			
		}
	}


	
	

	protected void applyChanges()
	{
		commitNodes();
		XMLConfiguration temp = new XMLConfiguration();
		
		try
		{
			StringWriter sw = new StringWriter();
			
			wc.save(sw);
			
			StringReader sr = new StringReader(sw.getBuffer().toString());
			
			temp.load(sr);
		} 
		catch (ConfigurationException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		@SuppressWarnings("unchecked")
		final ArrayList<String> cmds = generateConfigCommands("", temp.getRootNode().getChildren());
		
		
		/*
		cmds.add(0, "ui server conf freeze true");
		
		for (int i = 0;i<256;i++)
		{
			cmds.add("ui server conf set Drive" + i + "Path");
			cmds.add("ui server conf set Drive" + i + "Path[@category] disk,advanced");
			cmds.add("ui server conf set Drive" + i + "Path[@type] file");
		}
		
		
		cmds.add("ui server conf freeze false");
		
		*/
		
		
		class Stupid
		{
			ConfigEditorTaskWin ctw;
			
			public ConfigEditorTaskWin getCtw()
			{
				return this.ctw;
			}
			
			public void initCtw(Shell shell)
			{
				this.ctw = new ConfigEditorTaskWin(shell, SWT.DIALOG_TRIM, "Writing config to server..." , "Please wait while the configuration is written");
			}
		}
		
		
		
		final Shell shell = this;
		final Stupid stupid = new Stupid();
		
		Runnable lc = new Runnable() 
		{

			
			@Override
			public void run()
			{
				getDisplay().syncExec(new Runnable() {
		
					@Override
					public void run()
					{
						
						stupid.initCtw(shell);
						stupid.getCtw().open();
						
					}
					
				});
				
			
				try
				{
					stupid.getCtw().setStatus("Generate command list...", 10);
					
					stupid.getCtw().setStatus("Sending config commands...", 20);
					
					double slice = 80.0/Double.valueOf(cmds.size()); 
					double i = 0.0;
					for (String cmd : cmds)
					{
						
						Connection conn = new Connection(MainWin.getHost(), MainWin.getPort(), MainWin.getInstance());
						conn.Connect();
						conn.loadList(-1,cmd);
						conn.close();
						
						//MainWin.debug(cmd);
						
						stupid.getCtw().setStatus("Sending config commands...", (int) (20 + (i * slice)));
						i++;
					}		
					
					stupid.getCtw().setStatus("Complete", 100);
					
					stupid.getCtw().closeWin();
				} 
				catch (UnknownHostException e)
				{
					stupid.getCtw().setErrorStatus(e.getMessage());
				} 
					catch (IOException e)
				{
					stupid.getCtw().setErrorStatus(e.getMessage());
				} 
				catch (DWUIOperationFailedException e)
				{
					stupid.getCtw().setErrorStatus(e.getMessage());
				}
				
			}
			
		};
		
		Thread tc = new Thread(lc);
		tc.start();
		
		
	}

	









	@SuppressWarnings("unchecked")
	protected ArrayList<String> generateConfigCommands(String key, List<Node> nodes)
	{
		ArrayList<String> res = new ArrayList<String>();
		
		List<ConfigItem> items = new ArrayList<ConfigItem>();
		
		HashMap<String,Integer> count = new HashMap<String,Integer>();
		
		for (Node t : nodes)
		{
			
			if (count.containsKey(t.getName()))
				count.put(t.getName() , count.get(t.getName()) + 1);
			else
				count.put(t.getName(), 0);
			
			items.add(new ConfigItem(t,count.get(t.getName())));
			
		}
			
		
		Collections.sort(items);
		
		for (ConfigItem item : items)
		{
			String cmd = "ui server conf set ";
			
			String thiskey = item.getNode().getName() + "(" + item.getIndex() + ")";
			
			if (key != "")
				thiskey = key + "." + thiskey;
			
			cmd += thiskey;
			
			if (item.getNode().getValue() != null)
			{
				cmd += " " + item.getNode().getValue().toString();
			}
		
			res.add(cmd);
			
			
			HashMap<String,String> multi = new HashMap<String,String>();
			
			//	what a mess
			for (Node atn : (List<Node>) item.getNode().getAttributes() )
			{
					if (item.getNode().getAttributeCount(atn.getName()) > 1)
					{
						
						if (multi.containsKey(thiskey + "[@" + atn.getName() + "]"))
							 multi.put(thiskey + "[@" + atn.getName() + "]" ,  multi.get(thiskey + "[@" + atn.getName() + "]") +"," + atn.getValue().toString());
						else
							multi.put(thiskey + "[@" + atn.getName() + "]" , atn.getValue().toString());
							
					}
					else
					{
						res.add("ui server conf set " + thiskey + "[@" + atn.getName() + "] " + atn.getValue());
					}
				
			}
			
			for (Entry<String,String> e : multi.entrySet())
			{
				res.add("ui server conf set " + e.getKey() + " " + e.getValue());
			}
			
			
			
			if (item.getNode().hasChildren())
			{
				res.addAll(generateConfigCommands(thiskey, item.getNode().getChildren()));
			
			}
				
		}
		
		return res;
	}





	private String getTreePath(TreeItem ti)
	{
		String res = ((Node)ti.getData("param")).getName();
		
		res += "(" + ((Integer) ti.getData("index")) + ")";
		
		if (ti.getParentItem() != null)
			res = getTreePath(ti.getParentItem()) + "." + res;
		
		return res;
	}









	protected void updateBoolean(Node node, boolean selection)
	{
		node.setValue(selection);
		tree.getSelection()[0].setText(1,selection+"");
	}


	protected void updateInt(Node node, int selection)
	{
		node.setValue(selection);
		tree.getSelection()[0].setText(1,selection+"");
	}

	
	protected void updateString(Node node, String selection)
	{
		node.setValue(selection);
		
		if (selection == null)
			tree.getSelection()[0].setText(1,"");
		else
			tree.getSelection()[0].setText(1,selection);
		
	}




	


	private String getKeyPath(Node node)
	{
		String res = node.getName();
		
		
		if (node.getParent() != null)
		{
			res = getKeyPath(node.getParent()) + "." + res;
		}
		
		
		return res;
	}


	
	


	@SuppressWarnings("unchecked")
	protected void displayParam(TreeItem ti)
	{
		Node node = (Node) ti.getData("param");
		
		String type = null;
		
		if (this.getAttributeVal(node.getAttributes(), "type") != null)
		{
			type = this.getAttributeVal(node.getAttributes(), "type").toString();
		}
		
		setDisplayFor(ti, type);
			
		
		
	}

	
	
	
	
	
	
	@SuppressWarnings("unchecked")
	private void setDisplayFor(TreeItem ti, String type)
	{
		
		this.setRedraw(false);
		// all off
		this.lblItemTitle.setVisible(false);
		this.btnToggle.setVisible(false);
		this.textDescription.setVisible(false);
		this.spinnerInt.setVisible(false);
		this.lblIntText.setVisible(false);
		this.comboList.setVisible(false);
		this.lblList.setVisible(false);
		this.lblString.setVisible(false);
		this.btnFileDir.setVisible(false);
		this.textString.setVisible(false);
		
		this.lblIntTextS1.setVisible(false);
		this.spinnerIntS2.setVisible(false);
		this.lblIntTextS2.setVisible(false);
		this.textDescriptS1.setVisible(false);
		this.lblDescriptS1.setVisible(false);
		this.textNameS1.setVisible(false);
		this.lblNameS1.setVisible(false);
		
		
		if (ti == null)
		{
			this.setRedraw(true);
			return;
		}
		
		Node node = (Node) ti.getData("param");
		
		if (type == null)
		{
			if (node.getName().equals("midisynthprofile"))
			{
				this.lblItemTitle.setVisible(true);
				this.lblItemTitle.setText(node.getName());
				
				this.spinnerInt.setVisible(true);
				this.lblIntTextS1.setVisible(true);
				this.lblIntTextS1.moveAbove(null);
				this.spinnerIntS2.setVisible(true);
				this.spinnerIntS2.moveAbove(null);
				this.lblIntTextS2.setVisible(true);
				this.lblIntTextS2.moveAbove(null);
				this.textDescriptS1.setVisible(true);
				this.textDescriptS1.moveAbove(null);
				this.lblDescriptS1.setVisible(true);
				this.lblDescriptS1.moveAbove(null);
				this.textNameS1.setVisible(true);
				this.textNameS1.moveAbove(null);
				this.lblNameS1.setVisible(true);
				this.lblNameS1.moveAbove(null);
				
				
				this.lblIntTextS1.setText("Device offset");
				this.lblIntTextS2.setText("GM offset");
				this.lblDescriptS1.setText("Description:");
				this.lblNameS1.setText("Name:");
				
			}
			else
			{
				
				
				//System.out.println("n: " + node.getName());
				//System.out.println("v: " + node.getValue());
			}
			

		}
		else if (type.equals("boolean"))
		{
			this.lblItemTitle.setVisible(true);
			this.lblItemTitle.setText(node.getName());
			this.btnToggle.setVisible(true);
			
			if (ti.getText(2).equals(""))
				this.btnToggle.setSelection(Boolean.parseBoolean(node.getValue().toString()));
			else
				this.btnToggle.setSelection(Boolean.parseBoolean(ti.getText(2)));
			
			this.textDescription.setVisible(true);
			this.btnToggle.setText("Enable " + node.getName());
			showHelpFor(node);
			
		} 
		else if (type.equals("int"))
		{
			this.lblItemTitle.setVisible(true);
			this.lblItemTitle.setText(node.getName());
			
			this.textDescription.setVisible(true);
			showHelpFor(node);
			
			this.spinnerInt.removeModifyListener(this.spinnerModifyListener);
			
			
			if (this.hasAttribute(node.getAttributes(), "min"))
				this.spinnerInt.setMinimum(Integer.parseInt(this.getAttributeVal(node.getAttributes(), "min").toString()));
			
			if (this.hasAttribute(node.getAttributes(), "max"))
				this.spinnerInt.setMaximum(Integer.parseInt(this.getAttributeVal(node.getAttributes(), "max").toString()));
			
			
			if (ti.getText(2).equals(""))
			{
				if (node.getValue() != null)
					this.spinnerInt.setSelection(Integer.parseInt(node.getValue().toString()));
			}
			else
				this.spinnerInt.setSelection(Integer.parseInt(ti.getText(2)));
			
			this.spinnerInt.addModifyListener(this.spinnerModifyListener);
			
			this.spinnerInt.setVisible(true);
			this.lblIntText.setVisible(true);
			this.lblIntText.setText("Choose value for " + node.getName());
			
			
		}
		else if (type.equals("list"))
		{
			this.lblItemTitle.setVisible(true);
			this.lblItemTitle.setText(node.getName());
			this.comboList.setVisible(true);
			
			this.comboList.removeModifyListener(this.comboModifyListener);
			
			this.comboList.removeAll();
			
			if (this.hasAttribute(node.getAttributes(), "list"))
			{
                            for (Iterator it = this.getAttributeVals(node.getAttributes(), "list").iterator(); it.hasNext();) {
                                String s = (String) it.next();
                                this.comboList.add(s);
                            }
				
			}
			
			if (ti.getText(2).equals(""))
			{
				if (node.getValue() != null)
					this.comboList.select(this.comboList.indexOf(node.getValue().toString()));
			}
			else
				this.comboList.select(this.comboList.indexOf(ti.getText(2)));
			
			this.comboList.addModifyListener(this.comboModifyListener);
			
			this.lblList.setVisible(true);
			this.lblList.setText("Select value for " + node.getName());
			
			this.textDescription.setVisible(true);
			showHelpFor(node);
		}
		else if (type.equals("section"))
		{
			this.lblItemTitle.setVisible(true);
			this.lblItemTitle.setText(node.getName());
			this.comboList.setVisible(true);
			
			this.comboList.removeModifyListener(this.comboModifyListener);
			
			this.comboList.removeAll();
			
			if (this.hasAttribute(node.getAttributes(), "section"))
			{
				String key = this.getAttributeVal(node.getAttributes(), "section").toString();
				
				for (int i = 0;i <= MainWin.dwconfig.getMaxIndex(key);i++)
				{
					if (MainWin.dwconfig.containsKey(key + "(" + i + ")[@name]"))
						this.comboList.add(MainWin.dwconfig.getString(key + "(" + i + ")[@name]"));
				}
				
				
			}
			
			
			if (ti.getText(2).equals(""))
				this.comboList.select(this.comboList.indexOf(node.getValue().toString()));
			else
				this.comboList.select(this.comboList.indexOf(ti.getText(2)));
			
			
			this.comboList.addModifyListener(this.comboModifyListener);
			
			this.lblList.setVisible(true);
			this.lblList.setText("Select value for " + node.getName());
			
			this.textDescription.setVisible(true);
			showHelpFor(node);
		}
		else if (type.equals("string"))
		{
			this.lblItemTitle.setVisible(true);
			this.lblItemTitle.setText(node.getName());
			
			this.textDescription.setVisible(true);
			showHelpFor(node);
			
			if (ti.getText(2).equals(""))
				if (node.getValue() != null)
					this.textString.setText(node.getValue().toString());
				else
					this.textString.setText("");
			else
				this.textString.setText(ti.getText(2));
			
			this.textString.setVisible(true);
			this.lblString.setVisible(true);
			this.lblString.setText("Enter value for " + node.getName());
			
		}
		else if (type.equals("file") || type.equals("directory")) 
		{
			this.lblItemTitle.setVisible(true);
			this.lblItemTitle.setText(node.getName());
			
			this.textDescription.setVisible(true);
			showHelpFor(node);
			

			if (ti.getText(2).equals(""))
				if (node.getValue() != null)
					this.textString.setText(node.getValue().toString());
				else
					this.textString.setText("");
			else
				this.textString.setText(ti.getText(2));
			
			this.textString.setVisible(true);
			this.btnFileDir.setVisible(true);
		}
		else if (type.equals("serialdev"))
		{
			this.lblItemTitle.setVisible(true);
			this.lblItemTitle.setText(node.getName());
			this.comboList.setVisible(true);
			
			this.comboList.removeModifyListener(this.comboModifyListener);
			
			this.comboList.removeAll();
			
			try
			{
				List<String> p = UIUtils.loadList("ui server show serialdevs");
				
				for (String i : p)
				{
					this.comboList.add(i);
				}
				
				if (ti.getText(2).equals(""))
				{
					if (node.getValue() != null)
						this.comboList.select(this.comboList.indexOf(node.getValue().toString()));
				}
				else
					this.comboList.select(this.comboList.indexOf(ti.getText(2)));
				
			} 
			catch (IOException e)
			{
			} catch (DWUIOperationFailedException e)
			{
			}
			
			
			this.comboList.addModifyListener(this.comboModifyListener);
			
			this.lblList.setVisible(true);
			this.lblList.setText("Select serial port");
			
			this.textDescription.setVisible(true);
			
			showHelpFor(node);
		}
				
		
		this.setRedraw(true);
		
	}









	private void showHelpFor(final Node node)
	{
		textDescription.setText("Loading help for " + node.getName() + "...");
		
			
		Thread t = new Thread(
				  new Runnable() {
					  public void run()
					  {
						  String txt = "";
						  
						  try
							{
							  
								List<String> help = UIUtils.loadList(MainWin.getInstance(),"ui server show help " + getKeyPath(node));
								
								
								for (String t:help)
								{
									txt += t;
								}
								
								
							}
							catch (IOException e)
							{
								txt = e.getMessage();
							} 
							catch (DWUIOperationFailedException e)
							{
								txt += "No help found for " + node.getName(); 
							}
							
							final String ftxt = txt;
							final String fname = node.getName();
							
							if (!isDisposed())
							getDisplay().asyncExec( new Runnable() {
								public void run()
								{
									if (!textDescription.isDisposed() && (!lblItemTitle.isDisposed()) && lblItemTitle.getText().equals(fname))
										textDescription.setText(ftxt);
								}});
					  }
				  });
		
		t.start();
	}	










	
	private void loadConfig(TreeItem ti, List<Node> nodes)
	{
		tree.removeAll();
		setDisplayFor(null,"none");
		this.setRedraw(false);
		loadAllConfig(ti,nodes);
		commitNodes();
		filterConfig(null);
		this.setRedraw(true);
		
		if (this.selected != null)
			tryToSelect(selected);
		
	}
	
	private void tryToSelect(Node node)
	{
		
		for (TreeItem t:tree.getItems())
		{
			if (t.getData("param").equals(node))
			{
				tree.select(t);
				this.displayParam(t);
				return;
			}
		}
		
		// our item isn't in the list..
		
		setDisplayFor(null,"none");
	}









	private void filterConfig(TreeItem ti)
	{
		TreeItem[] items;
		
		if (ti == null)
			items = tree.getItems();
		else
			items = ti.getItems();
		
		for (TreeItem item : items)
		{
			if (item.getItemCount() > 0)
			{
				filterConfig(item);
			}
			
			if (item.getItemCount() == 0)
			{
				if (!filterItem((Node) item.getData("param")))
				{
					item.dispose();
				}
			}
		}
		
	}
	
	private void commitNodes()
	{
		commitNodes(tree.getItems());
	}
	
	@SuppressWarnings("unchecked")
	private void commitNodes(TreeItem[] treeitems)
	{
		for (TreeItem ti : treeitems)
		{
			if (ti.getItemCount() > 0)
			{
				commitNodes(ti.getItems());
			}
			
			Node node = (Node) ti.getData("param");
			
			String key = getTreePath(ti);
			
			wc.setProperty(key, node.getValue());
			
			HashMap<String,String> multi = new HashMap<String,String>();
			
			//	what a mess
			for (Node atn : (List<Node>) node.getAttributes() )
			{
					if (node.getAttributeCount(atn.getName()) > 1)
					{
						
						if (multi.containsKey(key + "[@" + atn.getName() + "]"))
							 multi.put(key + "[@" + atn.getName() + "]" ,  multi.get(key + "[@" + atn.getName() + "]") +"," + atn.getValue().toString());
						else
							multi.put(key + "[@" + atn.getName() + "]" , atn.getValue().toString());
							
					}
					else
					{
						wc.setProperty(key + "[@" + atn.getName() + "]",  atn.getValue());
					}
				
			}
			
			for (Entry<String,String> e : multi.entrySet())
			{
				wc.setProperty(e.getKey(), e.getValue());
			}
			
			
			
		}
	}
	
	
	
	
	@SuppressWarnings("unchecked")
	private void loadAllConfig(TreeItem ti, List<Node> nodes)
	{
	
		List<ConfigItem> items = new ArrayList<ConfigItem>();
		
		HashMap<String,Integer> count = new HashMap<String,Integer>();
		
		for (Node t : nodes)
		{
			
			if (count.containsKey(t.getName()))
				count.put(t.getName() , count.get(t.getName()) + 1);
			else
				count.put(t.getName(), 0);
			
			items.add(new ConfigItem(t,count.get(t.getName())));
			
		}
			
		
		Collections.sort(items);
		
		for (ConfigItem item : items)
		{
			TreeItem tmp;
			if (ti == null)
			{
				tmp = new TreeItem(tree,SWT.NONE);
			}
			else
			{
				tmp = new TreeItem(ti, SWT.NONE);
			}
			
			// apply master
			applyMaster(item.getNode());
			
			if (getAttributeVal(item.getNode().getAttributes(),"name") == null)
				tmp.setText(0,item.getNode().getName());
			else
				tmp.setText(0, item.getNode().getName() + ": " + getAttributeVal(item.getNode().getAttributes(),"name").toString());
			
			tmp.setData("param", item.getNode());
			tmp.setData("index", item.getIndex());
			
			if (item.getNode().getValue() == null)
			{
				if (getAttributeVal(item.getNode().getAttributes(),"desc") != null)
					tmp.setText(1, getAttributeVal(item.getNode().getAttributes(),"desc").toString());
				else if (hasAttribute(item.getNode().getAttributes(),"dev") && hasAttribute(item.getNode().getAttributes(),"gm"))
				{
					tmp.setText(1, "In (native): " + getAttributeVal(item.getNode().getAttributes(),"dev").toString() + " Out (GM): " + getAttributeVal(item.getNode().getAttributes(),"gm").toString());
				}
			}
			else
			{
				tmp.setText(1,item.getNode().getValue().toString());
			}
		
		//	if (changes.containsKey(item.getNode()))
		//		tmp.setText(2, changes.get(item.getNode()).toString());
			
			if (item.getNode().hasChildren())
			{
				loadAllConfig(tmp, item.getNode().getChildren());
				
				if (item.getNode().getName().equals("instance"))
					tmp.setExpanded(true);
			}
				
		}
		
	}
	
	
	
	
	
	@SuppressWarnings("unchecked")
	private boolean filterItem(Node t)
	{
		List<Node> attributes = t.getAttributes();
		
		// advanced/unknown
		//if (!this.tltmCheckAdvanced.getSelection() && !hasAttribute(attributes,"category"))
		//	return(false);
		
		if (!this.tltmCheckAdvanced.getSelection() && matchAttributeVal(attributes,"category","advanced"))
			return(false);
		
		// category
		if (this.tltmAll.getSelection())
			return(true);
		//else if (!hasAttribute(attributes,"category"))
		//	return(false);
		
		if (this.tltmMidi.getSelection() && !matchCategory(t,"midi"))
			return false;
		
		if (this.tltmDevice.getSelection() && !matchCategory(t,"device"))
			return false;
		
		if (this.tltmPrinting.getSelection() && !matchCategory(t,"printing"))
			return false;
		
		if (this.tltmLogging.getSelection() && !matchCategory(t,"logging"))
			return false;
		
		if (this.tltmDisk.getSelection() && !matchCategory(t,"disk"))
			return false;
		
		if (this.tltmNetworking.getSelection() && !matchCategory(t,"networking"))
			return false;
		
		//if (this.tltmNetworking.getSelection() && !matchAttributeVal(attributes,"category","networking"))
		//	return false;
		
		
		
		return true;
	}





	@SuppressWarnings("unchecked")
	private boolean matchCategory(Node node, String category)
	{
	
		
		if (matchAttributeVal(node.getAttributes(), "category", category))
			return true;
		
		if (node.getParent() != null)
		{
			return(matchCategory(node.getParent(), category));
		}
				
		return false;
	}





	private boolean matchAttributeVal(List<Node> attributes, String key, String val)
	{
		for (Node n : attributes)
		{
			if (n.getName().equals(key) && n.getValue().equals(val))
				return(true);
		}
		
		return false;
	}





	private Object getAttributeVal(List<Node> attributes, String key)
	{
		for (Node n : attributes)
		{
			if (n.getName().equals(key))
			{
				return(n.getValue());
			}
		}
		
		return null;
	}

	private List<String> getAttributeVals(List<Node> attributes, String key)
	{
		List<String> res = new ArrayList<String>();
		
		for (Node n : attributes)
		{
			if (n.getName().equals(key))
			{
				res.add(n.getValue().toString());
			}
		}
		
		return res;
	}



	private boolean hasAttribute(List<Node> attributes, String key)
	{
		for (Node n : attributes)
		{
			if (n.getName().equals(key))
				return(true);
		}
		
		return false;
	}




	private void loadConfig()
	{
		class Stupid
		{
			ConfigEditorTaskWin ctw;
			
			public ConfigEditorTaskWin getCtw()
			{
				return this.ctw;
			}
			
			public void initCtw(Shell shell)
			{
				this.ctw = new ConfigEditorTaskWin(shell, SWT.DIALOG_TRIM, "Loading config from server..." , "Please wait while the server's configuration is loaded");
			}
		}
		
		
		
		final Shell shell = this;
		final Stupid stupid = new Stupid();
		
		Runnable lc = new Runnable() 
		{

			@SuppressWarnings("unchecked")
			@Override
			public void run()
			{
				getDisplay().syncExec(new Runnable() {
		
					@Override
					public void run()
					{
						
						stupid.initCtw(shell);
						stupid.getCtw().open();
						
					}
					
				});
				
			
				try
				{
					Connection conn = new Connection(MainWin.getHost(), MainWin.getPort(), MainWin.getInstance());
					
					stupid.getCtw().setStatus("Connecting to server...", 10);
					conn.Connect();
					
					stupid.getCtw().setStatus("Sending command...", 20);
					StringReader sr = conn.loadReader(-1,"ui server config write");
					
					stupid.getCtw().setStatus("Received response", 30);
					conn.close();
		
					stupid.getCtw().setStatus("Processing config...", 60);
					wc.clear();
					wc.load(sr);
					
					
					stupid.getCtw().setStatus("Processing config...", 85);
					
					getDisplay().syncExec(new Runnable() {

						@Override
						public void run()
						{
							loadConfig(null, (List<Node>) wc.getRoot().getChildren());
						}
						
					});
					
					
					stupid.getCtw().setStatus("Complete", 100);
					
					stupid.getCtw().closeWin();
				} 
				catch (UnknownHostException e)
				{
					stupid.getCtw().setErrorStatus(e.getMessage());
				} 
				catch (ConfigurationException e)
				{
					stupid.getCtw().setErrorStatus(e.getMessage());
				} 
				catch (IOException e)
				{
					stupid.getCtw().setErrorStatus(e.getMessage());
				} 
				catch (DWUIOperationFailedException e)
				{
					stupid.getCtw().setErrorStatus(e.getMessage());
				}
			}
			
		};
		
		if (!this.isDisposed())
		{
			Thread tc = new Thread(lc);
			tc.start();
		}
		
	}


	
	private void loadConfig(final String filepath)
	{
		class Stupid
		{
			ConfigEditorTaskWin ctw;
			
			public ConfigEditorTaskWin getCtw()
			{
				return this.ctw;
			}
			
			public void initCtw(Shell shell)
			{
				this.ctw = new ConfigEditorTaskWin(shell, SWT.DIALOG_TRIM, "Loading config from file..." , "Please wait while the configuration is loaded");
			}
		}
		
		
		
		final Shell shell = this;
		final Stupid stupid = new Stupid();
		
		Runnable lc = new Runnable() 
		{

			@SuppressWarnings("unchecked")
			@Override
			public void run()
			{
				getDisplay().syncExec(new Runnable() {
		
					@Override
					public void run()
					{
						
						stupid.initCtw(shell);
						stupid.getCtw().open();
						
					}
					
				});
				
			
				try
				{
					
					stupid.getCtw().setStatus("Reading config...", 30);
					wc.clear();
					wc.load(filepath);
					
					stupid.getCtw().setStatus("Processing config...", 60);
					
					getDisplay().syncExec(new Runnable() {

						@Override
						public void run()
						{
							loadConfig(null, (List<Node>) wc.getRoot().getChildren());
						}
						
					});
					
					
					stupid.getCtw().setStatus("Complete", 100);
					
					stupid.getCtw().closeWin();
				} 
				catch (Exception e)
				{
					stupid.getCtw().setErrorStatus(e.getMessage());
				} 
				
			}
			
		};
		
		Thread tc = new Thread(lc);
		tc.start();
		
	}
	
	
	private void loadMaster()
	{
		masterc.clear();
		try
		{
			masterc.load("master.xml");
		} 
		catch (ConfigurationException e)
		{
			MainWin.showError("Error loading master config", e.getClass().getSimpleName() + ": " + e.getMessage(), UIUtils.getStackTrace(e), false);
			this.dispose();
		}
	}

	protected void applyMaster(Node node)
	{
		
		String key = getKeyPath(node);
		if (key.startsWith("drivewire-config."))
		{
			key = key.substring(17);
		}
		
		if (masterc.getMaxIndex(key) > -1)
		{
			Node mnode = masterc.configurationAt(key).getRoot();
			
			//node.removeAttributes();
			
			for (int i = 0;i<mnode.getAttributeCount();i++)
			{
				if (node.getAttributeCount(mnode.getAttribute(i).getName()) > 0)
					node.removeAttribute(mnode.getAttribute(i).getName());
				
			}
			
			for (int i = 0;i<mnode.getAttributeCount();i++)
			{
				node.addAttribute((ConfigurationNode) mnode.getAttribute(i).clone());
			}
			
			
		}
		else
		{
			//MainWin.debug("No master config for: " + key);
			
		}
	}

	
}
