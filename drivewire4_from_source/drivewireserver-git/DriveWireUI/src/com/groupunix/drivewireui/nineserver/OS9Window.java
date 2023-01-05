package com.groupunix.drivewireui.nineserver;

import org.eclipse.swt.graphics.Rectangle;

public interface OS9Window
{

	void mouseDown(Rectangle bounds, int x, int y);

	void mouseUp(Rectangle bounds, int x, int y);

	void moveSelect(Rectangle bounds, int x, int y);

	String getSelected();

	Rectangle addToScreen(byte data);

	boolean isSave();

	int getCursorX();
	int getCursorY();
	
	int getOffsetX();
	int getOffsetY();

	int getWidth();
	int getHeight();

	int getCWAOffsetX();
	int getCWAOffsetY();
	
	int getCWAWidth();
	int getCWAHeight();

	int getFGCol();
	int getBGCol();

	int getCharWidth();
	int getCharHeight();

	Rectangle getBounds();
	
	void setCWA(Byte byte1, Byte byte2, Byte byte3, Byte byte4);

	int getSTY();

	void setBGCol(int b);
	void setFGCol(int f);

}
