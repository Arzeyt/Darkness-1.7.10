package com.arzeyt.theDarkness;

import com.arzeyt.theDarkness.lightOrb.BlockLightOrb;
import com.arzeyt.theDarkness.lightOrb.BlockLighting;
import com.arzeyt.theDarkness.lightOrb.TileEntityLightOrb;
import com.arzeyt.theDarkness.lightOrb.TileEntityLighting;
import com.arzeyt.theDarkness.proxies.CommonProxy;
import com.arzeyt.theDarkness.tower.BlockTower;
import com.arzeyt.theDarkness.tower.BlockTowerDark;
import com.arzeyt.theDarkness.tower.BlockTowerEmpty;
import com.arzeyt.theDarkness.tower.TileEntityTower;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.FMLEventChannel;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(modid = TheDarkness.MODID, version = TheDarkness.VERSION)
public class TheDarkness {

	public static final String MODID = "thedarkness";
	public static final String VERSION = "0.1";
	
	public static Block towerBlockEmpty;
	public static Block towerBlock;
	public static Block towerBlockDark;
	public static Block lightingBlock;
	public static Block lightOrbBlock;
	
	public static Item lightOrbItem;
	

	
	public static TDWorldGenerator worldGen = new TDWorldGenerator();
	
	public static final String networkChannelName = "TheDarkness";
	public static FMLEventChannel channel;
	
	@SidedProxy(clientSide="com.arzeyt.theDarkness.proxies.ClientProxy", serverSide="com.arzeyt.theDarkness.proxies.CommonProxy")
    public static CommonProxy proxy;
	
	public static TowerManager towerManager;
	public static TDRenderManager renderManager;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event){
		//blocks
		towerBlock = new BlockTower();
		GameRegistry.registerBlock(towerBlock, "towerBlock");
		
		towerBlockEmpty = new BlockTowerEmpty();
		GameRegistry.registerBlock(towerBlockEmpty, "towerBlockEmpty");
		
		towerBlockDark = new BlockTowerDark();
		GameRegistry.registerBlock(towerBlockDark, "towerBlockDark");
		
		lightOrbBlock = new BlockLightOrb();
		GameRegistry.registerBlock(lightOrbBlock, "lightOrbBlock");
		
		lightingBlock = new BlockLighting();
		GameRegistry.registerBlock(lightingBlock, "lightBlock");
		
		//tile entities
		GameRegistry.registerTileEntity(TileEntityTower.class, "towerTileEntity");
		GameRegistry.registerTileEntity(TileEntityLightOrb.class, "lightOrbTileEntity");
		GameRegistry.registerTileEntity(TileEntityLighting.class, "lightingTileEntity");
		
		//items
		lightOrbItem = new ItemLightOrb();
		GameRegistry.registerItem(lightOrbItem, "lightOrbItem");
		
		
		//worldGen
		GameRegistry.registerWorldGenerator(worldGen, 1);
		
		//events
		TDEventHandler events = new TDEventHandler();
		FMLCommonHandler.instance().bus().register(events);
		MinecraftForge.EVENT_BUS.register(events);
		
		FMLCommonHandler.instance().bus().register(new PlayerTickHandler());
		
		//network registry
		TheDarkness.channel = NetworkRegistry.INSTANCE.newEventDrivenChannel(TheDarkness.networkChannelName);
		proxy.preInit(event);
		
		//custom classes
		towerManager=new TowerManager();
		renderManager=new TDRenderManager();
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event){
		proxy.init(event);
		
	}
	
	public void debug(String message){
		System.out.println(message);
	}
}
