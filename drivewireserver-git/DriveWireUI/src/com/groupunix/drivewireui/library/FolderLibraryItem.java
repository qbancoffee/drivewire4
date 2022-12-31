package com.groupunix.drivewireui.library;

import java.util.Vector;

import org.apache.commons.configuration.HierarchicalConfiguration.Node;
import org.eclipse.swt.graphics.Image;

import com.groupunix.drivewireui.DWLibrary;
import com.groupunix.drivewireui.MainWin;
import com.groupunix.drivewireui.UIUtils;

public class FolderLibraryItem extends LibraryItem
{
	
	
	private Node node = null;
	private Vector<MenuItemDef> menuItems;
	
	
	public FolderLibraryItem(String title, Node item)
	{
		super(title);
		this.type = DWLibrary.TYPE_FOLDER;
		
		if (item != null)
			this.node = item;
	
		createMenu();
	}
	
	
	

	private void createMenu()
	{
		// new path
		// new url
		// new folder
		// delete folder
		
		
		
	}




	public Node getNode()
	{
		return this.node;
	}
	
	
	public Image getIcon()
	{
		return org.eclipse.wb.swt.SWTResourceManager.getImage(MainWin.class, "/menu/database-link.png");
	}
	
	
	public Vector<MenuItemDef> getPopupMenuItems()
	{
		return this.menuItems;
	}
	
	@SuppressWarnings("unchecked")
	public Vector<LibraryItem> getChildren()
	{
		Vector<LibraryItem> c = new Vector<LibraryItem>();
		
		for(int i = 0;i<this.node.getChildrenCount();i++)
		{
			Node item = (Node) this.node.getChild(i);
			
			if (item.getName().equals("Folder"))
			{
				@SuppressWarnings("unused")
				FolderLibraryItem folditem;
			
				if (UIUtils.getAttributeVal(item.getAttributes(),"title") == null)
					c.add(new FolderLibraryItem("Untitled Folder", item));
				else
					c.add(new FolderLibraryItem((String) UIUtils.getAttributeVal(item.getAttributes(),"title"), item));
				
			}
			else if (item.getName().equals("URL"))
			{
				if (UIUtils.getAttributeVal(item.getAttributes(),"title") == null)
					c.add(new URLLibraryItem("Untitled URL", (String) item.getValue(), item));
				else
					c.add(new URLLibraryItem((String) UIUtils.getAttributeVal(item.getAttributes(),"title"), (String) item.getValue(), item));

			}
			else if (item.getName().equals("Path"))
			{
				if (UIUtils.getAttributeVal(item.getAttributes(),"title") == null)
					c.add(new PathLibraryItem("Untitled Path", (String) item.getValue(), item ));
				else
					c.add(new PathLibraryItem((String) UIUtils.getAttributeVal(item.getAttributes(),"title"), (String) item.getValue(), item));

			}
			else if (item.getName().equals("Device"))
			{
				if (UIUtils.getAttributeVal(item.getAttributes(),"title") == null)
					c.add(new DeviceLibraryItem("Untitled Device", (String) item.getValue(), item ));
				else
					c.add(new DeviceLibraryItem((String) UIUtils.getAttributeVal(item.getAttributes(),"title"), (String) item.getValue(), item));

			}
			
		}
		
		return c;
	}




	public Image getBigIcon()
	{
		if (this.node.getParent().getParent().getName().equals("configuration"))
			return org.eclipse.wb.swt.SWTResourceManager.getImage(MainWin.class, "/menu/go-home-4.png");
				
		return org.eclipse.wb.swt.SWTResourceManager.getImage(MainWin.class, "/menu/database-big.png");
	}




	public String getNotes()
	{
		if (this.node.getAttributeCount("notes") > 0)
		{
			for (int i = 0;i<this.node.getAttributeCount();i++)
			{
				if (this.node.getAttribute(i).getName().equals("notes"))
					return (String) (this.node.getAttribute(i).getValue());
			}
		}
		
		return "";
	}




	public void setNotes(String text)
	{
		if (this.node.getAttributeCount("notes") > 0)
		{
			for (int i = 0;i<this.node.getAttributeCount();i++)
			{
				if (this.node.getAttribute(i).getName().equals("notes"))
					this.node.getAttribute(i).setValue(text);
			}
		}
		else
		{
			Node n = new Node();
			n.setName("notes");
			n.setValue(text);
			n.setAttribute(true);
			node.addAttribute(n);
		}
	}
	
}
