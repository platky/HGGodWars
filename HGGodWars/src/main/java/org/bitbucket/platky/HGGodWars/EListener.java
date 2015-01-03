package org.bitbucket.platky.HGGodWars;

import java.io.File;
import java.util.UUID;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

public class EListener implements Listener{

	@EventHandler
	public void onLogin(PlayerLoginEvent event) {
		Player player = event.getPlayer();
		File dataFolder = HGGodWars.dataString;
		File curFile =  new File(dataFolder + File.separator + "Players"+File.separator+player.getUniqueId()+".yml");
		PlayerData playerD;
		if (!curFile.exists()) {
			//first time login
			YamlConfiguration yamlFile = YamlConfiguration.loadConfiguration(curFile);
			yamlFile.set("UUID", player.getUniqueId().toString());
			yamlFile.set("God", ""); //no god for now
			yamlFile.set("Karma", 0);
			playerD = new PlayerData(player, "", 0, player.getUniqueId());
		} else {
			playerD = startPlayer(curFile, player);
		}
		HGGodWars.players.add(playerD);
	}
	
	public PlayerData startPlayer(File givFile, Player player) {
		YamlConfiguration yamlFile = YamlConfiguration.loadConfiguration(givFile);
		UUID playerId = UUID.fromString(yamlFile.getString("UUID"));
		String godName = yamlFile.getString("God");
		int karma = yamlFile.getInt("Karma");
		PlayerData playerD = new PlayerData(player, godName, karma, playerId);
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
