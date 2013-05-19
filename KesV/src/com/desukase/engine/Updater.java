package com.desukase.engine;

/**
 * Keeps code from becoming a massive mess in a single class 
 * @author John Du
 */
public interface Updater{

	/** Initializes the updater */
	public void initialize();
	/** Updates the updater */
	public void update(int delta);
	
}