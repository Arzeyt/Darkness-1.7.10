package com.arzeyt.darkness;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

/**
 * Created by Default on 11/5/2015.
 */
public class DummyGhostServer implements IMessageHandler<GhostMessageToClient, IMessage>{
    @Override
    public IMessage onMessage(final GhostMessageToClient message, MessageContext ctx) {
        return null;
    }
}
