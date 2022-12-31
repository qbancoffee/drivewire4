package com.groupunix.drivewireui.library;

import org.eclipse.swt.events.SelectionAdapter;

public class MenuItemDef
{
	
	private String text;
	private String imagePath;
	private SelectionAdapter selectionAdapter;
	private boolean enabled;
	
	public MenuItemDef(String t, String i, SelectionAdapter s, boolean e)
	{
		this.setText(t);
		this.setImagePath(i);
		this.setSelectionAdapter(s);
		this.setEnabled(e);
	}

	public void setSelectionAdapter(SelectionAdapter selectionAdapter)
	{
		this.selectionAdapter = selectionAdapter;
	}

	public SelectionAdapter getSelectionAdapter()
	{
		return selectionAdapter;
	}



	public void setText(String text)
	{
		this.text = text;
	}

	public String getText()
	{
		return text;
	}

	public void setEnabled(boolean enabled)
	{
		this.enabled = enabled;
	}

	public boolean isEnabled()
	{
		return enabled;
	}

	public void setImagePath(String imagePath)
	{
		this.imagePath = imagePath;
	}

	public String getImagePath()
	{
		return imagePath;
	}

}
