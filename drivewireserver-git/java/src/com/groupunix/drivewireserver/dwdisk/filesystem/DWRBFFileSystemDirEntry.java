package com.groupunix.drivewireserver.dwdisk.filesystem;

import com.groupunix.drivewireserver.dwprotocolhandler.DWUtils;


public class DWRBFFileSystemDirEntry extends DWFileSystemDirEntry
{

	private String filename;
	private int fdlsn;
	private DWRBFFileDescriptor fd;

	public DWRBFFileSystemDirEntry(String fn, int fd_lsn, DWRBFFileDescriptor fd)
	{
		super(null);
		
		this.filename = fn;
		this.setFDLSN(fd_lsn);
		this.setFD(fd);
	}

	@Override
	public String getFileName()
	{
		return this.filename;
	}

	@Override
	public String getFileExt()
	{
		String res = "";
		
		int dot = this.filename.lastIndexOf(".");
		
		if ((dot > 0) && (dot < this.filename.length()-1))
		{
			res = this.filename.substring(dot+1);
		}
		
		return res;
	}

	@Override
	public String getFilePath()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPrettyFileType()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getFileType()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public DWFileSystemDirEntry getParentEntry()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isDirectory()
	{
		if ((this.fd.getAttributes() & 0x80) == 0x80)
			return true;
		
		return false;
	}

	@Override
	public boolean isAscii()
	{
		// TODO Auto-generated method stub
		return false;
	}

	public void setFD(DWRBFFileDescriptor fd)
	{
		this.fd = fd;
	}

	public DWRBFFileDescriptor getFD()
	{
		return fd;
	}

	public void setFDLSN(int fdlsn)
	{
		this.fdlsn = fdlsn;
	}

	public int getFDLSN()
	{
		return fdlsn;
	}

	public String getPrettyDateModified()
	{
		return DWUtils.pretty5ByteDateTime(this.fd.getDateModified());
	}

	public String getPrettyDateCreated()
	{
		 return DWUtils.pretty3ByteDate(this.fd.getDateCreated());
	}

}
