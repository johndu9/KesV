package com.desukase.kesv;

import java.util.ArrayList;
import java.util.Random;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;

import com.desukase.engine.Bar;
import com.desukase.engine.Color;
import com.desukase.engine.DataSet;
import com.desukase.engine.GameDisplay;
import com.desukase.engine.Point;
import com.desukase.engine.Timer;
import com.desukase.engine.Toggle;
import com.desukase.engine.polygon.FirstPolygon;

public class Game{
	
	private ArrayList<Soul> souls = new ArrayList<Soul>();
	private ArrayList<Explosion> explosions = new ArrayList<Explosion>();
	private FirstPolygon background;
	private FirstPolygon foreground;
	private Shepherd shepherd = new Shepherd();
	private FirstPolygon arrow;
	private boolean wasFullscreen = true;
	private Toggle fullscreen = new Toggle(wasFullscreen);
	private Timer influenceTimer = new Timer();
	private Bar soulGet;
	private int soulCount = 0;
	public static Random random = new Random("uwotm8".hashCode());
	public static final int START_SOUL_SIZE = 32;
	public static final int SAME_SOUL_MAX = 16;
	public static final int TERRITORY_RADIUS = 96;
	public static final Color FOUND_MINIMUM = new Color(0.75f, 0.75f, 0.0f, 0.5f);
	public static final Color FOUND_MAXIMUM = new Color(1.5f, 1.5f, 1.0f, 1.0f);
	public static final Color BACKGROUND = new Color(1.0f, 1.0f, 1.0f, 1.0f);
	public static final Color FOREGROUND = new Color(1.0f, 0.8f, 0.4f, 0.5f);
	
	public Game(DataSet dataSet){
		GameDisplay.setFullscreen(wasFullscreen);
		GameDisplay.frameCap = 80;
		GameDisplay.showCursor(false);
		GameDisplay.update();
		FirstPolygon.setRenderScale(0.1f, 0.1f);
		setBackground();
		reset();
	}
	
	public void reset(){
		souls.clear();
//		souls.add(new Soul(16, new Point(random.nextInt(2048) - 1024, random.nextInt(2048) - 1024), FirstPolygon.EMPTY));
//		generateSouls(16);
		generateSouls(32);
		shepherd = new Shepherd();
		arrow = new FirstPolygon(FirstPolygon.radiusToPoints(32, 3), 0, new Point(0, 0), new Color(shepherd.getColor()));
	}
	
	public void update(){
		wasFullscreen = fullscreen.getState();
		fullscreen.update(Keyboard.isKeyDown(Keyboard.KEY_F11));
		if(wasFullscreen != fullscreen.getState()){
			wasFullscreen = fullscreen.getState();
			GameDisplay.setFullscreen(wasFullscreen);
			setBackground();
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_0)){
			reset();
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_9)){
			Timer.setTimerSpeed(1.0);
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_8)){
			FirstPolygon.setRenderScale(0.1f, 0.1f);
			setBackground();
		}

		if(Keyboard.isKeyDown(Keyboard.KEY_LBRACKET) && FirstPolygon.getRenderScale().x > 0.1f){
			FirstPolygon.zoom(false, 3);
			setBackground();
		}else if(FirstPolygon.getRenderScale().x < 0.1f){
			FirstPolygon.getRenderScale().x = 0.1f;
			FirstPolygon.getRenderScale().y = 0.1f;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_RBRACKET) && FirstPolygon.getRenderScale().x < 1.0f){
			FirstPolygon.zoom(true, 3);
			setBackground();
		}else if(FirstPolygon.getRenderScale().x >= 1.0f){
			FirstPolygon.getRenderScale().x = 1.0f;
			FirstPolygon.getRenderScale().y = 1.0f;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_MINUS)){
			Timer.setTimerSpeed(Timer.getTimerSpeed() - 0.05);
		}
		if(Timer.getTimerSpeed() < 0.0){
			Timer.setTimerSpeed(0.0);
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_EQUALS)){
			Timer.setTimerSpeed(Timer.getTimerSpeed() + 0.05);
		}
		if(Timer.getTimerSpeed() > 10.0){
			Timer.setTimerSpeed(10.0);
		}
		if(influenceTimer.getDelay(500)){
			for(Soul soul : souls){
				if(soul.isLost() && soul.getRadius() <= shepherd.getRadius()){
					shepherd.applyInfluence(soul);
					float radius = soul.getRadius();
					if(shepherd.convertSoul(soul, souls)){
						if(radius == shepherd.getRadius()){
							soulCount++;
						}
						explosions.add(
							new Explosion(soul.getPosition(), 32,
								(int)shepherd.getRadius() * 2, (int)shepherd.getRadius() * 4,
								FOUND_MINIMUM, FOUND_MAXIMUM));
						if(shepherd.getRadius() == radius && soulCount >= SAME_SOUL_MAX){
							soulCount = 0;
							shepherd.setRadius(radius + 16);
							generateSouls(shepherd.getRadius());
						}
						break;
					}
				}
			}
		}
		FirstPolygon.softCenterOnPolygon(shepherd, 28.0);
		background.setPosition(FirstPolygon.getScreenCenter());
		foreground.setPosition(background.getPosition());
		soulGet.setPosition(new Point(
			background.getPosition().x,
			background.getPosition().y -
				(Display.getHeight() / 2) / FirstPolygon.getRenderScale().y + 32 / FirstPolygon.getRenderScale().y));
		soulGet.setValue(soulCount / (float)SAME_SOUL_MAX);
	}
	
	public void updatePolygons(){
		GameDisplay.update();
		int delta = GameDisplay.getDelta();
		background.update(delta);
		double shortest = (souls.size() > 0) ? (shepherd.getPosition().distanceTo(souls.get(0).getPosition())) : (0);
		int soulIndex = 0;
		for(Soul soul : souls){
			soul.update(delta);
			double distance = shepherd.getPosition().distanceTo(soul.getPosition());
			if(soul.getRadius() <= shepherd.getRadius() && distance < shortest){
				shortest = distance;
				soulIndex = souls.indexOf(soul);
			}
			if(distance > TERRITORY_RADIUS * 96){
				soul.setDirection(soul.getPosition().directionTo(shepherd.getPosition()));
			}
		}
		double direction = (souls.size() > 0) ? (shepherd.getPosition().directionTo(souls.get(soulIndex).getPosition())) : (0);
		arrow.setPosition(
			Point.add(
				shepherd.getPosition(),
				new Point((float)Math.cos(direction) * 128.0f, (float)Math.sin(direction) * 128.0f)));
		arrow.setDirection(direction);
		arrow.getColor().setAlpha(((float)shortest / FirstPolygon.DISPLAY_DIAGONAL) * FirstPolygon.getRenderScale().x);
		for(int i = 0; i < explosions.size(); i++){
			explosions.get(i).update(delta);
			if(explosions.get(i).isInvisible()){
				explosions.remove(i);
			}
		}
		soulGet.update(delta);
		arrow.update(delta);
		shepherd.update(delta);
		foreground.update(delta);
		if(souls.size() > 0 && Keyboard.isKeyDown(Keyboard.KEY_SPACE)){
			shepherd.move(direction, delta);
		}
	}
	
	private void setBackground(){
		background =
			new FirstPolygon(
				FirstPolygon.sizeToPoints(
//					Display.getWidth(), Display.getHeight()),
					Display.getWidth() * 2 / FirstPolygon.getRenderScale().x,
					Display.getHeight() * 2 / FirstPolygon.getRenderScale().y),
				0, FirstPolygon.getScreenCenter(), BACKGROUND);
		foreground =
			new FirstPolygon(background.getPoints(), 0, background.getPosition(), FOREGROUND);
		Color backColor = new Color(shepherd.getColor());
		backColor.setAlpha(shepherd.getColor().getAlpha() / 2);
		soulGet = new Bar(
			192 / FirstPolygon.getRenderScale().x, 32 / FirstPolygon.getRenderScale().y, 0,
			new Point(
				background.getPosition().x,
				background.getPosition().y -
					(Display.getHeight() / 2) / FirstPolygon.getRenderScale().y + 32 / FirstPolygon.getRenderScale().y),
				(float)soulCount / (float)SAME_SOUL_MAX, backColor, backColor);
	}
	
	private void generateSouls(float radius){
		for(int i = 0; i < SAME_SOUL_MAX; i++){
			double direction = random.nextDouble() * Math.PI * 2;
			souls.add(
				new Soul(
					radius,
					Point.add(
						(souls.size() > 0) ? (souls.get(souls.size() - 1).getPosition()) : (shepherd.getPosition()),
						new Point(
							256 * (radius / 16) * (float)Math.cos(direction),
							256 * (radius / 16) * (float)Math.sin(direction))),
					FirstPolygon.EMPTY));
		}
	}
	
	public static Color generateFoundColor(){
		return Color.randomColor(random, FOUND_MINIMUM, FOUND_MAXIMUM);
	}
	
	public static Color generateLostColor(){
		float value = random.nextFloat() - 0.25f;
		return new Color(
			value, value, value,
			random.nextFloat() / 4 + 0.5f);
	}
	
}