package com.groupunix.drivewireui;

public class MIDIProfile {

	private String name;
	private String desc;
	
	
	public MIDIProfile(String name, String desc) 
	{
		this.setName(name);
		this.setDesc(desc);
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getName() {
		return name;
	}


	public void setDesc(String desc) {
		this.desc = desc;
	}


	public String getDesc() {
		return desc;
	}

}
