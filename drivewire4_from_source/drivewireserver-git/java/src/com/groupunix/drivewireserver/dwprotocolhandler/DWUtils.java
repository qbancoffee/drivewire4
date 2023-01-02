package com.groupunix.drivewireserver.dwprotocolhandler;

import gnu.io.CommPortIdentifier;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import com.groupunix.drivewireserver.DWDefs;
import com.groupunix.drivewireserver.dwexceptions.DWFileSystemInvalidFilenameException;

public class DWUtils
{

	
	public static int int4(byte[] data) 
	{
		 return( (data[0] & 0xFF) << 32) + ((data[1] & 0xFF) << 16) + ((data[2] & 0xFF) << 8) + (data[3] & 0xFF);
	}
	
	public static int int3(byte[] data) 
	{
		 return((data[0] & 0xFF) << 16) + ((data[1] & 0xFF) << 8) + (data[2] & 0xFF);
	}

	
	public static int int2(byte[] data) 
	{
		 return((data[0] & 0xFF) << 8) + (data[1] & 0xFF);
	}
	
	
	public static byte reverseByte(int b)
	{
		return (byte) Integer.reverseBytes(Integer.reverse(b)); 
		
	}
	
	public static byte[] reverseByteArray(byte[] data) 
	{
		byte[] revdata = new byte[data.length];
		
		for (int i = 0;i<data.length;i++)
			revdata[i] = reverseByte(data[i]);
		
		return(revdata);
	}
	
	public static byte[] hexStringToByteArray(String hexs)
	{
		if (hexs.length() > 0) 
		{
			if ((hexs.length() % 2) == 0 )
			{
				byte[] res = new byte[hexs.length()/2];
				for (int i = 0;i<(hexs.length()/2);i++)
				{
					res[i] = (byte) Integer.parseInt(hexs.substring(i*2, i*2+1),16);
				}
				
				return(res);
			}
		}
		
		return null;
	}
	
	public static String byteArrayToHexString(byte in[])
	{
		return byteArrayToHexString(in, in.length);
	}
	
	public static String byteArrayToHexString(byte in[], int len) 
	{

	    byte ch = 0x00;

	    int i = 0; 

	    if (in == null || in.length <= 0)

	        return null;

	        

	    String pseudo[] = {"0", "1", "2",
	"3", "4", "5", "6", "7", "8",
	"9", "A", "B", "C", "D", "E",
	"F"};

	    StringBuffer out = new StringBuffer(in.length * 2);

	    

	    while (i < len) {

	        ch = (byte) (in[i] & 0xF0);

	        ch = (byte) (ch >>> 4);
	     // shift the bits down

	        ch = (byte) (ch & 0x0F);    
	// must do this is high order bit is on!

	        out.append(pseudo[ (int) ch]); // convert the

	        ch = (byte) (in[i] & 0x0F); // Strip off

	        out.append(pseudo[ (int) ch]); // convert the
	        
	        out.append(':');
	        i++;

	    }

	    String rslt = new String(out);

	    return rslt;

	}    

	
	
	public static String prettyTimer(byte tno)
	{
		String result = "unknown " + (tno & 0xff);
		
		switch (tno)
		{
			case DWDefs.TIMER_BAD_DATA:
				result = "invalid protocol data";
				break;
				
			case DWDefs.TIMER_DWINIT:
				result = "DWINIT operation";
				break;
			
			case DWDefs.TIMER_IO:
				result = "I/O operation";
				break;
				
			case DWDefs.TIMER_NP_OP:
				result = "protocol operation (non poll)";
				break;
			
			case DWDefs.TIMER_OP:
				result = "protocol operation";
				break;
			
			case DWDefs.TIMER_READ:
				result = "read operation";
				break;
			
			case DWDefs.TIMER_RESET:
				result = "instance reset";
				break;
			
			case DWDefs.TIMER_START:
				result = "server start";
				break;
			
			case DWDefs.TIMER_WRITE:
				result = "write operation";
				break;
			
			case DWDefs.TIMER_POLL:
				result = "poll operation";
				break;
				
				
			default:
				if ( (tno & 0xff) >= (DWDefs.TIMER_USER & 0xff))
				{
					result = "user " + ((tno & 0xff) -  (DWDefs.TIMER_USER & 0xff));
				}
			
		}
		
		return(result);
	}
		
	
	
	
	
	public static String prettySS(byte statcode)
	{
		String result = "unknown";
		
		switch (statcode)
		{
			case 0x00:
				result = "SS.Opt";
				break;

			case 0x02:
				result = "SS.Size";
				break;

			case 0x03:
				result = "SS.Reset";
				break;

			case 0x04:
				result = "SS.WTrk";
				break;

			case 0x05:
				result = "SS.Pos";
				break;

			case 0x06:
				result = "SS.EOF";
				break;

			case 0x0A:
				result = "SS.Frz";
				break;

			case 0x0B:
				result = "SS.SPT";
				break;

			case 0x0C:
				result = "SS.SQD";
				break;

			case 0x0D:
				result = "SS.DCmd";
				break;

			case 0x0E:
				result = "SS.DevNm";
				break;

			case 0x0F:
				result = "SS.FD";
				break;

			case 0x10:
				result = "SS.Ticks";
				break;

			case 0x11:
				result = "SS.Lock";
				break;

			case 0x12:
				result = "SS.VarSect";
				break;

			case 0x14:
				result = "SS.BlkRd";
				break;

			case 0x15:
				result = "SS.BlkWr";
				break;

			case 0x16:
				result = "SS.Reten";
				break;

			case 0x17:
				result = "SS.WFM";
				break;

			case 0x18: 
				result = "SS.RFM";
				break;
			
			case 0x1A:
				result = "SS.SSig";
				break;
				
			case 0x1B:
				result = "SS.Relea";
				break;

			case 0x1C:
				result = "SS.Attr";
				break;

			case 0x1E:
				result = "SS.RsBit";
				break;

			case 0x20:
				result = "SS.FDInf";
				break;

			case 0x26:
				result = "SS.DSize";
				break;

			case 0x27:
				result = "SS.KySns";
				break;

			// added for SCF/Ns	
			case 0x28:
				result = "SS.ComSt";
				break;
				
			case 0x29:
				result = "SS.Open";
				break;	
				
			case 0x2A:
				result = "SS.Close";
				break;
				
			case 0x30:
				result = "SS.HngUp";
				break;
				
			case (byte) 255:
				result =  "None";
				break;
			
			default:
				result = "Unknown: " + statcode;
		}
		
		return(result);
	}

	
	
	public static String prettyUtilMode(int mode) 
	{
		String res = "unset";

		switch(mode)
		{
		case DWDefs.UTILMODE_URL:
			res = "url";
			break;

		case DWDefs.UTILMODE_DWCMD:
			res = "dw cmd";
			break;
		case DWDefs.UTILMODE_UICMD:
			res = "ui cmd";
			break;
		case DWDefs.UTILMODE_TCPOUT:
			res = "tcp out";
			break;
		case DWDefs.UTILMODE_VMODEMOUT:
			res = "vmodem out";
			break;
		case DWDefs.UTILMODE_TCPIN:
			res = "tcp in";
			break;
		case DWDefs.UTILMODE_VMODEMIN:
			res = "vmodem in";
			break;
		case DWDefs.UTILMODE_TCPLISTEN:
			res = "tcp listen";
			break;
		case DWDefs.UTILMODE_NINESERVER:
			res = "nineserver";
			
		}
		
		return(res);
	}
	
	public static String prettyOP(byte opcode) 
	{
		String res = "Unknown";

		if ((opcode >= DWDefs.OP_FASTWRITE_BASE) && (opcode <= (DWDefs.OP_FASTWRITE_BASE + 31)))
		{
			res = "OP_FASTWRITE_" + (opcode - DWDefs.OP_FASTWRITE_BASE);
		}
		else
		{
		switch(opcode)
		{
		case DWDefs.OP_NOP:
			res = "OP_NOP";
			break;

		case DWDefs.OP_INIT:
			res = "OP_INIT";
			break;

		case DWDefs.OP_READ:
			res = "OP_READ";
			break;

		case DWDefs.OP_READEX:
			res = "OP_READEX";
			break;

		case DWDefs.OP_WRITE:
			res = "OP_WRITE";
			break;

		case DWDefs.OP_REREAD:
			res = "OP_REREAD";
			break;

		case DWDefs.OP_REREADEX:
			res = "OP_REREADEX";
			break;

		case DWDefs.OP_REWRITE:
			res = "OP_REWRITE";
			break;

		case DWDefs.OP_TERM:
			res = "OP_TERM";
			break;

		case DWDefs.OP_RESET1:
		case DWDefs.OP_RESET2:
		case DWDefs.OP_RESET3:
			res =  "OP_RESET";
			break;

		case DWDefs.OP_GETSTAT:
			res = "OP_GETSTAT";
			break;

		case DWDefs.OP_SETSTAT:
			res = "OP_SETSTAT";
			break;

		case DWDefs.OP_TIME:
			res = "OP_TIME";
			break;

		case DWDefs.OP_PRINT:
			res = "OP_PRINT";
			break;

		case DWDefs.OP_PRINTFLUSH:
			res = "OP_PRINTFLUSH";
			break;

		case DWDefs.OP_SERREADM:
			res = "OP_SERREADM";
			break;

		case DWDefs.OP_SERREAD:
			res = "OP_SERREAD";      
			break;

		case DWDefs.OP_SERWRITE:
			res = "OP_SERWRITE";
			break;

		case DWDefs.OP_SERSETSTAT:
			res = "OP_SERSETSTAT";
            break;
            
		case DWDefs.OP_SERGETSTAT:
			res =  "OP_SERGETSTAT";
            break;
		
		case DWDefs.OP_SERINIT:
			res =  "OP_SERINIT";
            break;
            
		case DWDefs.OP_SERTERM:
			res =  "OP_SERTERM";
            break;
            
		case DWDefs.OP_DWINIT:
			res =  "OP_DWINIT";
            break;
            
		default:
				res = "Unknown: " + opcode;
		}
		}
		
		return(res);
	}
	
	
	
	@SuppressWarnings("unchecked")
	public static ArrayList<String> getPortNames()
	{
		ArrayList<String> ports = new ArrayList<String>();
		
		java.util.Enumeration<CommPortIdentifier> portEnum = CommPortIdentifier.getPortIdentifiers();
		while ( portEnum.hasMoreElements() ) 
		{
			CommPortIdentifier portIdentifier = portEnum.nextElement();
		    if (portIdentifier.getPortType() == 1)
		    {
		    	ports.add(portIdentifier.getName());
		    }
		        
		}        
				
		return(ports);
	}

	public static String midimsgToText(int statusbyte, int data1, int data2) 
	{
		// make midi messages into something humans can read.. 
		String action = new String();
		String chan = new String();
		String d1 = new String();
		String d2 = new String();
		
		if ((statusbyte >= 128) && (statusbyte <= 143))
		{
			action = "Note off";
			chan = "Chan: " + (statusbyte - 128);
			d1 = "Pitch: " + prettyMidiPitch(data1);
			d2 = "Vel: " + data2;
		}
		else if ((statusbyte >= 144) && (statusbyte <= 159))
		{
			action = "Note on";
			chan = "Chan: " + (statusbyte - 144);
			d1 = "Pitch: " + prettyMidiPitch(data1);
			d2 = "Vel: " + data2;
		}
		else if ((statusbyte >= 160) && (statusbyte <= 175))
		{
			action = "Key press";
			chan = "Chan: " + (statusbyte - 160);
			d1 = "Key: " + data1;
			d2 = "Pressure: " + data2;
		}
		else if ((statusbyte >= 176) && (statusbyte <= 191))
		{
			action = "Ctr change";
			chan = "Chan: " + (statusbyte - 176);
			d1 = "Controller: " + data1;
			d2 = "Value: " + data2;
		}
		else if ((statusbyte >= 192) && (statusbyte <= 207))
		{
			action = "Prg change";
			chan = "Chan: " + (statusbyte - 192);
			d1 = "Preset: " + data1;
		}
		else if ((statusbyte >= 208) && (statusbyte <= 223))
		{
			action = "Chan press";
			chan = "Chan: " + (statusbyte - 208);
			d1 = "Pressure: " + data1;
		}
		else if ((statusbyte >= 224) && (statusbyte <= 239))
		{
			action = "Pitch bend";
			chan = "Chan: " + (statusbyte - 224);
			d1 = "LSB: " + data1;
			d2 = "MSB: " + data2;
		}
		else if (statusbyte == 248)
		{
			action = "Timing tick";
		}
		else 
		{
			action = "Unknown: " + statusbyte;
			d1 = "Data1: " + data1;
			d2 = "Data2: " + data2;
		}
		
		
		
		return(String.format("%-10s %-10s %-20s %-20s", action, chan, d1, d2));
	}
	
	private static String prettyMidiPitch(int pitch) 
	{
		String[] notes = new String[] {"C","C#","D","D#","E","F","F#","G","G#","A","A#","B"};
		
		return(String.format("%-3d %-2s %d",pitch,notes[pitch % 12],(pitch / 12) - 1));
	}
	
	
	public static String dropFirstToken(String txt)
	{
		// drop first token in string

		String rest = new String();
		
		String[] tokens = txt.split(" ");
		
		for (int x = 1; x < tokens.length; x++)
		{
			if (rest.length() > 0)
			{
				rest = rest + " " + tokens[x];
			}
			else
			{
				rest = tokens[x];
			}
			
		}
		
		return(rest);
	}
	
	
	public static String convertStarToBang(String txt)
	{
	
		txt = txt.replaceAll("\\*", "!");
		
		return txt;
	}

	public static boolean isStringFalse(String tf) 
	{
		if (tf.equalsIgnoreCase("false"))
			return true;
		
		if (tf.equalsIgnoreCase("off"))
			return true;
		
		return false;
	}
	
	public static boolean isStringTrue(String tf) 
	{
		if (tf.equalsIgnoreCase("true"))
			return true;
		
		if (tf.equalsIgnoreCase("on"))
			return true;
		
		return false;
	}

	   public static boolean testClassPath(String fullClassName) {
		      boolean result = false;
		      try {
		         Class.forName(fullClassName);
		         result = true;
		      } catch (Throwable e) 
		      {
		    	//  e.printStackTrace();
		      }

		      return result;
		   }    
	
	   
		public static boolean DirExistsOrCreate(String directoryName)
		{
			  File theDir = new File(directoryName);

			  // if the directory does not exist, create it
			  if (!theDir.exists())
			  {
			    return(theDir.mkdir());
			  }
			  else
			  {
				  return(true);
			  }
		}
		
		public static boolean FileExistsOrCreate(String fileName) throws IOException
		{
			  File theFile = new File(fileName);

			  // if the directory does not exist, create it
			  if (!theFile.exists())
			  {
			    return(theFile.createNewFile());
			  }
			  else
			  {
				  return(true);
			  }
		}

		
		public static void copyFile(String fromFileName, String toFileName) throws IOException 
		{
	    
			File fromFile = new File(fromFileName);
		    File toFile = new File(toFileName);
	
		    if (!fromFile.exists())
		      throw new IOException("no source file: " + fromFileName);
		    if (!fromFile.isFile())
		      throw new IOException("can't copy directory: " + fromFileName);
		    if (!fromFile.canRead())
		      throw new IOException("source file is unreadable: " + fromFileName);
	
		    if (toFile.isDirectory())
		      toFile = new File(toFile, fromFile.getName());
	
		    if (toFile.exists()) {
		      if (!toFile.canWrite())
		        throw new IOException("destination file is unwriteable: " + toFileName);
		     
		      String parent = toFile.getParent();
		      if (parent == null)
		        parent = System.getProperty("user.dir");
		      
		      File dir = new File(parent);
		      if (!dir.exists())
		        throw new IOException("destination directory doesn't exist: " + parent);
		      
		      if (dir.isFile())
		        throw new IOException("destination is not a directory: " + parent);
		      if (!dir.canWrite())
		        throw new IOException("destination directory is unwriteable: " + parent);
		    }
	
		    FileInputStream from = null;
		    FileOutputStream to = null;
		    try 
		    {
		      from = new FileInputStream(fromFile);
		      to = new FileOutputStream(toFile);
		      byte[] buffer = new byte[4096];
		      int bytesRead;
	
		      while ((bytesRead = from.read(buffer)) != -1)
		        to.write(buffer, 0, bytesRead); // write
		    } 
		    finally 
		    {
		      if (from != null)
		        try {
		          from.close();
		        } catch (IOException e) {
		          ;
		        }
		      if (to != null)
		        try {
		          to.close();
		        } catch (IOException e) {
		          ;
		        }
		    }
	  }
		
		
		public static String shortenLocalURI(String df) 
		{
			if (df.startsWith("file:///"))
			{
				if (df.charAt(9) == ':')
				{
					return df.substring(8);
				}
				else
				{
					return df.substring(7);
				}
			}
			return(df);
		}

		public static String getFileDescriptor(File f) 
		{
			String res = "";
			
			try {
				res = File.separator;
				res += "|" + f.getCanonicalPath();
				res += "|" + f.getParent();
				if (f.getParent() != null)
				{
					// these checks take a long time on removable media with no disk in the drive
					res += "|" + f.length();
					res += "|" + f.lastModified();
					res += "|" + f.isDirectory();
				}
				else
					res += "|0|0|true";
				
				
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return(res);
		}


		@SuppressWarnings("deprecation")
		public static String getFileXDescriptor(File f) throws DWFileSystemInvalidFilenameException 
		{
			
			if (f.length() > 4294967295L)
			{
				throw new DWFileSystemInvalidFilenameException("File too large for XDir");
			}
			
			if (f.getName().length() > 255)
			{
				throw new DWFileSystemInvalidFilenameException("Filename too long for XDir");
			}
			
			byte[] res = new byte[12 + f.getName().length()]; 
			
			int pos = 0;
			
			
			long l = f.length();
			
			// 4 byte file size
			
			res[pos++] = (byte) (l >>> 24);
			res[pos++] = (byte) (l >>> 16);
			res[pos++] = (byte) (l >>> 8);
			res[pos++] = (byte) (l);
			
			// 5 byte OS9 style modified date - Y M D Hr Min
			Date moddate = new Date(f.lastModified());
			
			res[pos++] = (byte) (moddate.getYear());
			res[pos++] = (byte) (moddate.getMonth());
			res[pos++] = (byte) (moddate.getDate());
			res[pos++] = (byte) (moddate.getHours());
			res[pos++] = (byte) (moddate.getMinutes());
			
			// is directory
			
			if (f.isDirectory())
				res[pos++] = (byte) 1;
			else
				res[pos++] = (byte) 0;
			
			// is readonly
			
			if (f.canWrite())
				res[pos++] = (byte) 0;
			else
				res[pos++] = (byte) 1;
			
			// name length
			
			res[pos++] = (byte) f.getName().length();
			
			for (int i = 0;i<f.getName().length();i++)
				res[pos++] = f.getName().getBytes()[i];
			
		
			System.out.println(f.getName() + "\t" + f.getName().length() + "\t" + f.length());
			
			System.out.println( byteArrayToHexString(res, res.length) );
			
			return(new String(res));
		}
		
		
		
		public String cocoString(byte[] bytes) 
		{
			// return string from 6809 style string
			String ret = new String();
			
			int i = 0;
			
			// thanks Christopher Hawks
			while ((i < bytes.length - 1) && (bytes[i] > 0))
			{
				ret += Character.toString((char) bytes[i]);
				i++;
			}
			
			ret += Character.toString((char) (bytes[i] + 128));
			
			return(ret);
		}

		public static String prettyFormat(int diskFormat)
		{
			String res = "unknown";
			
			switch(diskFormat)
			{
				case DWDefs.DISK_FORMAT_DMK:
					res = "DMK";
					break;
				case DWDefs.DISK_FORMAT_JVC:
					res = "JVC";
					break;
				case DWDefs.DISK_FORMAT_RAW:
					res = "DSK (raw)";
					break;
				case DWDefs.DISK_FORMAT_VDK:
					res = "VDK";
					break;
				case DWDefs.DISK_FORMAT_NONE:
					res = "none";
					break;
			}
			
			
			return res;
		}
	   
		
		public static String prettyFileSystem(int format)
		{
			String res = "unknown";
			
			switch(format)
			{
				case DWDefs.DISK_FILESYSTEM_OS9:
					res = "OS9";
					break;
				case DWDefs.DISK_FILESYSTEM_DECB:
					res = "DECB";
					break;
				case DWDefs.DISK_FILESYSTEM_LWFS:
					res = "LWFS";
					break;
				case DWDefs.DISK_FILESYSTEM_CCB:
					res = "CCB";
					break;

			}
			
			
			return res;
		}

		public static ThreadGroup getRootThreadGroup( ) {

	    ThreadGroup tg = Thread.currentThread( ).getThreadGroup( );
	    ThreadGroup ptg;
	    while ( (ptg = tg.getParent( )) != null )
	        tg = ptg;
	    return tg;
	}

		public static String OS9String(byte[] buf)
		{
			String res = "";
			
			int pos = 0;
			
			while( (pos < buf.length) && ((buf[pos] & 0xFF) < 128))
			{
				res += (char) (buf[pos] & 0xff);
				pos++;
			}
			
			if (pos < buf.length)
				res += (char) ((buf[pos] & 0xff) - 128);
			
			return res;
		}

		public static String pretty5ByteDateTime(byte[] data)
		{
			String res = String.format("%02d:%02d %02d/%02d/%04d", (data[3] & 0xff), (data[4] & 0xff), (data[1] & 0xff), (data[2] & 0xff), (1900 + (data[0] & 0xff)));
			
			return res;
		}

		
		public static String pretty3ByteDate(byte[] data)
		{
			String res = String.format("%02d/%02d/%04d", (data[1] & 0xff), (data[2] & 0xff), (1900 + (data[0] & 0xff)));
			
			return res;
		}

		

		
		
}
