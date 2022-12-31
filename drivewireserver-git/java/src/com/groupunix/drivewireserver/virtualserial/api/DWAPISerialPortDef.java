package com.groupunix.drivewireserver.virtualserial.api;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

import com.groupunix.drivewireserver.DWDefs;
import com.groupunix.drivewireserver.dwcommands.DWCommandResponse;



public class DWAPISerialPortDef 
{
	private int rate = -1;
	private int databits = -1;
	private int stopbits = -1;
	private int parity = -1;
	private int flowcontrol = -1;
	
	public int getRate() {
		return rate;
	}
	public void setRate(int rate) {
		this.rate = rate;
	}
	public int getDatabits() {
		return databits;
	}
	public void setDatabits(int databits) {
		this.databits = databits;
	}
	public int getStopbits() {
		return stopbits;
	}
	public void setStopbits(int stopbits) {
		this.stopbits = stopbits;
	}
	public int getParity() {
		return parity;
	}
	public void setParity(int parity) {
		this.parity = parity;
	}
	public int getFlowcontrol() {
		return flowcontrol;
	}
	
	public void setFlowcontrol(int flowcontrol) 
	{
		this.flowcontrol = flowcontrol;
	}
	
	public void setParams(SerialPort sp) throws NoSuchPortException, PortInUseException, UnsupportedCommOperationException 
	{
		
		if (this.rate == -1)
			this.rate = sp.getBaudRate();
		
		if (this.databits == -1)
			this.databits = sp.getDataBits();
		
		if (this.stopbits == -1)
			this.stopbits = sp.getStopBits();
		
		if (this.parity == -1)
			this.parity = sp.getParity();
		
		sp.setSerialPortParams(rate, databits, stopbits, parity);
		
		if (this.flowcontrol > -1)
			sp.setFlowControlMode(this.flowcontrol);
	
	
	}	
}
