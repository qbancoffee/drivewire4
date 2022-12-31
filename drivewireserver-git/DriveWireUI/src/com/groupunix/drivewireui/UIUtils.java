package com.groupunix.drivewireui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.zip.GZIPInputStream;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.HierarchicalConfiguration.Node;
import org.apache.commons.configuration.XMLConfiguration;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import com.groupunix.drivewireserver.DWDefs;
import com.groupunix.drivewireui.exceptions.DWUIOperationFailedException;

public class UIUtils {


	
	public static List<String> loadList(String arg) throws IOException, DWUIOperationFailedException
	{
		return(loadList(-1,arg));
	}
	
	
	public static List<String> loadList(int instance, String arg) throws IOException, DWUIOperationFailedException
	{
		//TODO - ignoring instance?
		Connection conn = new Connection(MainWin.getHost(), MainWin.getPort(), MainWin.getInstance());
		
		List<String> res = new ArrayList<String>();
		
		conn.Connect();
		
		res = conn.loadList(instance, arg);
			
		conn.close();
		
		return(res);
		
	}
	
	
	public static String getStackTrace(Throwable aThrowable) {
	    final Writer result = new StringWriter();
	    final PrintWriter printWriter = new PrintWriter(result);
	    aThrowable.printStackTrace(printWriter);
	    return result.toString();
	  }


	public static HashMap<String, String> getServerSettings(ArrayList<String> settings) throws DWUIOperationFailedException, IOException 
	{
		// create hashmap containing the requested settings
		
		HashMap<String,String> values = new HashMap<String,String>();
			
		Connection conn = new Connection(MainWin.getHost(), MainWin.getPort(), MainWin.getInstance());
		
		conn.Connect();
		
		
		
		
		for (int i = 0;i<settings.size();i++)
		{
			List<String> res = conn.loadList(-1, "ui server config show " + settings.get(i));
			values.put(settings.get(i), res.get(0));
		}
		
		conn.close();
		
		
		return(values);
	}



	public static boolean sTob(String val) 
	{
		if ((val != null) && (val.equalsIgnoreCase("true")))
			return true;
		
		return false;
	}


	public static void setServerSettings(HashMap<String, String> values) throws IOException, DWUIOperationFailedException 
	{
		if (values.size() > 0)
		{
			
			Connection conn = new Connection(MainWin.getHost(), MainWin.getPort(), MainWin.getInstance());
		
			conn.Connect();

			Collection<String> c = values.keySet();
			Iterator<String> itr = c.iterator();
		
			while(itr.hasNext())
			{
				String val = itr.next();
				conn.sendCommand("ui server config set " + val + " " + values.get(val),0);
			}
		
			conn.close();
		}
	}


	public static String bTos(boolean selection) 
	{
		if (selection)
			return("true");
		
		return "false";
	}

	public static boolean validateNum(String data) 
	{
		try 
		{
			Integer.parseInt(data);

		}
		catch (NumberFormatException e)
		{
			return false;
		}
		
		return true;
	}

	

	public static boolean validateNum(String data, int min) 
	{
		try 
		{
			int val = Integer.parseInt(data);
			if (val < min)
				return false;
		}
		catch (NumberFormatException e)
		{
			return false;
		}
		
		return true;
	}

	public static boolean validateNum(String data, int min, int max) 
	{
		try 
		{
			int val = Integer.parseInt(data);
			if ((val < min) || (val > max))
				return false;
			
		}
		catch (NumberFormatException e)
		{
			return false;
		}
		
		return true;
	}


	public static HashMap<String, String> getInstanceSettings(int instance, ArrayList<String> settings) throws DWUIOperationFailedException, IOException 
	{
		// create hashmap containing the requested settings
		
		HashMap<String,String> values = new HashMap<String,String>();
			
		Connection conn = new Connection(MainWin.getHost(), MainWin.getPort(), instance);
		
		conn.Connect();
		
		
		for (int i = 0;i<settings.size();i++)
		{
			List<String> res = conn.loadList(instance, "ui instance config show " + settings.get(i));
			values.put(settings.get(i), res.get(0));
		
		}
		
		conn.close();
		
		
		return(values);
	}


	public static boolean validTCPPort(int port) 
	{
		if ((port > 0) && (port < 65536))
			return true;
		
		return false;
	}


	public static void setInstanceSettings(int instance, HashMap<String, String> values) throws UnknownHostException, IOException, DWUIOperationFailedException
	{
		if (values.size() > 0)
		{
				
			Connection conn = new Connection(MainWin.getHost(), MainWin.getPort(), MainWin.getInstance());
		
			conn.Connect();
			
			Collection<String> c = values.keySet();
			Iterator<String> itr = c.iterator();
		
			while(itr.hasNext())
			{
				String val = itr.next();
				conn.sendCommand("dw config set " + val + " " + values.get(val),instance);
			}
		
			conn.close();
		}
		
	}

	
	
	

	public static HierarchicalConfiguration getServerConfig() throws UnknownHostException, IOException, ConfigurationException, DWUIOperationFailedException 
	{
		Connection conn = new Connection(MainWin.getHost(), MainWin.getPort(), MainWin.getInstance());
		
		conn.Connect();
		
		StringReader sr = conn.loadReader(-1,"ui server config write");
		conn.close();

		XMLConfiguration res = new XMLConfiguration();
		res.load(sr);
		return (HierarchicalConfiguration) res.clone();
	}
	
	

	

	public static DWServerFile[] getFileArray(String uicmd) throws IOException, DWUIOperationFailedException 
	{

		DWServerFile[] res = null;
			
		List<String> roots = UIUtils.loadList(uicmd);
				
		res = new DWServerFile[roots.size()];
				
		for (int i = 0;i<roots.size();i++)
		{
			res[i] = new DWServerFile(".");
			res[i].setVals(roots.get(i));
					
		}
			
		return(res);
		
	}

	
	public static void fileUnGzip(String src, String dst) throws IOException
	{
		File destFile = new File(dst);
		File sourceFile = new File(src);
		
		 OutputStream out = new FileOutputStream(destFile);
		 InputStream in = new FileInputStream(sourceFile);
		 
		 try 
		 {
		      in = new GZIPInputStream(in);
		      
		      byte[] buffer = new byte[65536];
		      int noRead;
		      while ((noRead = in.read(buffer)) != -1) 
		      {
		        out.write(buffer, 0, noRead);
		      }
		 } 
		 finally 
		 {
		      try { out.close(); in.close(); } catch (Exception e) {}
		    
		 }
	}
	
	
	public static void fileCopy(String src, String dst) throws IOException
	{
		File destFile = new File(dst);
		File sourceFile = new File(src);
		
	    if(!destFile.exists()) {
	        destFile.createNewFile();
	    }

	    FileChannel source = null;
	    FileChannel destination = null;
	    FileInputStream sis = null;
	    FileOutputStream dos = null;
	    
	    try {
	    	sis = new FileInputStream(sourceFile);
	    	dos = new FileOutputStream(destFile);
	    	
	        source = sis.getChannel();
	        destination = dos.getChannel();

	        long count = 0;
	        long size = source.size();              
	        while((count += destination.transferFrom(source, count, size-count))<size);
	    }
	    finally {
	    	if (sis != null)
	    	{
	    		sis.close();
	    		
	    	}
	    	if (dos != null)
	    	{
	    		dos.close();
	    	}
	    	if(source != null) {
	            source.close();
	        }
	        if(destination != null) {
	            destination.close();
	        }
	        
	       
	    }
		
	}


	
	


	public static String getFilenameFromURI(String string)
	{
		String res = string;
		
		if ((res.indexOf('/') > -1) && (res.indexOf('/') < (res.length()-1)))
					res = res.substring(res.lastIndexOf('/')+1);
			
		return(res);
	}
	
	public static String shortenLocalURI(String df) 
	{
		if ((df != null ) && df.startsWith("file:///"))
		{
			if (df.charAt(9) == ':')
			{
				return df.substring(8);
			}
			else
			{
				return df.substring(7);
			}
		}
		return(df);
	}
	
	public static String getLocationFromURI(String string)
	{
		String res = string;
		
		if ((res.indexOf('/') > -1) && (res.indexOf('/') < (res.length()-1)))
					res = res.substring(0,res.lastIndexOf('/'));
		
		
		return(shortenLocalURI(res));
	}
	
	
	public static void loadFonts()
	{
		
		MainWin.debug("load fonts");
		
		class OnlyExt implements FilenameFilter 
		{ 
			String ext; 
			public OnlyExt(String ext) 
			{ 
				this.ext = "." + ext; 
			} 
			
				
			public boolean accept(File dir, String name) 
			{ 
				return name.endsWith(ext); 
			}	 
		}
		
		
		File fontdir = new File("fonts");
		
		if (fontdir.exists() && fontdir.isDirectory())
		{
			String[] files = fontdir.list(new OnlyExt("ttf"));
			
			for(int i = 0;i<files.length;i++)
			{
				if (!MainWin.getDisplay().loadFont("fonts/" + files[i])) 
				{
		                MainWin.debug("Failed to load font from " + files[i]);
				}
				else
				{
					
					
					MainWin.debug("Loaded font from " + files[i]);
				}
			}
			
			
			
		}
		else
		{
			MainWin.debug("No font dir");
		}
		
	}
	
	
	public static Font findSizedFont(String fname, String txt, int maxw, int maxh, int style) 
	{
        
        int size = 8;
        int width = 0;
        int height = 0;
        
        Image test = new Image(null, maxw*2,maxh*2);
        GC gc = new GC(test);
        Font font = null;
        
        while ((width < maxw) && (height < maxh))
        {
        	if (font!=null)
        		font.dispose();
        	
        	font = new Font(MainWin.getDisplay(), fname, size, style);
        
        	gc.setFont(font);
        	width = gc.textExtent(txt).x;
        	height = gc.textExtent(txt).y;
        	
        	size++;
        }
        
        gc.dispose();
        
        MainWin.debug("chose font " + fname + " @ " + size + " = " + width + " x " + height);
    	
        
        return font;
	}

	public static Font findFont(Display display, HashMap<String,Integer> candidates , String text, int maxw, int maxh)
	{
		FontData[] fd = MainWin.getDisplay().getFontList(null, true);
		
		
		for (FontData f : fd)
		{
			for (Entry<String,Integer> e : candidates.entrySet())
			{
				if (f.getName().equals(e.getKey()) && (f.getStyle() == e.getValue()))
				{
					return(findSizedFont(f.getName(), text, maxw, maxh, f.getStyle()));
				}
			}
			
		}
		
		MainWin.debug("Failed to find a font");
		return Display.getCurrent().getSystemFont();
		
	}

	
	
	public static String listFonts()
	{
		String res = "";
		
		 FontData[] fd = Display.getDefault().getFontList(null, true);
		 
		 for (FontData f : fd)
			{
				res += f.getName() + " gh:" + f.getHeight() + " st: " + f.getStyle() + " ht: " + f.height + "\n";
		
			}
		
		return res;
	}

	
	
	
	
	
	public static void simpleConfigServer(ArrayList<String> cmds) throws IOException, DWUIOperationFailedException 
	{
		// configure device
		
			
		try
		{
			for (String cmd : cmds)
			{	
				UIUtils.loadList(MainWin.getInstance(), cmd);
			}
			
			UIUtils.loadList(MainWin.getInstance(), "ui instance reset protodev");
			
		} 
		catch (IOException e)
		{
			throw  new IOException(e.getMessage());
			
		} 
		catch (DWUIOperationFailedException e)
		{
			throw new DWUIOperationFailedException(e.getMessage());
		}
		
		
		
	}


	
	
	

	public static boolean hasArg(String[] args, String arg)
	{
		for (String a:args)
		{
			if (a.endsWith("-" + arg))
				return true;
		}
		
		return false;
	}


	public static int getLogLevelVal(String level)
	{
		int res = 0;
		
		for (int i = 0; i<DWDefs.LOG_LEVELS.length;i++)
		{
			if (DWDefs.LOG_LEVELS[i].equals(level))
			{
				res = i;
			}
		}
			
		return res;
	}
	
	
	
	static boolean matchAttributeVal(List<Node> attributes, String key, String val)
	{
		for (Node n : attributes)
		{
			if (n.getName().equals(key) && n.getValue().equals(val))
				return(true);
		}
		
		return false;
	}





	public static Object getAttributeVal(List<Node> attributes, String key)
	{
		for (Node n : attributes)
		{
			if (n.getName().equals(key))
			{
				return(n.getValue());
			}
		}
		
		return null;
	}

	static List<String> getAttributeVals(List<Node> attributes, String key)
	{
		List<String> res = new ArrayList<String>();
		
		for (Node n : attributes)
		{
			if (n.getName().equals(key))
			{
				res.add(n.getValue().toString());
			}
		}
		
		return res;
	}



	static boolean hasAttribute(List<Node> attributes, String key)
	{
		for (Node n : attributes)
		{
			if (n.getName().equals(key))
				return(true);
		}
		
		return false;
	}


	public static String prettyRBFAttribs(int att)
	{
		String res = "";
		
		if ((att & 0x80) == 0x80)
			res += "D";
		else
			res += "-";
		
		if ((att & 0x40) == 0x40)
			res += "S";
		else
			res += "-";
		
		if ((att & 0x20) == 0x20)
			res += "X";
		else
			res += "-";
		
		if ((att & 0x10) == 0x10)
			res += "W";
		else
			res += "-";
		
		if ((att & 0x08) == 0x08)
			res += "R";
		else
			res += "-";
		
		if ((att & 0x04) == 0x04)
			res += "x";
		else
			res += "-";
		
		if ((att & 0x02) == 0x02)
			res += "w";
		else
			res += "-";
		
		if ((att & 0x01) == 0x01)
			res += "r";
		else
			res += "-";
		
		
		return res;
	}
	
	/*
	public static String getMD5sum(FileObject fileObject)
	{
		String res = "";
		
		try
		{
			MessageDigest digest = MessageDigest.getInstance("MD5");
			
			byte[] md5sum = digest.digest(fileObject);
			BigInteger bigInt = new BigInteger(1, md5sum);
			
			res =  bigInt.toString(16);
			
		} 
		catch (NoSuchAlgorithmException e)
		{
		}
		
		return res;
	}
	*/
	
	
	private static String convertToHex(byte[] data) { 
	        StringBuffer buf = new StringBuffer();
	        for (int i = 0; i < data.length; i++) { 
	            int halfbyte = (data[i] >>> 4) & 0x0F;
	            int two_halfs = 0;
	            do { 
	                if ((0 <= halfbyte) && (halfbyte <= 9)) 
	                    buf.append((char) ('0' + halfbyte));
	                else 
	                    buf.append((char) ('a' + (halfbyte - 10)));
	                halfbyte = data[i] & 0x0F;
	            } while(two_halfs++ < 1);
	        } 
	        return buf.toString();
	    } 
	 
	
	public static String getSHA1(String text) 
	{ 
	    MessageDigest md;
	    byte[] sha1hash = new byte[40];
	    
	    try
		{
	    	md = MessageDigest.getInstance("SHA-1");
			md.update(text.getBytes("iso-8859-1"), 0, text.length());
			sha1hash = md.digest();
		} 
	    catch (UnsupportedEncodingException e)
		{
	    	e.printStackTrace();
		}
	    catch (NoSuchAlgorithmException e)
	    {
	    	e.printStackTrace();
	    }
	    
	    return convertToHex(sha1hash);
	}


	public static String getSHA1(InputStream is)
	{
		
		byte[] sha1hash = new byte[40];
		
		
		try
		{
		MessageDigest digest = MessageDigest.getInstance("SHA-1");
		
		byte[] buffer = new byte[8192];
		int read = 0;
	
		while( (read = is.read(buffer)) > 0) {
				digest.update(buffer, 0, read);
		
		}		
			
		sha1hash = digest.digest();
			
		}
		catch (UnsupportedEncodingException e)
		{
	    	e.printStackTrace();
		}
	    catch (NoSuchAlgorithmException e)
	    {
	    	e.printStackTrace();
	    } 
	    catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	    
		return convertToHex(sha1hash);
	} 
	
	

	public String prettyBuffer(int i, int j)
	{
		String res = String.format("%03d ",j);
		
		switch(i)
		{
			case 200:
				
				switch(j)
				{
					case 1: 
						res += "Fnt_S8x8";
						break;
					case 2: 
						res += "Fnt_S6x8";
						break;
					case 3: 
						res += "Fnt_G8x8";
						break;
				}
				
				break;
			case 202:
				switch(j)
				{
					case 1: 
						res += "Ptr_Arr";
						break;
					case 2: 
						res += "Ptr_Pen";
						break;
					case 3: 
						res += "Ptr_LCH";
						break;
					case 4: 
						res += "Ptr_Slp";
						break;
					case 5: 
						res += "Ptr_Ill";
						break;
					case 6: 
						res += "Ptr_Txt";
						break;
					case 7: 
						res += "Ptr_SCH";
						break;
					
				}
				break;
			
			case 203:
			case 204:
			case 205:
				switch(j)
				{
					case 1: 
						res += "Pat_Dot";
						break;
					case 2: 
						res += "Pat_Vrt";
						break;
					case 3: 
						res += "Pat_Hrz";
						break;
					case 4: 
						res += "Pat_XHtc";
						break;
					case 5: 
						res += "Pat_LSnt";
						break;
					case 6: 
						res += "Pat_RSnt";
						break;
					case 7: 
						res += "Pat_SDot";
						break;
					case 8: 
						res += "Pat_BDot";
						break;
					
				}
			
				break;
			
		}
		
		return res;
	}



	public String prettyBufferGroup(int i)
	{
		String res = String.format("%03d ",i);
		
		switch(i)
		{
			case 200:
				res += "Grp_Fnt";
				break;
			case 201:
				res += "Grp_Clip";
				break;
			case 202:
				res += "Grp_Ptr";
				break;
			case 203:
				res += "Grp_Pat2";
				break;
			case 204:
				res += "Grp_Pat4";
				break;
			case 205:
				res += "Grp_Pat16";
				break;
			
		}
		
		return res;
	}


	public static String getSHAForDir(String path, String prefix) 
	{
		String res = "";
		
		 
		File root = new File( path );
	    File[] list = root.listFiles();

	    for ( File f : list ) 
	    {
	    
	    	if ( f.isDirectory() ) 
	    	{
	                res += getSHAForDir( f.getAbsolutePath(), prefix + "/" + f.getName());
	        }
	        else 
	    	{
	        	try 
	        	{
					res += "<file sha=\"" + getSHA1(new FileInputStream(f)) + "\" name=\"";
					res += prefix + "/" + f.getName() + "\"/>" + System.getProperty("line.separator");
				} 
	        	catch (FileNotFoundException e) 
	        	{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        	
	        	
	        	
	            
	            
	    	}
	        
	    }
		
		return res;
	}

	
	
	public static void replaceFiles(String path, String prefix) 
	{
		 
		File root = new File( path );
	    File[] list = root.listFiles();

	    for ( File f : list ) 
	    {
	    
	    	if ( f.isDirectory() ) 
	    	{
	                replaceFiles( f.getAbsolutePath(), prefix + "/" + f.getName());
	        }
	        else if (f.getName().endsWith(".gzt"))
	    	{
	        	try 
	        	{
	        		UIUtils.fileUnGzip(f.getAbsolutePath(), f.getAbsolutePath().substring(0, f.getAbsolutePath().length() - 4));
	        		f.deleteOnExit();
				} 
	        	catch (FileNotFoundException e) 
	        	{
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
	        	catch (IOException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        	
	        	
	        	
	            
	            
	    	}
	        
	    }
		
	}

	
	
	public static ArrayList<String> getNetworkInterfaceIPs() 
	{
		ArrayList<String> res = new ArrayList<String>();
		
	    Enumeration<NetworkInterface> nets;
		try {
			nets = NetworkInterface.getNetworkInterfaces();
			
			for (NetworkInterface netint : Collections.list(nets))
	        {
		        Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
		        for (InetAddress inetAddress : Collections.list(inetAddresses)) 
		        {
		            res.add(inetAddress.getHostAddress());
		        }
	        }
	     
			
		} catch (SocketException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	        
	
		return res;
	}  
	
	

	public static boolean isServerLocal() 
	{
		boolean res = false;
		
		for (String ip :  getNetworkInterfaceIPs())
		{
			
			if (ip.equals(MainWin.getHost()))
				res = true;
		}
		
		return res;
	}
	
	
	
}
