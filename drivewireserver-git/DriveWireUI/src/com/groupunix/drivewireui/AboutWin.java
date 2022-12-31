package com.groupunix.drivewireui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.swtdesigner.SWTResourceManager;

public class AboutWin extends Dialog {

	protected Object result;
	protected Shell shell;

	private int width = 512;
	private int height = 384;

	private int xborder = 32;
	private int yborder = 32;
	private int yfudge = 38;
	private int xfudge = 16;
	
	private Image cocotext;
	private Image cocotext2;
	private Canvas coco;
	private Image cocoimg;
	private GC cocogc;
	
	private int[][] text = new int[16][32];
	private int[][] dtext = new int[16][32];
	
	private int curx = 0;
	private int cury = 3;
	
	private int cursorcolor = 0;
	private Color[] curscols = new Color[8];
	private int curpos = 1;
	private int dpos = -256;
	private boolean scrolltext = false;
	private Color scrollColor;
	private Color blendColor;
	private Font scrollFont;
	private Font thanksFont;
	private HashMap<String,Integer> fontmap;
	private boolean ssmode = false;
	private int faster = 500;
	private int namewid = 100;
	private int namehi = 100;
	private int noscalex = -1;
	private int noscaley = -1;
	private boolean[] toggles;

	// i could not resist
	private int[][] mem = { 
			
			{ 
			69, 88, 142, 128, 222, 206, 1, 42, 198, 10, 189, 165, 154, 142, 178, 119, 175, 67, 175, 72, 
			142, 137, 76, 191, 1, 13, 158, 138, 191, 1, 18, 189, 130, 156, 204, 44, 5, 221, 230, 142, 
			1, 62, 159, 176, 206, 180, 74, 198, 10, 239, 129, 90, 38, 251, 134, 126, 183, 1, 154, 142, 
			130, 185, 191, 1, 155, 183, 1, 139, 142, 136, 70, 191, 1, 140, 183, 1, 151, 142, 135, 229, 
			191, 1, 152, 183, 1, 121, 142, 142, 144, 191, 1, 122, 183, 1, 145, 142, 136, 240, 191, 1, 
			146, 183, 1, 106, 142, 140, 241, 191, 1, 107, 183, 1, 103, 142, 130, 115, 191, 1, 104, 183, 
			1, 118, 142, 130, 134, 191, 1, 119, 183, 1, 163, 142, 131, 4, 191, 1, 164, 183, 1, 148, 
			142, 130, 156, 191, 1, 149, 183, 1, 29, 142, 132, 137, 191, 1, 30, 189, 150, 230, 182, 255, 
			3, 138, 1, 183, 255, 3, 142, 68, 75, 188, 192, 0, 16, 39, 63, 82, 28, 175, 142, 128, 
			231, 189, 185, 156, 142, 128, 192, 159, 114, 126, 160, 226, 255, 15, 227, 15, 228, 182, 255, 3, 
			138, 1, 183, 255, 3, 126, 160, 232, 150, 104, 76, 39, 8, 31, 32, 147, 25, 211, 166, 221, 
			166, 57, 25, 129, 131, 129, 60, 14, 130, 30, 129, 104, 69, 88, 84, 69, 78, 68, 69, 68, 
			32, 67, 79, 76, 79, 82, 32, 66, 65, 83, 73, 67, 32, 50, 46, 48, 13, 67, 79, 80, 
			82, 46, 32, 49, 57, 56, 50, 44, 32, 49, 57, 56, 54, 32, 66, 89, 32, 84, 65, 78, 
			68, 89, 32, 32, 13, 85, 78, 68, 69, 82, 32, 76, 73, 67, 69, 78, 83, 69, 32, 70, 
			82, 79, 77, 32, 77, 73, 67, 82, 79, 83, 79, 70, 84, 13, 13, 0, 129, 203, 34, 8, 
			142, 129, 240, 128, 181, 126, 173, 212, 129, 255, 39, 8, 129, 205, 35, 21, 110, 159, 1, 55, 
			157, 159, 129, 144, 16, 39, 5, 122, 129, 159, 16, 39, 7, 254, 189, 1, 160, 126, 178, 119, 
			193, 66, 35, 4, 110, 159, 1, 60, 192, 40, 193, 16, 34, 7, 52, 4, 189, 178, 98, 53, 
			4, 142, 130, 87, 126, 178, 206, 68, 69, 204, 69, 68, 73, 212, 84, 82, 79, 206, 84, 82, 
			79, 70, 198, 68, 69, 198, 76, 69, 212, 76, 73, 78, 197, 80, 67, 76, 211, 80, 83, 69, 
			212, 80, 82, 69, 83, 69, 212, 83, 67, 82, 69, 69, 206, 80, 67, 76, 69, 65, 210, 67, 
			79, 76, 79, 210, 67, 73, 82, 67, 76, 197, 80, 65, 73, 78, 212, 71, 69, 212, 80, 85, 
			212, 68, 82, 65, 215, 80, 67, 79, 80, 217, 80, 77, 79, 68, 197, 80, 76, 65, 217, 68, 
			76, 79, 65, 196, 82, 69, 78, 85, 205, 70, 206, 85, 83, 73, 78, 199, 137, 112, 133, 51, 
			134, 167, 134, 168, 136, 113, 175, 137, 147, 187, 149, 50  
			}
			,
			{ 
				161, 203, 162, 130, 167, 124, 167, 11, 167, 244, 169, 222, 167, 216, 16, 206, 3, 215, 134, 55, 
				183, 255, 35, 150, 113, 129, 85, 38, 87, 158, 114, 166, 132, 129, 18, 38, 79, 110, 132, 49, 
				140, 228, 134, 58, 183, 255, 162, 142, 255, 32, 204, 255, 52, 111, 1, 111, 3, 74, 167, 132, 
				134, 248, 167, 2, 231, 1, 231, 3, 111, 2, 134, 2, 167, 132, 134, 255, 142, 255, 0, 111, 
				1, 111, 3, 111, 132, 167, 2, 231, 1, 231, 3, 126, 160, 114, 189, 140, 46, 126, 192, 0, 
				229, 2, 39, 10, 111, 30, 229, 2, 39, 2, 51, 94, 167, 93, 110, 164, 142, 4, 1, 111, 
				131, 48, 1, 38, 250, 189, 169, 40, 111, 128, 159, 25, 142, 127, 255, 32, 10, 18, 18, 18, 
				18, 18, 18, 18, 18, 18, 18, 159, 116, 159, 39, 159, 35, 48, 137, 255, 56, 159, 33, 31, 
				20, 142, 161, 13, 206, 0, 143, 198, 28, 189, 165, 154, 206, 1, 12, 198, 30, 189, 165, 154, 
				174, 20, 175, 67, 175, 72, 142, 1, 94, 204, 57, 75, 167, 128, 90, 38, 251, 183, 2, 217, 
				189, 173, 25, 126, 128, 2, 52, 20, 13, 231, 16, 38, 86, 168, 189, 161, 153, 189, 161, 203, 
				39, 248, 126, 161, 185, 114, 134, 85, 151, 113, 32, 11, 18, 15, 111, 189, 173, 51, 28, 175, 
				189, 169, 16, 126, 172, 115, 125, 255, 35, 43, 1, 59, 189, 140, 40, 189, 167, 209, 49, 140, 
				3, 126, 160, 42, 15, 113, 126, 192, 0, 18, 24, 10, 0, 128, 11, 0, 88, 0, 1, 16, 
				112, 132, 0, 180, 74, 12, 167, 38, 2, 12, 166, 182, 0, 0, 126, 170, 26, 126, 169, 179, 
				126, 160, 246, 126, 180, 74, 128, 79, 199, 82, 89, 255, 4, 94, 126, 178, 119, 53, 170, 102, 
				171, 103, 20, 171, 26, 170, 41, 67, 79, 76, 79, 82, 32, 66, 65, 83, 73, 67, 32, 49, 
				46, 50, 13, 40, 67, 41, 32, 49, 57, 56, 50, 32, 84, 65, 78, 68, 89, 0, 77, 73, 
				67, 82, 79, 83, 79, 70, 84, 13, 0, 141, 3, 132, 127, 57, 189, 1, 106, 15, 112, 13, 
				111, 39, 50, 13, 121, 38, 3, 3, 112, 57, 52, 116, 158, 122, 166, 128, 52, 2, 159, 122, 
				10, 121, 38, 3, 189, 166, 53, 53, 246, 10, 148, 38, 14, 198, 11, 215, 148, 158, 136, 166, 
				132, 139, 16, 138, 143, 167, 132, 142, 4, 94, 126, 167, 211, 52, 20, 141, 228, 141, 20, 39, 
				250, 198, 96, 231, 159, 0, 136, 53, 148, 126, 161, 203, 57, 57, 57, 57, 57, 57, 57, 52, 
				84, 206, 255, 0, 142, 1, 82, 79, 74, 52, 18, 167, 66, 105, 66, 36, 67, 108, 96, 141, 
				89, 167, 97, 168, 132, 164, 132, 230, 97, 231, 128, 77, 39, 235, 230, 66, 231, 98, 198, 248, 
				203, 8, 68, 36, 251, 235, 96, 39, 72, 193, 26, 34 }
				,
				{ 
				228, 166, 224, 68, 129, 1, 38, 230, 84, 84, 225, 31, 39, 4, 106, 228, 38, 217, 231, 130, 
				236, 225, 90, 42, 204, 57, 129, 58, 36, 10, 129, 32, 38, 2, 14, 159, 128, 48, 128, 208, 
				57, 188, 122, 188, 238, 188, 147, 1, 18, 191, 31, 191, 120, 183, 80, 182, 129, 180, 253, 183, 
				22, 182, 160, 182, 140, 165, 206, 169, 198, 182, 171, 182, 200, 182, 207, 168, 245, 165, 100, 180, 
				238, 121, 185, 197, 121, 185, 188, 123, 186, 204, 123, 187, 145, 127, 1, 29, 80, 178, 213, 70, 
				178, 212, 70, 79, 210, 71, 207, 82, 69, 205, 167, 69, 76, 83, 197, 73, 198, 68, 65, 84, 
				193, 80, 82, 73, 78, 212, 79, 206, 73, 78, 80, 85, 212, 69, 78, 196, 78, 69, 88, 212, 
				68, 73, 205, 82, 69, 65, 196, 82, 85, 206, 82, 69, 83, 84, 79, 82, 197, 82, 69, 84, 
				85, 82, 206, 83, 84, 79, 208, 80, 79, 75, 197, 67, 79, 78, 212, 76, 73, 83, 212, 67, 
				76, 69, 65, 210, 78, 69, 215, 67, 76, 79, 65, 196, 67, 83, 65, 86, 197, 79, 80, 69, 
				206, 67, 76, 79, 83, 197, 76, 76, 73, 83, 212, 83, 69, 212, 82, 69, 83, 69, 212, 67, 
				76, 211, 77, 79, 84, 79, 210, 83, 79, 85, 78, 196, 65, 85, 68, 73, 207, 69, 88, 69, 
				195, 83, 75, 73, 80, 198, 84, 65, 66, 168, 84, 207, 83, 85, 194, 84, 72, 69, 206, 78, 
				79, 212, 83, 84, 69, 208, 79, 70, 198, 171, 173, 170, 175, 222, 65, 78, 196, 79, 210, 190, 
				189, 188, 83, 71, 206, 73, 78, 212, 65, 66, 211, 85, 83, 210, 82, 78, 196, 83, 73, 206, 
				80, 69, 69, 203, 76, 69, 206, 83, 84, 82, 164, 86, 65, 204, 65, 83, 195, 67, 72, 82, 
				164, 69, 79, 198, 74, 79, 89, 83, 84, 203, 76, 69, 70, 84, 164, 82, 73, 71, 72, 84, 
				164, 77, 73, 68, 164, 80, 79, 73, 78, 212, 73, 78, 75, 69, 89, 164, 77, 69, 205, 173, 
				71, 174, 134, 174, 227, 174, 227, 174, 227, 175, 20, 174, 224, 184, 247, 175, 66, 175, 245, 174, 
				2, 176, 248, 179, 78, 176, 70, 174, 117, 173, 228, 174, 192, 174, 9, 183, 87, 174, 48, 183, 
				100, 174, 65, 173, 23, 164, 152, 164, 76, 165, 246, 164, 22, 183, 94, 168, 128, 168, 177, 169, 
				16, 167, 189, 169, 75, 169, 144, 165, 62, 165, 236, 78, 70, 83, 78, 82, 71, 79, 68, 70, 
				67, 79, 86, 79, 77, 85, 76, 66, 83, 68, 68, 47, 48, 73, 68, 84, 77, 79, 83, 76, 
				83, 83, 84, 67, 78, 70, 68, 65, 79, 68, 78, 73, 79, 70, 77, 78, 79, 73, 69, 68, 
				83, 32, 69, 82, 82, 79, 82, 0, 32, 73, 78, 32, 0, 13, 79, 75, 13, 0, 13, 66, 
				82, 69, 65, 75, 0, 48, 100, 198, 18, 159, 15, 166  }
				,
				{ 
				0, 0, 0, 127, 255, 255, 247, 255, 251, 238, 239, 251, 255, 187, 255, 255, 255, 251, 255, 255, 
				187, 187, 187, 191, 187, 187, 255, 191, 255, 254, 239, 255, 255, 255, 239, 127, 255, 255, 247, 187, 
				255, 187, 187, 191, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 
				255, 255, 187, 191, 187, 255, 187, 127, 255, 255, 246, 238, 238, 238, 238, 238, 239, 255, 254, 255, 
				254, 238, 238, 254, 238, 238, 238, 239, 238, 238, 238, 238, 238, 254, 238, 238, 238, 238, 238, 127, 
				255, 255, 243, 187, 255, 187, 187, 187, 187, 191, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 
				255, 255, 255, 255, 255, 251, 187, 187, 187, 187, 187, 127, 255, 255, 246, 170, 174, 170, 174, 170, 
				234, 187, 187, 251, 255, 187, 255, 191, 191, 255, 251, 191, 187, 187, 187, 187, 187, 186, 234, 170, 
				238, 174, 238, 127, 255, 255, 243, 187, 191, 187, 187, 187, 187, 191, 255, 255, 255, 255, 255, 255, 
				255, 255, 255, 255, 255, 255, 255, 255, 255, 251, 187, 187, 187, 187, 187, 127, 255, 255, 246, 238, 
				238, 238, 238, 238, 238, 238, 238, 239, 254, 239, 255, 255, 255, 255, 254, 239, 238, 238, 238, 238, 
				238, 238, 238, 238, 238, 238, 239, 127, 255, 255, 247, 187, 187, 187, 187, 187, 187, 191, 255, 255, 
				255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 187, 187, 187, 187, 187, 127, 
				255, 255, 246, 170, 238, 170, 170, 170, 234, 171, 187, 187, 255, 187, 255, 255, 255, 255, 255, 255, 
				251, 191, 255, 255, 187, 187, 234, 174, 170, 170, 175, 127, 255, 255, 247, 187, 187, 191, 255, 251, 
				187, 191, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 251, 187, 
				187, 187, 187, 127, 255, 255, 246, 238, 238, 238, 239, 239, 238, 238, 239, 255, 255, 255, 255, 255, 
				255, 255, 255, 255, 255, 255, 255, 255, 238, 238, 255, 239, 238, 238, 239, 127, 255, 255, 247, 187, 
				187, 191, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 
				255, 255, 255, 255, 187, 187, 187, 127, 255, 255, 247, 234, 170, 187, 187, 187, 187, 187, 251, 187, 
				191, 255, 255, 255, 255, 255, 255, 255, 255, 251, 255, 251, 251, 187, 187, 186, 170, 170, 239, 127, 
				255, 255, 247, 187, 187, 191, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 
				255, 255, 255, 255, 255, 255, 255, 251, 187, 187, 187, 127, 255, 255, 246, 238, 238, 238, 254, 238, 
				238, 238, 238, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 254, 238, 239, 254, 238, 
				238, 238, 239, 127, 255, 255, 247, 187, 187, 251, 191, 255, 255, 255, 255, 255, 255, 254, 0, 0, 
				0, 0, 0, 0, 0, 15, 255, 255, 255, 255, 255, 251  }
				,
				{ 
				255, 255, 255, 255, 255, 255, 0, 0, 160, 39, 0, 0, 0, 0, 0, 0, 255, 255, 255, 255, 
				255, 255, 255, 255, 0, 0, 0, 0, 0, 0, 0, 0, 255, 255, 255, 255, 255, 255, 255, 255, 
				0, 0, 0, 0, 0, 0, 0, 0, 255, 255, 255, 255, 255, 255, 255, 255, 0, 0, 0, 0, 
				0, 0, 0, 0, 255, 255, 255, 255, 255, 255, 255, 255, 0, 0, 0, 0, 0, 0, 0, 0, 
				255, 255, 255, 255, 255, 255, 255, 255, 0, 0, 0, 0, 0, 0, 0, 0, 255, 255, 255, 255, 
				255, 255, 255, 255, 0, 0, 0, 0, 0, 0, 0, 0, 255, 255, 255, 255, 255, 255, 255, 255, 
				0, 0, 0, 0, 0, 0, 0, 0, 255, 255, 255, 255, 255, 255, 255, 255, 0, 0, 0, 0, 
				0, 0, 0, 0, 255, 255, 255, 255, 255, 255, 255, 255, 0, 0, 0, 0, 0, 0, 0, 0, 
				255, 255, 255, 255, 255, 255, 255, 255, 0, 0, 0, 0, 0, 0, 0, 0, 255, 255, 255, 255, 
				255, 255, 255, 255, 0, 0, 0, 0, 0, 0, 0, 0, 239, 255, 255, 255, 255, 255, 255, 255, 
				0, 0, 0, 0, 0, 0, 0, 0, 255, 255, 255, 255, 255, 255, 255, 255, 0, 0, 0, 0, 
				0, 0, 0, 0, 255, 255, 255, 255, 255, 255, 255, 255, 0, 0, 0, 0, 0, 0, 0, 0, 
				255, 255, 255, 255, 255, 255, 255, 255, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
				0, 0, 0, 0, 255, 255, 255, 255, 255, 255, 255, 255, 0, 0, 0, 0, 0, 0, 0, 0, 
				255, 255, 255, 255, 255, 255, 255, 255, 0, 0, 0, 0, 0, 0, 0, 0, 255, 255, 255, 255, 
				255, 255, 255, 255, 0, 0, 0, 0, 0, 0, 0, 0, 255, 255, 255, 255, 255, 255, 255, 255, 
				0, 0, 0, 0, 0, 0, 0, 0, 255, 255, 255, 255, 255, 255, 255, 255, 0, 0, 0, 0, 
				0, 0, 0, 0, 255, 255, 255, 255, 255, 255, 255, 255, 0, 0, 0, 0, 0, 0, 0, 0, 
				255, 255, 255, 255, 255, 255, 255, 255, 0, 0, 0, 0, 0, 0, 0, 0, 255, 255, 255, 255, 
				255, 255, 255, 255, 0, 0, 0, 0, 0, 0, 0, 0, 255, 255, 255, 255, 255, 255, 255, 255, 
				0, 0, 0, 0, 0, 0, 0, 0, 255, 255, 255, 255, 255, 255, 255, 255, 0, 0, 0, 0, 
				0, 0, 0, 0, 255, 255, 255, 255, 255, 255, 255, 255, 0, 0, 0, 0, 0, 0, 0, 0, 
				255, 255, 255, 255, 255, 255, 255, 255, 0, 0, 0, 0, 0, 0, 0, 0, 255, 255, 255, 255, 
				255, 255, 255, 255, 0, 0, 0, 0, 0, 0, 0, 0, 255, 255, 255, 255, 255, 255, 255, 255, 
				0, 0, 0, 0, 0, 0, 0, 0, 255, 255, 255, 255, 255, 255, 255, 255, 0, 0, 0, 0, 
				0, 0, 0, 0, 255, 255, 255, 255, 255, 255, 255, 255  }
				,
				{ 
				0, 230, 224, 25, 224, 77, 224, 151, 224, 181, 224, 161, 224, 255, 225, 25, 126, 160, 94, 0, 
				0, 0, 0, 0, 0, 52, 50, 16, 33, 31, 225, 142, 224, 50, 150, 231, 39, 92, 142, 224, 
				59, 129, 1, 39, 85, 142, 224, 68, 32, 80, 204, 0, 0, 0, 0, 15, 224, 0, 0, 76, 
				3, 5, 18, 0, 0, 216, 0, 0, 76, 3, 21, 18, 0, 0, 216, 0, 0, 52, 50, 16, 
				33, 31, 173, 142, 224, 112, 16, 142, 224, 108, 150, 230, 129, 2, 35, 3, 142, 224, 121, 128, 
				1, 166, 166, 167, 2, 126, 224, 130, 21, 30, 20, 29, 76, 128, 0, 0, 0, 0, 192, 0, 
				0, 76, 128, 0, 0, 0, 0, 192, 0, 0, 166, 128, 183, 255, 144, 16, 142, 255, 152, 166, 
				128, 167, 160, 16, 140, 255, 160, 37, 246, 53, 178, 52, 54, 48, 141, 0, 68, 141, 82, 53, 
				182, 52, 54, 48, 141, 0, 58, 52, 16, 231, 132, 141, 68, 198, 56, 53, 16, 231, 132, 53, 
				182, 52, 54, 48, 141, 0, 38, 52, 16, 198, 54, 231, 1, 141, 46, 53, 16, 198, 57, 231, 
				1, 53, 182, 52, 54, 48, 141, 0, 16, 52, 16, 198, 52, 231, 14, 141, 24, 53, 16, 198, 
				53, 231, 14, 53, 182, 56, 57, 58, 59, 60, 61, 62, 63, 56, 48, 49, 50, 51, 61, 53, 
				63, 16, 142, 255, 160, 198, 16, 166, 128, 167, 160, 90, 38, 249, 57, 221, 64, 236, 228, 221, 
				66, 236, 98, 221, 68, 95, 247, 255, 145, 16, 222, 68, 220, 66, 52, 6, 220, 64, 28, 175, 
				57, 26, 80, 221, 64, 53, 6, 221, 66, 16, 223, 68, 198, 1, 247, 255, 145, 16, 206, 223, 
				255, 220, 68, 52, 6, 220, 66, 52, 6, 220, 64, 57, 13, 65, 38, 22, 150, 66, 129, 98, 
				35, 6, 206, 1, 27, 126, 184, 215, 134, 98, 206, 225, 88, 151, 66, 126, 184, 157, 150, 66, 
				129, 41, 35, 3, 126, 184, 215, 134, 41, 206, 225, 93, 32, 235, 23, 225, 197, 225, 146, 5, 
				226, 100, 225, 166, 0, 0, 0, 0, 0, 0, 51, 74, 109, 196, 16, 38, 214, 127, 48, 31, 
				166, 128, 132, 127, 129, 98, 37, 7, 128, 98, 206, 225, 88, 32, 231, 128, 41, 206, 225, 93, 
				32, 224, 129, 226, 37, 4, 129, 248, 35, 4, 110, 159, 1, 55, 128, 226, 142, 226, 54, 126, 
				173, 212, 193, 82, 37, 4, 193, 90, 35, 4, 110, 159, 1, 60, 192, 82, 193, 4, 36, 7, 
				52, 4, 189, 178, 98, 53, 4, 142, 226, 126, 126, 178, 206, 87, 73, 68, 84, 200, 80, 65, 
				76, 69, 84, 84, 197, 72, 83, 67, 82, 69, 69, 206, 76, 80, 79, 75, 197, 72, 67, 76, 
				211, 72, 67, 79, 76, 79, 210, 72, 80, 65, 73, 78, 212, 72, 67, 73, 82, 67, 76, 197, 
				72, 76, 73, 78, 197, 72, 71, 69, 212, 72, 80, 85  }
				,
				{ 
				255, 57, 240, 26, 240, 69, 240, 10, 240, 26, 52, 2, 67, 164, 132, 167, 132, 53, 2, 148, 
				181, 170, 132, 167, 128, 57, 52, 32, 16, 142, 240, 53, 31, 137, 68, 68, 68, 68, 166, 166, 
				189, 240, 10, 196, 15, 166, 165, 189, 240, 10, 53, 32, 57, 0, 3, 12, 15, 48, 51, 60, 
				63, 192, 195, 204, 207, 240, 243, 252, 255, 52, 34, 16, 142, 240, 108, 68, 68, 68, 68, 72, 
				236, 166, 189, 240, 10, 31, 152, 189, 240, 10, 53, 2, 132, 15, 72, 236, 166, 189, 240, 10, 
				31, 152, 189, 240, 10, 53, 32, 57, 0, 0, 0, 15, 0, 240, 0, 255, 15, 0, 15, 15, 
				15, 240, 15, 255, 240, 0, 240, 15, 240, 240, 240, 255, 255, 0, 255, 15, 255, 240, 255, 255, 
				220, 189, 88, 88, 73, 88, 73, 221, 189, 150, 192, 72, 72, 72, 151, 192, 57, 0, 0, 0, 
				0, 0, 0, 0, 0, 16, 16, 16, 16, 16, 0, 16, 0, 40, 40, 40, 0, 0, 0, 0, 
				0, 40, 40, 124, 40, 124, 40, 40, 0, 16, 60, 80, 56, 20, 120, 16, 0, 96, 100, 8, 
				16, 32, 76, 12, 0, 32, 80, 80, 32, 84, 72, 52, 0, 16, 16, 32, 0, 0, 0, 0, 
				0, 8, 16, 32, 32, 32, 16, 8, 0, 32, 16, 8, 8, 8, 16, 32, 0, 0, 16, 84, 
				56, 56, 84, 16, 0, 0, 16, 16, 124, 16, 16, 0, 0, 0, 0, 0, 0, 0, 16, 16, 
				32, 0, 0, 0, 124, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 16, 0, 0, 4, 8, 
				16, 32, 64, 0, 0, 56, 68, 76, 84, 100, 68, 56, 0, 16, 48, 16, 16, 16, 16, 56, 
				0, 56, 68, 4, 56, 64, 64, 124, 0, 56, 68, 4, 8, 4, 68, 56, 0, 8, 24, 40, 
				72, 124, 8, 8, 0, 124, 64, 120, 4, 4, 68, 56, 0, 56, 64, 64, 120, 68, 68, 56, 
				0, 124, 4, 8, 16, 32, 64, 64, 0, 56, 68, 68, 56, 68, 68, 56, 0, 56, 68, 68, 
				56, 4, 4, 56, 0, 0, 0, 16, 0, 0, 16, 0, 0, 0, 0, 16, 0, 0, 16, 16, 
				32, 8, 16, 32, 64, 32, 16, 8, 0, 0, 0, 124, 0, 124, 0, 0, 0, 32, 16, 8, 
				4, 8, 16, 32, 0, 56, 68, 4, 8, 16, 0, 16, 0, 56, 68, 4, 52, 76, 76, 56, 
				0, 16, 40, 68, 68, 124, 68, 68, 0, 120, 36, 36, 56, 36, 36, 120, 0, 56, 68, 64, 
				64, 64, 68, 56, 0, 120, 36, 36, 36, 36, 36, 120, 0, 124, 64, 64, 112, 64, 64, 124, 
				0, 124, 64, 64, 112, 64, 64, 64, 0, 56, 68, 64, 64, 76, 68, 56, 0, 68, 68, 68, 
				124, 68, 68, 68, 0, 56, 16, 16, 16, 16, 16, 56, 0, 4, 4, 4, 4, 4, 68, 56, 
				0, 68, 72, 80, 96, 80, 72, 68, 0, 64, 64, 64 }
				,
				{ 
				185, 156, 77, 105, 99, 114, 111, 119, 97, 114, 101, 32, 83, 121, 115, 116, 101, 109, 115, 32, 
				67, 111, 114, 112, 46, 13, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
				0, 0, 0, 0, 0, 0, 0, 0, 141, 64, 23, 255, 87, 141, 65, 142, 247, 26, 189, 185, 
				156, 52, 16, 48, 141, 255, 177, 134, 18, 167, 128, 167, 132, 48, 141, 255, 206, 167, 128, 140, 
				247, 77, 37, 249, 53, 16, 57, 13, 231, 38, 6, 189, 169, 40, 126, 163, 144, 23, 255, 124, 
				32, 248, 52, 32, 49, 141, 232, 207, 167, 35, 167, 44, 53, 160, 26, 80, 23, 233, 62, 57, 
				23, 233, 28, 28, 175, 57, 141, 7, 189, 161, 203, 39, 249, 53, 148, 10, 148, 38, 29, 198, 
				11, 215, 148, 141, 225, 190, 254, 0, 166, 1, 133, 64, 39, 5, 182, 254, 8, 32, 5, 182, 
				254, 8, 138, 64, 167, 1, 141, 208, 142, 4, 94, 126, 167, 211, 141, 194, 16, 33, 8, 76, 
				190, 254, 0, 129, 8, 38, 9, 140, 32, 0, 39, 30, 141, 32, 32, 26, 129, 13, 38, 4, 
				141, 93, 32, 11, 129, 32, 37, 14, 246, 254, 8, 237, 132, 141, 48, 188, 254, 6, 37, 2, 
				141, 118, 141, 152, 53, 150, 52, 6, 134, 32, 246, 254, 8, 237, 132, 202, 64, 237, 30, 48, 
				30, 191, 254, 0, 252, 254, 2, 74, 42, 8, 90, 247, 254, 3, 182, 254, 4, 74, 183, 254, 
				2, 53, 134, 52, 6, 134, 32, 246, 254, 8, 202, 64, 48, 2, 237, 132, 191, 254, 0, 252, 
				254, 2, 76, 177, 254, 4, 37, 226, 92, 247, 254, 3, 79, 32, 219, 52, 6, 134, 32, 246, 
				254, 8, 237, 129, 52, 2, 182, 254, 2, 76, 183, 254, 2, 177, 254, 4, 53, 2, 37, 238, 
				191, 254, 0, 127, 254, 2, 124, 254, 3, 134, 32, 246, 254, 8, 202, 64, 237, 132, 53, 134, 
				52, 6, 142, 32, 0, 182, 254, 4, 129, 40, 38, 14, 236, 136, 80, 237, 129, 140, 39, 48, 
				37, 246, 141, 15, 53, 134, 236, 137, 0, 160, 237, 129, 140, 46, 96, 37, 245, 32, 239, 127, 
				254, 2, 134, 23, 183, 254, 3, 134, 32, 246, 254, 8, 52, 16, 237, 129, 188, 254, 6, 38, 
				249, 127, 254, 2, 53, 16, 134, 32, 246, 254, 8, 202, 64, 237, 132, 191, 254, 0, 57, 13, 
				111, 38, 4, 13, 231, 38, 6, 189, 163, 95, 126, 185, 95, 23, 254, 190, 125, 254, 2, 52, 
				1, 23, 254, 188, 53, 1, 16, 38, 192, 150, 57, 13, 231, 38, 6, 189, 165, 84, 126, 185, 
				5, 198, 78, 126, 172, 70, 214, 231, 16, 33, 7, 40, 39, 243, 52, 4, 189, 231, 178, 150, 
				44, 53, 4, 193, 1, 38, 4, 129, 40, 32, 2, 129, 80, 16, 36, 187, 89, 214, 192, 193, 
				24, 36, 246, 52, 6, 23, 254, 118, 253, 254, 2, 190  }
	};
	
	
	
	
	ArrayList<String >folks = new ArrayList<String>(Arrays.asList( "Special thanks to:", "", "Cloud-9", "#coco_chat", "Malted Media", 
			"The Glenside Color Computer Club", "Darren Atkinson", "Boisy Pitre", 
			"John Linville", "RandomRodder", "lorddragon", "lostwizard", "beretta",  "Gary Becker", "Jim Hathaway",
			"Gene Heskett", "Wayne Campbell", "Stephen Fischer", "Christopher Hawks", "Bill Pierce", "John Orwen", "And apologies to any I forgot!"));
	
	protected boolean fullscreen = false;
	
	

	public AboutWin(Shell parent, int style) {
		super(parent, style);
		setText("About DriveWire User Interface");
	}

	public Object open() {
		cocotext = org.eclipse.wb.swt.SWTResourceManager.getImage(MainWin.class, "/dw/cocotext.png");
		cocotext2 = org.eclipse.wb.swt.SWTResourceManager.getImage(MainWin.class, "/dw/cocotext2.png");
		
		toggles = new boolean[10];
		for (int i = 0;i<10;i++)
		{
			toggles[i] = true;
		}
		
		createContents();
		shell.open();
		shell.layout();
		Display display = getParent().getDisplay();
		
		shell.setLocation(getParent().getLocation().x + (getParent().getSize().x/2 -  shell.getSize().x/2) ,getParent().getLocation().y + (getParent().getSize().y/2 - shell.getSize().y/2 ));
		
		noscalex = coco.getBounds().width - (xborder*2);
		noscaley = coco.getBounds().height - (yborder*2);
		
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return result;
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shell = new Shell(getParent(), getStyle());
		shell.setBackground(SWTResourceManager.getColor(SWT.COLOR_BLACK));
		shell.setImage(SWTResourceManager.getImage(AboutWin.class, "/com/sun/java/swing/plaf/windows/icons/Inform.gif"));
		shell.setSize(width + xfudge + xborder*2, height + yfudge + yborder*2);
		shell.setText("About DriveWire...");
		
		curscols[0] = new Color(shell.getDisplay(), 0, 255, 0);
		curscols[1] = new Color(shell.getDisplay(), 255, 255, 0);
		curscols[2] = new Color(shell.getDisplay(), 0, 0, 255);
		curscols[3] = new Color(shell.getDisplay(), 255, 0, 0);
		curscols[4] = new Color(shell.getDisplay(), 255, 255, 255);
		curscols[5] = new Color(shell.getDisplay(), 0, 255, 255);
		curscols[6] = new Color(shell.getDisplay(), 255, 0, 255);
		curscols[7] = new Color(shell.getDisplay(), 255, 128, 0);
		
		
		
		scrollColor = new Color(shell.getDisplay(), 240,240,240);
		blendColor = new Color(shell.getDisplay(), 50,50,50);
		
		fontmap = new HashMap<String,Integer>();
		fontmap.put("Roboto Cn", SWT.NORMAL);
		fontmap.put("Roboto", 3);
		fontmap.put("Roboto", 0);
		thanksFont = UIUtils.findFont(shell.getDisplay(), fontmap, "Special thanks to..", 250, 45);
		
		shell.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		coco = new Canvas(shell, SWT.DOUBLE_BUFFERED);
		
		coco.addKeyListener(new KeyListener() 
		{

			public void keyPressed(KeyEvent e)
			{
			
				if (e.character == 's')
				{
					curx = 31;
					cury = 18;
					ssmode = true;
				}
				else if (e.character == '=')
				{
					faster = Math.max(faster/2, 1);
				}
				else if (e.character == '-')
				{
					faster += faster;
				}
				else if ((e.character >= '0') && (e.character <= '9'))
				{
					toggles[Integer.parseInt(e.character+"")] = !toggles[Integer.parseInt(e.character+"")];
				}
				else if (e.character == 'f')
				{
					if (fullscreen)
					{
						fullscreen = false;
						shell.setSize(width + xfudge + xborder*2, height + yfudge + yborder*2);
					}
					else
					{
						fullscreen = true;
						shell.setFullScreen(true);
					}
				}
			}

			public void keyReleased(KeyEvent e)
			{
				
			}
			
		} 
		);
		
		
		
		coco.setBackground(MainWin.colorBlack);
		cocoimg = new Image(null, 512, 384);
		this.cocogc = new GC(cocoimg);
		// ?
		this.cocogc.setAdvanced(false);
		
		
		coco.addPaintListener(new PaintListener() 
		{
			long start;
			int wx;
			int hx;
			
			public void paintControl(PaintEvent e)
			{
				
				if (toggles[1])
					e.gc.setTextAntialias(SWT.OFF);
				else
					e.gc.setTextAntialias(SWT.ON);
				
				if (toggles[2])
					e.gc.setAntialias(SWT.OFF);
				else
					e.gc.setAntialias(SWT.ON);
				
				e.gc.setAdvanced(!toggles[3]);
				
				if (!toggles[0])
					start = System.currentTimeMillis();
				
				genCocoimg();
				
				wx = coco.getBounds().width - (xborder*2);
				hx = coco.getBounds().height - (yborder*2);
				
				if ((wx < 64) || (hx < 64))
				{
					e.gc.setForeground(scrollColor);
					e.gc.setFont(MainWin.fontGraphLabel);
					e.gc.drawString("I'm small!", 1, 1);
				}
				else
				{
					if (((wx == noscalex) && (hx == noscaley)) || !toggles[8])
					{
						e.gc.drawImage(cocoimg, xborder, yborder);
					}
					else
					{
						
						e.gc.drawImage(cocoimg, 0, 0, 512, 384, xborder, yborder, wx, hx);
					}
				}
				
				
				if (!toggles[0])
				{
					e.gc.setFont(MainWin.fontGraphLabel);
					e.gc.setForeground(scrollColor);
					e.gc.setBackground(MainWin.colorBlack);
					String ss = "genimg took " + (System.currentTimeMillis() - start) + " ms ";
					
					for (int i = 0;i < 10;i++)
					{
						ss += "  " + i + ":" + toggles[i];
					}
					
					e.gc.drawString(ss, 1 , 1);
				}
				
				
				
				if (!ssmode)
				{
					
				
				
					if (scrolltext)
					{
						if (dpos > -255)
						{
							int x = (shell.getBounds().width/2 - namewid/2 - 8);
							
							e.gc.setAlpha(Math.max(0, Math.min(128, dpos + 64)));
							e.gc.setBackground(MainWin.colorBlack);
							e.gc.setForeground(blendColor);
							e.gc.fillGradientRectangle(xborder, hx - namehi - 20 + yborder, wx, namehi + 20, true);
							
							
							if (dpos > 0)
							{
								e.gc.setFont(scrollFont);
								
								e.gc.setAlpha(Math.min(255, dpos));
								
								e.gc.setForeground(scrollColor);
								e.gc.drawString(folks.get(curpos), x, hx - namehi - 10 + yborder, true);
								
								if (curpos != folks.size()-1)
								{
									e.gc.setAlpha(Math.max(0, Math.min(255, dpos - 200)));
									e.gc.setFont(thanksFont);
									e.gc.drawString("Special thanks to...", x, hx - namehi - 70 + yborder, true);
									
								}
								
							}	
							
							
							dpos = dpos - 10;
						}
						else
						{
							curpos++;
							
							if (curpos==folks.size())
								curpos = 2;
						
							dpos = 500;
							
							if (scrollFont != null)
								scrollFont.dispose();
							
							scrollFont = UIUtils.findFont(shell.getDisplay(), fontmap, folks.get(curpos), wx, 90);
							e.gc.setFont(scrollFont);
							namewid = e.gc.stringExtent(folks.get(curpos)).x;
							namehi = e.gc.stringExtent(folks.get(curpos)).y;
							lockup1();
						}
						
					
					}
				}
				
				
			}
			
		});

		
		Runnable cursor = new Runnable()
		{
						
			public void run()
			{
				if (!shell.isDisposed())
				{
				
					if (ssmode)
						lockup1();
					else
					{
						cursorcolor++;
						if (cursorcolor==8)
							cursorcolor = 0;
					}
					
					
					if (!coco.isDisposed())
						coco.redraw();
					
					if (ssmode)
					{
						shell.getDisplay().timerExec(faster, this);
					
					}
					else
						shell.getDisplay().timerExec(25, this);
				}
				
			}
			
		};
		
		shell.getDisplay().timerExec(100, cursor);
		
		
		
		
		
		
		Runnable scroller = new Runnable() 
		{

			int curname = -1;
			int curline = 3;
			boolean wanttodie = false;
			
			public void run()
			{
				while (!shell.isDisposed() && !wanttodie && !ssmode)
				{
					if (curline == 3)
					{
						String[] c = { "DriveWire " + MainWin.DWUIVersion + " (" + MainWin.DWUIVersionDate + ")", 
							"by mobster #3", " " };
					
						
						
						for (int i = 0; i < 3;i++)
						{
							for (int j = 0;j<32;j++)
							{
								if (j >= c[i].length())
									text[i][j] = 32;
								else
									text[i][j] = c[i].toUpperCase().charAt(j);
							}
							
						}
						
						for (int i = 3; i < 16;i++)
						{
							for (int j = 0;j<32;j++)
							{
								text[i][j] = 32;
							}
							
						}
						
					
					}
						
					// type it
					
					if (ssmode)
						return;
					
					String  f = getNextName();
					
					for (int j = 0;j<32;j++)
					{
						if (ssmode)
							return;
						
						if (j >= f.length())
							text[curline][j] = 32;
						else
						{
							curx = j+1;
							cury = curline;
							text[curline][j] = f.toUpperCase().charAt(j);
							
							
							if (curname < 21 && toggles[9])
							try
							{
								Thread.sleep(new Random().nextInt(250)+50);
							} 
							catch (InterruptedException e)
							{
								wanttodie = true;
							}
							
							if ((curname == 7) && (j == 7))
							{
								lockup1();
								
								try
								{
									Thread.sleep(1200);
								} 
								catch (InterruptedException e)
								{
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
								Random r = new Random();
								
								int t = r.nextInt(100);
								for (int i = 0;i < t;i++)
								{
									// favor the weird stuff
									text[r.nextInt(16)][r.nextInt(32)] = r.nextInt(192) + 64;
								}
								
								curx = 32;
								cury = 24;
								
								
								try
								{
									Thread.sleep(1000);
								} 
								catch (InterruptedException e)
								{
									
									e.printStackTrace();
								}
								
								
								scrolltext = true;
								return;
							}
							
						}
					}
					
					if (!shell.isDisposed() && !wanttodie && !ssmode)
					{
						curline++;
						if (curline == 16)
						{
							curline = 15;
							
							int k = 5;
							if ((curname > 21) || (curname < 12))
									k=3;
							
							for (int i = k;i<15;i++)
							{
								for (int j = 0;j<32;j++)
								{
									text[i][j] = text[i+1][j];
								}
							}
							
							for (int i = 0;i<32;i++)
								text[15][i] = 32;
									 
						
						}
					}
					
					curx = 0;
					cury = curline; 
					
					
					
					if (!wanttodie && !ssmode && toggles[9])
					try
					{
						Thread.sleep(1000);
					} catch (InterruptedException e)
					{
						wanttodie = true;
					}
					
				}

			}

			
			private String getNextName()
			{
				curname++;
				if (curname == folks.size())
					curname = 0;
				
				return(folks.get(curname));
			}
			
			
		};
		
		Thread scrT = new Thread(scroller);
		scrT.start();
	
		
	}
	
	

	protected void genCocoimg()
	{
		
		
		if (!shell.isDisposed())
		{
			if (toggles[1])
				cocogc.setTextAntialias(SWT.OFF);
			else
				cocogc.setTextAntialias(SWT.ON);
			
			if (toggles[2])
				cocogc.setAntialias(SWT.OFF);
			else
				cocogc.setAntialias(SWT.ON);
			
			cocogc.setAdvanced(!toggles[3]);
			
			for (int y = 0;y<16;y++)
			{
				for (int x = 0;x<32;x++)
				{
					if ((y == this.cury) && (x == this.curx))
					{
						cocogc.setBackground(curscols[cursorcolor]);
						cocogc.fillRectangle(x*16, y * 24, 15, 24);
						dtext[y][x] = -1;
					}
					else if (text[y][x] != dtext[y][x])
					{
						if (text[y][x] < 128)
						{
							Point p = getCharPoint(text[y][x]);
							cocogc.drawImage(cocotext, p.x, p.y , 16, 24, x*16, y*24, 16, 24);
							
						}
						else
						{
							if (!toggles[4])
							{
								genCoCoChar(text[y][x], x*16, y*24);
							}
							else
							{
								Point p = getGfxPoint(text[y][x]);
								cocogc.drawImage(cocotext2, p.x, p.y , 16, 24, x*16, y*24, 16, 24);
							}
						}
						
						dtext[y][x] = text[y][x];
					}
				}
				
			}
		
		}
		
		
	}

	
	
	private void genCoCoChar(int chr, int x, int y)
	{
		cocogc.setBackground(MainWin.colorBlack);
		cocogc.fillRectangle(x, y, 16, 24);
		
		cocogc.setBackground(curscols[(chr-128) / 16]);
		
		if ((chr & 1) == 1)
			cocogc.fillRectangle(x+8, y+12, 8, 12);
		
		if ((chr & 2) == 2)
			cocogc.fillRectangle(x, y+12, 8, 12);
		
		if ((chr & 4) == 4)
			cocogc.fillRectangle(x+8, y, 8, 12);
		
		if ((chr & 8) == 8)
			cocogc.fillRectangle(x, y, 8, 12);
		
		
	}

	
	
	
	
	private void lockup1()
	{
		Random r = new Random();
		
		boolean backdrop = false;
		
		// backdrop
		if (toggles[5] && (r.nextInt(3)==0))
		{
			backdrop = true;
			int p = r.nextInt(8);
			int k = 0;
			for (int i = 0;i<16;i++)
			{
				for (int j = 0;j<32;j++)
				{
					text[i][j] = mem[p][k];
					k++;
					
				}
			}
		}
		
	
		
		// patterns
		int a = r.nextInt(500);
		
		if (!toggles[6])
			a = 0;
		
		for (int q = 0;q<a;q++)
		{
			int patlen = r.nextInt(36)+1;
			
			int[] pat = new int[patlen];
			
			for (int i = 0;i<patlen;i++)
			{
				int type = r.nextInt(20);
				
				if (type > 10)
				{
					pat[i] = -1;
				}
				else if ((backdrop) && (type<5))
				{
					pat[i] = -1;	
				}
				else
				{
				
					if (type<2)
					{
						// inverse @s are popular
						pat[i] = 96;
					}
					else if (type <3)
					{
						// regular letters
						pat[i] = 65 + r.nextInt(26);
					}
					else if (type<6)
					{
						// some weird gfx char
						pat[i] = 128 + r.nextInt(128);
					}
					else if (type<8)
					{
						// some other inverse char
						pat[i] = 96 + r.nextInt(32);
					}
					else if (type<9)
					{
						// !
						pat[i] = 33;
					}
					else
					{
						// wildcard
						pat[i] = r.nextInt(256);
					}
				}
			}
			
			int ptr = 0;
			
			boolean z = true;
			if (r.nextInt(3) == 0) 
				z = false;
			boolean u = r.nextBoolean();
			
			for (int i = 0;i<16;i++)
			{
				for (int j = 0;j<32;j++)
				{
					if (pat[ptr] > -1)
					{
						if ((r.nextInt(500)<499) && !z)
							text[i][j] = pat[ptr];
						else
							pat[ptr] = text[i][j];
					}
					
					if ((r.nextInt(2000)<1999) || u)
						ptr++;
					
					if (ptr == patlen)
						ptr = 0;
				}
			}
		}
		
		// inc pattern
		if (toggles[7] && (r.nextInt(3) == 0))
		{
			int val = r.nextInt(255)+1;
			int gap = r.nextInt(16)+1;
			
			for (int i = 0;i<512;i += gap)
			{
				text[i / 32][i % 32] = (val + text[i / 32][i % 32]) % 256;
				
			}
			
		}
		
		
		
		
		// random mess on top?
		
		if (toggles[8] && (r.nextInt(10)>5))
		{
			int t = r.nextInt(100);
			for (int i = 0;i < t;i++)
			{
				// favor the weird stuff
				text[r.nextInt(16)][r.nextInt(32)] = r.nextInt(192) + 64;
			}
		}
	
	}
	
	
	

	protected Point getCharPoint(int val)
	{
		Point res = new Point(0,0);
		
		if (val < 32)
			val = 32;
		
		if (val > 63)
			res.y = 24;
		res.x = (val % 64) * 16;
		
		return res;
	}

	protected Point getGfxPoint(int val)
	{
		val = val - 128;
	
		
		
		Point res = new Point(0,0);
		
		res.y = (val / 32) * 24;
		res.x = (val % 32) * 16;
		
		return res;
	}
	

	
	
}
