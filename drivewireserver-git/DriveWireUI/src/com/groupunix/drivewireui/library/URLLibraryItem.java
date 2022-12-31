package com.groupunix.drivewireui.library;

import org.apache.commons.configuration.HierarchicalConfiguration.Node;
import org.eclipse.swt.graphics.Image;

import com.groupunix.drivewireui.DWLibrary;
import com.groupunix.drivewireui.MainWin;

public class URLLibraryItem extends LibraryItem
{
	private String url;
	private String iconpath;
	private Node node = null;
	
	
	public URLLibraryItem(String title, String url, Node item)
	{
		super(title);
		
		this.setUrl(url);
		
		this.type = DWLibrary.TYPE_URL;
		this.iconpath = "/menu/world-link.png";
		this.node = item;
		
	}

	public void setUrl(String url)
	{
		this.url = url;
	}

	public String getUrl()
	{
		return url;
	}
	
	public Image getIcon()
	{
		return org.eclipse.wb.swt.SWTResourceManager.getImage(MainWin.class, this.iconpath);
	}
	
	public Node getNode()
	{
		return this.node;
	}
	
	public String getHoverText()
	{
		return this.url;
	}
	
}
