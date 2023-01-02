package com.groupunix.drivewireui;

import java.util.List;

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

public class SendCommandWin extends Dialog
{

	protected Object result;
	protected Shell shlSendingCommandTo;
	private Label lblMessage;
	private ProgressBar progressBar;
	private Label labelStatus;

	private List<String> commands;
	private String title;
	private String message;
	private Button btnOk;
	private Shell parshell;
	
	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public SendCommandWin(Shell parent, int style, List<String> commands, String title, String message)
	{
		super(parent, style);
		this.parshell = parent;
		this.commands = commands;
		this.title = title;
		this.message = message;
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open()
	{
		  
		createContents();
		
		this.btnOk.setVisible(false);
		
		Rectangle pos = this.parshell.getBounds();
		
		shlSendingCommandTo.setBounds(pos.x + (pos.width/2) - 204, pos.y + (pos.height/2) - 76, 408, 152);
		
		shlSendingCommandTo.open();
		shlSendingCommandTo.layout();
		Display display = getParent().getDisplay();
						
		Thread cmdT = new Thread(new cmdThread(this.commands));
		cmdT.start();
				
		while (!shlSendingCommandTo.isDisposed())
		{
			if (!display.readAndDispatch())
			{
				display.sleep();
			}
		}
		
		return result;
	}

	private void setErrorStatus(final String err)
	{
		
		shlSendingCommandTo.getDisplay().asyncExec(
				  new Runnable() {
					  public void run()
					  {
						  
						  labelStatus.setVisible(false);
						  progressBar.setVisible(false);
						  lblMessage.setText(err);
						  shlSendingCommandTo.setText("Error while sending commands");
						  btnOk.setVisible(true);
						  
					  }
				  });
	}

	private void setStatus(final String msg, final int progress)
	{
		  
		shlSendingCommandTo.getDisplay().asyncExec(
				  new Runnable() {
					  public void run()
					  {
						  
						  progressBar.setSelection(progressBar.getSelection() + progress);
						  labelStatus.setText(msg);
		
						  shlSendingCommandTo.redraw();
						  
						  
					  }
				  });
	}

	
	
	/**
	 * Create contents of the dialog.
	 */
	private void createContents()
	{
		shlSendingCommandTo = new Shell(getParent(), getStyle());
		shlSendingCommandTo.setSize(408, 165);
		shlSendingCommandTo.setText(this.title);
		
		btnOk = new Button(shlSendingCommandTo, SWT.NONE);
		btnOk.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shlSendingCommandTo.close();
			}
		});
		btnOk.setBounds(298, 89, 75, 25);
		btnOk.setText("Ok");
		
		progressBar = new ProgressBar(shlSendingCommandTo, SWT.NONE);
		progressBar.setBounds(23, 66, 350, 17);
		
		lblMessage = new Label(shlSendingCommandTo, SWT.WRAP);
		lblMessage.setBounds(23, 23, 343, 40);
		lblMessage.setText(this.message);
		
		labelStatus = new Label(shlSendingCommandTo, SWT.NONE);
		labelStatus.setBounds(23, 92, 350, 22);
		
	}

	protected Label getLblMessage() {
		return lblMessage;
	}
	protected ProgressBar getProgressBar() {
		return progressBar;
	}
	protected Label getLabelStatus() {
		return labelStatus;
	}
	protected Button getBtnOk() {
		return btnOk;
	}
	
	
	class cmdThread implements Runnable 
	{
		
		
		private List<String> cmds;

		public cmdThread(List<String> cmds)
		{
			this.cmds = cmds;
		}

		public void run()
		{
			Connection connection = new Connection(MainWin.getHost(), MainWin.getPort(), MainWin.getInstance());
			
			
			try
			{
				
				for (String command: this.cmds)
				{
					setStatus("Connecting to server..", 10 / this.cmds.size());
					connection.Connect();
			  
					setStatus("Send: " + command, 50 / this.cmds.size());
					
					
						
					connection.sendCommand(command,MainWin.getInstance());
				  	
					setStatus("Closing..", 40 / this.cmds.size());
					
					
					connection.close();
					
					
				}
				setStatus("Finished.", 100);
				
				closeWin();
			} 
			catch (Exception e)
			{
				
				setErrorStatus(e.getMessage());
			}
		}
		
	}


	public void closeWin()
	{
		shlSendingCommandTo.getDisplay().asyncExec(
				  new Runnable() {
					  public void run()
					  {
							
						  shlSendingCommandTo.dispose();
					  }
				  });
	}
}
