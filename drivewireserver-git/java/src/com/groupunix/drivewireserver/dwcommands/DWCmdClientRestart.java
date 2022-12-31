package com.groupunix.drivewireserver.dwcommands;





import com.groupunix.drivewireserver.dwprotocolhandler.DWVSerialProtocol;

public class DWCmdClientRestart extends DWCommand {



	private DWVSerialProtocol dwproto;


	public DWCmdClientRestart(DWVSerialProtocol dwProto2,DWCommand parent)
	{
		this.dwproto = dwProto2;
		setParentCmd(parent);

	}
	
	public String getCommand() 
	{
		return "restart";
	}

	
	public String getShortHelp() 
	{
		return "Restart client device";
	}


	public String getUsage() 
	{
		return "dw client restart";
	}

	public DWCommandResponse parse(String cmdline) 
	{
		return(doStart());
	}

	
	private DWCommandResponse doStart() 
	{
		
		this.dwproto.getVPorts().setRebootRequested(true);
		
		return(new DWCommandResponse("Restart pending"));
	}
	
	
	public boolean validate(String cmdline) 
	{
		return(true);
	}
}
