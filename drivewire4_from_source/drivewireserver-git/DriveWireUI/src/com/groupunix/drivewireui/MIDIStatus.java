package com.groupunix.drivewireui;

import java.util.LinkedHashMap;
import java.util.Set;

public class MIDIStatus 
{
	private String currentProfile = null;
	private String currentDevice = null;
	
	private LinkedHashMap<String, MIDIProfile> profiles;
	private LinkedHashMap<String, MIDIDevice> devices;
	
	private boolean enabled = false;
	private boolean voicelock = false;
	
	public MIDIStatus()
	{
		this.profiles = new LinkedHashMap<String, MIDIProfile>();
		this.devices = new LinkedHashMap<String, MIDIDevice>();
	}

	public Set<String> getProfiles() 
	{
		return profiles.keySet();
	}


	public Set<String> getDevices() 
	{
		return devices.keySet();
	}


	public void setCurrentDevice(String currentDevice) 
	{
		this.currentDevice = currentDevice;
	}


	public void addDevice(int devno, String type, String name, String desc, String vendor, String version)
	{
		this.devices.put(name, new MIDIDevice(devno, type, name, desc, vendor, version));
	}
	
	public MIDIDevice getDevice(String key)
	{
		if (this.devices.containsKey(key))
			return(this.devices.get(key));
		return(null);
	}
	
	public void addProfile(String name, String desc)
	{
		this.profiles.put(name, new MIDIProfile(name, desc));
	}
	
	public MIDIProfile getProfile(String key)
	{
		if (this.profiles.containsKey(key))
		{
			return(this.profiles.get(key));
		}
		
		return(null);
	}

	public String getCurrentDevice() 
	{
		return currentDevice;
	}

	public void setCurrentProfile(String currentProfile) 
	{
		this.currentProfile = currentProfile;
	}

	public String getCurrentProfile() 
	{
		return currentProfile;
	}


	public void setEnabled(boolean enabled) 
	{
		this.enabled = enabled;
	}

	public boolean isEnabled() 
	{
		return enabled;
	}

	public void setVoiceLock(boolean b) 
	{
		this.voicelock = b;
		
	}
	
	public boolean isVoiceLock()
	{
		return this.voicelock;
	}
	
	
}
