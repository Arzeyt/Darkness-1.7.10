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

public class BlockLighting extends BlockContainer{

	public final String unlocalizedName = "lightingBlock";
	@SideOnly(Side.CLIENT)
	IIcon blockIcon;
	
	public BlockLighting() {
		super(Material.air);
		
		this.setBlockName(TheDarkness.MODID+"_"+this.unlocalizedName);
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
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
	
	
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconRegister){
		this.blockIcon = iconRegister.registerIcon(TheDarkness.MODID+":"+"lightOrb");
	}
	
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta) {
		return blockIcon;
	}

	@Override
	public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
		return new TileEntityLighting();
	}

}
