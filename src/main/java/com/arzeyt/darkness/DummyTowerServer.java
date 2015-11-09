package com.arzeyt.darkness;

import com.arzeyt.darkness.towerObject.TowerMessageToClient;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

/**
 * Created by Default on 11/5/2015.
 */
public class DummyTowerServer implements IMessageHandler<TowerMessageToClient, IMessage>{
    @Override
    public IMessage onMessage(final TowerMessageToClient message, MessageContext ctx) {
        return null;
    }
}
