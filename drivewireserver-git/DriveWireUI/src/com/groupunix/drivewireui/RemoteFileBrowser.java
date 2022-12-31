package com.groupunix.drivewireui;

import javax.swing.JFileChooser;

public class RemoteFileBrowser implements Runnable
{
	String startpath;
	boolean dir;
	boolean save;
	String title;
	String buttontext;
	String[] fileext;
	
	String selected = null;
	
	public RemoteFileBrowser(final boolean save, final boolean dir, final String startpath, final String title, final String buttontext, String[] fileext)
	{
		this.startpath = startpath;
		this.dir = dir;
		this.save = save;
		this.title = title;
		this.buttontext = buttontext;
		this.fileext = fileext;
	}
	
	
	
	public void run() 
	{
		// create a file chooser
		final DWServerFileChooser fileChooser;

		if (!startpath.equals(""))
		{
			fileChooser = new DWServerFileChooser(startpath);
		}
		else
		{
			fileChooser = new DWServerFileChooser();
		}
		// 	configure the file dialog
		
		fileChooser.setFileHidingEnabled(false);
		fileChooser.setMultiSelectionEnabled(false);
		
		if (dir)
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		else
			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		
		if (save)
			fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
		else
			fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
		
		fileChooser.setDialogTitle(title);
		
		if (!startpath.equals(""))
		{
			fileChooser.setSelectedFile(new DWServerFile(startpath));
		}
		
		// TODO filters dont work
		//if ((fileext == null) || (fileext.length == 0))
			fileChooser.setAcceptAllFileFilterUsed(true);
		/*else
		{
			for (String ext : fileext)
			{
				final String text = ext;
				fileChooser.setFileFilter(new FileFilter() {
					public boolean accept(File f) {
						return f.getName().toLowerCase().endsWith(text) || f.isDirectory();
					}

					public String getDescription() {
						return text + " files";
					}
		        });
			}
		}
		*/
			
		// 	show the file dialog
		int answer = fileChooser.showDialog(fileChooser, buttontext);
		
		
		// 	check if a file was selected
		if (answer == JFileChooser.APPROVE_OPTION)
		{
			selected =  fileChooser.getSelectedFile().getPath();
		}		

	}

	public String getSelected()
	{
		return selected;
	}

}
