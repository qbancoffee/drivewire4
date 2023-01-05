package com.groupunix.drivewireui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class ErrorHelpCache
{
	private Vector<String> errhelp = new Vector<String>();
	
	public ErrorHelpCache()
	{
		errhelp.setSize(256);
	}

	public String getErrMessage(int err)
	{
		if ((err < errhelp.size()) && (errhelp.get(err) != null))
		{
			return errhelp.get(err);
		}
		
		return "Error " + err;
	}
	
	public void setErrMessage(int err, String msg)
	{
		if ((err > -1) && (err < errhelp.size()))
			errhelp.set(err, msg);
	}

	
	public void load() throws DWUIOperationFailedException 
	{
		
		MainWin.debug("Loading error help for instance # " + MainWin.getInstance());
		
		List<String> res = new ArrayList<String>();
		
		try 
		{
			res = UIUtils.loadList(MainWin.getInstance(), "ui server show errors");
		} 
		catch (IOException e1) 
		{
			if (MainWin.debugging)
				e1.printStackTrace();
			
		}
		for (String r : res)
		{
			
			String[] parts = r.trim().split("\\|");
			
			if (parts.length == 2)
			{
				try
				{
					setErrMessage(Integer.parseInt(parts[0]) ,parts[1]);
				}
				catch (NumberFormatException e)
				{
					
				}
			}
		}
		
	}

	
	public String dump()
	{
		String res = "errorHelpCache contents:\n\n";
		for (int i = 0;i < this.errhelp.size();i++)
		{
			if (this.errhelp.get(i) != null)
			{
				res += i + ":" + this.errhelp.get(i) + "\n";
			}
		}
		
		return(res);
	}
	
	
	
}
