package com.groupunix.drivewireserver.dwprotocolhandler;

import com.groupunix.drivewireserver.virtualserial.DWVSerialPorts;

public interface DWVSerialProtocol extends DWProtocol 
{

	DWVSerialPorts getVPorts();
	
}
