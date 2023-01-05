package com.groupunix.drivewireui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

public class PrinterSettingsWin extends Dialog {

	protected Object result;
	protected Shell shell;
	private Text text;
	private Text text_1;
	private Text text_2;
	private Text text_3;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public PrinterSettingsWin(Shell parent, int style) {
		super(parent, style);
		setText("Printer Settings");
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open() {
		createContents();
		shell.open();
		shell.layout();
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return result;
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shell = new Shell(getParent(), getStyle());
		shell.setSize(450, 322);
		shell.setText(getText());
		
		text = new Text(shell, SWT.BORDER);
		text.setBounds(86, 21, 112, 21);
		
		Combo combo = new Combo(shell, SWT.NONE);
		combo.setItems(new String[] {"FX80", "TEXT"});
		combo.setBounds(86, 48, 112, 23);
		
		Label lblName = new Label(shell, SWT.NONE);
		lblName.setAlignment(SWT.RIGHT);
		lblName.setBounds(10, 24, 70, 15);
		lblName.setText("Name:");
		
		Label lblDriver = new Label(shell, SWT.NONE);
		lblDriver.setAlignment(SWT.RIGHT);
		lblDriver.setBounds(10, 51, 70, 15);
		lblDriver.setText("Driver:");
		
		Composite compositeFX80 = new Composite(shell, SWT.NONE);
		compositeFX80.setBounds(10, 136, 424, 108);
		
		text_3 = new Text(compositeFX80, SWT.BORDER);
		text_3.setBounds(110, 10, 277, 21);
		
		Button button_1 = new Button(compositeFX80, SWT.NONE);
		button_1.setBounds(393, 8, 30, 25);
		button_1.setText("...");
		
		Label lblCharacterDefs = new Label(compositeFX80, SWT.NONE);
		lblCharacterDefs.setAlignment(SWT.RIGHT);
		lblCharacterDefs.setBounds(0, 13, 104, 15);
		lblCharacterDefs.setText("Character defs:");
		
		Spinner spinner = new Spinner(compositeFX80, SWT.BORDER);
		spinner.setMaximum(240);
		spinner.setBounds(110, 44, 63, 22);
		
		Label lblColumns = new Label(compositeFX80, SWT.NONE);
		lblColumns.setAlignment(SWT.RIGHT);
		lblColumns.setBounds(29, 47, 75, 15);
		lblColumns.setText("Columns:");
		
		Spinner spinner_1 = new Spinner(compositeFX80, SWT.BORDER);
		spinner_1.setMaximum(240);
		spinner_1.setBounds(286, 44, 63, 22);
		
		Label lblLinesPerPage = new Label(compositeFX80, SWT.NONE);
		lblLinesPerPage.setAlignment(SWT.RIGHT);
		lblLinesPerPage.setBounds(169, 47, 111, 15);
		lblLinesPerPage.setText("Lines per page:");
		
		Spinner spinner_2 = new Spinner(compositeFX80, SWT.BORDER);
		spinner_2.setIncrement(25);
		spinner_2.setPageIncrement(100);
		spinner_2.setMaximum(2400);
		spinner_2.setBounds(110, 72, 63, 22);
		
		Label lblDpi = new Label(compositeFX80, SWT.NONE);
		lblDpi.setAlignment(SWT.RIGHT);
		lblDpi.setBounds(49, 75, 55, 15);
		lblDpi.setText("DPI:");
		
		Combo combo_1 = new Combo(compositeFX80, SWT.NONE);
		combo_1.setItems(new String[] {"BMP", "GIF", "JPG", "PNG", "WBMP"});
		combo_1.setBounds(286, 72, 63, 23);
		
		Label lblImageFormat = new Label(compositeFX80, SWT.NONE);
		lblImageFormat.setAlignment(SWT.RIGHT);
		lblImageFormat.setBounds(179, 75, 101, 15);
		lblImageFormat.setText("Image format:");
		
		Button btnOk = new Button(shell, SWT.NONE);
		btnOk.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			}
		});
		btnOk.setBounds(278, 260, 75, 25);
		btnOk.setText("Ok");
		
		Button btnCancel = new Button(shell, SWT.NONE);
		btnCancel.setBounds(359, 260, 75, 25);
		btnCancel.setText("Cancel");
		
		Label lblNewLabel = new Label(shell, SWT.NONE);
		lblNewLabel.setAlignment(SWT.RIGHT);
		lblNewLabel.setBounds(10, 83, 69, 15);
		lblNewLabel.setText("Output to:");
		
		text_1 = new Text(shell, SWT.BORDER);
		text_1.setBounds(86, 80, 312, 21);
		
		Button button = new Button(shell, SWT.NONE);
		button.setBounds(404, 78, 30, 25);
		button.setText("...");
		
		text_2 = new Text(shell, SWT.BORDER);
		text_2.setBounds(121, 109, 277, 21);
		
		Label lblFlushCommand = new Label(shell, SWT.NONE);
		lblFlushCommand.setAlignment(SWT.RIGHT);
		lblFlushCommand.setBounds(10, 112, 105, 15);
		lblFlushCommand.setText("Flush command:");
		
		Label label = new Label(shell, SWT.NONE);
		label.setAlignment(SWT.RIGHT);
		label.setImage(SWTResourceManager.getImage(PrinterSettingsWin.class, "/printers/printer-4.png"));
		label.setBounds(343, 10, 91, 56);

	}
}
