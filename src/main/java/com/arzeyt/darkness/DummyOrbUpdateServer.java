package com.arzeyt.darkness;

import com.arzeyt.darkness.lightOrb.OrbUpdateMessageToClient;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

/**
 * Created by Default on 11/5/2015.
 */
public class DummyOrbUpdateServer implements IMessageHandler<OrbUpdateMessageToClient, IMessage>{
    @Override
    public IMessage onMessage(final OrbUpdateMessageToClient message, MessageContext ctx) {
        return null;
    }
}