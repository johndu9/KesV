package com.desukase.engine;

import java.util.ArrayList;

import org.lwjgl.input.Mouse;

import com.desukase.engine.polygon.FirstPolygon;

/**
 * Contains everything required for making a button, generally useful
 * @author John Du
 */
public class Button{
	
	/** The polygon used for selection */
	private FirstPolygon hitbox;
	/** Toggle for selection */
	private Toggle selected;
	/** String displayed on the button */
	private String string;
	/** Size of the string displayed */
	private int letterSize;
	/** Column to place the button */
	private int column;
	/** Row to place the button */
	private int row;
	/** Whether we can select the button */
	private boolean selectable;
	
	/** Whether we want to show the buttons at all */
	public static Toggle showButtons = Text.showText;
	
	/**
	 * Constructor, makes a button
	 * @param string String displayed on the button
	 * @param letterSize Size of the string displayed
	 * @param column Column to place the button
	 * @param row Row to place the button
	 * @param selectable Whether we can select the button
	 */
	public Button(String string, int letterSize, int column, int row, boolean selectable){
		setString(string);
		setLetSize(letterSize);
		setColumn(column);
		setRow(row);
		setSelectable(selectable);
		selected = new Toggle(false);
	}
	
	/**
	 * @return The polygons required for seeing the button
	 */
	public ArrayList<FirstPolygon> placeButton(){
		ArrayList<FirstPolygon> polygons = new ArrayList<FirstPolygon>();
		setHitbox();
		if(isSelectable()){
			if(inHitbox(Mouse.getX(), Mouse.getY())){
				selected.update(Mouse.isButtonDown(0));
			}
		}
		if(showButtons.getState()){
			if(selected.getState() || (isSelectable() && inHitbox(Mouse.getX(), Mouse.getY()))){
				polygons.add(hitbox);
			}
			polygons.add(hitbox);
		}
		polygons.addAll(Text.placeText(string, letterSize, column, row));
		return polygons;
	}
	
	/**
	 * @param x The x coordinate
	 * @param y The y coordinate
	 * @return Whether the point is in the polygon
	 */
	public boolean inHitbox(int x, int y){
		return hitbox.isInPolygon(x, y);
	}
	
	/**
	 * @return Confirms the selection and, if selected, toggles the selection state off
	 */
	public boolean confirmSelect(){
		if(selected.getState()){
			selected.toggle();
			return true;	
		}
		return false;
	}
	
	/**
	 * Selects the button
	 */
	public void select(){
		selected.setState(true);
	}
	
	/**
	 * @return Whether the button is selected
	 */
	public boolean isSelected(){
		return selected.getState();
	}
	
	/**
	 * @return String displayed on the button
	 */
	public String getString(){
		return string;
	}
	
	/**
	 * @param string String displayed on the button
	 */
	public void setString(String string){
		this.string = string;
	}
	
	/**
	 * @return Size of the string displayed
	 */
	public int getLetSize(){
		return letterSize;
	}
	
	/**
	 * @param letSize Size of the string displayed
	 */
	public void setLetSize(int letSize){
		this.letterSize = letSize;
	}
	
	/**
	 * @return Column to place the button
	 */
	public int getColumn(){
		return column;
	}
	
	/**
	 * @param column Column to place the button
	 */
	public void setColumn(int column){
		this.column = column;
	}
	
	/**
	 * @return Row to place the button
	 */
	public int getRow(){
		return row;
	}
	
	/**
	 * @param row Row to place the button
	 */
	public void setRow(int row){
		this.row = row;
	}
	
	/**
	 * @return Whether we can select the button
	 */
	public boolean isSelectable(){
		return selectable;
	}
	
	/**
	 * @param selectable Whether we can select the button
	 */
	public void setSelectable(boolean selectable){
		this.selectable = selectable;
	}
	
	/**
	 * Sets/updates the hitbox
	 */
	private void setHitbox(){
		hitbox =
			new FirstPolygon(
				FirstPolygon.sizeToPoints(
					string.length() * Text.columnConstant * letterSize,
					Text.rowConstant * letterSize / 2),
				0,
				new Point(
					Text.leftMargin + string.length() * Text.columnConstant * letterSize / 2 -
						Text.columnConstant * letterSize / 2 + Text.columnConstant * column * letterSize +
						5 * letterSize,
				Text.rowConstant * (row + 1) + 4 * letterSize),
				Text.getShadowColor()
				);
	}
	
}