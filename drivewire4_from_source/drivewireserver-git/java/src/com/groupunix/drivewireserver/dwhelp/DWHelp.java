package com.groupunix.drivewireserver.dwhelp;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

import net.htmlparser.jericho.Source;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.log4j.Logger;

import com.groupunix.drivewireserver.dwcommands.DWCmd;
import com.groupunix.drivewireserver.dwcommands.DWCommand;
import com.groupunix.drivewireserver.dwexceptions.DWHelpTopicNotFoundException;
import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocol;

public class DWHelp 
{
	private XMLConfiguration help;
	private String helpfile = null;
	
	private static final Logger logger = Logger.getLogger("DWHelp");
	
	public DWHelp(String helpfile) 
	{
		this.helpfile = helpfile;
		
		this.reload();
	}

	
	public DWHelp(DWProtocol dwProto) 
	{
		//try 
		//{
			help = new XMLConfiguration();
			addAllTopics(new DWCmd(dwProto), "");
			//help.setFileName(DWDefs.HELP_DEFAULT_FILE);
			//help.save(DWDefs.HELP_DEFAULT_FILE);
		//} 
		//catch (ConfigurationException e) 
	//	{
	//		logger.warn(e.getMessage());
	//	}
		
	}
	
	public void reload()
	{
		// load helpfile if possible
		logger.debug("reading help from '" + this.helpfile + "'");
		
		try 
    	{
			
			this.help = new XMLConfiguration(helpfile);
			
			// local help file
			
			this.help.setListDelimiter((char) 10);
			
			//this.help.setAutoSave(true);
			
		} 
    	catch (ConfigurationException e1) 
    	{
    		logger.warn("Error loading help file: " + e1.getMessage());
		}
		
	}

	
	@SuppressWarnings("unused")
	private void loadWikiTopics(String sourceUrlString) throws IOException 
	{
		Source source=new Source(new URL(sourceUrlString));
		source.getRenderer().setMaxLineLength(32);
		String renderedText=source.getRenderer().toString();
		
		String[] lines = renderedText.split("\n");
		
		String curkey = null;
		int blanks =0;
		
		for (int i=0;i<lines.length;i++)
		{
			lines[i] = lines[i].trim();
			
			
			if (this.hasTopic(lines[i]))
			{
				curkey = lines[i];
				this.clearTopic(lines[i]);
				blanks = 0;
			}
			else
			{
				if (lines[i].equals(""))
				{
					blanks++;
				}
				else
				{
					blanks = 0;
				}
				
				if ((blanks < 2) && (curkey != null))
				{
					System.out.println("Line: " + lines[i]);
					
					this.help.addProperty("topics." + this.spaceToDot(curkey) + ".text", lines[i]);
				}
				
				if (blanks == 3)
				{
					curkey = null;
				}
			}

		}

	}
	


	private void clearTopic(String topic) 
	{
		this.help.clearTree("topics." + this.spaceToDot(topic) + ".text");
	}

	public boolean hasTopic(String topic)
	{
		if (this.help != null)
			return(this.help.containsKey("topics." + this.spaceToDot(topic) + ".text"));
		return false;
	}
	


	public String getTopicText(String topic) throws DWHelpTopicNotFoundException
	{
		if (this.hasTopic(topic))
		{
			String text = new String();
			
			topic = this.spaceToDot(topic);
		
			
			String[] txts = help.getStringArray("topics." + topic + ".text");
			
			for (int i = 0;i<txts.length;i++)
			{
				text += txts[i] + "\r\n";
			}
			
			return(text);
		}
		else
		{
			throw new DWHelpTopicNotFoundException("There is no help available for the topic '" + topic + "'.");
		}
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<String> getTopics(String topic)
	{
		ArrayList<String> res = new ArrayList<String>();
		
		if (this.help != null)
		{
			for(Iterator<String> itk = help.configurationAt("topics").getKeys(); itk.hasNext();)
			{
				String key = itk.next();
				if (key.endsWith(".text"))
					res.add(this.dotToSpace(key.substring(0, key.length()-5)));
				
			}
		}
		
		return(res);
	}

	
	private String spaceToDot(String topic) 
	{
		return topic.replaceAll(" ", "\\." );
	}
	
	private String dotToSpace(String topic) 
	{
		return topic.replaceAll("\\.", " ");
	}

	
	private void addAllTopics(DWCommand dwc, String prefix)
	{
		String key = "topics.";
		
		if (!prefix.equals(""))
			key += spaceToDot(prefix) + "."; 
		
		key += spaceToDot(dwc.getCommand()) + ".text";
		
		help.addProperty(key, dwc.getUsage());
		help.addProperty(key, "");
		help.addProperty(key, dwc.getShortHelp());
		
		if (dwc.getCommandList() != null)
		{
			for (DWCommand dwsc : dwc.getCommandList().getCommands())
			{
				if (!prefix.equals(""))
					addAllTopics(dwsc, prefix + " " + dwc.getCommand());
				else
					addAllTopics(dwsc, dwc.getCommand());
			}
		}
		
	}


	@SuppressWarnings("unchecked")
	public ArrayList<String> getSectionTopics(String section)
	{
		ArrayList<String> res = new ArrayList<String>();
		if (this.help != null)
		{
			for(Iterator<String> itk = help.configurationAt("topics." + section).getKeys(); itk.hasNext();)
			{
				String key = itk.next();
				if (key.endsWith(".text"))
					res.add(this.dotToSpace(key.substring(0, key.length()-5)));
				
			}
		}
		return(res);
	}
	
	
}
