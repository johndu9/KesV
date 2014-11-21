package com.desukase.engine;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.input.Cursor;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

/**
 * This is a mostly convenience class for display-related things
 * @author John Du
 */
public class GameDisplay{

	/** Frames per second */
	private static int framesPerSecond;
	/** Time between frames */
	private static int delta;
	/** Maximum frames per second */
	public static int frameCap;
	/** Allows other classes to set the frame cap */
	public static int setFrameCap;
	/** Width of windowed mode */
	public static int windowedWidth = 800;
	/** Height of windowed mode */
	public static int windowedHeight = 600;
	
	private static int counter = 0;
	private static int accum = 0;

	/**
	 * Initializes the game display and loads in the icons
	 * @param frameCap Maximum frames per second
	 */
	public static void initialize(int frameCap){
		try{
			Display.setDisplayMode(new DisplayMode(windowedWidth, windowedHeight));
			loadIcons();
			Display.create();	
		}catch(Exception e){
			e.printStackTrace();
			System.exit(0);
		}
		setFrameCap = frameCap;
		GameDisplay.frameCap = setFrameCap;
		initializeGL();
		setDelta();
	}
	
	/**
	 * Sets whether the display is fullscreen or windowed
	 * @param fullscreen Fullscreen or window
	 */
	public static void setFullscreen(boolean fullscreen){
		DisplayMode displayMode = (fullscreen) ?
			(Display.getDesktopDisplayMode()) : (new DisplayMode(windowedWidth, windowedHeight));
		try{
			Display.destroy();
			Display.setDisplayMode(displayMode);
			Display.setFullscreen(fullscreen);
			loadIcons();
			Display.create();
			initializeGL();
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * @return The universal delta
	 */
	public static int getDelta(){
		return delta;
	}
	
	/**
	 * Sets the delta from the Timer
	 */
	private static void setDelta(){
		delta = Timer.getDelta();
	}

	/**
	 * Updates the display
	 */
	public static void update(){
		setDelta();
		Display.update();
		Display.sync(frameCap);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		if(Display.isCloseRequested()){
			System.exit(0);
		}
		accum += delta;
		if (accum >= 1000) {
			accum = 0;
			framesPerSecond = counter;
			counter = 0;
		} else {
			counter++;
		}
	}
	
	/**
	 * @return Frames per second
	 */
	public static int getFramesPerSecond() {
		return framesPerSecond;
	}
	
	/**
	 * Sets the title of the window
	 * @param title Window title
	 */
	public static void setTitle(String title){
		Display.setTitle(title);
	}
	
	/**
	 * Sets the title to something useful for debug
	 * @param polygonCount Number of polygons
	 */
	public static void setDebugTitle(){
		String title =
			"FPS: " + framesPerSecond +
			" | TME: " + Timer.getTime() +
			" | MSE: " + "x" + Mouse.getX() + "/" + "y" + (Display.getHeight() - Mouse.getY())
			;
		setTitle(title);
	}
	
	/**
	 * Loads in the icons
	 * @throws IOException
	 */
	private static void loadIcons() throws IOException{
		
		BufferedImage[] iconImages = new BufferedImage[3];
		ByteBuffer[] icons = new ByteBuffer[iconImages.length];
		
		for(int i = 0; i < iconImages.length; i++){
			BufferedImage image =
				ImageIO.read(new File(Data.DIR_PATH + "res" + Data.SEP + "gfx" + Data.SEP + "icon" + i + ".png"));
			byte[] buffer = new byte[image.getWidth() * image.getHeight() * 4];
			int counter = 0;
			for (int j = 0; j < image.getHeight(); j++){
				for (int k = 0; k < image.getWidth(); k++){
					int colorSpace = image.getRGB(k, j);
					buffer[counter + 0] = (byte)((colorSpace << 8) >> 24);
					buffer[counter + 1] = (byte)((colorSpace << 16) >> 24);
					buffer[counter + 2] = (byte)((colorSpace << 24) >> 24);
					buffer[counter + 3] = (byte)(colorSpace >> 24);
					counter += 4;
				}
			}
			icons[i] = ByteBuffer.wrap(buffer);
		}

		Display.setIcon(icons);
		
	}
	
	/**
	 * Do we want the default mouse to stay?
	 * @param showCursor True if yes, false if no
	 */
	public static void showCursor(boolean showCursor){
		try{
			if(showCursor){
				Mouse.setNativeCursor(null);
			}else{
				Mouse.setNativeCursor(new Cursor(1, 1, 0, 0, 1, BufferUtils.createIntBuffer(1), null));
			}
		}catch(LWJGLException e){
			e.printStackTrace();
		}
	}

	/**
	 * Initializes GL stuff
	 */
	private static void initializeGL(){
		GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
    	GL11.glEnable(GL11.GL_BLEND);
    	GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    	GL11.glViewport(0,0,Display.getWidth(),Display.getHeight());
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glOrtho(0, Display.getWidth(), Display.getHeight(), 0, 1, -1);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
	}
	
}