package com.groupunix.drivewireui.plugins;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableCursor;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import swing2swt.layout.BorderLayout;

import com.groupunix.drivewireserver.dwdisk.filesystem.DWFileSystemDirEntry;
import com.groupunix.drivewireui.GradientHelper;
import com.groupunix.drivewireui.MainWin;




public class HexViewer extends FileViewer
{

	private static final String TYPENAME = "Hex Viewer";
	private static final String TYPEIMAGE = "/menu/hex.png";
	
	private Table tableHex;
	
	private Label lblAddress;
	private Label lblValue;
	
	private Color[] section_hilights;
	private Color[] sector_hilights;

	private int cur_section_hl = 0;

	private Color colorPostamble;
	private Color colorPreamble;
		
	public HexViewer(Composite parent, int style)
	{
		super(parent, style);
		
		setLayout(new BorderLayout(0, 0));
		
		colorPreamble = new Color(parent.getDisplay(), new RGB(127,255,127));
		colorPostamble = new Color(parent.getDisplay(), new RGB(255,127,127));
		
		section_hilights = new Color[9];
		section_hilights[0] = new Color(parent.getDisplay(), new RGB(200,200,200));
		section_hilights[1] = new Color(parent.getDisplay(), new RGB(220,220,220));
		section_hilights[2] = new Color(parent.getDisplay(), new RGB(240,240,200));
		section_hilights[3] = new Color(parent.getDisplay(), new RGB(200,220,200));
		section_hilights[4] = new Color(parent.getDisplay(), new RGB(220,240,220));
		section_hilights[5] = new Color(parent.getDisplay(), new RGB(220,200,200));
		section_hilights[6] = new Color(parent.getDisplay(), new RGB(240,220,220));
		section_hilights[7] = new Color(parent.getDisplay(), new RGB(200,200,220));
		section_hilights[8] = new Color(parent.getDisplay(), new RGB(220,220,240));
		
		
		sector_hilights = new Color[2];
		sector_hilights[0] = new Color(parent.getDisplay(), new RGB(255,255,255));
		sector_hilights[1] = new Color(parent.getDisplay(), new RGB(235,235,235));
		
		createContents();
	}

	
	private void createContents()
	{
		tableHex = new Table(this, SWT.NONE);
		tableHex.setLinesVisible(true);
	
		tableHex.setHeaderVisible(true);
		tableHex.setFont(MainWin.logFont);
		
		
		TableColumn tblclmnOffset = new TableColumn(tableHex, SWT.NONE);
		tblclmnOffset.setWidth(40);
		tblclmnOffset.setText("Addr");
		tblclmnOffset.setAlignment(SWT.CENTER);
		

		for (int i = 0;i<16;i++)
		{
			TableColumn tableColumn = new TableColumn(tableHex, SWT.NONE);
			tableColumn.setWidth(30);
			tableColumn.setText(intToHexStr(i,2));
		}
		
		TableColumn tblclmnabcdef = new TableColumn(tableHex, SWT.NONE);
		tblclmnabcdef.setWidth(130);
		tblclmnabcdef.setText("0123456789abcdef");
		
		final TableCursor tableCursor = new TableCursor(tableHex, SWT.NONE);
		tableCursor.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
		tableCursor.setFont(MainWin.logFont);
		tableCursor.addSelectionListener(new SelectionListener() 
		{

			@Override
			public void widgetSelected(SelectionEvent e)
			{
				if ((tableCursor.getColumn() > 0) && (tableCursor.getColumn() < 17))
				{
					// databyte
					tableHex.setSelection(tableCursor.getRow());
					
					int off = tableHex.getSelectionIndex() * 16;
					int addr = off + tableCursor.getColumn() -1;
					
					
					try
					{
						int val = Integer.parseInt(tableCursor.getRow().getText(tableCursor.getColumn()), 16);
						
						String asc = "";
						if ((val >= 32) && (val<127))
						{
							asc = "'" + (char)val + "'";
						}
						
						lblValue.setText(String.format("val:   %02x h   %02d d   " + asc, val, val) );
					}
					catch (NumberFormatException e1)
					{
						lblValue.setText("val: " + e1.getMessage());
					}
					
					lblAddress.setText(String.format("addr:   %x h   %d d", addr, addr));
					
					
						
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e)
			{
				
			} } );
		
		final Composite composite_3 = new Composite(this, SWT.NO_FOCUS);
		composite_3.setLayoutData(BorderLayout.SOUTH);
		
		GradientHelper.applyVerticalGradientBG(composite_3, MainWin.getDisplay().getSystemColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT),MainWin.getDisplay().getSystemColor(SWT.COLOR_TITLE_BACKGROUND));
		
		composite_3.addListener(SWT.Resize, new Listener() {

			@Override
			public void handleEvent(Event event)
			{
				GradientHelper.applyVerticalGradientBG(composite_3, MainWin.getDisplay().getSystemColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT),MainWin.getDisplay().getSystemColor(SWT.COLOR_TITLE_BACKGROUND));
				
				
			} } );
		
		composite_3.setBackgroundMode(SWT.INHERIT_FORCE);
		
		lblAddress = new Label(composite_3, SWT.NONE);
		lblAddress.setBounds(10, 3, 230, 16);
		
		lblValue = new Label(composite_3, SWT.NONE);
		lblValue.setBounds(250, 3, 250, 16);
		
		FillLayout fl_compositeMemMap = new FillLayout();
		fl_compositeMemMap.type = SWT.VERTICAL;
		
	}
	
	

	@SuppressWarnings("unused")
	@Override
	public void viewFile(DWFileSystemDirEntry direntry, byte[] bytes)
	{
		int x = 1;
		int y = 0;
		int off = 0;
		
		String txt = "";
		
		tableHex.setRedraw(false);
		
		lblAddress.setText("");
		lblValue.setText("");
		tableHex.removeAll();
		
		TableItem ti = null;
		
		// put bytes in table
		
		for (int i = 0;i<bytes.length ;i++)
		{
			if (x == 1)
			{
				ti = new TableItem(tableHex, SWT.NONE);
				ti.setText(0, intToHexStr(off,4));
				ti.setBackground(0, this.sector_hilights[ (off/256) % 2 ]);
				off += 16;
				txt = "";
			}
			
			ti.setText(x, byteToHexStr(bytes[i]));
			txt += cleanAsciiStr((char)bytes[i] +"");
			
			x++;
			
			if (x == 17)
			{
				ti.setText(17, txt);
				x = 1;
				y++;
			}
			
		}
		
		
		if (ti != null)
		{
			ti.setText(17, txt);
		}
		
		
		// look for valid pre/postamble chain
		
		int blocksize = 0;
		int loadaddr = 0;
		int flag = 0;
		int pos = 0;
		
		
		while ((pos < bytes.length - 4) && (bytes[pos] == 0))
		{
			blocksize = ((bytes[pos+1] & 0xFF) << 8) + (bytes[pos+2] & 0xFF);
			loadaddr =  ((bytes[pos+3] & 0xFF) << 8) + (bytes[pos+4] & 0xFF);
			
			
			Color hc = getNextSectionColor();
			
			try
			{
				
				for (int i = pos; i < pos + 5;i++)
				{
					if (i < bytes.length)
						setBGColFor(i, this.colorPreamble);
				}
			
				int i = pos + 5;
				
				File f = new File("piece_" + loadaddr + ".bin");
				
				try
				{
					OutputStream of = new FileOutputStream(f);
					
					
					
					while ((i < pos + blocksize + 5) && (i < bytes.length))
					{
						of.write(bytes[i]);
						setBGColFor(i, hc);
						i++;
					}
					
					
					of.close();
					
				} catch (FileNotFoundException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
			}
			catch (IllegalArgumentException e)
			{
				
			}
			
			pos += blocksize + 5;
		}
		
		if ((pos > 0) && (pos < bytes.length) && ((bytes[pos] & 0xFF) == 0xFF))
		{
			for (int i = pos; i < pos + 5;i++)
			{
				if (i < bytes.length)
					setBGColFor(i, this.colorPostamble);
			}
			
		}
		
		
		
		
		tableHex.setRedraw(true);
		
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
	
	private String cleanAsciiStr(String txt)
	{
		
		txt = txt.replaceAll("[^\\x20-\\x7E]", ".");
	
		return(txt);
	}



	private String byteToHexStr(byte b)
	{
		return String.format("%02x", b);
	}



	private String intToHexStr(int i, int j)
	{
		return String.format("%0" + j + "x", i);
	}

	
	
	
	

	private Color getNextSectionColor()
	{
		this.cur_section_hl++;
		if (this.cur_section_hl >= this.section_hilights.length)
			this.cur_section_hl = 0;
		
		return(this.section_hilights[this.cur_section_hl]);
	}



	private void setBGColFor(int pos, Color col)
	{
		tableHex.getItem( pos / 16 ).  setBackground(1 + (pos % 16), col);
	}
	
	
	
	
	@Override
	public int getViewable(DWFileSystemDirEntry direntry, byte[] content)
	{
		if (!direntry.isAscii())
			return 2;
		return 1;
	}
	
	
	
	
	

	
}

