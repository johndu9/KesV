package com.desukase.kesv;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import com.desukase.engine.Color;
import com.desukase.engine.Point;
import com.desukase.engine.Toggle;
import com.desukase.engine.polygon.FirstPolygon;

public class GameCursor extends FirstPolygon{
	
	private FirstPolygon outline;
	private FirstPolygon selection;
	private Point selectPoint = new Point(0, 0);
	private Toggle selecting = new Toggle(false);

	public GameCursor(){
		super(radiusToPoints(10, 3), 0, new Point(0, 0), Color.EMPTY);
		outline = new FirstPolygon(radiusToPoints(12, 3), 0, new Point(0, 0), Color.EMPTY);
		selection = new FirstPolygon(sizeToPoints(0), 0, new Point(0, 0), Color.EMPTY);
	}
	
	public void update(int delta){
		boolean wasDown = selecting.getState();
		selecting.update(Mouse.isButtonDown(0));
		if (Mouse.isButtonDown(0)) {
			if (wasDown != selecting.getState()) {
				selectPoint = getPosition();
			}
			selection.setColor(Game.FOREGROUND);
			float deltaX = getPosition().x - selectPoint.x;
			float deltaY = getPosition().y - selectPoint.y;
			selection.setPoints(sizeToPoints(deltaX, deltaY));
			selection.setPosition(selectPoint.x + deltaX / 2, selectPoint.y + deltaY / 2);
			selection.update(delta);
		} else {
			selection.setColor(Color.EMPTY);
		}
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
	
	public boolean isSelected(Point point) {
		if (selecting.getState()) {
			return selection.isInPolygon(point);
		}
		return false;
	}
	
	public boolean isSelected(FirstPolygon polygon) {
		if (selecting.getState()) {
			return selection.hits(polygon);
		}
		return false;
	}
	
}