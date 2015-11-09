package com.arzeyt.darkness;

import com.arzeyt.darkness.lightOrb.DetonationMessageToClient;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

/**
 * Created by Default on 11/5/2015.
 */
public class DummyDetonationServer implements IMessageHandler<DetonationMessageToClient, IMessage>{
    @Override
    public IMessage onMessage(final DetonationMessageToClient message, MessageContext ctx) {
        return null;
    }
}
