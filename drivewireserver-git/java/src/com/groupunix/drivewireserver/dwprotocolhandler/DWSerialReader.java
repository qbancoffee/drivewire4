package com.groupunix.drivewireserver.dwprotocolhandler;

import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ArrayBlockingQueue;

public class DWSerialReader implements SerialPortEventListener
{
	private ArrayBlockingQueue<Byte> queue;
	private InputStream in;
	private boolean wanttodie = false;
	
	public DWSerialReader(InputStream in, ArrayBlockingQueue<Byte> q)
	{
		this.queue = q;
		this.in = in;
	}

	
	public void serialEvent(SerialPortEvent arg0)
	{
		 int data;
         
         try
         {
             while (!wanttodie && ( data = in.read()) > -1 )
             {
                 queue.add((byte) data);
             }
             
         }
         catch ( IOException e )
         {
             e.printStackTrace(); 
         }     
	}

	public void shutdown()
	{
		this.wanttodie = true;
		
	}

	

}
