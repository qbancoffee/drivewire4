package com.groupunix.drivewireserver;

public class MCXDefs
{

	// protocol contants
	public static final byte MCX_PROTOCOL_VERSION = 1;
	
	// protocol op codes
	
	public static final byte ALERT = '!';
	public static final byte OP_LOADFILE = 'L';
	public static final byte OP_GETDATABLOCK = 'G';
	public static final byte OP_PREPARENEXTBLOCK = 'N';
	public static final byte OP_SAVEFILE = 'S';
	public static final byte OP_WRITEBLOCK = 'W';
	public static final byte OP_OPENDATAFILE = 'O';
	public static final byte OP_DIRFILEREQUEST = 'F';
	public static final byte OP_RETRIEVENAME = '$';
	public static final byte OP_DIRNAMEREQUEST = 'D';
	public static final byte OP_SETCURRENTDIR = 'C';
	
	// response codes
	public static final byte MCXOK = 0;
	public static final byte MCXERROR_FC = (byte) 8;
	public static final byte MCXERROR_IO = (byte) 34;
	public static final byte MCXERROR_FM = (byte) 36;
	public static final byte MCXERROR_DN = (byte) 38;
	public static final byte MCXERROR_NE = (byte) 40;
	public static final byte MCXERROR_WP = (byte) 42;
	public static final byte MCXERROR_FN = (byte) 44;
	public static final byte MCXERROR_FS = (byte) 46;
	public static final byte MCXERROR_IE = (byte) 48;
	public static final byte MCXERROR_FD = (byte) 50;
	public static final byte MCXERROR_AO = (byte) 52;
	public static final byte MCXERROR_NO = (byte) 54;
	public static final byte MCXERROR_DS = (byte) 56;


	
	// input buffer
	public static final int INPUT_WAIT = 2000;
	

	

}
