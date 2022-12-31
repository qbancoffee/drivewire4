package com.groupunix.drivewireserver.dwdisk;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;

import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.event.ConfigurationListener;
import org.apache.commons.vfs.Capability;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.VFS;
import org.apache.log4j.Logger;

import com.groupunix.drivewireserver.DWDefs;
import com.groupunix.drivewireserver.dwexceptions.DWDiskInvalidSectorNumber;
import com.groupunix.drivewireserver.dwexceptions.DWDriveWriteProtectedException;
import com.groupunix.drivewireserver.dwexceptions.DWImageFormatException;
import com.groupunix.drivewireserver.dwexceptions.DWImageHasNoSourceException;
import com.groupunix.drivewireserver.dwexceptions.DWInvalidSectorException;
import com.groupunix.drivewireserver.dwexceptions.DWSeekPastEndOfDeviceException;

public abstract class DWDisk 
{
	private static final Logger logger = Logger.getLogger("DWServer.DWDisk");
	
	protected HierarchicalConfiguration params;
	public Vector<DWDiskSector> sectors = new Vector<DWDiskSector>();	
	protected FileObject fileobj = null;
	protected DWDiskConfigListener configlistener;
	protected DWDiskDrive drive;


	
	
	
	// required for format implementation:
	public abstract void seekSector(int lsn) throws DWInvalidSectorException, DWSeekPastEndOfDeviceException;
	public abstract void writeSector(byte[] data, boolean update) throws DWDriveWriteProtectedException, IOException;

	
	public abstract byte[] readSector() throws IOException, DWImageFormatException;
	protected abstract void load() throws IOException, DWImageFormatException;
	
	public abstract int getDiskFormat();
	
	
	// file image
	public DWDisk(FileObject fileobj) throws IOException, DWImageFormatException
	{

		this.fileobj = fileobj;
		
		this.params = new HierarchicalConfiguration();
		
		// internal 
		this.setParam("_path", fileobj.getName().getURI());
		
	    long lastmodtime = -1;
	    
	    try
	    {
	    	lastmodtime = this.fileobj.getContent().getLastModifiedTime();
	    }
	    catch (FileSystemException e)
	    {
	    	logger.warn(e.getMessage());
	    }
	    
	    this.setLastModifiedTime(lastmodtime); 
		
		this.setParam("_reads", 0);
		this.setParam("_writes", 0);
		this.setParam("_lsn", 0);
		
		
		// user options
		this.setParam("writeprotect",DWDefs.DISK_DEFAULT_WRITEPROTECT);
		
	}
	
	// memory image
	public DWDisk()
	{

		this.fileobj = null;
		
		this.params = new HierarchicalConfiguration();
		
		// internal 
		this.setParam("_path", "");
		this.setParam("_reads", 0);
		this.setParam("_writes", 0);
		this.setParam("_lsn", 0);
		
		
		// user options
		this.setParam("writeprotect",DWDefs.DISK_DEFAULT_WRITEPROTECT);
		
	}
	
	
	public HierarchicalConfiguration getParams()
	{
		return this.params;
	}
	
	
	public void setParam(String key, Object val) 
	{
		if (val == null)
		{
			this.params.clearProperty(key);
		}
		else
		{
			this.params.setProperty(key, val);
		}
	}
	
	public void incParam(String key)
	{
		this.setParam(key, this.params.getInt(key,0) + 1);
	}
	
	
	public String getFilePath()
	{
		if (this.fileobj != null)
			return this.fileobj.getName().getURI();
		else
			return("(in memory only)");
	}

	public FileObject getFileObject()
	{
		return(this.fileobj);
	}

	
	public void setLastModifiedTime(long lastModifiedTime) 
	{
		this.params.setProperty("_last_modified", lastModifiedTime);
	}
	
	public long getLastModifiedTime() 
	{
		return this.params.getLong("_last_modified", 0);
	}
	
	public int getLSN()
	{
		return(this.params.getInt("_lsn",0));
	}
	
	
	
	
	public int getDiskSectors()
	{
		return(this.sectors.size());
	}
	
	
	
	
	
	public void reload() throws IOException, DWImageFormatException
	{
		if (this.getFileObject() != null)
		{
			logger.debug("reloading disk sectors from " + this.getFilePath());
	
			this.sectors.clear();
			
			// load from path 
			load();
		}
		else
		{
			throw (new DWImageFormatException("Image is in memory only, so cannot reload."));
		}
	}

	
	public void eject() throws IOException
	{
		sync();
		this.sectors = null;
		
		if (this.fileobj != null)
		{
			this.fileobj.close();
			this.fileobj = null;
		}
	}

	
	public void sync() throws IOException
	{
		// NOP on readonly image formats
	}

	public void write() throws IOException, DWImageHasNoSourceException
	{
		// Fail on readonly image formats
		throw new IOException("Image is read only");
	}

	
	public void writeTo(String path) throws IOException
	{
		// write in memory image to specified path (raw format)
		// using most efficient method available
		
		FileObject altobj = VFS.getManager().resolveFile(path);
		
		if (altobj.isWriteable())
		{
			if (altobj.getFileSystem().hasCapability(Capability.WRITE_CONTENT))
			{
				// we always rewrite the entire object
				writeSectors(altobj);
			}
			else
			{
				// no way to write to this filesystem
				logger.warn("Filesystem is unwritable for path '"+ altobj.getName() + "'");
				throw new FileSystemException("Filesystem is unwriteable");
			}
		}
		else
		{
			logger.warn("File is unwriteable for path '" + altobj.getName() + "'");
			throw new IOException("File is unwriteable");
		}
	}
	
	
	 public void writeSectors(FileObject fobj) throws IOException
	 {
		 // write out all sectors
		   
		 long time_getdata = 0;
		 long time_write = 0;
		 long time_clean = 0;
		 long time_init = 0;
		 
		 long time_point = System.currentTimeMillis();
		 
		 logger.debug("Writing out all sectors from cache to " + fobj.getName());
		 
		 BufferedOutputStream fos = new BufferedOutputStream(fobj.getContent().getOutputStream());

		 int ss = DWDefs.DISK_SECTORSIZE;
 		 
 		 if (this.getParams().containsKey("_sectorsize"))
 		 {
 			 try
 			 {
 				ss = (Integer) this.getParam("_sectorsize");
 			 }
 			 catch (NumberFormatException e)
 			 {
 				 // how did they get a non int value in there.. whatever
 			 }
 			 
 		 }
		 
 		 byte[] zerofill = new byte[ss];
 		 
 		 time_init = System.currentTimeMillis() - time_point;
 		 
		 for (int i = 0;i < this.sectors.size();i++)
		 {
		 	 // we do have a sector obj
		 	 if (this.sectors.get(i) != null)
		 	 {
		 		 time_point = System.currentTimeMillis();
		 		 byte[] tmp = this.sectors.get(i).getData();
		 		 time_getdata += (System.currentTimeMillis() - time_point);
		 		 
		 		 time_point = System.currentTimeMillis();
			 	 fos.write(tmp, 0, tmp.length);
			 	 time_write += (System.currentTimeMillis() - time_point);
			 	 
			 	 time_point = System.currentTimeMillis();
		 		 this.sectors.get(i).makeClean();
		 		 time_clean += (System.currentTimeMillis() - time_point);
		 	 }
		 	 // we dont, write 0 filled
		 	 else
		 	 {
		 		fos.write(zerofill, 0, ss);
		 	 }
		 }
			
		 
		 logger.debug("disk write timing = init: " + time_init + "  getdata: " + time_getdata + "  writestream: " + time_write + "  clean: " + time_clean);
		 
		 fos.close();
		   
		 if (this.fileobj != null)
			 this.setLastModifiedTime(this.fileobj.getContent().getLastModifiedTime()); 
	   
	 }
	
	 
	 
	 
	public int getDirtySectors() 
	{
		int drt = 0;
		
		if (this.sectors != null)
		{
			for (int i=0;i<this.sectors.size();i++)
			{
				if (this.sectors.get(i) != null)
				{
					if (this.sectors.get(i).isDirty())
					{
						drt++;
					}
				}
			}
		}
		return(drt);
	}
	 
	 
	public DWDiskSector getSector(int no) throws DWDiskInvalidSectorNumber
	{
		if ((no < 0) || (no >= this.sectors.size()))
		{
			throw new DWDiskInvalidSectorNumber("Invalid sector number: " + no);
		}
		
		if (this.sectors.get(no) != null)
		{
			return(this.sectors.get(no));
		}
		
		return(null);
			
	}
	 
	 
	public boolean getWriteProtect()
	{
		return(this.params.getBoolean("writeprotect",DWDefs.DISK_DEFAULT_WRITEPROTECT));
	}
	
	public Object getParam(String key)
	{
		if (this.params.containsKey(key))
			return(this.params.getProperty(key));
		
		return(null);
	}


	
	public void insert(DWDiskDrive drive)
	{
		this.drive = drive;
		
		// remove any existing listeners
		@SuppressWarnings("unchecked")
		Iterator<ConfigurationListener> citr = this.params.getConfigurationListeners().iterator();
		while (citr.hasNext())
		{
			this.params.removeConfigurationListener(citr.next());
		}

		// add for this drive
		this.params.addConfigurationListener(new DWDiskConfigListener(this));
	
		// announce drive info to any event listeners
		@SuppressWarnings("unchecked")
		Iterator<String> itr = this.params.getKeys();
		while(itr.hasNext())
		{
			String key = itr.next();
			this.drive.submitEvent(key, this.params.getProperty(key).toString());
		}
		
	}
	
	public void submitEvent(String key, String val)
	{
		if (this.drive != null)
		{
			this.drive.submitEvent(key, val);
		}
	}
	
	public boolean getDirect()
	{
		
		return false;
	}
	
	public Vector<DWDiskSector> getSectors()
	{
		return this.sectors;
	}
	

	public void writeSector(byte[] data) throws DWDriveWriteProtectedException, IOException
	{
		writeSector(data, true);
	}

	
}
