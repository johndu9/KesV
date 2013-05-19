package com.desukase.engine;

/**
 * Toggle class, helpful with toggling stuff
 * @author John Du
 */
public class Toggle{
	
	/** The state of the toggle */
	private boolean state;
	/** Helper variable, whether or not something was pressed */
	private boolean wasPressed;
	
	/**
	 * Constructor, makes a new toggle
	 * @param state Whatever the state of the toggle should be at creation
	 */
	public Toggle(boolean state){
		this.state = state;
		wasPressed = false;
	}
	
	/**
	 * Updates the state of the toggle based on pressed, the boolean being monitored
	 * @param pressed Boolean monitored for updates, should be key or button state (isKeyPressed(key), isButtonPressed(button), etc)
	 */
	public void update(boolean pressed){
		if(pressed != wasPressed){
			if(pressed) state = !state;
			wasPressed = pressed;
		}
	}
	
	/**
	 * Sets state to whatever state isn't
	 */
	public void toggle(){
		state = !state;
	}
	
	/**
	 * Gets the current toggle state
	 * @return Toggle state
	 */
	public boolean getState(){
		return state;
	}
	
	/**
	 * Sets the current toggle state
	 * @param state Toggle state
	 */
	public void setState(boolean state){
		this.state = state;
	}
	
}