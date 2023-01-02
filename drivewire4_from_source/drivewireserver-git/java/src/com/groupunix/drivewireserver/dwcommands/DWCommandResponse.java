package com.groupunix.drivewireserver.dwcommands;

import com.groupunix.drivewireserver.DWDefs;

public class DWCommandResponse {

	private boolean success;
	private byte responseCode;
	private String responseText;
	private byte[] responseBytes;
	private boolean usebytes = false;
	
	public DWCommandResponse(boolean success, byte responsecode, String responsetext)
	{
		this.success = success;
		this.responseCode = responsecode;
		this.responseText = responsetext;
		
	}
	
	public DWCommandResponse(String responsetext)
	{
		this.success = true;
		this.responseCode = DWDefs.RC_SUCCESS;
		this.responseText = responsetext;
		
	}
	
	public DWCommandResponse(byte[] responsebytes)
	{
		this.success = true;
		this.responseCode = DWDefs.RC_SUCCESS;
		this.responseBytes = responsebytes;
		this.usebytes = true;
		
	}
	
	public boolean getSuccess()
	{
		return this.success;
	}
	
	public byte getResponseCode()
	{
		return this.responseCode;
	}
	
	public String getResponseText()
	{
		return this.responseText;
	}
	
	public byte[] getResponseBytes()
	{
		return this.responseBytes;
	}

	public boolean isUsebytes() {
		return usebytes;
	}
}
