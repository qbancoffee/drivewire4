package com.groupunix.drivewireserver.xcommands;

import com.groupunix.drivewireserver.DWDefs;
import com.groupunix.drivewireserver.dwprotocolhandler.DWUtils;


public class XCmdServerMemory extends XCommand {

	static final String command = "xsm";


	public String getCommand() 
	{
		return command;
		
	}

	public XCommandResponse parse(String cmdline)
	{
		byte[] res = new byte[10];
		
		short used = (short) (Runtime.getRuntime().totalMemory() / 1024 / 1024);
		short free = (short) (Runtime.getRuntime().freeMemory() / 1024 / 1024);
		short max = (short) (Runtime.getRuntime().maxMemory() / 1024 / 1024);
		
		int pos = 0;
		
		res[pos++] = 2;
		res[pos++] = (byte) (0xff00 & used);
		res[pos++] = (byte) (0x00ff & used);
		
		res[pos++] = 2;
		res[pos++] = (byte) (0xff00 & free);
		res[pos++] = (byte) (0x00ff & free);
		
		res[pos++] = 2;
		res[pos++] = (byte) (0xff00 & max);
		res[pos++] = (byte) (0x00ff & max);
		
		res[pos++] = 0;
				
		return(new XCommandResponse(DWDefs.RC_SUCCESS, res));
	}


	
	
}