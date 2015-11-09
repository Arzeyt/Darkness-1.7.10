package com.arzeyt.darkness;

import com.mojang.authlib.GameProfile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public class DPlayer{

	EntityPlayer player;
	BlockPos spawnPoint;

	public DPlayer(EntityPlayer player, BlockPos spawnPoint){
		this.player=player;
		this.spawnPoint=spawnPoint;
	}

	public void setSpawn(BlockPos pos){
		this.spawnPoint=pos;
	}

	public BlockPos getSpawnPoint(){
		return spawnPoint;
	}

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
