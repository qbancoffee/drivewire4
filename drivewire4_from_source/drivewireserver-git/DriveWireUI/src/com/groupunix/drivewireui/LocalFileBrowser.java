package com.groupunix.drivewireui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

public class LocalFileBrowser extends Dialog implements Runnable 
{
	String startpath;
	boolean dir;
	boolean save;
	String title;
	String buttontext;
	String[] fileext;
	
	String selected = null;
        private Shell shell;
	
	public LocalFileBrowser(Shell parent, final boolean save, final boolean dir, final String startpath, final String title, final String buttontext, String[] fileext)
	{
                super(parent);
		this.startpath = startpath;
		this.dir = dir;
		this.save = save;
		this.title = title;
		this.buttontext = buttontext;
		this.fileext = fileext;
                shell = parent;
	}
	
	
	@Override
	public void run() 
	{
		if (!dir)
		{
			FileDialog fd;
			
			if (save)
				fd = new FileDialog(shell, SWT.SAVE);
			else
				fd = new FileDialog(shell, SWT.OPEN);
			
	        fd.setText(title);
	        fd.setFilterPath(startpath);
	        
	        if (fileext != null)
	        	fd.setFilterExtensions(fileext);
	        else
	        	fd.setFilterExtensions(new String[] {"*.*"});
	        
	        selected = fd.open();
		}
		else
		{
			DirectoryDialog dd = new DirectoryDialog(Display.getCurrent().getActiveShell());
			
			dd.setFilterPath(startpath);
			dd.setText(title);
			dd.setMessage(buttontext);
			
			selected = dd.open();
			
		}
	}

	public String getSelected()
	{
		return selected;
	}

}
