package org.bitbucket.platky.HGGodWars;

import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.Bukkit;

public class PlayerData {
	private Player player;
	private String playerName;
	private UUID id;
	private String godName;
	private int karma;
	
	public PlayerData(Player givPlayer, String givGod, int givKarma, UUID givId) {
		this.player=givPlayer;
		this.godName=givGod;
		this.karma=givKarma;
		this.id = givId;
		if (player==null) {
			this.playerName = offlineName(givId);
		} else {
			this.playerName=player.getName();
		}
	}
	
	//private UUID findUUID(Player givPlayer) {
	//	return givPlayer.getUniqueId();
	//}
	
	public String offlineName(UUID givId) {
		 String theName = Bukkit.getServer().getOfflinePlayer(givId).getName();
		 return theName;
	}
	
	public UUID getUUID() {
		return this.id;
	}
	
	public void assignPlayer(Player givPlayer) {
		this.player = givPlayer;
	}
	
	public Player getPlayer() {
		return this.player;
	}
	
	public String getGodName() {
		return godName;
	}
	
	public void setGodName(String givGod) {
		this.godName=givGod;
	}
	
	public int getKarma() {
		return this.karma;
	}
	
	public void setKarma(int givKarma) {
		this.karma=givKarma;
	}
	
	public void addKarma(int givKarma) {
		this.karma = this.karma+givKarma;
		if (this.karma > 1200) {
			this.karma=1200;
		}
	}
	
	public void subKarma(int givKarma) {
		this.karma = this.karma-givKarma;
		if (this.karma < -1200) {
			this.karma=-1200;
		}
	}
	
	public String getName(){
		return playerName;
	}

}
