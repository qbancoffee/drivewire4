package com.groupunix.drivewireui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

public class UITaskComposite extends Composite
{

	protected Canvas status;
	protected StyledText details;
	protected String cmd = "";
	protected String det = "";
	private int activeframe = 0;
	private int taskid = -1;
	
	protected int stat;
	
	public static Image activeFrames = org.eclipse.wb.swt.SWTResourceManager.getImage(MainWin.class, "/status/process-working-2.png");
	
	public UITaskComposite(Composite master, int style, int tid)
	{
		super(master, style | SWT.DOUBLE_BUFFERED );
		this.taskid = tid;
		this.createContents(master);
	}


	private void createContents(Composite master)
	{
		this.setBackground(MainWin.colorWhite);
		
		this.status = new Canvas(this, SWT.DOUBLE_BUFFERED);
		this.status.setBounds(3, 3, 37, 37);
		this.status.setBackground(MainWin.colorWhite);
		
		this.status.addPaintListener(new PaintListener() {
		      public void paintControl(PaintEvent e) {
		          
		    	 
		    	  
		    	  switch(stat)
		  		{
		  			case UITaskMaster.TASK_STATUS_ACTIVE:
		  				int x = (activeframe % 8) * 32;
		  				int y = (activeframe / 8) * 32;
		  				
		  				
		  				e.gc.drawImage(activeFrames, x , y , 32, 32, 0, 0, 32, 32);
		  				break;
		  			case UITaskMaster.TASK_STATUS_FAILED:
		  				 e.gc.drawImage(org.eclipse.wb.swt.SWTResourceManager.getImage(MainWin.class, "/status/failed.png"),0,0);
		  				break;
		  			case UITaskMaster.TASK_STATUS_COMPLETE:
		  				e.gc.drawImage(org.eclipse.wb.swt.SWTResourceManager.getImage(MainWin.class, "/status/completed.png"),0,0);
		  				break;
		  			default:
		  				e.gc.drawImage(org.eclipse.wb.swt.SWTResourceManager.getImage(MainWin.class, "/status/unknown.png"),0,0);
		  				break;
		  		}
		    	  
		    	
		        }
		      });
		
		
		this.details = new StyledText(this, SWT.READ_ONLY | SWT.WRAP | SWT.MULTI | SWT.DOUBLE_BUFFERED );
		
		//this.details.setBackground(null);
		this.details.setBounds(42, 4, master.getClientArea().width - 44 , 40);
		this.details.setSize(master.getClientArea().width - 44, 40);
		this.details.setText("");
		
		this.details.setFont(UITaskMaster.taskFont());
		
		this.details.setBackground(MainWin.colorWhite);
		
		final Menu menu = new Menu(this.getShell(), SWT.POP_UP);
		this.details.setMenu(menu);
		
		
		
		menu.addMenuListener(new MenuAdapter() {
			@Override
			public void menuShown(MenuEvent e) 
			{
				for (MenuItem m : menu.getItems() )
				{
					m.dispose();
				}
				
				
				if (details.getSelectionCount() > 0)
				{
					MenuItem miCopy = new MenuItem(menu, SWT.PUSH);
					miCopy.addSelectionListener(new SelectionAdapter() {
						@Override
						public void widgetSelected(SelectionEvent e) 
						{
							if (details.getSelectionCount() > 0)
							{
								String txt = details.getSelectionText();
								
								if (txt != null)
								{
									Clipboard clipboard = new Clipboard(MainWin.getDisplay());
									TextTransfer textTransfer = TextTransfer.getInstance();
							
									Transfer[] transfers = new Transfer[]{textTransfer};
									Object[] data = new Object[]{ txt };
									clipboard.setContents(data, transfers);
									clipboard.dispose();
								}
							}
						}
					});
					
					miCopy.setText ("Copy");
				}
				
				
				
				if ((cmd != null) && (cmd.startsWith("dw")))
				{
					MenuItem miRecall = new MenuItem(menu, SWT.PUSH);
					miRecall.addSelectionListener(new SelectionAdapter() {
						@Override
						public void widgetSelected(SelectionEvent e) 
						{
							MainWin.setDWCmdText(cmd);
						}
					});
					
					miRecall.setText ("Recall command");
					
					
					MenuItem miSend = new MenuItem(menu, SWT.PUSH);
					miSend.addSelectionListener(new SelectionAdapter() {
						@Override
						public void widgetSelected(SelectionEvent e) 
						{
							MainWin.sendCommand(cmd, true);
						}
					});
					
					miSend.setText ("Resend command");
					
				}
				
				@SuppressWarnings("unused")
				MenuItem div = new MenuItem(menu, SWT.SEPARATOR);
				
				if (getData("refreshinterval") != null)
				{
					if (stat == UITaskMaster.TASK_STATUS_ACTIVE)
					{
						MenuItem miStop = new MenuItem(menu, SWT.PUSH);
						miStop.setText("Stop auto refresh");
						miStop.addSelectionListener(new SelectionAdapter() {
							@Override
							public void widgetSelected(SelectionEvent e) 
							{
								MainWin.taskman.updateTask(taskid, UITaskMaster.TASK_STATUS_COMPLETE, null);
							}
						});
						
						
					}
				}
				
				if (cmd.startsWith("/") || cmd.startsWith("dw") || cmd.startsWith("ui"))
				{
					div = new MenuItem(menu, SWT.SEPARATOR);
					
					if (stat != UITaskMaster.TASK_STATUS_ACTIVE)
					{
						MenuItem miRefresh = new MenuItem(menu, SWT.PUSH);
						miRefresh.setText("Refresh");
						miRefresh.addSelectionListener(new SelectionAdapter() {
							@Override
							public void widgetSelected(SelectionEvent e) 
							{
								MainWin.sendCommand(cmd, taskid, true);
							}
						});
						
						
						MenuItem miStart = new MenuItem(menu, SWT.PUSH);
						miStart.setText("Start auto refresh");
						miStart.addSelectionListener(new SelectionAdapter() {
							@Override
							public void widgetSelected(SelectionEvent e) 
							{
								if (getData("refreshinterval") == null)
								{
									setData("refreshinterval",30);
								}
								
								MainWin.taskman.updateTask(taskid, UITaskMaster.TASK_STATUS_ACTIVE, null);
							}
						});
					}
				}
				
				div = new MenuItem(menu, SWT.SEPARATOR);
				
				if (stat != UITaskMaster.TASK_STATUS_ACTIVE)
				{
					MenuItem miRemove = new MenuItem(menu, SWT.PUSH);
					miRemove.setText("Remove");
					miRemove.addSelectionListener(new SelectionAdapter() {
						@Override
						public void widgetSelected(SelectionEvent e) 
						{
							MainWin.taskman.removeTask(taskid);
						}
					});
				}
				
				MenuItem miRemoveAll = new MenuItem(menu, SWT.PUSH);
				miRemoveAll.setText("Remove all items");
				miRemoveAll.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) 
					{
						MainWin.taskman.removeAllTasks();
					}
				});
				
			}
		});
		
	}


	public void setStatus(int stat)
	{
		this.stat = stat;
	
		this.status.redraw();
		
	}

	

	public void setCommand(String text)
	{
		this.cmd = text;
		setDetailText();
	}
	

	public void setDetails(String text)
	{
		if (text != null)
		{
			this.det = text;
		}
		else
		{
			this.det = "Null response";
		}
		
		setDetailText();
	}
	
	
	private void setDetailText()
	{
		this.details.setText(cmd.trim());
		int cmdend = cmd.trim().length();
		this.details.append(this.details.getLineDelimiter());
		
		StyleRange styleRange = new StyleRange();
		styleRange.start = 0;
		styleRange.length = cmdend;
		styleRange.foreground = MainWin.colorCmdTxt;
		this.details.setStyleRange(styleRange);
		
		this.details.append(det.trim());
	}


	public int getHeight()
	{
		
		return(Math.max(40,  8 + this.details.getTextBounds(0, this.details.getCharCount()-1).height));
	}


	public void setTop(int y)
	{
		this.setBounds(0, y, this.getBounds().width, this.getBounds().height);
		
	}
	
	public void setBottom(int y)
	{
		this.setBounds(0, this.getBounds().y, this.getBounds().width, y - this.getBounds().y);
		
		this.details.setBounds(42, this.details.getBounds().y , this.getBounds().width - 44 , this.getBounds().height - this.details.getBounds().y);
		//System.out.println("X: " + this.details.getBounds().x + " Y: " + this.details.getBounds().y + "  w: " + this.details.getBounds().width + "  h: " + this.details.getBounds().height);
	}
	
	public String getCommand()
	{
		return this.cmd;
	}


	public void rotateActive()
	{
		if (activeframe < 31)
			activeframe++;
		else
			activeframe = 1;
		
		if ((this.status != null ) && !this.status.isDisposed())
			this.status.redraw();
		
		
	}
	
}
