package com.groupunix.drivewireui.plugins;

import java.io.IOException;

import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.FileSystemManager;
import org.apache.commons.vfs.FileType;
import org.apache.commons.vfs.VFS;

public class DWBrowserUtils
{

	public static void GenerateHTMLDir(String path)
	{
		String res = "<HTML><HEAD><TITLE>Disks at " + path + "</TITLE></HEAD>";
		String diskhtml = "";
		String dirhtml = "<table>";
		
		res += "<BODY style='font-family:arial;'>";
		
		FileSystemManager fsManager;
		try
		{
			fsManager = VFS.getManager();
			FileObject dir = fsManager.resolveFile(path);
			
			
			FileObject[] children = dir.getChildren();
			
			for ( int i = 0; i < children.length; i++ )
			{
				if (children[i].getType() == FileType.FOLDER)
				{
					dirhtml += "<tr><td><a href='" + children[i].getURL() + "'><img src='file:///" + System.getProperty("user.dir") + "/html/images/folder.png'></a></td>";
					
					dirhtml += "<td><a href='" + children[i].getURL() + "'>" + children[i].getName().getBaseName() + "</a></td></tr>";
					
				}
				else
				{
					diskhtml += children[i].getName().toString() + "<br>";
				}
			}
			
			res += dirhtml + "</table>";
			res += "<hr>";
			res += diskhtml;
	
			res += "</BODY></HTML>";
			
			
			FileObject out = fsManager.resolveFile(path + "/dw_disks.html");
			
			if ((out.exists() && out.delete()) || !out.exists() )
			{
				out.createFile();
				out.getContent().getOutputStream().write(res.getBytes());
				out.close();
				
			}
			
		} 
		catch (FileSystemException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}

}
