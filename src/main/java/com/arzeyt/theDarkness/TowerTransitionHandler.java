package com.arzeyt.theDarkness;

import java.io.IOException;

import com.arzeyt.theDarkness.proxies.PacketTheDarkness;
import com.arzeyt.theDarkness.tower.TileEntityTower;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.world.World;

public class TowerTransitionHandler {

	public void towerTransitionToEmpty(TileEntityTower tower){
		
			World world = tower.getWorldObj();
			int orbs = tower.orbs;
			int x=tower.xCoord;
			int y=tower.yCoord;
			int z=tower.zCoord;
			
			TileEntityTower newTower = new TileEntityTower();	
			newTower.orbs=orbs;
			newTower.towerType=TowerType.EMPTY;
			
			world.setBlock(x,y,z, TheDarkness.towerBlockEmpty);
			world.setTileEntity(x,y,z, newTower);
			
			
			/**
				TheDarkness.channel.sendToAll(PacketTheDarkness.createTowerPacket(newTower));
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			
			world.updateEntities();
			*/
	}

	
	public void towerTransitionToFull(TileEntityTower tower){
		World world = tower.getWorldObj();
		int orbs=tower.orbs;
		int x=tower.xCoord;
		int y=tower.yCoord;
		int z=tower.zCoord;
		TileEntityTower newTower = new TileEntityTower();
		newTower.orbs=orbs;
		newTower.towerType=TowerType.LIGHT;
		
		world.setBlock(x, y, z, TheDarkness.towerBlock);
		world.setTileEntity(x, y, z, newTower);
		
		/**
		try{
			TheDarkness.channel.sendToAll(PacketTheDarkness.createTowerPacket(newTower));
		}catch(Exception e){
			e.printStackTrace();
		}
		world.updateEntities();
		*/
	}
	
	public void towerTransitionToDark(TileEntityTower tower){
		
			World world = tower.getWorldObj();
			int orbs = tower.orbs;
			int x=tower.xCoord;
			int y=tower.yCoord;
			int z=tower.zCoord;
			
			TileEntityTower newTower = new TileEntityTower();	
			newTower.orbs=-1;
			newTower.towerType=TowerType.DARK;
			
			world.setBlock(x,y,z, TheDarkness.towerBlockDark);
			world.setTileEntity(x,y,z, newTower);
			
			/**
			try {
				TheDarkness.channel.sendToAll(PacketTheDarkness.createTowerPacket(newTower));
			} catch (IOException e) {
				e.printStackTrace();
			}
			world.updateEntities();
			*/
		
	}
}
