package com.arzeyt.theDarkness;

import java.util.HashMap;
import java.util.HashSet;

import net.minecraft.client.Minecraft;

/**
 * 
 * Client Side Only class
 *
 */
public class TDRenderManager {

	public HashMap<TDLocation, Boolean> towerRadiusBorderRender = new HashMap<TDLocation, Boolean>();
	public boolean shouldRenderDarknessSmoke=false;
	public boolean shouldRenderPersonalOrbSphere=false;

	
	public void activateTowerRadiusBorderRender(TDLocation varLoc){
		towerRadiusBorderRender.put(varLoc, true);
		System.out.println("activating tower radius border render with "+towerRadiusBorderRender.size()+" other locations in the map");
	}
	
	public void deactivateTowerRadiusBorderRenderAt(TDLocation varLoc){
		towerRadiusBorderRender.put(varLoc, false);
		System.out.println("deactivating tower radius border render with "+towerRadiusBorderRender.size()+" other locations in the map");
	}
	
	public void deactivateTowerRadiusBorderRender(){
		HashMap<TDLocation, Boolean> cleaner = new HashMap<TDLocation, Boolean>();
		this.towerRadiusBorderRender=cleaner;
	}
	
	public boolean shouldRenderTowerRadiusBorder(){
		if(towerRadiusBorderRender.size()<1){
			return false;
		}
		return towerRadiusBorderRender.containsValue(true);
	}
		
	/**
	 * this can be null
	 */
	public HashSet<TDLocation> getTowerRadiusBorderRenderingLocations(){
		HashSet<TDLocation> renderingLocs = new HashSet<TDLocation>();
		for(TDLocation mapLocs : towerRadiusBorderRender.keySet()){
			if(towerRadiusBorderRender.get(mapLocs)==true){
				renderingLocs.add(mapLocs);
			}
		}
		return renderingLocs;
	}
	
	
}
