package com.desukase.engine;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

import com.desukase.engine.polygon.FirstPolygon;

/**
 * Loads text from a file and turns them into lots of polygons for displaying
 * @author John Du
 */
public class PreloadText{

	/** The polygons */
	private ArrayList<FirstPolygon> polygons = new ArrayList<FirstPolygon>();
	
	/**
	 * Constructor, loads a text file
	 * @param name Name of file in res/txt/
	 * @param letterSize Size of the letters
	 * @param column Column of text
	 * @param row Row of text
	 */
	public PreloadText(String name, int letterSize, int column, int row){
		try{
			Scanner reader = new Scanner(new File(Data.DIR_PATH + "res" + Data.SEP + "txt" + Data.SEP + name + ".txt"));
			int length = 0;
			while(reader.hasNext()){
				polygons.addAll(Text.placeText(reader.nextLine(), letterSize, column, row + length));
				length++;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * @return All of the polygons
	 */
	public ArrayList<FirstPolygon> getPolygons(){
		return polygons;
	}
	
}