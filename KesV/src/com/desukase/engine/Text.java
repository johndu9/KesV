package com.desukase.engine;

import java.io.File;
import java.util.ArrayList;

import org.lwjgl.opengl.Display;

import com.desukase.engine.polygon.FirstPolygon;

/**
 * Class for creating and using text polygons
 * @author John Du
 */
public class Text{
	
	/** All the characters we have for display */
	public static final String characters = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./ ";
	/** Pixels from the left */
	public static final int leftMargin = 32;
	/** Pixels from the top */
	public static final int topMargin = 32;
	/** Pixels between each column */
	public static final int columnConstant = 8;
	/** Pixels between each row */
	public static final int rowConstant = 32;
	/** Holds all the text polygons */
	private static FirstPolygon[][][] text = new FirstPolygon[6][characters.length()][];
	/** Default text color */
	private static Color textColor = new Color(0, 0, 0, 0);
	/** Whether we want to show the text */
	public static Toggle showText = new Toggle(true);
	
	/** Initializes the text and sets it to a color */
	public static void initialize(Color color){
		setTextColor(color);
		for(int letSize = 0; letSize < text.length; letSize++){
			for(int i = 0; i < characters.length(); i++){
				ArrayList<FirstPolygon> textPolygons = getTextFromFile(
			    	characters.substring(i, i + 1),
			    	letSize + 1,
			    	new Point(0, 0),
			    	textColor);
				text[letSize][i] = new FirstPolygon[textPolygons.size()];
				for(int j = 0; j < text[letSize][i].length; j++){
					text[letSize][i][j] = textPolygons.get(j);					
				}	
			}	
		}
	}
	
	/**
	 * Gets what the bottom row would be based on the letter size
	 * @param letterSize Letter size
	 * @return Bottom row
	 */
	public static int getBottomRow(int letterSize){
		return (Display.getHeight() - rowConstant / letterSize) / rowConstant - 1;
	}
	
	/**
	 * Gets the column on the far right based on the letter size and length of the string
	 * @param length Length of string
	 * @param letterSize Letter size
	 * @return Right-most row
	 */
	public static int getRightColumn(int length, int letterSize){
		return (Display.getWidth() - letterSize * length * columnConstant) / (columnConstant * letterSize) - letterSize - 1;
	}
	
	/**
	 * Turns a letter size/column/row combination into a usable point
	 * @param letterSize Letter size 
	 * @param column Column of text
	 * @param row Row of text
	 * @return Calculated point
	 */
	public static Point columnRowToPoint(int letterSize, int column, int row){
		return
			new Point(
				leftMargin + column * columnConstant * letterSize + letterSize + columnConstant,
				topMargin + row * rowConstant + 4 * letterSize);
	}
	
	/**
	 * Places the text so that we can actually see it
	 * @param string Text to display
	 * @param letterSize Letter size
	 * @param position Position of text
	 * @return Polygons we can use to display
	 */
	public static ArrayList<FirstPolygon> placeText(String string, int letterSize, Point position){
		ArrayList<FirstPolygon> polygons = new ArrayList<FirstPolygon>();
		if(showText.getState()){
			polygons.addAll(Text.getText(
				string,
				letterSize,
				new Point(position.x + letterSize, position.y + letterSize),
				getShadowColor()
				));
			polygons.addAll(Text.getText(string, letterSize, new Point(position.x, position.y), textColor));	
		}
		return polygons;
	}
	
	/**
	 * Places the text so that we can actually see it
	 * @param string Text to display
	 * @param letterSize Letter size 
	 * @param column Column of text
	 * @param row Row of text
	 * @return Polygons we can use to display
	 */
	public static ArrayList<FirstPolygon> placeText(String string, int letterSize, int column, int row){
		return placeText(string, letterSize, columnRowToPoint(letterSize, column, row));
	}
	
	/**
	 * Gets polygons we can use for displaying text
	 * @param string Text to display
	 * @param letterSize Letter size
	 * @param position Position of text
	 * @param color Color of text
	 * @return Polygons we can use to display
	 */
	public static ArrayList<FirstPolygon> getText(String string, int letterSize, Point position, Color color){
		ArrayList<FirstPolygon> polygons = new ArrayList<FirstPolygon>();
		for(int i = 0; i < string.length(); i++){
			if(!string.substring(i, i + 1).equals(" ")){
				int index = characters.indexOf(string.substring(i, i + 1).toUpperCase());
				for(int j = 0; j < text[letterSize - 1][index].length; j++){
					polygons.add(new FirstPolygon(
						text[letterSize - 1][index][j].getPoints(),
						0,
						new Point(position.x + 8 * i * letterSize, position.y),
						color));	
				}	
			}	
		}
		return polygons;
	}
	
	/**
	 * Gets text polygons from file
	 * @param string Text to display
	 * @param letterSize Letter size 
	 * @param position Position of text
	 * @param color Color of text
	 * @return Polygons we can use to display
	 */
	public static ArrayList<FirstPolygon> getTextFromFile(String string, int letterSize, Point position, Color color){
		Point[][] points;
		String[] fileNames = checkSymbol(string);
		ArrayList<FirstPolygon> polygons = new ArrayList<FirstPolygon>();
		for(int i = 0; i < string.length(); i++){
			points = FontHandler.resizePoints(
				FontHandler.dataToPoints(Data.DIR_PATH + "res" + Data.SEP + "fnt" + Data.SEP + fileNames[i] + ".txt"), letterSize);
			for(int j = 0; j < points.length; j++){
				polygons.add(new FirstPolygon( points[j], 0, new Point(position.x + 8 * i * letterSize, position.y), color));	
			}
		}
		return polygons;
	}
	
	/**
	 * @return Calculated color of text shadow
	 */
	public static Color getShadowColor(){
		return new Color(textColor.getRed() / 2, textColor.getGreen() / 2, textColor.getBlue() / 2, textColor.getAlpha() / 2);
	}

	/**
	 * Gets the files of all the characters in a string
	 * @param string String to check 
	 * @return File names of all the characters in the string
	 */
	private static String[] checkSymbol(String string){
		String[] fileNames = new String[string.length()];
		for(int i = 0; i < string.length(); i++){
			fileNames[i] = string.substring(i, i + 1).toUpperCase();
			if(fileNames[i].equals("_")) fileNames[i] = "_underscore";
			else if(fileNames[i].equals(".")) fileNames[i] = "_period";
			else if(fileNames[i].equals(":")) fileNames[i] = "_colon";
			else if(fileNames[i].equals("-")) fileNames[i] = "_dash";
			else if(fileNames[i].equals("!")) fileNames[i] = "_exclamation";
			else if(fileNames[i].equals("'")) fileNames[i] = "_apostrophe";
			else if(fileNames[i].equals("`")) fileNames[i] = "_grave";
			else if(fileNames[i].equals("\"")) fileNames[i] = "_quote";
			else if(fileNames[i].equals(",")) fileNames[i] = "_comma";
			else if(fileNames[i].equals(";")) fileNames[i] = "_semicolon";
			else if(fileNames[i].equals("+")) fileNames[i] = "_plus";
			else if(fileNames[i].equals("*")) fileNames[i] = "_asterisk";
			else if(fileNames[i].equals("?")) fileNames[i] = "_question";
			else if(fileNames[i].equals("/")) fileNames[i] = "_fslash";
			else if(fileNames[i].equals("\\")) fileNames[i] = "_bslash";
			else if(fileNames[i].equals("=")) fileNames[i] = "_equals";
			else if(fileNames[i].equals("(")) fileNames[i] = "_lparen";
			else if(fileNames[i].equals(")")) fileNames[i] = "_rparen";
			else if(fileNames[i].equals("~")) fileNames[i] = "_tilde";
			else if(fileNames[i].equals("@")) fileNames[i] = "_at";
			else if(fileNames[i].equals("#")) fileNames[i] = "_pound";
			else if(fileNames[i].equals("$")) fileNames[i] = "_dollar";
			else if(fileNames[i].equals("<")) fileNames[i] = "_lthan";
			else if(fileNames[i].equals(">")) fileNames[i] = "_mthan";
			else if(fileNames[i].equals("^")) fileNames[i] = "_caret";
			else if(fileNames[i].equals("&")) fileNames[i] = "_ampersand";
			else if(fileNames[i].equals("[")) fileNames[i] = "_lbracket";
			else if(fileNames[i].equals("]")) fileNames[i] = "_rbracket";
			else if(fileNames[i].equals("{")) fileNames[i] = "_lbrace";
			else if(fileNames[i].equals("}")) fileNames[i] = "_rbrace";
			else if(fileNames[i].equals("|")) fileNames[i] = "_bar";
			else if(fileNames[i].equals("%")) fileNames[i] = "_percent";
			else if(
				fileNames[i].equals(" ") ||
				!new File(Data.DIR_PATH + "res" + Data.SEP + "fnt" + Data.SEP + fileNames[i] + ".txt").exists())
				fileNames[i] = "_space";
		}
		return fileNames;
	}
	
	/**
	 * Sets the text color
	 * @param color New text color
	 */
	public static void setTextColor(Color color){
		textColor = color;
	}
	
	/**
	 * @return Text color
	 */
	public static Color getTextColor(){
		return textColor;
	}
	
}