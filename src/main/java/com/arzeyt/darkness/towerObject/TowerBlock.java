package com.arzeyt.darkness.towerObject;

import com.arzeyt.darkness.BlockPos;
import com.arzeyt.darkness.Darkness;
import com.arzeyt.darkness.Reference;
import com.arzeyt.darkness.lightOrb.LightOrbItem;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

import java.util.Random;

public class TowerBlock extends Block implements ITileEntityProvider{
	
	private final String name = "towerBlock";
	
	public TowerBlock(){
		super(Material.iron);
		GameRegistry.registerBlock(this, name);
		this.setBlockName(Darkness.MODID + "_" + name);
		//set block texture name**********
		
		setCreativeTab(Darkness.darknessTab);
		setLightLevel(1.0f);
		setBlockBounds(0f, 0f, 0f, 1f, 4f, 1f);
		isBlockContainer=true;
		setHardness(3F);

		
	}

	@Override
	public Item getItemDropped(int p_149650_1_, Random p_149650_2_, int p_149650_3_) {
		return null;
	}

	@Override
	public int quantityDropped(Random p_149745_1_) {
		return 0;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public int getRenderType() {
		return -1;
	}

	public String getName(){
		return name;
	}

	@Override
	public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
		return new TowerTileEntity();
	}

	@Override
	public boolean onBlockActivated(World worldIn, int x, int y, int z, EntityPlayer playerIn, int meta, float hitX, float hitY, float hitZ) {
		if(worldIn.isRemote==false){
			TowerTileEntity te = (TowerTileEntity) worldIn.getTileEntity(x,y,z);

			if(!te.isInvalid()){
				if(playerIn.getHeldItem()!=null){

					//add orb to tower, and replace tower power with orb power
					if(playerIn.getHeldItem().getItem() instanceof LightOrbItem){ //player is holding a light orb
						ItemStack orb = playerIn.getHeldItem();
						if(orb.getTagCompound().hasKey("darkness")){
							NBTTagCompound nbt = orb.getTagCompound().getCompoundTag("darkness");
							int orbPower = nbt.getInteger(Reference.POWER);
							te.setPower(orbPower);
							orb.stackSize--;
							Darkness.darkLists.removeLightOrb(orb);
							worldIn.playSoundAtEntity(playerIn, "darkness:bell", 1.0F, 1.1F);
							//System.out.println("set tower power to: "+te.getPower());
						}else{ //this should never happen
							//System.out.println("no data in orb");
						}
					}else{
						//player is holding something else
					}
				}else{//player is holding nothing
					//System.out.println("power = "+te.getPower()+" time is: "+worldIn.getWorldTime());
					te.takeOrb(playerIn);
					Random rand = new Random();
					worldIn.playSoundAtEntity(playerIn, "darkness:bell", 1.0F, 0.5F+rand.nextFloat());				}
			}

		}

		return true;
	}

	//should probably use an event to handle this
	@Override
	public void onBlockDestroyedByPlayer(World worldIn, int x, int y, int z, int meta) {
		if(Darkness.darkLists.getPoweredTowers().contains(worldIn.getTileEntity(x,y,z))){
			Darkness.darkLists.removePoweredTower((TowerTileEntity) worldIn.getTileEntity(x,y,z));
		}
		if(worldIn.isRemote) {
			if (Darkness.clientLists.getPoweredTowers().contains(worldIn.getTileEntity(x, y, z))) {
				Darkness.clientLists.removePoweredTower((TowerTileEntity) worldIn.getTileEntity(x, y, z));
			}
			Darkness.clientLists.removeFakeTowerAt(x,y,z);
		}
		super.onBlockDestroyedByPlayer(worldIn, x, y, z, meta);
	}

	@Override
	public void onBlockDestroyedByExplosion(World worldIn, int x, int y, int z, Explosion explosion) {
		if(Darkness.darkLists.getPoweredTowers().contains(worldIn.getTileEntity(x,y,z))){
			Darkness.darkLists.removePoweredTower((TowerTileEntity) worldIn.getTileEntity(x,y,z));
		}
		if(worldIn.isRemote) {
			if (Darkness.clientLists.getPoweredTowers().contains(worldIn.getTileEntity(x, y, z))) {
				Darkness.clientLists.removePoweredTower((TowerTileEntity) worldIn.getTileEntity(x, y, z));
			}
			Darkness.clientLists.removeFakeTowerAt(x,y,z);
		}
		super.onBlockDestroyedByExplosion(worldIn, x, y, z, explosion);
	}
}
