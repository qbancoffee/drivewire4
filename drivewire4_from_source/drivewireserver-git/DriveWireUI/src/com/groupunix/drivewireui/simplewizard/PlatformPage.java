package com.groupunix.drivewireui.simplewizard;

import java.util.ArrayList;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wb.swt.ResourceManager;
import org.eclipse.wb.swt.SWTResourceManager;

import com.groupunix.drivewireui.PlatformDef;

public class PlatformPage extends WizardPage
{
	private Label lblWhatWillIt;
	private Button buttonApple;
	private Button buttonAtari;
	private Button btnCoco1;
	private Button btnCoco2;
	private Button buttonCoco3;
	private Button buttonCoco3FPGA;
	private Button buttonEmulator;

	private ArrayList<Button> buttons = new ArrayList<Button>();

	
	
	
	/**
	 * Create the wizard.
	 */
	public PlatformPage()
	{
		super("wizardPage");
		setTitle("Select Client Device Type");
		setDescription("Please choose the type of device you have.");
		setImageDescriptor(ResourceManager.getImageDescriptor(PlatformPage.class, "/wizard/dwlogo_pad64.png"));
		
	}

	/**
	 * Create contents of the wizard.
	 * @param parent
	 */
	public void createControl(Composite parent)
	{
		Composite container = new Composite(parent, SWT.BORDER);
		container.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		container.setBackgroundMode(SWT.INHERIT_FORCE);
		
		
		setControl(container);
		GridLayout gl_container = new GridLayout(5, false);
		gl_container.marginTop = 10;
		gl_container.marginLeft = 10;
		gl_container.horizontalSpacing = 25;
		gl_container.marginBottom = 5;
		container.setLayout(gl_container);
		
		Label labelLeftspace = new Label(container, SWT.NONE);
		labelLeftspace.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, true, false, 1, 1));
		
		btnCoco1 = new Button(container, SWT.TOGGLE);
		btnCoco1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (e.widget.getData("def") != null)
				{
					if (((Button) e.widget).getSelection())
					{
						((SimpleWizard) getWizard()).setPlatform((PlatformDef) e.widget.getData("def"));
						
					}
					else
					{
						((SimpleWizard) getWizard()).setPlatform(null);
					}
					
					setButtons();
				}
			}
		});
		this.buttons.add(btnCoco1);
		btnCoco1.setData("def", new PlatformDef("CoCo 1", "/wizard/coco1.png", 38400, "none", "1", false, false, false, false));
		
		btnCoco1.setLayoutData(new GridData(SWT.CENTER, SWT.FILL, false, false, 1, 1));
		btnCoco1.setImage(SWTResourceManager.getImage(PlatformPage.class, "/wizard/coco1.png"));
		
		btnCoco2 = new Button(container, SWT.TOGGLE);
		this.buttons.add(btnCoco2);
		btnCoco2.setData("def", new PlatformDef("CoCo 2", "/wizard/coco2.png", 57600, "none", "1", false, false, false, false));
		
		btnCoco2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (e.widget.getData("def") != null)
				{
					if (((Button) e.widget).getSelection())
					{
						((SimpleWizard) getWizard()).setPlatform((PlatformDef) e.widget.getData("def"));
						
					}
					else
					{
						((SimpleWizard) getWizard()).setPlatform(null);
					}
					
					setButtons();
				}
			}
		});
		
		btnCoco2.setLayoutData(new GridData(SWT.CENTER, SWT.FILL, false, false, 1, 1));
		btnCoco2.setImage(SWTResourceManager.getImage(PlatformPage.class, "/wizard/coco2.png"));
		
		buttonCoco3 = new Button(container, SWT.TOGGLE);
		this.buttons.add(buttonCoco3);
		buttonCoco3.setData("def", new PlatformDef("CoCo 3", "/wizard/coco3.png", 115200, "none", "1", false, false, false, false));
		
		buttonCoco3.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (e.widget.getData("def") != null)
				{
					if (((Button) e.widget).getSelection())
					{
						((SimpleWizard) getWizard()).setPlatform((PlatformDef) e.widget.getData("def"));
						
					}
					else
					{
						((SimpleWizard) getWizard()).setPlatform(null);
					}
					
					setButtons();
				}
			}
		});
		
		buttonCoco3.setLayoutData(new GridData(SWT.CENTER, SWT.FILL, false, false, 1, 1));
		buttonCoco3.setImage(SWTResourceManager.getImage(PlatformPage.class, "/wizard/coco3.png"));
		
		Label labelRightspace = new Label(container, SWT.NONE);
		labelRightspace.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, true, false, 1, 1));
		new Label(container, SWT.NONE);
		
		Label lblCoco = new Label(container, SWT.NONE);
		GridData gd_lblCoco = new GridData(SWT.CENTER, SWT.TOP, false, false, 1, 1);
		gd_lblCoco.heightHint = 30;
		gd_lblCoco.minimumHeight = 30;
		lblCoco.setLayoutData(gd_lblCoco);
		lblCoco.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblCoco.setAlignment(SWT.CENTER);
		lblCoco.setText("CoCo 1");
		
		Label lblCoco_1 = new Label(container, SWT.NONE);
		lblCoco_1.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, false, false, 1, 1));
		lblCoco_1.setText("CoCo 2");
		lblCoco_1.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblCoco_1.setAlignment(SWT.CENTER);
		
		Label lblCoco_2 = new Label(container, SWT.NONE);
		lblCoco_2.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, false, false, 1, 1));
		lblCoco_2.setText("CoCo 3");
		lblCoco_2.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblCoco_2.setAlignment(SWT.CENTER);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		
		buttonAtari = new Button(container, SWT.TOGGLE);
		this.buttons.add(buttonAtari);
		buttonAtari.setData("def", new PlatformDef("Atari", "/wizard/atari.png", 57600, "none", "1", false, false, false, false));
		
		buttonAtari.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (e.widget.getData("def") != null)
				{
					if (((Button) e.widget).getSelection())
					{
						((SimpleWizard) getWizard()).setPlatform((PlatformDef) e.widget.getData("def"));
						
					}
					else
					{
						((SimpleWizard) getWizard()).setPlatform(null);
					}
					
					setButtons();
				}
			}
		});
		
		buttonAtari.setLayoutData(new GridData(SWT.CENTER, SWT.FILL, false, false, 1, 1));
		buttonAtari.setImage(SWTResourceManager.getImage(PlatformPage.class, "/wizard/atari.png"));
		
		Label label = new Label(container, SWT.NONE);
		label.setLayoutData(new GridData(SWT.CENTER, SWT.FILL, false, false, 1, 3));
		label.setImage(SWTResourceManager.getImage(PlatformPage.class, "/wizard/cocoman_hello.png"));
		
		buttonCoco3FPGA = new Button(container, SWT.TOGGLE);
		this.buttons.add(buttonCoco3FPGA);
		buttonCoco3FPGA.setData("def", new PlatformDef("CoCo3FPGA", "/wizard/fpga.png", 115200, "none", "1", false, false, false, false));
		
		buttonCoco3FPGA.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (e.widget.getData("def") != null)
				{
					if (((Button) e.widget).getSelection())
					{
						((SimpleWizard) getWizard()).setPlatform((PlatformDef) e.widget.getData("def"));
						
					}
					else
					{
						((SimpleWizard) getWizard()).setPlatform(null);
					}
					
					setButtons();
				}
			}
		});
		
		buttonCoco3FPGA.setLayoutData(new GridData(SWT.CENTER, SWT.FILL, false, false, 1, 1));
		buttonCoco3FPGA.setImage(SWTResourceManager.getImage(PlatformPage.class, "/wizard/fpga.png"));
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		
		Label lblAtariWithLiber = new Label(container, SWT.NONE);
		lblAtariWithLiber.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		GridData gd_lblAtariWithLiber = new GridData(SWT.CENTER, SWT.TOP, false, false, 1, 1);
		gd_lblAtariWithLiber.minimumHeight = 30;
		gd_lblAtariWithLiber.heightHint = 30;
		lblAtariWithLiber.setLayoutData(gd_lblAtariWithLiber);
		lblAtariWithLiber.setText("Atari/Liber809");
		
		Label lblCocofpga = new Label(container, SWT.NONE);
		lblCocofpga.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblCocofpga.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, false, false, 1, 1));
		lblCocofpga.setText("CoCo3FPGA");
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		
		buttonApple = new Button(container, SWT.TOGGLE);
		this.buttons.add(buttonApple);
		buttonApple.setData("def", new PlatformDef("Apple", "/wizard/apple-logo.png", 115200, "none", "1", true, true, false, false));
		
		buttonApple.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (e.widget.getData("def") != null)
				{
					if (((Button) e.widget).getSelection())
					{
						((SimpleWizard) getWizard()).setPlatform((PlatformDef) e.widget.getData("def"));
						
					}
					else
					{
						((SimpleWizard) getWizard()).setPlatform(null);
					}
					
					setButtons();
				}
			}
		});
		
		buttonApple.setLayoutData(new GridData(SWT.CENTER, SWT.FILL, false, false, 1, 1));
		buttonApple.setImage(SWTResourceManager.getImage(PlatformPage.class, "/wizard/apple-logo.png"));
		
		buttonEmulator = new Button(container, SWT.TOGGLE);
		this.buttons.add(buttonEmulator);
		buttonEmulator.setData("def", new PlatformDef("TCP connection", "/wizard/emulator.png"));
		
		buttonEmulator.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (e.widget.getData("def") != null)
				{
					if (((Button) e.widget).getSelection())
					{
						((SimpleWizard) getWizard()).setPlatform((PlatformDef) e.widget.getData("def"));
						
					}
					else
					{
						((SimpleWizard) getWizard()).setPlatform(null);
					}
					
					setButtons();
				}
			}
		});
		
		
		
		buttonEmulator.setLayoutData(new GridData(SWT.CENTER, SWT.FILL, false, false, 1, 1));
		buttonEmulator.setImage(SWTResourceManager.getImage(PlatformPage.class, "/wizard/emulator.png"));
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		
		Label lblAppleIiSuper = new Label(container, SWT.NONE);
		lblAppleIiSuper.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblAppleIiSuper.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		lblAppleIiSuper.setText("Apple II with\r\nSuper Serial");
		
		lblWhatWillIt = new Label(container, SWT.WRAP);
		lblWhatWillIt.setAlignment(SWT.CENTER);
		lblWhatWillIt.setFont(SWTResourceManager.getFont(lblWhatWillIt.getFont().getFontData()[0].getName(), lblWhatWillIt.getFont().getFontData()[0].getHeight(), SWT.BOLD));
		
		lblWhatWillIt.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblWhatWillIt.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		lblWhatWillIt.setText("Which will it be?\r\n ");
		
		Label lblEmulatorVia = new Label(container, SWT.NONE);
		lblEmulatorVia.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblEmulatorVia.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		lblEmulatorVia.setText("Emulator or\r\nother TCP/IP");
		new Label(container, SWT.NONE);
		
		setButtons();
	}
	
	

	
	private void setButtons()
	{
		for (Button b : this.buttons)
		{
			b.setSelection(false);
		}
		
		lblWhatWillIt.setText("Which will it be?");
		
		if (((SimpleWizard) this.getWizard()).getPlatform() != null)
		{
			for (Button b : this.buttons)
			{
				if (b.getData("def") != null)
				{
					if (((SimpleWizard) this.getWizard()).getPlatform() == b.getData("def") )
					{
						b.setSelection(true);
						String t = "Ah.. the " + ((SimpleWizard) this.getWizard()).getPlatform().name + "."; 
						
						lblWhatWillIt.setText(t + "\r\n" + ((SimpleWizard) this.getWizard()).getRandomYay());
						
						this.setPageComplete(true);
					}
				}
			}
		}
		else
		{
			this.setPageComplete(false);
		}
		
		
	}

	@Override
	public IWizardPage getNextPage()
	{
		if ( ((SimpleWizard) this.getWizard()).getPlatform().tcp  )
		{
			return ((SimpleWizard)getWizard()).tcpPage;
		}
		else
		{
		    return ((SimpleWizard)getWizard()).serialPage;
		}
		
		
	}
	
	
	
	
	
	
	
	
	
	
	

	protected Label getLblWhatWillIt() {
		return lblWhatWillIt;
	}
	protected Button getButtonApple() {
		return buttonApple;
	}
	protected Button getButtonAtari() {
		return buttonAtari;
	}
	protected Button getBtnCoco1() {
		return btnCoco1;
	}
	protected Button getBtnCoco2() {
		return btnCoco2;
	}
	protected Button getButtonCoco3() {
		return buttonCoco3;
	}
	protected Button getButtonCoco3FPGA() {
		return buttonCoco3FPGA;
	}
	protected Button getButtonEmulator() {
		return buttonEmulator;
	}
	
	
	
	@Override
	public boolean isPageComplete()
	{
		if ( ((SimpleWizard) this.getWizard()).getPlatform() == null)
			return false;
		
		return true;
	}
	
}
