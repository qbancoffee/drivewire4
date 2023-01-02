package com.groupunix.drivewireserver.xcommands;


public class XCommandResponse {

	private byte responseCode;
	private byte[] responseBytes;
	
	// response with data
	public XCommandResponse(byte rc, byte[] rbytes)
	{
		this.responseCode = rc;
		this.responseBytes = rbytes;
	}
	
	// response code only, append 0 for terminator
	public XCommandResponse(byte rc)
	{
		this.responseCode = rc;
		this.responseBytes = new byte[] { 0 };
	}
	
	
	public byte getResponseCode()
	{
		return this.responseCode;
	}
	
	
	public byte[] getResponseBytes()
	{
		return this.responseBytes;
	}

	
}
