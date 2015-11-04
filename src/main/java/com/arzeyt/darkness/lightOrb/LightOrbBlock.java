package com.arzeyt.darkness.lightOrb;

import com.arzeyt.darkness.Darkness;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public class LightOrbBlock extends Block {

	private final String name = "lightOrbBlock";
	
	public LightOrbBlock() {
		super(Material.glass);
		GameRegistry.registerBlock(this, name);
		this.setBlockName(Darkness.MODID+"_"+name);

		//setCreativeTab(Darkness.darknessTab);
		setLightLevel(0.8f);
		setBlockBounds(0.3f, 0.0f, 0.3f, 0.6f, 0.3f, 0.6f);
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World p_149668_1_, int p_149668_2_, int p_149668_3_, int p_149668_4_) {
		return null;
	}

	
	@Override
	public Block setBlockUnbreakable() {
		return this;
	}



	/**
	@Override
	public EnumWorldBlockLayer getBlockLayer() {
		return EnumWorldBlockLayer.CUTOUT;
	}**/


	@Override
	public boolean isNormalCube() {
		return false;
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int meta, float hitx, float hity, float hitz) {
		if(player.getHeldItem()==null){
			int currentSlot = player.inventory.currentItem;
			player.inventory.setInventorySlotContents(currentSlot, new ItemStack(Darkness.lightOrb));
			this.breakBlock(world,x,y,z, this, meta);
		}else{
			System.out.println("Hand must be empty");
		}
		return super.onBlockActivated(world,x,y,z,player,meta,hitx,hity,hitz);
	}


	public String getName() {
		return name;
	}
}
