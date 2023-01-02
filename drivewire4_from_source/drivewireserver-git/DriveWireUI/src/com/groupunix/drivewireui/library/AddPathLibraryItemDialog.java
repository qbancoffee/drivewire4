package com.groupunix.drivewireui.library;

import org.apache.commons.configuration.HierarchicalConfiguration.Node;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;

import com.groupunix.drivewireui.MainWin;

public class AddPathLibraryItemDialog extends Dialog {

	protected Object result;
	protected static Shell shlAddPath;
	private Text textPath;
	private Button btnFile;
	private Text textTitle;
	private Label lblPath;
	private Node node;
	private Button btnDir;
	private Button btnDsk;
	@SuppressWarnings("unused")
	private FolderLibraryItem folderItem;
	private Tree tree;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 * @param tree 
	 */
	public AddPathLibraryItemDialog(Shell parent, int style, FolderLibraryItem fi, Tree tree)
	{
		super(parent, style);
		this.folderItem = fi;
		this.node = fi.getNode();
		this.tree = tree;
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open() {
		createContents();
		
		shlAddPath.open();
		shlAddPath.layout();
		
		Display display = getParent().getDisplay();
		
		int x = getParent().getBounds().x + (getParent().getBounds().width / 2) - (shlAddPath.getBounds().width / 2);
		int y = getParent().getBounds().y + (getParent().getBounds().height / 2) - (shlAddPath.getBounds().height / 2);
		
		shlAddPath.setLocation(x, y);
		
		
		
		
		
		while (!shlAddPath.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return result;
	}

	
	
	
	private void createContents() 
	{
		
		shlAddPath = new Shell(getParent(), getStyle());
		shlAddPath.setSize(377, 273);
		shlAddPath.setText("Add local item...");
		shlAddPath.setLayout(null);
		
		
		textTitle = new Text(shlAddPath, SWT.BORDER);
		textTitle.setBounds(22, 40, 329, 21);
		
		Label lblTitleForNew = new Label(shlAddPath, SWT.NONE);
		lblTitleForNew.setBounds(22, 18, 184, 15);
		lblTitleForNew.setText("Name for this new item:");
		
		btnDir = new Button(shlAddPath, SWT.RADIO);
		btnDir.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (btnDir.getSelection())
				{
					btnDsk.setSelection(false);
				}
			}
		});
		btnDir.setSelection(true);
		btnDir.setBounds(22, 78, 168, 16);
		btnDir.setText("Add a local directory");
		
		btnDsk = new Button(shlAddPath, SWT.RADIO);
		btnDsk.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (btnDsk.getSelection())
				{
					btnDir.setSelection(false);
				}
			}
		});
		btnDsk.setBounds(22, 101, 146, 16);
		btnDsk.setText("Add a local disk image");
		btnDsk.setSelection(false);

		
		lblPath = new Label(shlAddPath, SWT.NONE);
		lblPath.setBounds(22, 135, 277, 19);
		lblPath.setText("Path to this item:");
		
		textPath = new Text(shlAddPath, SWT.BORDER);
		textPath.setBounds(22, 157, 304, 21);
		
		if (MainWin.getInstanceConfig().containsKey("LocalDiskDir"))
			textPath.setText(MainWin.getInstanceConfig().getString("LocalDiskDir") + System.getProperty("file.separator"));
		
		btnFile = new Button(shlAddPath, SWT.NONE);
		btnFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) 
			{
				final String filename = MainWin.getFile(false, btnDir.getSelection() , textPath.getText(), "Select a local item..", "Select");

				if (filename != null)
					textPath.setText(filename);
					
			
			}
		});
		btnFile.setBounds(332, 156, 29, 23);
		btnFile.setText("...");
		
		Button btnCreate = new Button(shlAddPath, SWT.NONE);
		btnCreate.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) 
			{
				if ((textPath.getText() == null) || (textPath.getText().equals("")))
				{
					
				}
				else if ((textTitle.getText() == null) || (textTitle.getText().equals("")))
				{
					
				}
				else
				{
					// add node
					Node n = new Node();
					n.setName("Path");
					n.setValue(textPath.getText());
					Node a = new Node();
					a.setName("title");
					a.setValue(textTitle.getText());
					a.setAttribute(true);
					n.addAttribute(a);
					node.addChild(n);
					
					tree.clearAll(true);
					//treeItem.setItemCount(node.getChildrenCount());
					
					e.display.getActiveShell().close();
					
				}
				
			}
		});
		btnCreate.setBounds(133, 205, 100, 30);
		btnCreate.setText("Add Item");
		
		Button btnCancel = new Button(shlAddPath, SWT.NONE);
		btnCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) 
			{
				e.display.getActiveShell().close();
			}
		});
		btnCancel.setBounds(286, 205, 75, 30);
		btnCancel.setText("Cancel");

	}

	
	protected Button getBtnFile() {
		return btnFile;
	}
	protected Label getLblPath() {
		return lblPath;
	}
	protected Text getTextTitle() {
		return textTitle;
	}
	protected Text getTextPath() {
		return textPath;
	}
}
