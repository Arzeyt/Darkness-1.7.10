package com.arzeyt.theDarkness.proxies;

import java.io.IOException;

import com.arzeyt.theDarkness.ExtendedPeaceful;
import com.arzeyt.theDarkness.RenderTickHandler;
import com.arzeyt.theDarkness.TDLocation;
import com.arzeyt.theDarkness.TheDarkness;
import com.arzeyt.theDarkness.TowerType;
import com.arzeyt.theDarkness.tower.TileEntityTower;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.server.FMLServerHandler;

public class PacketTheDarkness {

	public PacketTheDarkness(){
		
	}

	// define IDs for custom packet types
	public final static int packetTypeTower = 1;
	public final static int packetTypeRenderInLightEffects = 2;
	public final static int packetTypeRenderInDarknessEffects= 3;
	public final static int packetTypeDarknessSmokeOn= 4;
	public final static int packetTypeTowerSync = 5;
	public final static int packetTypeClientTowerSync = 6;
	public final static int packetTypeExtendedPeacefulSync = 7;
	public final static int packetTypeRenderLightFieldEffects = 8;
	public final static int packetTypePersonalLightFieldEffects = 9;
	


	public static FMLProxyPacket personalLightFieldEffectsPacket() throws IOException {
		System.out.println("Sending PacketTheDarkness personal light field effects server side");

		ByteBufOutputStream bbos = new ByteBufOutputStream(Unpooled.buffer());

		bbos.writeInt(packetTypePersonalLightFieldEffects);
		
		
		// put payload into a packet		
		FMLProxyPacket thePacket = new FMLProxyPacket(bbos.buffer(), TheDarkness.networkChannelName);

		bbos.close();
		return thePacket;
	}
	public static FMLProxyPacket lightFieldEffectsPacket() throws IOException {
		System.out.println("Sending PacketTheDarkness light field effects server side");

		ByteBufOutputStream bbos = new ByteBufOutputStream(Unpooled.buffer());

		bbos.writeInt(packetTypeRenderLightFieldEffects);
		
		
		// put payload into a packet		
		FMLProxyPacket thePacket = new FMLProxyPacket(bbos.buffer(), TheDarkness.networkChannelName);

		bbos.close();
		return thePacket;
	}
	/**
	 * does nothing right now
	 */
	public static FMLProxyPacket extendedPeacefulPacket(ExtendedPeaceful extendedPeaceful, EntityLiving entity) throws IOException {
		System.out.println("Sending PacketTheDarkness extended peaceful sync server side");

		ByteBufOutputStream bbos = new ByteBufOutputStream(Unpooled.buffer());

		bbos.writeInt(packetTypeExtendedPeacefulSync);
		
		bbos.writeInt(entity.getEntityId());
		
		// put payload into a packet		
		FMLProxyPacket thePacket = new FMLProxyPacket(bbos.buffer(), TheDarkness.networkChannelName);

		bbos.close();
		return thePacket;
	}
	
	public static FMLProxyPacket towerClientSyncPacket(TileEntityTower te) throws IOException{
			System.out.println("Sending PacketTheDarkness tower sync on Client Side");

			ByteBufOutputStream bbos = new ByteBufOutputStream(Unpooled.buffer());

			bbos.writeInt(packetTypeClientTowerSync);
			
			bbos.writeInt(te.xCoord);
			bbos.writeInt(te.yCoord);
			bbos.writeInt(te.zCoord);
			
			bbos.writeBoolean(te.renderTransitionToEmptyHandled);
			bbos.writeBoolean(te.renderTransitionToFullHandled);
			
			// put payload into a packet		
			FMLProxyPacket thePacket = new FMLProxyPacket(bbos.buffer(), TheDarkness.networkChannelName);

			bbos.close();
			return thePacket;
	}
	
	public static FMLProxyPacket towerSyncPacket(TileEntityTower te) throws IOException
	{
		// DEBUG
		System.out.println("Sending PacketTheDarkness renderTowerTransitionToLight on Server Side");

		ByteBufOutputStream bbos = new ByteBufOutputStream(Unpooled.buffer());

		bbos.writeInt(packetTypeTowerSync);
		
		bbos.writeInt(te.xCoord);
		bbos.writeInt(te.yCoord);
		bbos.writeInt(te.zCoord);
		
		bbos.writeBoolean(te.renderTransitionToEmpty);
		bbos.writeBoolean(te.renderTransitionToEmptyHandled);
		bbos.writeBoolean(te.renderTransitionToFull);
		bbos.writeBoolean(te.renderTransitionToFullHandled);
		
		bbos.writeInt(te.getTowerTypeFromEnum(te.towerType));
		
		// put payload into a packet		
		FMLProxyPacket thePacket = new FMLProxyPacket(bbos.buffer(), TheDarkness.networkChannelName);

		bbos.close();
		return thePacket;
	}
	
	public static FMLProxyPacket createDarknessSmokePacket(EntityPlayer player) throws IOException
	{
		// DEBUG
		System.out.println("Sending PacketTheDarkness darknessSmokePacket on Server Side");

		ByteBufOutputStream bbos = new ByteBufOutputStream(Unpooled.buffer());

		// create payload by writing to data stream
		// first identity packet type
		bbos.writeInt(packetTypeDarknessSmokeOn);
		
		// put payload into a packet		
		FMLProxyPacket thePacket = new FMLProxyPacket(bbos.buffer(), TheDarkness.networkChannelName);

		bbos.close();
		return thePacket;
	}
	
	public static FMLProxyPacket createRenderInDarknessEffectsPacket(EntityPlayer player) throws IOException
	{
		// DEBUG
		System.out.println("Sending PacketTheDarkness wallVisOff on Server Side");

		ByteBufOutputStream bbos = new ByteBufOutputStream(Unpooled.buffer());

		// create payload by writing to data stream
		// first identity packet type
		bbos.writeInt(packetTypeRenderInDarknessEffects);
		
		// put payload into a packet		
		FMLProxyPacket thePacket = new FMLProxyPacket(bbos.buffer(), TheDarkness.networkChannelName);

		bbos.close();
		return thePacket;
	}
	
	public static FMLProxyPacket createWallVisPacket(EntityPlayer player, TileEntityTower tower) throws IOException
	{
		// DEBUG
		System.out.println("Sending PacketTheDarkness WallVisPacket on Server Side");

		ByteBufOutputStream bbos = new ByteBufOutputStream(Unpooled.buffer());

		// create payload by writing to data stream
		// first identity packet type
		bbos.writeInt(packetTypeRenderInLightEffects);
		
		//xyz
		bbos.writeInt(tower.xCoord);
		bbos.writeInt(tower.yCoord);
		bbos.writeInt(tower.zCoord);

		// put payload into a packet		
		FMLProxyPacket thePacket = new FMLProxyPacket(bbos.buffer(), TheDarkness.networkChannelName);

		bbos.close();
		
		return thePacket;
	}
	
	public static FMLProxyPacket createTowerPacket(TileEntityTower tower) throws IOException
	{
		// DEBUG
		System.out.println("Sending PacketTheDarkness createTowerPacket on Server Side");

		ByteBufOutputStream bbos = new ByteBufOutputStream(Unpooled.buffer());

		// create payload by writing to data stream
		// first identity packet type
		bbos.writeInt(packetTypeTower);
		
		//orbs
		bbos.writeInt(tower.orbs);

		//xyz
		bbos.writeInt(tower.xCoord);
		bbos.writeInt(tower.yCoord);
		bbos.writeInt(tower.zCoord);

		// put payload into a packet		
		FMLProxyPacket thePacket = new FMLProxyPacket(bbos.buffer(), TheDarkness.networkChannelName);

		// don't forget to close stream to avoid memory leak
		bbos.close();
		
		return thePacket;
	}

	public static void processPacketOnClientSide(ByteBuf parBB, Side parSide) throws IOException
	{
		if (parSide == Side.CLIENT)
		{
			// DEBUG
			System.out.println("Received PacketTheDarkness on Client Side");

			World theWorld = Minecraft.getMinecraft().theWorld;
			ByteBufInputStream bbis = new ByteBufInputStream(parBB);
			
			// process data stream
			// first read packet type
			int packetTypeID = bbis.readInt();
			System.out.println("Packet id: "+packetTypeID);
			
			if(packetTypeID==packetTypeTower){
					// find entity instance
					int orbs = bbis.readInt();
					// DEBUG
					System.out.println("packetTypeTower");
					
					int x=bbis.readInt();
					int y=bbis.readInt();
					int z=bbis.readInt();
					
					TileEntityTower tower = new TileEntityTower();
					
					tower.orbs=orbs;
					tower.xCoord=x;
					tower.yCoord=y;
					tower.zCoord=z;
					
					System.out.println("creating tile entity isRemote: "+theWorld.isRemote);
					System.out.println("xCoord= "+x+" yCoord= "+y+" zCoord= "+z);
					theWorld.setTileEntity(x, y, z, tower);
				
			}else if(packetTypeID==packetTypeRenderInLightEffects){
					System.out.println("packet type in light effects");
					int xCoord = bbis.readInt();
					int yCoord = bbis.readInt();
					int zCoord = bbis.readInt();
					
					TheDarkness.renderManager.activateTowerRadiusBorderRender(new TDLocation(xCoord, yCoord, zCoord));
					TheDarkness.renderManager.shouldRenderDarknessSmoke=false;
					TheDarkness.renderManager.shouldRenderPersonalOrbSphere=false;
					
			}else if(packetTypeID==packetTypeRenderInDarknessEffects){
				System.out.println("Packet Type In darkness effects");
				
				TheDarkness.renderManager.deactivateTowerRadiusBorderRender();
				TheDarkness.renderManager.shouldRenderDarknessSmoke=true;
				TheDarkness.renderManager.shouldRenderPersonalOrbSphere=false;
			
			
			}else if(packetTypeID==packetTypeTowerSync){
				System.out.println("packetTypeTowerSynce");
				
				//read
				int x = bbis.readInt();
				int y = bbis.readInt();
				int z = bbis.readInt();
				
				boolean empty = bbis.readBoolean();
				boolean emptyHandled = bbis.readBoolean();
				boolean full = bbis.readBoolean();
				boolean fullHandled = bbis.readBoolean();
				
				int towerType = bbis.readInt();
				
				//assign variables to tile entity
				try{
					TileEntityTower te = (TileEntityTower) Minecraft.getMinecraft().theWorld.getTileEntity(x, y, z);
					
					te.renderTransitionToEmpty=empty;
					te.renderTransitionToEmptyHandled=emptyHandled;
					te.renderTransitionToFull=full;
					te.renderTransitionToFullHandled=fullHandled;
					
					te.animationOffset=System.currentTimeMillis();
					
					te.towerType=te.getTowerTypeFromInt(towerType);
					
					
				}catch(Exception e){
					System.out.println("problem handling tower sync packet");
				}
			}else if(packetTypeID==packetTypeRenderLightFieldEffects){
				System.out.println("packet type render in light field effects");
				
				TheDarkness.renderManager.shouldRenderDarknessSmoke=false;
				
			}else if(packetTypeID==packetTypePersonalLightFieldEffects){
				System.out.println("packet type personal light field effects");
				
				TheDarkness.renderManager.shouldRenderPersonalOrbSphere=true;
				TheDarkness.renderManager.shouldRenderDarknessSmoke=false;
			}
			
			
			// don't forget to close stream to avoid memory leak
			bbis.close();			
		}
	}

	public static void processPacketOnServerSide(ByteBuf payload, Side parSide) throws IOException
	{
		if (parSide == Side.SERVER)
		{
			// DEBUG
				System.out.println("Received PacketTheDarkness on Server Side");

				World theWorld = Minecraft.getMinecraft().theWorld;
				ByteBufInputStream bbis = new ByteBufInputStream(payload);
				
				// process data stream
				// first read packet type
				int packetTypeID = bbis.readInt();
				System.out.println("Packet id: "+packetTypeID);
				
				if(packetTypeID==packetTypeClientTowerSync){
					
					int x = bbis.readInt();
					int y = bbis.readInt();
					int z = bbis.readInt();
					
					boolean emptyHandled = bbis.readBoolean();
					boolean fullHandled = bbis.readBoolean();
					
					TileEntityTower te = (TileEntityTower) FMLServerHandler.instance().getServer().worldServers[0].getTileEntity(x, y, z);
					
					te.renderTransitionToEmptyHandled=emptyHandled;
					te.renderTransitionToFullHandled=fullHandled;
				}
			bbis.close();
		}
		
	}

}