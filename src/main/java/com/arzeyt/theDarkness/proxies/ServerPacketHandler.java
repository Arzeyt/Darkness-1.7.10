package com.arzeyt.theDarkness.proxies;

import java.io.IOException;

import com.arzeyt.theDarkness.TheDarkness;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent.ServerCustomPacketEvent;

public class ServerPacketHandler {

	protected String channelName;
	
	@SubscribeEvent
	public void onServerPacket(ServerCustomPacketEvent event) throws IOException 
	{
		channelName = event.packet.channel();

		if (channelName == TheDarkness.networkChannelName)
		{
			PacketTheDarkness.processPacketOnServerSide(event.packet.payload(), event.packet.getTarget());
		}
	}
}

