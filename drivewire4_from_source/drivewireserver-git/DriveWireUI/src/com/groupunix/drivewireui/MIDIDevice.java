package com.groupunix.drivewireui;

public class MIDIDevice 
{
	private String name;
	private String desc;
	private String vendor;
	private String version;
	private String type;
	private int devnum;
	
	public MIDIDevice(int devnum, String type, String name, String desc, String vendor, String version) 
	{
		this.setDevnum(devnum);
		this.setName(name);
		this.setDesc(desc);
		this.setVendor(vendor);
		this.setVersion(version);
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

	public void setVendor(String vendor) {
		this.vendor = vendor;
	}

	public String getVendor() {
		return vendor;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getVersion() {
		return version;
	}

	public void setDevnum(int devnum) {
		this.devnum = devnum;
	}

	public int getDevnum() {
		return devnum;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

}
