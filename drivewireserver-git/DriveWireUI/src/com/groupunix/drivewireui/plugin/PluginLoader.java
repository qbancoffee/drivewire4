package com.groupunix.drivewireui.plugin;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;

public class PluginLoader 
{
	private static DWClassLoader dwclassLoader;

	private String pluginPath;
	
	private ArrayList<DWUIPlugin> plugins;
	
	public PluginLoader(String pp)
	{
		this.setPluginPath(pp);
		setPlugins(new ArrayList<DWUIPlugin>());
		loadPlugins();
	}

	private void loadPlugins() 
	{
		for (PluginInfo pi : getPluginInfos())
        		tryToLoad(pi);

	}

	public ArrayList<PluginInfo> getPluginInfos()
	{
		ArrayList<PluginInfo> res = new ArrayList<PluginInfo>();
		
		File pdir = new File(this.pluginPath);
		
	dwclassLoader = new DWClassLoader(new URL[0], this.getClass().getClassLoader());

        File[] files = pdir.listFiles();
        
        if (files != null)
	        for (int i = 0; i < files.length; i++) 
	        {
	        	if (files[i].isFile() && files[i].canRead() && files[i].getName().endsWith(".jar"))
	        	{
	        		res.add(this.getInfo(files[i]));
	        	}
	        }
        
        return res;
	}
		
	public PluginInfo getInfo( File file)
	{
		PluginInfo pi = null;
		
		try 
		{
			
			JarFile jf = new JarFile(file);
		    Manifest mf = jf.getManifest();
			
		    if (mf != null) 
		    {
		        String cp = mf.getMainAttributes().getValue("main-class");
		        
		        if (cp != null) 
		        {
		    		
		        	dwclassLoader.addURL(file.toURI().toURL());

		        	Class<?> clazz = Class.forName(cp, true, dwclassLoader);
			
		        	Class<? extends DWUIPlugin> plugClass = clazz.asSubclass(DWUIPlugin.class);
			
		        	Constructor<? extends DWUIPlugin> ctor = plugClass.getConstructor(new Class[] {});
		        	
		        	DWUIPlugin plugin = ctor.newInstance();
		        	
		        	pi = new PluginInfo(plugin);
		        	
		        }
		        else
		        	System.err.println("No main-class in manifest for plugin: " + file);
		    }
		    
		    jf.close();
			
		} 
		// TODO Better errors
		
		catch (Exception e)
		{
			System.err.println("Could not load plugin from: " + file);
			
			e.printStackTrace();
		}
		
		return pi;
	}
	

	public void tryToLoad(PluginInfo pluginInfo) 
	{
		if (pluginInfo.isPluginEnabled())
		{
			Class<? extends DWUIPlugin> plugClass = pluginInfo.getPluginClass().asSubclass(DWUIPlugin.class);
				
			Constructor<? extends DWUIPlugin> ctor;
			
			try 
			{
				ctor = plugClass.getConstructor(new Class[] {});
			
				DWUIPlugin plugin = ctor.newInstance();
				
				if (plugin.init() == DWUIPlugin.INIT_OK)
		    		this.plugins.add(plugin);
		    	
			} 
			catch (SecurityException e1) 
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} 
			catch (NoSuchMethodException e1) 
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			        	
		}
		        	
    	
	}

	public String getPluginPath() {
		return pluginPath;
	}

	public void setPluginPath(String pluginPath) {
		this.pluginPath = pluginPath;
	}

	public ArrayList<DWUIPlugin> getPlugins() {
		return plugins;
	}

	public void setPlugins(ArrayList<DWUIPlugin> plugins) {
		this.plugins = plugins;
	}

	public void unload(PluginInfo pi) {
		// TODO Auto-generated method stub
		
	}
	
	
	
	
	
}
