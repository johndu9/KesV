package com.desukase.engine;

import java.util.ArrayList;

import org.lwjgl.input.Keyboard;

import com.desukase.engine.polygon.FirstPolygon;

/**
 * Convenience class for arranging a bunch of buttons into a usable menu
 * @author John Du
 */
public class Menu{
	
	/** Buttons used in the menu */
	private Button[] buttons;
	/** Letter size in the buttons */
	private int letterSize;
	/** Column of menu */
	private int column;
	/** Row of menu */
	private int row;
	/** Spacing between buttons */
	private int space;
	/** Whether the menu is vertical or horizontal */
	private boolean vertical;
	
	/**
	 * Constructor, makes a menu
	 * @param buttonNames Names of buttons
	 * @param letterSize Letter size in the buttons
	 * @param column Column of menu
	 * @param row Row of menu 
	 * @param space Spacing between buttons
	 * @param vertical Whether the menu is vertical or horizontal
	 * @param selectable Whether the buttons can be selected
	 */
	public Menu(String[] buttonNames, int letterSize, int column, int row, int space, boolean vertical, boolean selectable){
		setVertical(vertical);
		setSpace(space);
		buttons = new Button[buttonNames.length];
		int columnAdd = 0;
		int rowAdd = 0;
		for(int i = 0; i < buttonNames.length; i++){
			if(vertical){
				rowAdd = (1 + space) * i;
			}else if(i > 0){
				columnAdd += buttonNames[i - 1].length() + space;
			}
			buttons[i] = new Button(buttonNames[i], letterSize, column + columnAdd, row + rowAdd, selectable);
		}
	}
	
	/**
	 * Builds all the polygons needed for rendering in a range
	 * @param beginIndex The first button to render
	 * @param endIndex The last button to render
	 * @return The polygons needed for rendering
	 */
	public ArrayList<FirstPolygon> placeMenu(int beginIndex, int endIndex){
		ArrayList<FirstPolygon> polygons = new ArrayList<FirstPolygon>();
		for(int i = beginIndex; i < endIndex; i++){
			polygons.addAll(buttons[i].placeButton());
		}
		return polygons;
	}
	
	/**
	 * Builds all the polygons needed for rendering
	 * @return The polygons needed for rendering
	 */
	public ArrayList<FirstPolygon> placeMenu(){
		return placeMenu(0, buttons.length);
	}
	
	/**
	 * @param name Name of button
	 * @return Whether the button is selected
	 */
	public boolean isSelected(String name){
		for(int i = 0; i < buttons.length; i++){
			if(buttons[i].getString().equals(name)){
				return buttons[i].isSelected();
			}
		}
		return false;
	}
	
	/**
	 * @param index Index of button
	 * @return Whether the button is selected
	 */
	public boolean isSelected(int index){
		if(index < 0 || buttons.length - 1 < index){
			return false;
		}
		return buttons[index].isSelected();
	}
	
	/**
	 * @return Selected button, -1 if no button is selected
	 */
	public int isSelected(){
		for(int i = 0; i < buttons.length; i++){
			if(buttons[i].isSelected()){
				return i;
			}
		}
		return -1;
		
	}
	
	/**
	 * Confirms the selection
	 * @param name Button name
	 * @return The confirmation
	 */
	public boolean confirmSelect(String name){
		for(int i = 0; i < buttons.length; i++){
			if(buttons[i].getString().equals(name)){
				return buttons[i].confirmSelect();
			}
		}
		return false;
	}
	
	/**
	 * Confirms the selection
	 * @param name Button name
	 * @param key Key to confirm
	 * @return The confirmation
	 */
	public boolean confirmSelect(String name, int key){
		if(Keyboard.isKeyDown(key)){
			for(int i = 0; i < buttons.length; i++){
				if(buttons[i].getString().equals(name)) buttons[i].select();
			}
		}
		return confirmSelect(name);
	}
	
	/**
	 * Confirms the selection
	 * @param index Button index
	 * @return The confirmation
	 */
	public boolean confirmSelect(int index){
		if(index < 0 || buttons.length - 1 < index){
			return false;
		}
		return buttons[index].confirmSelect();
		
	}
	
	/**
	 * Confirms whatever button is selected
	 * @return The confirmation
	 */
	public int confirmSelect(){
		for(int i = 0; i < buttons.length; i++){
			if(buttons[i].confirmSelect()){
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * @param beginIndex Beginning of index range 
	 * @param endIndex End of index range
	 * @return Whether any button is selected in a range
	 */
	public boolean anySelected(int beginIndex, int endIndex){
		if(beginIndex < 0 || endIndex > buttons.length){
			return false;
		}
		for(int i = beginIndex; i < endIndex; i++){
			if(buttons[i].isSelected()){
				return true;	
			}
		}
		return false;
	}
	
	/**
	 * @return Whether any button is selected
	 */
	public boolean anySelected(){
		return anySelected(0, buttons.length);
	}
	
	/**
	 * Selects a button at a given index
	 * @param index Index of button to select
	 */
	public void select(int index){
		if(index >= 0 && index < buttons.length - 1){
			buttons[index].select();
		}
	}
	
	/**
	 * Selects a button of a given name
	 * @param name Name of button to select
	 */
	public void select(String name){
		for(int i = 0; i < buttons.length; i++){
			if(buttons[i].getString().equals(name)){
				buttons[i].select();
			}
		}
	}
	
	/**
	 * @param index Index of button
	 * @return Name of button at given index
	 */
	public String getButtonName(int index){
		if(index >= 0 && index < buttons.length){
			return getButtonNames()[index];
		}else{
			return "";
		}
	}
	
	/**
	 * @return Names of all the buttons
	 */
	public String[] getButtonNames(){
		String[] names = new String[buttons.length];
		for(int i = 0; i < names.length; i++){
			names[i] = buttons[i].getString();
		}
		return names;
	}
	
	/**
	 * @return Letter size
	 */
	public int getLetterSize(){
		return letterSize;
	}
	
	/**
	 * Sets the letter size
	 * @param letterSize Letter size
	 */
	public void setLetterSize(int letterSize){
		this.letterSize = letterSize;
	}
	
	/**
	 * @return Menu column
	 */
	public int getColumn(){
		return column;
	}
	
	/**
	 * Sets the menu column
	 * @param column Menu column
	 */
	public void setColumn(int column){
		this.column = column;
	}
	
	/**
	 * @return Menu row
	 */
	public int getRow(){
		return row;
	}
	
	/**
	 * Sets the menu row
	 * @param row Menu row
	 */
	public void setRow(int row){
		this.row = row;
	}
	
	/**
	 * @return Whether the menu is vertical
	 */
	public boolean isVertical(){
		return vertical;
	}
	
	/**
	 * Set whether the menu is vertical
	 * @param vertical Verticality of the menu
	 */
	public void setVertical(boolean vertical){
		this.vertical = vertical;
	}
	
	/**
	 * @return Spacing between each button
	 */
	public int getSpace(){
		return space;
	}
	
	/**
	 * Sets the spacing between each button
	 * @param space Spacing between each button
	 */
	public void setSpace(int space){
		this.space = space;
	}
	
	/**
	 * @return Number of buttons
	 */
	public int size(){
		return buttons.length;
	}
	
}