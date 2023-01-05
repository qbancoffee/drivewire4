package com.groupunix.drivewireui;


public class UITask
{
	private int status = 0;
	private UITaskComposite taskcomp;
	private String text = "";
	private int taskid = -1;
	
	
	public UITask(int tid, UITaskComposite tc)
	{
		this.taskid = tid;
		this.taskcomp = tc;
	}
	
	public void setStatus(int status)
	{
		this.status = status;
		this.taskcomp.setStatus(status);
	}

	public int getStatus()
	{
		return status;
	}

	public void setText(String text)
	{
		this.text = text;
		this.taskcomp.setDetails(text);
	}

	public String getText()
	{
		return text;
	}

	public void setTaskcomp(UITaskComposite taskcomp)
	{
		this.taskcomp = taskcomp;
	}

	public UITaskComposite getTaskcomp()
	{
		return taskcomp;
	}
	
	public int getHeight()
	{
		return this.taskcomp.getHeight();
	}

	public void setTop(int y)
	{
		this.taskcomp.setTop(y);
	}

	public void setBottom(int y)
	{
		this.taskcomp.setBottom(y);
	}

	public void rotateActive()
	{
		if ((this.taskcomp != null) && (!this.taskcomp.isDisposed()))
		{
			this.taskcomp.rotateActive();
			
			if (this.taskcomp.getData("refreshinterval") != null)
			{
				if (this.taskcomp.getData("refreshcount") == null)
					this.taskcomp.setData("refreshcount", 0);
				
				int rint = Integer.parseInt(this.taskcomp.getData("refreshinterval").toString());
				int rc = Integer.parseInt(this.taskcomp.getData("refreshcount").toString());
				
				if (rc < rint)
				{
					this.taskcomp.setData("refreshcount", rc + 1);
				}
				else
				{
					String cmd = getCommand();
					
					if (cmd != null)
					{
						this.taskcomp.setRedraw(false);
						
						if (cmd.startsWith("/") || cmd.startsWith("dw") || cmd.startsWith("ui"))
						{
							MainWin.sendCommand(cmd, this.taskid, false);
						}
							
						this.taskcomp.setRedraw(true);
						
					}
					
					this.taskcomp.setData("refreshcount", 0);
				}
				
			}
		}
		
	}
	
	public void setCommand(String cmd)
	{
		if ((this.taskcomp != null) && (!this.taskcomp.isDisposed()))
		{
			this.taskcomp.setCommand(cmd);
		}
	}
	
	public String getCommand()
	{
		if ((this.taskcomp != null) && (!this.taskcomp.isDisposed()))
		{
			return(this.taskcomp.getCommand());
		}
		
		return null;
	}

	public int getTaskID()
	{
		return this.taskid;
	}
	
}
