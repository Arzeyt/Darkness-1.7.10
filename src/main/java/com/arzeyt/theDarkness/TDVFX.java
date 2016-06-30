package com.arzeyt.theDarkness;

import java.util.Random;

import net.minecraft.world.World;

public class TDVFX {

	
	public void createDarknessSmoke(World world, TDLocation loc, int density, int spread){
		for(int i=-spread; i<spread; i++){
			for(int j=-spread; j<spread; j++){
				for(int k=-spread; k<spread; k++){
					Random randDouble = new Random();
					Random randInt = new Random(100);
					if(randInt.nextInt()<density){
						world.spawnParticle("largesmoke", loc.x+i+randDouble.nextDouble(), loc.y+j+randDouble.nextDouble(), loc.z+k+randDouble.nextDouble(), 0.0, 0.0, 0.0);
					}
				}
			}
		}
	}
}
