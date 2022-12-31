package com.groupunix.drivewireserver.dwprotocolhandler;

import com.groupunix.drivewireserver.DWDefs;

public class DWProtocolTimers
{
	
	private long[] timers;
	
	public DWProtocolTimers()
	{
		this.timers = new long[256];
		
	}
	
	public void resetTimer(byte tno)
	{
		resetTimer(tno, System.currentTimeMillis());
	}
	
	public void resetTimer(byte tno, long tme)
	{
		this.timers[(tno & 0xff)] = tme;
		
		// Dependencies
		
		switch (tno)
		{
			// init and reset are also np ops
			case DWDefs.TIMER_DWINIT:
			case DWDefs.TIMER_RESET:
				resetTimer(DWDefs.TIMER_NP_OP, tme);
				break;
		
			// read/write is an io op
			case DWDefs.TIMER_READ:
			case DWDefs.TIMER_WRITE:
				resetTimer(DWDefs.TIMER_IO, tme);
				break;
			
			// io ops are also np ops
			case DWDefs.TIMER_IO:
				resetTimer(DWDefs.TIMER_NP_OP, tme);
				break;
				
				// poll is an op
			case DWDefs.TIMER_POLL:
				resetTimer(DWDefs.TIMER_OP, tme);
				break;
				
				
			// np ops are also ops
			case DWDefs.TIMER_NP_OP:
				resetTimer(DWDefs.TIMER_OP, tme);
				break;
			
		
				
		}
		
	}
	
	
	public long getTimer(byte tno)
	{
		if (this.timers[(tno & 0xff)] > 0)
			return  (System.currentTimeMillis() - this.timers[(tno & 0xff)]) & 0xFFFFFFFF; 
		
		return 0;
	}

	public byte[] getTimerBytes(byte tno)
	{
		byte[] res = new byte[4];
		
		long input = getTimer(tno);
		
		res[0] = (byte) ((input >> 24) & 0xFF);  
		res[1] = (byte) ((input >> 16) & 0xFF);
		res[2] = (byte) ((input >> 8) & 0xFF); 
		res[3] = (byte) (input & 0xFF);
		
		return res;
		
	}
	
	
	
}
