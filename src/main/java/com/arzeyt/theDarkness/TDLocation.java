package com.arzeyt.theDarkness;

/**
 * never create this without a NULL y value
 *
 */
public class TDLocation {

	int x, y, z, dimID;
	
	public TDLocation(int x, int y, int z){
		this.x=x;
		this.y=y;
		this.z=z;
	}
	
	public int getX(){
		return this.x;
	}
	
	public int getY(){
		return this.y;
	}
	
	public int getZ(){
		return this.z;
	}
	
	public boolean isSameLocation(TDLocation loc){
		if(loc==null){return false;}
		if(loc.x==x && loc.y==y && loc.z==z){
			return true;
		}else{
			return false;
		}
	}
	
	@Override
    public boolean equals(Object o) {
        if (o==null)return false;
		if (o == this) return true;   //If objects equal, is OK
        if (o instanceof TDLocation) {
           TDLocation tdl = (TDLocation)o;
           return (x == tdl.x)  && (y == tdl.y) && (z==tdl.z);
        }
        return false;
    }
	
	@Override
	public int hashCode(){
		int hashCode=1;
		hashCode = 31*hashCode+x;
		hashCode = 31*hashCode+y;
		hashCode = 31*hashCode+z;
		return hashCode;
	}
}
