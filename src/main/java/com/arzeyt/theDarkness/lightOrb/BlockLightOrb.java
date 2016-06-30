package com.arzeyt.theDarkness.lightOrb;

import com.arzeyt.theDarkness.TheDarkness;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class BlockLightOrb extends BlockContainer {

	public final String unlocalizedName = "lightOrbBlock";
	@SideOnly(Side.CLIENT)
	IIcon blockIcon;
	
	public BlockLightOrb() {
		super(Material.rock);
		
		this.setBlockName(TheDarkness.MODID+"_"+this.unlocalizedName);
		this.setBlockBounds(0.3125F, 0.0F, 0.3125F, 0.6875F, 0.375F, 0.6875F);
		this.setCreativeTab(CreativeTabs.tabMisc);
		this.setLightLevel(0.8F);
	}

	public int getRenderType(){
		return -1;
	}
	
	public boolean isOpaqueCube(){
		return false;
	}
	
	public boolean renderAsNormalBlock(){
		return false;
	}
	
	@Override
	public TileEntity createNewTileEntity(World world, int p_149915_2_) {
		return new TileEntityLightOrb();
	}
	
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconRegister){
		this.blockIcon = iconRegister.registerIcon(TheDarkness.MODID+":"+"lightOrb");
	}
	
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta) {
		return blockIcon;
	}

	@Override
	public void breakBlock(World world, int x, int y,
			int z, Block p_149749_5_, int p_149749_6_) {
		TheDarkness.towerManager.removeOrb((TileEntityLightOrb) world.getTileEntity(x, y, z));
		world.removeTileEntity(x, y, z);
		super.breakBlock(world, x, y, z,
				p_149749_5_, p_149749_6_);
	}
}
