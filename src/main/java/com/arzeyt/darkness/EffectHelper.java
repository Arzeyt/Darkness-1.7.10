package com.arzeyt.darkness;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;


import net.minecraft.block.BlockAir;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class EffectHelper {

	static Random rand = new Random();

	public static void teleportRandomly(EntityLiving e, int range){
		BlockPos pos = getRandomGroundPos(e.worldObj, new BlockPos(e.posX, e.posY, e.posZ), range);
		e.setPosition(pos.getX(), pos.getY(), pos.getZ());
		//System.out.println("teleport to: "+pos.toString());
		
	}
	
	/**
	 * 
	 * @param w world
	 * @param p start position
	 * @param range
	 * @return a random ground block in the range nearest the original y position
	 * @Warning can result in infinite loop if there are no ground blocks in range!
	 */
	public static BlockPos getRandomGroundPos(World w, BlockPos p, int range){
		int x = (rand.nextInt(range*2)-range)+p.getX();
		int y = p.getY();
		int z = (rand.nextInt(range*2)-range)+p.getZ();
		BlockPos mpos = new BlockPos(x,y,z);
		mpos=findGroundY(w, mpos);
		
		while(mpos==null){
			x = (rand.nextInt(range*2)-range)+p.getX();
			y = p.getY();
			z = (rand.nextInt(range*2)-range)+p.getZ();
			BlockPos pos = new BlockPos(x,y,z);
			mpos=findGroundY(w, pos);
		}
		return mpos;
	}
	/**
	 * 
	 * @param w
	 * @param p
	 * @return null if no ground can be found
	 */
	public static BlockPos findGroundY(World w, BlockPos p){
		HashSet<Integer> grounds = new HashSet<Integer>();
		for(int i = -128; i <128; i++){
			BlockPos pos=new BlockPos(p.getX(), i, p.getZ());
			if(w.getBlock(pos.getX(), pos.getY(), pos.getZ()) instanceof BlockAir ==false){
				BlockPos posUp1 = new BlockPos(pos.getX(), pos.getY()+1,pos.getZ());	
				if(w.getBlock(posUp1.getX(), posUp1.getY(), posUp1.getZ()) instanceof BlockAir){
					BlockPos posUp2 = new BlockPos(pos.getX(), posUp1.getY()+1, pos.getZ());
					if(w.getBlock(posUp2.getX(), posUp2.getY(), posUp2.getZ()) instanceof BlockAir){
						grounds.add(i);
					}
				}
			}
		}
	
		if(grounds.isEmpty()){
			return null;
		}
		
		int distance = 300;
		int closestY = 300;
		for(Integer ground : grounds){
			//calculate distances
			int dis = Math.abs(p.getY()-ground);
			if(dis<distance){
				distance=dis;
				closestY=ground;
			}
		}

		return new BlockPos(p.getX(), closestY+1, p.getZ());
	}
	
	public static List<Point2D> getPointsAlongLine(double x1, double z1, double x2, double z2){
		List<Point2D> points = new ArrayList<Point2D>();
		Line2D line = new Line2D.Double(0, 0, 8, 4);
		Point2D current;

		for (Iterator<Point2D> it = new LineIterator(line); it.hasNext();) {
		    current = it.next();
		    points.add(current);
		}
		return points;

	}
	
	public static int getManhattanDistance(int x1, int z1, int x2, int z2){
		int disx = Math.abs(x1-x2);
		int disz = Math.abs(z1-z2);
		return disx+disz;
		
		
	}


	public static void blink(EntityPlayer player) {
		int blinkDistance = 15;
		MovingObjectPosition target = player.rayTrace(blinkDistance, 1F);
		double i;
		double j;
		double k;
		float r = 3;
		if (target != null) {
			if (target.entityHit != null) {
				i = target.entityHit.posX;
				j = target.entityHit.posY;
				k = target.entityHit.posZ;
				if (getDistance(player.posX, player.posY, player.posZ, i, j, k)>blinkDistance){
					return;
				}
			} else {
				i = target.blockX;
				j = target.blockY;
				k = target.blockZ;
				if (getDistance(player.posX, player.posY, player.posZ, i, j, k)>blinkDistance){
					return;
				}
			}
			player.setPositionAndUpdate(i, j+1, k);
			//System.out.println("teled");
		}
	}

	public static double getDistance(double x1, double y1, double z1, double x2, double y2, double z2) {
		double dx = x1 - x2;
		double dy = y1 - y2;
		double dz = z1 - z2;

		// We should avoid Math.pow or Math.hypot due to perfomance reasons
		return Math.sqrt(dx * dx + dy * dy + dz * dz);
	}
}
