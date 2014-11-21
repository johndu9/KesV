package com.desukase.kesv;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import com.desukase.engine.Color;
import com.desukase.engine.Point;
import com.desukase.engine.Toggle;
import com.desukase.engine.polygon.FirstPolygon;

public class GameCursor extends FirstPolygon{
	
	private FirstPolygon selection;
	private Point selectPoint = new Point(0, 0);
	private Toggle selecting = new Toggle(false);
	private static Color selectOutline = new Color(1.0f, 0.8f, 0.4f, 1.0f);

	public GameCursor(){
		super(radiusToPoints(10, 3), 0, new Point(0, 0), Color.EMPTY);
		selection = new FirstPolygon(sizeToPoints(0), 0, new Point(0, 0), Game.FOREGROUND);
	}
	
	public void update(int delta){
		boolean wasDown = selecting.getState();
		selecting.update(Mouse.isButtonDown(0));
		if (Mouse.isButtonDown(0)) {
			if (wasDown != selecting.getState()) {
				selectPoint = getPosition();
			}
			float deltaX = getPosition().x - selectPoint.x;
			float deltaY = getPosition().y - selectPoint.y;
			selection.setPoints(sizeToPoints(deltaX, deltaY));
			selection.setPosition(selectPoint.x + deltaX / 2, selectPoint.y + deltaY / 2);
			selection.renderOutline(2 / FirstPolygon.getRenderScale().x, selectOutline);
			selection.update(delta);
		} else {
			selection.setPoints(new Point[]{});
		}
		super.update(delta);
		setPoints(radiusToPoints(10 / FirstPolygon.getRenderScale().x, 3));
		offset();
		setPosition(
			FirstPolygon.getScreenCenter().x - (Display.getWidth() / 2 - Mouse.getX()) / FirstPolygon.getRenderScale().x,
			FirstPolygon.getScreenCenter().y + (Display.getHeight() / 2 - Mouse.getY()) / FirstPolygon.getRenderScale().y);
	}
	
	public void show(boolean show) {
		if (show) {
			setColor(Shepherd.CHOSEN);
			getColor().setAlpha(1.0f);
		} else {
			setColor(Color.EMPTY);
		}
	}
	
	public boolean isSelected(Point point) {
		if (Mouse.isButtonDown(0)) {
			return selection.isInPolygon(point);
		}
		return false;
	}
	
	public boolean isSelected(FirstPolygon polygon) {
		if (Mouse.isButtonDown(0)) {
			return selection.hits(polygon);
		}
		return false;
	}
	
	private void offset() {
		for (int i = 0; i < getPoints().length; i++) {
			getPoints()[i].x += 4 / FirstPolygon.getRenderScale().x;
			getPoints()[i].y += 6 / FirstPolygon.getRenderScale().y;
		}
	}
	
}