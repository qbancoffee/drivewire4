package com.groupunix.drivewireui.nineserver;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class WindowView extends Dialog
{

	protected Object result;
	protected Shell shell;
	private Composite body;
	private Composite compositeContent;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public WindowView(Shell parent, int style)
	{
		super(parent, style);
		setText("SWT Dialog");
		
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open()
	{
		createContents();
		shell.open();
		shell.layout();
		Display display = getParent().getDisplay();
		while (!shell.isDisposed())
		{
			if (!display.readAndDispatch())
			{
				display.sleep();
			}
		}
		return result;
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents()
	{
		shell = new Shell(getParent(), getStyle());
		shell.setSize(450, 300);
		shell.setText(getText());
		shell.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		compositeContent = new Composite(shell, SWT.NONE);

		
	}

	public Composite getContent()
	{
		// TODO Auto-generated method stub
		return null;
	}
	public Composite getCompositeContent() {
		return compositeContent;
	}

	public Composite getBody() {
		return body;
	}

	public void setBody(Composite body) {
		this.body = body;
	}
}
