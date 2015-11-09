package com.arzeyt.darkness;

import java.util.HashSet;

import com.arzeyt.darkness.towerObject.FakeTower;
import com.arzeyt.darkness.towerObject.TowerTileEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import static com.arzeyt.darkness.Reference.TOWER_RADIUS;

public class ClientLists {

	//accessed every tick to see where to render detonations. The server should modify this list via packets.
	private HashSet<BlockPos> detonations = new HashSet<BlockPos>();
	private HashSet<TowerTileEntity> poweredTowers = new HashSet<TowerTileEntity>();
	private HashSet<FakeTower> fakePoweredTowers = new HashSet<FakeTower>();
	private boolean isGhost = false;

	public HashSet<BlockPos> getDetonations() {

		return detonations;
	}
	public void addDetonation(BlockPos pos){

		detonations.add(pos);
	}
	public void removeDetonation(BlockPos pos) {

		detonations.remove(pos);
	}
	public HashSet<TowerTileEntity> getPoweredTowers() {

		return poweredTowers;
	}

	public void addPoweredTower(TowerTileEntity t){
		if(towerExists(t)==false){
			poweredTowers.add(t);
			//System.out.println("added powered tower");
		}
		//System.out.println("tried to add powered tower");
	}

	/**
	 *
	 * @param t
	 * @return true if positions match
	 */
	public boolean towerExists(TowerTileEntity t){
		for(TowerTileEntity tow : getPoweredTowers()){
			if(tow.xCoord==t.xCoord
					&&tow.yCoord==t.yCoord
					&&tow.zCoord==t.zCoord){
				return true;
			}
		}
		return false;
	}

	public void removePoweredTower(TowerTileEntity t){
		if(getPoweredTowers().contains(t)){
			poweredTowers.remove(t);
			//System.out.println("removed powered tower");
		}
		//System.out.println("tried to remove powered tower");
	}

	public TowerTileEntity getTowerAt(int x, int y, int z){
		for(TowerTileEntity t : poweredTowers){
			if(t.xCoord==x
					&&t.yCoord==y
					&&t.zCoord==z){
				return t;
			}
		}
		return null;
	}

	public FakeTower getFakeTowerAt(BlockPos pos){
		for(FakeTower t : fakePoweredTowers) {
			if(t.getX()==pos.getX()
				&&t.getY()==pos.getY()
				&&t.getZ()==pos.getZ()){
				return t;
			}
		}
		return null;
	}

	public void addFakeTower(FakeTower fakeTower){
		BlockPos pos = new BlockPos(fakeTower.getX(), fakeTower.getY(), fakeTower.getZ());
		if(getFakeTowerAt(pos)==null){
			fakePoweredTowers.add(fakeTower);
		}
	}

	public void removeFakeTowerAt(int x, int y, int z){
		FakeTower tower = getFakeTowerAt(new BlockPos(x,y,z));
		if(tower!=null){
			fakePoweredTowers.remove(tower);
		}
	}

	public int getDistanceToNearestTower(EntityPlayer p){
		//System.out.println("towers: "+getPoweredTowers().size());
		BlockPos pos = new BlockPos(p.posX,p.posY,p.posZ);
		return getDistanceToNearestTower(p.worldObj.provider.dimensionId, pos);

	}

	/**
	 *
	 * @param dimID
	 * @param pos
	 * @return a realistic distance value. Change to manhattan distance for performances.
	 */
	public int getDistanceToNearestTower(int dimID, BlockPos pos){
		int distance = Integer.MAX_VALUE;
		for(TowerTileEntity t : getPoweredTowers()){
			if(dimID==t.getWorldObj().provider.dimensionId){
				BlockPos tpos = new BlockPos(t.xCoord,t.yCoord,t.zCoord);
				int dis = (int) Math.hypot(pos.getX() - tpos.getX(), pos.getZ() - tpos.getZ());
				distance = dis<distance ? dis : distance;
			}
		}
		for(FakeTower t : fakePoweredTowers){
			if(dimID==t.getWorld().provider.dimensionId){
				BlockPos tpos = new BlockPos(t.getX(),t.getY(),t.getZ());
				int dis = (int) Math.hypot(pos.getX() - tpos.getX(), pos.getZ() - tpos.getZ());
				distance = dis<distance ? dis : distance;
			}
		}
		//System.out.println("distance is: "+distance);
		return distance;
	}

	public double getDistanceToNearestTowerDouble(int dimID, BlockPos pos){
		double distance = Integer.MAX_VALUE;
		for(TowerTileEntity t : getPoweredTowers()){
			if(dimID==t.getWorldObj().provider.dimensionId){
				BlockPos tpos = new BlockPos(t.xCoord,t.yCoord,t.zCoord);
				double dis =  Math.hypot(pos.getX()-tpos.getX(), pos.getZ()-tpos.getZ());
				distance = (int) (dis<distance ? dis : distance);
			}
		}
		//System.out.println("distance is: "+distance);
		return distance;
	}


	public boolean isPosInTowerRadius(World w, BlockPos pos){
		for(TowerTileEntity t : getPoweredTowers()){
			if(w.provider.dimensionId==t.getWorldObj().provider.dimensionId) {
				int xmax = t.xCoord + TOWER_RADIUS;
				int xmin = t.xCoord - TOWER_RADIUS;

				int zmax = t.zCoord + TOWER_RADIUS;
				int zmin = t.zCoord - TOWER_RADIUS;

				int px = pos.getX();
				int pz = pos.getZ();

				if (xmin < px && px < xmax) {
					if (zmin < pz && pz < zmax) {
						return true;
					}
				}
			}
		}
		for(FakeTower t : fakePoweredTowers){
			if(w.provider.dimensionId==t.getWorld().provider.dimensionId) {
				int xmax = t.getX() + TOWER_RADIUS;
				int xmin = t.getX() - TOWER_RADIUS;

				int zmax = t.getZ() + TOWER_RADIUS;
				int zmin = t.getZ() - TOWER_RADIUS;

				int px = pos.getX();
				int pz = pos.getZ();

				if (xmin < px && px < xmax) {
					if (zmin < pz && pz < zmax) {
						return true;
					}
				}
			}
		}


		return false;

	}

	public boolean isPosInTowerRadiusPlus1(World w, BlockPos pos){
		for(TowerTileEntity t : getPoweredTowers()){
			if(w.provider.dimensionId==t.getWorldObj().provider.dimensionId) {
				int xmax = t.xCoord + TOWER_RADIUS+1;
				int xmin = t.xCoord - TOWER_RADIUS-1;

				int zmax = t.zCoord + TOWER_RADIUS+1;
				int zmin = t.zCoord - TOWER_RADIUS-1;

				int px = pos.getX();
				int pz = pos.getZ();

				if (xmin < px && px < xmax) {
					if (zmin < pz && pz < zmax) {
						return true;
					}
				}
			}
		}

		for(FakeTower t : fakePoweredTowers){
			if(w.provider.dimensionId==t.getWorld().provider.dimensionId) {
				int xmax = t.getX() + TOWER_RADIUS+1;
				int xmin = t.getX() - TOWER_RADIUS-1;

				int zmax = t.getZ() + TOWER_RADIUS+1;
				int zmin = t.getZ() - TOWER_RADIUS-1;

				int px = pos.getX();
				int pz = pos.getZ();

				if (xmin < px && px < xmax) {
					if (zmin < pz && pz < zmax) {
						return true;
					}
				}
			}
		}

		return false;

	}


	public boolean isPosInTowerRadiusX2minus1(World w, BlockPos pos){
		for(TowerTileEntity t : getPoweredTowers()){
			if(w.provider.dimensionId==t.getWorldObj().provider.dimensionId) {
				int xmax = t.xCoord + (TOWER_RADIUS*2-1);
				int xmin = t.xCoord - (TOWER_RADIUS*2-1);

				int zmax = t.zCoord + (TOWER_RADIUS*2-1);
				int zmin = t.zCoord - (TOWER_RADIUS*2-1);

				int px = pos.getX();
				int pz = pos.getZ();

				if (xmin < px && px < xmax) {
					if (zmin < pz && pz < zmax) {
						return true;
					}
				}
			}
		}

		for(FakeTower t : fakePoweredTowers){
			if(w.provider.dimensionId==t.getWorld().provider.dimensionId) {
				int xmax = t.getX() + (TOWER_RADIUS*2-1);
				int xmin = t.getX() - (TOWER_RADIUS*2-1);

				int zmax = t.getZ() + (TOWER_RADIUS*2-1);
				int zmin = t.getZ() - (TOWER_RADIUS*2-1);

				int px = pos.getX();
				int pz = pos.getZ();

				if (xmin < px && px < xmax) {
					if (zmin < pz && pz < zmax) {
						return true;
					}
				}
			}
		}

		return false;

	}

	public boolean isPlayerInTowerRadius(EntityPlayer p){
		return isPosInTowerRadius(p.worldObj, new BlockPos(p.posX,p.posY,p.posZ));
	}

	public boolean isGhost(){
		return isGhost;
	}

	public void setGhost(boolean isGhost){
		this.isGhost=isGhost;
	}

	public void clearTowerList(){

		this.poweredTowers= new HashSet<TowerTileEntity>();
		this.fakePoweredTowers = new HashSet<FakeTower>();
	}

}
