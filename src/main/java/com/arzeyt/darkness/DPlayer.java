package com.arzeyt.darkness;

import com.mojang.authlib.GameProfile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public class DPlayer{


	public static void nbtSetGhost(EntityPlayer p, boolean isGhost){
		if(p.getEntityData().hasKey("darkness")==false){
			p.getEntityData().setTag("darkness", new NBTTagCompound());
		}
		NBTTagCompound nbt = p.getEntityData().getCompoundTag("darkness");
		nbt.setBoolean("ghost", isGhost);
	}

	public static boolean isGhost(EntityPlayer p){
		if(p.getEntityData().hasKey("darkness")) {
			NBTTagCompound nbt = p.getEntityData().getCompoundTag("darkness");

			return nbt.getBoolean(Reference.P_GHOST);
		}else{
			return false;
		}
	}
}
