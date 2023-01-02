package com.groupunix.drivewireui.cococloud;

import java.io.IOException;
import java.net.Authenticator;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;

import com.groupunix.drivewireui.MainWin;

public class Cloud
{
	
	
	public static int lookupDisk(String sha1)
	{
		try
		{
			Authenticator.setDefault(new CloudAuth());
			URL url = new URL(MainWin.config.getString("CloudURL", "http://127.0.0.1:8080/CoCoCloudServer/diskLookup?sha1=") + sha1);
			
			
			
			XMLConfiguration c = new XMLConfiguration();
			c.load(url.openStream());
			
			if (c.containsKey("Error.Text"))
			{
				// TODO
				System.out.println("ERROR: " + c.configurationAt("Error").getString("text") );
			}
			else if (c.containsKey("Results.DiskID"))
			{
				return c.getInt("Results.DiskID");
			}
			
		} 
		catch (MalformedURLException e)
		{
			// TODO
			e.printStackTrace();
		} catch (ConfigurationException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		return -1;
	}

	public static XMLConfiguration getDiskInfo(int diskID)
	{
		XMLConfiguration res = new XMLConfiguration();
		
		try
		{
			Authenticator.setDefault(new CloudAuth());
			URL url = new URL(MainWin.config.getString("CloudURL", "http://127.0.0.1:8080/CoCoCloudServer/diskInfo?id=") + diskID);
			
			
			res.load(url.openStream());
			
			if (res.containsKey("Error.Text"))
			{
				// TODO
				System.out.println("ERROR: " + res.configurationAt("Error").getString("text") );
			}
			
		} 
		catch (MalformedURLException e)
		{
			// TODO
			e.printStackTrace();
		} catch (ConfigurationException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		
		
		return res;
	}
	
	
	
	
	
}



