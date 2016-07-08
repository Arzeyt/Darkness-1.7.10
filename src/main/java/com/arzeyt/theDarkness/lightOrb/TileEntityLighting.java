package com.arzeyt.theDarkness.lightOrb;

import com.arzeyt.theDarkness.ConfigValues;
import com.arzeyt.theDarkness.TheDarkness;
import com.typesafe.config.Config;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

public class TileEntityLighting extends TileEntity{

	int range = ConfigValues.lightOrbLightRange;
	int counter=0;
	
	public void updateEntity() {
		counter++;
	}
	
	public void readFromNBT(NBTTagCompound nbt){
    	super.readFromNBT(nbt);
    	markDirty();
    	updateContainingBlockInfo();
	}
    
	public void writeToNBT(NBTTagCompound nbt){
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
}
