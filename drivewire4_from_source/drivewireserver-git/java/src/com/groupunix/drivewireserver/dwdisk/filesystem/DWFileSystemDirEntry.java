package com.groupunix.drivewireserver.dwdisk.filesystem;


public abstract class DWFileSystemDirEntry
{
	
	byte[] data;
	
	public DWFileSystemDirEntry(byte[] buf)
	{
		if (buf != null)
		{
			this.data = new byte[buf.length];
			System.arraycopy(buf, 0, this.data, 0, buf.length);
		}
	}

	public abstract String getFileName();
	
	public abstract String getFileExt();
	
	public abstract String getFilePath();
	
	public abstract String getPrettyFileType();
	
	public abstract int getFileType();
	
	public abstract DWFileSystemDirEntry getParentEntry();
	
	public abstract boolean isDirectory();
	
	public abstract boolean isAscii();
	
	@Override
	public String toString()
	{
		return getFileName() + "|" + getFileExt() + "|" + getFilePath() + "|" + getFileType() + "|" + isAscii() + "|" + isDirectory();
	}
}
