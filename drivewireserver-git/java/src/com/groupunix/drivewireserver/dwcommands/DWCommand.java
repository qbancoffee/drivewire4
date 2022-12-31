package com.groupunix.drivewireserver.dwcommands;

import com.groupunix.drivewireserver.DWDefs;

public abstract class DWCommand 
{
	protected DWCommandList commands = new DWCommandList(null);;
	
	private DWCommand parentcmd = null;
	
	public abstract String getCommand();
	
	
	public DWCommandList getCommandList()
	{
		return(this.commands);
	}
	
	
	public DWCommandResponse parse(String cmdline)
	{
		return(new DWCommandResponse(false,DWDefs.RC_SERVER_NOT_IMPLEMENTED,"Not implemented (yet?)."));
	}
	
	public DWCommand getParentCmd()
	{
		return this.parentcmd;
	}
	
	public void setParentCmd(DWCommand cmd)
	{
		this.parentcmd = cmd;
	}
	

	public String getShortHelp()
	{
		return("Not implemented.");
	}

	public String getUsage()
	{
		return("Not implemented.");
	}

	public boolean validate(String cmdline)
	{
		return false;
	}
	
}


