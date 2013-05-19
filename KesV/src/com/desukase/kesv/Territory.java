package com.desukase.kesv;

import com.desukase.engine.Color;
import com.desukase.engine.polygon.FirstPolygon;
import com.desukase.engine.polygon.MovablePolygon;

public class Territory extends MovablePolygon{
	
	private FirstPolygon leader;
	
	public Territory(int radius, FirstPolygon leader){
		super(FirstPolygon.radiusToPoints(radius, 16), 0, leader.getPosition(), new Color(0, 0, 0, 0), 0, Math.PI * 4);
		setColor(new Color(leader.getColor()));
		getColor().setAlpha(getColor().getAlpha() / 2);
		this.leader = leader;
	}
	
	public void update(int delta){
		setPosition(leader.getPosition());
		super.update(delta);
		turn(true, delta);
	}
	
}