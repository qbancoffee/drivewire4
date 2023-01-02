package com.groupunix.drivewireserver.dwdisk.filesystem;

public class DWDECBFileSystemDirExtensionMapping
{
	private String extension;
	private byte flag;
	private byte type;
	
	public DWDECBFileSystemDirExtensionMapping(String ext, byte flag, byte type)
	{
		this.extension = ext;
		this.flag = flag;
		this.type = type;
	}
	
	public void setExtension(String extension)
	{
		this.extension = extension;
	}
	
	public String getExtension()
	{
		return extension;
	}
	
	public void setFlag(byte flag)
	{
		this.flag = flag;
	}
	
	public byte getFlag()
	{
		return flag;
	}
	
	public void setType(byte type)
	{
		this.type = type;
	}
	
	public byte getType()
	{
		return type;
	}
	
		
}
