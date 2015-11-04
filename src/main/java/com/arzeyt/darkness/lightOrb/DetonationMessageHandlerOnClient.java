package com.arzeyt.darkness.lightOrb;

import com.arzeyt.darkness.BlockPos;
import com.arzeyt.darkness.Darkness;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;

public class DetonationMessageHandlerOnClient implements IMessageHandler<DetonationMessageToClient, IMessage> {

	@Override
	public IMessage onMessage(final DetonationMessageToClient message, MessageContext ctx) {
		if(ctx.side!= Side.CLIENT){
			System.err.println("DetonationMessageToClient sent to wrong side!");
			return null;
		}
		if(message.isMessageValid()==false){
			System.err.println("DetonationMessageToClient is not valid");
			return null;
		}
		
		 Minecraft minecraft = Minecraft.getMinecraft();
		    final WorldClient worldClient = minecraft.theWorld;
		    processMessage(worldClient, message);

		    return null;
	}

	protected void processMessage(WorldClient worldClient,
			DetonationMessageToClient message) {

		System.out.println("processing detonation client side");
		BlockPos pos = message.getPos();
		if(message.shouldDetonate()==true){
			Darkness.clientLists.addDetonation(pos);
		}else{
			Darkness.clientLists.removeDetonation(pos);
		}
		
	}

}
