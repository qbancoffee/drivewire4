package com.groupunix.drivewireui;

import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;

public class JavaInfoWin extends Dialog {

	protected Object result;
	protected Shell shell;
	private Text textInfo;
	Clipboard cb;
	
	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public JavaInfoWin(Shell parent, int style) {
		super(parent, style);
		setText("Java environment info");
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open() {
		createContents();
		
		loadInfo();
		
		shell.open();
		shell.layout();
		Display display = getParent().getDisplay();
		cb = new Clipboard(display);
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return result;
	}

	private void loadInfo() 
	{
		String jitmp = new String();
		for (Map.Entry<Object, Object> e : System.getProperties().entrySet()) {
			jitmp += e +"\n";
		}
		getTextInfo().setText(jitmp);
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shell = new Shell(getParent(), getStyle());
		shell.setSize(606, 517);
		shell.setText(getText());
		shell.setLayout(new GridLayout(2, false));
		
		textInfo = new Text(shell, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		GridData gd_textInfo = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
		gd_textInfo.widthHint = 121;
		textInfo.setLayoutData(gd_textInfo);
		
		Button btnNewButton = new Button(shell, SWT.NONE);
		btnNewButton.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
	
					String textData = textInfo.getText();
					if (textData.length() > 0) {
						TextTransfer textTransfer = TextTransfer.getInstance();
						cb.setContents(new Object[]{textData}, new Transfer[]{textTransfer});
					}

				
			}
		});
		btnNewButton.setText("Copy to clipboard");
		
		Button btnClose = new Button(shell, SWT.NONE);
		btnClose.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				e.display.getActiveShell().close();
			}
		});
		btnClose.setText("Close");

	}
	protected Text getTextInfo() {
		return textInfo;
	}
}
