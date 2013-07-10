package com.desukase.kesv;

import java.util.ArrayList;
import java.util.Random;

import org.lwjgl.opengl.Display;

import com.desukase.engine.Bar;
import com.desukase.engine.Color;
import com.desukase.engine.Controls;
import com.desukase.engine.DataSet;
import com.desukase.engine.GameDisplay;
import com.desukase.engine.Point;
import com.desukase.engine.Sound;
import com.desukase.engine.Timer;
import com.desukase.engine.Toggle;
import com.desukase.engine.polygon.FirstPolygon;

public class Game{

	private ArrayList<Soul> souls = new ArrayList<Soul>();
	private ArrayList<Explosion> explosions = new ArrayList<Explosion>();
	private FirstPolygon background;
	private FirstPolygon foreground;
	private Shepherd shepherd;
	private Controls shepherdControls;
	private FirstPolygon arrow;
	private boolean wasReset = false;
	private boolean wasFullscreen = true;
	private Toggle fullscreen = new Toggle(wasFullscreen);
	private boolean wasPaused = false;
	private Toggle paused = new Toggle(wasPaused);
	private Toggle followToggle = new Toggle(false);
	private Timer influenceTimer = new Timer();
	private Bar soulGet;
	private int soulCount;
	private Sound music = new Sound("Wisps_of_Whorls.ogg", true, true, false);
	private Sound conversion = new Sound("Conversion.ogg", false, false, false);
	private Controls gameControls;
	public static Random random = new Random("uwotm8".hashCode());

	private static final float DEFAULT_ZOOM = 0.25f;
	private static final float MINIMUM_ZOOM = 0.10f;
	
	public static final int START_SOUL_SIZE = 32;
	public static final int SAME_SOUL_MAX = 16;
	public static final int TERRITORY_RADIUS = 96;
	public static final Color FOUND_MINIMUM = new Color(0.75f, 0.75f, 0.0f, 0.5f);
	public static final Color FOUND_MAXIMUM = new Color(1.5f, 1.5f, 1.0f, 1.0f);
	public static final Color BACKGROUND = new Color(1.0f, 1.0f, 1.0f, 1.0f);
	public static final Color FOREGROUND = new Color(1.0f, 0.8f, 0.4f, 0.5f);
	
	private static final int FOLLOW = 0;
	private static final int ZOOM_IN = 1;
	private static final int ZOOM_OUT = 2;
	private static final int ZOOM_DEFAULT = 3;
	private static final int FULLSCREEN = 4;
	private static final int PAUSE = 5;
	private static final int RESET = 6;
	private static final int TOGGLE_FOLLOW = 7;
	
	public Game(DataSet dataSet){
		wasFullscreen = !dataSet.getValue("windowed").equals("true");
		GameDisplay.setFullscreen(wasFullscreen);
		GameDisplay.showCursor(false);
		GameDisplay.update();
		FirstPolygon.setRenderScale(DEFAULT_ZOOM);
		shepherdControls = new Controls(new String[]{
			dataSet.getValue("up"),
			dataSet.getValue("left"),
			dataSet.getValue("down"),
			dataSet.getValue("right"),
			dataSet.getValue("pull"),
			dataSet.getValue("push")
		});
		gameControls = new Controls(new String[]{
			dataSet.getValue("follow"),
			dataSet.getValue("zoomIn"),
			dataSet.getValue("zoomOut"),
			dataSet.getValue("zoomDefault"),
			dataSet.getValue("fullscreen"),
			dataSet.getValue("pause"),
			dataSet.getValue("reset"),
			dataSet.getValue("toggleFollow")
		});
		reset();
		setBackground();
		music.play(1.0f, 1.0f);
	}
	
	public void reset(){
		shepherd = new Shepherd(shepherdControls);
		souls.clear();
		generateSouls(32);
		arrow = new FirstPolygon(FirstPolygon.radiusToPoints(48, 3), 0, new Point(0, 0), new Color(shepherd.getColor()));
		soulCount = 0;
	}
	
	public void update(){
		wasReset = gameControls.getState(RESET);
		wasPaused = paused.getState();
		wasFullscreen = fullscreen.getState();
		gameControls.update();
		paused.update(gameControls.getState(PAUSE));
		if(wasPaused != paused.getState()){
			for(Soul soul : souls){
				soul.setFrozen(paused.getState());
			}
			for(Explosion explosion : explosions){
				explosion.setFrozen(paused.getState());
			}
			shepherd.setFrozen(paused.getState());
			if(paused.getState()){
				music.pause();
			}else{
				music.resume(1.0f, 1.0f);
			}
		}
		fullscreen.update(gameControls.getState(FULLSCREEN));
		if(wasFullscreen != fullscreen.getState()){
			GameDisplay.setFullscreen(fullscreen.getState());
			setBackground();
		}
		followToggle.update(gameControls.getState(TOGGLE_FOLLOW));
		if(!wasReset && wasReset != gameControls.getState(RESET)){
			reset();
		}
		if(gameControls.getState(ZOOM_DEFAULT)){
			FirstPolygon.setRenderScale(DEFAULT_ZOOM);
			setBackground();
		}
		if(gameControls.getState(ZOOM_OUT) && FirstPolygon.getRenderScale().x > MINIMUM_ZOOM){
			FirstPolygon.zoom(false, 3);
			setBackground();
		}else if(FirstPolygon.getRenderScale().x < MINIMUM_ZOOM){
			FirstPolygon.getRenderScale().x = MINIMUM_ZOOM;
			FirstPolygon.getRenderScale().y = MINIMUM_ZOOM;
		}
		if(gameControls.getState(ZOOM_IN) && FirstPolygon.getRenderScale().x < DEFAULT_ZOOM){
			FirstPolygon.zoom(true, 3);
			setBackground();
		}else if(FirstPolygon.getRenderScale().x >= DEFAULT_ZOOM){
			FirstPolygon.getRenderScale().x = DEFAULT_ZOOM;
			FirstPolygon.getRenderScale().y = DEFAULT_ZOOM;
		}
		if(!paused.getState() && influenceTimer.getDelay(500)){
			for(Soul soul : souls){
				if(soul.isLost() && soul.getRadius() <= shepherd.getRadius()){
					shepherd.applyInfluence(soul);
					float radius = soul.getRadius();
					if(shepherd.convertSoul(soul, souls)){
						conversion.play(1.0f, 0.5f);
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
			if(soul.getRadius() == shepherd.getRadius()){
				if(distance < shortest){
					shortest = distance;
					soulIndex = souls.indexOf(soul);
				}
			}
			if(distance > TERRITORY_RADIUS * 96){
				soul.setDirection(soul.getPosition().directionTo(shepherd.getPosition()));
			}
		}
		double direction = (souls.size() > 0) ? (shepherd.getPosition().directionTo(souls.get(soulIndex).getPosition())) : (0);
		arrow.setPosition(
			Point.add(
				shepherd.getPosition(),
				new Point(
					(float)Math.cos(direction) * (shepherd.getRadius() + 128),
					(float)Math.sin(direction) * (shepherd.getRadius() + 128))));
		arrow.setDirection(direction);
		arrow.getColor().setAlpha(((float)shortest / FirstPolygon.DISPLAY_DIAGONAL) * FirstPolygon.getRenderScale().x);
		for(int i = 0; i < explosions.size(); i++){
			explosions.get(i).update(delta);
			if(explosions.get(i).isInvisible()){
				explosions.remove(i);
			}
		}
		
		if(souls.size() > 0 &&
			((followToggle.getState()) || (shortest >= shepherd.getRadius() * 5 && gameControls.getState(FOLLOW)))){
			shepherd.move(direction, delta);
		}
		
		soulGet.update(delta);
		arrow.update(delta);
		shepherd.update(delta);
		foreground.update(delta);
	}
	
	private void setBackground(){
		background =
			new FirstPolygon(
				FirstPolygon.sizeToPoints(
					Display.getWidth() * 2 / FirstPolygon.getRenderScale().x,
					Display.getHeight() * 2 / FirstPolygon.getRenderScale().y),
				0, FirstPolygon.getScreenCenter(), BACKGROUND);
		foreground =
			new FirstPolygon(background.getPoints(), 0, background.getPosition(), FOREGROUND);
		Color frontColor = new Color(shepherd.getColor());
		frontColor.setAlpha(shepherd.getColor().getAlpha() / 2);
		Color backColor = new Color(frontColor);
		backColor.setAlpha(frontColor.getAlpha() / 2);
		soulGet = new Bar(
			192 / FirstPolygon.getRenderScale().x, 32 / FirstPolygon.getRenderScale().y, 0,
			new Point(
				background.getPosition().x,
				background.getPosition().y -
					(Display.getHeight() / 2) / FirstPolygon.getRenderScale().y + 32 / FirstPolygon.getRenderScale().y),
				(float)soulCount / (float)SAME_SOUL_MAX, frontColor, backColor);
	}
	
	private void generateSouls(float radius){
		for(int i = 0; i < SAME_SOUL_MAX * 2; i++){
			double direction = random.nextDouble() * Math.PI * 2;
			souls.add(
				new Soul(
					radius,
					Point.add(
						(souls.size() > 0) ? (souls.get(souls.size() - 1).getPosition()) : (shepherd.getPosition()),
						new Point(
							256 * (radius / 16) * (float)Math.cos(direction),
							256 * (radius / 16) * (float)Math.sin(direction))),
					Shepherd.LOST));
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