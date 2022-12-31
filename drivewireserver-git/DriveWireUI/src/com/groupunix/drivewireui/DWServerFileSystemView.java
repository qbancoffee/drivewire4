package com.groupunix.drivewireui;

import java.io.File;
import java.io.IOException;

import javax.swing.Icon;
import javax.swing.filechooser.FileSystemView;

import com.groupunix.drivewireui.exceptions.DWUIOperationFailedException;

public class DWServerFileSystemView extends FileSystemView {

	private String lastdir = ".";
	private DWServerFileChooser chooser = null;

	
	public DWServerFileSystemView(DWServerFileChooser chooser) 
	{
		this.chooser = chooser;
	}
	
	@Override
	public File createNewFolder(File arg0) throws IOException {
		// TODO Auto-generated method stub
		return null;

	}

	@Override
	public File[] getRoots()
	{
		if (this.chooser.getRootCache() == null)
		{
		
			try 
			{
				this.chooser.setRootCache(UIUtils.getFileArray("ui server file roots"));
			} 
			catch (IOException e) 
			{
				MainWin.showError("An IO error occured", "Unable to retrieve a list of file system roots.", e.getMessage());
			} 
			catch (DWUIOperationFailedException e) 
			{	
				MainWin.showError("A DW error occured", "Unable to retrieve a list of file system roots.", e.getMessage());
			}
		}
		
		
		return(this.chooser.getRootCache());
	}
	
	@Override
	public DWServerFile getDefaultDirectory()
	{
		DWServerFile[] dd = null;
		try 
		{
			dd = UIUtils.getFileArray("ui server file defaultdir");
			
			if (!(dd == null) && (dd.length ==1))
			{
				return(dd[0]);
			}
			
			
		} 
		catch (IOException e) 
		{
			MainWin.showError("An IO error occured", "Unable to retrieve default directory.", e.getMessage());
			
		} 
		catch (DWUIOperationFailedException e) 
		{
			MainWin.showError("A DW error occured", "Unable to retrieve default directory.", e.getMessage());
		}
		
		
		return(null);
	}
	
	
	@Override
	public DWServerFile getHomeDirectory()
	{
		return(this.getDefaultDirectory());
	}
	
	
	@Override
	public DWServerFile[] getFiles(File dir, boolean useFileHiding)
	{
		
		this.lastdir = dir.getPath();
		this.chooser.setLastDirectory(this.lastdir);
		DWServerFile[] res = null;
		
		try {
			res = UIUtils.getFileArray("ui server file dir " + dir.getPath());
		}
		catch (IOException e) 
		{
			MainWin.showError("An IO error occured", "Unable to retrieve a list of files.", e.getMessage());
		} 
		catch (DWUIOperationFailedException e) 
		{
			MainWin.showError("A DW error occured", "Unable to retrieve a list of files.", e.getMessage());
		}
		
		if (!(res == null) && (res.length > 0))
		{
			this.chooser.setSeparator(res[0].getSeparator());
		}
		return(res);
	}
	
	
	
	 public DWServerFile createFileObject(DWServerFile dir, String filename)
	 {
		DWServerFile f = new DWServerFile(dir.getAbsolutePath() + dir.getSeparator() + filename);
		return f;
		 
	 }
     
	 public File createFileObject(String path)
	 {
	 
		 DWServerFile f = null;
		 
		 if ((path == null) || (this.chooser == null) || (this.chooser.getSeparator() == null))
		 {
			 return(new DWServerFile("THERE IS NO FILE YOU INSENSITIVE CLOD"));
		 }
		 
		 
		 if (path.indexOf(this.chooser.getSeparator()) > -1 )
		 {
			f = new DWServerFile(path);
			
		 }	
		 else
		 {
			f = new DWServerFile(this.chooser.getLastDirectory() + this.chooser.getSeparator() + path);
			
		 }
		
		 return f;
	 }
	 
     
	 public File getChild(File parent, String fileName)
	 {
		return null; 
	 }
      
      
	 public File getParentDirectory(File dir)
	 {
		if (dir.getParent() == null)
			return dir;
		return new DWServerFile(dir.getParent());
		
	 }
     

	 public String	getSystemDisplayName(File f)
	 {
		return f.getName();
	 }

	 public Icon getSystemIcon(File f)
	 {
		return null;
	 }
     
	 public String	getSystemTypeDescription(File f)
	 {
		return "description";
	 }
	 
	 public boolean isComputerNode(File dir)
	 {
		return false;
	 }
     
	 public boolean isDrive(File dir)
	 {
		return false;
	 }
	 
	 public boolean isFileSystem(File f)
	 {
		
		return true;
	 }
     
	 public boolean isFileSystemRoot(File dir)
	 {
		return false;
	 }
     
	 public boolean isFloppyDrive(File dir)
	 {
		return false;
	 }
	 
	 public boolean	isHiddenFile(File f)
	 {
		return false;
	 }
     
	 public boolean	isParent(File folder, File file)
	 {
		if (file.getPath().startsWith(folder.getPath()))
			return true;
		return false;
	 }
     
	 public boolean	isRoot(DWServerFile f) 
     {
		
		if (f == null)
			return false;
		
		return f.isRoot();
     }
	 


	public Boolean	isTraversable(File f) 
	{
		if (f.isDirectory())
			return(true);
		else
			return(false);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
