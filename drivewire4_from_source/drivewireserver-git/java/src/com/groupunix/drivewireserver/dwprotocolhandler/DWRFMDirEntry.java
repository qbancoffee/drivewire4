package com.groupunix.drivewireserver.dwprotocolhandler;

import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.FileType;


public class DWRFMDirEntry 
{
	String fileName; // 0 - 47
	
	// 48 - 52
	
	long fileSize = 0;  // 53 - 56
	
	byte filePerms = 0; // 57
	
	byte[] fileDate = new byte[6]; // 58 - 63


	public DWRFMDirEntry(FileObject fo) throws FileSystemException
	{
		// TODO sanity checks
		
		if (fo != null)
		{
			this.fileName = fo.getName().getBaseName();
			
			if (fo.getType() == FileType.FOLDER)
			{
				this.filePerms = (byte) 0x80;
			}
			
			if (fo.isReadable())
			{
				this.filePerms += (byte) 4;
			}
			
			if (fo.isWriteable())
			{
				this.filePerms += (byte) 2;
			}
			
			this.fileSize = fo.getContent().getSize();
			//long lmt = fo.getContent().getLastModifiedTime();
			
			// TODO lat mod time
		}
	}


	
	public byte[] getEntry()
	{
		byte[] res = new byte[64];
		
		if (this.fileName != null)
		{
			//TODO  incomplete
			
			System.arraycopy(this.fileName.getBytes() , 0,  res , 0,  Math.max(48, this.fileName.length()));
			
			res[57] = this.filePerms;
			
		}
		
		return res;
	}
}
