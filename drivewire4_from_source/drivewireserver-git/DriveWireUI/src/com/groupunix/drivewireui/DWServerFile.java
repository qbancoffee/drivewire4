package com.groupunix.drivewireui;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URI;
import java.net.URL;

import com.groupunix.drivewireui.exceptions.DWUIOperationFailedException;

public class DWServerFile extends File {


	private static final long serialVersionUID = -5842864119672365113L;
	private String pathname;
	private String aseparator;
	private String parent;
	private long length = 0;
	private long lastmodified = 0;
	private boolean directory = false;
	private boolean root = false;

	public DWServerFile(String pathname) 
	{
		super(pathname);
		this.pathname = pathname;

		if (!(pathname == null) && (!pathname.equals(".")))
		{
			// load details from server
			try 
			{
				String vals;
				vals = UIUtils.loadList("ui server file info " + pathname).get(0);
				this.setVals(vals);
			} 
			catch (IOException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			catch (DWUIOperationFailedException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
	
	public void setVals(String vals)
	{
		String[] parts = vals.split("\\|");
		if (parts.length == 7)
		{
			this.aseparator = parts[0];
			this.pathname = parts[1];
			this.parent = parts[2];
			this.length = Long.parseLong(parts[3]);
			this.lastmodified = Long.parseLong(parts[4]);
			this.directory = Boolean.parseBoolean(parts[5]);
			this.root = Boolean.parseBoolean(parts[6]);
		}
		else
		{
			System.out.println("Possible error in remote file browse response: " + vals);
		}
	}
	
	
	public boolean	canRead()
	{
		return(true);
	}
	
    
	public boolean	canWrite()
	{
		return(false);
	}
	

	public boolean	createNewFile()
	{
		return(false);
	}
    
	public static File	createTempFile(String prefix, String suffix) 
	{
		return null;
	}
    
	public static File	createTempFile(String prefix, String suffix, File directory)
	{
		return null;
	}
	
    
	public boolean	delete()
	{
		return false;
	}
    
	public void	deleteOnExit()
	{
		
	}
    
	
	public boolean	exists() 
	{
		return(true);
	}
	
	public File getAbsoluteFile()
	{
		return new DWServerFile(this.pathname);
	}
	
    
	public String getAbsolutePath()
	{
		return this.pathname;
	}
	
    
	public File getCanonicalFile()
	{
		
		return new DWServerFile(this.pathname);
	}
	
    
	public String	getCanonicalPath()
	{
		return this.pathname;
	}
	
    public String	getName() 
    {
    	if (this.pathname == null)
    		return(null);
    	
    	String sep = this.aseparator;
    	
    	// stupid hack for windows separator
    	if (sep.equals("\\"))
    	{
    		sep = "\\\\";
    	}
    	
    	String[] parts = this.pathname.split(sep); 
    	if (parts.length > 1)
    		return parts[parts.length - 1];
    	
    	return(this.pathname);
    }

    public String getParent() 
    {
    	if ((this.parent == null) || this.parent.equals("null"))
    	{
    		return null;
    	}
    	return this.parent;
    }
    
    public File	getParentFile()
    {
    	if ((this.parent == null) || this.parent.equals("null"))
    	{
    		return null;
    	}
    	return(new DWServerFile(this.parent));
    }
    
    public String getPath()
    {
    	return this.pathname;
    }
    
    public int	hashCode()
    {
    	return 0;
    }
    
    public boolean	isAbsolute()
    {
    	return true;
    }
    
    public boolean	isDirectory()
    {
    	return this.directory;
    }
    
    
    public boolean	isFile() 
    {
    	if (this.directory)
    		return false;
    	return true;
    }

    public boolean	isHidden() 
    {
    	return false;
    }
    
    public long	lastModified()
    {
    	return this.lastmodified;
    }
    
    public long	length() 
    {
    	return this.length;
    }
    
    
    public String[]	list()
    {
    	System.out.println("list");
    	return null;
    }
    
    public String[]	list(FilenameFilter filter) 
    {
    	System.out.println("list2");
    	return null;
    	
    }

    public File[]	listFiles() 
    {
    	System.out.println("listfiles");
    	return null;
    }
    
    public File[]	listFiles(FileFilter filter)
    {
    	System.out.println("listfiles2");
    	return null;
    }
    
    public File[]	listFiles(FilenameFilter filter)
    {
    	System.out.println("listfiles3");
    	return null;
    }
    
    public static File[]	listRoots()
    {
    	System.out.println("listroots");
    	return null;
    }
    
    public boolean	mkdir() 
    {
    	return false;
    	
    }

    public boolean	mkdirs() 
    {
    	return false;
    }
    
    public boolean	renameTo(File dest) 
    {
    	return false;
    }
    
    public boolean	setLastModified(long time)
    {
    	return false;
    }
    
    
    public boolean	setReadOnly()
    {
    	return false;
    }
    
    public String	toString() 
    {
    	return this.pathname;
    }

    public URI	toURI() 
    {
    	System.out.println("touri");
    	return null;
    }

    public URL	toURL() 
    {
    	System.out.println("tourl");
    	return null;
    }

	public boolean isRoot() 
	{
		return this.root;
	}

	public String getSeparator() {
		
		return this.aseparator;
	}
	

}
