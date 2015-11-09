package com.arzeyt.darkness;

import java.util.*;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ServerTickEvent;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.WorldServer;

import com.arzeyt.darkness.lightOrb.Detonation;
import com.arzeyt.darkness.lightOrb.DetonationMessageToClient;
import com.arzeyt.darkness.lightOrb.LightOrbItem;
import com.arzeyt.darkness.lightOrb.OrbUpdateMessageToClient;

import static cpw.mods.fml.relauncher.Side.CLIENT;


public class DarkEventHandlerFML {

	private int counter = 1;
	
	Reference r = new Reference();
	
	//40 since it's called twice. Counter increments 2 times faster than normal
	private final int DARKNESS_CHECK_RATE = Reference.DARKNESS_CHECK_RATE;
	private final int ORB_DEPLETION_RATE = Reference.ORB_DEPLETETION_RATE;
	private final int TOWER_RADIUS= Reference.TOWER_RADIUS;
	private final int HELD_ORB_RADIUS= Reference.HELD_ORB_RADIUS;
	private final int ORB_DETONATION_RAIDUS= Reference.ORB_DETONATION_RAIDUS;

	
	@SubscribeEvent
	public void darknessCheck(ServerTickEvent e){
		
		if(counter%DARKNESS_CHECK_RATE==0){

			ArrayList list = (ArrayList) MinecraftServer.getServer().getConfigurationManager().playerEntityList;
			Iterator iterator = list.iterator();
			while(iterator.hasNext())
			{
				EntityPlayerMP player = (EntityPlayerMP) iterator.next();
				WorldServer world = MinecraftServer.getServer().worldServerForDimension(player.dimension);

			//not in overworld
				if(world.provider.dimensionId!=0){
					Darkness.darkLists.removePlayerInDarkness(player);
				}
				
			//held orb
				else if(player.getHeldItem()!=null
						&& player.getHeldItem().getItem() instanceof LightOrbItem
						&& Darkness.darkLists.getPlayersWithOrb().contains(player)==false){					
					
					Darkness.darkLists.addPlayerWithOrb(player);
					Darkness.darkLists.removePlayerInDarkness(player);
					//System.out.println(player.getDisplayName()+" is holding orb");
				}
			//tower
				else if(Darkness.darkLists.getPoweredTowers().isEmpty()==false
						&& Darkness.darkLists.isPlayerInTowerRadius(player)){
					Darkness.darkLists.removePlayerInDarkness(player);
					//System.out.println(player.getDisplayName()+" is near powered tower");
				}
			//player
				else if(Darkness.darkLists.getPlayersWithOrb().isEmpty()==false
						&& Darkness.darkLists.getDistanceToNearestPlayerWithOrb(player)<=HELD_ORB_RADIUS){
					Darkness.darkLists.removePlayerInDarkness(player);
					//System.out.println(player.getDisplayName()+" is near player with orb");
				}
			//detonations
				else if(Darkness.darkLists.getOrbDetonations().isEmpty()==false
						&& Darkness.darkLists.getDistanceToNearestOrbDetonation(player)<=ORB_DETONATION_RAIDUS){
					Darkness.darkLists.removePlayerInDarkness(player);
					//System.out.println(player.getDisplayName()+" is near orb detonation");
				}
			//player is in darkness if none of the above are true
				else if(Darkness.darkLists.getPlayersInDarkness().isEmpty()==true
							|| Darkness.darkLists.getPlayersInDarkness().contains(player)==false){
						Darkness.darkLists.addPlayersInDarkness(player);
						//System.out.println(player.getDisplayName()+" is in darkness");
				}
				
				//System.out.println("Player in darkness: "+Darkness.darkLists.isPlayerInDarkness(player));
			}
		
		}
		counter++;
		if(counter>123456){
			counter=1;
		}
	}
	
	@SubscribeEvent
	public void orbDepletion(ServerTickEvent e){
		if(e.side== Side.CLIENT)return;
		if(counter%ORB_DEPLETION_RATE==0
				&&Darkness.darkLists.getLightOrbs().isEmpty()==false){
			//System.out.println("orbs in list: "+Darkness.darkLists.getLightOrbs());
			HashSet<ItemStack> removalOrbs = new HashSet<ItemStack>();
			
			for(ItemStack orb : Darkness.darkLists.getLightOrbs()){
				if(orb.hasTagCompound()==false){
					//System.out.println("Orb doesn't have TAG! D:");
				}else{
					NBTTagCompound nbt = (NBTTagCompound) orb.getTagCompound().getTag("darkness");
					
					int id = nbt.getInteger(Reference.ID);
					int dissipationPercent = nbt.getInteger(Reference.DISSIPATION_PERCENT);
					int initialPower = nbt.getInteger(Reference.INITAL_POWER);
					int power = nbt.getInteger(Reference.POWER);
					

					dissipationPercent++;
					power = initialPower-dissipationPercent;
					
					if(power<1){
						ItemStack pOrb = Darkness.darkLists.getActualOrbFromID(id);
						if(pOrb==null){
							//System.out.println("player orb cannot be found!");
						}else{
							pOrb.stackSize--;
							removalOrbs.add(orb);
							//System.out.println("removed orb");
						}
					}
					
					nbt.setInteger(Reference.POWER, power);
					nbt.setInteger(Reference.DISSIPATION_PERCENT, dissipationPercent);
					
					//System.out.println("Power: "+power+" dissipationPercent: "+dissipationPercent);
					//System.out.println("nbt data says id: "+nbt.getInteger(Reference.ID)+" Power: "+nbt.getInteger(Reference.POWER)+" dissipationP: "+nbt.getInteger(Reference.DISSIPATION_PERCENT));
					
					//per player basis... map needs to include orb owner
					//System.out.println("sending orb update message");
					Darkness.simpleNetworkWrapper.sendToAll(new OrbUpdateMessageToClient(id, power, dissipationPercent));
				}
			}
			for(ItemStack deadOrb : removalOrbs){
				Darkness.darkLists.removeLightOrb(deadOrb);
			}
		}
	}
	
	@SubscribeEvent
	public void detonationDepletion(ServerTickEvent e){
		if(e.side==Side.CLIENT)return;
		if(Darkness.darkLists.getOrbDetonations().isEmpty()==false){
			HashSet<Detonation> toRemove = new HashSet<Detonation>();

			//
			for(Detonation d : Darkness.darkLists.getOrbDetonations()){
				if(d.lifeRemaining<=0){
					toRemove.add(d);
					Darkness.simpleNetworkWrapper.sendToAll(new DetonationMessageToClient(false, d.pos.getX(), d.pos.getY(), d.pos.getZ()));
					//System.out.println("sent orb detonate message");
				}else{
					d.lifeRemaining--;
				}
			}
			
			if(toRemove.isEmpty()==false){
				for(Detonation d : toRemove){
					Darkness.darkLists.removeOrbDetonation(d.w, d.pos);
				}
			}
			//apply firey swag (too many ticks for this?) efficiency improvement possible
			int r = Reference.ORB_DETONATION_RAIDUS;
			for(Detonation d : Darkness.darkLists.getOrbDetonations()){
				List mobList = d.w.getEntitiesWithinAABB(EntityMob.class, AxisAlignedBB.getBoundingBox(d.pos.getX() - r, d.pos.getY() - r, d.pos.getZ() - r, d.pos.getX() + r, d.pos.getY() + r, d.pos.getZ() + r));
				Iterator it = mobList.iterator();
				while(it.hasNext()){
					EntityMob mob = (EntityMob) it.next();
					BlockPos mobPos = new BlockPos(mob.posX, mob.posY, mob.posZ);
					if(Darkness.darkLists.getDistanceToNearestOrbDetonation(mob.worldObj, mobPos)<=r){
						mob.fireResistance=0;
						mob.setFire(1);
					}
				}
			}
			
		}
	}
	
	//player tick event wasn't working so went with player loop through server tick 
	@SubscribeEvent
	public void playerEffects(ServerTickEvent e){
		if(counter%(DARKNESS_CHECK_RATE)==3){//offset from darkness check tick a bit...
			int icounter = 0;//unused for nao
			icounter++;
			
			ArrayList list = (ArrayList) MinecraftServer.getServer().getConfigurationManager().playerEntityList;
			Iterator iterator = list.iterator();
			while(iterator.hasNext())
			{
				EntityPlayer player = (EntityPlayer) iterator.next();
				WorldServer world = MinecraftServer.getServer().worldServerForDimension(player.dimension);
				BlockPos ppos = new BlockPos(player.posX, player.posY, player.posZ);
				
				//potion effect
				if(world.provider.dimensionId==0
					&&Darkness.darkLists.isPlayerInTowerRadius(player)==false){
					if(Darkness.darkLists.isGhost(player)) {
						player.removePotionEffect(16);
						player.removePotionEffect(23);
						player.removePotionEffect(1);
						player.removePotionEffect(8);
					}else if(player.getHeldItem() != null
						&& player.getHeldItem().getItem() instanceof LightOrbItem){
						player.addPotionEffect(new PotionEffect(2, DARKNESS_CHECK_RATE, 2));
					}else{
						player.addPotionEffect(new PotionEffect(2, DARKNESS_CHECK_RATE, 1));

					}
				}else {//in light
					if (Darkness.darkLists.isGhost(player)) {
						player.addPotionEffect(new PotionEffect(16, DARKNESS_CHECK_RATE, 0));
						player.addPotionEffect(new PotionEffect(23, DARKNESS_CHECK_RATE, 0));
						player.addPotionEffect(new PotionEffect(1, DARKNESS_CHECK_RATE, 0));
						player.addPotionEffect(new PotionEffect(8, DARKNESS_CHECK_RATE, 1));//jump
					} else {
						player.removePotionEffect(2);
					}
				}
				//remove from held orb list (doesn't really belong here...)
				ItemStack stack = player.getHeldItem();
				if(stack !=null 
						&& stack.getItem() instanceof LightOrbItem ==false
						&& Darkness.darkLists.getPlayersWithOrb().contains(player)){
					Darkness.darkLists.removePlayerWithOrb(player);
				}else if(stack==null
						&& Darkness.darkLists.getPlayersWithOrb().contains(player)){
					Darkness.darkLists.removePlayerWithOrb(player);
				}

				//dark player invisible AND flight
				if(Darkness.darkLists.isGhost(player)){
					player.setInvisible(true);
					player.addPotionEffect(new PotionEffect(14, DARKNESS_CHECK_RATE, 0));

					if(Darkness.darkLists.isPlayerInTowerRadius(player)){
						//player.capabilities.allowFlying=true;
					}
				}


			}
		}
	}




	@SubscribeEvent
	public void playerRespawn(PlayerEvent.PlayerRespawnEvent e){
		if(e.player.worldObj.isRemote==true)return;
		if(e.player.getEntityData().hasKey("darkness")){
			//System.out.println("player has death location saved as NBT");
			NBTTagCompound nbt = e.player.getEntityData().getCompoundTag("darkness");
			int dim = nbt.getInteger(Reference.P_SPAWN_DIMID);
			if(dim==0) {
				int x = nbt.getInteger(Reference.P_SPAWN_X);
				int y = nbt.getInteger(Reference.P_SPAWN_Y);
				int z = nbt.getInteger(Reference.P_SPAWN_Z);
				BlockPos spawnPos = new BlockPos(x, y, z);
				spawnPos = EffectHelper.findGroundY(e.player.worldObj, spawnPos);
				e.player.setPositionAndUpdate(spawnPos.getX(), spawnPos.getY() + 1, spawnPos.getZ());
			}
		}else{

			BlockPos spawnPoint = Darkness.darkLists.getSpawnFor(e.player);
			if(spawnPoint!=null && e.player.worldObj.provider.dimensionId==0) {
				spawnPoint = EffectHelper.findGroundY(e.player.worldObj, spawnPoint);
				e.player.setPositionAndUpdate(spawnPoint.getX(), spawnPoint.getY() + 1, spawnPoint.getZ());
			}
		}

		Darkness.darkLists.addGhostPlayer(e.player);
		e.player.setInvisible(true);
		DPlayer.nbtSetGhost(e.player, true);
		Darkness.simpleNetworkWrapper.sendTo(new GhostMessageToClient(true), (EntityPlayerMP) e.player);

		BlockPos pos = new BlockPos(e.player.posX, e.player.posY, e.player.posZ);
		int range = 10;
		List mobs = e.player.worldObj.getEntitiesWithinAABB(EntityMob.class, AxisAlignedBB.getBoundingBox(pos.getX()-range, pos.getY()-range, pos.getZ()-range, pos.getX()+range, pos.getY()+range,pos.getZ()+range));
		{
			Iterator it = mobs.iterator();
			while (it.hasNext()) {
				EntityMob mob = (EntityMob) it.next();
				mob.setAttackTarget(null);
				mob.setRevengeTarget(null);
			}
		}
		e.player.inventory.addItemStackToInventory(new ItemStack(Items.ender_pearl));
	}

	@SubscribeEvent
	public void playerLogIn(PlayerEvent.PlayerLoggedInEvent e){
		if(e.player.worldObj.isRemote){return;}
		if(DPlayer.isGhost(e.player)){
			Darkness.darkLists.addGhostPlayer(e.player);
			Darkness.simpleNetworkWrapper.sendTo(new GhostMessageToClient(true), (EntityPlayerMP) e.player);
		}
	}
}

