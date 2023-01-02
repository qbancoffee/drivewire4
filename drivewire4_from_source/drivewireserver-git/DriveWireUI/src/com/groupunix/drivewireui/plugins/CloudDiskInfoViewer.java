package com.groupunix.drivewireui.plugins;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.tree.ConfigurationNode;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import swing2swt.layout.BorderLayout;

import com.groupunix.drivewireserver.dwdisk.DWDisk;
import com.groupunix.drivewireui.GradientHelper;
import com.groupunix.drivewireui.MainWin;
import com.groupunix.drivewireui.cococloud.Cloud;
import com.groupunix.drivewireui.library.PathLibraryItem;

public class CloudDiskInfoViewer extends Composite
{
	

	@SuppressWarnings("unused")
	private DWDisk disk;


	@SuppressWarnings("unused")
	private PathLibraryItem pitem;
	private Text textDescription;


	@SuppressWarnings("unused")
	private int diskID;


	private Label lblImg;
	
	
	
	public CloudDiskInfoViewer(Composite parent, int style)
	{
		super(parent, style);
		
		setLayout(new BorderLayout(0, 0));
		
		Composite compositeDetails = new Composite(this, SWT.NONE);
		compositeDetails.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		compositeDetails.setLayoutData(BorderLayout.CENTER);
		compositeDetails.setLayout(new GridLayout(3, false));
		
		Label lblDescription = new Label(compositeDetails, SWT.NONE);
		lblDescription.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		lblDescription.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblDescription.setText("Description");
		
		Label label = new Label(compositeDetails, SWT.NONE);
		GridData gd_label = new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1);
		gd_label.widthHint = 5;
		label.setLayoutData(gd_label);
		
		Composite compositeImages = new Composite(compositeDetails, SWT.NONE);
		compositeImages.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, true, 1, 4));
		compositeImages.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		compositeImages.setLayout(new GridLayout(1, false));
		
		lblImg = new Label(compositeImages, SWT.SHADOW_NONE);
		lblImg.setAlignment(SWT.CENTER);
		lblImg.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
		GridData gd_lblImg = new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1);
		gd_lblImg.heightHint = 242;
		gd_lblImg.widthHint = 322;
		lblImg.setLayoutData(gd_lblImg);
		lblImg.setBounds(0, 0, 55, 15);
		lblImg.setText("\r\n\r\n\r\n\r\n\r\n\r\n\r\n(No Screenshots)");
		
		Label lblImgDesc = new Label(compositeImages, SWT.NONE);
		lblImgDesc.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		GridData gd_lblImgDesc = new GridData(SWT.FILL, SWT.BOTTOM, false, false, 1, 1);
		gd_lblImgDesc.widthHint = 320;
		lblImgDesc.setLayoutData(gd_lblImgDesc);
		lblImgDesc.setBounds(0, 0, 55, 15);
		lblImgDesc.setText("Description");
		
		Composite compositeImgCtl = new Composite(compositeImages, SWT.NONE);
		compositeImgCtl.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		compositeImgCtl.setLayout(new GridLayout(3, false));
		GridData gd_compositeImgCtl = new GridData(SWT.FILL, SWT.BOTTOM, false, false, 1, 1);
		gd_compositeImgCtl.widthHint = 320;
		compositeImgCtl.setLayoutData(gd_compositeImgCtl);
		compositeImgCtl.setBounds(0, 0, 64, 64);
		
		Button buttonPrevImg = new Button(compositeImgCtl, SWT.NONE);
		buttonPrevImg.setText("<<");
		
		Button buttonNextImg = new Button(compositeImgCtl, SWT.NONE);
		buttonNextImg.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		buttonNextImg.setText(">>");
		
		Button btnAddImage = new Button(compositeImgCtl, SWT.NONE);
		btnAddImage.setLayoutData(new GridData(SWT.RIGHT, SWT.BOTTOM, true, false, 1, 1));
		btnAddImage.setText("Add Image..");
		
		textDescription = new Text(compositeDetails, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL | SWT.MULTI);
		GridData gd_textDescription = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
		gd_textDescription.heightHint = 220;
		textDescription.setLayoutData(gd_textDescription);
		new Label(compositeDetails, SWT.NONE);
		
		Label lblDetails = new Label(compositeDetails, SWT.NONE);
		lblDetails.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		lblDetails.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblDetails.setText("Details");
		new Label(compositeDetails, SWT.NONE);
		
		ScrolledComposite scrolledCompositeDetails = new ScrolledComposite(compositeDetails, SWT.H_SCROLL | SWT.V_SCROLL);
		scrolledCompositeDetails.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		scrolledCompositeDetails.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 2));
		scrolledCompositeDetails.setExpandHorizontal(true);
		scrolledCompositeDetails.setExpandVertical(true);
		new Label(compositeDetails, SWT.NONE);
		new Label(compositeDetails, SWT.NONE);
		
		Composite composite = new Composite(compositeDetails, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		
		final Composite compositeBottom = new Composite(this, SWT.NONE);
		compositeBottom.setLayoutData(BorderLayout.SOUTH);
		compositeBottom.setLayout(new GridLayout(2, false));
		
		GradientHelper.applyVerticalGradientBG(compositeBottom, MainWin.getDisplay().getSystemColor(SWT.COLOR_WHITE),MainWin.getDisplay().getSystemColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
		
		compositeBottom.addListener(SWT.Resize, new Listener() {

			@Override
			public void handleEvent(Event event)
			{
				GradientHelper.applyVerticalGradientBG(compositeBottom, MainWin.getDisplay().getSystemColor(SWT.COLOR_WHITE),MainWin.getDisplay().getSystemColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
				
				
			} } );
		
		compositeBottom.setBackgroundMode(SWT.INHERIT_FORCE);
		
		
		new Label(compositeBottom, SWT.NONE);
		
		Button btnSaveChanges = new Button(compositeBottom, SWT.NONE);
		btnSaveChanges.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));
		btnSaveChanges.setText("Save Changes");
		
	
		
	}

		


	









	public void displayDisk(final int diskID)
	{
		this.diskID = diskID;
		
		this.getDisplay().asyncExec(new Runnable(){

			@Override
			public void run()
			{
				getDiskInfo(diskID);
			}});
		
		
	}





	protected void getDiskInfo(int diskID)
	{
		HierarchicalConfiguration info = Cloud.getDiskInfo(diskID);
		
		info = info.configurationAt("Results");
		
			
		
		for (Object cn : info.getRootNode().getChildren())
		{
			ConfigurationNode item = ((ConfigurationNode) cn);
			
			if (item.getName().equals("Description"))
			{
				textDescription.setText(info.getString("Description.Value"));
			}
			
			if (item.getName().equals("Screenshot"))
			{
				if (info.getInt("Screenshot.PathType", -1) == 1)
				{
					try
					{
						URL url = new URL(info.getString("Screenshot.Path"));
						Image image = new Image(getDisplay(), url.openStream());
						
						lblImg.setImage(new Image(getDisplay(), image.getImageData().scaledTo( 320, 240  )) );
						
						//lblImg.setBackground(MainWin.getDisplay().getSystemColor(SWT.COLOR_TITLE_BACKGROUND));
					} 
					catch (MalformedURLException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
			}
			
			
		}
		
	}
}
