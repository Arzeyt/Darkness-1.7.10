package com.arzeyt.darkness.towerObject;

import com.arzeyt.darkness.BlockPos;
import com.arzeyt.darkness.Darkness;


import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.world.World;

public class TowerMessageHandlerOnClient implements IMessageHandler<TowerMessageToClient, IMessage> {

	@Override
	public IMessage onMessage(final TowerMessageToClient message, MessageContext ctx) {
		//System.out.println("tower update message recieved");
		if(ctx.side!= Side.CLIENT){
			System.err.println("TowerMessageToClient sent to wrong side!");
			return null;
		}
		if(message.isMessageValid()==false){
			System.err.println("TowerMessageToClient is not valid");
			return null;
		}

		//don't need worldclient here
		 Minecraft minecraft = Minecraft.getMinecraft();
		    final WorldClient worldClient = minecraft.theWorld;
		    processMessage(message);


		    return null;
	}

	protected void processMessage(TowerMessageToClient message) {

		//System.out.println("getting entity at: "+message.getPos().getX()+" "+message.getPos().getY()+" "+message.getPos().getZ());
		int power = message.power();
		BlockPos pos = message.getPos();
		World world = Minecraft.getMinecraft().theWorld;
		/**
		if(world.getTileEntity(pos.getX(), pos.getY(), pos.getZ())!=null
				&& world.getTileEntity(pos.getX(), pos.getY(), pos.getZ()).isInvalid()==false){
			TowerTileEntity te = (TowerTileEntity) world.getTileEntity(pos.getX(), pos.getY(), pos.getZ());
			te.setPower(power);
			Darkness.clientLists.addPoweredTower(te);
			//System.out.println("Message processed. Set power to: "+power+" and the tile entity now has: "+te.getPower());
			//System.out.println("Tower is client list: "+ Darkness.clientLists.getPoweredTowers().size());
		}**/

		if(world.getTileEntity(pos.getX(), pos.getY(), pos.getZ())!=null
				&& world.getTileEntity(pos.getX(), pos.getY(), pos.getZ()).isInvalid()==false){//tile entity exists in world
			if(world.getTileEntity(pos.getX(), pos.getY(), pos.getZ()) instanceof TowerTileEntity){//tower exists in world
				TowerTileEntity te = (TowerTileEntity) world.getTileEntity(pos.getX(), pos.getY(), pos.getZ());
				te.setPower(power);
				Darkness.clientLists.addPoweredTower(te);
				Darkness.clientLists.removeFakeTowerAt(te.xCoord, te.yCoord, te.zCoord);
			}
		}else{//tile entity does not exist in world
			FakeTower fakeTower = Darkness.clientLists.getFakeTowerAt(new BlockPos(pos.getX(), pos.getY(), pos.getZ()));

			if(fakeTower==null) {//te doesn't exist in client list
				fakeTower = new FakeTower(pos.getX(), pos.getY(), pos.getZ(), power, world);
				//System.out.println("client fake tower created with power: "+power);
				Darkness.clientLists.addFakeTower(fakeTower);
			}else{//te exists in client list
				fakeTower.setPower(power);
				//System.out.println("client fake tower power updated to: "+power);
			}
		}
	}

}
