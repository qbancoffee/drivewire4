package com.groupunix.drivewireserver;

import java.sql.Date;
import java.util.HashMap;

public class Version 
{

	private int major;
	private int minor;
	private int build;
	private String revison;
	private Date date;
	private String priority;
	private String target;
	private String description;
	private HashMap<String,String> files = new HashMap<String,String>();
	private boolean restart = true;
	
	public Version(int ma, int mi, int b, String r, Date d)
	{
		this.major = ma;
		this.minor = mi;
		this.build = b;
		this.revison = r;
		this.date = d;
	}
	
	public Version(int ma, int mi, int b, String r)
	{
		this.major = ma;
		this.minor = mi;
		this.build = b;
		this.revison = r;
		this.date = null;
	}

	@Override
	public String toString()
	{
		return this.major + "." + this.minor + "." + this.build + this.revison;
	}
	
	
	public int getMajor() {
		return major;
	}
	public void setMajor(int major) {
		this.major = major;
	}
	public int getMinor() {
		return minor;
	}
	public void setMinor(int minor) {
		this.minor = minor;
	}
	public int getBuild() {
		return build;
	}
	public void setBuild(int build) {
		this.build = build;
	}
	public String getRevison() {
		return revison;
	}
	public void setRevison(String revison) {
		this.revison = revison;
	}


	public Date getDate() {
		return date;
	}


	public void setDate(Date date) {
		this.date = date;
	}

	public boolean newerThan(Version v) 
	{
		if (this.major > v.major)
			return true;
		
		if (this.major == v.major)
		{
			if (this.minor > v.minor)
				return true;
		
			if (this.minor == v.minor)
			{
				if (this.build > v.build)
					return true;
				
				if ((this.build == v.build) && (this.revison.compareTo(v.revison) > 0))
					return true;
				
			}
		}
		
		return false;
	}

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isRestart() {
		return restart;
	}

	public void setRestart(boolean restart) {
		this.restart = restart;
	}

	public HashMap<String,String> getFiles() {
		return files;
	}

	public void setFiles(HashMap<String,String> files) {
		this.files = files;
	}

	public boolean equalsVersion(Version lv) 
	{
		if (lv.getMajor() != this.major)
			return false;
		
		if (lv.getMinor() != this.minor)
			return false;
		
		if (lv.getBuild() != this.build)
			return false;
		
		if (!lv.getRevison().equals(this.revison))
			return false;
		
		
		return true;
	}

	
	
	@SuppressWarnings("deprecation")
	public byte getOS9Year() 
	{
		if (this.date != null)
			return (byte) date.getYear();
		return 0;
	}

	@SuppressWarnings("deprecation")
	public byte getOS9Month() 
	{
		if (this.date != null)
			return (byte) date.getMonth();
		return 0;
	}
	
	@SuppressWarnings("deprecation")
	public byte getOS9Day() 
	{
		if (this.date != null)
			return (byte) date.getDay();
		return 0;
	}
	
	
	
	
	
}
