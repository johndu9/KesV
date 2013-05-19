package com.desukase.engine;

import java.util.Scanner;
import java.util.StringTokenizer;
import java.io.*;

/**
 * This class reads complex polygons (more than one square/triangle) from files and makes them into polygons.
 * @author John Du
 */
public class FontHandler{

	/** Reads files */
	private static Scanner fileReader;
	/** Used for tokenizing stuff */
	private static StringTokenizer tokens;
	
	/**
	 * Reads data from a path
	 * @param path File path of the polygon data
	 * @param delimiter Delimiter between polygon points
	 * @return Array of the pieces of the polygon
	 */
	private static String[][] readData(String path, String delimiter){
		if(!new File(path).exists()){
			return null;
		}
		String[][] returned;
		int size1 = 0;
		int size2;
		String cache = "";
		resetFileReader(path);
		while(fileReader.hasNext()){
			cache = fileReader.nextLine();
			size1++;
		}
		setTokens(cache, delimiter);
		size2 = tokens.countTokens();
		returned = new String[size1][size2];
		resetFileReader(path);
		for(int i = 0; fileReader.hasNext(); i++){
			setTokens(fileReader.nextLine(), delimiter);
			for(int j = 0; tokens.hasMoreTokens(); j++){
				returned[i][j] = tokens.nextToken();
			}
		}
		return returned;
	}
	
	/**
	 * Refines the String data into usable integer data
	 * @param data Unrefined String array data
	 * @return Refined integer array data
	 */
	private static int[][][] refineData(String[][] data){
		int[][] rawData = new int[data.length][data[0].length];
		for(int i = 0; i < data.length; i++){
			for(int j = 0; j < data[i].length; j++){
				rawData[i][j] = Integer.parseInt(data[i][j]);	
			}	
		}
		int mode[];
		int count = 1;
		for(int i = 0; i < rawData.length - 1; i++){
			if(rawData[i][0] != rawData[i + 1][0]) count++;	
		}
		mode = new int[count];
		for(int i = 0; i < mode.length; i++){
			count = 0;
			for(int j = 0; j < rawData.length; j++){
				if(rawData[j][0] == i) count++;	
			}
			mode[i] = count;	
		}
		int max = 0;
		for(int i = 0; i < mode.length; i++){
			if(mode[i] > max) max = mode[i];	
		}
		int[][][] newData = new int[rawData[rawData.length - 1][0] + 1][max][rawData[0].length - 1];
		for(int i = 0; i < newData.length; i++){
			for(int j = 0; j < newData[i].length; j++){
				for(int k = 0; k < newData[i][j].length; k++){
					newData[i][j][k] = rawData[j + i * max][k + 1];	
				}	
			}	
		}
		return newData;
	}
	
	/**
	 * Resizes several points by applying a size scale
	 * @param points Points to scale
	 * @param size Size scale to apply
	 * @return Resized points
	 */
	public static Point[] resizePoints(Point[] points, int size){
		Point[] newPoints = points;
		for(int i = 0; i < newPoints.length; i++){
			newPoints[i].x = points[i].x * size;
			newPoints[i].y = points[i].y * size;	
		}
		return newPoints;
	}
	
	/**
	 * Resizes several points by applying a size scale
	 * @param points Points to scale
	 * @param size Size scale to apply
	 * @return Resized points
	 */
	public static Point[][] resizePoints(Point[][] points, int size){
		Point[][] newPoints = points;
		for(int i = 0; i < newPoints.length; i++){
			newPoints[i] = resizePoints(newPoints[i], size);	
		}
		return newPoints;
	}

	/**
	 * Turns integer data into usable points
	 * @param data Refined integer data
	 * @return Points of the complex polygon
	 */
	public static Point[][] dataToPoints(int[][][] data){
		Point[][] points = new Point[data.length][data[0].length];
		for(int i = 0; i < data.length; i++){
			for(int j = 0; j < data[i].length; j++){
				points[i][j] = new Point(data[i][j][0], data[i][j][1]);
			}
		}
		return points;
	}
	
	/**
	 * Turns file data into usable points of a complex polygon
	 * @param path File path of the complex polygon
	 * @return Points of the complex polygon
	 */
	public static Point[][] dataToPoints(String path){
		return dataToPoints(refineData(readData(path, ",")));
	}
	
	/**
	 * Resets the file reader
	 * @param path Path of the file
	 */
	private static void resetFileReader(String path){
		try{
			fileReader = new Scanner(new File(path));
		}catch(FileNotFoundException e){
			fileReader = null;
			e.printStackTrace();	
		}
	}
	
	/**
	 * Sets the tokens
	 * @param string String to tokenize
	 * @param delimiter Delimiter between tokens
	 */
	private static void setTokens(String string, String delimiter){
		tokens = new StringTokenizer(string, delimiter);
	}
	
}