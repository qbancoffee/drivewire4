package com.groupunix.drivewireui;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileSystemView;

public class DWServerFileChooser extends JFileChooser {

	private static final long serialVersionUID = -1032049099726544597L;
	private String lastdir = ".";
	private String separator = "/";
	private File[] rootcache = null;
	
	public DWServerFileChooser() 
	{
		super();
	}


	public DWServerFileChooser(String text) {
		super(text);
	}


	public FileSystemView getFileSystemView()
	{
		
		return(new DWServerFileSystemView(this));

	}


	public void setLastDirectory(String lastdir) 
	{
		this.lastdir = lastdir;
	}
	
	public String getLastDirectory()
	{
		return this.lastdir;
	}


	public void setSeparator(String separator) {
		this.separator = separator;
	}


	public String getSeparator() {
		return separator;
	}


	public void setSelectedFile(String fname) 
	{
		this.setSelectedFile(new DWServerFile(fname));
	}
	
	public File[] getRootCache()
	{
		return this.rootcache;
	}
	
	public void setRootCache(File[] rc)
	{
		this.rootcache = rc;
	}
}
