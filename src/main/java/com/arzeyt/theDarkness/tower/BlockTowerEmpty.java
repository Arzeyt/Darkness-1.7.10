package com.arzeyt.theDarkness.tower;

import com.arzeyt.theDarkness.TheDarkness;
import com.arzeyt.theDarkness.TowerType;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class BlockTowerEmpty extends Block {
	
	@SideOnly(Side.CLIENT)
	private IIcon emptyIcon;
	
	public final String unlocalizedName = "towerBlockEmpty";

	public BlockTowerEmpty() {
		super(Material.rock);
		
		setBlockName(TheDarkness.MODID+"_"+unlocalizedName);
		setBlockUnbreakable();
		setCreativeTab(CreativeTabs.tabMisc);
		setLightLevel(0.2F);
		
	}

	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister register){
		this.emptyIcon = register.registerIcon(TheDarkness.MODID+":"+"towerBlockEmpty");
	}
	
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta) {
		return emptyIcon;
	}
	
	@Override
	public boolean hasTileEntity(int metadata) {
		return true;
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
			te.takeOrb(player);
		}else if(player.getHeldItem().getUnlocalizedName().equals(lightOrbBlockItem.getUnlocalizedName())){
			System.out.println("hit the tower with a light orb");
			player.inventory.consumeInventoryItem(player.getHeldItem().getItem());
			te.addOrbs(1);
			te.toLightblock(world);
		}else{
			te.takeOrb(player);
		}
		
		return true;
		
	}
	
	@Override
	public void onBlockAdded(World world, int x,
			int y, int z) {
	}
	
	
	@Override
	public TileEntity createTileEntity(World world, int metadata) {
		//System.out.println("created new tile entity isRemote: "+world.isRemote);
		TileEntityTower tower = new TileEntityTower();
		tower.orbs=0;
		tower.towerType=TowerType.EMPTY;
		return tower;
	}
	
	@Override
	public void breakBlock(World world, int x, int y,
			int z, Block block, int p_149749_6_) {
	
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
