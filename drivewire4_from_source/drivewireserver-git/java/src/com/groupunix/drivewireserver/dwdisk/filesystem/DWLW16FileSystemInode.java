package com.groupunix.drivewireserver.dwdisk.filesystem;

public class DWLW16FileSystemInode
{

	private int	filetype;
	private int permissions;
	
	private int mode;
	private int links;				// number of links
	private int uid;				// uid of the owner
	private int gid;				// gid of the owner
	private int filesize;			// size of the file in bytes
	private int atime;				// last access time - updates optional
	private int mtime;				// last modify tim
	private int ctime;				// last status change time
	private int[] dblocks = new int[14];		// direct blocks
	private int[] indirblocks = new int[2];		// indirect blocks
	private int[] dblindirblocks = new int[4];	// double indirect blocks
	private int inodenum;
	
	public DWLW16FileSystemInode(int inodenum, byte[] data)
	{
		this.setInodenum(inodenum);
		
		this.setMode(get2(0,data));
		this.setLinks(get2(2,data));
		this.setUid(get2(4,data));
		this.setGid(get2(6,data));
		this.setFilesize(get4(8,data));
		this.setAtime(get4(12,data));
		this.setMtime(get4(16,data));
		this.setCtime(get4(20,data));
		
		for (int i = 0;i<14;i++)
			dblocks[i] = get2(24 + i*2, data);
		
		for (int i = 0;i<2;i++)
			indirblocks[i] = get2(52 + i*2, data);
		
		for (int i = 0;i<4;i++)
			dblindirblocks[i] = get2(56 + i*2, data);
		
	}

	
	@Override
	public String toString()
	{
		String res = new String();
		
		res += "inode " + this.getInodenum() + System.getProperty("line.separator");
		res += "mode  " + this.getMode() + System.getProperty("line.separator");
		res += "links " + this.getLinks() + System.getProperty("line.separator");
		res += "uid   " + this.getUid() + System.getProperty("line.separator");
		res += "gid   " + this.getGid() + System.getProperty("line.separator");
		
		res += "size  " + this.getFilesize() + System.getProperty("line.separator");
		
		res += "atime " + this.getAtime() + System.getProperty("line.separator");
		res += "mtime " + this.getMtime() + System.getProperty("line.separator");
		res += "ctime " + this.getCtime() + System.getProperty("line.separator");
		
		return res;
	}
	
	
	private int get4(int i, byte[] data)
	{
		return ((0xff & data[i]) * 256 * 256 * 256) + ((0xff & data[i+1]) * 256 * 256) + ((0xff & data[i+2]) * 256) + (0xff & data[i+3]);
	}

	private int get2(int i, byte[] data)
	{
		return ((0xff & data[i]) * 256) + (0xff & data[i+1]);
	}

	public void setFiletype(int filetype)
	{
		this.filetype = filetype;
	}

	public int getFiletype()
	{
		return filetype;
	}

	public void setPermissions(int permissions)
	{
		this.permissions = permissions;
	}

	public int getPermissions()
	{
		return permissions;
	}

	public void setMode(int mode)
	{
		this.mode = mode;
	}

	public int getMode()
	{
		return mode;
	}

	public void setLinks(int links)
	{
		this.links = links;
	}

	public int getLinks()
	{
		return links;
	}

	public void setUid(int uid)
	{
		this.uid = uid;
	}

	public int getUid()
	{
		return uid;
	}

	public void setGid(int gid)
	{
		this.gid = gid;
	}

	public int getGid()
	{
		return gid;
	}

	public void setFilesize(int filesize)
	{
		this.filesize = filesize;
	}

	public int getFilesize()
	{
		return filesize;
	}

	public void setAtime(int atime)
	{
		this.atime = atime;
	}

	public int getAtime()
	{
		return atime;
	}

	public void setMtime(int mtime)
	{
		this.mtime = mtime;
	}

	public int getMtime()
	{
		return mtime;
	}

	public void setCtime(int ctime)
	{
		this.ctime = ctime;
	}

	public int getCtime()
	{
		return ctime;
	}

	public void setInodenum(int inodenum)
	{
		this.inodenum = inodenum;
	}

	public int getInodenum()
	{
		return inodenum;
	}
	
}
