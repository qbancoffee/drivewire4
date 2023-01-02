package com.groupunix.drivewireui.cococloud;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

public class CloudAuth extends Authenticator
{
	protected PasswordAuthentication getPasswordAuthentication() 
	{
	    return new PasswordAuthentication ("nobody", "nobody".toCharArray());
	}
	
}
