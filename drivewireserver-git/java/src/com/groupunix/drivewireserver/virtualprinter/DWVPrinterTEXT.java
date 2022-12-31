package com.groupunix.drivewireserver.virtualprinter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.log4j.Logger;

import com.groupunix.drivewireserver.dwexceptions.DWPrinterFileError;
import com.groupunix.drivewireserver.dwexceptions.DWPrinterNotDefinedException;
import com.groupunix.drivewireserver.dwprotocolhandler.DWUtils;
import com.groupunix.drivewireserver.virtualserial.DWVSerialCircularBuffer;

public class DWVPrinterTEXT implements DWVPrinterDriver 
{
	
	private static final Logger logger = Logger.getLogger("DWServer.DWVPrinter.DWVPrinterTEXT");

	private DWVSerialCircularBuffer printBuffer = new DWVSerialCircularBuffer(-1, true);
	
	private HierarchicalConfiguration config; 

	
	public DWVPrinterTEXT(HierarchicalConfiguration config) 
	{
		this.config = config;
		this.printBuffer.clear();
	}

	
	
	public void addByte(byte data) throws IOException 
	{
		this.printBuffer.getOutputStream().write(data);
		
	}

	
	public String getDriverName() 
	{
		return("TEXT");
	}

	
	public void flush() throws IOException, DWPrinterNotDefinedException, DWPrinterFileError 
	{
		
		File file = getPrinterFile();
		
		FileOutputStream fos = new FileOutputStream(file);
		
		while (this.printBuffer.getAvailable() > 0)
		{
			byte[] buf = new byte[256];
			int read = this.printBuffer.getInputStream().read(buf);
			fos.write(buf, 0, read);
		}
		
		fos.flush();
		fos.close();
		
		logger.info("Flushed print job to " + file.getCanonicalPath());
		
		// execute cocodw command..
		if (config.containsKey("FlushCommand"))
		{
			doExec(doVarSubst(file, config.getString("FlushCommand")));
		}
		
	}


	private void doExec(String cmd) 
	{
		
		try 
		{
			logger.info("executing flush command: " + cmd);
			Runtime.getRuntime().exec(cmd);
		} 
		catch (IOException e) 
		{
			logger.warn("Error during flush command: " + e.getMessage());
		}
	}


	private String doVarSubst(File file, String cmd) throws IOException 
	{
		// substitute vars in cmdline
		// $name - printer name
		// $file - full file path
		
		HashMap<String, String> vars = new HashMap<String, String>();
		vars.put("name", config.getString("Name"));
		// double \ so the replaceall doesnt eat it later
		vars.put("file", file.getCanonicalPath().replaceAll("\\\\","\\\\\\\\"));
		
		for (Map.Entry<String, String> e : vars.entrySet()) 
		{
			  cmd = cmd.replaceAll("\\$" + e.getKey(), e.getValue());
		}
		
		return(cmd);

	}


	public String getFileExtension() 
	{
		return(".txt");
	}


	public String getFilePrefix() 
	{
		return("dw_text_");
	}

	
	public File getPrinterFile() throws IOException, DWPrinterNotDefinedException, DWPrinterFileError 
	{
		if (this.config.containsKey("OutputFile"))
		{
			if (DWUtils.FileExistsOrCreate(this.config.getString("OutputFile")))
			{
				return(new File(this.config.getString("OutputFile")));
			}
			else
			{
				throw new DWPrinterFileError("Cannot find or create the output file '" + this.config.getString("OutputFile") + "'");
			}
			
		} 
		else if (this.config.containsKey("OutputDir"))
		{
			if (DWUtils.DirExistsOrCreate(this.config.getString("OutputDir")))
			{
				return(File.createTempFile(getFilePrefix(),getFileExtension(), new File(this.config.getString("OutputDir"))));
					
			}
			else
			{
				throw new DWPrinterFileError("Cannot find or create the output directory '" + this.config.getString("OutputDir") + "'");
				
			}
		
		}
		else
		{
			throw new DWPrinterFileError("No OutputFile or OutputDir defined in printer config, don't know where to send output.");
		}
	}


	
	public String getPrinterName() 
	{
		return(this.config.getString("[@name]","?noname?"));
	}

	
	
}
