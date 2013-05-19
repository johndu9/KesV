package com.desukase.engine;

import java.util.ArrayList;

import com.desukase.engine.polygon.FirstPolygon;

/**
 * Convenience class for making pages of text 
 * @author John Du
 */
public class TextScreen{
	
	/** Loaded text */
	private PreloadText[] textPages;
	/** Number of pages */
	private int pages;
	/** Name of text screen */
	private String name;
	/** Miniature menu of moving back and forth between pages */
	private static Menu backnext =
		new Menu(new String[]{"Back", "Next"}, 2, 33, 16, 2, false, true);
	/** Shows how many pages there are */
	private static Menu pageMenu =
		new Menu(new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", ""}, 2, 6, 16, 2, false, false);
	
	/**
	 * Constructor, makes a text screen
	 * @param name Name of text screen
	 * @param pages Pages
	 */
	public TextScreen(String name, int pages){
		textPages = new PreloadText[pages];
		setName(name);
		setPages(pages);
		refresh();
	}
	
	/**
	 * Places the screen polygons so we can see it
	 * @return Polygons for display
	 */
	public ArrayList<FirstPolygon> placeScreen(){
		ArrayList<FirstPolygon> polygons = new ArrayList<FirstPolygon>();
		polygons.addAll(Text.placeText("Page: ", 2, 0, 16));
		polygons.addAll(pageMenu.placeMenu(0, pages));
		polygons.addAll(backnext.placeMenu());
		polygons.addAll(textPages[pageMenu.isSelected()].getPolygons());
		return polygons;
	}
	
	/**
	 * @return Whether we're done with the text screen
	 */
	public boolean done(){
		if(backnext.isSelected("Back") || backnext.isSelected("Next")){
			if(
				(backnext.isSelected("Back") && pageMenu.isSelected() == 0) ||
				(backnext.isSelected("Next") && pageMenu.isSelected() == pages - 1)){
				backnext.confirmSelect();
				pageMenu.confirmSelect();
				return true;
			}else{
				int selected = pageMenu.confirmSelect();
				if(backnext.isSelected("Back")){
					pageMenu.select(selected - 1);
				}
				else if(backnext.isSelected("Next")){
					pageMenu.select(selected + 1);
				}
				backnext.confirmSelect();
			}
		}
		return false;
	}
	
	/**
	 * @return Number of pages
	 */
	public int getPages(){
		return pages;
	}
	
	/**
	 * Sets the number of pages
	 * @param pages Number of pages
	 */
	public void setPages(int pages){
		this.pages = pages;
	}
	
	/**
	 * @return Screen name
	 */
	public String getName(){
		return name;
	}
	
	/**
	 * Sets the screen name
	 * @param name Screen name
	 */
	public void setName(String name){
		this.name = name;
	}
	
	/**
	 * Select the page
	 * @param index Index of page to select
	 */
	public void select(int index){
		pageMenu.select(index);
	}
	
	/**
	 * Refreshes the screen
	 */
	public void refresh(){
		for(int i = 0; i < textPages.length; i++){
			textPages[i] = new PreloadText(name + i, 2, 0, 1);
		}
		select(0);
	}
	
}