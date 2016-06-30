package com.arzeyt.theDarkness.proxies;

import com.arzeyt.theDarkness.PlayerTickHandler;
import com.arzeyt.theDarkness.RenderTickHandler;
import com.arzeyt.theDarkness.TheDarkness;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.IGuiHandler;

public class CommonProxy implements IGuiHandler{

	public void preInit(FMLPreInitializationEvent e){
		/**
		// load configuration before doing anything else
	      processConfig();

	      // register stuff
	      registerBlocks();
	      registerItems();
	      registerTileEntities();
	      registerModEntities();
	      registerEntitySpawns();
	      registerFuelHandlers();
	     **/
	}
	
	public void init(FMLInitializationEvent e){
		TheDarkness.channel.register(new ServerPacketHandler()); 
		/**
		// register custom event listeners
        registerEventListeners();
 
        // register networking channel 
        registerNetworkingChannel();
        
        // register server packet handler
        registerServerPacketHandler();
        
        // register recipes here to allow use of items from other mods
       registerRecipes();
       **/
	}
	
	public void postInit(FMLPostInitializationEvent e){
		// can do some inter-mod stuff here
		
	}
	
	public void processConfig(){
		 // might need to use suggestedConfigFile (event.getSuggestedConfigFile) location to publish
	}

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world,
			int x, int y, int z) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world,
			int x, int y, int z) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
