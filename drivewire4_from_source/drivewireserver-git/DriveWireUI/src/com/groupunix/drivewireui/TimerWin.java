package com.groupunix.drivewireui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.wb.swt.SWTResourceManager;

public class TimerWin extends Dialog
{

	protected static final long UPDATE_SLEEP = 150;
	protected Object result;
	protected Shell shlTimers;
	private Table table;
	protected ArrayList<TimerData> timers;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public TimerWin(Shell parent, int style)
	{
		super(parent, style);
		setText("SWT Dialog");
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open()
	{
		this.timers = new ArrayList<TimerData>();
		createContents();
		shlTimers.open();
		shlTimers.layout();
		final Display display = getParent().getDisplay();
		
		Thread ut = new Thread(new Runnable() 
		{

			@Override
			public void run()
			{
				boolean die = false;
				
				while (!die)
				{
				
					try
					{
						Thread.sleep(UPDATE_SLEEP);
					} 
					catch (InterruptedException e)
					{
						die = true;
					}
					
					if (!die && !shlTimers.isDisposed())
					{
						try
						{
							List<String> res = UIUtils.loadList( MainWin.getInstance(), "ui instance timer show");
							
							timers = new ArrayList<TimerData>();
							
							for (String l : res)
							{
								timers.add(new TimerData(l.trim()));
							}
							
							
							
							display.asyncExec(new Runnable() {

								@Override
								public void run()
								{
									if (!shlTimers.isDisposed())
									{
										for (int i = 0; i < timers.size();i++)
										{
											TableItem ti;
											
											if (table.getItemCount() <= i)
											{
												ti = new TableItem(table, SWT.NONE);
											}
											else
											{
												ti = table.getItem(i);
											}
											
											ti.setText(0, timers.get(i).getTimerNumber() + "");
											ti.setText(1, timers.get(i).getTimerDesc());
											ti.setText(2, timers.get(i).getTimerValue() + "");
											ti.setText(3, timers.get(i).getTimerDuration());
										}
									}
								} });
							
							
							
						} 
						catch (IOException e)
						{
							die = true;
						} catch (DWUIOperationFailedException e)
						{
							die = true;
						}
						
						
						
					}
				}	
			 }
		});
		
		ut.setDaemon(true);
		ut.start();
		
		while (!shlTimers.isDisposed())
		{
			if (!display.readAndDispatch())
			{
				display.sleep();
			}
		}
		return result;
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents()
	{
		shlTimers = new Shell(getParent(), SWT.SHELL_TRIM);
		shlTimers.setImage(SWTResourceManager.getImage(TimerWin.class, "/constatus/user-away.png"));
		shlTimers.setSize(450, 300);
		shlTimers.setText("Timers");
		shlTimers.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		table = new Table(shlTimers, SWT.FULL_SELECTION);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		TableColumn tableColumn = new TableColumn(table, SWT.NONE);
		tableColumn.setWidth(36);
		tableColumn.setText("#");
		
		TableColumn tblclmnDescription = new TableColumn(table, SWT.NONE);
		tblclmnDescription.setWidth(180);
		tblclmnDescription.setText("Description");
		
		TableColumn tblclmnValue = new TableColumn(table, SWT.RIGHT);
		tblclmnValue.setWidth(86);
		tblclmnValue.setText("Value");
		
		TableColumn tblclmnDuration = new TableColumn(table, SWT.NONE);
		tblclmnDuration.setWidth(110);
		tblclmnDuration.setText("Duration");
		
		final Menu popup = new Menu (shlTimers, SWT.POP_UP);
		table.setMenu(popup);
		
		popup.addMenuListener(new MenuAdapter() {
			@Override
			public void menuShown(MenuEvent e) 
			{
				for (MenuItem mi : popup.getItems())
				{
					mi.dispose();
				}
				
				if (table.getSelectionCount() == 0)
					
					return;
				
				MenuItem mntmResetCounter = new MenuItem(popup, SWT.NONE);
				mntmResetCounter.setText("Reset counter " + table.getSelection()[0].getText(0));
				mntmResetCounter.addSelectionListener(new SelectionAdapter() 
				{ 
					@Override
					public void widgetSelected(SelectionEvent e) 
					{
						try
						{
							UIUtils.loadList(MainWin.getInstance(), "ui instance timer reset " + table.getSelection()[0].getText(0) );
						} catch (IOException e1)
						{
							
						} catch (DWUIOperationFailedException e1)
						{
							
						}
					}
				});
			}
		});
		
		

		
		
		
	}

	
	protected Table getTable() {
		return table;
	}
}
