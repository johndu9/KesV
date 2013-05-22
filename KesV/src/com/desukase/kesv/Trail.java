package com.desukase.kesv;

import com.desukase.engine.Color;
import com.desukase.engine.Timer;
import com.desukase.engine.polygon.FirstPolygon;

public class Trail{
	
	private FirstPolygon[] trail = new FirstPolygon[4];
	private FirstPolygon leader;
	private Color color;
	private int counter = 0;
	private Timer moveTimer = new Timer();
	
	public Trail(FirstPolygon leader){
		this.leader = leader;
		color = new Color(leader.getColor());
		color.setAlpha(color.getAlpha() / 4 + 0.1f);
		for(int i = 0; i < trail.length; i++){
			trail[i] = new FirstPolygon(leader.getPoints(), leader.getDirection(), leader.getPosition(), new Color(color));
		}
	}
	
	public void update(int delta){
		if(moveTimer.getDelay(50)){
			counter = (counter + 1) % (trail.length);
			trail[counter].setPosition(leader.getPosition());
			trail[counter].setColor(new Color(color));
		}
		for(FirstPolygon polygon : trail){
			polygon.getColor().setAlpha(polygon.getColor().getAlpha() - 0.01f);
			polygon.update(delta);
		}
	}
	
	public Color getColor(){
		return color;
	}
	
	public void setColor(Color color){
		this.color = color;
	}
	
}