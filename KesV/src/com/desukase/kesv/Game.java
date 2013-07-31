package com.desukase.kesv;

import java.util.ArrayList;
import java.util.Random;

import org.lwjgl.input.Mouse;
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
	private ArrayList<Shepherd> heretics = new ArrayList<Shepherd>();
	private ArrayList<Shepherd> prophets = new ArrayList<Shepherd>();
	private FirstPolygon background;
	private FirstPolygon foreground;
	private Shepherd shepherd;
	private Controls shepherdControls;
	private FirstPolygon arrow;
	private boolean enableHeretics;
	private boolean enableProphets;
	private boolean zoomingOut = false;
	private boolean wasReset = false;
	private boolean wasFullscreen;
	private Toggle fullscreen = new Toggle(wasFullscreen);
	private boolean wasPaused = false;
	private Toggle paused = new Toggle(wasPaused);
	private Toggle followToggle = new Toggle(false);
	private Timer influenceTimer = new Timer();
	private Bar shepherdMeter;
	private Bar hereticMeter;
	private int shepherdSouls;
	private int hereticSouls;
	private int soulsLeft = 0;
	private Sound music = new Sound("Wisps_of_Whorls.ogg", true, true, false);
	private Sound conversion = new Sound("Conversion.ogg", false, false, false);
	private Controls gameControls;
	public static Random random = new Random("uwotm8".hashCode());
	
	private FirstPolygon resume;
	private FirstPolygon close;
	private GameCursor cursor = new GameCursor();

	private static final float MAXIMUM_ZOOM = 0.25f;
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
	private static final int FULLSCREEN = 3;
	private static final int PAUSE = 4;
	private static final int RESET = 5;
	private static final int TOGGLE_FOLLOW = 6;
	
	public Game(DataSet dataSet){
		wasFullscreen = !dataSet.getValue("windowed").equals("true");
		GameDisplay.setFullscreen(wasFullscreen);
		GameDisplay.showCursor(false);
		GameDisplay.update();
		enableHeretics = dataSet.getValue("enableHeretics").equals("true");
		enableProphets = dataSet.getValue("enableProphets").equals("true");
		FirstPolygon.setRenderScale(MAXIMUM_ZOOM);
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
		heretics.clear();
		generateSouls(START_SOUL_SIZE);
		arrow = new FirstPolygon(FirstPolygon.radiusToPoints(48, 3), 0, new Point(0, 0), new Color(shepherd.getColor()));
		shepherdSouls = 0;
		hereticSouls = 0;
	}
	
	public void update(){
		wasReset = gameControls.getState(RESET);
		wasPaused = paused.getState();
		wasFullscreen = fullscreen.getState();
		gameControls.update();
		paused.update(gameControls.getState(PAUSE));
		if(paused.getState()){
			if(Mouse.isButtonDown(0)){
				if(cursor.hits(resume)){
					wasPaused = true;
					paused.setState(false);
				}else if(cursor.hits(close)){
					close();
				}
			}
		}
		if(wasPaused != paused.getState()){
			conversion.play(1.0f, 0.1f);
			for(Soul soul : souls){
				soul.setFrozen(paused.getState());
			}
			for(Explosion explosion : explosions){
				explosion.setFrozen(paused.getState());
			}
			for(Shepherd heretic : heretics){
				heretic.setFrozen(paused.getState());
			}
			for(Shepherd prophet : prophets){
				prophet.setFrozen(paused.getState());
			}
			shepherd.setFrozen(paused.getState());
			if(paused.getState()){
				music.pause();
				GameDisplay.showCursor(true);
			}else{
				music.resume(1.0f, 1.0f);
				GameDisplay.showCursor(false);
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
		if(gameControls.getState(ZOOM_OUT)){
			zoomingOut = true;
		}else if(gameControls.getState(ZOOM_IN)){
			zoomingOut = false;
		}
		if(zoomingOut && FirstPolygon.getRenderScale().x > MINIMUM_ZOOM){
			FirstPolygon.zoom(false, 3);
			setBackground();
		}else if(FirstPolygon.getRenderScale().x < MINIMUM_ZOOM){
			FirstPolygon.getRenderScale().x = MINIMUM_ZOOM;
			FirstPolygon.getRenderScale().y = MINIMUM_ZOOM;
		}
		if(!zoomingOut && FirstPolygon.getRenderScale().x < MAXIMUM_ZOOM){
			FirstPolygon.zoom(true, 3);
			setBackground();
		}else if(FirstPolygon.getRenderScale().x > MAXIMUM_ZOOM){
			FirstPolygon.getRenderScale().x = MAXIMUM_ZOOM;
			FirstPolygon.getRenderScale().y = MAXIMUM_ZOOM;
		}
		if(!paused.getState() && influenceTimer.getDelay(500)){
			if(soulsLeft == 0){
				generateSouls(shepherd.getRadius());
			}
			soulLoop: for(Soul soul : souls){
				float radius = soul.getRadius();
				for(Shepherd heretic : heretics){
					if(soul.isLost()){
						heretic.applyInfluence(soul);
						if(heretic.convertSoul(soul, souls)){
							if(radius == shepherd.getRadius()){
								conversion.play(1.0f, 0.25f);
								hereticSouls++;
								soulsLeft--;
							}
							break soulLoop;
						}
					}
				}
				for(Shepherd prophet : prophets){
					if(soul.isLost()){
						prophet.applyInfluence(soul);
						if(prophet.convertSoul(soul, souls)){
							if(radius == shepherd.getRadius()){
								conversion.play(1.0f, 0.5f);
								shepherdSouls++;
								soulsLeft--;
							}
							break soulLoop;
						}
					}
				}
				if(soul.isLost() && soul.getRadius() <= shepherd.getRadius()){
					shepherd.applyInfluence(soul);
					if(shepherd.convertSoul(soul, souls)){
						conversion.play(1.0f, 0.5f);
						if(radius == shepherd.getRadius()){
							shepherdSouls++;
							soulsLeft--;
						}
						explosions.add(
							new Explosion(soul.getPosition(), 32,
								(int)shepherd.getRadius() * 2, (int)shepherd.getRadius() * 4,
								FOUND_MINIMUM, FOUND_MAXIMUM));
						break soulLoop;
					}
				}
			}
			if(shepherdSouls >= SAME_SOUL_MAX){
				levelUp();
			}
		}
		FirstPolygon.softCenterOnPolygon(shepherd, 28.0);
		background.setPosition(FirstPolygon.getScreenCenter());
		foreground.setPosition(background.getPosition());
		shepherdMeter.setPosition(new Point(
			background.getPosition().x,
			background.getPosition().y -
				(Display.getHeight() / 2) / FirstPolygon.getRenderScale().y + 32 / FirstPolygon.getRenderScale().y));
		hereticMeter.setPosition(new Point(
			background.getPosition().x,
			background.getPosition().y -
				(Display.getHeight() / 2) / FirstPolygon.getRenderScale().y + 48 / FirstPolygon.getRenderScale().y));
		shepherdMeter.setValue(shepherdSouls / (float)SAME_SOUL_MAX);
		hereticMeter.setValue(hereticSouls / (float)SAME_SOUL_MAX);
	}
	
	public void updatePolygons(){
		GameDisplay.update();
		int delta = GameDisplay.getDelta();
		background.update(delta);
		for(Soul soul : souls){
			soul.update(delta);
			if(soul.getRadius() == shepherd.getRadius()){
				soul.updateTerritory(delta);
			}
			double distance = shepherd.getPosition().distanceTo(soul.getPosition());
			if(distance > TERRITORY_RADIUS * 128){
				soul.setDirection(soul.getPosition().directionTo(shepherd.getPosition()));
			}
		}
		for(Shepherd heretic : heretics){
			heretic.update(delta);
			heretic.setDirection(heretic.getPosition().directionTo(heretic.getClosestSoul(souls, false).getPosition()));
		}
		for(Shepherd prophet : prophets){
			prophet.update(delta);
			prophet.setDirection(prophet.getPosition().directionTo(prophet.getClosestSoul(souls, false).getPosition()));
		}
		Point nearest = shepherd.getClosestSoul(souls, true).getPosition();
		double direction = shepherd.getPosition().directionTo(nearest);
		double shortest = shepherd.getPosition().distanceTo(nearest);
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
		
		arrow.update(delta);
		shepherd.update(delta);
		shepherdMeter.update(delta);
		hereticMeter.update(delta);
		if(paused.getState()){
			resume.setPosition(
				FirstPolygon.getScreenCenter().x - 192 / FirstPolygon.getRenderScale().x,
				FirstPolygon.getScreenCenter().y);
			resume.update(delta);
			close.setPosition(
				FirstPolygon.getScreenCenter().x + 192 / FirstPolygon.getRenderScale().x,
				FirstPolygon.getScreenCenter().y);
			close.update(delta);
		}
		cursor.update(delta);
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
		Color shepherdMeterColor = Shepherd.CHOSEN;
		Color shepherdMeterBack = new Color(shepherdMeterColor);
		shepherdMeterBack.setAlpha(shepherdMeterColor.getAlpha() / 2);
		shepherdMeter = new Bar(
			192 / FirstPolygon.getRenderScale().x, 16 / FirstPolygon.getRenderScale().y, 0,
			new Point(
				background.getPosition().x,
				background.getPosition().y -
					(Display.getHeight() / 2) / FirstPolygon.getRenderScale().y + 32 / FirstPolygon.getRenderScale().y),
				(float)shepherdSouls / (float)SAME_SOUL_MAX, shepherdMeterColor, shepherdMeterBack);
		Color hereticMeterColor = Shepherd.HERETIC;
		Color hereticMeterBack = new Color(hereticMeterColor);
		hereticMeterBack.setAlpha(hereticMeterColor.getAlpha() / 2);
		hereticMeter = new Bar(
			192 / FirstPolygon.getRenderScale().x, 16 / FirstPolygon.getRenderScale().y, 0,
			new Point(
				background.getPosition().x,
				background.getPosition().y -
					(Display.getHeight() / 2) / FirstPolygon.getRenderScale().y + 48 / FirstPolygon.getRenderScale().y),
				(float)hereticSouls / (float)SAME_SOUL_MAX, hereticMeterColor, hereticMeterBack);
		resume =
			new FirstPolygon(
				FirstPolygon.radiusToPoints(64 / FirstPolygon.getRenderScale().x, 3),
				0, FirstPolygon.getScreenCenter(), shepherd.getColor());
		close =
			new FirstPolygon(
				FirstPolygon.sizeToPoints(96 / FirstPolygon.getRenderScale().x),
				0, FirstPolygon.getScreenCenter(), shepherd.getColor());
	}
	
	private void levelUp(){
		soulsLeft = 0;
		Point position = shepherd.getSouls().get(0).getPosition();
		for(int i = 0; i < 4; i++){
			shepherd.getSouls().remove(0);
		}
		if(enableProphets){
			prophets.add(Shepherd.generateProphet(shepherd.getRadius() + 16, position));
		}
		if(enableHeretics){
			heretics.add(Shepherd.generateHeretic(shepherd.getRadius() + 16, souls.get(souls.size() - 1).getPosition()));
		}
		shepherdSouls = 0;
		hereticSouls = 0;
		shepherd.setRadius(shepherd.getRadius() + 16);
		generateSouls(shepherd.getRadius());
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
							128 * radius * (float)Math.cos(direction),
							128 * radius * (float)Math.sin(direction))),
					generateLostColor(),
					Shepherd.LOST));
		}
		soulsLeft += SAME_SOUL_MAX * 2;
	}
	
	private void close(){
		System.exit(0);
	}
	
	public static Color generateFoundColor(){
		return Color.randomColor(random, FOUND_MINIMUM, FOUND_MAXIMUM);
	}
	
	public static Color generateLostColor(){
		float value = random.nextFloat() - 0.25f;
		return new Color(value, value, value, random.nextFloat() / 4 + 0.5f);
	}
	
}