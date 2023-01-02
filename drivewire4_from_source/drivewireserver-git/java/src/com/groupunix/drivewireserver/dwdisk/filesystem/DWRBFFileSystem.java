package com.groupunix.drivewireserver.dwdisk.filesystem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.groupunix.drivewireserver.dwdisk.DWDisk;
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


public class DWRBFFileSystem extends DWFileSystem
{

	private static final String FSNAME = "RBF";
	
	public DWRBFFileSystem(DWDisk disk)
	{
		super(disk);
		
	}

	@Override
	public List<DWFileSystemDirEntry> getDirectory(String path) throws IOException, DWFileSystemInvalidDirectoryException
	{
		ArrayList<DWFileSystemDirEntry> res = new ArrayList<DWFileSystemDirEntry>();
		
		try
		{
			for (DWRBFFileSystemDirEntry entry : this.getDirectoryFromFD( this.getFDFromPath(path)))
			{
				res.add(entry);
			}
		} 
		catch (DWDiskInvalidSectorNumber e)
		{
			throw new DWFileSystemInvalidDirectoryException(e.getMessage());
		} 
		catch (DWFileSystemFileNotFoundException e)
		{
			throw new DWFileSystemInvalidDirectoryException(e.getMessage());
		}
		
		return res;
		
	}

	@Override
	public boolean hasFile(String filename) throws IOException
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ArrayList<DWDiskSector> getFileSectors(String filename)
			throws DWFileSystemFileNotFoundException,
			DWFileSystemInvalidFATException, IOException,
			DWDiskInvalidSectorNumber, DWFileSystemInvalidDirectoryException
	{
		// TODO Auto-generated method stub
		return null;
	}

	
	public DWRBFFileSystemDirEntry getDirEntry(String filename)	throws DWFileSystemFileNotFoundException, IOException, DWFileSystemInvalidDirectoryException
	{
		
		//DWRBFFileSystemDirEntry res = new DWRBFFileSystemDirEntry(null);
		
		
		return null;
	}

	
	@Override
	public byte[] getFileContents(String filename) throws DWFileSystemFileNotFoundException, DWFileSystemInvalidFATException, IOException,	DWDiskInvalidSectorNumber, DWFileSystemInvalidDirectoryException
	{
		return this.getFileContentsFromDescriptor(getFDFromPath(filename));
	}

	
	public DWRBFFileDescriptor getFDFromPath(String filename) throws IOException, DWDiskInvalidSectorNumber, DWFileSystemFileNotFoundException, DWFileSystemInvalidDirectoryException
	{
		if (filename == null)
		{
			return (new DWRBFFileDescriptor(this.disk.getSector(this.getRootDirectoryLSN()).getData()));
		}
		
		
		String[] path = filename.split("/");
		ArrayList<DWRBFFileSystemDirEntry> dir = this.getRootDirectory();
		DWRBFFileDescriptor res = null;
		
		for (int i = 0;i<path.length;i++)
		{
			res = null;
			int j = 0;
			
			while ((j<dir.size()) && (res == null))
			{
				if (dir.get(j).getFileName().equals(path[i]))
				{
					res = dir.get(j).getFD();
				}
				j++;
			}
			
			if (res == null)
				throw new DWFileSystemFileNotFoundException("File not found: " + filename);
		}
		
		return res;
	}

	
	
	@Override
	public void addFile(String filename, byte[] filecontents)
			throws DWFileSystemFullException,
			DWFileSystemInvalidFilenameException,
			DWFileSystemFileNotFoundException, DWFileSystemInvalidFATException,
			IOException, DWDiskInvalidSectorNumber,
			DWFileSystemInvalidDirectoryException
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void format() throws DWInvalidSectorException,
			DWSeekPastEndOfDeviceException, DWDriveWriteProtectedException,
			IOException
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getFSName()
	{
		return FSNAME;
	}

	@Override
	public boolean isValidFS()
	{
		try
		{
			RBFFileSystemIDSector idsec = new RBFFileSystemIDSector(disk.getSector(0).getData());
			
			int ddmap = (Integer) idsec.getAttrib("DD.MAP"); 
			int ddbit = (Integer) idsec.getAttrib("DD.BIT");
			
			if  ((ddbit > 0) && (Math.abs(((Integer) idsec.getAttrib("DD.TOT")) - (ddmap / ddbit * 8)) < 8)) 
			{	
				return(true);
			}
			else
			{
				//fserrors.add("DD.TOT != DD.MAP /DD.BIT * 8: tot " + (Integer) idsec.getAttrib("DD.TOT") + " vs  map " + ddmap + " bit " + ddbit);
			}
			
		} 
		catch (IOException e)
		{
			//fserrors.add(e.getMessage());
		} 
		catch (DWDiskInvalidSectorNumber e)
		{
		}
		
		return false;
	}

	@Override
	public List<String> getFSErrors()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public RBFFileSystemIDSector getIDSector() throws IOException, DWDiskInvalidSectorNumber
	{
		return(new RBFFileSystemIDSector(disk.getSector(0).getData()));
	}
	
	public byte[] getSectorAllocationMap() throws IOException, DWDiskInvalidSectorNumber
	{
		RBFFileSystemIDSector idsec = this.getIDSector();
		int mapbytes = Integer.parseInt(idsec.getAttrib("DD.MAP").toString()); 
		
		byte[] res = new byte[mapbytes];
		int lsn = 1;
		int bytesread_tot = 0;
	
		while (bytesread_tot < mapbytes)
		{
			DWDiskSector sector = this.disk.getSector(lsn);
			int toread = Math.min(256, mapbytes - bytesread_tot);
			System.arraycopy(sector.getData(),0, res, bytesread_tot, toread);
			bytesread_tot += toread;
		}
		
		return res;
	}

	public ArrayList<DWRBFFileSystemDirEntry> getRootDirectory() throws IOException, DWDiskInvalidSectorNumber, DWFileSystemInvalidDirectoryException
	{
		ArrayList<DWRBFFileSystemDirEntry> dir = new ArrayList<DWRBFFileSystemDirEntry>();
		
		try
		{
			int rootsec = this.getRootDirectoryLSN();
			
			DWRBFFileDescriptor fd = new DWRBFFileDescriptor(this.disk.getSector(rootsec).getData());
			
			dir = this.directoryFromContents(this.getFileContentsFromDescriptor(fd));
			
		}
		catch (NumberFormatException e)
		{
			throw new DWDiskInvalidSectorNumber(e.getMessage());
		}
		
		return dir;
	}
	
	
	private int getRootDirectoryLSN() throws DWDiskInvalidSectorNumber, IOException
	{
		int rootsec = -1;
		
		try
		{
			rootsec = Integer.parseInt(this.getIDSector().getAttrib("DD.DIR").toString());
			
		}
		catch (NumberFormatException e)
		{
			throw new DWDiskInvalidSectorNumber(e.getMessage());
		}
		
		return rootsec;
	}

	public ArrayList<DWRBFFileSystemDirEntry> getDirectoryFromFD(DWRBFFileDescriptor fd) throws IOException, DWDiskInvalidSectorNumber, DWFileSystemInvalidDirectoryException
	{
		
		return this.directoryFromContents(this.getFileContentsFromDescriptor(fd));
	}
	

	private ArrayList<DWRBFFileSystemDirEntry> directoryFromContents(byte[] data) throws IOException, DWDiskInvalidSectorNumber
	{
		ArrayList<DWRBFFileSystemDirEntry> res = new ArrayList<DWRBFFileSystemDirEntry>();
		
		for (int i = 0;i< data.length/32;i++)
		{
			byte[] entry = new byte[32];
			System.arraycopy(data, i*32, entry, 0, 32);
			
			if (entry[0] != 0)
			{
				int lsn = (entry[29] & 0xFF) * 256 * 256 + (entry[30] & 0xFF) * 256 + (entry[31] & 0xFF);
				
				DWRBFFileDescriptor fd = new DWRBFFileDescriptor(this.disk.getSector(lsn).getData());
				
				res.add(new DWRBFFileSystemDirEntry(DWUtils.OS9String(entry), lsn , fd));
			}
		}
		
		return res;
	}

	public byte[] getFileContentsFromDescriptor(DWRBFFileDescriptor fd) throws IOException, DWDiskInvalidSectorNumber, DWFileSystemInvalidDirectoryException
	{
		if (fd.getFilesize() < 0)
		{
			throw new DWFileSystemInvalidDirectoryException("Negative file size?");
		}
		
		byte[] res = new byte[fd.getFilesize()];
		
		int bytesread = 0;
		int segmentsread = 0;
		
		while ((bytesread < res.length) && (segmentsread < 48))
		{
			int lsn = fd.getSegmentList()[segmentsread].getLsn();
			int siz = fd.getSegmentList()[segmentsread].getSize();
				
			//System.out.println("size: " + res.length + " read: " + bytesread +   "  lsn: " + lsn + "  siz: " + siz + " toread: " + toread);
			
			int i = lsn;
			
			while ((i<lsn+siz) && (bytesread < res.length))
			{
				int toread = Math.min(256, res.length - bytesread); 
				System.arraycopy(this.disk.getSector(i).getData(), 0, res, bytesread, toread);
				bytesread += toread;
				i++;
			}
			
			segmentsread++;
			
		}
		
		
		return res;
	}

	public DWDiskSector getSector(int no) throws DWDiskInvalidSectorNumber
	{
		return this.disk.getSector(no);
		
	}
	

}
