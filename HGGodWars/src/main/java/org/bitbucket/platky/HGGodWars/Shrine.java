package org.bitbucket.platky.HGGodWars;

import javax.tools.JavaFileManager.Location;

public class Shrine {
	public Location loc= null;
	public int level =0;
	
	public Shrine(Location shrineLoc, int shrineLevel) {
		this.loc=shrineLoc;
		this.level=shrineLevel;
	}
	
	public Location getLoc() {
		return this.loc;
	}
	
	public int getLevel() {
		return this.level;
	}
}
