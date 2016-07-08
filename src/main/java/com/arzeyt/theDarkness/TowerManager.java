package com.arzeyt.theDarkness;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

import com.arzeyt.theDarkness.lightOrb.TileEntityLightOrb;
import com.arzeyt.theDarkness.tower.TileEntityTower;
import com.ibm.icu.util.BytesTrie.Iterator;

import net.minecraft.block.Block;
import net.minecraft.block.BlockGlowstone;
import net.minecraft.block.BlockStone;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class TowerManager {

	private int towerRadius=ConfigValues.towerRadius;
	
	public HashSet<TileEntityTower> towers = new HashSet<TileEntityTower>();
	HashSet<TDPlayer> players = new HashSet<TDPlayer>();
	public HashSet<TileEntityLightOrb> lightOrbs = new HashSet<TileEntityLightOrb>();
	
	Random randD = new Random();
	Random randI = new Random(100);
	
	public void addOrb(TileEntityLightOrb orb){
		HashSet<TileEntityLightOrb> temp = new HashSet<TileEntityLightOrb>();
		temp=lightOrbs;
		for(TileEntityLightOrb o : temp){
			if(o.equals(orb)){
				return;
			}
		}
		temp.add(orb);
		lightOrbs=temp;
		System.out.println("orbs size: "+lightOrbs.size());
	}
	
	public void removeOrb(TileEntityLightOrb orb){
		HashSet<TileEntityLightOrb> temp = new HashSet<TileEntityLightOrb>();
		temp=lightOrbs;
		if(temp.contains(orb)){
			temp.remove(orb);
			System.out.println("removed orb");
		}
		lightOrbs=temp;
	}
	/**
	 * adds the player to the TDPlayer set, and runs a location check to determine if the player is in the darkness.
	 * @param player
	 */
	public void addPlayer(EntityPlayer player){
		HashSet<TDPlayer> temp = new HashSet<TDPlayer>();
		temp=players;
		
		//check player exists in map
		for(TDPlayer tdp : temp){
			if(tdp.player.getDisplayName()==player.getDisplayName()){
				return;
			}
		}
		TDPlayer newPlayer = new TDPlayer(player);
		newPlayer.inDarkness=inDarkness(player.worldObj, (int)newPlayer.player.posX, (int) newPlayer.player.posY, (int)newPlayer.player.posZ);
		newPlayer.justLoggedIn=true;
		temp.add(newPlayer);
		players=temp;
	}
	
	public void removePlayer(EntityPlayer player){
		HashSet<TDPlayer> temp = new HashSet<TDPlayer>();
		temp=players;
		
		//check player exists in map
		for(TDPlayer tdp : temp){
			if(tdp.player.getDisplayName()==player.getDisplayName()){
				temp.remove(tdp);
			}
		}
		players=temp;
	}
	
	public void updatePlayer(EntityPlayer player) {
		HashSet<TDPlayer> temp = new HashSet<TDPlayer>();
		temp=players;
		
		//check player exists in map and see if he's in darkness
		for(TDPlayer tdp : temp){
			if(tdp.player.getDisplayName()==player.getDisplayName()){
				if(player.getHeldItem()!=null && player.getHeldItem().getItem().equals(Item.getItemFromBlock(TheDarkness.lightOrbBlock))){
					tdp.isHoldingLightOrb=true;
					tdp.inLightOrbField=true;
				}else{
					tdp.isHoldingLightOrb=false;
					tdp.inLightOrbField=false;
				}
				tdp.byDarkTower=byDarkTower(player.worldObj, (int)player.posX, (int)player.posZ);
				tdp.inLightOrbField=inLightOrbField((int)player.posX, (int)player.posY, (int)player.posZ);
				tdp.inDarkness=tdp.byDarkTower&&tdp.inLightOrbField==false&&tdp.isHoldingLightOrb==false;
				tdp.closeToTower=getDistanceToNearestTower((int)player.posX, (int)player.posZ)<=ConfigValues.towerRadius ? true : false;
				
			}
		}
		
		players=temp;
		
	}
	
	public TDPlayer getTDPlayer(EntityPlayer player){
		HashSet<TDPlayer> temp = new HashSet<TDPlayer>();
		temp=players;
		
		//check player exists in map
		for(TDPlayer tdp : temp){
			if(tdp.player.getDisplayName()==player.getDisplayName()){
				return tdp;
			}
		}
		
		return null;
	}
	
	public boolean inDarkness(World world, int x, int y, int z){
		
		if(towers.size()<1){
			return false;
		}else if(byDarkTower(world, x, z) && !inLightOrbField(x, y, z)){
			return true;
		}
		
			
		return false;
	}
	
	public boolean byDarkTower(World world, int x, int z){
		if(towers.size()<1){
			return false;
		}else if(world.provider.isSurfaceWorld()==false){
			return false;
		}else if(getNearestTower(x, z).towerType.equals(TowerType.DARK)){
			return true;
		}
		return false;
	}
	
	/**
	 * returns true if player is in a light orb field, either from a light orb placed in the world, or from another player. 
	 * Does NOT check if player is holding a light orb directly. 
	 */
	public boolean inLightOrbField(int x, int y, int z){
		if(lightOrbs.size()<0)return false;
		for(TileEntityLightOrb orb : lightOrbs){
			if(getDistance3D(x, y, z, orb.xCoord, orb.yCoord, orb.zCoord)<=orb.radius){
				return true;
			}
		}
		for(TDPlayer p : players){
			if(p.isHoldingLightOrb){
				if(getDistance3D(x, y, z, p.player.posX, p.player.posY, p.player.posZ)<ConfigValues.lightOrbLightRange){
					return true;
				}
			}
		}
		return false;
	}

	public void addTower(TileEntityTower tower){
		HashSet<TileEntityTower> temp = new HashSet<TileEntityTower>();
		temp=towers;
		if(temp.contains(tower)==false){
			temp.add(tower);
			towers=temp;
		}
	}
	
	public void removeTower(TileEntityTower tower){
		HashSet<TileEntityTower> temp = new HashSet<TileEntityTower>();
		temp=towers;
		if(temp.contains(tower)){
			temp.remove(tower);
		}
		towers=temp;
	}
	
	public int getDistance(int x1, int z1, int x2, int z2){
		
		return (int) MathHelper.sqrt_double((z2-z1)*(z2-z1)+(x2-x1)*(x2-x1));
		
	}
	
	public double getDistanceDouble(int x1, int z1, int x2, int z2){
		
		return MathHelper.sqrt_double((z2-z1)*(z2-z1)+(x2-x1)*(x2-x1));
		
	}
	
	public int getDistance3D(int x1, int y1, int z1, int x2, int y2, int z2){
		int x = x2-x1;
		int y = y2-y1;
		int z = z2-z1;
		
		double dist = MathHelper.sqrt_double((x*x + y*y + z*z)); 
		return (int) dist;
	}


	public double getDistance3D(double x1, double y1, double z1, double x2, double y2, double z2){
		double x = x2-x1;
		double y = y2-y1;
		double z = z2-z1;
		
		double dist = MathHelper.sqrt_double((x*x + y*y + z*z)); 
		return dist;
	}
	
	public TileEntityTower getNearestTower(int x, int z){
		TileEntityTower tower=null;
		int distance = 123456789;
		
		for(TileEntityTower t : towers){
			int testDistance = getDistance(x, z, t.xCoord, t.zCoord);
			if(testDistance<distance){
				distance=testDistance;
				tower=t;
			}
		}
		return tower;
	}
	
	public int getDistanceToNearestTower(int x, int z){
		TileEntityTower tower=null;
		int distance =  123456789;
		
		for(TileEntityTower t : towers){
			int testDistance = getDistance(x, z, t.xCoord, t.zCoord);
			if(testDistance<distance){
				distance=testDistance;
				tower=t;
			}
		}
		return distance;
	}
	
	//need a way to do this clientside only
	public void showDarknessWall(EntityPlayer player, TileEntityTower tower){
		
		TDPlayer tdp = getTDPlayer(player);
		TDLocation locOfWall;
		int towerRadius = ConfigValues.towerRadius;
		World world = player.worldObj;
		
			createBlobOfBlocks(world, tower.xCoord+towerRadius, tower.yCoord, tower.zCoord+towerRadius);
			createBlobOfBlocks(world, tower.xCoord+towerRadius, tower.yCoord, tower.zCoord-towerRadius);
			createBlobOfBlocks(world, tower.xCoord-towerRadius, tower.yCoord, tower.zCoord+towerRadius);
			createBlobOfBlocks(world, tower.xCoord-towerRadius, tower.yCoord, tower.zCoord-towerRadius);
			
			createBlobOfBlocks(world, tower.xCoord+towerRadius, tower.yCoord+1, tower.zCoord+towerRadius);
			createBlobOfBlocks(world, tower.xCoord+towerRadius, tower.yCoord+1, tower.zCoord-towerRadius);
			createBlobOfBlocks(world, tower.xCoord-towerRadius, tower.yCoord+1, tower.zCoord+towerRadius);
			createBlobOfBlocks(world, tower.xCoord-towerRadius, tower.yCoord+1, tower.zCoord-towerRadius);
	}
	
	public void createBlobOfBlocks(World world, int x, int y, int z){

		world.setBlock(x, y, z, Blocks.glowstone);
		world.setBlock(x+1, y, z, Blocks.glowstone);
		world.setBlock(x-1, y, z, Blocks.glowstone);
		world.setBlock(x, y, z+1, Blocks.glowstone);
		world.setBlock(x, y, z-1, Blocks.glowstone);
		
	}
	
	public HashSet<TDLocation> getLine(int x1, int z1, int x2, int z2, int y){
		HashSet<TDLocation> locs = new HashSet<TDLocation>();
		if(x1==x2){
			if(z1==z2){
				return null;
			}else if(z1>z2){
				for(int z=z1;z>z2;z--){
					locs.add(new TDLocation(x1, y, z));
				}
			}else if(z2>z1){
				for(int z=z2;z>z1;z--){
					locs.add(new TDLocation(x1, y, z));
				}
			}
		}else if(z1==z2){
			if(x1==x2){
				return null;
			}else if(x1>x2){
				for(int x=x1; x>x2; x--){
					locs.add(new TDLocation(x, y, z1));
				}
			}else if(x2>x1){
				for(int x=x2; x>x1; x--){
					locs.add(new TDLocation(x,  y, z1));
				}
			}
		}
		return locs;
	}

	public ArrayList<TDLocation> getTowerRadiusCornerBlocks(TDLocation loc){
		ArrayList<TDLocation> towerRadiusCornerBlocks = new ArrayList<TDLocation>();
		towerRadiusCornerBlocks.add(new TDLocation(loc.x+ConfigValues.towerRadius, loc.y, loc.z+ConfigValues.towerRadius));
		towerRadiusCornerBlocks.add(new TDLocation(loc.x+ConfigValues.towerRadius, loc.y, loc.z-ConfigValues.towerRadius));
		towerRadiusCornerBlocks.add(new TDLocation(loc.x-ConfigValues.towerRadius, loc.y, loc.z-ConfigValues.towerRadius));
		towerRadiusCornerBlocks.add(new TDLocation(loc.x-ConfigValues.towerRadius, loc.y, loc.z+ConfigValues.towerRadius));
		return towerRadiusCornerBlocks;

	}
	
	public HashSet<TDLocation> getTowerRadiusBorder(TDLocation loc){
		HashSet<TDLocation> borderLocs = new HashSet<TDLocation>();
		ArrayList<TDLocation> corners = getTowerRadiusCornerBlocks(loc);
		borderLocs.addAll(getLine(corners.get(0).x, corners.get(0).z, corners.get(1).x, corners.get(1).z, corners.get(0).y));
		borderLocs.addAll(getLine(corners.get(1).x, corners.get(1).z, corners.get(2).x, corners.get(2).z, corners.get(0).y));
		borderLocs.addAll(getLine(corners.get(2).x, corners.get(2).z, corners.get(3).x, corners.get(3).z, corners.get(0).y));
		borderLocs.addAll(getLine(corners.get(3).x, corners.get(3).z, corners.get(0).x, corners.get(0).z, corners.get(0).y));

		return borderLocs;
	}
	
	public void createTowerRadiusBorderWithBlocks(World world, TDLocation towerLoc, Block block){
		for(TDLocation loc : getTowerRadiusBorder(towerLoc)){
			world.setBlock(loc.x, loc.y, loc.z, block);
		}
	}
	
	public void createSmokeyTowerRadiusBorder(World world, TDLocation towerLoc, int density){
		Random rand = new Random();
		for(TDLocation loc : getTowerRadiusBorder(towerLoc)){
			if(rand.nextInt()<density){
				try{
					world.spawnParticle("largesmoke", loc.x+rand.nextDouble(), loc.y+rand.nextDouble(), loc.z+rand.nextDouble(), 0.0D, 0.0D, 0.0D);
				}catch(Exception e){
					System.out.println("There was a problem rendering the darkness");
				}
			}
		}
	}
	
	public void createSparklingTowerRadiusBorder(World world, TDLocation towerLoc, int variability, int trackingHeight, int wallHeight){
		Random randDouble = new Random();
		Random randInt = new Random(100);
		
		for(TDLocation loc : getTowerRadiusBorder(towerLoc)){
			for(int h=-wallHeight; h<wallHeight; h++){
				if(randInt.nextInt()>variability){
					try{
						world.spawnParticle("fireworksSpark", loc.x+randDouble.nextDouble(), trackingHeight+h+randDouble.nextDouble(), loc.z+randDouble.nextDouble(), 0.0D, 0.0D, 0.0D);
					}catch(Exception e){
						System.out.println("There was a problem rendering the darkness");
					}
				}
			}
		}
	}
	/**
	 * @param world
	 * @param towerLoc
	 * @param densityPercent
	 * @param trackingHeight
	 * @param wallHeight
	 * @param playerLoc
	 * @param range
	 */
	public void createSparklingTowerRadiusBorderTracking(World world, TDLocation towerLoc, int densityPercent, int trackingHeight, int wallHeight, TDLocation playerLoc, int range){
		
		
		for(TDLocation loc : getTowerRadiusBorder(towerLoc)){
			if(getDistance(playerLoc.x, playerLoc.z, loc.x, loc.z)<=range){
				for(int h=-wallHeight; h<wallHeight; h++){
					if(randI.nextInt()<densityPercent){
						try{
							world.spawnParticle("fireworksSpark", loc.x+randD.nextDouble(), trackingHeight+h+randD.nextDouble(), loc.z+randD.nextDouble(), 0.0D, 0.0D, 0.0D);
						}catch(Exception e){
							System.out.println("There was a problem rendering the darkness");
						}
					}
				}
			}
			
		}
	}
	
	public TDLocation getBorderBlockNearestLocation(TDLocation trackingLoc, TDLocation towerLoc){
		double distance = 123456789;
		TDLocation result = null;
		for(TDLocation location : getTowerRadiusBorder(towerLoc)){
			double testDistance = getDistanceDouble(trackingLoc.getX(), trackingLoc.getZ(), location.getX(), location.getZ());
			if(testDistance<distance){
				distance = testDistance;
				result = location;
			}
		}
		return result;
	}
	
	public void createTrackingBorderSmoke(World world, TDLocation trackingLoc, TDLocation towerLoc){
		Random rand = new Random();
		TDLocation loc = getBorderBlockNearestLocation(trackingLoc, towerLoc);
		world.spawnParticle("largesmoke", loc.x+rand.nextDouble(), trackingLoc.y+rand.nextDouble(), loc.z+rand.nextDouble(), 0.0D, 0.0D, 0.0D);
		world.spawnParticle("largesmoke", loc.x+rand.nextDouble(), trackingLoc.y+1+rand.nextDouble(), loc.z+rand.nextDouble(), 0.0D, 0.0D, 0.0D);
	}
	
	public void createTrackingBorderSparkles(World world, TDLocation trackingLoc, TDLocation towerLoc, int range, int density){
		Random rand = new Random(100);
		HashSet<TDLocation> locs = new HashSet<TDLocation>();
		HashSet<TDLocation> locs2 = new HashSet<TDLocation>();
		HashSet<TDLocation> locs3 = new HashSet<TDLocation>();
		
		for(int i=-range;i<range;i++){
			for(int k=-range;k<range;k++){
				int ix = trackingLoc.x+i;
				int kz = trackingLoc.z+k;
				TDLocation modLoc = new TDLocation(ix, trackingLoc.y, kz);
				
				//a point on the wall at a valid location, but not y adjusted
				TDLocation locXY = getBorderBlockNearestLocation(modLoc, towerLoc);
				locs.add(locXY);
			}
		}
		for(TDLocation locXY : locs){
			//y adjusting and adding to locs2
			for(int j=-range;j<range;j++){
				int jy = trackingLoc.y+j;
				TDLocation locY = new TDLocation(locXY.x, jy, locXY.z);
				
				locs2.add(locY);
			}
		}
		
		for(TDLocation loc : locs2){
			if(rand.nextInt()<density){
				world.spawnParticle("fireworksSpark", loc.x+rand.nextDouble(), loc.y+range+rand.nextDouble(), loc.z+rand.nextDouble(), 0.0D, 0.0D, 0.0D);
			}
		}
	}
	
	public void createDarknessSmoke(World world, TDLocation loc, int density, int spread){
		for(int i=-spread; i<spread; i++){
			for(int j=-spread; j<spread; j++){
				for(int k=-spread; k<spread; k++){
					if(randI.nextInt(100)<density){
						
						world.spawnParticle("depthsuspend", loc.x+i+randD.nextDouble(), loc.y+j+randD.nextDouble(), loc.z+k+randD.nextDouble(), 0.0, 0.0, 0.0);
					}
				}
			}
		}
	}
	
	/**
	 * not necessary since TDLocations can return their own equality
	 * @param loc1
	 * @param loc2
	 * @return
	 */
	public boolean isSameLocation(TDLocation loc1, TDLocation loc2){
		if(loc1==null || loc2==null){
			return false;
		}
		if(isSameLocation(loc1.x, loc1.y, loc1.z, loc2.x, loc2.y, loc2.z)){
			return true;
		}else{
			return false;
		}
	}
	
	private boolean isSameLocation(int x, int y, int z, int x2, int y2, int z2){
		if(x==x2 && y==y2 && z==z2){
			return true;
		}else{
			return false;
		}
	}
	
	public void createBlockSphere(World world, double posX, double posY, double posZ, Block block){
		int i = MathHelper.floor_double(posX);
		int j = MathHelper.floor_double(posY); 
		int k = MathHelper.floor_double(posZ);
		int r = 100;
		for(int x = -r; x < r; x++){
			for(int y = -r; y < r; y++){ 
				for(int z = -r; z < r; z++){					
					double dist = MathHelper.sqrt_double((x*x + y*y + z*z)); //Calculates the distance
					if(dist > r)
						continue;
					world.setBlock(i+x, j+y, k+z, block);
				}
			}
		}
	}
	
	public void createParticleSphere(World world, double posX, double posY, double posZ, int radius, int density, String particleName){
		/**
		int i = MathHelper.floor_double(posX);
		int j = MathHelper.floor_double(posY); 
		int k = MathHelper.floor_double(posZ);
		*/
		double i = posX;
		double j = posY;
		double k = posZ;
		
		int r = radius;
		for(double x = -r; x < r; x++){
			for(double y = -r; y < r; y++){ 
				for(double z = -r; z < r; z++){					
					double dist = MathHelper.sqrt_double((x*x + y*y + z*z)); //Calculates the distance
					if((dist >= r-1 && dist <= r+1) && randI.nextInt(100)<density){
						world.spawnParticle(particleName, i+x+randD.nextDouble(), j+y+randD.nextDouble(), k+z+randD.nextDouble(), 0.0, 0.0, 0.0);
					}
				}
			}
		}
	}
	
}
