package com.arzeyt.theDarkness;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.passive.EntityAmbientCreature;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityMooshroom;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;

public class TDEventHandler {

	int counter=0;
	
	@SubscribeEvent
	public void playerInteract(PlayerInteractEvent event){
		if(event.entity.worldObj.isRemote==false){
			if(TheDarkness.towerManager.getTDPlayer(event.entityPlayer).inDarkness==true && event.entityPlayer.capabilities.isCreativeMode==false){
				System.out.println("player is interacting in darkness");
				event.setCanceled(true);
			}
		}
		
	}
	
	
	@SubscribeEvent
	public void playerDrop(PlayerDropsEvent event){
		
	}
	
	@SubscribeEvent
	public void playerLogIn(PlayerLoggedInEvent event){
		TheDarkness.towerManager.addPlayer(event.player);
	}

	@SubscribeEvent
	public void playerLogOut(PlayerLoggedOutEvent event){
		TheDarkness.towerManager.removePlayer(event.player);
	}
	
	@SubscribeEvent
	public void onEntityJoinWorld(EntityJoinWorldEvent event){
		if(event.entity.worldObj.isRemote==true)return;
		if (isPeaceful(event.entity) && ExtendedPeaceful.get((EntityLiving) event.entity) == null){
			ExtendedPeaceful.register((EntityLiving) event.entity);
			ExtendedPeaceful ep = ExtendedPeaceful.get((EntityLiving) event.entity);
			ep.hasDarkness=TheDarkness.towerManager.inDarkness(event.world, (int)event.entity.posX, (int)event.entity.posY, (int)event.entity.posZ);
			ep.sync();
		}
		
	}

	@SubscribeEvent
	public void playerRender(RenderPlayerEvent.Specials.Pre e){
		if(e.entity.worldObj.isRemote==true){
			if(e.entityPlayer.getDisplayName()!=Minecraft.getMinecraft().thePlayer.getDisplayName()){
				System.out.println("rendered player is not user");
				if(e.entityPlayer.getHeldItem().getItem().equals(Item.getItemFromBlock(TheDarkness.lightOrbBlock))){
					TheDarkness.towerManager.createParticleSphere(e.entity.worldObj, e.entity.posX, e.entity.posY, e.entity.posZ, ConfigValues.lightOrbLightRange, 20, "fireworksSpark");
				}
			}
		}
	}
	
	
	private boolean isPeaceful(Entity entity) {
		if(entity instanceof EntityAmbientCreature || entity instanceof EntityPig || entity instanceof EntityChicken || entity instanceof EntityCow 
				|| entity instanceof EntityMooshroom || entity instanceof EntityHorse || entity instanceof EntitySheep){
			return true;
		}
		return false;
	}
}
