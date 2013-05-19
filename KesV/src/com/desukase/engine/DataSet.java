package com.desukase.engine;

import java.io.IOException;

/**
 * Holds a lot of data, useful for loading in settings
 * @author John Du
 */
public class DataSet{
	
	/** Array of data */
	private Data[] dataSet;
	
	/**
	 * Constructor, makes the data set
	 * @param dataSet Array of data
	 */
	public DataSet(Data[] dataSet){
		setDataSet(dataSet);
	}
	
	/**
	 * @return Number of datum
	 */
	public int length(){
		return dataSet.length;
	}
	
	/**
	 * @return The entire set of data
	 */
	public Data[] getDataSet(){
		return dataSet;
	}
	
	/**
	 * Sets the data set
	 * @param dataSet Array of data
	 */
	public void setDataSet(Data[] dataSet){
		this.dataSet = dataSet;
	}
	
	/**
	 * Adds data to the data set
	 * @param data Data to be added
	 */
	public void addData(Data data){
		Data[] newDataSet = new Data[dataSet.length + 1];
		for(int i = 0; i < dataSet.length; i++){
			newDataSet[i] = dataSet[i];
		}
		newDataSet[dataSet.length] = data;
		dataSet = newDataSet;
	}
	
	/**
	 * Adds multiple data to the data set
	 * @param data Data to be added
	 */
	public void addData(Data[] data){
		for(int i = 0; i < data.length; i++){
			addData(data[i]);
		}
	}
	
	/**
	 * Removes data from the data set
	 * @param index Index of data to be removed
	 */
	public void removeData(int index){
		Data[] newDataSet = new Data[dataSet.length - 1];
		for(int i = 0; i < dataSet.length; i++){
			if(i > index){
				newDataSet[i] = dataSet[i - 1];
			}else if(i != index){
				newDataSet[i] = dataSet[i];
			}
		}
		dataSet = newDataSet;
	}
	
	/**
	 * @param index Index of data to get
	 * @return Data at given index
	 */
	public Data getData(int index){
		return dataSet[index];
	}
	
	/**
	 * Sets the data at a given index
	 * @param index Index of data to set
	 * @param data Data to set
	 */
	public void setData(int index, Data data){
		dataSet[index] = data;
	}
	
	/**
	 * @param name Name to search
	 * @return Number of occurrences of a name in all the data
	 */
	public int countName(String name){
		int count = 0;
		for(int i = 0; i < dataSet.length; i++){
			count += dataSet[i].countName(name);
		}
		return count;
	}
	
	/**
	 * @param name Name of value to find
	 * @return First value from a given name in all the data
	 */
	public String getValue(String name){
		int index = searchForValue(name);
		if(index > -1){
			return dataSet[index].getValue(name);
		}else{
			return "";
		}
	}
	
	/**
	 * Sets the first name's value to something new
	 * @param name Name of value to find
	 * @param value Value to set
	 */
	public void setValue(String name, String value){
		int index = searchForValue(name);
		if(index > -1){
			dataSet[index].setValue(name, value);
		}
	}
	
	/**
	 * Adds value to the first data in the set
	 * @param name Added name
	 * @param value Added value
	 */
	public void addValue(String name, String value){
		dataSet[0].addValue(name, value);
	}
	
	/**
	 * Removes the first occurrence of a name in the data set
	 * @param name Name and value to remove
	 */
	public void removeValue(String name){
		int index = searchForValue(name);
		if(index > -1){
			dataSet[index].removeValue(name);
		}
	}
	
	/**
	 * Writes all the data to their respective files
	 */
	public void writeDataToFile(){
		for(int i = 0; i < dataSet.length; i++){
			try{
				dataSet[i].writeDataToFile();
			}catch (IOException e){
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Looks for a value
	 * @param name Name of value
	 * @return Value of name
	 */
	private int searchForValue(String name){
		for(int i = 0; i < dataSet.length; i++){
			String value = dataSet[i].getValue(name);
			if(!value.equals("")){
				return i;
			}
		}
		return -1;
	}
	
}