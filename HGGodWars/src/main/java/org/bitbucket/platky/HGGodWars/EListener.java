package org.bitbucket.platky.HGGodWars;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class EListener implements Listener{

	@EventHandler
	public void onLogin(PlayerLoginEvent event) {
		Player player = event.getPlayer();
		File dataFolder = HGGodWars.dataString;
		File curFile =  new File(dataFolder + File.separator + "Players"+File.separator+player.getUniqueId()+".yml");
		PlayerData playerD;
		if (!curFile.exists()) {
			//first time login
			try {
			YamlConfiguration yamlFile = YamlConfiguration.loadConfiguration(curFile);
			yamlFile.set("UUID", player.getUniqueId().toString());
			yamlFile.set("God", ""); //no god for now
			yamlFile.set("Karma", 0);
				yamlFile.save(dataFolder + File.separator + "Players"+File.separator+player.getUniqueId()+".yml");
			} catch (IOException e) {
				e.printStackTrace();
			}

			playerD = new PlayerData(player, "", 0, player.getUniqueId());

			HGGodWars.players.add(playerD);
		} else {
			playerD = startPlayer(player);
		}
	}
	
	@EventHandler
	public void onLogout(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		endPlayer(player);
	}
	
	//TODO DO NOT CREATE A NEW INSTANCE! this player instance already exists somewhere!!
	public PlayerData startPlayer(Player player) {
		PlayerData playerD = HGGodWars.findPlayerD(player.getUniqueId());
		if (playerD==null){

			Bukkit.getLogger().info("YOU DONE GOOFED!");
		}
		Bukkit.getLogger().info("Looking for player: " + player.getName());
		playerD.assignPlayer(player);
		return playerD;
	}
	
	public void endPlayer(Player player) {
		//make sure the player file is saved and remove from players list
		savePlayer(player);
		PlayerData playerD = HGGodWars.findPlayerData(player);
		HGGodWars.players.remove(playerD);
	}
	
	public Boolean savePlayer(Player player) {
		PlayerData playerD = HGGodWars.findPlayerData(player);
		File dataFolder = HGGodWars.dataString;
		File curFile =  new File(dataFolder + File.separator + "Players"+File.separator+player.getUniqueId()+".yml");
		if(!curFile.exists()) {
			return false;
		}
		YamlConfiguration yamlFile = YamlConfiguration.loadConfiguration(curFile);
		yamlFile.set("God", playerD.getGodName());
		yamlFile.set("Karma", playerD.getKarma());
		yamlFile.set("UUID", playerD.getUUID());
		return true;
	}
}
