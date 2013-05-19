package com.desukase.engine;

/**
 * Simple value storage, used as color
 * @author John Du
 */
public class Color{

	/** Color values, {red, green, blue, alpha} */
	private float[] values = new float[4];
	
	/**	Red index */
	public static final int RED = 0;
	/**	Green index */
	public static final int GREEN = 1;
	/**	Blue index */
	public static final int BLUE = 2;
	/**	Alpha index */
	public static final int ALPHA = 3;
	
	/**
	 * Constructor, makes a color
	 * @param values Color values, {red, green, blue, alpha}
	 */
	public Color(float[] values){
		this(values[RED], values[GREEN], values[BLUE], values[ALPHA]);
	}
	
	/**
	 * Constructor, makes a color
	 * @param red The red-ity
	 * @param green The green-ness
	 * @param blue The blue-acity
	 * @param alpha The alpha-bility
	 */
	public Color(float red, float green, float blue, float alpha){
		setRed(red);
		setGreen(green);
		setBlue(blue);
		setAlpha(alpha);
	}
	
	/**
	 * Constructor, remakes a color
	 * @param color Color to copy
	 */
	public Color(Color color) {
		this(color.values);
	}

	/**
	 * Adds two colors together and makes a color baby
	 * @param color1 The first color
	 * @param color2 The second color
	 * @param alpha The alpha of the resulting color
	 * @return The combined colors with a given alpha
	 */
	public static Color addColors(Color color1, Color color2, float alpha){
		float[] values = new float[4];
		for(int i = 0; i < values.length - 1; i++){
			values[i] = color1.getValue(i) + color2.getValue(i);
		}
		values[3] = alpha;
		return new Color(values);
	}
	
	/**
	 * Adds two colors together and makes a color baby
	 * @param color1 The first color
	 * @param color2 The second color
	 * @return The combined colors
	 */
	public static Color addColors(Color color1, Color color2){
		return addColors(color1, color2, color1.getAlpha() + color2.getAlpha());
	}
	
	/**
	 * Subtracts one color from another
	 * @param color1 The color subtracted from
	 * @param color2 The color subtracted
	 * @param alpha The alpha of the resulting color
	 * @return The color resulting from subtracting color2 from color1 with a given alpha
	 */
	public static Color subtractColors(Color color1, Color color2, float alpha){
		float[] values = new float[4];
		for(int i = 0; i < values.length - 1; i++){
			values[i] = color1.getValue(i) - color2.getValue(i);
		}
		values[3] = alpha;
		return new Color(values);
	}

	/**
	 * Subtracts one color from another
	 * @param color1 The color subtracted from
	 * @param color2 The color subtracted
	 * @return The color resulting from subtracting color2 from color1
	 */
	public static Color subtractColors(Color color1, Color color2){
		return addColors(color1, color2, color1.getAlpha() - color2.getAlpha());
	}

	/**
	 * @return Color values, {red, green, blue, alpha}
	 */
	public float[] getValues(){
		return values;
	}

	/**
	 * @param values Color values, {red, green, blue, alpha}
	 */
	public void setValues(float[] values){
		this.values = values;
	}

	/**
	 * @param index Index of color value
	 * @return Color value at given index
	 */
	public float getValue(int index){
		return values[index];
	}

	/**
	 * @param index Index of color value
	 * @param value Color value at given index
	 */
	public void setValue(int index, float value){
		values[index] = value;
	}

	/**
	 * @return The red-ity
	 */
	public float getRed(){
		return values[0];
	}

	/**
	 * @param red The red-ity
	 */
	public void setRed(float red){
		values[0] = red;
	}

	/**
	 * @return The green-ness
	 */
	public float getGreen(){
		return values[1];
	}

	/**
	 * @param green The green-ness
	 */
	public void setGreen(float green){
		values[1] = green;
	}

	/**
	 * @return The blue-acity
	 */
	public float getBlue(){
		return values[2];
	}

	/**
	 * @param blue The blue-acity
	 */
	public void setBlue(float blue){
		values[2] = blue;
	}

	/**
	 * @return The alpha-bility
	 */
	public float getAlpha(){
		return values[3];
	}

	/**
	 * @param alpha The alpha-bility
	 */
	public void setAlpha(float alpha){
		values[3] = alpha;
	}
	
	public String toString(){
		String returned = "";
		for(int i = 0; i < values.length; i++){
			returned += values[i];
			if(i < values.length - 1) returned += ",";
		}
		return returned;
	}
	
	/**
	 * Parses a string and gets a color out of it
	 * @param toParse String to parse
	 * @return Color resulting from the parse
	 */
	public static Color parseColor(String toParse){
		Color color = new Color(0, 0, 0, 0);
		String[] elements = Data.getElementsFromValue(toParse);
		for(int i = 0; i < color.getValues().length; i++){
			color.setValue(i, Float.parseFloat(elements[i]));
		}
		return color;
	}
	
}