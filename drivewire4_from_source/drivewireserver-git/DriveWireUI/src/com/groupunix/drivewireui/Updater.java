package com.groupunix.drivewireui;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;

public class Updater 
{

	public static final String UPDATEURL = "http://dwupdate.cococoding.com";
	
	private XMLConfiguration upconf;
	
	private boolean userIgnore = false;

	private boolean updateComplete = false;
	
	
	
	public Updater() throws ConfigurationException, Exception 
	{
		upconf = new XMLConfiguration();
		
		upconf.load(MainWin.config.getString("UpdateURL", UPDATEURL) + "/updates.xml");
		
		
		
	}
	

	
	public Version getLatestVersion()
	{
		Version lv = new Version(0,0,0,"","");
		
		for (Object verobj : upconf.configurationsAt("version"))
		{
			HierarchicalConfiguration ver = (HierarchicalConfiguration) verobj;
			
			if ( ( new Version(ver.getInt("major", 0), ver.getInt("minor", 0) , ver.getInt("build", 0),ver.getString("revision", ""), ver.getString("date", ""))).newerThan(lv))
			{
				// load full version object details
				lv.setMajor(ver.getInt("major", 0));
				lv.setMinor(ver.getInt("minor", 0));
				lv.setBuild(ver.getInt("build", 0));
				lv.setRevison(ver.getString("revision", ""));
				lv.setDate(ver.getString("date", ""));
				
				lv.setPriority(((HierarchicalConfiguration)ver).getString("priority", ""));
				lv.setTarget(((HierarchicalConfiguration)ver).getString("target", ""));
				lv.setDescription(((HierarchicalConfiguration)ver).getString("description", ""));
				lv.setRestart(((HierarchicalConfiguration)ver).getBoolean("restart", true));
				
				lv.getFiles().clear();
				
				for (int x = 0; x <= ((HierarchicalConfiguration)ver).getMaxIndex("file");x++)
				{
					lv.getFiles().put( ((HierarchicalConfiguration)ver).getString("file(" + x + ")[@name]","")   , ((HierarchicalConfiguration)ver).getString("file(" + x + ")[@sha]","")  );
				}
				
				
			}
		}
		
		return(lv);
	}
	
	
	
	 public static String getURLText(String url) throws Exception 
	 {
	
		 URL website = new URL(url);
	     URLConnection connection = website.openConnection();
	     BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

		StringBuilder response = new StringBuilder();
		String inputLine;
		
		while ((inputLine = in.readLine()) != null) 
		    response.append(inputLine);
		
		in.close();
		
		return response.toString();
	    
	 }



	public boolean isUserIgnore() {
		return userIgnore;
	}



	public void setUserIgnore(boolean userIgnore) {
		this.userIgnore = userIgnore;
	}



	public boolean isUpdateComplete() 
	{
		return updateComplete;
	}
	
	public void setUpdateComplete(boolean b) 
	{
		this.updateComplete = b;
	}



	public Version getVersion(int major, int minor, int build, String revision) 
	{
	
		Version lv = null;
		Version tv = new Version(major, minor, build, revision, "");
		
		
		for (Object verobj : upconf.configurationsAt("version"))
		{
			HierarchicalConfiguration ver = (HierarchicalConfiguration) verobj;
			
			Version cv =  new Version(ver.getInt("major", 0), ver.getInt("minor", 0) , ver.getInt("build", 0),ver.getString("revision", ""), ver.getString("date", ""));
			
			if ( cv.equalsVersion(tv))
			{
				lv = new Version(ver.getInt("major", 0), ver.getInt("minor", 0), ver.getInt("build", 0), ver.getString("revision", ""), ver.getString("date", ""));
				
				lv.setPriority(((HierarchicalConfiguration)ver).getString("priority", ""));
				lv.setTarget(((HierarchicalConfiguration)ver).getString("target", ""));
				lv.setDescription(((HierarchicalConfiguration)ver).getString("description", ""));
				lv.setRestart(((HierarchicalConfiguration)ver).getBoolean("restart", true));
				
				lv.getFiles().clear();
				
				for (int x = 0; x <= ((HierarchicalConfiguration)ver).getMaxIndex("file");x++)
				{
					lv.getFiles().put( ((HierarchicalConfiguration)ver).getString("file(" + x + ")[@name]","")   , ((HierarchicalConfiguration)ver).getString("file(" + x + ")[@sha]","")  );
				}
				
				
			}
		}
		
		return(lv);
		
		
	}
	

}
