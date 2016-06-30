package com.arzeyt.theDarkness.tower;

import java.io.IOException;
import java.util.Random;
import java.util.UUID;

import com.arzeyt.theDarkness.ConfigValues;
import com.arzeyt.theDarkness.TDLocation;
import com.arzeyt.theDarkness.TheDarkness;
import com.arzeyt.theDarkness.TowerTransitionHandler;
import com.arzeyt.theDarkness.TowerType;
import com.arzeyt.theDarkness.lightOrb.TileEntityLightOrb;
import com.arzeyt.theDarkness.proxies.PacketTheDarkness;
import com.typesafe.config.Config;

import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraft.block.Block;
import net.minecraft.block.BlockClay;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityDiggingFX;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class TileEntityTower extends TileEntity {
	//save these
	public int orbs = 1;
	public TowerType towerType = TowerType.LIGHT;

	//lose these
	private int counter=1;
	public boolean registered = false;
	
	//rendering
	public boolean renderTransitionToEmpty=false;
	public boolean renderTransitionToEmptyHandled=true;
	
	public boolean renderTransitionToFull=false;
	public boolean renderTransitionToFullHandled=false;
	
	public long animationOffset;
	
	public float arm1RotationZ=0;
	public float arm2RotationX=0;
	public float arm3RotationZ=0;
	public float arm4RotationX=0;
		
	public ModelTower towerModel;
	
	private static final String NBT_ORBS = "orbs";
	private static final String NBT_TOWERTYPE = "towerType";
	private static final String NBT_RENDER_TRANSITION_EMPTY_HANDLED = "renderTransitionEmptyHandled";
	private static final String NBT_RENDER_TRANSITION_FULL_HANDLED = "renderTransitionFullHandled";

	

	public TileEntityTower(){
		this.towerModel=new ModelTower();
	}
	//Avoid world references
    public void readFromNBT(NBTTagCompound nbt){
    	super.readFromNBT(nbt);
    	orbs = nbt.getInteger(NBT_ORBS);
    	towerType=getTowerTypeFromInt(nbt.getInteger(NBT_TOWERTYPE));
    	renderTransitionToEmptyHandled=nbt.getBoolean(NBT_RENDER_TRANSITION_EMPTY_HANDLED);
    	renderTransitionToFullHandled=nbt.getBoolean(NBT_RENDER_TRANSITION_FULL_HANDLED);
    	
    	markDirty();
    	updateContainingBlockInfo();
    	
    }
    
    public void writeToNBT(NBTTagCompound nbt){
    	super.writeToNBT(nbt);
    	nbt.setInteger(NBT_ORBS, orbs);
    	nbt.setInteger(NBT_TOWERTYPE, getTowerTypeFromEnum(towerType));
    	nbt.setBoolean(NBT_RENDER_TRANSITION_EMPTY_HANDLED, renderTransitionToEmptyHandled);
    	nbt.setBoolean(NBT_RENDER_TRANSITION_FULL_HANDLED, renderTransitionToFullHandled);
    	//nbt.setBoolean(NBT_RENDER_TRANSITION_HANDLED, renderTransitionToEmptyHandled);
    }
    
    public Integer getTowerTypeFromEnum(TowerType type){
    	if(type==TowerType.DARK){return -1;}
    	else if(type==TowerType.EMPTY){return 0;}
    	else if(type==TowerType.LIGHT){return 1;}
    	else{return 303;}
    }
    	
    public TowerType getTowerTypeFromInt(int towerType){
    	if(towerType==-1)return TowerType.DARK;
    	else if(towerType==0)return TowerType.EMPTY;
    	else if(towerType==1)return TowerType.LIGHT;
    	else{return TowerType.EMPTY;}
    }
    
    @Override
    public Packet getDescriptionPacket() {
	    NBTTagCompound tag = new NBTTagCompound();
	    this.writeToNBT(tag);
	    return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 1, tag);
    }
    	
    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) {
    	readFromNBT(packet.func_148857_g());
    }
    
    public boolean takeOrb(EntityPlayer player){
    		
    	if(subtractOrb()){
    		System.out.println("taking orb");
    		EntityItem dropItem = new EntityItem(player.worldObj, player.posX, player.posY, player.posZ, new ItemStack(TheDarkness.lightOrbBlock));
    		dropItem.delayBeforeCanPickup = 0;
    		player.worldObj.spawnEntityInWorld(dropItem);
    		return true;
    	}else{
    		return false;
    	}
    		
    }
    
    public boolean subtractOrb(){
    	if(this.orbs==1){
    		this.toEmptyBlock(worldObj);
    		this.orbs=0;
    		return true;
    	}else if(this.orbs<=0){
    		return false;
    	}else{
    		this.orbs=this.orbs-1;
    		return true;
    	}
    }
    
    public void addOrbs(int orbs){
    	this.orbs=this.orbs+orbs;
    	if(this.orbs>0){
    		this.toLightblock(worldObj);
    	}
    }
    
    
    public void toEmptyBlock(World world){
    	//new TowerTransitionHandler().towerTransitionToEmpty(this); 
    	this.towerType=TowerType.EMPTY;
    	this.orbs=0;
    	resetRenderValues();
    	this.renderTransitionToEmpty=true;
    	
    	sync();
    	markDirty();
    }
    
    public void toLightblock(World world){
    	//new TowerTransitionHandler().towerTransitionToFull(this);
    	this.towerType=TowerType.LIGHT;
    	resetRenderValues();
    	this.renderTransitionToFull=true;
    	
    	sync();
    	markDirty();
    }

    
    public void toDarkBlock(World world){
    	//new TowerTransitionHandler().towerTransitionToDark(this);
    	this.towerType=TowerType.DARK;
    	this.orbs=-1;
    	resetRenderValues();
    	this.renderTransitionToEmpty=true;
    	
    	sync();
    	markDirty();
    }
    
    public TileEntityTower cloneToLocation(World world, int x, int y, int z){
    	TileEntityTower newTower = new TileEntityTower();
    	world.setTileEntity(xCoord, yCoord, zCoord, newTower);
    	newTower.orbs=this.orbs;
    	return newTower;
    }
    
    public void updateEntity() {
    	
    	//register with towermanager
    	if(registered==false && counter==1 && this.getWorldObj().isRemote==false){
    		TheDarkness.towerManager.addTower(this);
    		System.out.println("total registered towers: "+TheDarkness.towerManager.towers.size());
    		registered=true;
    	}
    	
    	//generate light orb
    	if(towerType.equals(TowerType.LIGHTGENERATOR) && worldObj.isRemote==false && timeIsNoon() && isSameDay()==false){
    		this.addOrbs(1);
    	}
    	

    	//display light orb
    	if(this.orbs>0 && isDisplayingLightOrb()==false && this.worldObj.isRemote==false){
    		worldObj.setBlock(xCoord, yCoord+1, zCoord, TheDarkness.lightOrbBlock);
    		worldObj.setTileEntity(xCoord, yCoord+1, zCoord, new TileEntityLightOrb());
    	}else if(this.orbs<1 && isDisplayingLightOrb() && this.worldObj.isRemote==false){
    		worldObj.setBlock(xCoord, yCoord+1, zCoord, Blocks.air);
    	}
    	
    	//apply darkness over time
    	if(towerType.equals(TowerType.EMPTY) && worldObj.isRemote==false && timeIsMidnight()){
    		toDarkBlock(worldObj);
    		System.out.println("transitioning to dark tower---- counter: "+counter);
    	}
    	
    	//particles to see what sort of tower it is
    	if(this.getWorldObj().isRemote==true){
    		Random rand = new Random();

    		if(towerType==towerType.LIGHT){
	    		this.getWorldObj().spawnParticle("fireworksSpark", xCoord+rand.nextDouble(), yCoord+rand.nextDouble(), zCoord+rand.nextDouble(), rand.nextDouble()-0.5, rand.nextDouble()-0.5, rand.nextDouble()-0.5);
    		}else if(towerType==TowerType.EMPTY){
	    		this.getWorldObj().spawnParticle("snowshovel", xCoord+rand.nextDouble(), yCoord+rand.nextDouble(), zCoord+rand.nextDouble(), rand.nextDouble()-0.5, rand.nextDouble()-0.5, rand.nextDouble()-0.5);
    		}else if(towerType==TowerType.DARK){
	    		this.getWorldObj().spawnParticle("largesmoke", xCoord+rand.nextDouble(), yCoord+rand.nextDouble(), zCoord+rand.nextDouble(), rand.nextDouble()-0.5, rand.nextDouble()-0.5, rand.nextDouble()-0.5);
    		}else if(towerType==towerType.LIGHTGENERATOR){
	    		this.getWorldObj().spawnParticle("fireworksSpark", xCoord+rand.nextDouble(), yCoord+rand.nextDouble(), zCoord+rand.nextDouble(), rand.nextDouble()-0.5, rand.nextDouble(), rand.nextDouble()-0.5);
    		}
    	}
    	
    	//sync client with server
    	
    	counter++;
    }
    

	public TDLocation getTDLocation(){
    	return new TDLocation(xCoord, yCoord, zCoord);
    }
    
    public boolean isDisplayingLightOrb(){
    	if(worldObj.getBlock(xCoord, yCoord+1, zCoord).equals(TheDarkness.lightOrbBlock)){
    		return true;
    	}else{
    		return false;
    	}
    }
    
    public boolean timeIsNoon(){
    	if(worldObj.provider.getWorldTime()>5500 && worldObj.getWorldTime()<6500){
    		return true;
    	}else{
    		return false;
    	}
    }
    
    public boolean timeIsMidnight(){
    	if(worldObj.getWorldTime()>17500 && worldObj.getWorldTime()<18500){
    		return true;
    	}else{
    		return false;
    	}
    }
	 private boolean isSameDay() {
	    	
			return false;
	}
	 
	 public void resetRenderValues(){
		 this.renderTransitionToEmpty=false;
		 this.renderTransitionToEmptyHandled=false;
		 this.renderTransitionToFull=false;
		 this.renderTransitionToFullHandled=false;
		 this.animationOffset=System.currentTimeMillis();
	 }

    public void sync(){
    	try {
			TheDarkness.channel.sendToAll(PacketTheDarkness.towerSyncPacket(this));
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    public void setRenderTransitionToEmptyHandled(boolean b){
    	this.renderTransitionToEmptyHandled=b;
    }
    
    public void setRenderTransitionToFullHandled(boolean b){
    	this.renderTransitionToFullHandled=b;
    }
}
