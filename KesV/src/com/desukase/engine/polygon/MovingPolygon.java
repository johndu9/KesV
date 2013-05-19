package com.desukase.engine.polygon;

import com.desukase.engine.Color;
import com.desukase.engine.Point;

/**
 * Polygon that constantly moves during its updates
 * @author John Du
 */
public class MovingPolygon extends MovablePolygon{

	/**
	 * Constructor, makes a moving polygon
	 * @param points Points that make up the hitbox
	 * @param direction Direction you want it to point
	 * @param position Wherever you want it to be
	 * @param color The color you want it to be
	 * @param speed Movement in pixels per second
	 * @param rotation Rotation in radians per second
	 */
	public MovingPolygon(Point[] points, double direction, Point position, Color color, double speed, double rotation){
		super(points, direction, position, color, speed, rotation);
	}
	
	public void update(int delta){
		super.update(delta);
		move(getDirection(), delta);
		turn(true, delta);
	}
	
}