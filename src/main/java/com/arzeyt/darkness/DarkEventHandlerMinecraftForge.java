package com.arzeyt.darkness;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.item.ItemExpireEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;

import com.arzeyt.darkness.lightOrb.DetonationMessageToClient;
import com.arzeyt.darkness.lightOrb.LightOrbItem;
import com.arzeyt.darkness.towerObject.TowerBlock;
import com.arzeyt.darkness.towerObject.TowerTileEntity;

import static com.arzeyt.darkness.MobSpawnerData.*;

public class DarkEventHandlerMinecraftForge {

	@SubscribeEvent
	public void addOrbsFromInventories(EntityJoinWorldEvent e){
		if(e.entity instanceof EntityPlayer){
			EntityPlayer p = (EntityPlayer) e.entity;
			for(ItemStack stack : p.inventory.mainInventory){
				if(stack!=null 
						&& stack.hasTagCompound()
						&& stack.getItem() instanceof LightOrbItem){
					Darkness.darkLists.addLightOrb(stack);

					//System.out.println("added light orb to darkList");
				}
			}
		}
	}
	@SubscribeEvent
	public void onTowerBreak(BreakEvent e){
		if(e.world.isRemote)return;
		if(e.world.getTileEntity(e.x, e.y, e.z) instanceof TowerTileEntity){
			TowerTileEntity te = (TowerTileEntity) e.world.getTileEntity(e.x, e.y, e.z);
			if(Darkness.darkLists.towerExists(te)){
				Darkness.darkLists.removePoweredTower(te);

				//System.out.println("removed tower");
			}
		}
	}
	
	@SubscribeEvent
	public void onDarkBlockBreak(BreakEvent e){
		if(e.world.isRemote) return;
		EntityPlayer p = e.getPlayer();
		BlockPos pos = new BlockPos(e.x, e.y, e.z);
		if(Darkness.darkLists.inDarkness(e.world, new BlockPos(e.x, e.y, e.z))){
			e.setCanceled(true);
			e.world.playSoundAtEntity(p, "darkness:whooshPuff", 1.2F, 1.0F);
			Darkness.simpleNetworkWrapper.sendToAll(new FXMessageToClient(Reference.FX_BLOCK, pos.getX(), pos.getY(), pos.getZ()));

		}
	}
	
	@SubscribeEvent
	public void onBlockPlaceInDarkness(PlayerInteractEvent e){
		if(e.entity.worldObj.isRemote)return;
		if(Darkness.darkLists.isPlayerInDarkness(e.entityPlayer)){
			if(e.entityPlayer.inventory.getCurrentItem()!=null){
				ItemStack stack = e.entityPlayer.inventory.getCurrentItem();
				//item is block
				if(Block.getBlockFromItem(stack.getItem())!=null){
					System.out.println("denied!");
					e.useItem= Event.Result.DENY;
					Darkness.simpleNetworkWrapper.sendTo(new FXMessageToClient(Reference.FX_BLOCK, e.x,e.y+1,e.z),(EntityPlayerMP) e.entityPlayer);
					e.world.playSoundAtEntity(e.entityPlayer, "darkness:whooshPuff", 1.2F, 1.0F);
				}
			}
		}
	}
	
	@SubscribeEvent
	public void onOrbDrop(ItemTossEvent e){
		if(e.entityItem.getEntityItem().getItem() instanceof LightOrbItem){
			Darkness.darkLists.removePlayerWithOrb(e.player);
			e.entityItem.lifespan=20*1;
		}
	}
	
	@SubscribeEvent
	public void onOrbDespawnDetonate(ItemExpireEvent e){
		if(e.entity.worldObj.isRemote)return;
		if(e.entityItem.getEntityItem().getItem() instanceof LightOrbItem){
			ItemStack orb = e.entityItem.getEntityItem();
			BlockPos pos = new BlockPos(e.entity.posX, e.entity.posY, e.entity.posZ);
			
			Darkness.darkLists.removeLightOrb(orb);
			Darkness.darkLists.addNewOrbDetonation(e.entity.worldObj, pos);
			Darkness.simpleNetworkWrapper.sendToDimension(new DetonationMessageToClient(true, pos.getX(), pos.getY(), pos.getZ()), e.entityItem.dimension);
			e.entity.worldObj.playSoundAtEntity(e.entity, "darkness:sustainedBell", 1.0F, 1.0F);


			//throw mobs and set them on fire. constant fire is handled in DarkEventHandlerFML
			Random rand = new Random();
			World w = e.entity.worldObj;
			Reference r = new Reference();
			List mobs = w.getEntitiesWithinAABB(EntityMob.class, AxisAlignedBB.getBoundingBox(pos.getX() - Reference.ORB_DETONATION_RAIDUS, pos.getY() - Reference.ORB_DETONATION_RAIDUS, pos.getZ() - Reference.ORB_DETONATION_RAIDUS, pos.getX() + Reference.ORB_DETONATION_RAIDUS, pos.getY() + Reference.ORB_DETONATION_RAIDUS, pos.getZ() + Reference.ORB_DETONATION_RAIDUS));
			Iterator it = mobs.iterator();
			while(it.hasNext()){
				EntityMob mob = (EntityMob) it.next();
				mob.addVelocity(-0.5D+rand.nextDouble(), 2D, -0.5D+rand.nextDouble());
				mob.setFire(10);
				mob.attackEntityFrom(DamageSource.onFire, 10);
			}
		}
	}
	
	//used this instead of light orb class because it wouldn't let me decrement the item stack.
	@SubscribeEvent
	public void onOrbUse(PlayerInteractEvent e){
		if(PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK != null
				&& e.world.isRemote==false){
			World w = e.entity.worldObj;
			BlockPos pos = new BlockPos(e.x, e.y, e.z);
			ItemStack stack = e.entityPlayer.getHeldItem();
			
			if(w.isRemote==false
					&& stack!= null 
					&& stack.getItem() instanceof LightOrbItem){
				//debug

				//System.out.println("Towers: "+Darkness.darkLists.getPoweredTowers().size());
				for(TowerTileEntity t : Darkness.darkLists.getPoweredTowers()){

					//System.out.println("tower pos: "+t.xCoord+" "+t.yCoord+" "+t.zCoord);
				}
				
				
				if(Darkness.darkLists.isPosInTowerRadiusX2minus1(w, pos)==false){
					if(w.getBlock(pos.getX(), pos.getY(), pos.getZ()) instanceof TowerBlock){
						//handled in towerblock class
					}else if(w.getBlock(pos.getX(), pos.getY() + 1, pos.getZ()) instanceof BlockAir
							&& w.getBlock(pos.getX(), pos.getY() + 2, pos.getZ()) instanceof BlockAir){
						w.setBlock(pos.getX(), pos.getY()+1, pos.getZ(), Darkness.towerBlock);
						Darkness.darkLists.removeLightOrb(stack);
						stack.stackSize--;
						Random rand = new Random();
						w.playSoundAtEntity(e.entityPlayer, "darkness:bell", 1.0F, 0.5F+rand.nextFloat());
					}
				}
			}
		}
	}
	
	@SubscribeEvent
	public void onMobDamage(LivingAttackEvent e){
		//player attack
		if(e.entityLiving.worldObj.isRemote)return;
		if(e.source.getEntity() instanceof EntityPlayer){
			EntityPlayer p = (EntityPlayer) e.source.getEntity();
			//invincimob
			if(e.entityLiving instanceof EntityMob){
				EntityMob mob = (EntityMob) e.entityLiving;
				//mob darkness check
				if(Darkness.darkLists.inDarkness(mob.worldObj, new BlockPos(mob.posX, mob.posY, mob.posZ))){
					Darkness.simpleNetworkWrapper.sendToAll(new FXMessageToClient(Reference.FX_BLOCK, (int)mob.posX, (int)mob.posY, (int)mob.posZ));
					mob.worldObj.playSoundAtEntity(mob, "darkness:whoosh", 1.0F, 1.0F);
					e.setCanceled(true);
				}
			//teleport animal
			}else if(e.entityLiving instanceof EntityAnimal){
				EntityAnimal ani = (EntityAnimal) e.entityLiving;

				BlockPos anipos = new BlockPos(ani.posX, ani.posY, ani.posZ);
				if(Darkness.darkLists.inDarkness(ani.worldObj, anipos)){
					Darkness.simpleNetworkWrapper.sendToAll(new FXMessageToClient(Reference.FX_VANISH, anipos.getX(), anipos.getY(), anipos.getZ()));
					EffectHelper.teleportRandomly(ani, Reference.EVASION_RADIUS);
					ani.worldObj.playSoundAtEntity(ani, "darkness:teleWhoosh", 1.5F, 1.0F);
					e.setCanceled(true);
				}
			}
			//player is attacked
		}else if(e.entityLiving instanceof EntityPlayer){
			EntityPlayer p = (EntityPlayer) e.entityLiving;
			//all mobs
			if(e.source.getEntity() instanceof EntityMob){
				p.attackEntityFrom(DamageSource.wither, e.ammount/2);
			}
			//spiders
			if(e.source.getEntity() instanceof EntitySpider){
				Random rand = new Random();
				int value = rand.nextInt(2);
				if(value==0)
					//poison
					p.addPotionEffect(new PotionEffect(19, 20*5, 0));
				else if(value==1){
					//stronger poison
					p.addPotionEffect(new PotionEffect(19, 20*5, 1));
					//nausea
				}else if(value==2){
					p.addPotionEffect(new PotionEffect(9, 20*15, 0));
				}
			}
		}else if (e.entityLiving instanceof EntityMob) {
		}
	}

	@SubscribeEvent
	public void meow(PlayerInteractEvent e){
		Random rand = new Random();
		//e.world.playSoundAtEntity(e.entityPlayer, "darkness:meow", 1.0F, 0.5F+rand.nextFloat());
	}

	@SubscribeEvent
	public void playerDeath(LivingDeathEvent e){
		if(e.entityLiving.worldObj.isRemote)return;
		if(e.entityLiving instanceof EntityPlayer){

			//System.out.println("player died");
			EntityPlayer p = (EntityPlayer) e.entityLiving;
			p.setSpawnChunk(new ChunkCoordinates((int)p.posX, (int)p.posY, (int)p.posZ), true, p.dimension);
			if(p.getEntityData().hasKey("darkness")==false){
				p.getEntityData().setTag("darkness", new NBTTagCompound());
			}
			NBTTagCompound tag = (NBTTagCompound) p.getEntityData().getTag("darkness");
			tag.setInteger(Reference.P_SPAWN_DIMID, p.worldObj.provider.dimensionId);
			tag.setInteger(Reference.P_SPAWN_X, (int) p.posX);
			tag.setInteger(Reference.P_SPAWN_Y, (int) p.posY);
			tag.setInteger(Reference.P_SPAWN_Z, (int) p.posZ);
			Darkness.darkLists.addSpawnPoint(p,new BlockPos(p.posX, p.posY, p.posZ));
			Darkness.darkLists.addGhostPlayer(p);
		}
	}

	//cancels interactions with everything. Also resurrects on tower absorb
	@SubscribeEvent
	public void deadPlayerInteract(PlayerInteractEvent e) {
		//
		// //System.out.println("interact = "+e.action.toString());
		if (e.entityPlayer.worldObj.isRemote == false) { //server side resurrection
			EntityPlayer p = e.entityPlayer;
			if (Darkness.darkLists.isGhost(p)) {
				System.out.println("player is ghost");
				if (e.action.equals(PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK)) {
					if (p.getEntityWorld().getTileEntity(e.x, e.y, e.z) != null
							&& p.getEntityWorld().getTileEntity(e.x, e.y, e.z) instanceof TowerTileEntity) {//resurrection
						TowerTileEntity t = (TowerTileEntity) p.getEntityWorld().getTileEntity(e.x, e.y, e.z);
						if (t.getPower() > 99) {
							t.setPower(1);
							p.setInvisible(false);
							DPlayer.nbtSetGhost(p, false);
							Darkness.simpleNetworkWrapper.sendTo(new GhostMessageToClient(false), (EntityPlayerMP) p);
							Darkness.darkLists.removeGhostPlayer(p);
							Darkness.simpleNetworkWrapper.sendToAll(new FXMessageToClient(Reference.FX_OUTWARDS_SPARKLE, (int) p.posX, (int) p.posY, (int) p.posZ));
							p.worldObj.playSoundAtEntity(p, "darkness:bell", 1.0F, 1.2F);
						}
					}
				}
				e.setCanceled(true);
			}
		} else { //client side
			if(Darkness.clientLists.isGhost()) {

				if (e.action.equals(PlayerInteractEvent.Action.RIGHT_CLICK_AIR)) {//blink

					//System.out.println("right clicked air");
					if (e.entityPlayer.getFoodStats().getFoodLevel() >= 6) {

						//System.out.println("blink passed");
						EffectHelper.blink(e.entityPlayer);
						e.entityPlayer.getFoodStats().setFoodLevel(e.entityPlayer.getFoodStats().getFoodLevel() - 6);
						//}
					}
					e.setCanceled(true);
				}
			}
		}
	}

	@SubscribeEvent
	public void deadPlayerAttack(LivingHurtEvent e){
		//player is attacking
		if(e.source.getEntity() instanceof EntityPlayer){
			EntityPlayer p = (EntityPlayer) e.source.getEntity();
			if(Darkness.darkLists.isGhost(p)){//ghost
				if(Darkness.darkLists.isPlayerInDarkness(p)) {//in darkness
					EntityLiving entity = (EntityLiving) e.entityLiving;
					entity.attackEntityFrom(DamageSource.magic, 0.5F);
					e.setCanceled(true);

				}else{//in light
					if(e.entityLiving instanceof EntityPlayer==false){//not a player
						e.entityLiving.attackEntityFrom(DamageSource.magic, 0.5F);
						e.entityLiving.setFire(2);
						e.setCanceled(true);
					}else{//a player
						e.entityLiving.attackEntityFrom(DamageSource.magic, 0.5F);
						e.setCanceled(true);
					}
				}
			}

			//something attacking player
		}else if(e.entityLiving instanceof EntityPlayer){
			EntityPlayer p = (EntityPlayer) e.entityLiving;
			if(Darkness.darkLists.isGhost(p)){
				e.setCanceled(true);
			}
		}
	}

	@SubscribeEvent
	public void deadPlayerPickup(EntityItemPickupEvent e){
		if(Darkness.darkLists.isGhost(e.entityPlayer)){
			e.setCanceled(true);
		}
	}

	@SubscribeEvent
	public void deadTargetEvent(LivingSetAttackTargetEvent e){
		if(e.entityLiving.worldObj.isRemote==true)return;
		if(e.target instanceof  EntityPlayer){
			if (Darkness.darkLists.isGhost((EntityPlayer) e.target)){
				e.entityLiving.setRevengeTarget(null);
			}
		}
	}

	@SubscribeEvent
	public void darkMobDeath(LivingDeathEvent e){
		if(e.entity.getEntityData().hasKey("darkness")){
			mobs.remove(e.entityLiving);
		}
	}

}
