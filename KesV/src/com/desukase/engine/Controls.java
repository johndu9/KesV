package com.desukase.engine;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

/**
 * Holds key and button control states, plus it's got all this neat updating stuff
 * @author John Du
 */
public class Controls{
	
	/** The control values */
	private int[] controls;
	/** The control states (pressed = true) */
	private boolean[] states;
	/** Standard WASD keys */
	public static final Controls WASD_MOVEMENT = new Controls(new String[]{"W", "A", "S", "D"});
	/** All the mouse buttons */
	public static final Controls MOUSE_ACTIONS = new Controls(new String[]{"BUTTON0", "BUTTON1"});
	/** All the numbers */
	public static final Controls NUMBERS = new Controls(new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "0"});
	/** Empty controls */
	public static final Controls EMPTY = new Controls(new String[]{});
	
	/**
	 * Constructor, makes controls from values
	 * @param controls Key/Button values
	 */
	public Controls(int[] controls){
		setControls(controls);
	}
	
	/**
	 * Constructor, makes controls from key/button names
	 * @param controls Key/Button names
	 */
	public Controls(String[] controls){
		this(controlsToValues(controls));
	}
	
	/**
	 * Updates the states of the controls
	 */
	public void update(){
		for(int i = 0; i < states.length; i++){
			if(controls[i] <= 0){
				states[i] = Mouse.isButtonDown(-controls[i]);
			}else if(controls[i] != 0){
				states[i] = Keyboard.isKeyDown(controls[i]);
			}else{
				states[i] = false;
			}
		}
	}
	
	/**
	 * Gives you the value the key/button at a given index
	 * @param index Whichever key/button value you want from the controls
	 * @return The value of the key/button at the index
	 */
	public int getControl(int index){
		return controls[index];
	}
	
	/**
	 * Sets the control at the given index
	 * @param control Whichever key/button value you want it to be
	 * @param index The index of the key/button
	 */
	public void setControl(int index, int control){
		controls[index] = control;
	}
	
	/**
	 * Gives you the state of the key/button at a given index
	 * @param index The key/button's state's index
	 * @return The state of the key/button at the index
	 */
	public boolean getState(int index){
		return states[index];
	}
	
	/**
	 * Gives you the state of the key/button that you name
	 * @param control The name of the key/button you're looking for
	 * @return The state of the key/button you specify
	 */
	public boolean getState(String control){
		for(int i = 0; i < controls.length; i++){
			if(controlToValue(control) == controls[i]){
				return states[i];
			}
		}
		return false;
	}
	
	/**
	 * @return The controls' key/button values
	 */
	public int[] getControls(){
		return controls;
	}
	
	/**
	 * Sets the controls
	 * @param controls Key/Button values for the controls
	 */
	public void setControls(int[] controls){
		this.controls = controls;
		states = new boolean[controls.length];
	}
	
	/**
	 * Gives you however many controls there are
	 * @return However many controls there are
	 */
	public int size(){
		return controls.length;
	}
	
	/**
	 * Combines controls together into one big controls
	 * @param controlsSet Set of controls you want combined
	 * @return Combined set of controls
	 */
	public static Controls combineControls(Controls[] controlsSet){
		int totalSize = 0;
		for(int i = 0; i < controlsSet.length; i++){
			totalSize += controlsSet[i].size();
		}
		
		int[] values = new int[totalSize];
		int counter = 0;
		for(int i = 0; i < controlsSet.length; i++){
			for(int j = 0; j < controlsSet[i].size(); j++){
				values[counter] = controlsSet[i].getControl(j);
				counter++;
			}
		}
		
		return new Controls(values);
	}
	
	/**
	 * Converts a key/button name to a usable value
	 * @param control The name of the key/button you want to convert
	 * @return The value of the key/button
	 */
	public static int controlToValue(String control){
		if(control.length() > "BUTTON".length() && control.substring(0, "BUTTON".length()).equals("BUTTON")){
			return -Integer.parseInt(control.substring("BUTTON".length(), control.length()));
		}else{
			return getKeyValue(control);
		}
	}
	
	/**
	 * Converts key/button names to usable values
	 * @param controls Names of the keys/buttons you want to convert
	 * @return The values of the keys/buttons
	 */
	public static int[] controlsToValues(String[] controls){
		int[] values = new int[controls.length];
		for(int i = 0; i < values.length; i++){
			values[i] = controlToValue(controls[i]);
		}
		return values;
	}
	
	/**
	 * Gives you the name of a key
	 * @param keyValue The value of the key
	 * @return The name of the key, returns NOTAKEY if the value does not correspond to a name
	 */
	public static String getKeyName(int keyValue){
		return Keyboard.getKeyName(keyValue);
	}
	
	/**
	 * Gives you the value of a key
	 * @param keyName The name of the key
	 * @return The value of the key, returns Integer.MIN_VALUE if the name does not correspond to a value
	 */
	public static int getKeyValue(String keyName){
		return Keyboard.getKeyIndex(keyName);
	}
	
	public String toString(){
		String returned = "";
		for(int i = 0; i < controls.length; i++){
			returned += getKeyName(controls[i]);
			if(i < controls.length - 1){
				returned += ",";
			}
		}
		return returned;
	}
	
}