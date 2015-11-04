package com.arzeyt.darkness;

import com.arzeyt.darkness.lightOrb.DetonationMessageHandlerOnClient;
import com.arzeyt.darkness.lightOrb.DetonationMessageToClient;
import com.arzeyt.darkness.lightOrb.LightOrbItem;
import com.arzeyt.darkness.lightOrb.LightOrbBlock;
import com.arzeyt.darkness.lightOrb.OrbUpdateMessageHandlerOnClient;
import com.arzeyt.darkness.lightOrb.OrbUpdateMessageToClient;
import com.arzeyt.darkness.towerObject.*;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;


@Mod(modid = Darkness.MODID, version = Darkness.VERSION)
public class Darkness {
	public static final String MODID = "darkness";
    public static final String VERSION = "a1";
    
    //blocks
    public static Block effectBlock;
    public static Block towerBlock;
    public static Block lightOrbBlock;
    public static Block lightBlock;
    
    //items
    public static Item effectItem;
    public static Item lightOrb;
   
    
    //network 
    public static SimpleNetworkWrapper simpleNetworkWrapper;
    
    	//network variables
    	public static final byte ID_MESSAGE_CTOS = 10;
    	public static final byte EFFECTID_MESSAGE_STOC = 11;
    	public static final byte TOWER_MESSAGE_STOC = 12;
    	public static final byte DETONATION_MESSAGE_STOC = 13;
    	public static final byte ORB_UPDATE_MESSAGE_STOC=14;
    	public static final byte FX_MESSAGE_STOC=15;
		public static final byte PLAYER_MESSAGE_STOC=16;
        
    //other stuff
    public final static boolean debugMode=false;
    public static DarkLists darkLists;
    public static ClientLists clientLists;
    public static Reference reference;
    
    public static final DarknessTab darknessTab = new DarknessTab("tabDarkness");
    
    //render
    private static StatusBarRenderer statusBarRenderer;
    
    
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent e){


    	//blocks
    		towerBlock = new TowerBlock();
    		lightOrbBlock = new LightOrbBlock();
    		lightBlock = new LightBlock();
    	
    	//items
    		lightOrb = new LightOrbItem();
    		
    	//tile entities
    		GameRegistry.registerTileEntity(TowerTileEntity.class, "towerTileEntity");
    	
    	//network
	    	simpleNetworkWrapper = NetworkRegistry.INSTANCE.newSimpleChannel("DarknessChannel");
			//simpleNetworkWrapper.registerMessage(EffectMessageHandlerOnServer.class, EffectMessageToServer.class, ID_MESSAGE_CTOS, Side.SERVER);
	    	
	    	if(e.getSide()== Side.CLIENT){
	    		//simpleNetworkWrapper.registerMessage(EffectMessageHandlerOnClient.class, EffectMessageToClient.class, EFFECTID_MESSAGE_STOC, Side.CLIENT);
	    		simpleNetworkWrapper.registerMessage(TowerMessageHandlerOnClient.class, TowerMessageToClient.class, TOWER_MESSAGE_STOC, Side.CLIENT);
	    		simpleNetworkWrapper.registerMessage(DetonationMessageHandlerOnClient.class, DetonationMessageToClient.class, DETONATION_MESSAGE_STOC, Side.CLIENT);
	    		simpleNetworkWrapper.registerMessage(OrbUpdateMessageHandlerOnClient.class, OrbUpdateMessageToClient.class, ORB_UPDATE_MESSAGE_STOC, Side.CLIENT);
	    		simpleNetworkWrapper.registerMessage(FXMessageHandlerOnClient.class, FXMessageToClient.class, FX_MESSAGE_STOC, Side.CLIENT);
				simpleNetworkWrapper.registerMessage(GhostMessageHandlerOnClient.class, GhostMessageToClient.class, PLAYER_MESSAGE_STOC, Side.CLIENT);
	    	}
	    	
    	//classes
	    	darkLists=new DarkLists();

			if(e.getSide()==Side.CLIENT){
				clientLists=new ClientLists();
			}
		 	    	
    }
    
    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
		//Events
		FMLCommonHandler.instance().bus().register(new DarkEventHandlerFML());
		FMLCommonHandler.instance().bus().register(new MobSpawner());
		MinecraftForge.EVENT_BUS.register(new DarkEventHandlerMinecraftForge());
    	
    	if(event.getSide() == Side.CLIENT){ //client side stuff. screw proxies.
			FMLCommonHandler.instance().bus().register(new ClientEffectTick());
			MinecraftForge.EVENT_BUS.register(new ClientEffectEventHandler());

			//tesr
			ClientRegistry.bindTileEntitySpecialRenderer(TowerTileEntity.class, new TowerTESR());

			//items

		}
    	

    }
    
    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
    	//overlay
    	if(event.getSide()== Side.CLIENT){
    		statusBarRenderer = new StatusBarRenderer(Minecraft.getMinecraft());
    		MinecraftForge.EVENT_BUS.register(new OverlayEventHandler(statusBarRenderer));
    	}
    }
    
    @Mod.EventHandler
    public void serverStop(FMLServerStoppingEvent e){

		darkLists.clearTowerList();
		if(e.getSide()==Side.CLIENT){
			clientLists.clearTowerList();
		}
    }
}


   
