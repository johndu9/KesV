package com.desukase.kesv;

import java.util.Random;

import com.desukase.engine.Color;
import com.desukase.engine.Point;
import com.desukase.engine.polygon.FirstPolygon;
import com.desukase.engine.polygon.MovablePolygon;

public class Soul extends MovablePolygon{
	
	private static Random random = Game.random;
	private FirstPolygon leader;
	private Trail trail;
	private Territory territory;
	private int influence;
	private float radius;
	
	public static final int MAX_INFLUENCE = 100;
	
	public Soul(float radius, Point point, FirstPolygon leader){
		super(
			FirstPolygon.radiusToPoints(radius, 16),
			random.nextDouble() * Math.PI * 2,
			point,
			generateFoundColor(),
			400 + random.nextInt(300), 0);
		setLeader(leader);
		if(isLost()){
			setLost();
			setInfluence(0);
		}else{
			setInfluence(MAX_INFLUENCE);
		}
		this.radius = radius;
		trail = new Trail(this);
		territory = new Territory(getTerritoryRadius(), this);
	}
	
	public void update(int delta){
		trail.update(delta);
		if(!isLost()){
			territory.update(delta);
		}
		super.update(delta);
		Point leaderPosition = leader.getPosition();
		double distanceToLeader = getPosition().distanceTo(leaderPosition);
		double directionToLeader = getPosition().directionTo(leaderPosition);
		double directionIncrement = random.nextDouble() * Math.PI / 16.0 - Math.PI / 32.0;
		if(
			!isLost() &&
			distanceToLeader > getTerritoryRadius() + random.nextInt(100) &&
			(getDirection() < directionToLeader - Math.PI / 4 ||
			getDirection() > directionToLeader + Math.PI / 4)){
			setDirection(directionToLeader + directionIncrement);
		}else{
			setDirection(getDirection() + directionIncrement);
		}
		move(delta);
	}
	
	public boolean isInTerritory(Point other){
		float distance = (float)getPosition().distanceTo(other);
		if(distance < getTerritoryRadius()){
			return true;
		}
		return false;
	}
	
	public FirstPolygon getLeader(){
		return leader;
	}
	
	public void setLeader(FirstPolygon leader){
		this.leader = leader;
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
	
	public void setFound(){
		setSpeed(getSpeed() + 350);
		setColor(generateFoundColor());
	}
	
	public void setLost(){
		setSpeed(getSpeed() - 350);
		setColor(generateLostColor());
	}
	
	public static Color generateFoundColor(){
		return new Color(
			random.nextFloat() + 0.75f,
			random.nextFloat() + 0.75f,
			random.nextFloat(),
			random.nextFloat() / 2 + 0.5f);
	}
	
	public static Color generateOtherColor(){
		return new Color(
			random.nextFloat(),
			random.nextFloat() + 0.75f,
			random.nextFloat() + 0.75f,
			random.nextFloat() / 2 + 0.5f);
	}
	
	public static Color generateLostColor(){
		float value = random.nextFloat() - 0.25f;
		return new Color(
			value, value, value,
			random.nextFloat() / 4 + 0.5f);
	}
	
	public boolean isLost(){
		return leader.equals(FirstPolygon.EMPTY);
	}
	
}