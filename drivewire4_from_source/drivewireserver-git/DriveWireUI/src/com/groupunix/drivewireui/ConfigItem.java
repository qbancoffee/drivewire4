package com.groupunix.drivewireui;

import org.apache.commons.configuration.HierarchicalConfiguration.Node;


public class ConfigItem implements Comparable<ConfigItem>
{
	Node node;
	private int index;
	
	public ConfigItem(Node node, int index)
	{
		this.index = index;
		this.node = node;
	}
	
	public Node getNode()
	{
		return this.node;
	}
	
	public int getIndex()
	{
		return this.index;
	}
	
	@Override
	public int compareTo(ConfigItem o)
	{
		if (o.getNode().hasChildren() && !this.getNode().hasChildren())
			return(-1);
		
		if (!o.getNode().hasChildren() && this.getNode().hasChildren())
			return(1);
		
		return(-1 * o.getNode().getName().compareTo(this.getNode().getName()));

	}
	
}
