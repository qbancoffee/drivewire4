package com.groupunix.drivewireui.simplewizard;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

import org.eclipse.jface.wizard.Wizard;

import com.groupunix.drivewireui.DWUIOperationFailedException;
import com.groupunix.drivewireui.MainWin;
import com.groupunix.drivewireui.PlatformDef;
import com.groupunix.drivewireui.UIUtils;

public class SimpleWizard extends Wizard
{
	private PlatformDef platform = null;
	private String serialport= null;
	private boolean midi = true;
	
	private ArrayList<String> yays = new ArrayList<String>();
	
	private int serialRate = 0;
	private boolean serialRTS = false;
	private boolean serialDTR= false;
	private String serialParity = "none";
	private int serialDatabits = 8;
	private String serialStopbits = "1";
	private boolean SerialRTSCTSin = false;
	private boolean SerialRTSCTSout = false;
	private boolean SerialXONXOFFin = false;
	private boolean SerialXONXOFFout = false;
	private boolean printerText = true;
	private String printerDir = "cocoprints";
	
	private boolean tcpMode = false;
	private boolean tcpClient = false;
	private String tcpClientHost = null;
	private int tcpPort = 65504;
	
	IntroPage introPage;
	PlatformPage platformPage;
	SerialPage serialPage;
	SerialParamPage serialParamPage;
	MIDIPage midiPage;
	PrinterPage printerPage;
	FinishPage finishPage;
	TCPPage tcpPage;
	
	
	
	public SimpleWizard()
	{
		setWindowTitle("Configuration Wizard");
		
		
		

		yays.add("Yes!");
		yays.add("Excellent!");
		yays.add("Good choice.");
		yays.add("One of my favorites.");
		yays.add("This should be good.");
		yays.add("Let's go!");
		yays.add("Works for me..");
		yays.add("Sounds good.");
		yays.add("Sounds like fun.");
		yays.add("Let's do this!");
		yays.add("Perfect!");
		yays.add("We can handle that.");
		yays.add("Good deal.");
		yays.add("Awesome!");
		yays.add("Nice!");
		yays.add("I like it.");
		yays.add("I can't wait!");
		yays.add("Next question..");
		yays.add("Nice one.");
		yays.add("Alright!");
		yays.add("Great!");
		yays.add("Great choice!");
		yays.add("We can do this.");
		yays.add("This I like!");
		
	}

	@Override
	public void addPages()
	{
		this.introPage = new IntroPage();
		this.platformPage = new PlatformPage();
		this.serialPage = new SerialPage();
		this.serialParamPage = new SerialParamPage();
		this.midiPage = new MIDIPage();
		this.printerPage = new PrinterPage();
		this.finishPage = new FinishPage();
		this.tcpPage = new TCPPage();
		
		
		addPage(introPage);
		addPage(platformPage);
		addPage(serialPage);
		addPage(serialParamPage);
		addPage(tcpPage);
		addPage(midiPage);
		addPage(printerPage);
		addPage(finishPage);
	}

	@Override
	public boolean performFinish()
	{
		ArrayList<String> cmds = new ArrayList<String>();
		
		if (this.tcpMode)
		{
			if (this.tcpClient)
			{
				cmds.add("dw config set DeviceType tcp-client");
				cmds.add("dw config set TCPClientHost " + this.tcpClientHost);
				cmds.add("dw config set TCPClientPort " + this.tcpPort);
				
			}
			else
			{
				cmds.add("dw config set DeviceType tcp-server");
				cmds.add("dw config set TCPServerPort " + this.tcpPort);
				
			}
			
			cmds.add("dw config set [@name] "+ this.platform.name + " via TCP");
		}
		else
		{
			cmds.add("dw config set DeviceType serial");
			cmds.add("dw config set SerialDevice " + this.serialport);
			cmds.add("dw config set SerialRate " + this.serialRate);
			cmds.add("dw config set SerialParity " + this.serialParity);
			cmds.add("dw config set SerialStopbits " + this.serialStopbits);
			cmds.add("dw config set SerialDTR " + this.serialDTR);
			cmds.add("dw config set SerialRTS " + this.serialRTS);
			cmds.add("dw config set SerialFlowControl_RTSCTS_IN " + this.SerialRTSCTSin);
			cmds.add("dw config set SerialFlowControl_RTSCTS_OUT " + this.SerialRTSCTSout);
			
			cmds.add("dw config set [@name] "+ this.platform.name + " on " + this.serialport);
		}
		
		
		cmds.add("dw config set [@desc] Autocreated " +  new java.sql.Timestamp(Calendar.getInstance().getTime().getTime()).toString() );
		
		
		cmds.add("dw config set UseMIDI " + this.midi);
		
		String printertype = "Text";
		
		if (!this.printerText)
			printertype = "FX80";
		
		for (int i = 0;i<=MainWin.getInstanceConfig().getMaxIndex("Printer");i++)
		{
			if (MainWin.getInstanceConfig().getString("Printer("+i+")[@name]").equals(printertype))
			{
				cmds.add("dw config set CurrentPrinter " + printertype);
				cmds.add("dw config set Printer("+i+").OutputDir " + this.printerDir);
			}
			
		}
		
		try
		{
			UIUtils.simpleConfigServer(cmds);
		} 
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DWUIOperationFailedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
			
		return true;
	}

	
	
	
	@Override
	public boolean canFinish()
	{
		if ((this.platform != null) && ((this.serialport != null) || this.tcpMode) )
			return true;
			
		return false;
	}

	
	
	
	
	
	
	public PlatformDef getPlatform()
	{
		return this.platform;
	}

	public void setPlatform(PlatformDef data)
	{
		this.platform = data;
		
		if (data != null)
		{
			this.serialRate = data.rate;
			this.serialParity = data.parity;
			this.serialStopbits = data.stopbits;
			this.serialDTR = data.setDTR;
			this.serialRTS = data.setRTS;
			this.SerialRTSCTSin = data.useRTSin;
			this.SerialRTSCTSout = data.useRTSout;
			this.tcpMode = data.tcp;
			
		}
	}

	public void setSerialPort(String pname)
	{
		this.serialport = pname;
	}
	
	public String getSerialPort()
	{
		return this.serialport;
	}

	public void setMIDI(boolean b)
	{
		this.midi  = b;
	}
	
	public boolean getMIDI()
	{
		return this.midi;
	}

	public void setSerialRate(int serial_rate)
	{
		this.serialRate = serial_rate;
	}

	public int getSerialRate()
	{
		return serialRate;
	}

	public void setSerialRTS(boolean serialRTS)
	{
		this.serialRTS = serialRTS;
	}

	public boolean isSerialRTS()
	{
		return serialRTS;
	}

	public void setSerialDTR(boolean serialDTR)
	{
		this.serialDTR = serialDTR;
	}

	public boolean isSerialDTR()
	{
		return serialDTR;
	}

	public void setSerialParity(String serialParity)
	{
		this.serialParity = serialParity;
	}

	public String getSerialParity()
	{
		return serialParity;
	}

	public void setSerialDatabits(int serialDatabits)
	{
		this.serialDatabits = serialDatabits;
	}

	public int getSerialDatabits()
	{
		return serialDatabits;
	}

	public void setSerialStopbits(String serialStopbits)
	{
		this.serialStopbits = serialStopbits;
	}

	public String getSerialStopbits()
	{
		return serialStopbits;
	}

	public void setSerialRTSCTSin(boolean serialRTSCTSin)
	{
		SerialRTSCTSin = serialRTSCTSin;
	}

	public boolean isSerialRTSCTSin()
	{
		return SerialRTSCTSin;
	}

	public void setSerialRTSCTSout(boolean serialRTSCTSout)
	{
		SerialRTSCTSout = serialRTSCTSout;
	}

	public boolean isSerialRTSCTSout()
	{
		return SerialRTSCTSout;
	}

	public void setSerialXONXOFFin(boolean serialXONXOFFin)
	{
		SerialXONXOFFin = serialXONXOFFin;
	}

	public boolean isSerialXONXOFFin()
	{
		return SerialXONXOFFin;
	}

	public void setSerialXONXOFFout(boolean serialXONXOFFout)
	{
		SerialXONXOFFout = serialXONXOFFout;
	}

	public boolean isSerialXONXOFFout()
	{
		return SerialXONXOFFout;
	}

	public void setPrinterTextMode(boolean selection)
	{
		this.printerText  = selection;
	}
	
	public boolean getPrinterTextMode()
	{
		return this.printerText;
	}

	public void setPrinterDir(String text)
	{
		this.printerDir  = text;
	}
	
	public String getPrinterDir()
	{
		return this.printerDir;
	}

	

	public void setTcpMode(boolean tcpMode)
	{
		this.tcpMode = tcpMode;
	}

	public boolean isTcpMode()
	{
		return tcpMode;
	}

	public void setTcpClient(boolean tcpClient)
	{
		this.tcpClient = tcpClient;
	}

	public boolean isTcpClient()
	{
		return tcpClient;
	}

	public void setTcpClientHost(String tcpServer)
	{
		this.tcpClientHost = tcpServer;
	}

	public String getTcpClientHost()
	{
		return tcpClientHost;
	}

	public void setTcpPort(int tcpPort)
	{
		this.tcpPort = tcpPort;
	}

	public int getTcpPort()
	{
		return tcpPort;
	}
	
	public String getRandomYay()
	{
		Random r = new Random();
		return yays.get(r.nextInt(this.yays.size()));
	}
}
