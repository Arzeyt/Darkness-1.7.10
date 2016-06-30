package com.arzeyt.theDarkness;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemLightOrb extends Item {
	public final String unlocalizedName = "lightOrb(Old)";
	StringBuilder sb = new StringBuilder(1000);

	
	public ItemLightOrb(){
		setUnlocalizedName(TheDarkness.MODID+"_"+unlocalizedName);
		setCreativeTab(CreativeTabs.tabMisc);
		setTextureName(TheDarkness.MODID+":"+unlocalizedName);
		
		//drop
		//onDroppedByPlayer(new ItemStack(this), player)
	}
	
	
	@Override
	public boolean onItemUse(ItemStack item, EntityPlayer player,
			World world, int x, int y, int z,
			int p_77648_7_, float p_77648_8_, float p_77648_9_,
			float p_77648_10_) {
		
		addStringToBuilder("*********Client side= "+world.isRemote);
		addStringToBuilder("towers in map: "+TheDarkness.towerManager.towers.size());
		addStringToBuilder("player inDarkness: "+TheDarkness.towerManager.inDarkness(world, x, y, z));
		addStringToBuilder("tdp player inDarkness: "+TheDarkness.towerManager.getTDPlayer(player).inDarkness);
		addStringToBuilder("player tick  value: "+TheDarkness.towerManager.getTDPlayer(player).counter);
		addStringToBuilder("holding light orb: "+TheDarkness.towerManager.getTDPlayer(player).isHoldingLightOrb);
		addStringToBuilder("in light field: "+TheDarkness.towerManager.getTDPlayer(player).inLightOrbField);
		addStringToBuilder("by dark tower: "+TheDarkness.towerManager.getTDPlayer(player).byDarkTower);
		addStringToBuilder("#of tdps: "+TheDarkness.towerManager.players.size());
		addStringToBuilder("darkness smoke render: "+TheDarkness.towerManager.getTDPlayer(player).doDarknessSmokeRender);
		addStringToBuilder("Light field render: "+TheDarkness.towerManager.getTDPlayer(player).doLightFieldRender);
		addStringToBuilder("Personal Light field render: "+TheDarkness.towerManager.getTDPlayer(player).doPersonalLightOrbFieldRender);
		addStringToBuilder("darkness wall render: "+TheDarkness.towerManager.getTDPlayer(player).doDarknessWallRender());
		
		System.out.println(sb);
		resetStringBuilder();
		return true;
	}
	
	public void addStringToBuilder(String string){
		sb.append(" "+string+" | ");
	}

	public void resetStringBuilder(){
		StringBuilder newSB = new StringBuilder(1000);
		sb = newSB;
	}
}
