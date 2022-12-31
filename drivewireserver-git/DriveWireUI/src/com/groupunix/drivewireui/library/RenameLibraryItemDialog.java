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

public class RenameLibraryItemDialog extends Dialog {

	protected Object result;
	protected static Shell shlAddPath;
	private Text textTitle;
	private Node node;
	private LibraryItem item;
	private Tree tree;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 * @param tree 
	 */
	public RenameLibraryItemDialog(Shell parent, int style, LibraryItem libitem, Tree tree)
	{
		super(parent, style);
		this.item = libitem;
		this.node = libitem.getNode();
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
		shlAddPath.setSize(377, 151);
		shlAddPath.setText("Rename Item...");
		shlAddPath.setLayout(null);
		
		
		textTitle = new Text(shlAddPath, SWT.BORDER);
		textTitle.setBounds(22, 40, 329, 21);
		
		textTitle.setText(item.getTitle());
		
		Label lblTitleForNew = new Label(shlAddPath, SWT.NONE);
		lblTitleForNew.setBounds(22, 18, 184, 15);
		lblTitleForNew.setText("Name for this item:");
			
		Button btnCreate = new Button(shlAddPath, SWT.NONE);
		btnCreate.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) 
			{
				if ((textTitle.getText() == null) || (textTitle.getText().equals("")))
				{
					
				}
				else
				{
					for (int i = 0;i<node.getAttributeCount();i++)
					{
						if (node.getAttribute(i).getName().equals("title"))
							node.getAttribute(i).setValue(textTitle.getText());
					}
					
					tree.clearAll(true);
					//treeItem.setItemCount(node.getChildrenCount());
					
					e.display.getActiveShell().close();
					
				}
				
			}
		});
		btnCreate.setBounds(132, 84, 100, 30);
		btnCreate.setText("Set Name");
		
		Button btnCancel = new Button(shlAddPath, SWT.NONE);
		btnCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) 
			{
				e.display.getActiveShell().close();
			}
		});
		btnCancel.setBounds(275, 84, 75, 30);
		btnCancel.setText("Cancel");

	}

	

	protected Text getTextTitle() {
		return textTitle;
	}
	
}
