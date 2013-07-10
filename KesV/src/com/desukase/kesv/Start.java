package com.desukase.kesv;

import com.desukase.engine.Data;
import com.desukase.engine.DataSet;
import com.desukase.engine.GameDisplay;
import com.desukase.engine.Sound;

public class Start{
	
	static Game game;
	
	public static void main(String[] args){
		
		Data settings = new Data(Data.DIR_PATH + "settings.txt");
		DataSet dataSet = new DataSet(new Data[]{settings});

		GameDisplay.initialize(Integer.parseInt(dataSet.getValue("frameCap")));
//		Text.initialize(Color.parseColor(dataSet.getValue("text")));
		Sound.setMasterVolume(Float.parseFloat(dataSet.getValue("masterVolume")));
		GameDisplay.windowedWidth = Integer.parseInt(dataSet.getValue("windowedWidth"));
		GameDisplay.windowedHeight = Integer.parseInt(dataSet.getValue("windowedHeight"));
		GameDisplay.setTitle("KesV");

		game = new Game(dataSet);
		
		while(true){

			game.update();
			game.updatePolygons();
			
		}
		
	}
	
}