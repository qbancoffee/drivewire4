package com.groupunix.drivewireserver;

public class MCXDefs {
  public static final byte MCX_PROTOCOL_VERSION = 1;
  
  public static final byte ALERT = 33;
  
  public static final byte OP_LOADFILE = 76;
  
  public static final byte OP_GETDATABLOCK = 71;
  
  public static final byte OP_PREPARENEXTBLOCK = 78;
  
  public static final byte OP_SAVEFILE = 83;
  
  public static final byte OP_WRITEBLOCK = 87;
  
  public static final byte OP_OPENDATAFILE = 79;
  
  public static final byte OP_DIRFILEREQUEST = 70;
  
  public static final byte OP_RETRIEVENAME = 36;
  
  public static final byte OP_DIRNAMEREQUEST = 68;
  
  public static final byte OP_SETCURRENTDIR = 67;
  
  public static final byte MCXOK = 0;
  
  public static final byte MCXERROR_FC = 8;
  
  public static final byte MCXERROR_IO = 34;
  
  public static final byte MCXERROR_FM = 36;
  
  public static final byte MCXERROR_DN = 38;
  
  public static final byte MCXERROR_NE = 40;
  
  public static final byte MCXERROR_WP = 42;
  
  public static final byte MCXERROR_FN = 44;
  
  public static final byte MCXERROR_FS = 46;
  
  public static final byte MCXERROR_IE = 48;
  
  public static final byte MCXERROR_FD = 50;
  
  public static final byte MCXERROR_AO = 52;
  
  public static final byte MCXERROR_NO = 54;
  
  public static final byte MCXERROR_DS = 56;
  
  public static final int INPUT_WAIT = 2000;
}


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/MCXDefs.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */