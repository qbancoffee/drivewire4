package com.groupunix.drivewireserver;

import java.util.HashMap;
import java.util.Set;

public class DWEvent {

	private byte eventType;
	private int eventInstance = -1;
	
	private HashMap<String, String> params = new HashMap<String, String>();
	
	public DWEvent(byte eventType, int instance) 
	{
		this.setEventInstance(instance);
		this.setEventType(eventType);
	}
	
	public void setParam(String key, String val)
	{
		this.params.put(key,val);
	}

	public void setParam(String key, byte val) 
	{
		this.params.put(key, String.format("%02x", (0xff & val)));
	}

	public void setParam(String key, byte[] val) 
	{
		String bytes = "";
		
		for (byte b : val)
		{
			bytes += String.format("%02x", (0xff & b));
		}
		
		this.params.put(key, bytes);
			
	}
	
	
	public boolean hasParam(String key)
	{
		return(this.params.containsKey(key));
	}
	
	public String getParam(String key)
	{
		if (this.params.containsKey(key))
			return(this.params.get(key));
		return(null);
	}
	
	public Set<String> getParamKeys()
	{
		return(this.params.keySet());
	}

	public void setEventType(byte eventType) {
		this.eventType = eventType;
	}

	public byte getEventType() {
		return eventType;
	}

	public int getEventInstance() {
		return eventInstance;
	}

	public void setEventInstance(int eventInstance) {
		this.eventInstance = eventInstance;
	}

	


}
