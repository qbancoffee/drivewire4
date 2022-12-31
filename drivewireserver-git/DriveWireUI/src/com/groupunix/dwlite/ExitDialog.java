package com.groupunix.dwlite;

import java.awt.BorderLayout;
import java.awt.Toolkit;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

public class ExitDialog extends JDialog {



	private static final long serialVersionUID = 1L;

	public ExitDialog() 
	{
		setUndecorated(true);
		setResizable(false);
		
		setTitle("Exiting...");
		//setIconImage(Toolkit.getDefaultToolkit().getImage(ExitDialog.class.getResource("/status/active_16.png")));
		
		setBounds(100, 100, 278, 126);
		
		JPanel panel = new JPanel();
		getContentPane().add(panel, BorderLayout.CENTER);
		panel.setLayout(new MigLayout("", "[20px:20px][grow,center][20px:20px]", "[grow][][grow]"));
		
		JLabel lblDrivewireIsShutting = new JLabel("DriveWire is shutting down...");
		panel.add(lblDrivewireIsShutting, "cell 1 1");
		
	}

	
	
}
