package com.groupunix.drivewireui;

public class ServerStatusItem
{
	private int interval = 0;
	private long memtotal = 0;
	private long memfree = 0;
	private long ops = 0;
	private long diskops = 0;
	private long vserialops = 0;
	private int instances = 0;
	private int instancesalive = 0;
	private int threads = 0;
	private int uiclients = 0;
	private long magic = 0;
	
	
	public void setInterval(int interval)
	{
		this.interval = interval;
	}
	public int getInterval()
	{
		return interval;
	}
	public void setMemtotal(long memtotal)
	{
		this.memtotal = memtotal;
	}
	public long getMemtotal()
	{
		return memtotal;
	}
	public void setMemfree(long memfree)
	{
		this.memfree = memfree;
	}
	public long getMemfree()
	{
		return memfree;
	}
	public void setOps(long ops)
	{
		this.ops = ops;
	}
	public long getOps()
	{
		return ops;
	}
	public void setDiskops(long diskops)
	{
		this.diskops = diskops;
	}
	public long getDiskops()
	{
		return diskops;
	}
	public void setVserialops(long vserialops)
	{
		this.vserialops = vserialops;
	}
	public long getVserialops()
	{
		return vserialops;
	}
	public void setInstances(int instances)
	{
		this.instances = instances;
	}
	public int getInstances()
	{
		return instances;
	}
	public void setInstancesalive(int instancesalive)
	{
		this.instancesalive = instancesalive;
	}
	public int getInstancesalive()
	{
		return instancesalive;
	}
	public void setThreads(int threads)
	{
		this.threads = threads;
	}
	public int getThreads()
	{
		return threads;
	}

	public String toString()
	{
		String res = "";
		
		res = "Interval: " + this.interval + "  MemTot: " + this.memtotal + "  MemFree: " + this.memfree + "  Ops: " + this.ops + "  DiskOps: " + this.diskops;
		res += "  VSOps: " + this.vserialops + "  Instances: " + this.instances + "  InstAlive: " + this.instancesalive + "  Threads: " + this.threads + "  UIClients: " + this.uiclients;
				
		return res;
	}
	
	public void setUIClients(int uiclients)
	{
		this.uiclients = uiclients;
	}
	public int getUIClients()
	{
		return uiclients;
	}
	public void setMagic(long l)
	{
		this.magic = l;
	}
	
	public long getMagic()
	{
		return this.magic;
	}


}


