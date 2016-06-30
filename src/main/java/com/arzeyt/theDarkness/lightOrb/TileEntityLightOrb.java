package com.arzeyt.theDarkness.lightOrb;

import java.util.Random;
import java.util.UUID;

import com.arzeyt.theDarkness.ConfigValues;
import com.arzeyt.theDarkness.TheDarkness;
import com.arzeyt.theDarkness.tower.TileEntityTower;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

public class TileEntityLightOrb extends TileEntity {

	public static UUID id = UUID.randomUUID();
	private static String NBT_LIFETIME = "lifetime";
	public int lifetime;
	private static float NORMAL_ROTATION_SPEED = 0.001F;
	
	public double startY;
	public double targetY;
	public float rotationSpeed=0;
	
	public int radius=ConfigValues.lightOrbLightRange;
	public boolean registered=false;
	

	
	public void updateEntity(){
	
		//register
		if(registered==false){
			TheDarkness.towerManager.addOrb(this);
			registered=true;
		}
		//fireworks particles
		if(worldObj.isRemote==true){
			Random rand = new Random();
			this.getWorldObj().spawnParticle("fireworksSpark", xCoord+rand.nextDouble(), yCoord+rand.nextDouble(), zCoord+rand.nextDouble(), rand.nextDouble()-0.5, rand.nextDouble()-0.5, rand.nextDouble()-0.5);
		}
		
		//sphere
		if(worldObj.isRemote==true && isOnTower()==false){
			TheDarkness.towerManager.createParticleSphere(worldObj, xCoord, yCoord, zCoord, 8, 10, "fireworksSpark");
		}
		//determine rotation
		if(worldObj.isRemote==true){
			if(isOnTower()){
				this.rotationSpeed=NORMAL_ROTATION_SPEED;
			}
		}
		
		lifetime++;
	}
		
	public void readFromNBT(NBTTagCompound nbt){
	    	super.readFromNBT(nbt);
	    	lifetime = nbt.getInteger(NBT_LIFETIME);
	    	markDirty();
	    	updateContainingBlockInfo();
	    	
	 }
	    
    public void writeToNBT(NBTTagCompound nbt){
    	super.writeToNBT(nbt);
    	nbt.setInteger(NBT_LIFETIME, lifetime);
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
    
    public boolean isOnTower(){
    	if(worldObj.getTileEntity(xCoord, yCoord-1, zCoord)!=null && worldObj.getTileEntity(xCoord, yCoord-1, zCoord) instanceof TileEntityTower){
    		return true;
    	}else{
    		return false;
    	}
    }
    
    @Override
    public boolean equals(Object o){
    	if(o instanceof TileEntityLightOrb==false)return false;
    	TileEntityLightOrb orb = (TileEntityLightOrb) o;
    	if(this.xCoord==orb.xCoord && this.yCoord==orb.yCoord && this.zCoord==orb.zCoord){
    		return true;
    	}
    	return false;
    }
    
    @Override
	public int hashCode(){
		int hashCode=1;
		hashCode = 31*hashCode+xCoord;
		hashCode = 31*hashCode+yCoord;
		hashCode = 31*hashCode+zCoord;
		return hashCode;
	}
}


