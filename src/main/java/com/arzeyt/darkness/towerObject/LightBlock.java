package com.arzeyt.darkness.towerObject;

import java.util.Random;

import com.arzeyt.darkness.Darkness;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class LightBlock extends Block{
	
	private final String name = "lightBlock";

	public LightBlock() {
		super(Material.air);
		GameRegistry.registerBlock(this, name);
		this.setBlockName(Darkness.MODID + "_" + name);
		this.setBlockBounds(0F, 0F, 0F, 0F, 0F, 0F);
		
		setLightLevel(0.5F);
	}

	//is air?

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		return null;
	}
	
	@Override
	public int getRenderType() {
		return -1;
	}
	
	@Override
	public boolean isOpaqueCube() {
		return false;
	}
}
