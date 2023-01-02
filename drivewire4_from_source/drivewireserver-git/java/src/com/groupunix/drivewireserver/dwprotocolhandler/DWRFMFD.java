package com.groupunix.drivewireserver.dwprotocolhandler;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.FileSystemManager;
import org.apache.commons.vfs.FileType;
import org.apache.commons.vfs.VFS;
import org.apache.log4j.Logger;

import com.groupunix.drivewireserver.OS9Defs;

public class DWRFMFD
{

	/*
	 * ************************
* File Descriptor Format
*
* The file descriptor is a sector that is present for every file
* on an RBF device.  It contains attributes, modification dates,
* and segment information on a file.
*
               ORG       0
FD.ATT         RMB       1                   Attributes
FD.OWN         RMB       2                   Owner
FD.DAT         RMB       5                   Date last modified
FD.LNK         RMB       1                   Link count
FD.SIZ         RMB       4                   File size
FD.Creat       RMB       3                   Segment list extension
FD.SEG         EQU       .                   Beginning of segment list
* Segment List Entry Format
               ORG       0
FDSL.A         RMB       3                   Segment beginning physical sector number
FDSL.B         RMB       2                   Segment size
FDSL.S         EQU       .                   Segment list entry size
FD.LS1         EQU       FD.SEG+((256-FD.SEG)/FDSL.S-1)*FDSL.S
FD.LS2         EQU       (256/FDSL.S-1)*FDSL.S
MINSEC         SET       16
	 */

	
	
	private byte ATT;
	private byte[] OWN;
	private byte[] DAT;
	private byte LNK;
	private byte[] SIZ;
	private byte[] Creat;

	private String pathstr;
	
	private FileSystemManager fsManager;
	private FileObject fileobj;
	
	private static final Logger logger = Logger.getLogger("DWServer.DWRFMFD");
	
	
	public DWRFMFD(String pathstr) throws FileSystemException
	{
		this.pathstr = pathstr;
		
		this.fsManager = VFS.getManager();
		this.fileobj = this.fsManager.resolveFile(pathstr);

		logger.info("New FD for '" + pathstr + "'");
		
	}


	public byte[] getFD()
	{
		byte[] b = new byte[256];
		
		for (int i = 0;i<256;i++)
		{
			b[i] = 0;
		}
		
		b[0] = getATT();
		System.arraycopy(getOWN(), 0, b, 1, 2);
		System.arraycopy(getDAT(), 0, b, 3, 5);
		b[8] = getLNK();
		System.arraycopy(getSIZ(), 0, b, 9, 4);
		System.arraycopy(getCreat(), 0, b, 13, 3);
				
		return(b);
		
	}
	
	public void setFD(byte[] fd)
	{
		byte[] b = new byte[5];
		
		setATT(fd[0]);
		
		System.arraycopy(fd, 1, b, 0, 2);
		setOWN(b);
		
		System.arraycopy(fd, 3, b, 0, 5);
		setDAT(b);
		
		setLNK(fd[8]);
		
		System.arraycopy(fd, 9, b, 0, 4);
		setSIZ(b);
		
		System.arraycopy(fd, 13, b, 0, 3);
		setCreat(b);
		
	}

	
	public void writeFD()
	{
		
		
	/*	if (this.fileobj.exists())
		{
			if (this.fileobj.isWriteable())
			{
				// we only write attributes + mod time
			
			
				// for now.. user = public.. if either is set, we set on file 
			
				if ( ((getATT() & OS9Defs.MODE_R) == OS9Defs.MODE_R) || ((getATT() & OS9Defs.MODE_PR) == OS9Defs.MODE_PR))
				{
					fileobj.getContent().setAttribute(arg0, arg1)
				}
				f.setReadable(true);
			}
			else
			{
				f.setReadable(false);
			}
			
			if ( ((getATT() & OS9Defs.MODE_W) == OS9Defs.MODE_W) || ((getATT() & OS9Defs.MODE_PW) == OS9Defs.MODE_PW))
			{
				f.setWritable(true);
			}
			else
			{
				f.setWritable(false);
			}
			
			if ( ((getATT() & OS9Defs.MODE_E) == OS9Defs.MODE_E) || ((getATT() & OS9Defs.MODE_PE) == OS9Defs.MODE_PE))
			{
				f.setExecutable(true);
			}
			else
			{
				f.setExecutable(false);
			}
			
			
			// date and time modified
			
			f.setLastModified(bytesToTime(getDAT()));
			
		}
		else
		{
			logger.error("attempt to write FD for non existent file '" + this.pathstr + "'");
		}
		*/
	}
	


	public void readFD() throws FileSystemException
	{
		setOWN( new byte[] {0,0} );
		setLNK((byte)1);
		
		if (this.fileobj.exists())
		{
			// attributes
			
			byte tmpmode = 0;
			
			// for now.. user = public 
			
			if (fileobj.isReadable())
				tmpmode += OS9Defs.MODE_R + OS9Defs.MODE_PR;
			
			if (fileobj.isWriteable())
				tmpmode += OS9Defs.MODE_W + OS9Defs.MODE_PW;
			
			// everything is executable for now
				tmpmode += OS9Defs.MODE_E + OS9Defs.MODE_PE;
			
			if (fileobj.getType() == FileType.FOLDER)
				tmpmode += OS9Defs.MODE_DIR;
			
			
			setATT(tmpmode);
						
			// date and time modified
			
			setDAT(timeToBytes(fileobj.getContent().getLastModifiedTime()));
			
			// size
			setSIZ(lengthToBytes(fileobj.getContent().getSize()));
			
			
			
			// date created (java doesn't know)
			setCreat(new byte[] {0,0,0});
		}
		else
		{
			logger.error("attempt to read FD for non existant file '" + this.pathstr + "'");
		}
		
	}

	
	
	private byte[] lengthToBytes(long length)
	{
		double maxlen = Math.pow( 256, 4) / 2;
		
		if (length > maxlen)
		{
			logger.error("File too big: " + length + " bytes in '" + this.pathstr + "' (max " + maxlen + ")" );
			return(new byte[] {0,0,0,0});
		}
		
		byte [] b = new byte[4];  
		for(int i= 0; i < 4; i++)
		{  
		    b[3 - i] = (byte)(length >>> (i * 8));  
		}  
		
		return(b);
	}



	private byte[] timeToBytes(long time)
	{
		GregorianCalendar c = new GregorianCalendar();
		
		c.setTime(new Date(time));
		
		byte[] b = new byte[5];
		
		b[0] = (byte)(c.get(Calendar.YEAR)-108);
		b[1] = (byte)(c.get(Calendar.MONTH)+1);
		b[2] = (byte)(c.get(Calendar.DAY_OF_MONTH));
		b[3] = (byte)(c.get(Calendar.HOUR_OF_DAY));
		b[4] = (byte)(c.get(Calendar.MINUTE));
		
		return(b);
	}

	@SuppressWarnings("unused")
	private long bytesToTime(byte[] b)
	{

		GregorianCalendar c = new GregorianCalendar();
		
		c.set(Calendar.YEAR, b[0] + 108);
		c.set(Calendar.MONTH, b[1]-1);
		c.set(Calendar.DAY_OF_MONTH, b[2]);
		c.set(Calendar.HOUR_OF_DAY,b[3]);
		c.set(Calendar.MINUTE,b[4]);
		
		return c.getTimeInMillis();
	}

	

	public void setATT(byte aTT)
	{
		ATT = aTT;
	}



	public byte getATT()
	{
		return ATT;
	}



	public void setOWN(byte[] oWN)
	{
		OWN = oWN;
	}



	public byte[] getOWN()
	{
		return OWN;
	}



	public void setDAT(byte[] dAT)
	{
		DAT = dAT;
	}



	public byte[] getDAT()
	{
		return DAT;
	}



	public void setLNK(byte lNK)
	{
		LNK = lNK;
	}



	public byte getLNK()
	{
		return LNK;
	}



	public void setSIZ(byte[] sIZ)
	{
		SIZ = sIZ;
	}



	public byte[] getSIZ()
	{
		return SIZ;
	}



	public void setCreat(byte[] creat)
	{
		Creat = creat;
	}



	public byte[] getCreat()
	{
		return Creat;
	}

}
