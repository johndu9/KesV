package com.desukase.engine;

import org.newdawn.slick.openal.Audio;
import org.newdawn.slick.openal.AudioLoader;
import org.newdawn.slick.util.ResourceLoader;

/**
 * Class used for playing and using sounds
 * @author John Du
 */
public class Sound{
	
    /** Name of file in /res/sfx/ */
	private String name;
	/**	Whether it's music */
	private boolean isMusic;
	/** Whether it loops */
	private boolean loops;
	/** The position of the sound */
	private float position = 0;
	/** Used for actually playing the sound */
	private Audio sound;
	/** Whether it initialized properly */
	private boolean initialized = false;
	/** Game's master volume */
	private static float masterVolume = 1.0f;
	/** Non-muted master volume */
	private static float masterVolumeStorage;
	
	/**
	 * Constructor, makes a sound
	 * @param name Name of file in /res/sfx/
	 * @param isMusic Whether it's music
	 * @param loops Whether it loops
	 * @param stream Whether it streams
	 */
	public Sound(String name, boolean isMusic, boolean loops, boolean stream){
		setName(name);
		this.isMusic = isMusic;
		this.loops = loops;
		initialized = findSound(stream);
		if(!initialized){
			System.out.println("Problem encountered initializing sound");
		}
	}
	
	/**
	 * Plays the sound
	 * @param pitch Pitch shift
	 * @param gain Gain change
	 */
	public void play(float pitch, float gain){
		if(initialized){
			if(isMusic){
				sound.playAsMusic(pitch, gain * masterVolume, loops);			
			}else{
				sound.playAsSoundEffect(pitch, gain * masterVolume, loops);
			}	
		}
	}
	
	/**
	 * Pauses the sound
	 */
	public void pause(){
		if(initialized){
			position = sound.getPosition();
			sound.stop();
		}
	}
	
	/**
	 * Resumes the sound
	 * @param pitch Pitch shift
	 * @param gain Gain change
	 */
	public void resume(float pitch, float gain){
		if(initialized){
			play(pitch, gain);
			sound.setPosition(position);
		}
	}
	
	/**
	 * @return Current position of the sound
	 */
	public float getPosition(){
		if(initialized){
			return sound.getPosition();
		}
		return 0.0f;
	}
	
	/**
	 * Sets the position in the sound
	 * @param seconds Position of playback
	 */
	public void setPosition(float seconds){
		if(initialized){
			sound.setPosition(seconds);
		}
	}
	
	/**
	 * Tries to look for the sound
	 * @param stream Whether we want to stream it
	 * @return True if created properly, false otherwise
	 */
	private boolean findSound(boolean stream){
        try{
        	String type = name.substring(name.length() - 3).toUpperCase();
        	String path = Data.DIR_PATH + "res" + Data.SEP + "sfx" + Data.SEP + name;
        	if(stream){
        		sound = AudioLoader.getStreamingAudio(type, ResourceLoader.getResource(path));
        	}else{
        		sound = AudioLoader.getAudio(type, ResourceLoader.getResourceAsStream(path));
        	}
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
	 * @return Game's master volume in range [0.0f, 1.0f]
	 */
	public static float getMasterVolume(){
		return masterVolume;
	}
	
	/**
	 * Sets the game's master volume in range [0.0f, 1.0f]
	 * @param masterVolume New master volume
	 */
	public static void setMasterVolume(float masterVolume){
		if(masterVolume <= 1.0f && masterVolume >= 0.0f){
			Sound.masterVolume = masterVolume;
		}else if(masterVolume < 0.0f){
			Sound.masterVolume = 0.0f;
		}else{
			Sound.masterVolume = 1.0f;
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
	
}