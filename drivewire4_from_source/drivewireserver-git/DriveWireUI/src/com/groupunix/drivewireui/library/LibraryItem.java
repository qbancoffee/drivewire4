package com.groupunix.drivewireui.library;

import java.util.Vector;

import org.apache.commons.configuration.HierarchicalConfiguration.Node;
import org.eclipse.swt.graphics.Image;

import com.groupunix.drivewireui.DWLibrary;
import com.groupunix.drivewireui.MainWin;

public class LibraryItem
{
	private String title;
	protected Vector<LibraryItem> children;
	protected int type = DWLibrary.TYPE_UNKNOWN;
	
	public LibraryItem(String title)
	{
		this.title = title;
		this.children = new Vector<LibraryItem>();
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public String getTitle()
	{
		return title;
	}

	
	public Vector<LibraryItem> getChildren()
	{
		return children;
	}

	public void setType(int type)
	{
		this.type = type;
	}

	public int getType()
	{
		return type;
	}

	public Image getIcon()
	{
		return org.eclipse.wb.swt.SWTResourceManager.getImage(MainWin.class, "/menu/inactive.png");
	}

	public Vector<MenuItemDef> getPopupMenuItems()
	{
		return null;
	}

	public Node getNode()
	{
		
		return null;
	}

	public String getHoverText()
	{
		return this.title;
	}
	

	
	
}
