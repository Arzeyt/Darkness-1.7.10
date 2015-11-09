package com.arzeyt.darkness;

import com.arzeyt.darkness.lightOrb.LightOrbItem;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraftforge.event.entity.player.PlayerUseItemEvent;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static cpw.mods.fml.common.gameevent.TickEvent.*;

/**
 * somehow being registered server side...
 *
 */
public class ClientEffectTick {

	private final int DETONATION_TICK_RATE = Reference.DETONATION_EFFECT_TICK_RATE;
	private int counter=0;
	Random rand = new Random();
	
	@SubscribeEvent
	public void detonationEffect(ClientTickEvent e){
		if(e.side== Side.SERVER)return;
		counter++;
		
		//detonation effect
		if(Darkness.clientLists.getDetonations().isEmpty()==false
				&&counter%DETONATION_TICK_RATE==0){
			for(BlockPos pos : Darkness.clientLists.getDetonations()){
				Random rand = new Random();
				double vx = ThreadLocalRandom.current().nextDouble(-1, 1);
				double vy = ThreadLocalRandom.current().nextDouble(-2,2);
				double vz = ThreadLocalRandom.current().nextDouble(-1,1);
	
				Minecraft.getMinecraft().theWorld.spawnParticle("fireworksSpark", pos.getX(), pos.getY(), pos.getZ(), vx, vy, vz);
			}
			
		}
	}
	
	@SubscribeEvent
	public void sphereDetonationEffect(ClientTickEvent e){
		if(e.side==Side.SERVER)return;
		
		if(Darkness.clientLists.getDetonations().isEmpty()==false
				&&counter%(DETONATION_TICK_RATE*5)==0){
			for(BlockPos pos : Darkness.clientLists.getDetonations()){
				double i = pos.getX();
				double j = pos.getY();
				double k = pos.getZ();
				Random rand = new Random();
				int r = Reference.ORB_DETONATION_RAIDUS;
				int density = 20;
				
				for(double x = -r; x < r; x++){
					for(double y = -r; y < r; y++){ 
						for(double z = -r; z < r; z++){					
							double dist = MathHelper.sqrt_double((x*x + y*y + z*z)); //Calculates the distance
							if((dist >= r-1 && dist <= r+1) && rand.nextInt(100)<density){
								Minecraft.getMinecraft().theWorld.spawnParticle("fireworksSpark", i+x+rand.nextDouble(), j+y+rand.nextDouble(), k+z+rand.nextDouble(), 0.0, 0.0, 0.0);
							}
						}
					}
				}
			}
		}
	}

	@SubscribeEvent
	public void HungerReplenish(ClientTickEvent e){
		if(Darkness.clientLists.isGhost()
				&& Darkness.clientLists.isPlayerInTowerRadius(Minecraft.getMinecraft().thePlayer)){
			Minecraft.getMinecraft().thePlayer.getFoodStats().setFoodLevel(Minecraft.getMinecraft().thePlayer.getFoodStats().getFoodLevel()+1);
		}
	}


	//disabled
	public void playerFlight(ClientTickEvent e){
		if(Minecraft.getMinecraft().thePlayer==null){return;}
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;

		if(Darkness.clientLists.isPlayerInTowerRadius(player)){
			if(Darkness.clientLists.isGhost()){
				player.capabilities.allowFlying=true;
			}
		}else{
			player.capabilities.allowFlying=false;

		}
	}



	@SubscribeEvent
	public void atmosphereTick(ClientTickEvent e){
		if(counter%Reference.ATMOSPHERE_TICK==0) {
			if (Minecraft.getMinecraft().theWorld == null) {
				return;
			}
			if(Minecraft.getMinecraft().theWorld.provider.dimensionId!=0){
				return;
			}
			EntityPlayer player = Minecraft.getMinecraft().thePlayer;
			BlockPos pos = new BlockPos(player.posX, player.posY, player.posZ);
			int radius = 10;
			int height = 3;
			int spawnChance = 5;

			for (int i = -radius; i < radius; i++) {
				for (int j = -height; j < height; j++) {
					for (int k = -radius; k < radius; k++) {
						if (rand.nextInt(100) <= spawnChance) {
							double x=pos.getX()+i+rand.nextDouble();
							double y=pos.getY()+j+rand.nextDouble();
							double z=pos.getZ()+k+rand.nextDouble();
							if (Darkness.clientLists.isPosInTowerRadius(Minecraft.getMinecraft().theWorld, new BlockPos(x,y,z))) {
								//Minecraft.getMinecraft().theWorld.spawnParticle(EnumParticleTypes.FIREWORKS_SPARK, x,y,z, 0.0D, 0.1D, 0.0D);
							}else if(Minecraft.getMinecraft().thePlayer.getHeldItem()!=null
									&& Minecraft.getMinecraft().thePlayer.getHeldItem().getItem() instanceof LightOrbItem
									&& Darkness.clientLists.isPosInTowerRadiusX2minus1(Minecraft.getMinecraft().theWorld, new BlockPos(x,y,z))==false){
								Minecraft.getMinecraft().theWorld.spawnParticle("witchMagic", x,y,z, 0.0D, 0.0D, 0.0D);
							}else if(Darkness.clientLists.isPosInTowerRadiusPlus1(Minecraft.getMinecraft().theWorld, new BlockPos(x,y,z))){
								Minecraft.getMinecraft().theWorld.spawnParticle("fireworksSpark", x, y, z, 0.0D, 0.5D, 0.0D);
							}else {
								Minecraft.getMinecraft().theWorld.spawnParticle("smoke", x, y, z, 0.0D, 0.03D, 0.0D);
							}
						}
					}
				}
			}

		}
	}


}
