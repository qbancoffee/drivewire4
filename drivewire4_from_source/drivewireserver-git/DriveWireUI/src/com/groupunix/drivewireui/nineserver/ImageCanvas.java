package com.groupunix.drivewireui.nineserver;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.opengl.GLCanvas;
import org.eclipse.swt.opengl.GLData;
import org.eclipse.swt.widgets.Composite;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;

class ImageCanvas extends GLCanvas {
	public ImageCanvas(Composite parent, int style, GLData data) {
		super(parent, style, data);
		// TODO Auto-generated constructor stub
	}

	private final Point imagePosition = new Point(0, 0);
	private ImageData bufferedImage = null;
	private ByteBuffer buffer = null;
	private int w = 0;
	private int h = 0;

	public void setBufferedImage(BufferedImage result) 
	{
		
		// ImageHelper.toBufferedImage(bufferedImage);//.getScaledInstance(50, 50, java.awt.Image.SCALE_DEFAULT));
		
		w = result.getWidth();
		h = result.getHeight();
		buffer = ByteBuffer.allocateDirect(w * h * 4).order(ByteOrder.nativeOrder());

		
		buffer.put( ((DataBufferByte)(result.getData().getDataBuffer())).getData()   );
		buffer.rewind();
	}

	@SuppressWarnings("unused")
	private void updateImageBuffer() {
		// ImageData fullImageData = makeTransparent(bufferedImage, 0);
		System.out.println("1");
		BufferedImage bi = NineServerUtils.convertToAWT(bufferedImage);
		System.out.println("2");
		BufferedImage result = new BufferedImage(bi.getWidth(), bi.getHeight(), BufferedImage.TYPE_INT_ARGB);
		
		/*
		System.out.println("3");
		Graphics2D g = result.createGraphics();
		g.scale(1, -1);
		g.drawImage(bi, 0, -bi.getHeight(), null);
		g.dispose();
		*/
		
		System.out.println("3a");
		WritableRaster wr = result.getData().createCompatibleWritableRaster();
		System.out.println("3b");
		int arx[] = new int[4];
		int zero[] = new int[4];
		
		for (int x = 0; x < result.getWidth(); x++) {
			for (int y = 0; y < result.getHeight(); y++) {
				
				System.out.println("at " + x + "," + y);
				
				
				zero = result.getData().getPixel(x, y, arx);
				arx[0] = zero[2];
				arx[1] = zero[1];
				arx[2] = zero[0];
				// arx[3]=255;
				wr.setPixel(x, y, arx);
			}
		}
		System.out.println("4");
		result = new BufferedImage(result.getColorModel(), wr, result.isAlphaPremultiplied(), null);
		
		Raster r = result.getData();
		DataBuffer db = r.getDataBuffer();
		DataBufferInt dataBufferInt = (DataBufferInt) db;
		System.out.println("5");
		//buffer.put(dataBufferInt.getData());
		buffer.rewind();
	}

	public void setImagePosition(int x, int y) {
		imagePosition.x = x;
		imagePosition.y = y;
	}

	public void render() {
		setCurrent();
		try {
			GLContext.useContext(this);
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
		
		
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glOrtho(0, getBounds().width, 0, getBounds().width, -2, 2);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();

		GL11.glClearColor(255, 255, 255, 255);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

	
		int format = GL11.GL_RGB8;
		int type = GL11.GL_UNSIGNED_BYTE;
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		GL11.glRasterPos4i(imagePosition.x, imagePosition.y, 0, 1);
		GL11.glDrawPixels(w, h, format, type, buffer);
		
		swapBuffers();
		
		
		
	}
}