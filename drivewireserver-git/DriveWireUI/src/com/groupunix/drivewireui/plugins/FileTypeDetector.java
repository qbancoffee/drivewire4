package com.groupunix.drivewireui.plugins;

import java.io.IOException;

import org.eclipse.swt.graphics.Image;

import com.groupunix.drivewireserver.DECBDefs;
import com.groupunix.drivewireserver.dwdisk.filesystem.DWDECBFileSystemDirEntry;
import com.groupunix.drivewireserver.dwdisk.filesystem.DWRBFFileSystem;
import com.groupunix.drivewireserver.dwdisk.filesystem.DWRBFFileSystemDirEntry;
import com.groupunix.drivewireserver.dwexceptions.DWDiskInvalidSectorNumber;
import com.groupunix.drivewireui.DWLibrary;
import com.groupunix.drivewireui.MainWin;
import com.swtdesigner.SWTResourceManager;

public class FileTypeDetector
{

	
	
	public static int getDECBFileType(DWDECBFileSystemDirEntry entry, byte[] fc)
	{
		int res = DWLibrary.FILETYPE_UNKNOWN;
		
		// defaults from dir entry
		if (entry.isAscii())
			res = DWLibrary.FILETYPE_ASCII;
		else
			res = DWLibrary.FILETYPE_BINARY;
		
		if (entry.getFileType() == DECBDefs.FILETYPE_BASIC)
		{
			if (entry.isAscii())
				res = DWLibrary.FILETYPE_BASIC_ASCII;
			else
				res = DWLibrary.FILETYPE_BASIC_TOKENS;
		}
		else
		{
			// cocomax
			
			if (((fc.length - 10) % 6144 == 0)  && (fc.length > 4) &&  (fc[0] == 0) && ((fc[1] == 0x30) || (fc[1] == 0x18)) && (fc[2] == 0) && (fc[3] == 0x0e) && (fc[4] == 0))
			{
				res = DWLibrary.FILETYPE_CCMAX;
			}
			// pmode 4
			else if ((fc.length > 4) &&  (fc[0] == 0) && (fc[3] == 0x0e) && (fc[4] == 0))
			{
				res = DWLibrary.FILETYPE_PMODE;
			}
			// file extensions..
			else if (entry.getFileExt().equalsIgnoreCase("ASM"))
			{
				res = DWLibrary.FILETYPE_SOURCE_ASM;
			}
			else if (entry.getFileExt().equalsIgnoreCase("BIN"))
			{
				res = DWLibrary.FILETYPE_BIN;
			}
			else if (entry.getFileExt().equalsIgnoreCase("DOC"))
			{
				res = DWLibrary.FILETYPE_DOC;
			}
			else if (entry.getFileExt().equalsIgnoreCase("FNT"))
			{
				res = DWLibrary.FILETYPE_FONT;
			}
			else if (entry.getFileExt().equalsIgnoreCase("DMP") || entry.getFileExt().equalsIgnoreCase("DRV"))
			{
				res = DWLibrary.FILETYPE_PRINTDRIVER;
			}
			else if (entry.getFileType() == DECBDefs.FILETYPE_DATA)
			{
				res = DWLibrary.FILETYPE_BASIC_DATA;
			}
			
				
		}
			
		return res;
	}
	
	
	public static Image getFileIcon(int type)
	{
		String path = "/filetypes/blank.png";
		
		
		switch(type)
		{
			case DWLibrary.FILETYPE_ASCII:
				path = "/filetypes/txt.png";
				break;
			
			case DWLibrary.FILETYPE_BINARY:
				path = "/filetypes/binary.png";
				break;
				
			case DWLibrary.FILETYPE_BASIC_TOKENS:
			case DWLibrary.FILETYPE_BASIC_ASCII:
				path = "/filetypes/basic.png";
				break;
				
			case DWLibrary.FILETYPE_BASIC_DATA:
				path = "/filetypes/basic-data.png";
				break;
				
			case DWLibrary.FILETYPE_CCMAX:
				path = "/filetypes/image-cocomax.png";
				break;	
				
			case DWLibrary.FILETYPE_SOURCE_ASM:
				path = "/filetypes/asm.png";
				break;

			case DWLibrary.FILETYPE_BIN:
				path = "/filetypes/bin.png";
				break;

			case DWLibrary.FILETYPE_DOC:
				path = "/filetypes/doc.png";
				break;

				
			case DWLibrary.FILETYPE_FONT:
				path = "/filetypes/font.png";
				break;

			case DWLibrary.FILETYPE_ARCHIVE:
				path = "/filetypes/archive.png";
				break;

			case DWLibrary.FILETYPE_SOUND:
				path = "/filetypes/sound.png";
				break;
				
			case DWLibrary.FILETYPE_PMODE:
				path = "/filetypes/image.png";
				break;

			case DWLibrary.FILETYPE_PRINTDRIVER:
				path = "/filetypes/printdrv.png";
				break;
				
			case DWLibrary.FILETYPE_MIDI:
				path = "/filetypes/audio-keyboard.png";
				break;
				
			case DWLibrary.FILETYPE_SOURCE_C:
				path = "/filetypes/text-x-c.png";
				break;
				
			case DWLibrary.FILETYPE_SOURCE_C_HEADER:
				path = "/filetypes/text-x-c-hdr.png";
				break;
				
			case DWLibrary.FILETYPE_SOURCE_BASIC09:
				path = "/filetypes/audio-keyboard.png";
				break;
			
			case DWLibrary.FILETYPE_SOURCE_FORTH:
				path = "/filetypes/audio-keyboard.png";
				break;
			case DWLibrary.FILETYPE_OS9_MODULE_6809:
				path = "/filetypes/cpu.png";
				break;
				
			case DWLibrary.FILETYPE_OS9_MODULE_PASCAL:	
			case DWLibrary.FILETYPE_OS9_MODULE_BASIC09:
				path = "/filetypes/mod_basic09.png";
				break;
				
			
				
			case DWLibrary.FILETYPE_OS9_MODULE_PRGRM:
				path = "/filetypes/bin.png";
				break;
				
			case DWLibrary.FILETYPE_OS9_MODULE_SBRTN:
				path = "/filetypes/mod_sbrtn.png";
				break;
				
			case DWLibrary.FILETYPE_OS9_MODULE_MULTI:
				path = "/filetypes/mod_multi.png";
				break;
				
			case DWLibrary.FILETYPE_OS9_MODULE_DATA:
				path = "/filetypes/mod_data.png";
				break;
				
			case DWLibrary.FILETYPE_OS9_MODULE_SYSTM:
				path = "/filetypes/mod_systm.png";
				break;
				
			case DWLibrary.FILETYPE_OS9_MODULE_FLMGR:
				path = "/filetypes/blockdevice-2.png";
				break;
				
			case DWLibrary.FILETYPE_OS9_MODULE_DRIVR:
				path = "/filetypes/mod_drivr.png";
				break;
				
			case DWLibrary.FILETYPE_OS9_MODULE_DEVIC:
				path = "/filetypes/mod_devic.png";
				break;
				
			case DWLibrary.FILETYPE_OS9_MODULE_USERDEF:
			case DWLibrary.FILETYPE_OS9_MODULE:
				path = "/filetypes/module.png";
				break;
				
		}
		
				
		
		return SWTResourceManager.getImage(MainWin.class, path);
	}
	
	
	
	
	public static String getFileDescription(int type)
	{
		String path = "Unknown";
		
		
		switch(type)
		{
			case DWLibrary.FILETYPE_ASCII:
				path = "ASCII Text";
				break;
			
			case DWLibrary.FILETYPE_BINARY:
				path = "Unknown Binary";
				break;
				
			case DWLibrary.FILETYPE_BASIC_TOKENS:
				path = "BASIC (Tokenized)";
				break;
				
			case DWLibrary.FILETYPE_BASIC_ASCII:
				path = "BASIC (ASCII)";
				break;
				
			case DWLibrary.FILETYPE_BASIC_DATA:
				path = "BASIC Data";
				break;
				
			case DWLibrary.FILETYPE_CCMAX:
				path = "CoCoMAX Image";
				break;	
				
			case DWLibrary.FILETYPE_SOURCE_ASM:
				path = "ASM Source";
				break;

			case DWLibrary.FILETYPE_BIN:
				path = "Executable";
				break;

			case DWLibrary.FILETYPE_DOC:
				path = "Text file";
				break;

				
			case DWLibrary.FILETYPE_FONT:
				path = "Font Definition";
				break;

			case DWLibrary.FILETYPE_ARCHIVE:
				path = "Archive";
				break;

			case DWLibrary.FILETYPE_SOUND:
				path = "Sound";
				break;
				
			case DWLibrary.FILETYPE_PMODE:
				path = "PMODE Image";
				break;

			case DWLibrary.FILETYPE_PRINTDRIVER:
				path = "Printer Driver";
				break;
				
			case DWLibrary.FILETYPE_MIDI:
				path = "MIDI";
				break;
				
		}
	
		return path;
	}


	public static int getRBFFileType(DWRBFFileSystemDirEntry entry, DWRBFFileSystem rbffs)
	{
		// module check
		try
		{
			byte[] fsec = rbffs.getSector(entry.getFD().getSegmentList()[0].getLsn()).getData();
			
			if (( (fsec[0] & 0xFF) == 0x87) && ((fsec[1] & 0xFF) == 0xCD))
			{
				// header check, xor first 8 bytes should == ~9th
				int test = (fsec[0] & 0xFF);
				
				for (int i = 1;i<8;i++)
				{
					test = test ^ (fsec[i] & 0xff);
				}
				
				if (test == (~fsec[8] & 0xff))
				{
					// header is valid
					
					/*
					$1x Program module Prgrm
					$2x Subroutine module Sbrtn
					$3x Multi-module (for future use) Multi
					$4x Data module Data
					$5x-$Bx User-definable module
					$Cx NitrOS-9 system module Systm
					$Dx NitrOS-9 file manager module FlMgr
					$Ex NitrOS-9 device driver module Drivr
					$Fx NitrOS-9 device descriptor module Devic
					 */
					
					int mt = (0xff & fsec[6]);
					if (mt < 0x10)
					{
						return DWLibrary.FILETYPE_OS9_MODULE;
					}
					else if (mt < 0x30)
					{
						if ((mt & 0x01) == 0x01)
							return DWLibrary.FILETYPE_OS9_MODULE_6809;
						else if ((mt & 0x02) == 0x02)
							return DWLibrary.FILETYPE_OS9_MODULE_BASIC09;
						else if ((mt & 0x03) == 0x03)
							return DWLibrary.FILETYPE_OS9_MODULE_PASCAL;
						else if (mt < 0x20)
							return DWLibrary.FILETYPE_OS9_MODULE_PRGRM;
						
						return DWLibrary.FILETYPE_OS9_MODULE_SBRTN;
					}
					else if (mt < 0x40)
					{
						return DWLibrary.FILETYPE_OS9_MODULE_MULTI;
					}
					else if (mt < 0x50)
					{
						return DWLibrary.FILETYPE_OS9_MODULE_DATA;
					}
					else if (mt < 0xC0)
					{
						return DWLibrary.FILETYPE_OS9_MODULE_USERDEF;
					}
					else if (mt < 0xD0)
					{
						return DWLibrary.FILETYPE_OS9_MODULE_SYSTM;
					}
					else if (mt < 0xE0)
					{
						return DWLibrary.FILETYPE_OS9_MODULE_FLMGR;
					}
					else if (mt < 0xF0)
					{
						return DWLibrary.FILETYPE_OS9_MODULE_DRIVR;
					}
					else if (mt < 0x20)
					{
						return DWLibrary.FILETYPE_OS9_MODULE_DEVIC;
					}
					
					return DWLibrary.FILETYPE_OS9_MODULE;
				}
			}
			
		} 
		catch (DWDiskInvalidSectorNumber e)
		{
		} 
		catch (IOException e)
		{
		}
		
		
		if ((entry.getFD().isAttr_PE()) || (entry.getFD().isAttr_E()))
		{
			return DWLibrary.FILETYPE_BIN;
		}
		
		// extensions.. ?
		
		if (entry.getFileExt().equals("c"))
		{
			// C
			return DWLibrary.FILETYPE_SOURCE_C;
		}
		else if (entry.getFileExt().equals("h"))
		{
			return DWLibrary.FILETYPE_SOURCE_C_HEADER;
		}
		
		

		return DWLibrary.FILETYPE_UNKNOWN;
	}
	
}
