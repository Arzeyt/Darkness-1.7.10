package com.arzeyt.theDarkness.proxies;

import java.io.IOException;

import com.arzeyt.theDarkness.TheDarkness;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent.ClientCustomPacketEvent;

//Remember client run configuration includes server side too
public class ClientPacketHandler extends ServerPacketHandler
{
	@SubscribeEvent
	public void onClientPacket(ClientCustomPacketEvent event) throws IOException 
	{
		channelName = event.packet.channel();

		if (channelName == TheDarkness.networkChannelName)
		{
			PacketTheDarkness.processPacketOnClientSide(event.packet.payload(), event.packet.getTarget());
		}
	}
}

