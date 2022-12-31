package com.groupunix.drivewireui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;

import com.groupunix.drivewireui.exceptions.DWUIOperationFailedException;


public class Connection 
{
	
	private static final int BUFFER_SIZE = 2048;
	private int port;
	private String host;
	private int instance;
	
	private Socket sock;

	private BufferedReader in;
	
	
	public Connection(String host, int port, int instance)
	{
		setHost(host);
		setPort(port);
		setInstance(instance);
	}
	
	
	public void Connect() throws UnknownHostException, IOException
	{
		MainWin.setConStatusConnect();
		this.sock = new Socket(this.host, this.port);
		this.sock.setSoTimeout(MainWin.config.getInt("TCPTimeout",MainWin.default_TCPTimeout));
		this.in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
	}
	
	
	public void setPort(int port) {
		this.port = port;
	}
	public int getPort() {
		return port;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public String getHost() {
		return host;
	}


	public void close() throws IOException 
	{
		this.sock.close();
	}


	public boolean connected() 
	{
		if (this.sock.isClosed())
		{
			return false;
		}
		
		return true;
	}

	
	
	
	

	public void sendCommand(String cmd, int instance) throws IOException, DWUIOperationFailedException 
	{
		
		// send command
		List<String> resp = loadList(instance,cmd);	
			
		if ((resp.size() > 0) && (resp.get(0).startsWith("FAIL")))
		{
			throw new DWUIOperationFailedException(resp.get(0).trim());
			
		}
		
				
	}




	public void setInstance(int instance) {
		this.instance = instance;
	}


	public int getInstance() {
		return instance;
	}
	
	
	public StringReader loadReader(int instance, String arg) throws IOException, DWUIOperationFailedException 
	{
		
		sock.getOutputStream().write((instance + "").getBytes());
		sock.getOutputStream().write(0);
		sock.getOutputStream().write((arg + "\n").getBytes());
		
		return(new StringReader(getResponse()));
	}

	
	
	
	private String getResponse() throws IOException, DWUIOperationFailedException
	{
		StringBuilder buffer = new StringBuilder(BUFFER_SIZE * 2);
		char[] cbuf = new char[BUFFER_SIZE];
		
		int readres = in.read(cbuf, 0, BUFFER_SIZE);
		
		while (!this.sock.isClosed() && (readres > -1))
		{
			buffer.append(cbuf, 0, readres);
			readres = in.read(cbuf, 0, BUFFER_SIZE);
			
		}
		
		
		// check for DW header
		if (buffer.length() < 3)
			throw new DWUIOperationFailedException(com.groupunix.drivewireserver.DWDefs.RC_UI_MALFORMED_RESPONSE, "Incomplete or missing header");
			
		if ((buffer.charAt(0) == 0) && (buffer.charAt(2) == 0))
		{
			// check for DW error response
			if (buffer.charAt(1) != 0)
				throw new DWUIOperationFailedException((byte) buffer.charAt(1),buffer.substring(3));
		}
		else
		{
			// dont have 0 x 0
			throw new DWUIOperationFailedException(com.groupunix.drivewireserver.DWDefs.RC_UI_MALFORMED_RESPONSE, "Corrupt header (Old server version?)");
		}
		
		// strip header, send along
		buffer.delete(0,3);
		return(buffer.toString());
	}


	public List<String> loadList(int instance, String arg) throws IOException, DWUIOperationFailedException 
	{
		
		sock.getOutputStream().write((instance + "").getBytes());
		sock.getOutputStream().write(0);
		sock.getOutputStream().write((arg + "\n").getBytes());
		
		List<String> res = Arrays.asList(getResponse().split("\n"));
		
		return res;
	}


	public Socket getSocket()
	{
		return this.sock;
	}

	


	
	
	
}
