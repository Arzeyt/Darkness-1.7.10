package com.arzeyt.theDarkness;

import java.util.HashSet;

public class TDLineLocs {

	public HashSet<TDLocation> locs = new HashSet<TDLocation>();
	TDLocation loc1, loc2;
	
	public TDLineLocs(HashSet<TDLocation> lineLocs, TDLocation loc1, TDLocation loc2){
		this.locs=lineLocs;
		this.loc1=loc1;
		this.loc2=loc2;
		
	}
	
	public TDLocation getLocation1(){
		return this.loc1;
	}
	
	public TDLocation getLocation2(){
		return this.loc2;
	}
}
