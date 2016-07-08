package com.arzeyt.theDarkness;

import java.io.IOException;

import com.arzeyt.theDarkness.proxies.PacketTheDarkness;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;

public class ExtendedPeaceful implements IExtendedEntityProperties{
	public final static String EXT_PROP_NAME = "TDExtendedPeaceful";
	private String NBT_HASDARKNESS = "hasDarkness";
	
	private final EntityLiving entity;
	
	public boolean hasDarkness;
	
	public ExtendedPeaceful(EntityLiving entity)
	{
	this.entity = entity;
	this.hasDarkness=false;
	}
	
	/**
	* Used to register these extended properties for the player during EntityConstructing event
	* This method is for convenience only; it will make your code look nicer
	*/
	public static final void register(EntityLiving entity)
	{
		entity.registerExtendedProperties(ExtendedPeaceful.EXT_PROP_NAME, new ExtendedPeaceful(entity));
	}
	
	/**
	* Returns ExtendedPlayer properties for player
	* This method is for convenience only; it will make your code look nicer
	*/
	public static final ExtendedPeaceful get(EntityLiving entity){
		return (ExtendedPeaceful) entity.getExtendedProperties(EXT_PROP_NAME);
	}
	
	// Save any custom data that needs saving here
	@Override
	public void saveNBTData(NBTTagCompound compound){

		NBTTagCompound properties = new NBTTagCompound();
		
		properties.setBoolean(NBT_HASDARKNESS, this.hasDarkness);
		compound.setTag(EXT_PROP_NAME, properties);
	}
	
	// Load whatever data you saved
	@Override
	public void loadNBTData(NBTTagCompound compound){
		NBTTagCompound properties = (NBTTagCompound) compound.getTag(EXT_PROP_NAME);
		this.hasDarkness = properties.getBoolean(NBT_HASDARKNESS);
		System.out.println("[TUT PROPS] hasDarkness from NBT: " + this.hasDarkness);
	}
	
	/*
	I personally have yet to find a use for this method. If you know of any,
	please let me know and I'll add it in!
	*/
	@Override
	public void init(Entity entity, World world)
	{
	}

	public void sync() {
		/**
		try {
			TheDarkness.channel.sendToAll(PacketTheDarkness.extendedPeacefulPacket(this, entity));
		} catch (IOException e) {
			e.printStackTrace();
		}
		*/
	}


}
