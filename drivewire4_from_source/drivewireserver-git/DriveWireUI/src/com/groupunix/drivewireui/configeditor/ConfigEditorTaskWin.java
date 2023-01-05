package com.groupunix.drivewireui.configeditor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;

public class ConfigEditorTaskWin extends Dialog
{

	protected Object result;
	protected Shell shell;
	private Label lblMessage;
	private ProgressBar progressBar;
	private Label labelStatus;


	private String title;
	private Button btnOk;
	private String message;
	

	public ConfigEditorTaskWin(Shell parent, int style, String title, String message)
	{
		super(parent, style);
		this.title = title;
		this.message = message;
		
	}


	public Object open()
	{
		createContents();
		this.btnOk.setVisible(false);
		
		Rectangle pos = Display.getCurrent().getActiveShell().getBounds();
		
		shell.setBounds(pos.x + (pos.width/2) - 204, pos.y + (pos.height/2) - 76, 408, 152);
		
		shell.open();
		shell.layout();
		//Display display = getParent().getDisplay();
		
		/*
		while (!shell.isDisposed())
		{
			if (!display.readAndDispatch())
			{
				display.sleep();
			}
		}
		*/
		return result;
	}

	
	public void setErrorStatus(final String err)
	{
		shell.getDisplay().asyncExec(
				  new Runnable() {
					  public void run()
					  {
						  labelStatus.setVisible(false);
						  progressBar.setVisible(false);
						  lblMessage.setText(err);
						  shell.setText("Error while sending commands");
						  btnOk.setVisible(true);
						  shell.redraw();
					  }
				  });
	}

	
	public void setStatus(final String msg, final int progress)
	{
		shell.getDisplay().syncExec(
				  new Runnable() {
					  public void run()
					  {
						  progressBar.setSelection(progressBar.getSelection() + progress);
						  if (msg != null)
							  labelStatus.setText(msg);
		
						  shell.redraw();
					  }
				  });
		
		
	}

	
	

	private void createContents()
	{
		shell = new Shell(getParent(), getStyle());
		shell.setSize(408, 165);
		shell.setText(this.title);
		
		btnOk = new Button(shell, SWT.NONE);
		btnOk.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shell.close();
			}
		});
		
		btnOk.setBounds(298, 89, 75, 25);
		btnOk.setText("Ok");
		
		progressBar = new ProgressBar(shell, SWT.NONE);
		progressBar.setBounds(23, 66, 350, 17);
		
		lblMessage = new Label(shell, SWT.WRAP);
		lblMessage.setBounds(23, 23, 343, 40);
		
		lblMessage.setText(this.message);
		
		labelStatus = new Label(shell, SWT.NONE);
		labelStatus.setBounds(23, 92, 350, 22);
		
	}

	
	


	public void closeWin()
	{
		shell.getDisplay().asyncExec(
				  new Runnable() {
					  public void run()
					  {
						  shell.dispose();
					  }
				  });
	}
}
