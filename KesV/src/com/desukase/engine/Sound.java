package com.desukase.engine;

import java.io.File;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;

/**
 * Class used for playing and using sounds
 * @author John Du
 */
public class Sound{
	
	/** Used for actually playing the sound */
	private Clip clip;
	/** Used for controlling the volume */
    private FloatControl controller;
    /** Name of file in /res/sfx/ */
	private String name;
	/** Volume of sound */
	private int volume;
	/** Times we loop */
	private int loops = 0;
	/** Game's master volume */
	private static int masterVolume;
	/** Non-muted master volume */
	private static int masterVolumeStorage;
	
	/**
	 * Constructor, makes a sound
	 * @param name Name of file in /res/sfx/
	 */
	public Sound(String name){
		setName(name);
		if(findSound()){
			setVolume(100);
		}else{
			System.out.println("Problem encountered initializing sound");
		}
	}
	
	/**
	 * Plays the sound
	 */
	public void play(){
		setVolume(getVolume());
		clip.stop();
		clip.setMicrosecondPosition(0);
//		clip.start();
		clip.loop(loops);
	}
	
	/**
	 * Initializes the game's master volume
	 * @param masterVolume Game's master volume
	 */
	public static void initialize(int masterVolume){
		setMasterVolume(masterVolume);
	}
	
	/**
	 * Tries to look for the sound
	 * @return True if created properly, false otherwise
	 */
	private boolean findSound(){
        try{
			AudioInputStream stream = AudioSystem.getAudioInputStream(
				new File(Data.DIR_PATH + "res" + Data.SEP + "sfx" + Data.SEP + getName() + ".wav"));
			clip = (Clip)AudioSystem.getLine(new DataLine.Info(Clip.class, stream.getFormat()));
			clip.open(stream);
	        controller = (FloatControl)clip.getControl(FloatControl.Type.MASTER_GAIN);
		}catch(Exception e){
			e.printStackTrace();
			return false;	
		}
		return true;
	}
	
	/**
	 * @return Name of file in /res/sfx/
	 */
	public String getName(){
		return name;
	}
	
	/**
	 * Sets the name of file in /res/sfx/
	 * @param name Name of file in /res/sfx/
	 */
	public void setName(String name){
		this.name = name;
	}
	
	/**
	 * @return Volume of sound in range [0, 100]
	 */
	public int getVolume(){
		return volume;
	}
	
	/**
	 * Sets the volume of sound in range [0, 100]
	 * @param volume New volume
	 */
	public void setVolume(int volume){
		float newVol =
			((float)(volume * masterVolume) / 10000) * (controller.getMaximum() -
				controller.getMinimum()) + controller.getMinimum();
		if(newVol >= controller.getMinimum() && newVol <= controller.getMaximum()){
			this.volume = volume;
			controller.setValue(newVol);
		}else if(newVol < controller.getMinimum()){
			this.volume = 0;
			controller.setValue(controller.getMinimum());
		}else{
			this.volume = 100;
			controller.setValue(controller.getMaximum());
		}
	}
	
	/**
	 * @return Game's master volume in range [0, 100]
	 */
	public static int getMasterVolume(){
		return masterVolume;
	}
	
	/**
	 * Sets the game's master volume in range [0, 100]
	 * @param masterVolume New master volume
	 */
	public static void setMasterVolume(int masterVolume){
		if(masterVolume <= 100 && masterVolume >= 0){
			Sound.masterVolume = masterVolume;
		}else if(masterVolume < 0){
			Sound.masterVolume = 0;
		}else{
			Sound.masterVolume = 100;
		}
		Sound.masterVolumeStorage = Sound.masterVolume;
	}
	
	/**
	 * @return Whether the game has been muted
	 */
	public static boolean isMute(){
		return masterVolume == 0 && masterVolume != masterVolumeStorage;
	}
	
	/**
	 * Mutes the game
	 */
	public static void mute(){
		masterVolume = 0;
	}
	
	/**
	 * Unmutes the game
	 */
	public static void unmute(){
		masterVolume = masterVolumeStorage;
	}
	
	/**
	 * @return Times the sound loops on play
	 */
	public int getLoops(){
		return loops;
	}
	
	/**
	 * Sets the times the sound loops on play
	 * @param loops Times the sound loops on play
	 */
	public void setLoops(int loops){
		this.loops = loops;
	}
	
}