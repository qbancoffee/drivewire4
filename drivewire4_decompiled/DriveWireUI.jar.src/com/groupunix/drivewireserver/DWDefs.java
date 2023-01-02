/*     */ package com.groupunix.drivewireserver;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DWDefs
/*     */ {
/*     */   public static final byte DW_PROTOCOL_VERSION = 4;
/*     */   public static final byte OP_NOP = 0;
/*     */   public static final byte OP_NAMEOBJ_MOUNT = 1;
/*     */   public static final byte OP_NAMEOBJ_CREATE = 2;
/*     */   public static final byte OP_NAMEOBJ_TYPE = 3;
/*     */   public static final byte OP_TIME = 35;
/*     */   public static final byte OP_AARON = 65;
/*     */   public static final byte OP_WIREBUG_MODE = 66;
/*     */   public static final byte OP_SERREAD = 67;
/*     */   public static final byte OP_SERGETSTAT = 68;
/*     */   public static final byte OP_SERINIT = 69;
/*     */   public static final byte OP_PRINTFLUSH = 70;
/*     */   public static final byte OP_GETSTAT = 71;
/*     */   public static final byte OP_INIT = 73;
/*     */   public static final byte OP_PRINT = 80;
/*     */   public static final byte OP_READ = 82;
/*     */   public static final byte OP_SETSTAT = 83;
/*     */   public static final byte OP_TERM = 84;
/*     */   public static final byte OP_WRITE = 87;
/*     */   public static final byte OP_DWINIT = 90;
/*     */   public static final byte OP_SERREADM = 99;
/*     */   public static final byte OP_SERWRITEM = 100;
/*     */   public static final byte OP_REREAD = 114;
/*     */   public static final byte OP_REWRITE = 119;
/*     */   public static final byte OP_FASTWRITE_BASE = -128;
/*     */   public static final byte OP_FASTWRITE_P1 = -127;
/*     */   public static final byte OP_FASTWRITE_P2 = -126;
/*     */   public static final byte OP_FASTWRITE_P3 = -125;
/*     */   public static final byte OP_FASTWRITE_P4 = -124;
/*     */   public static final byte OP_FASTWRITE_P5 = -123;
/*     */   public static final byte OP_FASTWRITE_P6 = -122;
/*     */   public static final byte OP_FASTWRITE_P7 = -121;
/*     */   public static final byte OP_FASTWRITE_P8 = -120;
/*     */   public static final byte OP_FASTWRITE_P9 = -119;
/*     */   public static final byte OP_FASTWRITE_P10 = -118;
/*     */   public static final byte OP_FASTWRITE_P11 = -117;
/*     */   public static final byte OP_FASTWRITE_P12 = -116;
/*     */   public static final byte OP_FASTWRITE_P13 = -115;
/*     */   public static final byte OP_FASTWRITE_P14 = -114;
/*     */   public static final byte OP_FASTWRITE_P15 = -113;
/*     */   public static final byte OP_SERWRITE = -61;
/*     */   public static final byte OP_SERSETSTAT = -60;
/*     */   public static final byte OP_SERTERM = -59;
/*     */   public static final byte OP_READEX = -46;
/*     */   public static final byte OP_RFM = -42;
/*     */   public static final byte OP_230K230K = -26;
/*     */   public static final byte OP_REREADEX = -14;
/*     */   public static final byte OP_RESET3 = -8;
/*     */   public static final byte OP_230K115K = -3;
/*     */   public static final byte OP_RESET2 = -2;
/*     */   public static final byte OP_RESET1 = -1;
/*     */   public static final byte DWERROR_WP = -14;
/*     */   public static final byte DWERROR_CRC = -13;
/*     */   public static final byte DWERROR_READ = -12;
/*     */   public static final byte DWERROR_WRITE = -11;
/*     */   public static final byte DWERROR_NOTREADY = -10;
/*     */   public static final byte DWOK = 0;
/*     */   public static final int UTILMODE_UNSET = 0;
/*     */   public static final int UTILMODE_DWCMD = 1;
/*     */   public static final int UTILMODE_URL = 2;
/*     */   public static final int UTILMODE_TCPOUT = 3;
/*     */   public static final int UTILMODE_VMODEMOUT = 4;
/*     */   public static final int UTILMODE_TCPIN = 5;
/*     */   public static final int UTILMODE_VMODEMIN = 6;
/*     */   public static final int UTILMODE_TCPLISTEN = 7;
/*     */   public static final byte RC_SUCCESS = 0;
/*     */   public static final byte RC_SYNTAX_ERROR = 10;
/*     */   public static final byte RC_DRIVE_ERROR = 100;
/*     */   public static final byte RC_INVALID_DRIVE = 101;
/*     */   public static final byte RC_DRIVE_NOT_LOADED = 102;
/*     */   public static final byte RC_DRIVE_ALREADY_LOADED = 103;
/*     */   public static final byte RC_IMAGE_FORMAT_EXCEPTION = 104;
/*     */   public static final byte RC_NO_SUCH_DISKSET = 110;
/*     */   public static final byte RC_INVALID_DISK_DEF = 111;
/*     */   public static final byte RC_NET_ERROR = 120;
/*     */   public static final byte RC_NET_IO_ERROR = 121;
/*     */   public static final byte RC_NET_UNKNOWN_HOST = 122;
/*     */   public static final byte RC_NET_INVALID_CONNECTION = 123;
/*     */   public static final byte RC_INVALID_PORT = -116;
/*     */   public static final byte RC_INVALID_HANDLER = -115;
/*     */   public static final byte RC_CONFIG_KEY_NOT_SET = -114;
/*     */   public static final byte RC_MIDI_ERROR = -106;
/*     */   public static final byte RC_MIDI_UNAVAILABLE = -105;
/*     */   public static final byte RC_MIDI_INVALID_DEVICE = -104;
/*     */   public static final byte RC_MIDI_INVALID_DATA = -103;
/*     */   public static final byte RC_MIDI_SOUNDBANK_FAILED = -102;
/*     */   public static final byte RC_MIDI_SOUNDBANK_NOT_SUPPORTED = -101;
/*     */   public static final byte RC_MIDI_INVALID_PROFILE = -100;
/*     */   public static final byte RC_SERVER_ERROR = -56;
/*     */   public static final byte RC_SERVER_FILESYSTEM_EXCEPTION = -55;
/*     */   public static final byte RC_SERVER_IO_EXCEPTION = -54;
/*     */   public static final byte RC_SERVER_FILE_NOT_FOUND = -53;
/*     */   public static final byte RC_SERVER_NOT_IMPLEMENTED = -52;
/*     */   public static final byte RC_SERVER_NOT_READY = -51;
/*     */   public static final byte RC_INSTANCE_NOT_READY = -50;
/*     */   public static final byte RC_UI_ERROR = -36;
/*     */   public static final byte RC_UI_MALFORMED_REQUEST = -35;
/*     */   public static final byte RC_UI_MALFORMED_RESPONSE = -34;
/*     */   public static final byte RC_HELP_TOPIC_NOT_FOUND = -26;
/*     */   public static final byte RC_FAIL = -1;
/*     */   public static final int DISK_MAXDRIVES = 256;
/*     */   public static final int DISK_MAXSECTORS = 16777215;
/*     */   public static final int DISK_SECTORSIZE = 256;
/*     */   public static final int DISK_MAX_SYNC_SKIPS = 1;
/*     */   public static final long DISK_SYNC_INOP_PAUSE = 40L;
/*     */   public static final int DISK_HDBDOS_DISKSIZE = 630;
/*     */   public static final int DISK_FORMAT_NONE = 0;
/*     */   public static final int DISK_FORMAT_RAW = 1;
/*     */   public static final int DISK_FORMAT_DMK = 2;
/*     */   public static final int DISK_FORMAT_JVC = 3;
/*     */   public static final int DISK_FORMAT_VDK = 4;
/*     */   public static final int DISK_FORMAT_CCB = 5;
/*     */   public static final int DISK_CONSIDER_NO = 0;
/*     */   public static final int DISK_CONSIDER_MAYBE = 1;
/*     */   public static final int DISK_CONSIDER_YES = 2;
/* 156 */   public static final Boolean DISK_DEFAULT_EXPAND = Boolean.valueOf(true);
/* 157 */   public static final Boolean DISK_DEFAULT_SYNCTO = Boolean.valueOf(true);
/* 158 */   public static final Boolean DISK_DEFAULT_SYNCFROM = Boolean.valueOf(false);
/* 159 */   public static final Boolean DISK_DEFAULT_WRITEPROTECT = Boolean.valueOf(false);
/* 160 */   public static final Boolean DISK_DEFAULT_NAMEDOBJECT = Boolean.valueOf(false);
/*     */   
/*     */   public static final int DISK_DEFAULT_OFFSET = 0;
/*     */   
/*     */   public static final int DISK_DEFAULT_SIZELIMIT = -1;
/*     */   
/*     */   public static final int DISK_IMAGE_HEADER_SIZE = 256;
/*     */   
/*     */   public static final int DISK_FILESYSTEM_OS9 = 0;
/*     */   
/*     */   public static final int DISK_FILESYSTEM_DECB = 1;
/*     */   
/*     */   public static final int DISK_FILESYSTEM_LWFS = 2;
/*     */   
/*     */   public static final int DISK_FILESYSTEM_CCB = 3;
/*     */   
/*     */   public static final int DISK_FILESYSTEM_UNKNOWN = -1;
/*     */   
/*     */   public static final String HELP_DEFAULT_FILE = "help.xml";
/*     */   
/*     */   public static final int DWCMD_DEFAULT_COLS = 80;
/*     */   
/*     */   public static final byte EVENT_TYPE_SERVERCONFIG = 67;
/*     */   
/*     */   public static final byte EVENT_TYPE_INSTANCECONFIG = 73;
/*     */   
/*     */   public static final byte EVENT_TYPE_DISK = 68;
/*     */   
/*     */   public static final byte EVENT_TYPE_LOG = 76;
/*     */   
/*     */   public static final byte EVENT_TYPE_MIDI = 77;
/*     */   
/*     */   public static final byte EVENT_TYPE_NET = 78;
/*     */   
/*     */   public static final byte EVENT_TYPE_PRINT = 80;
/*     */   public static final byte EVENT_TYPE_VSERIAL = 83;
/*     */   public static final byte EVENT_TYPE_STATUS = 64;
/*     */   public static final String EVENT_ITEM_KEY = "k";
/*     */   public static final String EVENT_ITEM_VALUE = "v";
/*     */   public static final String EVENT_ITEM_DRIVE = "d";
/*     */   public static final String EVENT_ITEM_INSTANCE = "i";
/*     */   public static final String EVENT_ITEM_LOGLEVEL = "l";
/*     */   public static final String EVENT_ITEM_TIMESTAMP = "t";
/*     */   public static final String EVENT_ITEM_LOGMSG = "m";
/*     */   public static final String EVENT_ITEM_THREAD = "r";
/*     */   public static final String EVENT_ITEM_LOGSRC = "s";
/*     */   public static final String EVENT_ITEM_INTERVAL = "0";
/*     */   public static final String EVENT_ITEM_MEMTOTAL = "1";
/*     */   public static final String EVENT_ITEM_MEMFREE = "2";
/*     */   public static final String EVENT_ITEM_OPS = "3";
/*     */   public static final String EVENT_ITEM_DISKOPS = "4";
/*     */   public static final String EVENT_ITEM_VSERIALOPS = "5";
/*     */   public static final String EVENT_ITEM_INSTANCES = "6";
/*     */   public static final String EVENT_ITEM_INSTANCESALIVE = "7";
/*     */   public static final String EVENT_ITEM_THREADS = "8";
/*     */   public static final String EVENT_ITEM_UICLIENTS = "9";
/*     */   public static final String EVENT_ITEM_MAGIC = "!";
/*     */   public static final int EVENT_MAX_QUEUE_SIZE = 800;
/*     */   public static final int EVENT_QUEUE_LOGDROP_SIZE = 500;
/*     */   public static final int LOGGING_MAX_BUFFER_EVENTS = 500;
/*     */   public static final long UITHREAD_WAIT_TICK = 200L;
/*     */   public static final long UITHREAD_SERVER_WAIT_TIME = 3000L;
/*     */   public static final long UITHREAD_INSTANCE_WAIT_TIME = 3000L;
/*     */   public static final long SERVER_MEM_UPDATE_INTERVAL = 5000L;
/*     */   public static final long SERVER_SLOW_OP = 200L;
/* 225 */   public static final String[] LOG_LEVELS = new String[] { "ALL", "DEBUG", "INFO", "WARN", "ERROR", "FATAL" };
/*     */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/DWDefs.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */