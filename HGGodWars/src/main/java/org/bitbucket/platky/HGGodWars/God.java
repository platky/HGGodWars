package org.bitbucket.platky.HGGodWars;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;


public class God {
	public String name = null;
	public boolean active = false;
	public String type = null;
	public int points =0;
	List<Player> players = new ArrayList<Player>();
	List<Shrine> shrines = new ArrayList<Shrine>();
	
	public God(String godName, boolean godActive, String godType, int godPoints, List<Player> playerList, List<Shrine> shrineList) {
		this.name=godName;
		this.active=godActive;
		this.type= godType;
		this.points=godPoints;
		this.players=playerList;
		this.shrines=shrineList;
	}
	
	public String getGName(){
		return this.name;
	}
	
	public boolean isActive() {
		return this.active;
	}
	
	public int getPoints() {
		return this.points;
	}
}
