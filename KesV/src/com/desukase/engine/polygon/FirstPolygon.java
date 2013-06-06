package com.desukase.engine.polygon;

import java.awt.*;
import java.awt.geom.Area;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

import com.desukase.engine.Color;
import com.desukase.engine.Point;
import com.desukase.engine.Timer;

/**
 * The base for everything to be rendered, updated, entities, etc. in this engine
 * @author John Du
 */
public class FirstPolygon{
	
	/** Points that make up the hitbox */
	private Point[] points;
	/** The height */
	private float height;
	/** The width */
	private float width;
	/** Direction you want it to point */
	private double direction;
	/** Wherever you want it to be */
	private Point position;
	/** Wherever the polygon was born */
	private Point origin;
	/** The color you want it to be */
	private Color color;
	/** Used for calculating collision */
	private Polygon hitbox = new Polygon();
	
	/** Initial render scale */
	private static final Point BASE_RENDER_SCALE = new Point(1.0f, 1.0f);
	/** Scales everything rendered */
	protected static Point renderScale = new Point(BASE_RENDER_SCALE);
	/** Initial render position */
	private static final Point BASE_RENDER_POSITION = new Point(0.0f, 0.0f);
	/** Everything is rendered relative to this point*/
	protected static Point renderPosition = new Point(BASE_RENDER_POSITION);
	
	/** The preferred debugging color */
	public static final Color DEBUG_COLOR = new Color(1.0f, 0.0f, 0.0f, 0.5f);
	/** The preferred debugging point size */
	public static final float DEBUG_POINT_SIZE = 10.0f;
	/** The preferred debugging line width */
	public static final float DEBUG_LINE_WIDTH = 5.0f;
	/** Empty polygon */
	public static final FirstPolygon EMPTY = new FirstPolygon(sizeToPoints(0), 0, new Point(0, 0), new Color(0, 0, 0, 0));
	/** The longest line on the screen */
	public static final float DISPLAY_DIAGONAL =
		(float)Math.sqrt(Math.pow(Display.getWidth(), 2) + Math.pow(Display.getHeight(), 2));

	/**
	 * Constructor, makes a polygon
	 * @param points Points that make up the hitbox
	 * @param direction Direction you want it to point
	 * @param position Wherever you want it to be
	 * @param color The color you want it to be
	 */
	public FirstPolygon(Point[] points, double direction, Point position, Color color){
		setPoints(points);
		setDirection(direction);
		setPosition(position);
		setColor(color);
		setHitbox();
		origin = new Point(position);
	}
	
	/**
	 * Constructor, copies a polygon
	 * @param polygon Polygon to copy
	 */
	public FirstPolygon(FirstPolygon polygon) {
		this(polygon.points, polygon.direction, new Point(polygon.position), new Color(polygon.color));
		Point[] newPoints = new Point[polygon.points.length];
		for(int i = 0; i < newPoints.length; i++){
			newPoints[i] = new Point(polygon.points[i]);
		}
		setPoints(newPoints);
	}

	/**
	 * @param x The x coordinate
	 * @param y The y coordinate
	 * @return Whether the point is in the polygon
	 */
	public boolean isInPolygon(float x, float y){		
		return hitbox.contains(x, y);
	}
	
	/**
	 * @param point The point to check
	 * @return Whether the point is in the polygon
	 */
	public boolean isInPolygon(Point point){
		return isInPolygon(point.x, point.y);
	}
	
	/**
	 * @param other The other polygon to check against
	 * @return Whether the two polygons intersect
	 */
	public boolean hits(FirstPolygon other){
		Area thisArea = new Area(hitbox);
		Area thatArea = new Area(other.hitbox);
		thisArea.intersect(thatArea);
		return !thisArea.isEmpty();
	}
	
	/**
	 * Updates the polygon's hitbox and renders it; use it for subclasses, too
	 * @param delta Time between frames in milliseconds
	 */
	public void update(int delta){		
		setHitbox();
		if(color.getAlpha() > 0 && Display.isVisible()){
			render();
		}
	}
	
	/**
	 * @return Calculated height of the polygon
	 */
	public float getHeight(){
		return height;
	}
	
	/**
	 * Calculates the height
	 */
	private void setHeight(){
		height = difmaxmin(distillPoints(points, false));
	}
	
	/**
	 * @return Calculated width of the polygon
	 */
	public float getWidth(){
		return width;
	}
	
	/**
	 * Calculates the width
	 */
	private void setWidth(){
		width = difmaxmin(distillPoints(points, true));
	}
	
	/**
	 * @return Points that make up the hitbox
	 */
	public Point[] getPoints(){		
		return points;
	}
	
	/**
	 * Sets the points that make up the hitbox
	 * @param points The points that make up the hitbox
	 */
	public void setPoints(Point[] points){		
		this.points = points;
		setHeight();
		setWidth();
	}

	/**
	 * @return The direction it's pointing
	 */
	public double getDirection(){		
		return direction;
	}

	/**
	 * Sets the direction the polygon is pointing
	 * @param direction Direction you want it to point
	 */
	public void setDirection(double direction){		
		this.direction = direction;
	}
	
	/**
	 * @return Wherever the polygon was constructed
	 */
	public Point getOrigin(){
		return origin;
	}
	
	/**
	 * @return The polygon's current position
	 */
	public Point getPosition(){		
		return position;
	}

	/**
	 * Sets the polygon's current position
	 * @param position Wherever you want it to be
	 */
	public void setPosition(Point position){		
		this.position = position;
	}

	/**
	 * Sets the polygon's current position
	 * @param x The x of where you want it to be
	 * @param y The y of where you want it to be
	 */
	public void setPosition(float x, float y){		
		setPosition(new Point(x, y));
	}

	/**
	 * @return The color of the polygon
	 */
	public Color getColor(){		
		return color;
	}

	/**
	 * Sets the color of the polygon
	 * @param color The color you want it to be
	 */
	public void setColor(Color color){		
		this.color = color;
	}
	
	public void scale(float scale){
		Point[] newPoints = new Point[points.length];
		for(int i = 0; i < points.length; i++){
			newPoints[i] = points[i].scalePoint(scale, scale);
		}
		setPoints(newPoints);
	}
	
	/**
	 * Sets the hitbox automatically based on its position in the world
	 */
	private void setHitbox(){
		int[] xPoints = new int[points.length];
		int[] yPoints = new int[points.length];
		for(int i = 0; i < points.length; i++){
			xPoints[i] =
				(int)(position.x +
				getPoints()[i].x * Math.cos(direction) - getPoints()[i].y * Math.sin(direction));
			yPoints[i] =
				Display.getHeight() - (int)(position.y +
				getPoints()[i].y * Math.cos(direction) + getPoints()[i].x * Math.sin(direction));
		}
		hitbox = (new Polygon(xPoints, yPoints, points.length));
	}

	/**
	 * @param width The width of the rectangle
	 * @param height The height of the rectangle
	 * @return An array of points that make up a rectangle of a certain width and height
	 */
	public static Point[] sizeToPoints(float width, float height){
		return new Point[]{
			new Point(-width / 2, -height / 2),
			new Point(-width / 2, height / 2),
			new Point(width / 2, height / 2),
			new Point(width / 2, -height / 2)};
	}

	/**
	 * @param size The width and height of the square
	 * @return An array of points that make up a square of a certain width and height
	 */
	public static Point[] sizeToPoints(float size){
		return sizeToPoints(size, size);
	}
	
	/**
	 * @param radius Radius of the polygon
	 * @param vertices Vertices count
	 * @return Equilateral polygon with the given vertices and a given radius
	 */
	public static Point[] radiusToPoints(float radius, int vertices){
		Point[] points = new Point[vertices];
		for(int i = 0; i < points.length; i++){
			double direction = ((2 * Math.PI) / points.length) * (i + 1);
			points[i] = new Point(
				(float)Math.cos(direction) * radius,
				(float)Math.sin(direction) * radius);
		}
		return points;
	}
	
	/**
	 * Renders specific debug information
	 */
	public void renderDebug(){
		if(canRender()){
			renderHitbox();
			renderDirection();
			renderPosition();
//			renderCoordinates();
		}
	}
	
	/**
	 * Renders the hitbox
	 */
	public void renderHitbox(){
		renderPolygon(points, direction, position, DEBUG_COLOR, false, false);
	}
	
	/**
	 * Renders the direction
	 */
	public void renderDirection(){
		renderLine(DISPLAY_DIAGONAL, DEBUG_LINE_WIDTH, direction, position, DEBUG_COLOR, false, false);
	}
	
	/**
	 * Renders the position
	 */
	public void renderPosition(){
		renderPolygon(sizeToPoints(DEBUG_POINT_SIZE), 0, position, DEBUG_COLOR, false, false);
	}
	
	/**
	 * Renders the coordinates
	 */
	public void renderCoordinates(){
		renderLine(Display.getHeight(), DEBUG_LINE_WIDTH, Math.PI / 2, new Point(position.x, 0), DEBUG_COLOR, false, false);
		renderLine(Display.getWidth(), DEBUG_LINE_WIDTH, 0, new Point(0, position.y), DEBUG_COLOR, false, false);
	}
	
	/**
	 * Renders the polygon
	 */
	public void render(){
		if(canRender()){
			renderPolygon(points, direction, position, color, true, true);	
		}
	}
	
	/**
	 * @return Whether or not the polygon is actually in the screen
	 */
	public boolean canRender(){
		return !position.scalePoint(renderScale).outBounds(
			(-renderPosition.x - width) * (renderScale.x),
			(-renderPosition.x + width) * (renderScale.x) + Display.getWidth(),
			(-renderPosition.y - height) * (renderScale.y),
			(-renderPosition.y + height) * (renderScale.y) + Display.getHeight()
		);
	}
	
	/**
	 * @return Scale applied to rendering
	 */
	public static Point getRenderScale(){
		return renderScale;
	}
	
	/**
	 * Sets the scale applied to rendering
	 * @param newRenderScale The new scale
	 */
	public static void setRenderScale(Point newRenderScale){
		renderScale = newRenderScale;
	}
	
	/**
	 * Sets the scale applied to rendering
	 * @param x The new x scale
	 * @param y The new y scale
	 */
	public static void setRenderScale(float x, float y){
		renderScale = new Point(x, y);
	}

	/**
	 * Sets the scale applied to rendering
	 * @param scale The new scale
	 */
	public static void setRenderScale(float scale){
		renderScale = new Point(scale, scale);
	}
	
	/**
	 * @return The point relative to which everything is rendered
	 */
	public static Point getRenderPosition(){		
		return renderPosition;
	}

	/**
	 * Sets the point relative to which everything is rendered
	 * @param newRenderPosition The new rendering position
	 */
	public static void setRenderPosition(Point newRenderPosition){		
		renderPosition = newRenderPosition;
	}

	/**
	 * Sets the point relative to which everything is rendered
	 * @param x The new x
	 * @param y The new y
	 */
	public static void setRenderPosition(float x, float y){		
		setRenderPosition(new Point(x, y));
	}
	
	/**
	 * Renders a line
	 * @param length The length of the line
	 * @param width The width of the line
	 * @param direction The direction of the line in radians
	 * @param position The position of the beginning of the line
	 * @param color The color of the line
	 * @param relative Whether you want to apply the relative position to rendering
	 * @param scale Whether you want to apply the scaling to rendering
	 */
	public static void renderLine(
		float length, float width, double direction, Point position, Color color, boolean relative, boolean scale){
		Point newPosition = Point.add(position,
			new Point((float)((length / 2) * Math.cos(direction)), (float)(length / 2 * Math.sin(direction))));
		renderPolygon(sizeToPoints(length, width), direction, newPosition, color, relative, scale);
	}
	
	/**
	 * Renders a polygon
	 * @param points The points making up the polygon
	 * @param direction The direction of the polygon in radians
	 * @param position The position of the center of the polygon
	 * @param color The color of the polygon
	 * @param relative Whether you want to apply the relative position to rendering
	 * @param scale Whether you want to apply the scaling to rendering
	 */
	public static void renderPolygon(
		Point[] points, double direction, Point position, Color color, boolean relative, boolean scale){
		Point renderPosition =
			(relative) ? (FirstPolygon.renderPosition) : (BASE_RENDER_POSITION);
		Point renderScale =
			(scale) ? (FirstPolygon.renderScale) : (BASE_RENDER_SCALE);
		Point translatePoint = Point.add(renderPosition, position).scalePoint(renderScale);
		Point[] renderPoints = new Point[points.length];
		for(int i = 0; i < renderPoints.length; i++){
			renderPoints[i] = Point.add(Point.add(renderPosition, position), points[i]).scalePoint(renderScale);
		}
		GL11.glPushMatrix();
			GL11.glColor4f(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
			GL11.glTranslatef(translatePoint.x, translatePoint.y, 0);
			GL11.glRotatef((float)Math.toDegrees(direction), 0.0f, 0.0f, 1.0f);
			GL11.glTranslatef(-translatePoint.x, -translatePoint.y, 0);
			GL11.glBegin(GL11.GL_POLYGON);
				for(int i = 0; i < points.length; i++){							
					GL11.glVertex2f(renderPoints[i].x, renderPoints[i].y);
				}
			GL11.glEnd();
		GL11.glPopMatrix();
	}
	
	/**
	 * @return The point at the center of the screen
	 */
	public static Point getScreenCenter(){
		return Point.subtract(
			new Point(Display.getWidth() / (2 * renderScale.x),Display.getHeight() / (2 * renderScale.y)),
			renderPosition);
	}
	
	/**
	 * Scrolls the render position to a given point at a given speed
	 * @param point Point to scroll to
	 * @param speed Weird constant number for scrolling
	 */
	public static void scrollRenderPosition(Point point, double speed){
		Point oldPos = FirstPolygon.getRenderPosition();
		double direction = oldPos.directionTo(point);
		double multiplier = oldPos.distanceTo(point) / (speed / Timer.getTimerSpeed());
		Point newPos = new Point(
			oldPos.x + (float)(Math.cos(direction) * multiplier),
			oldPos.y + (float)(Math.sin(direction) * multiplier));
		FirstPolygon.setRenderPosition(newPos);
	}
	
	/**
	 * Centers the screen on a polygon
	 * @param polygon Polygon to center on
	 */
	public static void centerOnPolygon(FirstPolygon polygon){
		setRenderPosition(
			Point.subtract(
				new Point(Display.getWidth()  / 2, Display.getHeight() / 2),
				polygon.getPosition().scalePoint(renderScale)).scalePoint(1 / renderScale.x, 1 / renderScale.y)
			);
	}
	
	/**
	 * Scrolls ("soft center") to a polygon
	 * @param polygon Polygon to scroll to
	 * @param speed Weird constant number for scrolling
	 */
	public static void softCenterOnPolygon(FirstPolygon polygon, double speed){
		scrollRenderPosition(
			Point.subtract(
				new Point(Display.getWidth()  / 2, Display.getHeight() / 2),
				polygon.getPosition().scalePoint(renderScale)).scalePoint(1 / renderScale.x, 1 / renderScale.y), speed
			);
	}
	
	/**
	 * Zooms; decreases/increases the render scale
	 * @param in Whether we want to zoom in or out
	 * @param speed Speed at which we zoom
	 */
	public static void zoom(boolean in, double speed){
		int direction = (in) ? (1) : (-1);
		FirstPolygon.getRenderScale().x =
			(float)(FirstPolygon.getRenderScale().x + direction * Math.pow(FirstPolygon.getRenderScale().x / 2, speed));
		FirstPolygon.getRenderScale().y = 
			(float)(FirstPolygon.getRenderScale().y + direction * Math.pow(FirstPolygon.getRenderScale().y / 2, speed));
	}
	
	/**
	 * @param points The points to be distilled
	 * @param xOrY Whether we want the x values or the y values
	 * @return The x or y values of a bunch of points
	 */
	private static float[] distillPoints(Point[] points, boolean xOrY){
		float[] values = new float[points.length];
		for(int i = 0; i < values.length; i++){
			values[i] = (xOrY) ? (points[i].x) : (points[i].y); 
		}
		return values;
	}
	
	/**
	 * @param numbers The numbers to process
	 * @return The difference between the highest and lowest values in the set
	 */
	private static float difmaxmin(float[] numbers){		
		float max = 0;
		float min = 0;
		for(int i = 0; i < numbers.length; i++){
			if(numbers[i] > max) max = numbers[i];
			if(numbers[i] < min) min = numbers[i];
		}
		return max - min;
	}
	
}