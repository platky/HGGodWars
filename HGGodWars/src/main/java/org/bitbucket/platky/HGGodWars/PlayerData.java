package org.bitbucket.platky.HGGodWars;

import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Score;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

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
	
	public void clearGod() {
		this.godName="";
	}
	
	public int getKarma() {
		return this.karma;
	}
	
	public void setKarma(int givKarma) {
		this.karma=givKarma;
		Score curScore = HGGodWars.kBoard.karmaObj.getScore(this.playerName);
		curScore.setScore(this.karma);
		realignKarma();
	}
	
	public void addKarma(int givKarma) {
		this.karma = this.karma+givKarma;
		if (this.karma > 1200) {
			this.karma=1200;
		}
		Score curScore = HGGodWars.kBoard.karmaObj.getScore(this.playerName);
		curScore.setScore(this.karma);
		realignKarma();
	}
	
	public void subKarma(int givKarma) {
		this.karma = this.karma-givKarma;
		if (this.karma < -1200) {
			this.karma=-1200;
		}
		Score curScore = HGGodWars.kBoard.karmaObj.getScore(this.playerName);
		curScore.setScore(this.karma);
		realignKarma();
	}
	
	public String getName(){
		return playerName;
	}
	
	public void displayKarma() {
		this.player.sendMessage(HGGodWars.bgs +ChatColor.WHITE+"You currently have " + this.karma + " karma");
	}
	
	public void realignKarma() {
		if (this.karma >= 1000) {
			HGGodWars.kBoard.good4.addPlayer(this.player);
			this.player.setPlayerListName(ChatColor.DARK_GREEN + playerName);
		} else if(this.karma >= 600) {
			HGGodWars.kBoard.good3.addPlayer(this.player);
			this.player.setPlayerListName(ChatColor.GREEN + playerName);
		} else if(this.karma >= 200) {
			HGGodWars.kBoard.good2.addPlayer(this.player);
			this.player.setPlayerListName(ChatColor.DARK_BLUE + playerName);
		} else if(this.karma >= 5) {
			HGGodWars.kBoard.good1.addPlayer(this.player);
			this.player.setPlayerListName(ChatColor.BLUE + playerName);
		} else if(this.karma > -5) {
			HGGodWars.kBoard.neutral.addPlayer(this.player);
			this.player.setPlayerListName(ChatColor.WHITE + playerName);
		} else if(this.karma > -200) {
			HGGodWars.kBoard.evil1.addPlayer(this.player);
			this.player.setPlayerListName(ChatColor.GRAY + playerName);
		} else if(this.karma > -600) {
			HGGodWars.kBoard.evil2.addPlayer(this.player);
			this.player.setPlayerListName(ChatColor.DARK_GRAY + playerName);
		} else if(this.karma > -1000) {
			HGGodWars.kBoard.evil3.addPlayer(this.player);
			this.player.setPlayerListName(ChatColor.RED + playerName);
		} else {
			HGGodWars.kBoard.evil4.addPlayer(this.player);
			this.player.setPlayerListName(ChatColor.DARK_RED + playerName);
		}
	}

}
