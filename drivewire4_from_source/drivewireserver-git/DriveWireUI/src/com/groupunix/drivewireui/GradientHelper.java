package com.groupunix.drivewireui;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;

public class GradientHelper{

	//private static Image oldImage = null;

	public static void applyVerticalGradientBG(Composite composite, Color scol, Color ecol) 
	{
		Rectangle rect = composite.getClientArea();
	
		Image newImage = new Image(composite.getDisplay(), 1, Math.max(1, rect.height));
		
		GC gc = new GC(newImage);
		gc.setForeground(scol);
		gc.setBackground(ecol);
		gc.fillGradientRectangle(0, 0, 1, rect.height, true);
		
		gc.dispose();
		composite.setBackgroundImage(newImage);

		//if (oldImage != null)
		//	oldImage.dispose();
		//oldImage = newImage;
	}
	

	public static void applyHorizontalGradientBG(Composite composite, Color scol, Color ecol) 
	{
		Rectangle rect = composite.getClientArea();
	
		Image newImage = new Image(composite.getDisplay(), Math.max(1, rect.height), 1);
		
		GC gc = new GC(newImage);
		gc.setForeground(scol);
		gc.setBackground(ecol);
		gc.fillGradientRectangle(0, 0, rect.width, 1, false);
		
		gc.dispose();
		composite.setBackgroundImage(newImage);

		//if (oldImage != null)
		//	oldImage.dispose();
		//oldImage = newImage;
	}

}