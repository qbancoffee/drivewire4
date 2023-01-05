package com.groupunix.drivewireserver.dwdisk.filesystem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.groupunix.drivewireserver.DECBDefs;
import com.groupunix.drivewireserver.dwdisk.DWDiskSector;

import com.groupunix.drivewireserver.dwexceptions.DWFileSystemFullException;
import com.groupunix.drivewireserver.dwexceptions.DWFileSystemInvalidFATException;

public class DWDECBFileSystemFAT
{
	/*
	FILE ALLOCATION TABLE

	The  file allocation table (FAT)  is used to  keep track  of  whether or not a granule has been allocated  
	to  a  file or if it is free. The  FAT is composed of  six control bytes followed by  68   data bytes  —  
	one   byte for  each granule.  The  FAT is stored on  sector two   of the directory track (17). 
	A   RAM  image of  the FAT is  kept in the disk RAM   for each of the four possible  drives.  
	 Keeping  an  image of  the FAT in RAM   helps  speed up  the overall  operation of the DOS by  eliminating 
	 the need for 	disk  I/O every time the  DOS modifies the FAT.   Saving the  FAT to disk is  done
	approximately every  19   times that a  new  granule is  pulled from   the free granule reserve. 
	It is written  to  disk  whenever a  file is closed and   there  are some   DOS operations,  which force  
	the FAT to be   written to disk when  that DOS operation allocates a  free granule.

	Only   the DOS uses two   of the six  control bytes. The  first FAT control byte keeps track of  how  many  
	FCBs  are active  on  the drive for a  particular FAT.   This byte is used to preclude the loading in of the 
	FAT from   disk when  there is any active file  currently  using the FAT.   You  can   imagine the  disaster, 
	which  would occur if you   were creating  a  file and   had   allocated some   granules to  your new  file 
	but had   not saved the  new  FAT to disk when  the old FAT was   loaded into  RAM   on  top of the new  FAT.   
	Your   new  file would  be   hopelessly  gone. For   that  reason the DOS must   not allow the FAT to be   
	loaded into  RAM   from   disk while an  FCB is active for that FAT.

	The  second FAT control byte is  used to  govern the need to  write  data from   the FAT RAM   image to  the disk.  
	If  the value of this  byte is  zero it means   that  the FAT RAM   image  is an  exact  copy of  what   is  
	currently stored on  the disk. If  the value is non—zero,  it indicates  that the data in the FAT RAM   image 
	has  been changed since the last  time that the FAT was   written to disk.  The  number   stored in this  byte is
	an  indicator of how  many  granules  have been removed from   the FAT since the last  FAT to disk write. 
	Some  BASIC  commands, such  as  KILL,   cause an  immediate FAT RAM   image to disk write when  granules are either 
	freed or allocated.  Other  commands, which allocate  granules, increment the  second FAT control byte. This byte is  
	then 	compared  to the disk variable WFATVL   and   when  the second control  byte is >=  WFATVL, the FAT is  
	written to disk.

	The  FAT data bytes  are used to  determine whether or not a  granule is free and if it has been allocated  they are 
	used to  determine to  which file  the granule belongs. If a  data byte is  $FF, it means   that  the granule is free 
	and   may  be allocated to any   file. If a  granule has been allocated,  it is part of a  sector chain, which  defines  
	which granules belong to  a  certain file. The  only information required to be   able to  trace  the granule chain is 
	the number   of the first  granule 	in the chain. If the first  granule of  the chain is not  known, the  chain  cannot 
	be traced down  backwards.

	A  granule data byte,  which has been allocated,  will  contain a  value,  which is the number   of the next granule 
	in the granule chain for that file. If the two   most significant bits (6,7) of a  granule data byte  are set, then 
	that  granule is the last  granule in a  file’s granule chain.  The  low   order four bits  will  contain the
	number   of sectors in the last  granule,  which the  file uses. Even   though a  file may not use all  of the sectors 
	in the last  granule in the chain, no  other file may  use the sectors.  Disk space is  not allocated on  a  sector basis, 
	it  is allocated on  a granule basis  and   the granule may  not be   broken  down.  The  smallest one-byte  file
	will still  require a  full granule to be   allocated  in order to store the file.
*/

	
	
	private DWDiskSector sector;
	
	public DWDECBFileSystemFAT(DWDiskSector sector)
	{
		this.sector = sector;
	}

	public ArrayList<DWDiskSector> getFileSectors(Vector<DWDiskSector> sectors, byte granule) throws DWFileSystemInvalidFATException, IOException
	{
		ArrayList<DWDiskSector> res = new ArrayList<DWDiskSector>();
		
		byte entry = getEntry(granule);
		
		while (!this.isLastEntry(entry))
		{
			res.addAll(this.getGranuleSectors(sectors, granule));
			
			granule = entry;
			entry = getEntry(granule);
		}
		
		// last granule is partial, first 4 bits say how many sectors to read
		for (int i = 0;i < (entry & 0x0F);i++ )
		{
			res.add(sectors.get(getFirstSectorNoForGranule(granule)+i));
		}
		
		
		return(res);
	}
	
	
	public ArrayList<Byte> getFileGranules(byte granule) throws DWFileSystemInvalidFATException, IOException
	{
		ArrayList<Byte> res = new ArrayList<Byte>();
		
		while (!this.isLastEntry(granule))
		{
			res.add(granule);
			granule = getEntry(granule);
		}
		
		return(res);
	}
	
	
	
	private List<DWDiskSector> getGranuleSectors(Vector<DWDiskSector> sectors, byte granule)
	{
		List<DWDiskSector> res = new ArrayList<DWDiskSector>();
		
		for (int i = 0;i<9;i++)
		{
			res.add(sectors.get(this.getFirstSectorNoForGranule(granule) + i));
		}
		
		return res;
	}

	
	private int getFirstSectorNoForGranule(byte granule)
	{
		if ((granule & 0xFF) < 34)
			return (granule * 9);
		else
			// skip track 17 like this?
			return((granule + 2) * 9);
		
	}

	public byte getEntry(byte granule) throws DWFileSystemInvalidFATException, IOException
	{
	
		if (((granule & 0xFF) ) <= DECBDefs.FAT_SIZE)
			if ((this.sector.getData()[(granule & 0xFF) ] & 0xFF) == 0xFF)
				throw (new DWFileSystemInvalidFATException("Chain links to unused FAT entry #" + granule));
			else
				return(this.sector.getData()[(granule & 0xFF)]);
		else
			throw (new DWFileSystemInvalidFATException("Invalid granule #" + granule));
	}
	
	public byte getGranuleByte(byte granule) throws IOException
	{
		return this.sector.getData()[(granule & 0xFF)];
	}
	
	public boolean isLastEntry(byte entry)
	{
		if ((entry & 0xC0) == 0xC0)
			return true;
		
		return false;
		
	}

	public int getFreeGanules() throws IOException
	{
		int free =0;
		
		for (int i = 0;i< (DECBDefs.FAT_SIZE);i++)
			if ((this.sector.getData()[i] & 0xFF) == 0xFF)
				free++;
		
		return free;
	}

	public byte allocate(int bytes) throws DWFileSystemFullException, IOException
	{
		int allocated = 0;
	
		int sectorsneeded = (bytes / 256) + 1;

		int granulesneeded = sectorsneeded / 9;
		
		if (!(sectorsneeded % 9 == 0))
			granulesneeded++;
		
		// check for free space
		
		if (this.getFreeGanules() < granulesneeded)
			throw (new DWFileSystemFullException("Need " + granulesneeded + " granules, have only " + this.getFreeGanules() + " free."));
		
		
		byte lastgran = this.getFreeGranule();
		byte firstgran = lastgran;
		allocated++;
		
		while (allocated < granulesneeded)
		{
			this.setEntry(lastgran, (byte) 0xc0);
			byte nextgran = this.getFreeGranule();
			this.setEntry(lastgran, nextgran);
			
			lastgran = nextgran;
			allocated++;
		}
		
		this.setEntry(lastgran, (byte) ((sectorsneeded % 9) + 0xC0));
		
		return firstgran;
	}

	
	private void setEntry(byte gran, byte nextgran) throws IOException
	{
		this.sector.setDataByte((0xFF & gran), nextgran);
		this.sector.makeDirty();
	}

	private byte getFreeGranule() throws IOException
	{
		for (byte i = 0;i< (DECBDefs.FAT_SIZE);i++)
		{
			if ((this.sector.getData()[i] & 0xFF) == 0xFF)
				return(i);
		}
		return -1;
	}

	public String dumpFat() throws IOException
	{
		String res = "";
			
		for (int i = 0; i< DECBDefs.FAT_SIZE; i++)
			if (this.sector.getData()[i] != -1)
				res += i + ": " + this.sector.getData()[i] + "\t\t";
		
		return(res);
			
	}
	
	
	
}
