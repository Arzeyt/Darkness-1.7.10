package com.arzeyt.theDarkness;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import com.arzeyt.theDarkness.proxies.PacketTheDarkness;
import com.arzeyt.theDarkness.tower.TileEntityTower;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.PlayerTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.WorldTickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * 
 * @author Arzeyt
 * @Notes: 
 * Reset tdp renders when changing effects. 
 */
public class PlayerTickHandler {

	//if there's only one instance on the server, these variables (inDarkness) will prove to be troublesome. Probably need to create a set to hold 
	//their values
	
	private int locationCheckTick = ConfigValues.locationTickRate;
	private int counter = 0;
	private boolean inDarkness=false;
	public HashMap<EntityPlayer, TDLocation> lightingBlocks = new HashMap<EntityPlayer, TDLocation>();


	//executes EVERY tick
	@SubscribeEvent
	public void onPlayerTick(PlayerTickEvent e) throws IOException{
	
		boolean isRemote = e.player.worldObj.isRemote;
		TDLocation playerLoc = new TDLocation((int)e.player.posX, (int)e.player.posY, (int)e.player.posZ);
		
		if(isRemote==false){
			executeServerPlayerTick(e, playerLoc);
		}else if(isRemote==true){
			executeClientPlayerTick(e, playerLoc);
		}
	}
	
	

	private void executeServerPlayerTick(PlayerTickEvent e, TDLocation playerLoc) throws IOException {
		
		//optimization needed. Is it necessary to calculate nearest tower EVERY tick for each player?
		//TileEntityTower tower = TheDarkness.towerManager.getNearestTower((int)e.player.posX, (int)e.player.posZ);

		//what's this for? 
		TheDarkness.towerManager.getTDPlayer(e.player).counter++;
		
		//update player in towerManager and set inDarkness. Should do this FIRST!
		if(counter%(locationCheckTick)==0){
			if(TheDarkness.towerManager.towers.size()<1){
				System.out.println("No Towers");
			}else{
				TheDarkness.towerManager.updatePlayer(e.player);
				inDarkness = TheDarkness.towerManager.getTDPlayer(e.player).inDarkness;
				//System.out.println("inDarkness = "+ inDarkness+" players in map"+TheDarkness.towerManager.players.size());
				
				
			}
		}
		
		//lighting block placement and removal
		if(counter%ConfigValues.lightOrbLightRenderRate==0){
			//if player is holding light orb
			if(TheDarkness.towerManager.getTDPlayer(e.player).isHoldingLightOrb){
				if(lightingBlocks.containsKey(e.player)==false){
					lightingBlocks.put(e.player, new TDLocation(0, 0, 0));
				}else if(playerLoc.equals(lightingBlocks.get(e.player))==false && e.player.worldObj.getBlock(playerLoc.x, playerLoc.y, playerLoc.z).equals(Blocks.air)){
					TDLocation oldLoc = lightingBlocks.get(e.player);
					e.player.worldObj.setBlockToAir(oldLoc.x, oldLoc.y, oldLoc.z);
					e.player.worldObj.setBlock(playerLoc.x, playerLoc.y, playerLoc.z, TheDarkness.lightingBlock);
					lightingBlocks.remove(e.player);
					lightingBlocks.put(e.player, playerLoc);
				}
			//if the player isn't holding a light orb but the lighting block is in the world
			}else if(lightingBlocks.containsKey(e.player)){
				System.out.println("removing player from lightingBlock set");
				TDLocation lightingLoc = lightingBlocks.get(e.player);
				e.player.worldObj.setBlockToAir(lightingLoc.x, lightingLoc.y, lightingLoc.z);
				lightingBlocks.remove(e.player);
			}
		}
		
		
		//send darkness visualization packets to players
		if(counter%(ConfigValues.darknessWallLocationCheckTime)==0){
			TDPlayer tdp = TheDarkness.towerManager.getTDPlayer(e.player);
			TileEntityTower tower = TheDarkness.towerManager.getNearestTower((int)e.player.posX, (int)e.player.posZ);
			
			//light field effects
			if(tdp.inLightOrbField && !tdp.isHoldingLightOrb && tdp.byDarkTower && !tdp.doLightFieldRender){
				System.out.println("in light orb field (not personal)");
				TheDarkness.channel.sendTo(PacketTheDarkness.lightFieldEffectsPacket(), (EntityPlayerMP) e.player);
				resetRenders(tdp);
				tdp.doLightFieldRender=true;
			
			//personal light field render
			}else if(tdp.isHoldingLightOrb && tdp.byDarkTower && !tdp.doPersonalLightOrbFieldRender){
				System.out.println("in personal light orb field and in darkness");
				TheDarkness.channel.sendTo(PacketTheDarkness.personalLightFieldEffectsPacket(), (EntityPlayerMP)e.player);
				resetRenders(tdp);
				tdp.doPersonalLightOrbFieldRender=true;

			//light tower effect
			//if the player isn't by a dark tower, and he either isn't rendering the wall, or isn't rendering at the same location
			}else if(tdp.byDarkTower==false && (!tdp.doDarknessWallRender() || !isSameLocation(tdp.darknessWallRenderLocation(), tower.getTDLocation()))){
				System.out.println("show light tower effects");
				try {
					TheDarkness.channel.sendTo(PacketTheDarkness.createWallVisPacket(e.player, tower), (EntityPlayerMP) e.player);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				resetRenders(tdp);
				tdp.doDarknessWallRenderOnLocation(new TDLocation(tower.xCoord, tower.yCoord, tower.zCoord));
				
			
			//darkness effect
			}else if( tdp.byDarkTower && !tdp.isHoldingLightOrb && tdp.inDarkness && (tdp.doDarknessWallRender() || tdp.doPersonalLightOrbFieldRender || tdp.doLightFieldRender)){
					System.out.println("Stop showing light tower effects and render darkness");
					TheDarkness.channel.sendTo(PacketTheDarkness.createRenderInDarknessEffectsPacket(e.player), (EntityPlayerMP) e.player);
					resetRenders(tdp);
					tdp.doDarknessSmokeRender=true;
			
			//darkness on logon effect
			}else if(inDarkness==true && tdp.justLoggedIn){
				TheDarkness.channel.sendTo(PacketTheDarkness.createRenderInDarknessEffectsPacket(e.player), (EntityPlayerMP) e.player);
				tdp.justLoggedIn=false;
				resetRenders(tdp);
				tdp.doDarknessSmokeRender=true;
			}
		}
		//zombie spawn 
		if(counter%300==0){
			TileEntityTower tower1 = TheDarkness.towerManager.getNearestTower((int) e.player.posX, (int)e.player.posZ);
			if(tower1.towerType==TowerType.DARK){
				spawnEntity(tower1.xCoord+1, tower1.yCoord+1, tower1.zCoord, "Zombie", e.player.worldObj);
				//add particle effects
			}
			
		}
		//extended peaceful (WIP)
		if(counter%ConfigValues.locationTickRate==0){
			int range = 5;
			EntityPlayer p = e.player;
			List<EntityLiving> mobs =  p.worldObj.getEntitiesWithinAABB(EntityLiving.class, AxisAlignedBB.getBoundingBox(p.posX-range, p.posY-range, p.posZ-range, p.posX + range, p.posY + range, p.posZ + range));
			for(EntityLiving mob : mobs){
				if(ExtendedPeaceful.get(mob)!=null){
					ExtendedPeaceful exprop = ExtendedPeaceful.get(mob);
					if(exprop.hasDarkness){
						p.worldObj.spawnParticle("largesmoke", mob.posX, mob.posY, mob.posZ, 0.0D, 0.0D, 0.0D);
					}
				}
			}
		}
			
		counter++;
	}

	//for rending, its better to send packets. Everything is managed by the server here. EVERYTHING. 
	private void executeClientPlayerTick(PlayerTickEvent e, TDLocation playerLoc) {
		//smokey peaceful
	
		/**
		if(counter%ConfigValues.locationTickRate==0){
			if(e.player.getHeldItem()!=null){
				if(e.player.getHeldItem().getItem().equals(Item.getItemFromBlock(TheDarkness.lightOrbBlock))){
					TheDarkness.renderManager.shouldRenderPersonalOrbSphere=true;
				}else{
					TheDarkness.renderManager.shouldRenderPersonalOrbSphere=false;
				}
			}
		}
		*/
		counter++;
	}
	
	
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
	
	public void resetRenders(TDPlayer p){
		p.doDarknessSmokeRender=false;
		p.doLightFieldRender=false;
		p.doPersonalLightOrbFieldRender=false;
		p.stopDarknessWallRender();
	}
	
	public void spawnEntity(int x, int y, int z, String name, World world){
		
			 String fullEntityName = name;
			    if (EntityList.stringToClassMapping.containsKey(fullEntityName))
			    {
		            Entity conjuredEntity = EntityList.createEntityByName(fullEntityName, world);
		    		conjuredEntity.setPosition(x, y, z); 
		    		world.spawnEntityInWorld(conjuredEntity);
			    }
			    else
			    {
			    	System.out.println("Entity not found");
			    }  
	}
	
	
	
}
