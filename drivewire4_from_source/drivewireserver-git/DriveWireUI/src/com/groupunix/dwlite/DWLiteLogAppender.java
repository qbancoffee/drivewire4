package com.groupunix.dwlite;



import javax.swing.JTextArea;

import org.apache.log4j.Appender;
import org.apache.log4j.Layout;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.spi.ErrorHandler;
import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggingEvent;

public class DWLiteLogAppender implements Appender 
{

	private static Layout layout = new PatternLayout("%d{dd MMM yyyy HH:mm:ss} %-5p [%-14t] %m%n");
	
	private JTextArea textArea;

	public DWLiteLogAppender(JTextArea txtArea)
	{
		this.textArea = txtArea;
	}
	
	
	
	public void addFilter(Filter arg0) {
		// TODO Auto-generated method stub

	}

	
	public void clearFilters() {
		// TODO Auto-generated method stub

	}

	
	public void close() {
		// TODO Auto-generated method stub

	}

	
	public void doAppend(LoggingEvent arg0) 
	{
		this.textArea.append(arg0.getTimeStamp() + " " + arg0.getLevel().toString() + " " + arg0.getThreadName() + " " + arg0.getRenderedMessage() + System.getProperty("line.separator"));
	}

	
	public ErrorHandler getErrorHandler() {
		// TODO Auto-generated method stub
		return null;
	}

	
	public Filter getFilter() {
		// TODO Auto-generated method stub
		return null;
	}

	
	public Layout getLayout() 
	{
		return DWLiteLogAppender.layout;
	}

	
	public String getName() 
	{
		return "DWLiteAppender";
	}

	
	public boolean requiresLayout() 
	{
		return true;
	}

	
	public void setErrorHandler(ErrorHandler arg0) {
		// TODO Auto-generated method stub

	}

	
	public void setLayout(Layout arg0) 
	{
		DWLiteLogAppender.layout = arg0;
	}

	
	public void setName(String arg0) {
		// TODO Auto-generated method stub

	}

}

