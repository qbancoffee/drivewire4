package com.groupunix.drivewireserver.dwdisk.filesystem;


public class DWLW16FileSystemDirEntry extends DWFileSystemDirEntry
{

	private String filename;
	private DWLW16FileSystemInode inode;
	
	

	public DWLW16FileSystemDirEntry(String fn, DWLW16FileSystemInode inode)
	{
		super(null);
		
		this.setInode(inode);
		this.filename = fn;
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
		
		return false;
	}

	@Override
	public boolean isAscii()
	{
		// TODO Auto-generated method stub
		return false;
	}

	public void setInode(DWLW16FileSystemInode inode)
	{
		this.inode = inode;
	}

	public DWLW16FileSystemInode getInode()
	{
		return inode;
	}

	
}
