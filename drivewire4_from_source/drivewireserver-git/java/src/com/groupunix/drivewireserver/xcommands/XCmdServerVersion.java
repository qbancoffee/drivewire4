package com.groupunix.drivewireserver.xcommands;

import java.util.Date;

import com.groupunix.drivewireserver.DWDefs;
import com.groupunix.drivewireserver.DriveWireServer;


public class XCmdServerVersion extends XCommand {

	static final String command = "xsv";


	public String getCommand() 
	{
		return command;
	}

	public XCommandResponse parse(String cmdline)
	{
		byte[] res = new byte[12];
		
		res[0] = (byte) 4; // version
		res[1] = (byte) DriveWireServer.DWVersion.getMajor();
		res[2] = (byte) DriveWireServer.DWVersion.getMinor();
		res[3] = (byte) DriveWireServer.DWVersion.getBuild();
		res[4] = (byte) DriveWireServer.DWVersion.getRevison().charAt(0);
		
		res[5] = (byte) 5; // date
		res[6] = DriveWireServer.DWVersion.getOS9Year();
		res[7] = DriveWireServer.DWVersion.getOS9Month();
		res[8] = DriveWireServer.DWVersion.getOS9Day();
		res[9] = 0;
		res[10] = 0;
		
		res[11] = 0;
		
		return(new XCommandResponse(DWDefs.RC_SUCCESS, res));
	}


	
	
}