package org.bitbucket.platky.HGGodWars;

import java.util.List;



public class God {
	private String name = null;
	private boolean active = false;
	private String type = null;
	private String align=null;
	private int points =0;
	private List<PlayerData> players ;
	private List<Shrine> shrines;
	
	public God(String godName, boolean godActive, String godType, String godAlign, int godPoints, List<PlayerData> playerList, List<Shrine> shrineList) {
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
	
	public List<PlayerData> getPlayers () {
		return this.players;
	}
	
	public void addPlayer(PlayerData newPlayer) {
		this.players.add(newPlayer);
	}
	
	public void removePlayer(PlayerData oldPlayer) {
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
