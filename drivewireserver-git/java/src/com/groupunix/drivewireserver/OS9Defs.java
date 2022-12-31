package com.groupunix.drivewireserver;



public class OS9Defs
{
	// Constants from OS9
	
	// Mode byte
	public static final byte MODE_R = (byte) 1;
	public static final byte MODE_W = (byte) 2;
	public static final byte MODE_E = (byte) 4;
	public static final byte MODE_PR = (byte) 8;
	public static final byte MODE_PW = (byte) 16;
	public static final byte MODE_PE = (byte) 32;
	public static final byte MODE_SHARE = (byte) 64;
	public static final byte MODE_DIR = (byte) 128;
	
	
	// Status codes for Get/SetStat
	public static final byte SS_Opt = 0;
	public static final byte SS_Ready = 1;
	public static final byte SS_Size = 2;
	public static final byte SS_Reset = 3;
	public static final byte SS_WTrk = 4;
	public static final byte SS_Pos = 5;
	public static final byte SS_EOF = 6;
	public static final byte SS_Link = 7;
	public static final byte SS_ULink = 8;
	public static final byte SS_Feed = 9;
	public static final byte SS_Frz = 10;
	public static final byte SS_SPT = 11;
	public static final byte SS_SQD = 12;
	public static final byte SS_DCmd = 13;
	public static final byte SS_DevNm = 14;
	public static final byte SS_FD = 15;
	public static final byte SS_Ticks = 16;
	public static final byte SS_Lock = 17;
	
	public static final byte SS_KySns = 0x27;
	
	public static final byte SS_DirEnt = 0x21;
	
	
	
	
	
	
// General commands
	
	public static final byte CMD_Escape = 0x1b;
	
	public static final byte CMD_DWExt = 0x7F;
		public static final byte CMD_DWExt_DevName = 0x01;
		public static final byte CMD_DWExt_DefWin = 0x02;
		public static final byte CMD_DWExt_Title = 0x03;
		public static final byte CMD_DWExt_Palette = 0x04;
		public static final byte CMD_DWExt_Icon = 0x05;
			public static final byte CMD_DWExt_Icon_Normal = 0x00;
			public static final byte CMD_DWExt_Icon_OK = 0x01;
			public static final byte CMD_DWExt_Icon_Info = 0x02;
			public static final byte CMD_DWExt_Icon_Warn = 0x03;
			public static final byte CMD_DWExt_Icon_Error = 0x04;
			public static final byte CMD_DWExt_Icon_Busy = 0x05;
			
		
	public static final byte CMD_BColor = 0x33;
	public static final byte CMD_BoldSw = 0x3D;
	public static final byte CMD_Border = 0x34;
	public static final byte CMD_CWArea = 0x25;
	public static final byte CMD_DefColr = 0x30;
	public static final byte CMD_DfnGPBuf = 0x29;
	public static final byte CMD_DWEnd = 0x24;
	public static final byte CMD_DWProtSw = 0x36;
	public static final byte CMD_DWSet = 0x20;
	
		public static final byte STY_CurrentDisplay = (byte)0xff;
		public static final byte STY_CurrentProcess = 0x00;
		public static final byte STY_Text40 = 0x01;
		public static final byte STY_Text80 = 0x02;
		public static final byte STY_GfxHiRes2Col = 0x05;
		public static final byte STY_GfxLoRes4Col = 0x06;
		public static final byte STY_GfxHiRes4Col = 0x07;
		public static final byte STY_GfxLoRes16Col = 0x08;
		
	public static final byte CMD_FColor = 0x32;
	public static final byte CMD_Font = 0x3a;
	public static final byte CMD_GCSet = 0x39;
	public static final byte CMD_GetBlk = 0x2c;
	public static final byte CMD_GPLoad = 0x2b;
	public static final byte CMD_KilBuf = 0x2a;
	public static final byte CMD_LSet = 0x2f;
	public static final byte CMD_OWEnd = 0x23;
	public static final byte CMD_OWSet = 0x22;
	public static final byte CMD_Palette = 0x31;
	public static final byte CMD_PropSw = 0x3f;
	public static final byte CMD_PSet = 0x2e;
	public static final byte CMD_PutBlk = 0x2d;
	public static final byte CMD_ScaleSw = 0x35;
	public static final byte CMD_Select = 0x21;
	public static final byte CMD_TCharSw = 0x3c;
	
	
	// Drawing commands
	
	public static final byte CMD_Arc3P = 0x52;
	public static final byte CMD_Bar = 0x4a;
	public static final byte CMD_RBar = 0x4b;
	public static final byte CMD_Box = 0x48;
	public static final byte CMD_RBox = 0x49;
	public static final byte CMD_Circle = 0x50;
	public static final byte CMD_Ellipse = 0x51;
	public static final byte CMD_FFill = 0x4f;
	public static final byte CMD_Line = 0x44;
	public static final byte CMD_RLine = 0x45;
	public static final byte CMD_LineM = 0x46;
	public static final byte CMD_RLineM = 0x47;
	public static final byte CMD_Point = 0x42;
	public static final byte CMD_RPoint = 0x43;
	public static final byte CMD_PutGC = 0x4e;
	public static final byte CMD_SetDPtr = 0x40;
	public static final byte CMD_RSetDPtr = 0x41;
	
		
	// Text commands
	public static final byte CTL_Home = 0x01;
	public static final byte CTL_Position = 0x02;
	public static final byte CTL_EraseLine = 0x03;
	public static final byte CTL_EraseToEOL = 0x04;
	public static final byte CTL_CursorOnOff = 0x05;
		public static final byte CTL_CursorOnOff_Off = 0x20;
		public static final byte CTL_CursorOnOff_On = 0x21;
	public static final byte CTL_CursorRight = 0x06;
	public static final byte CTL_Bell = 0x07;
	public static final byte CTL_CursorLeft = 0x08;
	public static final byte CTL_CursorUp = 0x09;
	public static final byte CTL_CursorDown = 0x0a;
	public static final byte CTL_EraseToEOS = 0x0b;
	public static final byte CTL_ClearScreen = 0x0c;
	public static final byte CTL_CR = 0x0d;
	
	public static final byte CTL_Extended = 0x1f;
		public static final byte CTL_Ext_Reverse_On = 0x20;
		public static final byte CTL_Ext_Reverse_Off = 0x21;
		public static final byte CTL_Ext_Underline_On = 0x22;
		public static final byte CTL_Ext_Underline_Off = 0x23;
		public static final byte CTL_Ext_Blink_On = 0x24;
		public static final byte CTL_Ext_Blink_Off = 0x25;
		public static final byte CTL_Ext_Insert_Line = 0x30;
		public static final byte CTL_Ext_Delete_Line = 0x31;
		
		
	
}
