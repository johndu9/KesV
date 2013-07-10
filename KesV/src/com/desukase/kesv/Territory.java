package com.desukase.kesv;

import com.desukase.engine.Color;
import com.desukase.engine.polygon.FirstPolygon;
import com.desukase.engine.polygon.MovablePolygon;

public class Territory extends MovablePolygon{
	
	private FirstPolygon leader;
	private int radius;
	
	public Territory(int radius, FirstPolygon leader){
		super(FirstPolygon.radiusToPoints(radius, 16), 0, leader.getPosition(), new Color(0, 0, 0, 0), 0, Math.PI * 4);
		setColor(new Color(leader.getColor()));
		getColor().setAlpha(getColor().getAlpha() / 2);
		this.radius = radius;
		this.leader = leader;
	}
	
	public void update(int delta){
		super.update(delta);
		turn(true, delta);
		setPosition(leader.getPosition());
	}
	
	public int getRadius(){
		return radius;
	}
	
}