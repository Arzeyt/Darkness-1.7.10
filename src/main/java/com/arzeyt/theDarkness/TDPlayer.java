package com.arzeyt.theDarkness;

import java.util.HashMap;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;

public class TDPlayer {

	UUID id = UUID.randomUUID();
	EntityPlayer player;
	
	boolean inDarkness=false;
	int counter=0;
	/**
	 * includes empty towers
	 */
	boolean closeToTower=false;
	private boolean doDarknessWallRender=false;
	private TDLocation darknessWallRenderLoc=null;
	public boolean justLoggedIn=false;
	public boolean doDarknessSmokeRender=false;
	public boolean isHoldingLightOrb = false;
	public boolean inLightOrbField=false;
	public boolean doPersonalLightOrbFieldRender=false;
	public boolean byDarkTower=false;
	public boolean doLightFieldRender=false;
	
	HashMap<Boolean, TDLocation> darknessWallRenderOnLocation = new HashMap<Boolean, TDLocation>();
	
	

	public TDPlayer(EntityPlayer player){
		this.player=player;
	}
	
	public String toString(){
		return id.toString();
		
	}
	
	@Override
	public boolean equals(Object o){
		if(o instanceof TDPlayer==false){
			return false;
		}else{
			TDPlayer tdplayer = (TDPlayer) o;
			if(tdplayer.player.getDisplayName()==this.player.getDisplayName()){
				return true;
			}else{
				return false;
			}
		}
	}
	
	@Override
	public int hashCode(){
		int hashCode=1;
		hashCode=hashCode*31+player.getDisplayName().hashCode();
		return hashCode;
	}
	
	public void doDarknessWallRenderOnLocation(TDLocation loc){
		darknessWallRenderLoc=loc;
		doDarknessWallRender=true;
	}
	
	public void stopDarknessWallRender(){
		darknessWallRenderLoc=null;
		doDarknessWallRender=false;
	}
	
	public boolean doDarknessWallRender(){
		return doDarknessWallRender;
	}
	
	public TDLocation darknessWallRenderLocation(){
		return darknessWallRenderLoc;
		
	}
	
	public void setPlayerHoldingLightOrb(){
		inDarkness=false;
		isHoldingLightOrb=true;
		inLightOrbField=true;
	}

}
