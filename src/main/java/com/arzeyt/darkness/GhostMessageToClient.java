package com.arzeyt.darkness;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;
import net.minecraft.init.Blocks;

import java.util.HashSet;

/**
 * Created by Default on 11/4/2015.
 */
public class GhostMessageToClient implements IMessage{

    boolean isGhost;
    private boolean messageValid;

    public GhostMessageToClient(boolean isGhost){
        this.isGhost=isGhost;
        messageValid=true;
    }

    public GhostMessageToClient(){
        messageValid=false;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        try{
            isGhost=buf.readBoolean();

        }catch(IndexOutOfBoundsException e){
            System.err.println("GhostMessageToClient ioe "+e);
        }

        messageValid=true;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        if(!messageValid)return;
        buf.writeBoolean(isGhost);
    }

    public boolean isMessageValid() {
        return messageValid;
    }


}

