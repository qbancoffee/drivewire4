package com.groupunix.drivewireserver.dwcommands;

import gnu.io.PortInUseException;
import gnu.io.SerialPort;

import com.groupunix.drivewireserver.DWDefs;
import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocol;


public class DWCmdServerShowSerial extends DWCommand {



	DWCmdServerShowSerial(DWProtocol dwProto, DWCommand parent)
	{

		setParentCmd(parent);
	}
	
	public String getCommand() 
	{
		return "serial";
	}


	
	public String getShortHelp() 
	{
		return "Show serial device information";
	}


	public String getUsage() 
	{
		return "dw server show serial";
	}

	public DWCommandResponse parse(String cmdline) 
	{
		String text = new String();
		
		text += "Server serial devices:\r\n\r\n";
		
		 @SuppressWarnings("unchecked")
		java.util.Enumeration<gnu.io.CommPortIdentifier> thePorts =  gnu.io.CommPortIdentifier.getPortIdentifiers();
	        
		 while (thePorts.hasMoreElements()) 
	     {
			 try
			 {
		            gnu.io.CommPortIdentifier com = thePorts.nextElement();
		            if (com.getPortType() == gnu.io.CommPortIdentifier.PORT_SERIAL)
		            {
		            	text += com.getName() + "  ";
		            	
		            	try
		            	{
		            		SerialPort serialPort = (SerialPort) com.open("DWList",2000);
		            		
		            		text += serialPort.getBaudRate() + " bps  ";
		            		text += serialPort.getDataBits();
		            		
		            		
		            		switch(serialPort.getParity())
		            		{
		            			case SerialPort.PARITY_NONE:
		            				text += "N";
		            				break;
		            				
		            			case SerialPort.PARITY_EVEN:
		            				text += "E";
		            				break;
		            			
		            			case SerialPort.PARITY_MARK:
		            				text += "M";
		            				break;
		            			
		            			case SerialPort.PARITY_ODD:
		            				text += "O";
		            				break;
		            			
		            			case SerialPort.PARITY_SPACE:
		            				text += "S";
		            				break;
		            				
		            		}
		            		
		            		
		            		text += serialPort.getStopBits();
		            		
		            		if (serialPort.getFlowControlMode() == SerialPort.FLOWCONTROL_NONE)
		            			text += "  No flow control  ";
		            		else
		            		{
		            			text += "  ";
		            			
		            			if ((serialPort.getFlowControlMode() & SerialPort.FLOWCONTROL_RTSCTS_IN) == SerialPort.FLOWCONTROL_RTSCTS_IN)
		            				text += "In: RTS/CTS  ";
		            			
		            			if ((serialPort.getFlowControlMode() & SerialPort.FLOWCONTROL_RTSCTS_OUT) == SerialPort.FLOWCONTROL_RTSCTS_OUT)
		            				text += "Out: RTS/CTS  ";
		            			
		            			if ((serialPort.getFlowControlMode() & SerialPort.FLOWCONTROL_XONXOFF_IN) == SerialPort.FLOWCONTROL_XONXOFF_IN)
		            				text += "In: XOn/XOff  ";
		            			
		            			if ((serialPort.getFlowControlMode() & SerialPort.FLOWCONTROL_XONXOFF_OUT) == SerialPort.FLOWCONTROL_XONXOFF_OUT)
		            				text += "Out: XOn/XOff  ";
		            		}
		            		
		            		
		            		text += " CD:" + yn(serialPort.isCD());
		            		
	            			text += " CTS:" + yn(serialPort.isCTS());
		            		
		            		text += " DSR:" + yn(serialPort.isDSR());
		            		
		            		text += " DTR:" + yn(serialPort.isDTR());
		            		
	            			text += " RTS:" + yn(serialPort.isRTS());
		            		
		            		
		            		text += "\r\n";
		            		
		            		serialPort.close();
		            	}
		            	catch (PortInUseException e1)
		            	{
		            		text += "In use by " + e1.getMessage() + "\r\n";
		            	}
		            	
		            }
			 }
			 catch (Exception e)
			 {
				 return(new DWCommandResponse(false, DWDefs.RC_SERVER_IO_EXCEPTION, "While gathering serial port info: " + e.getMessage() ));
			 }
	     }
		            	
		
		return(new DWCommandResponse(text));
	}
	
	private String yn(boolean cd) {
		if (cd)
			return "Y";
		
		return "n";
	}

	public boolean validate(String cmdline) 
	{
		return(true);
	}
}
