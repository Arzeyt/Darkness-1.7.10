package com.arzeyt.theDarkness;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.WorldSavedData;

public class TDSaveData extends WorldSavedData{

	private NBTTagCompound data = new NBTTagCompound();
	
	public TDSaveData(String tagName) {
		super(tagName);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		data = compound.getCompoundTag("TheDarkness");
	}

	@Override
	public void writeToNBT(NBTTagCompound compound) {
		data.setTag("TheDarkness", compound);
	}
	
	public NBTTagCompound getData(){
		return data;
	}

}
