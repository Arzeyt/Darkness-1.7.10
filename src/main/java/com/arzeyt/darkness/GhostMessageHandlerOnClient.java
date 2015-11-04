package com.arzeyt.darkness;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;

/**
 * Created by Default on 11/4/2015.
 */
public class GhostMessageHandlerOnClient implements IMessageHandler<GhostMessageToClient, IMessage>{
    @Override
    public IMessage onMessage(final GhostMessageToClient message, MessageContext ctx) {
        if(ctx.side!= Side.CLIENT){
            System.err.println("GhostMessageToClient sent to wrong side!");
            return null;
        }
        if(message.isMessageValid()==false){
            System.err.println("GhostMessageToClient is not valid");
            return null;
        }

        Minecraft minecraft = Minecraft.getMinecraft();
        final WorldClient worldClient = minecraft.theWorld;


        processMessage(worldClient, message);

        return null;
    }

    protected void processMessage(WorldClient worldClient,
                                  GhostMessageToClient message) {

        System.out.println("processing GhostMessageToClient client side");

        if(message.isGhost){
            System.out.println("client player is ghost");
        }else{
            System.out.println("Client player is not a ghost");
        }
        Darkness.clientLists.setGhost(message.isGhost);

    }

}
