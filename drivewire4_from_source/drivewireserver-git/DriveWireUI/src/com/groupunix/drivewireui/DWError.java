package com.groupunix.drivewireui;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DWError
{
	private int errno = -1;
	private String title = null;
	private String summary = null;
	private String detail = null;
	private boolean gui = false;
	
	public DWError(String t, String s, String d, boolean gui)
	{
		this.title = t;
		this.summary = s;
		this.detail = d;
		this.gui = gui;
		
		// parse summary strings from DW server
		
		/*
		StringBuffer myStringBuffer = new StringBuffer();
		Matcher m = Pattern.compile("FAIL \\d+").matcher(summary);
		while (m.find()) 
		{
		    m.appendReplacement(myStringBuffer, MainWin.errorHelpCache.getErrMessage( Integer.parseInt(summary.substring(m.start()+5, m.end()))) + ": ");
		}
		m.appendTail(myStringBuffer);
		
		this.summary = myStringBuffer.toString();
		*/
	}
	
	
	public int getErrno()
	{
		return errno;
	}
	

	public String getTitle()
	{
		return title;
	}


	public String getSummary()
	{
		return summary;
	}

	
	public String getDetail()
	{
		return detail;
	}

	public boolean isGui()
	{
		return gui;
	}


	public String getTextError()
	{

		return this.summary;
	}
	
	
}
