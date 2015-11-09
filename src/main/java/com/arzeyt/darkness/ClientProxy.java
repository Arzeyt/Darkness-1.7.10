package com.arzeyt.darkness;

import com.arzeyt.darkness.lightOrb.LightOrbModel;
import com.arzeyt.darkness.lightOrb.LightOrbItemRender;
import com.arzeyt.darkness.towerObject.TowerTESR;
import com.arzeyt.darkness.towerObject.TowerTileEntity;
import cpw.mods.fml.client.registry.ClientRegistry;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;

/**
 * Created by Default on 11/5/2015.
 */
public class ClientProxy extends CommonProxy {

    public static StatusBarRenderer statusBarRenderer;

    @Override
    public void preInit() {
        super.preInit();
    }

    @Override
    public void init() {
        super.init();

        ClientRegistry.bindTileEntitySpecialRenderer(TowerTileEntity.class, new TowerTESR());
        MinecraftForgeClient.registerItemRenderer(Darkness.lightOrbItem, new LightOrbItemRender(new LightOrbModel()));
    }

    @Override
    public void postInit() {
        super.postInit();
        statusBarRenderer = new StatusBarRenderer(Minecraft.getMinecraft());
        MinecraftForge.EVENT_BUS.register(new OverlayEventHandler(statusBarRenderer));
    }
}

