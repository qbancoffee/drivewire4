package com.groupunix.drivewireui.plugins;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import swing2swt.layout.BorderLayout;

import com.groupunix.drivewireserver.dwdisk.filesystem.DWFileSystemDirEntry;
import com.groupunix.drivewireui.MainWin;

public class ASCIIViewer extends FileViewer
{

	private static final String TYPENAME = "ASCII Text";
	private static final String TYPEIMAGE = "/filetypes/txt.png";
	private Text viewAsciiText;
	
	public ASCIIViewer(Composite parent, int style)
	{
		super(parent, style);
		
		setLayout(new BorderLayout(0, 0));
		
		viewAsciiText = new Text(this, SWT.BORDER | SWT.READ_ONLY | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL | SWT.MULTI);
		viewAsciiText.setBackground(org.eclipse.wb.swt.SWTResourceManager.getColor(SWT.COLOR_WHITE));
		viewAsciiText.setFont(MainWin.logFont);
		
	}


	@Override
	public void viewFile(DWFileSystemDirEntry direntry, byte[] content)
	{
		this.viewAsciiText.setText(this.filterAscii(content));
	}

	@Override
	public String getTypeName()
	{
		return TYPENAME;
	}

	@Override
	public String getTypeIcon()
	{
		return TYPEIMAGE;
	}
	
	protected String filterAscii(byte[] fc)
	{
		
		String res = "";
		
		for (int i = 0;i<fc.length;i++)
		{
			if ((fc[i] < 128) && (fc[i] > 0))
			{
				if (fc[i] == 13)
				{
					res += Text.DELIMITER;
				}
				else
				{
					res += (char)fc[i];
				}
			}
		}
		
		
		return res;
	}
	
	public int getViewable(DWFileSystemDirEntry direntry, byte[] fc)
	{
		if (direntry.isAscii())
			return 2;
		else 
			return 1;
	}
	
	
	
}
