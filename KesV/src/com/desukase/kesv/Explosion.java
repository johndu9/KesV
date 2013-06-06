package com.desukase.kesv;

import java.util.Random;

import com.desukase.engine.Color;
import com.desukase.engine.Point;
import com.desukase.engine.polygon.FirstPolygon;
import com.desukase.engine.polygon.MovingPolygon;

public class Explosion{
	
	private MovingPolygon[] explosion;
	private static Random random = Game.random;
	private Point origin;
	private boolean frozen = false;
	
	public Explosion(Point point, int polygonCount, int minSize, int maxSize, Color minimum, Color maximum){
		explosion = new MovingPolygon[polygonCount];
		for(int i = 0; i < polygonCount; i++){
			int explosionSize = random.nextInt(maxSize - minSize) + minSize;
			Color color = Color.randomColor(random, minimum, maximum);
			color.setAlpha(color.getAlpha() + 0.25f);
			explosion[i] = 
				new MovingPolygon(
					FirstPolygon.sizeToPoints(explosionSize, explosionSize),
					random.nextDouble() * 2 * Math.PI,
					point,
					color,
					random.nextInt(32) * 10 + 160,
					0);
		}
		origin = point;
	}
	
	public void update(int delta){
		for(MovingPolygon polygon : explosion){
			polygon.update(delta);
			if(!frozen){
				polygon.getColor().setAlpha(polygon.getColor().getAlpha() - 0.001f * (float) delta);
			}
		}
	}
	
	public boolean hits(FirstPolygon other){
		for(MovingPolygon polygon : explosion){
			if(polygon.hits(other)){
				return true;
			}
		}
		return false;
	}
	
	public boolean isInvisible(){
		for(MovingPolygon polygon : explosion){
			if(polygon.getColor().getAlpha() > 0.0f){
				return false;
			}
		}
		return true;
	}
	
	public Point getOrigin(){
		return origin;
	}
	
	public void setFrozen(boolean frozen){
		this.frozen = frozen;
		for(MovingPolygon polygon : explosion){
			polygon.setFrozen(frozen);
		}
	}
	
}