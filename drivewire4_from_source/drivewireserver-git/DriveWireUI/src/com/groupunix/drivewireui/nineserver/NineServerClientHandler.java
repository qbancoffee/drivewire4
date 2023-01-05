package com.groupunix.drivewireui.nineserver;

import java.io.IOException;
import java.net.Socket;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabItem;

import com.groupunix.drivewireui.MainWin;

public class NineServerClientHandler implements Runnable
{

	private NineScreen ninescreen;
	private Socket skt;

	public NineServerClientHandler(Socket skt)
	{
		this.skt = skt;
	}

	@Override
	public void run()
	{
		// allocate display
		MainWin.getDisplay().syncExec(new Runnable(){

			@Override
			public void run()
			{
				if (MainWin.config.getBoolean("NineServerWinDetached", true))
				{
					ninescreen = new NineScreen(MainWin.getShell(), SWT.NONE, null , skt);
				}
				else
				{
					
					CTabItem cti = MainWin.getNineScreenTab();
					ninescreen = new NineScreen(MainWin.tabFolderOutput, SWT.NONE, cti, skt);
				
					cti.setControl(ninescreen);
				}
				
				
			}});
		
		 
		int data = 0;
		
		byte[] buf = new byte[128];
		
		while ((skt.isClosed() == false) && (data > -1) && (!this.ninescreen.isDisposed()))
		{
			try 
			{
				// prob should read > 1 byte at a time, but..
				data = skt.getInputStream().read(buf);
				
				for (int i = 0;i<data;i++)
					this.ninescreen.takeInput(buf[i]);
				
			} 
			catch (IOException e) 
			{
				if ((skt != null) && (!skt.isClosed()))
				{
					try
					{
						skt.close();
					} catch (IOException e1)
					{
					}
				}
						
			}
			
		}
		
		
	}

}
