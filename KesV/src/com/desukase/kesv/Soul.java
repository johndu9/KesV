package com.desukase.kesv;

import java.util.Random;

import com.desukase.engine.Color;
import com.desukase.engine.Point;
import com.desukase.engine.polygon.FirstPolygon;
import com.desukase.engine.polygon.MovablePolygon;

public class Soul extends MovablePolygon{
	
	private static Random random = Game.random;
	private Shepherd shepherd;
	private Trail trail;
	private Territory territory;
	private int influence;
	private float radius;
	
	public static final int MAX_INFLUENCE = 100;
	
	public Soul(float radius, Point point, Shepherd shepherd){
		super(
			FirstPolygon.radiusToPoints(radius, 16),
			random.nextDouble() * Math.PI * 2,
			point,
			Color.EMPTY, 0, 0);
		setLeader(shepherd);
		float speed;
		if(shepherd.equals(Shepherd.LOST)){
			speed = ((radius / 8) * 100 + random.nextInt(100));
			speed = (speed > 1600) ? (1600) : (speed);
		}else{
			speed = (512 + random.nextInt(512 + 256));
		}
		setSpeed(speed);
		if(isLost()){
			setColor(Game.generateLostColor());
			setInfluence(0);
		}else{
			setColor(Game.generateFoundColor());
			setInfluence(MAX_INFLUENCE);
		}
		this.radius = radius;
		setTrail();
		territory = new Territory(getTerritoryRadius(), this);
	}
	
	public void update(int delta){
		trail.update(delta);
		super.update(delta);
		Point shepherdPosition = shepherd.getPosition();
		double distanceToShepherd = getPosition().distanceTo(shepherdPosition);
		double directionToShepherd = getPosition().directionTo(shepherdPosition);
		double directionIncrement = random.nextDouble() * Math.PI / 16.0 - Math.PI / 32.0;
		if(
			!isLost() &&
			distanceToShepherd > shepherd.getLeash()&&
			(getDirection() < directionToShepherd - Math.PI / 4 ||
			getDirection() > directionToShepherd + Math.PI / 4)){
			setDirection(directionToShepherd + directionIncrement);
		}else{
			setDirection(getDirection() + directionIncrement);
		}
		move(delta);
		territory.update(delta);
	}
	
	public boolean isInTerritory(Point other){
		float distance = (float)getPosition().distanceTo(other);
		if(distance < getTerritoryRadius()){
			return true;
		}
		return false;
	}
	
	public Shepherd getLeader(){
		return shepherd;
	}
	
	public void setLeader(Shepherd shepherd){
		this.shepherd = shepherd;
	}
	
	public Trail getTrail(){
		return trail;
	}
	
	public void setTrail(){
		trail = new Trail(this);
	}
	
	public int getInfluence(){
		return influence;
	}
	
	public void setInfluence(int influence){
		this.influence = influence;
	}
	
	public float getRadius(){
		return radius;
	}
	
	public int getTerritoryRadius(){
		return (int)radius + Game.TERRITORY_RADIUS / 2;
	}
	
	public boolean isLost(){
		return shepherd.equals(Shepherd.LOST);
	}
	
	public void setFrozen(boolean frozen){
		super.setFrozen(frozen);
		trail.setFrozen(frozen);
	}
	
}