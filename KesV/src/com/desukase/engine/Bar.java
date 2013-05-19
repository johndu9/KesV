package com.desukase.engine;

import org.lwjgl.input.Mouse;

import com.desukase.engine.polygon.FirstPolygon;

/**
 * Very simple bar, can be used for settings sort of stuff as well as displaying information.
 * @author John Du
 */
public class Bar extends FirstPolygon{

	/** The value in [0.0, 1.0], shows the fullness of the bar */
	private float value;
	/** The bar that is displayed, represents the value */
	private FirstPolygon foreground;
	
	/**
	 * Constructor, makes a bar
	 * @param x Length of the bar
	 * @param y Height of the bar
	 * @param direction Direction you want it to point
	 * @param position Wherever you want it to be
	 * @param value The value in [0.0, 1.0], shows the fullness of the bar
	 * @param frontColor The color of the front part
	 * @param backColor The color of the back part
	 */
	public Bar(float x, float y, double direction, Point position, float value, Color frontColor, Color backColor){
		super(sizeToPoints(x, y), direction, new Point(position.x, position.y), backColor);
		int foregroundWidth = (int)(getWidth() * value);
		foreground =
			new FirstPolygon(sizeToPoints(foregroundWidth, y), direction,
				new Point(getPosition().x - (getWidth() - foregroundWidth) / 2, getPosition().y), frontColor);
		setValue(value);
	}
	
	public void update(int delta){
		super.update(delta);
		foreground.update(delta);
		foreground.setPoints(sizeToPoints((int)(getWidth() * value), getHeight()));
		foreground.setPosition(
			getPosition().x - (getWidth() - foreground.getWidth()) / 2, getPosition().y);
	}
	
	/**
	 * @return The value of the bar, [0.0, 1.0]
	 */
	public float getValue(){
		return value;
	}
	
	/**
	 * @param value The value to set, [0.0, 1.0]
	 */
	public void setValue(float value){
		this.value = value;
	}
	
	/**
	 * Allows the value to be set by mouse
	 */
	public void setValue(){
		if(isInPolygon(Mouse.getX(), Mouse.getY()) && Mouse.isButtonDown(0)){
			setValue((Mouse.getX() - getPosition().x - getPoints()[0].x) / (getWidth()));
		}
	}
	
	/**
	 * @return Number of polygons
	 */
	public int size(){
		return 2;
	}
	
	public void render(){
		super.render();
		foreground.render();
	}
	
}