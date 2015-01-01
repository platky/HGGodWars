package org.bitbucket.platky.HGGodWars;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;


public class God {
	private String name = null;
	private boolean active = false;
	private String type = null;
	private String align=null;
	private int points =0;
	private List<Player> players = new ArrayList<Player>();
	private List<Shrine> shrines = new ArrayList<Shrine>();
	
	public God(String godName, boolean godActive, String godType, String godAlign, int godPoints, List<Player> playerList, List<Shrine> shrineList) {
		this.name=godName;
		this.active=godActive;
		this.type= godType;
		this.align=godAlign;
		this.points=godPoints;
		this.players=playerList;
		this.shrines=shrineList;
	}
	
	public String getGName(){
		return this.name;
	}
	
	public String getType() {
		return this.type;
	}
	
	public boolean isActive() {
		return this.active;
	}
	
	public void setActive(boolean val) {
		this.active=val;
	}
	
	public int getPoints() {
		return this.points;
	}
	
	public void setPoints(int givPoints) {
		this.points=givPoints;
	}
	
	public List<Player> getPlayers () {
		return this.players;
	}
	
	public void addPlayer(Player newPlayer) {
		this.players.add(newPlayer);
	}
	
	public void removePlayer(Player oldPlayer) {
		this.players.remove(oldPlayer);
	}
	
	public int playerCount(){
		return this.players.size();
	}
	
	public String getAlignment() {
		return this.align;
	}
	
	public int shrineCount() {
		return this.shrines.size();
	}
}
