package com.groupunix.dwlite;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import net.miginfocom.swing.MigLayout;

import com.groupunix.drivewireserver.DriveWireServer;
import com.groupunix.drivewireserver.dwcommands.DWCmd;
import com.groupunix.drivewireserver.dwcommands.DWCommandList;
import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocolHandler;
import com.groupunix.drivewireui.MainWin;

public class DWLite 
{

	
	private JFrame frmDwlite;

	protected static long servermagic;
	private int handlerno = 0;
	private JCheckBoxMenuItem chckbxmntmHdbdosTranslation;
	private JLabel lblDisk0;
	private JLabel lblDisk1;
	private JLabel lblDisk2;
	private JLabel lblDisk3;
	private JLabel lblDiskX;
	private final JSpinner spinnerDriveX = new JSpinner();

	private static String lastDir = ".";
	private JPanel panelDrives;

	protected boolean showDriveX = false;
	protected boolean showPaths = true;
	private JButton btnEjectX;
	private JButton button_X;

	private JScrollPane sp;

	private int[] lastreads = new int[5];
	private JButton btnDrive0;

	private int[] lastwrites = new int[5];
	private JButton btnDrive1;
	private JButton btnDrive2;
	private JButton btnDrive3;
	private JMenuItem mntmLockWindow;
	public JTextField lblDisk1Path;
	public JTextField lblDisk0Path;
	public JTextField lblDisk2Path;
	public JTextField lblDisk3Path;
	public JTextField lblDiskXPath;

	protected static DWLite window;



	/**
	 * Launch the application.
	 */
	public static void main(final String[] args) 
	{
		EventQueue.invokeLater(new Runnable() 
		{
			public void run() 
			{
				String theme = "Windows";
				
				if (MainWin.config != null)
				{
					theme = MainWin.config.getString("DWLiteTheme", "Windows");
					lastDir = MainWin.config.getString("DWLiteLastDir", ".");
				}
				
				try {
					
					
					try 
					{
					    for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) 
					    {
					       if (theme.equals(info.getName())) {
					            UIManager.setLookAndFeel(info.getClassName());
					            break;
					       }

					    }
					} 
					catch (Exception e) 
					{
					  
					    try 
					    {
					        UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
					    } 
					    catch (Exception ex) 
					    {
					        // whatever
					    }
					}
					
					window = new DWLite();
					
					Thread diskUpdate = new Thread(new DiskViewUpdater(window));
					diskUpdate.setDaemon(true);
					diskUpdate.start();
					
					
					window.frmDwlite.setVisible(true);
				} 
				catch (Exception e) 
				{
					e.printStackTrace();
				}
			}
		});
	}
	
	
	
	


	public DWLite() 
	{
		
		initialize();
	}

	
	private void initialize() 
	{
		frmDwlite = new JFrame();
		
		if (MainWin.config != null)
		{
			frmDwlite.setUndecorated(MainWin.config.getBoolean("DWLiteUndecorated", false));
			showDriveX = (MainWin.config.getBoolean("DWLiteShowDriveX", false));
			showPaths = (MainWin.config.getBoolean("DWLiteShowPaths", true));
		}
		
		frmDwlite.setIconImage(Toolkit.getDefaultToolkit().getImage(DWLite.class.getResource("/dw/dw4square.jpg")));
		
		frmDwlite.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		frmDwlite.addWindowListener( new WindowAdapter()
		{
		    public void windowClosing(WindowEvent e)
		    {
		    	ExitDialog ed = new ExitDialog();
		    	ed.setBounds(frmDwlite.getBounds());
		    	ed.setVisible(true);
		    	ed.update(ed.getGraphics());
		    	
		    	MainWin.stopDWServer();
					    	
		    }
		});
		
		
		
		frmDwlite.setTitle("DW4Lite");
		frmDwlite.setBounds(100, 100, 372, 385);
		
		frmDwlite.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmDwlite.getContentPane().setLayout(new MigLayout("", "[366px,grow,fill]", "[grow]"));
		
		
		sp = new JScrollPane();
		
		sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		
		frmDwlite.getContentPane().add(sp, "cell 0 0,grow");
	
		panelDrives = new JPanel();
		sp.setViewportView(panelDrives);
		
		panelDrives.setLayout(new MigLayout("", "[][grow,fill][]", "[20px:20px][20px:20px][20px:20px][20px:20px][20px:20px][20px:20px][20px:20px][20px:20px][20px:20px][20px:20px][]"));
		
		
		
		btnDrive0 = new JButton("");
		btnDrive0.setMinimumSize(new Dimension(24, 9));
		btnDrive0.setMaximumSize(new Dimension(35, 35));
		btnDrive0.setToolTipText("Load Drive 0");
		btnDrive0.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				  doDiskInsert(0);
			}
		});
		panelDrives.add(btnDrive0, "cell 0 0 1 2");
		btnDrive0.setIcon(new ImageIcon(DWLite.class.getResource("/lite/disk0.png")));
		
		lblDisk0 = new JLabel("Not loaded");
		lblDisk0.setFont(new Font("Tahoma", Font.BOLD, 11));
		panelDrives.add(lblDisk0, "cell 1 0,alignx left,aligny bottom");
		
		JButton button = new JButton("");
		button.setMaximumSize(new Dimension(35, 35));
		button.setToolTipText("Eject Drive 0");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) 
			{
				doCommand("dw disk eject 0");
			}
		});
		panelDrives.add(button, "cell 2 0 1 2,alignx right,aligny center");
		button.setIcon(new ImageIcon(DWLite.class.getResource("/lite/eject.png")));
		
		lblDisk0Path = new JTextField();
		lblDisk0Path.setFont(new Font("Tahoma", Font.PLAIN, 9));
		lblDisk0Path.setEditable(false);
		lblDisk0Path.setBorder(null);
		
		panelDrives.add(lblDisk0Path, "cell 1 1,alignx left");
		lblDisk0Path.setColumns(10);
		
		btnDrive1 = new JButton("");
		btnDrive1.setMaximumSize(new Dimension(35, 35));
		btnDrive1.setToolTipText("Load Drive 1");
		panelDrives.add(btnDrive1, "cell 0 2 1 2");
		btnDrive1.setIcon(new ImageIcon(DWLite.class.getResource("/lite/disk1.png")));
		btnDrive1.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				  doDiskInsert(1);
			}
		});
		
		
		lblDisk1 = new JLabel("Not loaded");
		
		lblDisk1.setFont(new Font("Tahoma", Font.BOLD, 11));
		panelDrives.add(lblDisk1, "cell 1 2,aligny bottom");
		
		JButton button_1 = new JButton("");
		button_1.setMaximumSize(new Dimension(35, 35));
		button_1.setToolTipText("Eject Drive 1");
		panelDrives.add(button_1, "cell 2 2 1 2,alignx right");
		button_1.setIcon(new ImageIcon(DWLite.class.getResource("/lite/eject.png")));
		button_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) 
			{
				doCommand("dw disk eject 1");
			}
		});
		
		lblDisk1Path = new JTextField();
		lblDisk1Path.setFont(new Font("Tahoma", Font.PLAIN, 9));
		lblDisk1Path.setBorder(null);
		lblDisk1Path.setEditable(false);
		panelDrives.add(lblDisk1Path, "cell 1 3,growx");
		lblDisk1Path.setColumns(10);
		
		btnDrive2 = new JButton("");
		btnDrive2.setMaximumSize(new Dimension(35, 35));
		btnDrive2.setToolTipText("Load Drive 2");
		panelDrives.add(btnDrive2, "cell 0 4 1 2");
		btnDrive2.setIcon(new ImageIcon(DWLite.class.getResource("/lite/disk2.png")));
		btnDrive2.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				  doDiskInsert(2);
			}
		});
		
		lblDisk2 = new JLabel("Not loaded");
		lblDisk2.setFont(new Font("Tahoma", Font.BOLD, 11));
		panelDrives.add(lblDisk2, "cell 1 4,aligny bottom");
		
		JButton button_2 = new JButton("");
		button_2.setMaximumSize(new Dimension(35, 35));
		button_2.setToolTipText("Eject Drive 2");
		panelDrives.add(button_2, "cell 2 4 1 2");
		button_2.setIcon(new ImageIcon(DWLite.class.getResource("/lite/eject.png")));
		button_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) 
			{
				doCommand("dw disk eject 2");
			}
		});
		
		lblDisk2Path = new JTextField();
		lblDisk2Path.setFont(new Font("Tahoma", Font.PLAIN, 9));
		lblDisk2Path.setBorder(null);
		lblDisk2Path.setEditable(false);
		panelDrives.add(lblDisk2Path, "cell 1 5,growx");
		lblDisk2Path.setColumns(10);
		
		btnDrive3 = new JButton("");
		btnDrive3.setMaximumSize(new Dimension(35, 35));
		btnDrive3.setToolTipText("Load Drive 3");
		panelDrives.add(btnDrive3, "cell 0 6 1 2");
		btnDrive3.setIcon(new ImageIcon(DWLite.class.getResource("/lite/disk3.png")));
		btnDrive3.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				  doDiskInsert(3);
			}
		});
		
		lblDisk3 = new JLabel("Not loaded");
		lblDisk3.setFont(new Font("Tahoma", Font.BOLD, 11));
		panelDrives.add(lblDisk3, "cell 1 6,aligny bottom");
		
		JButton button_3 = new JButton("");
		button_3.setMaximumSize(new Dimension(35, 35));
		button_3.setToolTipText("Eject Drive 3");
		panelDrives.add(button_3, "cell 2 6 1 2");
		button_3.setIcon(new ImageIcon(DWLite.class.getResource("/lite/eject.png")));
		
		lblDisk3Path = new JTextField();
		lblDisk3Path.setFont(new Font("Tahoma", Font.PLAIN, 9));
		lblDisk3Path.setEditable(false);
		lblDisk3Path.setBorder(null);
		panelDrives.add(lblDisk3Path, "cell 1 7,growx");
		lblDisk3Path.setColumns(10);
		
	
		
		lblDiskX = new JLabel("Not loaded");
		lblDiskX.setFont(new Font("Tahoma", Font.BOLD, 11));
		panelDrives.add(lblDiskX, "cell 1 8,aligny bottom");
		spinnerDriveX.setMaximumSize(new Dimension(40, 20));
		spinnerDriveX.setToolTipText("Choose which drive 4 through 255 to display in Drive X slot");
		
		
		spinnerDriveX.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) 
			{
				updateDriveDisplay();
			}
		});
		
		lblDiskXPath = new JTextField();
		lblDiskXPath.setFont(new Font("Tahoma", Font.PLAIN, 9));
		lblDiskXPath.setBorder(null);
		lblDiskXPath.setEditable(false);
		panelDrives.add(lblDiskXPath, "cell 1 9,growx");
		lblDiskXPath.setColumns(10);
		spinnerDriveX.setModel(new SpinnerNumberModel(4, 4, 255, 1));
		panelDrives.add(spinnerDriveX, "cell 0 10,growx");
		
		button_X = new JButton("");
		button_X.setMaximumSize(new Dimension(35, 35));
		button_X.setToolTipText(" Load Drive X (choose number below)");
		button_X.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) 
			{
				doDiskInsert((Integer) spinnerDriveX.getValue());
			}
		});
		button_X.setIcon(new ImageIcon(DWLite.class.getResource("/lite/diskX.png")));
		panelDrives.add(button_X, "cell 0 8 1 2");
		
		btnEjectX = new JButton("");
		btnEjectX.setMaximumSize(new Dimension(35, 35));
		btnEjectX.setToolTipText("Eject Drive X");
		btnEjectX.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				doCommand("dw disk eject " + (Integer)spinnerDriveX.getValue());
			}
		});
		btnEjectX.setIcon(new ImageIcon(DWLite.class.getResource("/lite/eject.png")));
		panelDrives.add(btnEjectX, "cell 2 8 1 2");
		
		
		
		button_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) 
			{
				doCommand("dw disk eject 3");
			}
		});
		
		JMenuBar menuBar = new JMenuBar();
		frmDwlite.setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		JMenuItem mntmExit = new JMenuItem("Exit");
		mntmExit.setIcon(new ImageIcon(DWLite.class.getResource("/menu/application-exit-5.png")));
		mntmExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) 
			{
				WindowEvent windowClosing = new WindowEvent(frmDwlite, WindowEvent.WINDOW_CLOSING);
				frmDwlite.dispatchEvent(windowClosing);
			
				
			}
		});
		mnFile.add(mntmExit);
		
		JMenu mnOptions = new JMenu("Options");
		mnOptions.addMenuListener(new MenuListener() {
			public void menuCanceled(MenuEvent arg0) {
			}
			public void menuDeselected(MenuEvent arg0) {
			}
			public void menuSelected(MenuEvent arg0) 
			{
				if ((DriveWireServer.getHandler(handlerno) != null) && (DriveWireServer.getHandler(handlerno).getConfig() != null))
				{
					chckbxmntmHdbdosTranslation.setEnabled(true);
					chckbxmntmHdbdosTranslation.setSelected(DriveWireServer.getHandler(handlerno).getConfig().getBoolean("HDBDOSMode", false));
				}
				else
					chckbxmntmHdbdosTranslation.setEnabled(false);
				
			}
		});
		menuBar.add(mnOptions);
		
		chckbxmntmHdbdosTranslation = new JCheckBoxMenuItem("HDBDOS Translation");
		
		chckbxmntmHdbdosTranslation.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (DriveWireServer.getHandler(handlerno).getConfig().getBoolean("HDBDOSMode", false))
					doCommand("dw config set HDBDOSMode false");
				else
					doCommand("dw config set HDBDOSMode true");
				
			}
		});
		mnOptions.add(chckbxmntmHdbdosTranslation);
		
		mntmLockWindow = new JMenuItem("Window Preferences..");
		mntmLockWindow.setIcon(new ImageIcon(DWLite.class.getResource("/menu/cog-edit.png")));
		mntmLockWindow.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) 
			{
				WindowPrefs wp = new WindowPrefs(window, sp);
				wp.setVisible(true);
			}
		});
		
	
		//mnOptions.add(mntmNewMenuItem);
		mnOptions.add(mntmLockWindow);
		
		
		this.btnEjectX.setVisible(false);
		this.lblDiskX.setVisible(false);
		this.spinnerDriveX.setVisible(false);
		this.button_X.setVisible(false);
		
		frmDwlite.pack();
		
		if (MainWin.config != null)
		{
			frmDwlite.setBounds(MainWin.config.getInt("DWLiteX", 100), MainWin.config.getInt("DWLiteY", 100), MainWin.config.getInt("DWLiteW", 382), MainWin.config.getInt("DWLiteH", 403));
			
			frmDwlite.setAlwaysOnTop(MainWin.config.getBoolean("DWLiteOnTop", false));
			frmDwlite.setResizable(MainWin.config.getBoolean("DWLiteResize", false));
			
			if (MainWin.config.getBoolean("DWLiteVScroll", true))
				sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			else
				sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
			
			
		}
		
	}
	


	














	protected void doDiskInsert(int diskno) 
	{
		JFileChooser c = new JFileChooser(lastDir);
	     
	      int rVal = c.showOpenDialog(frmDwlite);
	      
	      if (rVal == JFileChooser.APPROVE_OPTION) 
	      {
	    	  lastDir  = c.getCurrentDirectory().toString();
	    	  
	    	  if (MainWin.config != null)
	    		  MainWin.config.setProperty("DWLiteLastDir", lastDir);
	    	  
	    	  doCommand("dw disk insert " + diskno + " " + c.getSelectedFile().getAbsolutePath());
	      }
	}





	protected void doCommand(String cmd) 
	{
		DWCommandList commands = new DWCommandList(DriveWireServer.getHandler(0));
		commands.addcommand(new DWCmd(DriveWireServer.getHandler(0)));	
		
		commands.parse(cmd);
		
		updateDriveDisplay();
	}




	public void updateDriveDisplay() 
	{
		DWProtocolHandler dw = (DWProtocolHandler) DriveWireServer.getHandler(handlerno);
		
		if ((dw != null) && (frmDwlite != null))
		{
			try 
			{
				if (dw.getDiskDrives().isLoaded(0) && (frmDwlite != null))
				{
					
					this.lblDisk0.setText(prettyFile(dw.getDiskDrives().getDisk(0).getFilePath()));
					
					if (showPaths)
					{
						this.lblDisk0Path.setText(prettyPath(dw.getDiskDrives().getDisk(0).getFilePath()));
						this.lblDisk0Path.setVisible(true);
					}
					else
					{
						this.lblDisk0Path.setText("");
						this.lblDisk0Path.setVisible(false);
					}
					
					int r = dw.getDiskDrives().getDisk(0).getParams().getInt("_reads", 0);
					int w = dw.getDiskDrives().getDisk(0).getParams().getInt("_writes", 0);
					
					if (w != lastwrites[0])
					{
						btnDrive0.setIcon(new ImageIcon(DWLite.class.getResource("/lite/disk0-w.png")));
					}
					else if (r != lastreads[0])
					{
						btnDrive0.setIcon(new ImageIcon(DWLite.class.getResource("/lite/disk0-r.png")));
					}
					else
						btnDrive0.setIcon(new ImageIcon(DWLite.class.getResource("/lite/disk0.png")));
					
					lastreads[0] = r;
					lastwrites[0] = w;
					
				}
				else
				{
					this.lblDisk0.setText("Not loaded");
					this.lblDisk0Path.setText("");
				}
				
				if (dw.getDiskDrives().isLoaded(1) && (frmDwlite != null))
				{
					this.lblDisk1.setText(prettyFile(dw.getDiskDrives().getDisk(1).getFilePath()));
					
					if (showPaths)
					{
						this.lblDisk1Path.setText(prettyPath(dw.getDiskDrives().getDisk(1).getFilePath()));
						this.lblDisk1Path.setVisible(true);
					}
					else
					{
						this.lblDisk1Path.setText("");
						this.lblDisk1Path.setVisible(false);
					}
					
					int r = dw.getDiskDrives().getDisk(1).getParams().getInt("_reads", 0);
					int w = dw.getDiskDrives().getDisk(1).getParams().getInt("_writes", 0);
					
					if (w != lastwrites[1])
					{
						btnDrive1.setIcon(new ImageIcon(DWLite.class.getResource("/lite/disk1-w.png")));
					}
					else if (r != lastreads[1])
					{
						btnDrive1.setIcon(new ImageIcon(DWLite.class.getResource("/lite/disk1-r.png")));
					}
					else
						btnDrive1.setIcon(new ImageIcon(DWLite.class.getResource("/lite/disk1.png")));
					
					lastreads[1] = r;
					lastwrites[1] = w;
					
					
				}
				else
				{
					this.lblDisk1.setText("Not loaded");
					this.lblDisk1Path.setText("");
				}
				
				if (dw.getDiskDrives().isLoaded(2) && (frmDwlite != null))
				{
					this.lblDisk2.setText(prettyFile(dw.getDiskDrives().getDisk(2).getFilePath()));
					
					if (showPaths)
					{
						this.lblDisk2Path.setText(prettyPath(dw.getDiskDrives().getDisk(2).getFilePath()));
						this.lblDisk2Path.setVisible(true);
					}
					else
					{
						this.lblDisk2Path.setText("");
						this.lblDisk2Path.setVisible(false);
					}
					
					int r = dw.getDiskDrives().getDisk(2).getParams().getInt("_reads", 0);
					int w = dw.getDiskDrives().getDisk(2).getParams().getInt("_writes", 0);
					
					if (w != lastwrites[2])
					{
						btnDrive2.setIcon(new ImageIcon(DWLite.class.getResource("/lite/disk2-w.png")));
					}
					else if (r != lastreads[2])
					{
						btnDrive2.setIcon(new ImageIcon(DWLite.class.getResource("/lite/disk2-r.png")));
					}
					else
						btnDrive2.setIcon(new ImageIcon(DWLite.class.getResource("/lite/disk2.png")));
					
					lastreads[2] = r;
					lastwrites[2] = w;
					
				}
				else
				{
					this.lblDisk2.setText("Not loaded");
					this.lblDisk2Path.setText("");
				}
				
				if (dw.getDiskDrives().isLoaded(3) && (frmDwlite != null))
				{
					this.lblDisk3.setText(prettyFile(dw.getDiskDrives().getDisk(3).getFilePath()));
					
					if (showPaths)
					{
						this.lblDisk3Path.setText(prettyPath(dw.getDiskDrives().getDisk(3).getFilePath()));
						this.lblDisk3Path.setVisible(true);
					}
					else
					{
						this.lblDisk3Path.setText("");
						this.lblDisk3Path.setVisible(false);
					}
					
					int r = dw.getDiskDrives().getDisk(3).getParams().getInt("_reads", 0);
					int w = dw.getDiskDrives().getDisk(3).getParams().getInt("_writes", 0);
					
					if (w != lastwrites[3])
					{
						btnDrive3.setIcon(new ImageIcon(DWLite.class.getResource("/lite/disk3-w.png")));
					}
					else if (r != lastreads[3])
					{
						btnDrive3.setIcon(new ImageIcon(DWLite.class.getResource("/lite/disk3-r.png")));
					}
					else
						btnDrive3.setIcon(new ImageIcon(DWLite.class.getResource("/lite/disk3.png")));
					
					lastreads[3] = r;
					lastwrites[3] = w;
					
				}
				else
				{
					this.lblDisk3.setText("Not loaded");
					this.lblDisk3Path.setText("");
				}
				
				
				setDriveXVisible(showDriveX);
				
				if (showDriveX  && (frmDwlite != null))
				{
					if (dw.getDiskDrives().isLoaded((Integer)spinnerDriveX.getValue()))
					{
						int dn = (Integer)spinnerDriveX.getValue();
						this.lblDiskX.setText(prettyFile(dw.getDiskDrives().getDisk(dn).getFilePath()));
						
						if (showPaths)
						{
							this.lblDiskXPath.setText(prettyPath(dw.getDiskDrives().getDisk(dn).getFilePath()));
							this.lblDiskXPath.setVisible(true);
						}
						else
						{
							this.lblDiskXPath.setText("");
							this.lblDiskXPath.setVisible(false);
						}
						
						int r = dw.getDiskDrives().getDisk(dn).getParams().getInt("_reads", 0);
						int w = dw.getDiskDrives().getDisk(dn).getParams().getInt("_writes", 0);
						
						if (w != lastwrites[4])
						{
							
							button_X.setIcon(new ImageIcon(DWLite.class.getResource("/lite/diskX-w.png")));
						}
						else if (r != lastreads[4])
						{
							button_X.setIcon(new ImageIcon(DWLite.class.getResource("/lite/diskX-r.png")));
						}
						else
							button_X.setIcon(new ImageIcon(DWLite.class.getResource("/lite/diskX.png")));
						
						lastreads[4] = r;
						lastwrites[4] = w;
						
					}
					else
					{
						this.lblDiskX.setText("Not loaded");
						this.lblDiskXPath.setText("");
					}
					
				}
			
			} 
			catch (Exception e) 
			{
				// whatever
			}
		}
			
	}


	public void setDriveXVisible(boolean vis) 
	{
		this.btnEjectX.setVisible(vis);
		this.lblDiskX.setVisible(vis);
		this.lblDiskXPath.setVisible(vis);
		this.spinnerDriveX.setVisible(vis);
		this.button_X.setVisible(vis);
		
		this.showDriveX = vis;
	}















	private String prettyPath(String path) 
	{
		String res = path;
		
		if (path.startsWith("file:///"))
			res = path.substring(8);
		
		if (res.indexOf("/") > -1)
			res = res.substring(0, res.lastIndexOf("/"));
		
		
		
		return res;
	}

	private String prettyFile(String path) 
	{
		String res = path;
		
		res = res.substring(res.lastIndexOf("/")+1);
		
		
		return res;
	}












	protected JButton getBtnEjectX() {
		return btnEjectX;
	}
	protected JSpinner getSpinnerDriveX() {
		return spinnerDriveX;
	}
	protected JButton getButton_4() {
		return button_X;
	}
	protected JButton getBtnDrive0() {
		return btnDrive0;
	}
	protected JButton getBtnDrive1() {
		return btnDrive1;
	}
	protected JButton getBtnDrive2() {
		return btnDrive2;
	}
	protected JButton getBtnDrive3() {
		return btnDrive3;
	}






	public JFrame getFrame() 
	{
		return this.frmDwlite;
	}

}
