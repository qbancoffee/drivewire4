package com.groupunix.drivewireserver.dwdisk;

import org.apache.commons.configuration.event.ConfigurationEvent;
import org.apache.commons.configuration.event.ConfigurationListener;

public class DWDiskConfigListener implements ConfigurationListener 
{
	private DWDisk disk;

	public DWDiskConfigListener(DWDisk disk)
	{
		super();
		this.disk = disk;
	}
	

	public void configurationChanged(ConfigurationEvent event) 
	{
		if (!event.isBeforeUpdate())
        {
            if (event.getPropertyName() != null)
            {
            	
            	if (event.getPropertyValue() != null)
            		this.disk.submitEvent( event.getPropertyName(), event.getPropertyValue().toString());
            	else
            		this.disk.submitEvent( event.getPropertyName(), "");
            	
            }
        }
            
		
	}

}