package com.groupunix.drivewire.cli;


import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class DWCLI 
{

	
	
	private static final int BUFFER_SIZE = 2048;

	
	
	public static void main(String[] args) 
	{
		// defaults
		String serverAddress = "127.0.0.1";
		int serverPort = 6800;
		int serverInstance = 0;
		boolean verbose = false;
		
		// command line arguments
		Options cmdoptions = new Options();
		
		cmdoptions.addOption("help", false, "display command line argument help and exit");
		cmdoptions.addOption("server", true, "server hostname or IP address (default 127.0.0.1)");
		cmdoptions.addOption("port", true, "server ui port (default 6800)");
		cmdoptions.addOption("instance", true, "server instance # (default 0)");
		cmdoptions.addOption("command", true, "(required) command to send to server");
		cmdoptions.addOption("verbose", false, "verbose output (debugging)");
		
		CommandLineParser parser = new GnuParser();
		
		try 
		{ 
			
			CommandLine line = parser.parse( cmdoptions, args );
		    
			if (!line.hasOption("command") || line.hasOption("help"))
			{
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp( "java -jar DWCLI.jar [OPTIONS]", cmdoptions );
				System.exit(0);
			}
	
			String command = line.getOptionValue("command"); 
				
			
			if (line.hasOption("server"))
			{
				serverAddress = line.getOptionValue("server");
			}
			
			if (line.hasOption("port"))
			{
				serverPort = Integer.parseInt(line.getOptionValue("port"));
			}
			
			if (line.hasOption("instance"))
			{
				serverInstance = Integer.parseInt(line.getOptionValue("instance"));
			}
			
			
			// sanity
			if (serverPort < 1 || serverPort > 65535)
				throw new NumberFormatException("Valid port numbers are 1-65535");
				
			if (serverInstance < 0 || serverInstance > 255)
				throw new NumberFormatException("Valid instance numbers are 0-255");
			
			
			
			if (line.hasOption("verbose"))
			{
				verbose = true;
			}
			
			
			doCmd(serverAddress, serverPort, serverInstance, command, verbose);
			
			
		}
		catch( ParseException e)
		{
			
		}
		catch (NumberFormatException e1) 
		
		{
		    handleException(e1);
		}
		
		System.exit(0);
		
	}

	private static void handleException(Exception e) 
	{
		System.err.println(System.getProperty("line.separator") + "Error:" + System.getProperty("line.separator") + System.getProperty("line.separator") + e.getMessage() );
	    System.exit(-1);
	}

	private static void doCmd(String serverAddress, int serverPort, int serverInstance, String command, boolean verbose) 
	{
		
		if (verbose)
		{
			System.out.println("Server:    " + serverAddress);
			System.out.println("Port:      " + serverPort);
			System.out.println("Instance:  " + serverInstance);
			System.out.println("Command:   " + command);
	
		}
		
		Socket sock = null;
		
		try 
		{
			sock = new Socket(serverAddress, serverPort);	
			DataOutputStream out = new DataOutputStream(sock.getOutputStream());
			BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			
			out.writeBytes(serverInstance + "");
			out.write(0);
			out.writeBytes(command + '\n');
			
			StringBuilder buffer = new StringBuilder(BUFFER_SIZE * 2);
			char[] cbuf = new char[BUFFER_SIZE];
			
			int readres = in.read(cbuf, 0, BUFFER_SIZE);
			
			while (!sock.isClosed() && (readres > -1))
			{
				buffer.append(cbuf, 0, readres);
				readres = in.read(cbuf, 0, BUFFER_SIZE);
				
			}
			
			
			// check for DW header
			if (buffer.length() < 3)
				throw new IOException("Incomplete or missing header in server response");
				
			if ((buffer.charAt(0) == 0) && (buffer.charAt(2) == 0))
			{
				// check for DW error response
				if (buffer.charAt(1) != 0)
					throw new IOException("Server says: " + buffer.substring(3));
			}
			else
			{
				// dont have 0 x 0
				throw new IOException("Corrupt header in response (Old server version?)");
			}
			
			// strip header, send along
			buffer.delete(0,3);
			
			System.out.print(buffer.toString());
	
			
		} 
		catch (IOException e) 
		{
			handleException(e);
		} 
		finally
		{
			if ((sock != null) && (!sock.isClosed()))
			{
				try {
					sock.close();
				} catch (IOException e) 
				{
				}
			}
		}
		 
		
		
		
	}

}
