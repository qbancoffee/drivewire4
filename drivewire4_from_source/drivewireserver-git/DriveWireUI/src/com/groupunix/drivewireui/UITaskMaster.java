package com.groupunix.drivewireui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Composite;

public class UITaskMaster
{
	public static final int TASK_STATUS_ACTIVE = 0;
	public static final int TASK_STATUS_COMPLETE = 1;
	public static final int TASK_STATUS_FAILED = 2;
	static Font taskFont;
	static Font versionFont;
	private int nexttaskno = 0;
	
	private Composite master;
	private List<UITask> tasks = new ArrayList<UITask>();
	
	public UITaskMaster(Composite master)
	{
		this.master = master;
		
		HashMap<String,Integer> fontmap = new HashMap<String,Integer>();
		
		fontmap.put("Droid Sans Mono", SWT.NORMAL);
		
		UITaskMaster.taskFont = UIUtils.findFont(master.getDisplay(), fontmap, "4.0.0", 36,17);
		
		fontmap.clear();
		fontmap.put("Roboto Cn", SWT.NORMAL);
		fontmap.put("Roboto", 3);
		fontmap.put("Roboto", 0);
		UITaskMaster.versionFont = UIUtils.findFont(master.getDisplay(), fontmap, "4.0.0", 24,15);
		
		
	}
	
	
	public int addTask(final String cmd)
	{
	
		
		master.getDisplay().syncExec(new Runnable() {
		  public void run()
		  {
			  
			  UITaskComposite tc;
			  UITask task;
			  
			  if (tasks.size() > MainWin.config.getInt("DWOpsHistorySize", 20))
			  {
					  tasks.remove(0);
			  }
				
			  
			  synchronized(tasks)
			  {
				  nexttaskno++;
				  
				  if (cmd.equals("/splash"))
				  {
					  tc = new UITaskCompositeSplash(master, SWT.DOUBLE_BUFFERED, nexttaskno);
					
				  }
				  else
				  {
					  master.setRedraw(false);
					  tc =  new UITaskComposite(master, SWT.DOUBLE_BUFFERED, nexttaskno);
					  // get some initial dimensions, maybe not needed..
					  tc.setBounds(0,tasks.size() * 40, master.getClientArea().width, 40);
					  
				  }
					
				  tc.setCommand(cmd);
				
			  	  task = new UITask(nexttaskno, tc);
			  	  tasks.add(task);
			  }
			  
			  master.setRedraw(true);
			  
			  
			  if (MainWin.tabFolderOutput.getSelectionIndex() != 0)
			  {
				  MainWin.tabFolderOutput.getItems()[0].setImage(org.eclipse.wb.swt.SWTResourceManager.getImage(MainWin.class, "/logging/info.png"));
			  }
		   }
		});
	
	
	
		return(nexttaskno);
		
	}
	
	protected void resizeTasks()
	{
		int y = 0;
			
		for (UITask t : this.tasks)
		{
			if ((t.getTaskcomp() != null) && (!t.getTaskcomp().isDisposed()))
			{
				t.getTaskcomp().setRedraw(false);
				t.setTop(y);
				y += t.getHeight();

				t.setBottom(y);
			}
		}
		
		master.setBounds(0,0, master.getBounds().width, y);
		
		MainWin.scrolledComposite.setOrigin(0, y);
		
		for (UITask t : this.tasks)
		{
			if ((t.getTaskcomp() != null) && (!t.getTaskcomp().isDisposed()))
			{
				t.getTaskcomp().setRedraw(true);
			}
		}
	}

	public UITask getTask(int tid) throws DWUINoSuchTaskException
	{
		for (UITask t : this.tasks)
		{
			if (t.getTaskID() == tid)
			{
				return(t);
			}
		}
		
		throw new DWUINoSuchTaskException("No task id " + tid);
	}
	
	public void updateTask(final int tid, final int status, final String txt)
	{
		master.getDisplay().syncExec(new Runnable() {
		  public void run()
		  {
			  MainWin.scrolledComposite.setRedraw(false);
			
			  
			try
			{
				UITask t = getTask(tid);
				
				t.getTaskcomp().setRedraw(false);
				
				if (txt != null)
					t.setText(txt);
				
				 t.setStatus(status);
				 
				 t.getTaskcomp().setRedraw(true);
			} 
			catch (DWUINoSuchTaskException e)
			{
				
			}
			  
			  resizeTasks(); 
			  MainWin.scrolledComposite.setRedraw(true);
			 
			  if (MainWin.tabFolderOutput.getSelectionIndex() != 0)
			  {
				  
				  switch(status)
				  {
			  			case UITaskMaster.TASK_STATUS_ACTIVE:
			  				MainWin.tabFolderOutput.getItems()[0].setImage(org.eclipse.wb.swt.SWTResourceManager.getImage(MainWin.class, "/status/active_16.png"));
							break;
			  			case UITaskMaster.TASK_STATUS_FAILED:
			  				MainWin.tabFolderOutput.getItems()[0].setImage(org.eclipse.wb.swt.SWTResourceManager.getImage(MainWin.class, "/status/failed_16.png"));
			  				break;
			  			case UITaskMaster.TASK_STATUS_COMPLETE:
			  				MainWin.tabFolderOutput.getItems()[0].setImage(org.eclipse.wb.swt.SWTResourceManager.getImage(MainWin.class, "/status/completed_16.png"));
			  				break;
			  			default:
			  				MainWin.tabFolderOutput.getItems()[0].setImage(org.eclipse.wb.swt.SWTResourceManager.getImage(MainWin.class, "/status/unknown_16.png"));
			  				break;
			  		}
				  
			  }
		  }
	  });
	}

	public int getNumTasks()
	{
		return this.tasks.size();
	}


	public static Font taskFont()
	{
		// TODO Auto-generated method stub
		return taskFont;
	}


	public void removeTask(int tid)
	{
		MainWin.shell.setRedraw(false);
		
		int deltid = -1;
		int curtid = 0;
		
		for (UITask t : this.tasks)
		{
			
			if (t.getTaskID() == tid)
			{
				deltid = curtid;
				if ((t.getTaskcomp() != null) && (!t.getTaskcomp().isDisposed()))
				{
					t.getTaskcomp().dispose();
				}
						
			}
			
			curtid++;
		}
		
		if (deltid > -1)
		{
			this.tasks.remove(deltid);
			this.resizeTasks();
		}
		
		MainWin.shell.setRedraw(true);
	}


	public boolean hasTask(int tid)
	{
		for (UITask t : this.tasks)
		{
			if (t.getTaskID() == tid)
			{
				return true;
			}
		}
		return false;
	}


	public void rotateWaiters()
	{
		for (UITask t : this.tasks)
		{
			if (t.getStatus() == UITaskMaster.TASK_STATUS_ACTIVE)
			{
				t.rotateActive();
			}
		}
	}
	
	
	
	public Composite getMaster()
	{
		return this.master;
	}


	public List<UITask> getTasks()
	{
		return(this.tasks);
	}


	public void removeAllTasks()
	{
		Vector<Integer> tids = new Vector<Integer>();
		
		for (UITask t : this.tasks)
		{
			if (t.getStatus() != UITaskMaster.TASK_STATUS_ACTIVE)
			{
				tids.add(t.getTaskID());
			}
		}
		
		for (Integer tid : tids)
		{
			this.removeTask(tid);
		}
		
	}


	


	
	
}
