package com.arzeyt.darkness.lightOrb;

import java.util.List;

import com.arzeyt.darkness.Darkness;
import com.arzeyt.darkness.Reference;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

public class LightOrbItem extends Item {

	private final String itemName="lightOrbItem";
	private final double DISSIPATION_TICKS = 6000;
	private final int UPDATE_RATE = 20;
	
	private int dissipationCounter;
	private int counter=0;

	
	public LightOrbItem(){
		GameRegistry.registerItem(this, itemName);
		setUnlocalizedName(Darkness.MODID+"_"+itemName);
		setTextureName("darkness:lightOrb");
		setCreativeTab(Darkness.darknessTab);
		setMaxStackSize(1);
		this.dissipationCounter=(int) (DISSIPATION_TICKS/UPDATE_RATE);
		this.setMaxDamage(100);
		setFull3D();
	}
	
	public String getName(){
		return itemName;
	}
	
	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn,
			int itemSlot, boolean isSelected) {
			
		if(stack.stackSize==0){
			EntityPlayer p = (EntityPlayer)entityIn;
			p.inventory.setInventorySlotContents(itemSlot, null);
		}
		int power = getPowerFromNBT(stack);
		if(getPowerFromNBT(stack)!=0){
			this.setDamage(stack, 100-power);
		}
		super.onUpdate(stack, worldIn, entityIn, itemSlot, isSelected);
		
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer playerIn,
			List tooltip, boolean advanced) {

			
			if(stack.getTagCompound() != null){
				if(stack.getTagCompound().hasKey("darkness")){
					
					NBTTagCompound nbt = (NBTTagCompound) stack.getTagCompound().getTag("darkness");
					
					tooltip.add("id: "+nbt.getInteger(Reference.ID));
					tooltip.add("power: "+nbt.getInteger(Reference.POWER));
					tooltip.add("dissipation percent: "+nbt.getInteger(Reference.DISSIPATION_PERCENT));
					
					Reference r = Darkness.reference;
					int power = nbt.getInteger(Reference.POWER);
					
					if(power<10){
						stack.setStackDisplayName(EnumChatFormatting.DARK_RED+"Dying Light Orb");
					}else if(power<25){
						stack.setStackDisplayName(EnumChatFormatting.RED+"Faint Light Orb");
					}else if(power<50){
						stack.setStackDisplayName(EnumChatFormatting.GRAY+"Diminished Light Orb");
					}else if(power<=100){
						stack.setStackDisplayName(EnumChatFormatting.GOLD+"Light Orb");
					}
				}
			}
			
			super.addInformation(stack, playerIn, tooltip, advanced);
	}
	
	@Override
	public boolean hasEffect(ItemStack stack) {
		
		if(stack.getTagCompound() != null){
			return stack.getTagCompound().hasKey("darkness");
		}
		return false;
	}

	public int getPowerFromNBT(ItemStack stack){
		int power = 0;
		if(stack.hasTagCompound()){
			power = stack.getTagCompound().getCompoundTag("darkness").getInteger(Reference.POWER);
		}
		return power;
	}
	
	/**
	 * Overrides all nbt info! use with caution
	 * @param stack - a lightOrbItem item stack (no checks to ensure this)
	 * @param power
	 */
	public void setPowerNBT(ItemStack stack, int power){
		if(stack.hasTagCompound() && stack.getTagCompound().getTag("darkness")!=null){
			//System.out.println("darkness tag exists");
			NBTTagCompound compound = stack.getTagCompound();
			NBTTagCompound nbt = (NBTTagCompound) compound.getTag("darkness");
			nbt.setInteger("power", power);
			compound.setTag("darkness", nbt);
			stack.setTagCompound(compound);
			
		}
		
		NBTTagCompound compound = new NBTTagCompound();
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setInteger("power", power);
		compound.setTag("darkness", nbt);
		stack.setTagCompound(compound);
	}	
	
	@Override
	public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn,
			EntityPlayer playerIn) {
		
		if(itemStackIn.hasTagCompound()==false){
			//System.out.println("u must beh haxin, cuz that dont got no dats, brah");
		}else{
			//debug info CLIENT ONLY
			Reference r = new Reference();
			NBTTagCompound nbt = itemStackIn.getTagCompound().getCompoundTag("darkness");
			//System.out.println("------------------------------------------------------------------");
			//System.out.println("ID: "+nbt.getInteger(Reference.ID)+" Power: "+nbt.getInteger(Reference.POWER)+" DissipationP: "+nbt.getInteger(Reference.DISSIPATION_PERCENT));
			//System.out.println("orbs in list (lightOrbItem): "+Darkness.darkLists.getLightOrbs().size());
			
			//System.out.println("------------------------------------------------------------------");
		}

		
		
		return super.onItemRightClick(itemStackIn, worldIn, playerIn);
	}
	
}
