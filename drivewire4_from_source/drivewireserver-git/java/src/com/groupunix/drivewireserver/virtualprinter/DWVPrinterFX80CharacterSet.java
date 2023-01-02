package com.groupunix.drivewireserver.virtualprinter;


public class DWVPrinterFX80CharacterSet 
{

		private DWVPrinterFX80Character[] characters = new DWVPrinterFX80Character[256];
		

		public void setCharacter(int charnum, int[] bits, int len)
		{
			characters[charnum] = new DWVPrinterFX80Character(bits, len);
		}
		
		public int getCharacterCol(int charnum, int colnum)
		{
			return(characters[charnum].getCol(colnum));
		}
	}
