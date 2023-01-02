package com.groupunix.drivewireserver.dwdisk.filesystem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.groupunix.drivewireserver.DECBDefs;
import com.groupunix.drivewireserver.DWDefs;
import com.groupunix.drivewireserver.DriveWireServer;
import com.groupunix.drivewireserver.dwdisk.DWDisk;
import com.groupunix.drivewireserver.dwdisk.DWDiskDrives;
import com.groupunix.drivewireserver.dwdisk.DWDiskSector;
import com.groupunix.drivewireserver.dwexceptions.DWDiskInvalidSectorNumber;
import com.groupunix.drivewireserver.dwexceptions.DWDriveWriteProtectedException;
import com.groupunix.drivewireserver.dwexceptions.DWFileSystemFileNotFoundException;
import com.groupunix.drivewireserver.dwexceptions.DWFileSystemFullException;
import com.groupunix.drivewireserver.dwexceptions.DWFileSystemInvalidDirectoryException;
import com.groupunix.drivewireserver.dwexceptions.DWFileSystemInvalidFATException;
import com.groupunix.drivewireserver.dwexceptions.DWFileSystemInvalidFilenameException;
import com.groupunix.drivewireserver.dwexceptions.DWInvalidSectorException;
import com.groupunix.drivewireserver.dwexceptions.DWSeekPastEndOfDeviceException;
import com.groupunix.drivewireserver.dwprotocolhandler.DWUtils;

public class DWDECBFileSystem extends DWFileSystem
{
	
	
	private static final String FSNAME = "DECB";
	private List<String> fserrors = new ArrayList<String>();

	public DWDECBFileSystem(DWDisk disk)
	{
		super(disk);
	}
	
	public List<DWFileSystemDirEntry> getDirectory(String path) throws IOException, DWFileSystemInvalidDirectoryException	
	{
		List<DWFileSystemDirEntry> dir = new ArrayList<DWFileSystemDirEntry>();
		
		try
		{
			for (int i = 0;i<9;i++)
			{
				for (int j = 0;j<8;j++)
				{
						byte[] buf = new byte[32];
						System.arraycopy(disk.getSector(i + DECBDefs.DIRECTORY_OFFSET).getData(), 32*j , buf, 0, 32);
						DWDECBFileSystemDirEntry entry = new DWDECBFileSystemDirEntry(buf);
						dir.add(entry);
				}
			
			}
		}
		catch (DWDiskInvalidSectorNumber e)
		{
			throw new DWFileSystemInvalidDirectoryException("Invalid DECB directory: " + e.getMessage());
		}
		
		
		return dir;
	
	}


	public boolean hasFile(String filename) throws IOException
	{
		
		try
		{
			for (DWFileSystemDirEntry e : this.getDirectory(null))
			{
				if ((e.getFileName().trim() + "." + e.getFileExt()).equalsIgnoreCase(filename))
				{
					return true;
				}
			}
		} 
		catch (DWFileSystemInvalidDirectoryException e)
		{
		}
		
		return false;
	}
	
	
	public ArrayList<DWDiskSector> getFileSectors(String filename) throws DWFileSystemFileNotFoundException, DWFileSystemInvalidFATException, IOException, DWDiskInvalidSectorNumber, DWFileSystemInvalidDirectoryException
	{
		
			
		return(getFAT().getFileSectors(disk.getSectors(), ((DWDECBFileSystemDirEntry) getDirEntry(filename)).getFirstGranule()));
		
	}


	public DWFileSystemDirEntry getDirEntry(String filename) throws DWFileSystemFileNotFoundException, IOException, DWFileSystemInvalidDirectoryException
	{
		
		for (DWFileSystemDirEntry e : this.getDirectory(null))
		{
			if ((e.getFileName().trim() + "." + e.getFileExt()).equalsIgnoreCase(filename))
			{
				return e;
			}
		}
		
		throw (new DWFileSystemFileNotFoundException("File '" + filename + "' not found in DOS directory."));
	}


	public byte[] getFileContents(String filename) throws DWFileSystemFileNotFoundException, DWFileSystemInvalidFATException, IOException, DWDiskInvalidSectorNumber, DWFileSystemInvalidDirectoryException
	{
			
		
		byte[] res = new byte[0];
		
		ArrayList<DWDiskSector> sectors = this.getFileSectors(filename);
		
		int bl = ((DWDECBFileSystemDirEntry) getDirEntry(filename)).getBytesInLastSector();
		
		if ((sectors != null) && (sectors.size() > 0))
		{
			res = new byte[(sectors.size()-1) * DWDefs.DISK_SECTORSIZE  + bl];
			
			for (int i = 0; i<sectors.size() - 1;i++)
			{
				System.arraycopy(sectors.get(i).getData(), 0, res, i*DWDefs.DISK_SECTORSIZE, DWDefs.DISK_SECTORSIZE);
			}
		}
		
		// last sector is partial bytes
		
			
		if (bl > 0)
		{
			System.arraycopy(sectors.get(sectors.size()-1).getData(), 0, res, (sectors.size() - 1) * DWDefs.DISK_SECTORSIZE, bl);
			
		}
		
		return res;
	}
	
	
	public void addFile(String filename, byte[] filecontents) throws DWFileSystemFullException, DWFileSystemInvalidFilenameException, DWFileSystemFileNotFoundException, DWFileSystemInvalidFATException, IOException, DWDiskInvalidSectorNumber, DWFileSystemInvalidDirectoryException
	{
		DWDECBFileSystemFAT fat = getFAT();
		// make fat entries
		byte firstgran = fat.allocate(filecontents.length);
		
		// dir entry
		this.addDirectoryEntry(filename, firstgran, (byte) (filecontents.length % 256));
		
		// put content into sectors
		ArrayList<DWDiskSector> sectors = this.getFileSectors(filename);
		
		int byteswritten = 0;
		byte[] buf = new byte[256];
		
		for (DWDiskSector sector : sectors)
		{
			if (filecontents.length - byteswritten >= 256)
			{
				System.arraycopy(filecontents, byteswritten, buf, 0, 256);
				byteswritten += 256;
			}
			else
			{
				System.arraycopy(filecontents, byteswritten, buf, 0, (filecontents.length - byteswritten));
				// zero pad partial sectors?
				for (int i = (filecontents.length - byteswritten); i < 256; i++)
					buf[i] = 0;
				byteswritten += (filecontents.length - byteswritten);
			}
			
			sector.setData(buf);
			
		
		}
		
		
	}


	public DWDECBFileSystemFAT getFAT() throws DWFileSystemInvalidFATException, DWDiskInvalidSectorNumber
	{
		if (disk.getDiskSectors() < DECBDefs.FAT_OFFSET)
		{
			throw new DWFileSystemInvalidFATException("Image is too small to contain a FAT");
		}
		
		return(new DWDECBFileSystemFAT(disk.getSector(DECBDefs.FAT_OFFSET)));
	}

	private void addDirectoryEntry(String filename, byte firstgran, byte leftovers) throws DWFileSystemFullException, DWFileSystemInvalidFilenameException, IOException, DWDiskInvalidSectorNumber, DWFileSystemInvalidDirectoryException
	{
		List<DWFileSystemDirEntry> dr = this.getDirectory(null);
		
		int dirsize = 0;
		
		for (DWFileSystemDirEntry d : dr)
		{
			if (((DWDECBFileSystemDirEntry) d).isUsed())
				dirsize++;
		}
		
		if (dirsize > 67)
			throw (new DWFileSystemFullException("No free directory entries"));
			
		byte[] buf = new byte[32];
		byte[] secdata = new byte[256];
		
		DWDiskSector sec = this.disk.getSector((dirsize / 8) + DECBDefs.DIRECTORY_OFFSET);
		
		secdata = sec.getData();
		
		String[] fileparts = filename.split("\\.");

		if (fileparts.length != 2)
			throw (new DWFileSystemInvalidFilenameException("Invalid filename (parts) '" + filename + "' " + fileparts.length));
		
		String name = fileparts[0];
		String ext = fileparts[1];
		
		if ((name.length()<1) || (name.length()>8))
			throw (new DWFileSystemInvalidFilenameException("Invalid filename (name) '" + filename + "'"));
		
		if (ext.length() != 3)
			throw (new DWFileSystemInvalidFilenameException("Invalid filename (ext) '" + filename + "'"));
		
		while (name.length()<8)
			name += " ";
		
		System.arraycopy(name.getBytes(), 0 , buf, 0, 8);
		System.arraycopy(ext.getBytes(), 0, buf, 8, 3);
		
		
		// try to recognize filetype.. assume binary?
		DWDECBFileSystemDirExtensionMapping mapping = new DWDECBFileSystemDirExtensionMapping(ext, DECBDefs.FLAG_BIN, DECBDefs.FILETYPE_ML);
		
		if (DriveWireServer.serverconfig.getMaxIndex("DECBExtensionMapping") > -1)
		{
			for (int i = 0;i<=DriveWireServer.serverconfig.getMaxIndex("DECBExtensionMapping");i++)
			{
				String kp = "DECBExtensionMapping(" + i + ")";
				// validate entry first
				if (DriveWireServer.serverconfig.containsKey(kp + "[@extension]") && DriveWireServer.serverconfig.containsKey(kp + "[@ascii]") && DriveWireServer.serverconfig.containsKey(kp + "[@filetype]"))
				{
					if (DriveWireServer.serverconfig.getString(kp + "[@extension]").equalsIgnoreCase(ext))
					{
						// we have a winner
						
						mapping.setType(DriveWireServer.serverconfig.getByte(kp + "[@filetype]"));
						
						if (DriveWireServer.serverconfig.getBoolean(kp + "[@ascii]"))
							mapping.setFlag(DECBDefs.FLAG_ASCII);
						else
							mapping.setFlag(DECBDefs.FLAG_BIN);
						
					}
					
				}
				
				
			}
			
		}	

		// set our dirinfos
		buf[11] = mapping.getType();
		buf[12] = mapping.getFlag();
		
	
		
		buf[13] = firstgran;
		
		buf[14] = 0;
		buf[15] = leftovers;
				
		System.arraycopy(buf, 0 , secdata, (dirsize % 8) * 32, 32);
		
		sec.setData(secdata);
	
		
	}


	public void format() throws DWInvalidSectorException, DWSeekPastEndOfDeviceException, DWDriveWriteProtectedException, IOException
	{
		// just init to all FF (mess does this?)
		
		if (this.disk != null)
		{
			byte[] buf = new byte[256];
			
			for (int i = 0;i<256;i++)
				buf[i] = (byte) 0xFF;
			
			for (int i =0; i< 630;i++)
			{
				this.disk.seekSector(i);
				this.disk.writeSector(buf, false);
			}
			
			this.disk.seekSector(0);
			
			this.disk.setParam("_filesystem", DWUtils.prettyFileSystem(DWDiskDrives.getDiskFSType(this.disk.sectors)));
			
		}
		
	}

	

	@Override
	public String getFSName()
	{
		return FSNAME;
	}

	/*
	@Override
	public boolean isValidFS()
	{
		if (this.disk.getDiskSectors() == 630)
		{
			boolean wacky = false;
			
			try
			{
				List<DWDECBFileSystemDirEntry> dir = getDirectory();
				
				// look for wacky directory entries
				
				for (DWDECBFileSystemDirEntry e : dir)
				{
					
					if (e.getFirstGranule() > DECBDefs.FAT_SIZE)
					{
						this.fserrors.add(e.getFileName() + "." + e.getFileExt() + ": First granule of " + e.getFirstGranule() +  " is > FAT size");
						wacky = true;
					}
					// Some applications use their own filetypes, so remove this.. found .MUS = type 4
					//else if (e.getFileType() > 3)
					//{
					//	System.out.println(e.getFileName() + "." + e.getFileExt() + ": FileType of " + e.getFileType() + " is > 3..?");
					//	wacky = true;
					//}
					else if ( (e.getFileFlag() != 0) && (e.getFileFlag() != 255) ) 
					{
						this.fserrors.add(e.getFileName() + "." + e.getFileExt() + ": FileFlag of " + e.getFileFlag() + " is not defined..?");
						wacky = true;
					}
				}
				
				// look for wacky fat
				for (int i = 0; i < DECBDefs.FAT_SIZE;i++)
				{
					int val;
					
					try
					{
						val = (0xFF & this.disk.getSector(DECBDefs.FAT_OFFSET).getData()[i]);
						
						if (((val > DECBDefs.FAT_SIZE) && (val < 0xC0)) || ((val > 0xCF) && (val < 0xFF)))
						{
							this.fserrors.add("FAT entry #"+ i + " is " + val + ", which points beyond FAT");
							wacky = true;
						}
					} 
					catch (DWDiskInvalidSectorNumber e1)
					{
						this.fserrors.add("FAT sector missing: " + e1.getMessage());
						wacky = true;
					}
					
					
					
				}
			}
			catch (IOException e)
			{
				wacky = true;
				this.fserrors.add("IOError: " + e.getMessage());
			} 
			catch (DWFileSystemInvalidDirectoryException e)
			{
				wacky = true;
				this.fserrors.add(e.getMessage());
			}
			
			if (!wacky)
				return(true);
		}
		else
		{
			this.fserrors.add("Disk size of " + this.disk.getDiskSectors() + " doesn't match known DECB image size");
		}
		
		return false;
	}

	*/
	
	
	@Override
	public boolean isValidFS()
	{
		if (this.disk.getSectors().size() == 630)
		{
			boolean wacky = false;
			
			try
			{
				List<DWFileSystemDirEntry> dir = this.getDirectory(null);
				
				// look for wacky directory entries
				
				for (DWFileSystemDirEntry e : dir)
				{
					
					if (((DWDECBFileSystemDirEntry) e).getFirstGranule() > DECBDefs.FAT_SIZE)
					{
						this.fserrors.add(e.getFileName() + "." + e.getFileExt() + ": First granule of " + ((DWDECBFileSystemDirEntry) e).getFirstGranule() +  " is > FAT size");
						wacky = true;
					}
					// Some applications use their own filetypes, so remove this.. found .MUS = type 4
					//else if (e.getFileType() > 3)
					//{
					//	System.out.println(e.getFileName() + "." + e.getFileExt() + ": FileType of " + e.getFileType() + " is > 3..?");
					//	wacky = true;
					//}
					else if ( (((DWDECBFileSystemDirEntry) e).getFileFlag() != 0) && (((DWDECBFileSystemDirEntry) e).getFileFlag() != 255) ) 
					{
						this.fserrors.add(e.getFileName() + "." + e.getFileExt() + ": FileFlag of " + ((DWDECBFileSystemDirEntry) e).getFileFlag() + " is not defined..?");
						wacky = true;
					}
				}
				
				// look for wacky fat
				for (int i = 0; i < DECBDefs.FAT_SIZE;i++)
				{
					int val;
					
					try
					{
						val = (0xFF & this.disk.getSector(DECBDefs.FAT_OFFSET).getData()[i]);
						
						if (((val > DECBDefs.FAT_SIZE) && (val < 0xC0)) || ((val > 0xCF) && (val < 0xFF)))
						{
							this.fserrors.add("FAT entry #"+ i + " is " + val + ", which points beyond FAT");
							wacky = true;
						}
						
					} 
					catch (IOException e1)
					{
						this.fserrors.add(e1.getMessage());
						wacky = true;
					} 
					catch (DWDiskInvalidSectorNumber e)
					{
						wacky = true;
						this.fserrors.add(e.getMessage());
					}
					
				}
			}
			catch (IOException e)
			{
				wacky = true;
				this.fserrors.add(e.getMessage());
			} 
			catch (DWFileSystemInvalidDirectoryException e)
			{
				wacky = true;
				this.fserrors.add(e.getMessage());
			} 
			
			
			if (!wacky)
				return(true);
		}
		else
		{
			this.fserrors.add("Disk size doesn't match known DECB image size");
		}
		
		return false;
	}


	@Override
	public List<String> getFSErrors()
	{
		return this.fserrors;
	}
	
}
