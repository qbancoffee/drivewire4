package com.groupunix.drivewireui.plugin;

import org.eclipse.swt.widgets.Composite;

import com.groupunix.drivewireui.MainWin;


public abstract class DWUIPlugin
{

	public static final int INIT_OK = 0;
	public static final int INIT_ERROR = 1;
	private Composite parentComposite;
	
	
	
	public abstract String getPluginName();
	public abstract String getPluginVersion();
	public abstract boolean isSingleInstance();
	
	
	
	public boolean isContentComposite()
	{
		return false;
	}
	
	public abstract Composite getContentComposite();
	public void setParentComposite(Composite p)
	{
		this.parentComposite = p;
	}
	
	public Composite getParentComposite()
	{
		return this.parentComposite;
	}
	
	
	public boolean isDiskUpdates()
	{
		return false;
	}
	
	public boolean isEnabled() 
	{
		return MainWin.config.getBoolean("plugin_" + this.getPluginName() + "_enabled", true);
	}

	
	public int init()
	{
		return INIT_OK;
	}
	

	public void update() 
	{

	}

	public void shutdown()
	{
		
	}
	
	public boolean isLogUpdates() {
		return false;
	}
	
	
	
	

	
}
