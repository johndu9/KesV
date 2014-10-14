package com.desukase.kesv;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import com.desukase.engine.Color;
import com.desukase.engine.Point;
import com.desukase.engine.polygon.FirstPolygon;

public class GameCursor extends FirstPolygon{
	
	private FirstPolygon outline;

	public GameCursor(){
		super(radiusToPoints(10, 3), 0, new Point(0, 0), Color.EMPTY);
		outline = new FirstPolygon(radiusToPoints(12, 3), 0, new Point(0, 0), Color.EMPTY);
	}
	
	public void update(int delta){
		outline.update(delta);
		outline.setPoints(radiusToPoints(15 / FirstPolygon.getRenderScale().x, 3));
		super.update(delta);
		setPoints(radiusToPoints(10 / FirstPolygon.getRenderScale().x, 3));
		setPosition(
			FirstPolygon.getScreenCenter().x - (Display.getWidth() / 2 - Mouse.getX()) / FirstPolygon.getRenderScale().x,
			FirstPolygon.getScreenCenter().y + (Display.getHeight() / 2 - Mouse.getY()) / FirstPolygon.getRenderScale().y);
		outline.setPosition(getPosition());
	}
	
	public void show(boolean show) {
		if (show) {
			outline.setColor(Game.FOREGROUND);
			setColor(Shepherd.CHOSEN);
		} else {
			outline.setColor(Color.EMPTY);
			setColor(Color.EMPTY);
		}
	}
	
}