package com.arzeyt.theDarkness.proxies;

import java.awt.Color;
import java.awt.image.BufferedImage;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.glu.Sphere;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;

import com.arzeyt.theDarkness.RenderTickHandler;
import com.arzeyt.theDarkness.TheDarkness;
import com.arzeyt.theDarkness.lightOrb.ItemRenderLightOrb;
import com.arzeyt.theDarkness.lightOrb.RenderLightOrb;
import com.arzeyt.theDarkness.lightOrb.TileEntityLightOrb;
import com.arzeyt.theDarkness.tower.ItemRenderTower;
import com.arzeyt.theDarkness.tower.RenderTower;
import com.arzeyt.theDarkness.tower.TileEntityTower;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy {

	
	public void preInit(FMLPreInitializationEvent e) {
		super.preInit(e);
	}
	
	public void init(FMLInitializationEvent e) {
		TheDarkness.channel.register(new ClientPacketHandler());
		FMLCommonHandler.instance().bus().register(new RenderTickHandler());
		
		registerRenders();
		
		
		super.init(e);
	}
	
	public void postInit(FMLPostInitializationEvent e) {
		// TODO Auto-generated method stub
		super.postInit(e);
	}
	
	public void registerRenders(){
		TileEntitySpecialRenderer renderLightOrb = new RenderLightOrb();
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityLightOrb.class, renderLightOrb);
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(TheDarkness.lightOrbBlock), new ItemRenderLightOrb(renderLightOrb, new TileEntityLightOrb()));
		
		TileEntitySpecialRenderer renderTower = new RenderTower();
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTower.class, renderTower);
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(TheDarkness.towerBlock), new ItemRenderTower(renderTower, new TileEntityTower()));
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(TheDarkness.towerBlockEmpty), new ItemRenderTower(renderTower, new TileEntityTower()));
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(TheDarkness.towerBlockDark), new ItemRenderTower(renderTower, new TileEntityTower()));
		
		
		
	}
	
	
}
