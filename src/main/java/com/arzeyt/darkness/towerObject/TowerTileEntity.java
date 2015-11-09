package com.arzeyt.darkness.towerObject;

import java.util.Random;

import com.arzeyt.darkness.BlockPos;
import com.arzeyt.darkness.Darkness;
import com.arzeyt.darkness.EffectHelper;
import com.arzeyt.darkness.Reference;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class TowerTileEntity extends TileEntity {

	//sync variables
	public final int SYNC_DISTANCE = 50;
	private int counter = 0;
	private int syncRate = 20*3;
	private int nearbyPlayerSyncRate = 20*5;
	private int particleProductionRate = 2;
	private int borderConstructRate = 3;
	private final int TAKE_ORB_COOLDOWN=200; 

	/**
	 * on every variable change, make sure to increment this value.
	 */
	private int syncState = 0;
	private int syncStateOld = 0;
	
	//variables
	private boolean powered = false;
	private boolean loaded = false;
	private int power = 0;
	private long worldTime =0;
	private boolean takingOrbAtNoon=false;
	private boolean doBorderEffect=false;
	private boolean skyClear=true;
	private int token = 0;
	private BlockPos magicBlockPos;

	Reference r = new Reference();
	
	public void TowerTileEntity(){
	}

	@Override
	public boolean canUpdate() {
		return true;
	}

	@Override
	public void updateEntity() {

		if(worldObj.isRemote==false){ //serverside
			//sync data
			if(counter%syncRate==0){
				//System.out.println("update tower");
				//updates all clients any time the sync state doesn't equal the old sync state
				if(syncState!=syncStateOld && worldObj.isRemote==false){
					updateClient();
					syncState++;
					syncStateOld=syncState;
					//System.out.println("syncing to match syncstate");
				}
			}
			//if there is a player within sync distance, always sync according to the sync rate
			if(counter%nearbyPlayerSyncRate==0
					&& worldObj.getClosestPlayer(xCoord, yCoord, zCoord, SYNC_DISTANCE)!=null
					&& power>99){
				
				//System.out.println("player is nearby");
				updateClient();
			}
			
			//for initial loading
			if(loaded==false){
				//System.out.println("loading tower");
				if(isPowered() && Darkness.darkLists.getPoweredTowers().contains(this)==false){
					Darkness.darkLists.addPoweredTowers(this);
					loaded=true;
				}else{
					loaded=true;
				}
				updateClient();
			}

			//orbPower time cycle, limited by sync rate. Also sets server time
			worldTime =worldObj.getWorldTime();
			long timeOfDay = worldTime > 24000 ? worldTime %24000 : worldTime;
			
			if(counter% Reference.TOWER_DEPLETION_RATE ==0
					&& timeOfDay < Reference.TOWER_DEPLETE_END_TIME
					&& timeOfDay> Reference.TOWER_DEPLETE_START_TIME
					&& power>=1){
				//System.out.println("decrementing power");
					setPower(getPower()-1);
			}
			if(counter% Reference.TOWER_CHARGE_RATE ==0
					&& skyClear==true
					&& timeOfDay< Reference.TOWER_CHARGE_END_TIME
					&& Reference.TOWER_CHARGE_START_TIME <timeOfDay
					&& power<100){
				//System.out.println("incrementing power");
				setPower(getPower()+1);
				//System.out.println("power = "+getPower());
			}

			//sky clear check
			if(counter%Reference.TOWER_SKY_CLEAR_CHECK_RATE==0){
				if(isSkyClear()){
					this.skyClear=true;
				}else{
					this.skyClear=false;
				}
			}
		}
		if(worldObj.isRemote==true){//clientside
			//effect
			double adjustedParticleProductionRate = getPower() > 0 ? particleProductionRate*100/getPower() : particleProductionRate*100;
			if(getPower()>0 && counter%adjustedParticleProductionRate==0){
				Random rand = new Random();
				this.getWorldObj().spawnParticle("fireworksSpark", xCoord+.5, yCoord+2, zCoord+.5, -0.5D+rand.nextDouble(), 1.0D, -0.5D+rand.nextDouble());

			}
			/**
			if(getPower()>0){
				Reference r = Darkness.reference;
				double adjustedTowerRadius = (double)r.TOWER_RADIUS/100*(double)getPower();
				if(adjustedTowerRadius<1){
					adjustedTowerRadius=1;
				}
				Random rand = new Random();
				double randX = rand.nextInt((int) (adjustedTowerRadius*2))-adjustedTowerRadius;
				double randY = rand.nextInt((int) (adjustedTowerRadius*2))-adjustedTowerRadius;
				double randZ = rand.nextInt((int) (adjustedTowerRadius*2))-adjustedTowerRadius;
				this.getWorld().spawnParticle(EnumParticleTypes.FIREWORKS_SPARK, xCoord+.5+randX, rand.nextInt(256), zCoord+randZ+.5, 0.0D, 0.1D, 0.0D);
			}**/


			//border effect
			/**
			if(doBorderEffect==false
					&&power>0){
				doBorderEffect=true;
				borderEffectRender();
				//System.out.println("border effect on");
			}else if(doBorderEffect==true
					&&power<=0){
				doBorderEffect=false;
				borderEffectOff();
				//System.out.println("border effect off");
			}else if(doBorderEffect==true
					&&counter%borderConstructRate==0){
				borderEffectRender();
			}
			**/
		}
		
		
		//increment counters
		counter++;
		if(counter>Integer.MAX_VALUE-20){ //just so we dont get huge numbers
			counter=0;
		}
	}

	Random rand = new Random();
	private void magicLight() {
		if(token>100 || counter<200){
			return;
		}
		if(magicBlockPos.getX()==0){
			magicBlockPos = new BlockPos(xCoord,yCoord,zCoord);
		}
		int i = rand.nextInt(4);
		int x= magicBlockPos.getX();
		int y= magicBlockPos.getY();
		int z= magicBlockPos.getZ();
		switch (i) {
		case 0:
			x=x+1;
			break;
		case 1: 
			x=x-1;
			break;
		case 2:
			z=z+1;
			break;
		case 3:
			z=z-1;
		default:
			break;
		}
		//System.out.println("magic block: " + magicBlockPos.toString());
		magicBlockPos = EffectHelper.findGroundY(worldObj, new BlockPos(x,y+1,z));
		try{
			worldObj.setBlock(magicBlockPos.getX(), magicBlockPos.getY(), magicBlockPos.getZ(), Blocks.bedrock);
		}catch(Exception e){
			//System.out.println("exception, brah");
		}
		token++;
		//System.out.println("token: "+token);
	}

	private void updateClient() {
		if(worldObj.isRemote==false){
			//System.out.println("update tower at: "+this.xCoord+" "+this.yCoord+" "+this.zCoord+" dim: "+this.worldObj.provider.dimensionId);
			Darkness.simpleNetworkWrapper.sendToDimension(new TowerMessageToClient(getPower(),xCoord, yCoord, zCoord), this.worldObj.provider.dimensionId);
		}
	}
	
	
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		NBTTagCompound nbt = (NBTTagCompound) compound.getTag("darkness");
		this.powered = nbt.getBoolean("powered");
		this.power=(nbt.getInteger("orbPower"));
		//System.out.println("nbt tag = "+compound.toString());
	}
	
	
	@Override
	public void writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		generateCompound(compound);

		//System.out.println("nbt tag = "+compound.toString());
	}
	
	public NBTTagCompound generateCompound(NBTTagCompound compound){
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setBoolean("powered", powered);
		nbt.setInteger("orbPower", getPower());
		
		compound.setTag("darkness", nbt);
		//System.out.println("compound tag generated is: "+compound.toString());
		return compound;
	}
	
	public int getPower(){
		return this.power;
	}
	
	/**
	 * 
	 * @param power Values between 0 to 100 ONLY
	 * @Description sets tower power and adjusts all variables accordingly. 
	 */
	public void setPower(int power){
		this.power=power;
		if(power>0 && this.powered==false){
			this.powered=true;
			if(this.worldObj.isRemote==false
					&& Darkness.darkLists.getPoweredTowers().isEmpty() 
					|| Darkness.darkLists.towerExists(this)==false){
				Darkness.darkLists.addPoweredTowers(this);
			}
		}else if(power<=0 && this.powered==true){
			this.powered=false;
			if(this.worldObj.isRemote==false
					&& Darkness.darkLists.getPoweredTowers().isEmpty()==false 
					&& Darkness.darkLists.towerExists(this)==true){
				Darkness.darkLists.removePoweredTower(this);
			}
		}
		//System.out.println("set power to: "+power+"  powered= "+this.powered);
		syncState++;
	}
	
	public void orbAbove(boolean present){
		if(present){
			if(worldObj.getBlock(xCoord, yCoord+2, zCoord) instanceof BlockAir){
				worldObj.setBlock(xCoord, yCoord + 2, zCoord, Darkness.lightOrbBlock);
			}
		}else{
			worldObj.setBlock(xCoord, yCoord+2, zCoord, Blocks.air);
		}
	}
	/**
	 * 
	 * @param p player to give the orb to
	 * @return true if worked, false if didn't
	 */
	public boolean takeOrb(EntityPlayer p){
		/**
		if(noonLowerEnd<worldTime && worldTime<noonHigherEnd){
			if(takingOrbAtNoon==false){
				this.takingOrbAtNoon=true;//activate taking orb. automatically deactivated in update()
				this.takeOrbAtNoonCooldownCounter=TAKE_ORB_COOLDOWN;//reset the cooldown
				
				ItemStack newOrb = generateLightOrb(p);
				p.inventory.addItemStackToInventory(newOrb);
				setPower(0);
				//System.out.println("took orb");
				return true;
			}else{
				//System.out.println("wait for cooldown in: "+takeOrbAtNoonCooldownCounter+" ticks");
				return false;
			}
		}**/
		if(getPower()<=1){
			//System.out.println("power too low to take orb");
			return false;
		}else{
			ItemStack newOrb = generateLightOrb(p);
			p.inventory.addItemStackToInventory(newOrb);
			setPower(1);
			//System.out.println("took orb");
			return true;
		}
	}
	
	/**
	 * @Warning Does not handle decrementing tower power
	 * @return An itemStack of 1 light orb with appropriate nbt data. Also adds light orb to light orb list
	 */
	public ItemStack generateLightOrb(EntityPlayer p){
		Random rand = new Random();
		ItemStack lightOrb = new ItemStack(Darkness.lightOrbItem);
		NBTTagCompound compound = new NBTTagCompound();
		NBTTagCompound nbt = new NBTTagCompound();
			nbt.setInteger(Reference.POWER, this.getPower());
			nbt.setInteger(Reference.INITAL_POWER, this.getPower());
			nbt.setInteger(Reference.ID, rand.nextInt(Integer.MAX_VALUE));
		compound.setTag("darkness", nbt);
		lightOrb.setTagCompound(compound);
		Darkness.darkLists.addLightOrb(lightOrb);
		return lightOrb;
	}
	
	public boolean isPowered(){

		return powered;
	}
	
	private void borderEffectRender(){
		int towerRadius=Reference.TOWER_RADIUS;

		int y = (int) (Minecraft.getMinecraft().thePlayer.posY-2);

		createXLineSparkles(worldObj, xCoord-towerRadius, xCoord+towerRadius, y, zCoord-towerRadius,  7);
		createXLineSparkles(worldObj, xCoord-towerRadius, xCoord+towerRadius, y, zCoord+towerRadius,  7);
		createZLineSparkles(worldObj, xCoord+towerRadius, y, zCoord-towerRadius, zCoord+towerRadius,  7);
		createZLineSparkles(worldObj, xCoord-towerRadius, y, zCoord-towerRadius, zCoord+towerRadius,  7);
		
		y=y+2;
		createXLineSparkles(worldObj, xCoord-towerRadius, xCoord+towerRadius, y, zCoord-towerRadius,  7);
		createXLineSparkles(worldObj, xCoord-towerRadius, xCoord+towerRadius, y, zCoord+towerRadius,  7);
		createZLineSparkles(worldObj, xCoord+towerRadius, y, zCoord-towerRadius, zCoord+towerRadius,  7);
		createZLineSparkles(worldObj, xCoord-towerRadius, y, zCoord-towerRadius, zCoord+towerRadius,  7);
	}
	
	private void createXLineSparkles(World w, int xstart, int xend, int y, int z, int density){
		Random rand = new Random();
		for(int x=xstart; x<xend; x=x+rand.nextInt(density)){
			BlockPos pos = new BlockPos(x,y,z);
			if(rand.nextFloat()<=density){
				w.spawnParticle("fireworksSpark", xCoord, yCoord, zCoord, 0.0D, 1.0D, 0.0D);
			}
		}
	}
	
	private void createZLineSparkles(World w, int x, int y, int zstart, int zend, int density){
		Random rand = new Random();
		for(int z=zstart; z<zend; z=z+rand.nextInt(density)){
			BlockPos pos = new BlockPos(x,y,z);
			if(rand.nextFloat()<=density){
					w.spawnParticle("fireworksSpark", xCoord, yCoord, zCoord, 0.0D, 1.0D, 0.0D);
			}
		}
	}
	
	private void createXLine(World w, int xstart, int xend, int y, int z, Block block, int density){
		Random rand = new Random();
		for(int x=xstart; x<xend; x=x+rand.nextInt(density)){
			BlockPos pos = new BlockPos(x,y,z);
			if(worldObj.getBlock(xCoord, yCoord, zCoord) instanceof BlockAir
					&& rand.nextFloat()<=density){
				w.setBlock(xCoord, yCoord, zCoord, block);
			}
		}
	}
	
	private void createZLine(World w, int x, int y, int zstart, int zend, Block block, int density){
		Random rand = new Random();
		for(int z=zstart; z<zend; z=z+rand.nextInt(density)){
			BlockPos pos = new BlockPos(x,y,z);
			if(worldObj.getBlock(xCoord, yCoord, zCoord) instanceof BlockAir
					&& rand.nextFloat()<=density){
					w.setBlock(xCoord, yCoord, zCoord, block);
			}
		}
	}
	private void borderEffectOff(){
		BlockPos minPos = new BlockPos(this.xCoord-Reference.TOWER_RADIUS, 0,zCoord-Reference.TOWER_RADIUS);
		BlockPos maxPos = new BlockPos(this.xCoord+Reference.TOWER_RADIUS, 256, this.zCoord+Reference.TOWER_RADIUS);
		worldObj.markBlockRangeForRenderUpdate(minPos.getX(), minPos.getY(), minPos.getZ(), maxPos.getX(), maxPos.getY(), maxPos.getZ());
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof TowerTileEntity){
			TowerTileEntity t = (TowerTileEntity) obj;
			if(t.xCoord==this.xCoord
					&&t.yCoord==this.yCoord
					&&t.zCoord==this.zCoord){
				return true;
			}
		}
		return false;
	}

	public boolean isSkyClear(){
		for(int y = yCoord+2; y<=256; y++){
			if(worldObj.getBlock(xCoord,y,zCoord)instanceof BlockAir==false){
				return false;
			}
		}
		return true;
	}
	
}
