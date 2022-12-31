package com.groupunix.drivewireui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import com.groupunix.drivewireui.exceptions.DWUIOperationFailedException;

public class BugReportWin extends Dialog {

	protected Object result;
	protected Shell shlBugReport;

	private Button btnClose;
	
	private String title;
	private String summary;
	private String detail;
	private Text textAdditionalInfo;
	private Text textEmail;
	private Button btnErrMsg;
	private Button btnServerConf;
	private Button btnUIConf;
	private Button btnErrDetails;
	private Button btnJavaInfo;
	private Button btnUIText;
	private Button btnServerText;
	private Label label;

	
	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public BugReportWin(Shell parent, int style, String title, String summary, String detail) {
		super(parent, style);
		this.title = title;
		this.summary = summary;
		this.detail = detail;
		
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open() {
		createContents();

		shlBugReport.open();
		shlBugReport.layout();
		Display display = getParent().getDisplay();
		
		int x = getParent().getBounds().x + (getParent().getBounds().width / 2) - (shlBugReport.getBounds().width / 2);
		int y = getParent().getBounds().y + (getParent().getBounds().height / 2) - (shlBugReport.getBounds().height / 2);
		
		shlBugReport.setLocation(x, y);
		
		
		while (!shlBugReport.isDisposed()) {
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
		shlBugReport = new Shell(getParent(), SWT.DIALOG_TRIM | SWT.RESIZE);
		shlBugReport.setSize(776, 700);
		shlBugReport.setText("Bug Report for '" + title + "'");
		GridLayout gl_shlBugReport = new GridLayout(3, false);
		gl_shlBugReport.marginRight = 5;
		gl_shlBugReport.marginLeft = 5;
		gl_shlBugReport.marginTop = 5;
		gl_shlBugReport.marginBottom = 5;
		shlBugReport.setLayout(gl_shlBugReport);
		
		Label lblIfYouBelieve = new Label(shlBugReport, SWT.WRAP);
		lblIfYouBelieve.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1));
		lblIfYouBelieve.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.NORMAL));
		lblIfYouBelieve.setText("Please submit as much information as you can about this problem. ");
		new Label(shlBugReport, SWT.NONE);
		new Label(shlBugReport, SWT.NONE);
		new Label(shlBugReport, SWT.NONE);
		
		Label lblDataToInclude = new Label(shlBugReport, SWT.NONE);
		lblDataToInclude.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		lblDataToInclude.setText("What data would you like to include in this bug report?");
		new Label(shlBugReport, SWT.NONE);
		new Label(shlBugReport, SWT.NONE);
		
		label = new Label(shlBugReport, SWT.NONE);
		label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		new Label(shlBugReport, SWT.NONE);
		
		btnErrMsg = new Button(shlBugReport, SWT.CHECK);
		btnErrMsg.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1));
		btnErrMsg.setSelection(true);
		btnErrMsg.setText("The error message itself, if any");
		
		btnErrDetails = new Button(shlBugReport, SWT.CHECK);
		btnErrDetails.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1));
		btnErrDetails.setSelection(true);
		btnErrDetails.setText("The extended error details, as seen in the lower pane of the error dialog");
		
		btnUIConf = new Button(shlBugReport, SWT.CHECK);
		btnUIConf.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1));
		btnUIConf.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
			}
		});
		btnUIConf.setSelection(true);
		btnUIConf.setText("The DriveWire UI configuration (the contents of drivewireUI.xml on the client)");
		
		btnServerConf = new Button(shlBugReport, SWT.CHECK);
		btnServerConf.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1));
		btnServerConf.setSelection(true);
		btnServerConf.setText("The DriveWire server configuration (the contents of config.xml on the server)");
		
		btnJavaInfo = new Button(shlBugReport, SWT.CHECK);
		btnJavaInfo.setSelection(true);
		
		Link link = new Link(shlBugReport, SWT.NONE);
		link.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 2, 1));
		link.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				MainWin.doDisplayAsync(new Runnable() {

					public void run() 
					{
						JavaInfoWin jiwin = new JavaInfoWin(shlBugReport, SWT.DIALOG_TRIM);
						jiwin.open();
					}
					
				});
				
			}
		});
		link.setText("Information about your Java environment  (<a>click here to see what is included</a>)");
		
		btnUIText = new Button(shlBugReport, SWT.CHECK);
		btnUIText.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1));
		btnUIText.setText("The contents of the 'UI' pane (output from dw commands, etc)");
		
		btnServerText = new Button(shlBugReport, SWT.CHECK);
		btnServerText.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1));
		btnServerText.setText("The contents of the 'Server' pane (server log entries)");
		new Label(shlBugReport, SWT.NONE);
		new Label(shlBugReport, SWT.NONE);
		new Label(shlBugReport, SWT.NONE);
		
		Label lblAdditionalInformationbe = new Label(shlBugReport, SWT.NONE);
		lblAdditionalInformationbe.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1));
		lblAdditionalInformationbe.setText("Additional information (please be verbose):");
		
		textAdditionalInfo = new Text(shlBugReport, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL | SWT.MULTI);
		textAdditionalInfo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 3, 1));
		
		Label lblEmailAddressoptional = new Label(shlBugReport, SWT.NONE);
		lblEmailAddressoptional.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1));
		lblEmailAddressoptional.setText("Email address (if you wish to be contacted regarding this problem):");
		
		textEmail = new Text(shlBugReport, SWT.BORDER);
		textEmail.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 3, 1));
		
		new Label(shlBugReport, SWT.NONE);
		
		
		Button btnNewButton = new Button(shlBugReport, SWT.NONE);
		GridData gd_btnNewButton = new GridData(SWT.CENTER, SWT.FILL, true, false, 1, 1);
		gd_btnNewButton.heightHint = 32;
		btnNewButton.setLayoutData(gd_btnNewButton);
		btnNewButton.setImage(SWTResourceManager.getImage(BugReportWin.class, "/constatus/network-transmit-2.png"));
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (doSubmit())
				{
					shlBugReport.close();
				}
				
			}
		});
		btnNewButton.setText(" Submit via Internet");
		
		
		//FontData f = MainWin.getDialogFont();
		
		btnClose = new Button(shlBugReport, SWT.NONE);
		btnClose.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, false, false, 1, 1));
		btnClose.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) 
			{
			
				shlBugReport.close();
			
			}
		});
		btnClose.setText("Cancel");
		
		
		
		
	}

	
	
	

	

	protected boolean doSubmit() 
	{
		boolean res = false;
		
		String err = new String();
		
		// submit this thing..
		String surl = new String();
		
		surl = "br=0";
		
		try 
		{
			// Error details
			
			if (this.getBtnErrMsg().getSelection())
			{
				surl += "&" + encv("bugsum", this.summary);
			}
			
			if (this.getBtnErrDetails().getSelection())
			{
				surl += "&" + encv("bugdet", this.detail);
			}
			
			// DWUI
			
			if (this.getBtnUIConf().getSelection())
			{
				// DWUI version
				surl += "&" + encv("uiv",MainWin.DWUIVersion.toString());
			
				// DWUI config
				StringWriter sw = new StringWriter();
				try 
				{
					MainWin.config.save(sw);
					surl += "&" + encv("uiconf", sw.getBuffer().toString());
				} 
				catch (Exception e) 
				{
					surl += "&" + encv("uiconf", e.getMessage());
				}
				
			}
			
			
			// Server
			if (this.getBtnServerConf().getSelection())
			{
				// Try to get server version..
				Connection conn = new Connection(MainWin.getHost(),MainWin.getPort(), MainWin.getInstance());
			
				List<String> vres = new ArrayList<String>();
		
				try 
				{
					conn.Connect();
					vres = conn.loadList(-1,"ui server show version");	
					conn.close();
				
					surl += "&" + encv("dwv",vres.get(0));
				
				} 
				catch (UnknownHostException e) 
				{
					surl += "&" + encv("dwv",e.getMessage());
				} 
				catch (IOException e) 
				{
					surl += "&" + encv("dwv",e.getMessage());
				} 
				catch (DWUIOperationFailedException e)
				{
					surl += "&" + encv("dwv",e.getMessage());
				}
				
				// use local server config
				XMLConfiguration tmpc = new XMLConfiguration(MainWin.dwconfig);
				StringWriter sw = new StringWriter();
				try 
				{
					tmpc.save(sw);
					surl += "&" + encv("dwconf", sw.getBuffer().toString());
				} 
				catch (ConfigurationException e) 
				{
					surl += "&" + encv("dwconf", e.getMessage());
				}
				
				
			}
			
			// java info
			if (this.btnJavaInfo.getSelection())
			{
				String jitmp = new String();
				for (Map.Entry<Object, Object> e : System.getProperties().entrySet()) {
					jitmp += e +"\n";
				}
				surl += "&" + encv("java", jitmp);
			}
			
			// UI panes
			if (this.getBtnUIText().getSelection())
			{
				// TODO surl += "&" + encv("uitxt", MainWin.getUIText());
			}
			
			if (this.getBtnServerText().getSelection())
			{
				// TODO surl += "&" + encv("srvtxt", MainWin.getServerText());
			}
			
			
			// user supplied
			if (!this.getTextAdditionalInfo().getText().equals(""))
			{
				surl += "&" + encv("usrinf",this.getTextAdditionalInfo().getText());
			}
			if (!this.getTextEmail().getText().equals(""))
			{
				surl += "&" + encv("usraddr",this.getTextEmail().getText());
			}
			
		} 
		catch (UnsupportedEncodingException e) 
		{
			err += " " + e.getMessage();
		}
		
		
		if (!surl.equals("br=0"))
		{
			// 	finally.. submit this thing
		
			
			URL url;
			try 
			{
				url = new URL("http://aaronwolfe.com:80/dw4/br.pl");
				URLConnection conn = url.openConnection();
				conn.setDoOutput(true);
				OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
				wr.write(surl);
				wr.flush();
				
				BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				String line;
				@SuppressWarnings("unused")
				String txt = "";
				while ((line = rd.readLine()) != null) 
				{
					txt += line;  
				}
				wr.close();
				rd.close();

				
				res = true;
			} 
			catch (MalformedURLException e) 
			{
				err += " " + e.getMessage();
			} 
			catch (IOException e) 
			{	
				err += " " + e.getMessage();
			}
	    	
		}
		else
		{
			err += " No data to send!";
		}
		
		if (!res)
		{
			if (err.equals("aaronwolfe.com"))
				MainWin.showError("This just isn't your day...", "We had an error sending the bug report.  No internet connection?", "It seems we were unable to contact the bug report site via the internet.  The site could be down, or there may be a networking problem.");
			else
				MainWin.showError("This just isn't your day...", "We had an error sending the bug report.  Seek alternate routes, consult an exorcist, abort, retry, fail, etc...", "Possible clues: " + err);
		}
		
		
		return res;
	}

	private String encv(String key, String val) throws UnsupportedEncodingException 
	{
		String res = new String();
		
		res = URLEncoder.encode(key, "UTF-8") + "=" + URLEncoder.encode(val,"UTF-8");
		
		return res;
	}
	protected Button getBtnErrMsg() {
		return btnErrMsg;
	}
	protected Text getTextAdditionalInfo() {
		return textAdditionalInfo;
	}
	protected Text getTextEmail() {
		return textEmail;
	}
	protected Button getBtnServerConf() {
		return btnServerConf;
	}
	protected Button getBtnUIConf() {
		return btnUIConf;
	}
	protected Button getBtnErrDetails() {
		return btnErrDetails;
	}
	protected Button getBtnJavaInfo() {
		return btnJavaInfo;
	}
	protected Button getBtnUIText() {
		return btnUIText;
	}
	protected Button getBtnServerText() {
		return btnServerText;
	}
}
