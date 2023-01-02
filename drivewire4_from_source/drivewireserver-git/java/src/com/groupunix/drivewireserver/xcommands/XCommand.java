package com.groupunix.drivewireserver.xcommands;

import com.groupunix.drivewireserver.DWDefs;

public abstract class XCommand 
{
	public abstract String getCommand();
	
	public XCommandResponse parse(String cmdline)
	{
		return(new XCommandResponse(DWDefs.RC_SERVER_NOT_IMPLEMENTED));
	}
	
}


