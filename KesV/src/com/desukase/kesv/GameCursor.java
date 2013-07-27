package com.desukase.kesv;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import com.desukase.engine.Color;
import com.desukase.engine.Point;
import com.desukase.engine.polygon.FirstPolygon;

public class GameCursor extends FirstPolygon{

	public GameCursor(){
		super(sizeToPoints(10), 0, new Point(0, 0), Color.EMPTY);
	}
	
	public void update(int delta){
		super.update(delta);
		setPosition(
			FirstPolygon.getScreenCenter().x - (Display.getWidth() / 2 - Mouse.getX()) / FirstPolygon.getRenderScale().x,
			FirstPolygon.getScreenCenter().y + (Display.getHeight() / 2 - Mouse.getY()) / FirstPolygon.getRenderScale().y);
	}
	
}