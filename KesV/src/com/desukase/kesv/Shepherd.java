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
	public static final int PULL = 4;
	public static final int PUSH = 5;
	
	private static Random random = Game.random;
	private ArrayList<Soul> souls = new ArrayList<Soul>();
	private int persuasiveness;
	private float leash;
	private float radius = 32;
	private Trail trail;
	private Territory territory;
	private Controls controls;

	public static final Color CHOSEN = new Color(1.0f, 1.0f, 0.0f, 0.75f);
	public static final Color PROPHET = new Color(0.25f, 1.0f, 1.0F, 0.5f);
	public static final Color HERETIC = new Color(1.0f, 0.25f, 0.25f, 0.5f);
	public static final Shepherd LOST = new Shepherd(new Point(0, 0), 0, 0, 0, Color.EMPTY, Controls.EMPTY);
	
	public Shepherd(Point position, int speed, int persuasiveness, int soulCount, Color color, Controls controls) {
		super(FirstPolygon.radiusToPoints(32, 16), 0, position, color, speed, Math.PI * 4);
		Color influenceColor = new Color(color);
		influenceColor.setAlpha(color.getAlpha() / 2);
		for(int i = 0; i < soulCount; i++){
			souls.add(new Soul(16, position, color, this));
		}
		setPersuasiveness(persuasiveness);
		setControls(controls);
		trail = new Trail(this);
		territory = new Territory(16 + Game.TERRITORY_RADIUS, this);
		leash = territory.getRadius();
	}
	
	public Shepherd(Controls controls){
		this(new Point(0, 0), 1600, 10, 3, CHOSEN, controls);
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
		float multiplier = leash / territory.getRadius();
		this.radius = radius;
		setPoints(FirstPolygon.radiusToPoints(radius, 16));
		trail = new Trail(this);
		territory = new Territory((int)radius + Game.TERRITORY_RADIUS, this);
		leash = (territory.getRadius() * multiplier);
	}
	
	public boolean convertSoul(Soul soul, ArrayList<Soul> list){
		if(!list.contains(soul)){
			return false;
		}
		if(soul.isLost() && soul.getInfluence() >= Soul.MAX_INFLUENCE){
			Color color;
			if(getColor().equals(CHOSEN)){
				color = Game.generateFoundColor();
			}else{
				color = getColor();
				for(int i = 0; i < color.getValues().length - 1; i++){
					color.getValues()[i] *= (0.75f + 0.5f * random.nextFloat());
				}
			}
			souls.add(new Soul(soul.getRadius(), soul.getPosition(), color, this));
			list.remove(soul);
			return true;
		}
		return false;
	}
	
	public void applyInfluence(Soul soul){
		if(isInTerritory(soul.getPosition())){
			soul.setInfluence(soul.getInfluence() + persuasiveness);
		}
		for(Soul found : souls){
			if(found.isInTerritory(soul.getPosition())){
				soul.setInfluence(soul.getInfluence() + (persuasiveness / 2));
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
			soul.updateTerritory(delta);
		}
		if(controls.equals(Controls.EMPTY)){
			double directionIncrement = random.nextDouble() * Math.PI / 16.0 - Math.PI / 32.0;
			setDirection(getDirection() + directionIncrement);
			move(delta);
		}else{
			handleControls(delta);	
		}
		super.update(delta);
		territory.update(delta);
	}
	
	public void handleControls(int delta){
		controls.update();
		boolean up = controls.getState(UP);
		boolean left = controls.getState(LEFT);
		boolean down = controls.getState(DOWN);
		boolean right = controls.getState(RIGHT);
		boolean pull = controls.getState(PULL);
		boolean push = controls.getState(PUSH);
		if(up && right) move(-Math.PI / 4, delta);
		else if(up && left) move(-Math.PI * 3 / 4, delta);
		else if(down && left) move(Math.PI * 3 / 4, delta);
		else if(down && right) move(Math.PI / 4, delta);
		else if(up) move(-Math.PI / 2, delta);
		else if(left) move(Math.PI, delta);
		else if(down) move(Math.PI / 2, delta);
		else if(right) move(0, delta);
		if(pull && !push){
			leash = territory.getRadius();
		}else if(push && !pull){
			leash = territory.getRadius() * 4;
		}
	}
	
	public float getLeash(){
		return leash;
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
	
	public void setFrozen(boolean frozen){
		super.setFrozen(frozen);
		for(Soul soul : souls){
			soul.setFrozen(frozen);
		}
		trail.setFrozen(frozen);
	}
	
	public Soul getClosestSoul(ArrayList<Soul> souls, boolean sameRadius){
		double shortest = getPosition().distanceTo(souls.get(0).getPosition());
		int index = 0;
		for(int i = 0; i < souls.size(); i++){
			double distance = getPosition().distanceTo(souls.get(i).getPosition()); 
			if(distance < shortest && (!sameRadius || (sameRadius && souls.get(i).getRadius() == radius))){
				shortest = distance;
				index = i;
			}
		}
		return souls.get(index);
	}
	
	public static Shepherd generateHeretic(float radius, Point position){
		Shepherd heretic = new Shepherd(position, 512 + 256 + random.nextInt(512 + 256), 5, 3, HERETIC, Controls.EMPTY);
		heretic.setRadius(radius);
		heretic.leash = (heretic.territory.getRadius() * 2);
		return heretic;
	}
	
	public static Shepherd generateProphet(float radius, Point position){
		Shepherd prophet = new Shepherd(position, 512 + 256 + random.nextInt(512 + 256), 5, 3, PROPHET, Controls.EMPTY);
		prophet.setRadius(radius);
		prophet.leash = (prophet.territory.getRadius() * 2);
		return prophet;
	}
	
}