package com.groupunix.drivewireui;

public class BasicLine
{
	private String line;
	private int lineno = -1;
	
	BasicLine(String ln)
	{
		if (ln.matches("^\\d+\\s.+"))
		{
			this.setLine(ln.substring(ln.indexOf(" ")));
			this.setLineno(Integer.parseInt( ln.substring(0, ln.indexOf(" ")) ));
		}
		else
		{
			this.setLine(ln);
		}
	}

	public void setLine(String line)
	{
		this.line = line;
	}

	public String getLine()
	{
		return line;
	}

	public void setLineno(int lineno)
	{
		this.lineno = lineno;
	}

	public int getLineno()
	{
		return lineno;
	}
}
