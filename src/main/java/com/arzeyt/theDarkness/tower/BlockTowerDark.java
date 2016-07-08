package com.arzeyt.theDarkness.tower;

import java.util.HashMap;
import java.util.List;

import com.arzeyt.theDarkness.TheDarkness;
import com.arzeyt.theDarkness.TowerType;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class BlockTowerDark extends BlockContainer {

	@SideOnly(Side.CLIENT)
	private IIcon darkIcon;
	
	public final String unlocalizedName = "towerBlockDark";
	
	public BlockTowerDark() {
		super(Material.rock);
		setBlockName(TheDarkness.MODID+"_"+unlocalizedName);
		setCreativeTab(CreativeTabs.tabBlock);
		setBlockUnbreakable();		
		
	}
	
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister register){
		this.darkIcon = register.registerIcon(TheDarkness.MODID+":"+"towerBlockDark");
	}
	
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta) {
		return darkIcon;
	}

	@Override
	public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
		TileEntityTower tower = new TileEntityTower();
		tower.orbs=-1;
		tower.towerType=TowerType.DARK;
		return tower;
	}
	
	@Override
	public boolean onBlockActivated(World world, int x,
			int y, int z, EntityPlayer player,
			int p_149727_6_, float p_149727_7_, float p_149727_8_,
			float p_149727_9_) {
		
		if(world.isRemote==true)return true;
		
		TileEntityTower te = (TileEntityTower) world.getTileEntity(x, y, z);
		Item lightOrbBlockItem = Item.getItemFromBlock(TheDarkness.lightOrbBlock);
		
		if(player.getHeldItem()==null){
			
		}else if(player.getHeldItem().getItem()==lightOrbBlockItem){
			player.inventory.consumeInventoryItem(lightOrbBlockItem);
			te.addOrbs(1);
			te.toLightblock(world);
		}
		
		
		System.out.println("orbs: "+te.orbs+" isRemote: "+world.isRemote);
		return true;
		
	}

	@Override
	public void breakBlock(World world, int x, int y,
			int z, Block p_149749_5_, int p_149749_6_) {
		
		try{
			TileEntityTower te = (TileEntityTower) world.getTileEntity(x, y, z);
			TheDarkness.towerManager.removeTower(te);
			world.removeTileEntity(x, y, z);
		}catch(Exception e){
			e.printStackTrace();
		}
		
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

	
}
