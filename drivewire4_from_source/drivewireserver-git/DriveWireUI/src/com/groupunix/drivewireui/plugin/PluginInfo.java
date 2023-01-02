package com.groupunix.drivewireui.plugin;

public class PluginInfo 
{

	private String pluginName;
	private String pluginVersion;
	private boolean pluginSingleInstance;
	private boolean pluginEnabled;
	private Class<? extends DWUIPlugin> pluginClass;

	public PluginInfo(DWUIPlugin plugin) 
	{
		this.setPluginName(plugin.getPluginName());
		this.setPluginVersion(plugin.getPluginVersion());
		this.setPluginSingleInstance(plugin.isSingleInstance());
		this.setPluginEnabled(plugin.isEnabled());
		this.setPluginClass(plugin.getClass());
	}

	public String getPluginName() {
		return pluginName;
	}

	public void setPluginName(String pluginName) {
		this.pluginName = pluginName;
	}

	public String getPluginVersion() {
		return pluginVersion;
	}

	public void setPluginVersion(String pluginVersion) {
		this.pluginVersion = pluginVersion;
	}

	public boolean isPluginSingleInstance() {
		return pluginSingleInstance;
	}

	public void setPluginSingleInstance(boolean pluginSingleInstance) {
		this.pluginSingleInstance = pluginSingleInstance;
	}

	public boolean isPluginEnabled() {
		return pluginEnabled;
	}

	public void setPluginEnabled(boolean pluginEnabled) {
		this.pluginEnabled = pluginEnabled;
	}

	public Class<? extends DWUIPlugin> getPluginClass() {
		return pluginClass;
	}

	public void setPluginClass(Class<? extends DWUIPlugin> pluginClass) {
		this.pluginClass = pluginClass;
	}
	
	
}
