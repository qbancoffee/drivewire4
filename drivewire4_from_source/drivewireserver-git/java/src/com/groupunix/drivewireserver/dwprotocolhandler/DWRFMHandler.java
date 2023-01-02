package com.groupunix.drivewireserver.dwprotocolhandler;

import java.io.IOException;

import org.apache.commons.vfs.FileSystemException;
import org.apache.log4j.Logger;

import com.groupunix.drivewireserver.OS9Defs;
import com.groupunix.drivewireserver.dwexceptions.DWCommTimeOutException;

public class DWRFMHandler
{
	private static final Logger logger = Logger.getLogger("DWServer.DWRFMHandler");
	
	// RFM opcodes
	public static final byte RFM_OP_CREATE = (byte) 1;
	public static final byte RFM_OP_OPEN = (byte) 2;
	public static final byte RFM_OP_MAKDIR = (byte) 3;
	public static final byte RFM_OP_CHGDIR = (byte) 4;
	public static final byte RFM_OP_DELETE = (byte) 5;
	public static final byte RFM_OP_SEEK = (byte) 6;
	public static final byte RFM_OP_READ = (byte) 7;
	public static final byte RFM_OP_WRITE = (byte) 8;
	public static final byte RFM_OP_READLN = (byte) 9;
	public static final byte RFM_OP_WRITLN = (byte) 10;
	public static final byte RFM_OP_GETSTT = (byte) 11;
	public static final byte RFM_OP_SETSTT = (byte) 12;
	public static final byte RFM_OP_CLOSE = (byte) 13;
	
	
	
	
	private DWRFMPath[] paths = new DWRFMPath[256];
	
	private int handlerno;
	
	public DWRFMHandler(int handlerno)
	{
		logger.debug("init for handler #" + handlerno);
		this.handlerno = handlerno;
	}
	
	public void DoRFMOP(DWProtocolDevice protodev, int rfm_op)
	{
		switch (rfm_op)
		{
			case RFM_OP_CREATE:
				DoOP_RFM_CREATE(protodev);
				break;
			case RFM_OP_OPEN:
				DoOP_RFM_OPEN(protodev);
				break;
			case RFM_OP_MAKDIR:
				DoOP_RFM_MAKDIR();
				break;
			case RFM_OP_CHGDIR:
				DoOP_RFM_CHGDIR();
				break;
			case RFM_OP_DELETE:
				DoOP_RFM_DELETE();
				break;
			case RFM_OP_SEEK:
				DoOP_RFM_SEEK(protodev);
				break;
			case RFM_OP_READ:
				DoOP_RFM_READ(protodev);
				break;
			case RFM_OP_WRITE:
				DoOP_RFM_WRITE();
				break;
			case RFM_OP_READLN:
				DoOP_RFM_READLN(protodev);
				break;
			case RFM_OP_WRITLN:
				DoOP_RFM_WRITLN(protodev);
				break;
			case RFM_OP_GETSTT:
				DoOP_RFM_GETSTT(protodev);
				break;
			case RFM_OP_SETSTT:
				DoOP_RFM_SETSTT(protodev);
				break;
			case RFM_OP_CLOSE:
				DoOP_RFM_CLOSE(protodev);
				break;
			
		}
	}
	
	
	private void DoOP_RFM_CLOSE(DWProtocolDevice protodev)
	{
		logger.debug("CLOSE");
		
		// read path #
		try
		{
			int pathno = protodev.comRead1(true);
			
			if (this.paths[pathno] == null)
			{
				logger.error("close on null path: " + pathno);
			}
			else
			{
				this.paths[pathno].close();
				this.paths[pathno] = null;
			}
			
			// send response
			protodev.comWrite1(0, true);
			
		} 
		catch (IOException  e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DWCommTimeOutException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}


	private void DoOP_RFM_SETSTT(DWProtocolDevice protodev)
	{
		logger.debug("SETSTT");
		
		// read path #
		try
		{
			int pathno = protodev.comRead1(true);
			
			// read call
			int call = protodev.comRead1(true);
			
			logger.debug("SETSTT path " + pathno + " call " + call);
			
			switch (call)
			{
				case OS9Defs.SS_FD:
					setSTT_FD(protodev, pathno);
					break;
					
					
			}
			
		}
		catch (IOException  e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DWCommTimeOutException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
	}


	private void DoOP_RFM_GETSTT(DWProtocolDevice protodev)
	{
		logger.debug("GETSTT");
		
		// read path #
		try
		{
			int pathno = protodev.comRead1(true);
			
			// read call
			int call = protodev.comRead1(true);
			
			logger.debug("GETSTT path " + pathno + " call " + call);
		
			switch (call)
			{
				case OS9Defs.SS_FD:
					getSTT_FD(protodev, pathno);
					break;
				
				case OS9Defs.SS_DirEnt:
					setSTT_FD(protodev, pathno);
					break;
				
					
			}
			
			
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DWCommTimeOutException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
	}


	private void getSTT_FD(DWProtocolDevice protodev, int pathno)
	{
		logger.debug("getstt_fd");
		
		// read # bytes wanted
		
		try
		{
			int size = DWUtils.int2(protodev.comRead(2));
			
			byte[] buf = new byte[size];
			
			try
			{
				buf = this.paths[pathno].getFd(size);
			} catch (FileSystemException e)
			{
				logger.error("Failed to get FD for path " + pathno);
			}
			
			
			protodev.comWrite(buf,size, true);
			
			logger.debug("sent " + size +" bytes of FD for path " + pathno);
		} 
		catch (IOException  e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DWCommTimeOutException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	private void setSTT_FD(DWProtocolDevice protodev, int pathno)
	{
		logger.debug("getstt_fd");
		
		// read # bytes coming
		
		try
		{
			int size = DWUtils.int2(protodev.comRead(2));
			
			byte[] buf = new byte[size];
			
			buf = protodev.comRead(size);
			
			try
			{
				this.paths[pathno].setFd(buf);
			} catch (FileSystemException e)
			{
				logger.error("Failed to set FD on path " + pathno);
			}
			
			logger.debug("read " + size +" bytes of FD for path " + pathno);
		} 
		catch (IOException  e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DWCommTimeOutException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	

	private void DoOP_RFM_WRITLN(DWProtocolDevice protodev)
	{
		logger.debug("WRITLN");	
		
		// read path #
		try
		{
			int pathno = protodev.comRead1(true);
			
			// read sending bytes
			byte[] maxbytesb = new byte[2];
			
			maxbytesb= protodev.comRead(2);
			
			int maxbytes = DWUtils.int2(maxbytesb);
			
			// read bytes
			byte[] buf = new byte[maxbytes];
			
			buf = protodev.comRead(maxbytes);
			
			// write to file
			this.paths[pathno].writeBytes(buf,maxbytes);
			this.paths[pathno].incSeekpos(maxbytes);
			
			logger.debug("writln on path " + pathno + " bytes: " + maxbytes);
		} 
		catch (IOException  e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DWCommTimeOutException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
	}


	private void DoOP_RFM_READLN(DWProtocolDevice protodev)
	{
		logger.debug("READLN");
		
		// read path #
		try
		{
			int pathno = protodev.comRead1(true);
			
			// read max bytes
			byte[] maxbytesb = new byte[2];
			
			maxbytesb= protodev.comRead(2);
			
			int maxbytes = DWUtils.int2(maxbytesb);
			
			int availbytes = this.paths[pathno].getBytesAvail(maxbytes);

			logger.debug("initial AB: " + availbytes);
			
			byte[] buf = new byte[availbytes];
			
			System.arraycopy(this.paths[pathno].getBytes(availbytes),0, buf, 0, availbytes);
			
			// find $0D or end
			int x = 0;
			while (x < availbytes)
			{
				if (buf[x] == (byte)13)
				{
					availbytes = x+1;
				}
				x++;
			}
			
			logger.debug("adjusted AB: " + availbytes);
			
			protodev.comWrite1(availbytes, true);
			
			if (availbytes > 0)
			{
				// possible prefix needed?
				protodev.comWrite(buf, availbytes, false);
				this.paths[pathno].incSeekpos(availbytes);
			}
			
			logger.debug("readln on path " + pathno + " maxbytes: " + maxbytes + " availbytes: " + availbytes );
		} 
		catch (IOException  e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DWCommTimeOutException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		
	}


	private void DoOP_RFM_WRITE()
	{
		logger.debug("WRITE");		
	}


	private void DoOP_RFM_READ(DWProtocolDevice protodev)
	{
		logger.debug("READ");
		
		// read path #
		try
		{
			int pathno = protodev.comRead1(true);
			
			// read max bytes
			byte[] maxbytesb = new byte[2];
			
			maxbytesb= protodev.comRead(2);
			
			int maxbytes = DWUtils.int2(maxbytesb);
			
			int availbytes = this.paths[pathno].getBytesAvail(maxbytes);
			
			if (maxbytes > availbytes)
			{
				maxbytes = availbytes;
			}
			
			byte[] buf = new byte[maxbytes];
			
			System.arraycopy(this.paths[pathno].getBytes(maxbytes),0, buf, 0, maxbytes);
			
			protodev.comWrite1(maxbytes, true);
			
			if (maxbytes > 0)
			{
				// possible prefix needed
				protodev.comWrite(buf, maxbytes, false);
				this.paths[pathno].incSeekpos(maxbytes);
				
				logger.debug("buf: " + DWUtils.byteArrayToHexString(buf));
				
			}
			
			logger.debug("read on path " + pathno + " maxbytes: " + maxbytes );
		} 
		catch (IOException  e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DWCommTimeOutException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		
		
	}


	private void DoOP_RFM_SEEK(DWProtocolDevice protodev)
	{
		logger.debug("SEEK");
		try
		{
			int pathno = protodev.comRead1(true);
			
			// read seek pos
			byte[] seekpos = new byte[4];
			
			
			seekpos = protodev.comRead(4);
			
			this.paths[pathno].setSeekpos(DWUtils.int4(seekpos));
			
			// assume it worked, for now
			protodev.comWrite1(0, true);
			
		}
		catch (IOException  e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DWCommTimeOutException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
			
	}


	private void DoOP_RFM_DELETE()
	{
		logger.debug("DELETE");		
	}


	private void DoOP_RFM_CHGDIR()
	{
		logger.debug("CHGDIR");
	}


	private void DoOP_RFM_MAKDIR()
	{
		logger.debug("MAKDIR");
	}


	private void DoOP_RFM_CREATE(DWProtocolDevice protodev)
	{
		logger.debug("CREATE");
		
		// read path #
		try
		{
			int pathno = protodev.comRead1(true);
			int modebyte = protodev.comRead1(true);
			
			modebyte = (int)(modebyte & 0xFF);
			
			// read path str
			String pathstr = new String();
		
			int nchar = protodev.comRead1(true);
			while (nchar != 13)
			{
				pathstr += Character.toString((char) nchar);
				nchar = protodev.comRead1(true);
			}

			// send result
			
	
			this.paths[pathno] = new DWRFMPath(this.handlerno, pathno);
			this.paths[pathno].setPathstr(pathstr);
			
			int result = this.paths[pathno].createFile();
			
			protodev.comWrite1(result, true);
			
			logger.debug("create path " + pathno + " mode " + modebyte + ", to " + pathstr + ": result " + result);
		} 
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		
		} catch (DWCommTimeOutException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	private void DoOP_RFM_OPEN(DWProtocolDevice protodev)
	{
		logger.debug("OPEN");
		
		// read path #
		try
		{
			int pathno = protodev.comRead1(true);
			int modebyte = protodev.comRead1(true);

			modebyte = (int)(modebyte & 0xFF);
						
			// read path str
			String pathstr = new String();
		
			int nchar = protodev.comRead1(true);
			while (nchar != 13)
			{
				pathstr += Character.toString((char) nchar);
				nchar = protodev.comRead1(true);
			}

			// send result
			
			// anything needed for dealing with multiple opens..
			
			this.paths[pathno] = new DWRFMPath(this.handlerno, pathno);
			this.paths[pathno].setPathstr(pathstr);
			
			int result = this.paths[pathno].openFile(modebyte);
			protodev.comWrite1(result, true);
				
			logger.debug("open path " + pathno + " mode " + modebyte + ", to " + pathstr + ": result " + result);
			
		} 
		catch (IOException  e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DWCommTimeOutException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
}
