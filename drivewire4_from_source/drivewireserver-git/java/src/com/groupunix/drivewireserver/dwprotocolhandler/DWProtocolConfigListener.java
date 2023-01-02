package com.groupunix.drivewireserver.dwprotocolhandler;

import org.apache.commons.configuration.event.ConfigurationEvent;
import org.apache.commons.configuration.event.ConfigurationListener;

public class DWProtocolConfigListener implements ConfigurationListener 
{


	private DWProtocol dwProto;

	public DWProtocolConfigListener(DWProtocol dwProto)
	{
		super();
		this.dwProto = dwProto;
	}
	
	public void configurationChanged(ConfigurationEvent event) 
	{
		if (!event.isBeforeUpdate())
        {
            if (event.getPropertyName() != null)
            {
            	if (event.getPropertyValue() == null)
            	{
            		this.dwProto.submitConfigEvent( event.getPropertyName(),"");
            	}
            	else
            	{
            		this.dwProto.submitConfigEvent( event.getPropertyName(), event.getPropertyValue().toString());
            	}
            	
            }
        }
            
		
	}

}
