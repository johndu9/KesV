package com.desukase.engine.polygon;

import com.desukase.engine.Color;
import com.desukase.engine.Point;

/**
 * The basic polygon for anything movable without having to constantly set position, useful stuff
 * @author John Du
 */
public class MovablePolygon extends FirstPolygon{
	
	/** Movement in pixels per second */
	private double speed;
	/** Rotation in radians per second */
	private double rotation;
	/** Whether the polygon can move, useful for pausing */
	protected boolean frozen;
	
	/**
	 * Constructor, makes a polygon
	 * @param points Points that make up the hitbox
	 * @param direction Direction you want it to point
	 * @param position Wherever you want it to be
	 * @param color The color you want it to be
	 * @param speed Movement in pixels per second
	 * @param rotation Rotation in radians per second
	 */
	public MovablePolygon(Point[] points, double direction, Point position, Color color, double speed, double rotation){
		super(points, direction, position, color);
		setSpeed(speed);
		setRotation(rotation);
		setDirection(direction);
	}
	
	public void update(int delta){
		super.update(delta);
	}

	/**
	 * Gets the speed in pixels per second
	 * @return Polygon speed in pixels per second
	 */
	public double getSpeed(){
		return speed;
	}

	/**
	 * Sets the speed in pixels per second 
	 * @param speed Polygon speed in pixels per second
	 */
	public void setSpeed(double speed){
		this.speed = speed;
	}

	/**
	 * Gets the rotation in radians per second
	 * @return Polygon rotation in radians per second
	 */
	public double getRotation(){
		return rotation;
	}

	/**
	 * Sets the rotation in radians per second
	 * @param rotation Polygon rotation in radians per second
	 */
	public void setRotation(double rotation){
		this.rotation = rotation;
	}
	
	/**
	 * Tells you whether the polygon is frozen
	 * @return Whether the polygon is frozen
	 */
	public boolean isFrozen(){
		return frozen;
	}
	
	/**
	 * Sets whether or not the polygon is frozen
	 * @param frozen Whether the polygon is frozen
	 */
	public void setFrozen(boolean frozen){
		this.frozen = frozen;
	}
	
	/**
	 * Moves the polygon with a given speed in pixels per second in a given direction
	 * @param speed Speed in pixels per second to move with
	 * @param direction Direction to move in in radians
	 * @param delta Difference in time between frames
	 */
	public void move(double speed, double direction, int delta){
		if(frozen){
			return;
		}
		setPosition(
			getPosition().x + (float)speed * ((float)delta / 1000) * (float)Math.cos(direction),
			getPosition().y + (float)speed * ((float)delta / 1000) * (float)Math.sin(direction));
	}
	
	/**
	 * Moves the polygon in a given direction based on its speed
	 * @param direction Direction to move in radians
	 * @param delta Difference in time between frames
	 */
	public void move(double direction, int delta){
		move(speed, direction, delta);
	}
	
	/**
	 * Moves the polygon based on its speed and direction
	 * @param delta Difference in time between frames
	 */
	public void move(int delta){
		move(speed, getDirection(), delta);
	}
	
	/**
	 * Turns the polygon with a given rotation in radians per second in a given direction
	 * @param rotation Radians per second to turn with
	 * @param clockwise Whether the polygon should turn clockwise
	 * @param delta Difference in time between frames
	 */
	public void turn(double rotation, boolean clockwise, int delta){
		if(frozen){
			return;
		}
		int i = (clockwise) ? (1) : (-1);
		setDirection(getDirection() + i * (((float)delta / 1000) * rotation));
		
	}
	
	/**
	 * Turns the polygon in a given direction based on its rotation in radians per second
	 * @param clockwise 
	 * @param delta
	 */
	public void turn(boolean clockwise, int delta){
		turn(rotation, clockwise, delta);
	}
	
}