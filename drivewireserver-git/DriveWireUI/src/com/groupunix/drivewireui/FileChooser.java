package com.groupunix.drivewireui;

import java.io.File;

import javax.swing.JFileChooser;

public class FileChooser {

	private String fname;
	private String desc;
	private boolean dir;
	

	public FileChooser(String fname, String desc, boolean dir)
	{
		this.fname = fname;
		this.desc = desc;
		this.dir = dir;
		
	}
	
	

	public String getFile() 
	{
		// create a file chooser
		DWServerFileChooser fileChooser;
		
		if ((this.fname == null) || (this.fname.equals("")))
		{
			fileChooser = new DWServerFileChooser();
		}
		else
		{
			fileChooser = new DWServerFileChooser(fname);
		}
		
		int answer;
		//	configure the file dialog
		
		fileChooser.setFileHidingEnabled(false);
		fileChooser.setMultiSelectionEnabled(false);
		if (dir)
		{
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		}
		else
		{
			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		}
		fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
		fileChooser.setSelectedFile(fname);

		// 	show the file dialog
		answer = fileChooser.showDialog(fileChooser, this.desc);
			
		// 	check if a file was selected
		if (answer == JFileChooser.APPROVE_OPTION)
		{
			final File selected =  fileChooser.getSelectedFile();

			return(selected.getPath());

		}
		
		return(null);
	}


}
