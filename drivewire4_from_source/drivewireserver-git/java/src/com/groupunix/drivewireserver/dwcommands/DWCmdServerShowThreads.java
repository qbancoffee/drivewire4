package com.groupunix.drivewireserver.dwcommands;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

import com.groupunix.drivewireserver.dwprotocolhandler.DWUtils;

public class DWCmdServerShowThreads extends DWCommand {

	DWCmdServerShowThreads(DWCommand parent)
	{
		setParentCmd(parent);
	}
	
	public String getCommand() 
	{
		return "threads";
	}


	public String getShortHelp() 
	{
		return "Show server threads";
	}


	public String getUsage() 
	{
		return "dw server show threads";
	}

	public DWCommandResponse parse(String cmdline) 
	{
		String text = new String();
		
		text += "\r\nDriveWire Server Threads:\r\n\n";

		Thread[] threads = getAllThreads();
		
		for (int i = 0;i<threads.length;i++)
		{
			if (threads[i] != null)
			{
				text += String.format("%40s %3d %-8s %-14s",shortenname(threads[i].getName()),threads[i].getPriority(),threads[i].getThreadGroup().getName(), threads[i].getState().toString()) + "\r\n";

			}
		}
		
		return(new DWCommandResponse(text));
	}

	
	
	private Object shortenname(String name)
	{
		String res = name;
		
		
		
		return res;
	}

	private Thread[] getAllThreads( ) {
	    final ThreadGroup root = DWUtils.getRootThreadGroup( );
	    final ThreadMXBean thbean = ManagementFactory.getThreadMXBean( );
	    int nAlloc = thbean.getThreadCount( );
	    int n = 0;
	    Thread[] threads;
	    do {
	        nAlloc *= 2;
	        threads = new Thread[ nAlloc ];
	        n = root.enumerate( threads, true );
	    } while ( n == nAlloc );
            Thread[] copy = new Thread[threads.length];
            System.arraycopy(threads, 0, copy, 0, threads.length);
	    return copy;
	}
	
	public boolean validate(String cmdline) 
	{
		return(true);
	}
	
}
