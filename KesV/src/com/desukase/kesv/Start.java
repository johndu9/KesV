package com.desukase.kesv;

import com.desukase.engine.Data;
import com.desukase.engine.DataSet;
import com.desukase.engine.GameDisplay;

public class Start{
	
	static Game game;
	
	public static void main(String [] args){
		
//		Data settings = new Data(Data.DIR_PATH + "settings.txt");
//		Data controls = new Data(Data.DIR_PATH + "controls.txt");
//		Data skin = new Data(Data.DIR_PATH + "res" + Data.SEP + "skn" + Data.SEP + settings.getValue("skin") + ".txt");
//		Data weapons = new Data(Data.DIR_PATH + "res" + Data.SEP + "txt" + Data.SEP + "weapons.txt");
//		Data gameModes = new Data(Data.DIR_PATH + "res" + Data.SEP + "txt" + Data.SEP + "gameModes.txt");
//		Data tips = new Data(Data.DIR_PATH + "res" + Data.SEP + "txt" + Data.SEP + "tips.txt");
//		DataSet dataSet = new DataSet(new Data[]{settings, controls, skin, weapons, gameModes, tips});
		DataSet dataSet = new DataSet(new Data[]{});

//		GameDisplay.initialize(Integer.parseInt(dataSet.getValue("frameCap")));
//		Text.initialize(Color.parseColor(dataSet.getValue("text")));
//		Timer.setTimerSpeed(Double.parseDouble(dataSet.getValue("gameSpeed")));
//		Sound.initialize(Integer.parseInt(dataSet.getValue("masterVolume")));
//		Button.initialize();
		GameDisplay.initialize(80);

		game = new Game(dataSet);
		
		while(true){

			game.update();
			game.updatePolygons();
			
		}
		
	}
	
}