package com.desukase.engine;

/**
 * This class is used for getting deltas (time in milliseconds between each update) and delays using the System's timer.
 * To apply multiple delays of differing lengths, you should use multiple timers.
 * 
 * Darrien Singleton is free to use this. <3
 * Day-Singles
 * 
 * @author John Du
 */
public class Timer{
	
	/**
	 * Used for determining delta; the time in a previous iteration in a loop
	 */
	private static long lastTime;
	
	/**
	 * Used for determining whether or not a delay has passed
	 */
	private long lastRecordedTime;
	
	/**
	 * The time scale that acts as a multiplier for delta and delays
	 */
	private static double timerSpeed = 1.0;
	
	/**
	 * Constructs a timer for determining delay
	 */
	public Timer(){
		lastRecordedTime = getTime();
	}
	
	/**
	 * Gets the current time in milliseconds
	 * @return Current time in milliseconds
	 */
	public static long getTime(){
		return System.currentTimeMillis();
	}
	
	/**
	 * Gets the delta, or milliseconds between updates
	 * @return Delta, or milliseconds between updates, scaled by timerSpeed
	 */
	public static int getDelta(){
		long time = getTime();
		int delta = (int)(time - lastTime);
		lastTime = time;
		
		return (int)(delta * timerSpeed);
	}
	
	/**
	 * Whenever a delay is desired without using Thread.sleep, you can use if(getDelay(delay)) to achieve a similar effect.
	 * @param delay Delay desired in milliseconds
	 * @return Whether or not the delay has been passed
	 */
	public boolean getDelay(long delay){
		long time = getTime();
		if(time - lastRecordedTime > delay / timerSpeed){
			lastRecordedTime = time;
			return true;
		}
		return false;
	}
	
	/**
	 * Gets last recorded time for the delay
	 * @return Last recorded time for the delay
	 */
	public long getLastRecordedTime(){
		return lastRecordedTime;
	}
	
	/**
	 * Sets the last recorded time for the delay; useful for pause feature
	 * @param lastDelay Last recorded time for the delay
	 */
	public void setLastRecordedTime(long lastDelay){
		this.lastRecordedTime = lastDelay;
	}
	
	/**
	 * Gets the timer speed
	 * @return Timer speed
	 */
	public static double getTimerSpeed(){
		return timerSpeed;
	}
	
	/**
	 * Sets the timer speed
	 * @param timerSpeed Timer speed
	 */
	public static void setTimerSpeed(double timerSpeed){
		Timer.timerSpeed = timerSpeed;
	}
	
}