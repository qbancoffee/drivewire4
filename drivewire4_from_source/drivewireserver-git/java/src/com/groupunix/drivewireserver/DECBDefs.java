package com.groupunix.drivewireserver;

public class DECBDefs
{
	public static final byte FILETYPE_BASIC 			= (byte) 0;
	public static final byte FILETYPE_DATA 				= (byte) 1;
	public static final byte FILETYPE_ML 				= (byte) 2;
	public static final byte FILETYPE_TEXT 				= (byte) 3;
	
	public static final byte FLAG_ASCII 				= (byte) 0xFF;
	public static final byte FLAG_BIN	 				= (byte) 0;
	
	public static final int DIRECTORY_OFFSET = 308;
	public static final int FAT_OFFSET = 307;
	public static final byte FAT_SIZE = 68;
	
	
}
