package org.bitbucket.platky.HGGodWars;

import org.bukkit.entity.Player;

public class PlayerData {
	private Player player;
	private String godName;
	private int karma;
	
	public PlayerData(Player givPlayer, String givGod, int givKarma) {
		this.player=givPlayer;
		this.godName=givGod;
		this.karma=givKarma;
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

}
