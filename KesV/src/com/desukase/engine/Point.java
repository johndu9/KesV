package com.desukase.engine;

/**
 * This is a simple convenience class that stores x and y coordinates and does some math with them
 * Points can find direction to another point, distance between two points, and scale itself
 * Points can also be converted: "absolute" (where 0, 0 is the top left corner), "center" (where 0, 0 is wherever center is)
 * 
 * @author John Du
 */
public class Point{
	
	/** These are the x and y coordinates */
	public float x, y;
	/** This is the relative center used in converting between absolute and center */
	public static Point center = new Point(0, 0);
	
	/**
	 * Constructor, makes a new point
	 * @param x The x coordinate
	 * @param y The y coordinate
	 */
	public Point(float x, float y){
		this.x = x;
		this.y = y;
	}

	/**
	 * Constructor, makes a point identical to point
	 * @param point The point to be copied
	 */
	public Point(Point point) {
		this.x = point.x;
		this.y = point.y;
	}
	
	/**
	 * Gives the direction from this point to the other point in radians
	 * @param other The other point
	 * @return Direction to the other point in radians
	 */
	public double directionTo(Point other){
		double direction;
		
		if(other.x == x && other.y > y){
			return Math.PI / 2;
		}else if(other.x == x && other.y < y){
			return -Math.PI / 2;
		}
		
		direction = -Math.atan(Math.abs(y - other.y) / (x - other.x));
		if(other.x < x){
			direction += Math.PI;
		}
		if(other.y < y){
			direction = 2 * Math.PI - direction;
		}
		if(Double.isNaN(direction)){
			return 0;
		}
		return direction;
	}

	/**
	 * Gives the direction from this point to the other point in radians
	 * @param x The other point's x coordinate
	 * @param y The other point's y coordinate
	 * @return Direction to the other point in radians
	 */
	public double directionTo(float x, float y){
		return directionTo(new Point(x, y));
	}
	
	/**
	 * Gives the distance between this point and the other point
	 * @param other The other point
	 * @return Distance to the other point
	 */
	public double distanceTo(Point other){
		double distance = Math.sqrt(Math.pow((other.x - x), 2) + Math.pow((other.y - y), 2));
		if(Double.isNaN(distance)){
			return 0;
		}
		return distance;
	}

	/**
	 * Gives the distance between this point and the other point
	 * @param x The other point's x coordinate
	 * @param y The other point's y coordinate
	 * @return Distance to the other point
	 */
	public double distanceTo(float x, float y){
		return distanceTo(new Point(x, y));
	}
	
	/**
	 * Scales the x and y according to multipliers, useful for stretching
	 * @param xScale The multiplier affecting the x coordinate
	 * @param yScale The multiplier affecting the y coordinate
	 * @return Scaled point
	 */
	public Point scalePoint(float xScale, float yScale){
		return new Point(x * xScale, y * yScale);
	}
	
	/**
	 * Scales the x and y according to multipliers, useful for stretching
	 * @param scale The multiplier affecting both scales
	 * @return Scaled point
	 */
	public Point scalePoint(Point scale){
		return scalePoint(scale.x, scale.y);
	}
	
	/**
	 * Gets a polar point given the parameters
	 * @param origin The "0, 0" point with which the returned point is relative to
	 * @param magnitude Distance from the origin
	 * @param angle Angle from the origin
	 * @return Point with given magnitude and angle from the origin
	 */
	public static Point getPolar(Point origin, float magnitude, float angle){
		return Point.add(origin, new Point(magnitude * (float)Math.cos(angle), -magnitude * (float)Math.sin(angle)));
	}
	
	/**
	 * Gets a polar point given the parameters
	 * @param magnitude Distance from (0, 0)
	 * @param angle Angle from (0, 0)
	 * @return Point with given magnitude and angle from (0, 0)
	 */
	public static Point getPolar(float magnitude, float angle){
		return getPolar(new Point(0, 0), magnitude, angle);
	}
	
	/**
	 * Keeps a direction within the domain of 0 to 2pi
	 * @param direction Direction to be wrapped
	 * @return Wrapped direction
	 */
	public static double wrapDirection(double direction){
		double newDirection = direction;
		if(direction < -2 * Math.PI || direction > 2 * Math.PI){
			newDirection = direction - (int)(direction / (2 * Math.PI)) * (2 * Math.PI);
		}
		return newDirection;
	}
	
	/**
	 * Subtracts point two from point one
	 * @param one The point subtracted from
	 * @param two The subtracted point
	 * @return Point one - Point two
	 */
	public static Point subtract(Point one, Point two){
		return new Point(one.x - two.x, one.y - two.y);
	}
	
	/**
	 * Adds point one and point two
	 * @param one Point added
	 * @param two Point added
	 * @return Point one + Point two
	 */
	public static Point add(Point one, Point two){
		return new Point(one.x + two.x, one.y + two.y);
	}

	/**
	 * Keeps the polygon in the bounds by moving it to the other side of the bounds
	 * @param minX Will set polygon's x to this if polygon's x is greater than maxX
	 * @param maxX Will set polygon's x to this if polygon's x is less than minX
	 * @param minY Will set polygon's y to this if polygon's y is greater than maxX
	 * @param maxY Will set polygon's y to this if polygon's y is less than minX
	 */
	public void wrapBounds(float minX, float maxX, float minY, float maxY){		
		if(x < minX) x = maxX;
		if(x > maxX) x = minX;
		if(y < minY) y = maxY;
		if(y > maxY) y = minY;
	}
	
	/**
	 * Keeps the polygon in the bounds by moving it to the other side of the bounds
	 * @param bounds The bounds by which you want to wrap the polygon
	 */
	public void wrapBounds(float[] bounds){
		if(bounds.length != 4){
			return;
		}
		wrapBounds(bounds[0], bounds[1], bounds[2], bounds[3]);
	}

	/**
	 * Keeps the polygon in the bounds by keeping it from moving outside of the bounds
	 * @param minX Will set polygon's x to this if polygon's x is less than minX
	 * @param maxX Will set polygon's x to this if polygon's x is greater than maxX
	 * @param minY Will set polygon's y to this if polygon's y is less than minY
	 * @param maxY Will set polygon's y to this if polygon's y is greater than maxX
	 */
	public void confineBounds(float minX, float maxX, float minY, float maxY){		
		if(x < minX) x = minX;
		if(x > maxX) x = maxX;
		if(y < minY) y = minY;
		if(y > maxY) y = maxY;
	}
	
	/**
	 * Keeps the polygon in the bounds by keeping it inside the bounds
	 * @param bounds The bounds by which you want to confine the polygon
	 */
	public void confineBounds(float[] bounds){
		if(bounds.length != 4){
			return;
		}
		confineBounds(bounds[0], bounds[1], bounds[2], bounds[3]);
	}
	
	/**
	 * @param minX Minimum x of the bounds, the left-most x
	 * @param maxX Maximum x of the bounds, the right-most x
	 * @param minY Minimum y of the bounds, the top-most y
	 * @param maxY Maximum y of the bounds, the bottom-most y
	 * @return Polygon's x is not between minX and maxX or polygon's y is not between minY and maxY
	 */
	public boolean outBounds(float minX, float maxX, float minY, float maxY){		
		return x < minX || x > maxX || y < minY || y > maxY;
	}
	
	/**
	 * @param bounds The bounds by which you want to check the polygon
	 * @return Polygon's x is not between minX and maxX or polygon's y is not between minY and maxY
	 */
	public boolean outBounds(float[] bounds){
		if(bounds.length != 4){
			return false;
		}
		return outBounds(bounds[0], bounds[1], bounds[2], bounds[3]);
	}
	
	/**
	 * Converts a point that is "center" (where 0, 0 is wherever center is) to "absolute" (where 0, 0 is the top left corner)
	 * @param point The point to be converted
	 * @return The converted point
	 */
	public static Point convertToAbsolute(Point point){
		return new Point(center.x + point.x, center.y - point.y);
	}

	/**
	 * Converts a point that is "center" (where 0, 0 is wherever center is) to "absolute" (where 0, 0 is the top left corner)
	 * @param x The x coordinate of the point to be converted
	 * @param y The y coordinate of the point to be converted
	 * @return The converted point
	 */
	public static Point convertToAbsolute(float x, float y){
		return convertToAbsolute(new Point(x, y));
	}

	/**
	 * Converts a point that is "absolute" (where 0, 0 is the top left corner) to "center" (where 0, 0 is wherever center is)
	 * @param point The point to be converted
	 * @return The converted point
	 */
	public static Point convertToCenter(Point point){
		return new Point(point.x - center.x, center.y - point.y);
	}

	/**
	 * Converts a point that is "absolute" (where 0, 0 is the top left corner) to "center" (where 0, 0 is wherever center is)
	 * @param x The x coordinate of the point to be converted
	 * @param y The y coordinate of the point to be converted
	 * @return The converted point
	 */
	public static Point convertToCenter(float x, float y) {
		return convertToCenter(new Point(x, y));
	}
	
	public boolean equals(float x, float y){
		return this.x == x && this.y == y;
	}
	
	public boolean equals(Object o){
		if(this == o){
			return true;
		}
		if(o == null || getClass() != o.getClass()){
			return false;
		}

		Point other = (Point)o;
		if(Float.compare(other.x, x) != 0){
			return false;
		}
		if(Float.compare(other.y, y) != 0){
			return false;
		}

		return true;
	}
	
	public String toString(){
		return "(" + x + "," + y + ")";
	}
	
}