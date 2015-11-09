package com.arzeyt.darkness;

import com.arzeyt.darkness.lightOrb.DetonationMessageHandlerOnClient;
import com.arzeyt.darkness.lightOrb.DetonationMessageToClient;
import com.arzeyt.darkness.lightOrb.LightOrbItem;
import com.arzeyt.darkness.lightOrb.LightOrbBlock;
import com.arzeyt.darkness.lightOrb.OrbUpdateMessageHandlerOnClient;
import com.arzeyt.darkness.lightOrb.OrbUpdateMessageToClient;
import com.arzeyt.darkness.towerObject.*;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;


@Mod(modid = Darkness.MODID, version = Darkness.VERSION)
public class Darkness {

	@Instance
	public static Darkness instance;

	@SidedProxy(clientSide = "com.arzeyt.darkness.ClientProxy",serverSide = "com.arzeyt.darkness.CommonProxy")
	public static CommonProxy proxy;

	public static final String MODID = "darkness";
    public static final String VERSION = "a1";
    
    //blocks
    public static Block effectBlock;
    public static Block towerBlock;
    public static Block lightOrbBlock;
    public static Block lightBlock;
    
    //items
    public static Item effectItem;
    public static Item lightOrbItem;
   
    
    //network 
    public static SimpleNetworkWrapper simpleNetworkWrapper;
    
    	//network variables
    	public static final byte ID_MESSAGE_CTOS = 10;
    	public static final byte EFFECTID_MESSAGE_STOC = 11;
    	public static final byte TOWER_MESSAGE_STOC = 12;
    	public static final byte DETONATION_MESSAGE_STOC = 13;
    	public static final byte ORB_UPDATE_MESSAGE_STOC=14;
    	public static final byte FX_MESSAGE_STOC=15;
		public static final byte GHOST_MESSAGE_STOC =16;
	
        
    //other stuff
    public final static boolean debugMode=false;
    public static DarkLists darkLists;
    public static ClientLists clientLists;
    public static Reference reference;


	public static final DarknessTab darknessTab = new DarknessTab("tabDarkness");

    
    
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent e){

		proxy.preInit();

    	//blocks
    		towerBlock = new TowerBlock();
    		lightOrbBlock = new LightOrbBlock();
    		lightBlock = new LightBlock();
    	
    	//items
    		lightOrbItem = new LightOrbItem();
    		
    	//tile entities
    		GameRegistry.registerTileEntity(TowerTileEntity.class, "towerTileEntity");
    	
    	//network
	    	simpleNetworkWrapper = NetworkRegistry.INSTANCE.newSimpleChannel("DarknessChannel");

			simpleNetworkWrapper.registerMessage(DummyTowerServer.class, TowerMessageToClient.class, TOWER_MESSAGE_STOC, Side.SERVER);
			simpleNetworkWrapper.registerMessage(DummyDetonationServer.class, DetonationMessageToClient.class, DETONATION_MESSAGE_STOC, Side.SERVER);
			simpleNetworkWrapper.registerMessage(DummyOrbUpdateServer.class, OrbUpdateMessageToClient.class, ORB_UPDATE_MESSAGE_STOC, Side.SERVER);
			simpleNetworkWrapper.registerMessage(DummyFXServer.class, FXMessageToClient.class, FX_MESSAGE_STOC, Side.SERVER);
			simpleNetworkWrapper.registerMessage(DummyGhostServer.class, GhostMessageToClient.class, GHOST_MESSAGE_STOC, Side.SERVER);

	    	if(e.getSide()== Side.CLIENT){
	    		simpleNetworkWrapper.registerMessage(TowerMessageHandlerOnClient.class, TowerMessageToClient.class, TOWER_MESSAGE_STOC, Side.CLIENT);
	    		simpleNetworkWrapper.registerMessage(DetonationMessageHandlerOnClient.class, DetonationMessageToClient.class, DETONATION_MESSAGE_STOC, Side.CLIENT);
	    		simpleNetworkWrapper.registerMessage(OrbUpdateMessageHandlerOnClient.class, OrbUpdateMessageToClient.class, ORB_UPDATE_MESSAGE_STOC, Side.CLIENT);
	    		simpleNetworkWrapper.registerMessage(FXMessageHandlerOnClient.class, FXMessageToClient.class, FX_MESSAGE_STOC, Side.CLIENT);
				simpleNetworkWrapper.registerMessage(GhostMessageHandlerOnClient.class, GhostMessageToClient.class, GHOST_MESSAGE_STOC, Side.CLIENT);
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
		proxy.init();
		//Events
		FMLCommonHandler.instance().bus().register(new DarkEventHandlerFML());
		FMLCommonHandler.instance().bus().register(new MobSpawner());
		MinecraftForge.EVENT_BUS.register(new DarkEventHandlerMinecraftForge());
    	
    	if(event.getSide() == Side.CLIENT){ //client side stuff. screw proxies.
			//System.out.println("client side registering");
			FMLCommonHandler.instance().bus().register(new ClientEffectTick());
			MinecraftForge.EVENT_BUS.register(new ClientEffectEventHandler());


			//items

		}
    	

    }
    
    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
		proxy.postInit();
    	//overlay
    	if(event.getSide()== Side.CLIENT){

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


   
