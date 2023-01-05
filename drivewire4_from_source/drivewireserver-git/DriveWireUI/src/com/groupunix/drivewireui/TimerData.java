package com.groupunix.drivewireui;


public class TimerData
{
	private int timerNumber = -1;
	private String timerDesc = "";
	private int timerValue = -1;
	
	
	public TimerData(String l)
	{
		String[] data = l.split("\\|");
		
		
		if (data.length == 3)
		{
			try
			{
				this.timerNumber = Integer.parseInt(data[0]);
				this.timerDesc = data[1];
				this.timerValue = Integer.parseInt(data[2]);
			}
			catch (NumberFormatException e)
			{
				this.timerDesc = "Invalid data!";
			}
		}
		
	}


	public void setTimerNumber(int timerNumber)
	{
		this.timerNumber = timerNumber;
	}


	public int getTimerNumber()
	{
		return timerNumber;
	}


	public void setTimerDesc(String timerDesc)
	{
		this.timerDesc = timerDesc;
	}


	public String getTimerDesc()
	{
		return timerDesc;
	}


	public void setTimerValue(int timerValue)
	{
		this.timerValue = timerValue;
	}


	public int getTimerValue()
	{
		return timerValue;
	}


	public String getTimerDuration()
	{
		String txt = "";
		
		long tv = (this.timerValue & 0xffffffff);
		
		if (tv > 3600000)
		{
			txt += (tv / 3600000) + " hr ";
			tv = tv - ((tv / 3600000) * 3600000);
		}
		
		if (tv > 60000)
		{
			txt += (tv / 60000) + " min ";
			tv = tv - ((tv / 60000) * 60000);
		}
		
		if (tv > 1000)
		{
			txt += (tv / 1000) + " sec ";
			tv = tv - ((tv / 1000) * 1000);
		}
		
		txt += tv + " ms";
		
		
		return txt;
	}

}
