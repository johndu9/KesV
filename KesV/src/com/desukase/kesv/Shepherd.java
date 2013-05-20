package com.desukase.kesv;

import java.util.ArrayList;
import java.util.Random;

import com.desukase.engine.Color;
import com.desukase.engine.Controls;
import com.desukase.engine.Point;
import com.desukase.engine.polygon.Controllable;
import com.desukase.engine.polygon.FirstPolygon;
import com.desukase.engine.polygon.MovablePolygon;

public class Shepherd extends MovablePolygon implements Controllable{

	public static final int UP = 0;
	public static final int LEFT = 1;
	public static final int DOWN = 2;
	public static final int RIGHT = 3;
	
	private static Random random = Game.random;
	private ArrayList<Soul> souls = new ArrayList<Soul>();
	private int persuasiveness;
	private float radius = 16;
	private Trail trail;
	private Territory territory;
	
	private Controls controls;

	private static final Color CHOSEN = new Color(1.0f, 1.0f, 0.0f, 0.75f);
//	private static final Color OTHER = new Color(0.0f, 1.0f, 1.0f, 0.75f);
	
	public Shepherd(Point position, int speed, int persuasiveness, int soulCount, Color color, Controls controls) {
		super(FirstPolygon.radiusToPoints(16, 16), 0, position, color, speed, Math.PI * 4);
		Color influenceColor = new Color(color);
		influenceColor.setAlpha(color.getAlpha() / 2);
		for(int i = 0; i < soulCount; i++){
			souls.add(new Soul(16, position, this));	
		}
		setPersuasiveness(persuasiveness);
		setControls(controls);
		trail = new Trail(this);
		territory = new Territory(16 + Game.TERRITORY_RADIUS, this);
	}
	
	public Shepherd(){
		this(new Point(0, 0), 800, 10, 0, CHOSEN, Controls.WASD_MOVEMENT);
	}
	
	public int getPersuasiveness(){
		return persuasiveness;
	}
	
	public void setPersuasiveness(int persuasiveness){
		this.persuasiveness = persuasiveness;
	}
	
	public float getRadius(){
		return radius;
	}
	
	public void setRadius(float radius){
		this.radius = radius;
		setPoints(FirstPolygon.radiusToPoints(radius, 16));
		trail = new Trail(this);
		territory = new Territory((int)radius + Game.TERRITORY_RADIUS, this);
	}
	
	public boolean convertSoul(Soul soul, ArrayList<Soul> list){
		if(!list.contains(soul)){
			return false;
		}
		if(soul.isLost() && soul.getInfluence() >= Soul.MAX_INFLUENCE){
			souls.add(new Soul(soul.getRadius(), soul.getPosition(), this));
			list.remove(soul);
			return true;
		}
		return false;
	}
	
	public void applyInfluence(Soul soul){
		int lost = (soul.isLost()) ? (1) : (-1);
		if(isInTerritory(soul.getPosition())){
			soul.setInfluence(soul.getInfluence() + persuasiveness * lost);
		}
		for(Soul found : souls){
			if(found.isInTerritory(soul.getPosition())){
				soul.setInfluence(soul.getInfluence() + (persuasiveness / 2) * lost);
			}
		}
	}
	
	public boolean isInTerritory(Point other){
		float distance = (float)getPosition().distanceTo(other);
		if(distance < radius + Game.TERRITORY_RADIUS){
			return true;
		}
		return false;
	}
	
	public void update(int delta){
		trail.update(delta);
		for(Soul soul : souls){
			soul.update(delta);
		}
		territory.update(delta);
		if(controls.equals(Controls.EMPTY)){
			double directionIncrement = random.nextDouble() * Math.PI / 16.0 - Math.PI / 32.0;
			setDirection(getDirection() + directionIncrement);
			move(delta);
		}else{
			handleControls(delta);	
		}
		super.update(delta);
	}
	
	public void handleControls(int delta){
		controls.update();
		boolean up = controls.getState(UP);
		boolean left = controls.getState(LEFT);
		boolean down = controls.getState(DOWN);
		boolean right = controls.getState(RIGHT);
		if(up && right) move(-Math.PI / 4, delta);
		else if(up && left) move(-Math.PI * 3 / 4, delta);
		else if(down && left) move(Math.PI * 3 / 4, delta);
		else if(down && right) move(Math.PI / 4, delta);
		else if(up) move(-Math.PI / 2, delta);
		else if(left) move(Math.PI, delta);
		else if(down) move(Math.PI / 2, delta);
		else if(right) move(0, delta);
	}
	
	public ArrayList<Soul> getSouls(){
		return souls;
	}

	public Controls getControls() {
		return controls;
	}

	public void setControls(Controls controls) {
		this.controls = controls;
	}
	
}