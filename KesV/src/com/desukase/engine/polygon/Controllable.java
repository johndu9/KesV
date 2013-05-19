package com.desukase.engine.polygon;

import com.desukase.engine.Controls;

public interface Controllable{
	
	public void handleControls(int delta);
	public Controls getControls();
	public void setControls(Controls controls);
	
}