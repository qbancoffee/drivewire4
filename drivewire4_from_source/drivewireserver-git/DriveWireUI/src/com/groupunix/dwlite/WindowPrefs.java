package com.groupunix.dwlite;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.miginfocom.swing.MigLayout;

import com.groupunix.drivewireui.MainWin;

public class WindowPrefs extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private JSpinner spinnerPosX;
	private JSpinner spinnerPosY;
	private JSpinner spinnerWidth;
	private JSpinner spinnerHeight;
	private JFrame frmDwlite;
	private DWLite dwlite;
	private JCheckBox chckbxResizable;
	private JCheckBox chckbxAlwaysOnTop;
	private JCheckBox chckbxVerticalScrollbar;
	private JScrollPane sp;
	@SuppressWarnings("rawtypes")
	private JComboBox comboBoxTheme;
	private JCheckBox chckbxUndecorated;
	private JCheckBox chckbxShowFilePath;
	private JCheckBox chckbxShowDriveX;
	
	public WindowPrefs(DWLite Dwlite, JScrollPane sp) 
	{
		this.dwlite = Dwlite;
		this.frmDwlite = Dwlite.getFrame();
		this.sp = sp;
		
		setTitle("DW Lite Window Preferences");
		//setIconImage(Toolkit.getDefaultToolkit().getImage(WindowPrefs.class.getResource("/dw/dw4square.jpg")));
		setBounds(100, 100, 450, 322);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new MigLayout("", "[grow][][grow][][][][grow]", "[10px:10px][][10px:10px][][10px:10px][][][10px:10px][][]"));
		{
			JLabel lblTheme = new JLabel("Theme:");
			contentPanel.add(lblTheme, "cell 1 1,alignx trailing");
		}
		{
			comboBoxTheme = new JComboBox();
			
			contentPanel.add(comboBoxTheme, "cell 2 1 2 1,growx");
		}
		{
			chckbxShowDriveX = new JCheckBox("Show drive X");
			contentPanel.add(chckbxShowDriveX, "cell 1 3");
		}
		{
			chckbxShowFilePath = new JCheckBox("Show disk image paths");
			contentPanel.add(chckbxShowFilePath, "cell 2 3 3 1");
		}
		{
			JLabel lblScreenPositionX = new JLabel("Screen position X:");
			contentPanel.add(lblScreenPositionX, "cell 0 5 2 1,alignx right");
		}
		{
			spinnerPosX = new JSpinner();
			spinnerPosX.setModel(new SpinnerNumberModel(0, 0, 1000, 1));
			contentPanel.add(spinnerPosX, "cell 2 5,alignx left");
		}
		
		{
			JLabel lblWindowWidth = new JLabel("Window width:");
			contentPanel.add(lblWindowWidth, "cell 4 5,alignx right");
		}
		{
			spinnerWidth = new JSpinner();
			spinnerWidth.setModel(new SpinnerNumberModel(0, 0, 1000, 1));
			contentPanel.add(spinnerWidth, "cell 5 5,alignx left");
		}
		{
			JLabel lblScreenPositionY = new JLabel("Screen position Y:");
			contentPanel.add(lblScreenPositionY, "cell 0 6 2 1,alignx right");
		}
		{
			spinnerPosY = new JSpinner();
			spinnerPosY.setModel(new SpinnerNumberModel(0, 0, 1000, 1));
			contentPanel.add(spinnerPosY, "cell 2 6,alignx left");
		}
		{
			JLabel lblNewLabel = new JLabel("Window height:");
			contentPanel.add(lblNewLabel, "cell 4 6,alignx right");
		}
		{
			spinnerHeight = new JSpinner();
			
			spinnerHeight.setModel(new SpinnerNumberModel(0, 0, 1000, 1));
			contentPanel.add(spinnerHeight, "cell 5 6,alignx left");
		}
		{
			chckbxResizable = new JCheckBox("Resizable");
			contentPanel.add(chckbxResizable, "cell 1 8");
		}
		{
			chckbxVerticalScrollbar = new JCheckBox("Vertical scrollbar");
			contentPanel.add(chckbxVerticalScrollbar, "cell 2 8 2 1");
		}
		
		
		
		{
			chckbxAlwaysOnTop = new JCheckBox("Always on top");
			contentPanel.add(chckbxAlwaysOnTop, "cell 4 8");
		}
		{
			chckbxUndecorated = new JCheckBox("Hide window decorations (requires restart to take effect)");
			contentPanel.add(chckbxUndecorated, "cell 1 9 5 1");
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("Save as default");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) 
					{
						saveSettings();
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Close");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) 
					{
						dispose();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
		
		
		
		loadCurrentSettings();
		
		
		spinnerPosX.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) 
			{
				applySettings();
			}
		});
		
		spinnerHeight.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) 
			{
				applySettings();
			}
		});
		
		spinnerPosY.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) 
			{
				applySettings();
			}
		});
		
		spinnerWidth.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) 
			{
				applySettings();
			}
		});
		
		chckbxResizable.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				applySettings();
			}
		});
		
		chckbxAlwaysOnTop.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				applySettings();
			}
		});
		
		chckbxVerticalScrollbar.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				applySettings();
			}
		});
		
		comboBoxTheme.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				applySettings();
			}
		});
		
		chckbxShowDriveX.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				applySettings();
			}
		});
		
		chckbxShowFilePath.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				applySettings();
			}
		});
		
	}

	
	
	protected void saveSettings() 
	{
		if (MainWin.config != null)
		{
			MainWin.config.setProperty("DWLiteX", (Integer)this.spinnerPosX.getValue());
			MainWin.config.setProperty("DWLiteY", (Integer)this.spinnerPosY.getValue());
			MainWin.config.setProperty("DWLiteH", (Integer)this.spinnerHeight.getValue());
			MainWin.config.setProperty("DWLiteW", (Integer)this.spinnerWidth.getValue());
			
			MainWin.config.setProperty("DWLiteVScroll", this.chckbxVerticalScrollbar.isSelected());
			MainWin.config.setProperty("DWLiteResize", this.chckbxResizable.isSelected());
			MainWin.config.setProperty("DWLiteOnTop", this.chckbxAlwaysOnTop.isSelected());
			MainWin.config.setProperty("DWLiteUndecorated", this.chckbxUndecorated.isSelected());
			MainWin.config.setProperty("DWLiteTheme", this.comboBoxTheme.getSelectedItem().toString());	
			
			MainWin.config.setProperty("DWLiteShowDriveX", this.chckbxShowDriveX.isSelected());
			MainWin.config.setProperty("DWLiteShowPaths", this.chckbxShowFilePath.isSelected());
			
		}
		
		dispose();
	}



	protected void applySettings() 
	{
		frmDwlite.setBounds((Integer)this.spinnerPosX.getValue(), (Integer)this.spinnerPosY.getValue(), (Integer)this.spinnerWidth.getValue(), (Integer)this.spinnerHeight.getValue());
		
		frmDwlite.setAlwaysOnTop(this.chckbxAlwaysOnTop.isSelected());
		frmDwlite.setResizable(this.chckbxResizable.isSelected());
		
		if (this.chckbxVerticalScrollbar.isSelected())
			sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		else
			sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		
		
		try 
		{
		    for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) 
		    {
		       if (this.comboBoxTheme.getSelectedItem().toString().equals(info.getName())) {
		            UIManager.setLookAndFeel(info.getClassName());
		            SwingUtilities.updateComponentTreeUI( this );
		            SwingUtilities.updateComponentTreeUI( frmDwlite );
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
		
		dwlite.lblDisk0Path.setBorder(null);
		dwlite.lblDisk0Path.setBackground(UIManager.getColor("Panel.background"));
		dwlite.lblDisk1Path.setBorder(null);
		dwlite.lblDisk1Path.setBackground(UIManager.getColor("Panel.background"));
		dwlite.lblDisk2Path.setBorder(null);
		dwlite.lblDisk2Path.setBackground(UIManager.getColor("Panel.background"));
		dwlite.lblDisk3Path.setBorder(null);
		dwlite.lblDisk3Path.setBackground(UIManager.getColor("Panel.background"));
		dwlite.lblDiskXPath.setBorder(null);
		dwlite.lblDiskXPath.setBackground(UIManager.getColor("Panel.background"));
		
		dwlite.setDriveXVisible(this.chckbxShowDriveX.isSelected());
		dwlite.showPaths = this.chckbxShowFilePath.isSelected();
	}



	private void loadCurrentSettings() 
	{
		this.spinnerPosX.setValue(frmDwlite.getBounds().x);
		this.spinnerPosY.setValue(frmDwlite.getBounds().y);
		this.spinnerWidth.setValue(frmDwlite.getBounds().width);
		this.spinnerHeight.setValue(frmDwlite.getBounds().height);
		
		if (frmDwlite.isAlwaysOnTop())
			this.chckbxAlwaysOnTop.setSelected(true);
		else
			this.chckbxAlwaysOnTop.setSelected(false);
		
		if (frmDwlite.isResizable())
			this.chckbxResizable.setSelected(true);
		else
			this.chckbxResizable.setSelected(false);
		
		if (sp.getVerticalScrollBarPolicy() == JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED)
			this.chckbxVerticalScrollbar.setSelected(true);
		else
			this.chckbxVerticalScrollbar.setSelected(false);
		
		if (frmDwlite.isUndecorated())
			this.chckbxUndecorated.setSelected(true);
		else
			this.chckbxUndecorated.setSelected(false);
		
		this.comboBoxTheme.removeAll();
		
		for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) 
		{
		     this.comboBoxTheme.addItem(info.getName());
		     if ((info != null) && (info.getName() != null) && (MainWin.config != null) && (info.getName().equals(MainWin.config.getString("DWLiteTheme", "Windows"))))
		    	 this.comboBoxTheme.setSelectedItem(info.getName());
		}
		
		this.chckbxShowDriveX.setSelected(dwlite.showDriveX);
		this.chckbxShowFilePath.setSelected(dwlite.showPaths);
	}

	protected JSpinner getSpinnerPosX() {
		return spinnerPosX;
	}
	protected JSpinner getSpinnerPosY() {
		return spinnerPosY;
	}
	protected JSpinner getSpinnerWidth() {
		return spinnerWidth;
	}
	protected JSpinner getSpinnerHeight() {
		return spinnerHeight;
	}
	
	
	protected JCheckBox getChckbxUndecorated() {
		return chckbxUndecorated;
	}
	protected JCheckBox getChckbxShowDriveX() {
		return chckbxShowDriveX;
	}
	protected JCheckBox getChckbxShowFilePath() {
		return chckbxShowFilePath;
	}
}
