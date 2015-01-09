package org.bitbucket.platky.HGGodWars;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scoreboard.Score;

public class EListener implements Listener{

	@EventHandler
	public void onLogin(PlayerJoinEvent event) {
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
			player.setScoreboard(HGGodWars.kBoard.karmaBoard);
			Score curScore = HGGodWars.kBoard.karmaObj.getScore(player.getName());
			curScore.setScore(0);
			playerD.realignKarma();
		} else {
			playerD = startPlayer(player);
			player.setScoreboard(HGGodWars.kBoard.karmaBoard);
			Score curScore = HGGodWars.kBoard.karmaObj.getScore(player.getName());
			curScore.setScore(playerD.getKarma());
			playerD.realignKarma();
		}
	}
	
	
	@EventHandler
	public void onLogout(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		if (player==null) {
			Bukkit.getLogger().info("ERROR player is null");
		}
		endPlayer(player);
	}
	
	@EventHandler
	public Boolean entityDeath(EntityDeathEvent event) {
		LivingEntity entity = event.getEntity();
		Player player = entity.getKiller();
		if (player==null) {
			//a player was not involved in this
			return false;
		}
		PlayerData playerD = HGGodWars.findPlayerD(player.getUniqueId()); 
		if (entity instanceof Player) { //check if a player died
			playerD.subKarma(5);
		} else { //it was not a player that died
			if(entity.getType().equals(EntityType.VILLAGER)) {
				playerD.subKarma(3);
			} else if (entity.getType().equals(EntityType.OCELOT)) {
				playerD.subKarma(2);
			} else if (entity.getType().equals(EntityType.HORSE)) {
				if(entity.getCustomName() != null) { //TODO mythic custom entities can be dealt like with this
					
				}
				playerD.subKarma(2);
			} else if (entity.getType().equals(EntityType.ENDERMAN)) {
				playerD.addKarma(1);
			} else if (entity.getType().equals(EntityType.MAGMA_CUBE)) {
				playerD.addKarma(1);
			} else if (entity.getType().equals(EntityType.SILVERFISH)) {
				playerD.addKarma(1);
			} else if (entity.getType().equals(EntityType.SLIME)) {
				playerD.addKarma(1);
			} else if (entity.getType().equals(EntityType.SPIDER)) {
				playerD.addKarma(2);
			} else if (entity.getType().equals(EntityType.CAVE_SPIDER)) {
				playerD.addKarma(2);
			}  else if (entity.getType().equals(EntityType.PIG_ZOMBIE)) {
				playerD.addKarma(2);
			} else if (entity.getType().equals(EntityType.BLAZE)) {
				playerD.addKarma(3);
			} else if (entity.getType().equals(EntityType.CREEPER)) {
				playerD.addKarma(3);
			}else if (entity.getType().equals(EntityType.SKELETON)) {
				playerD.addKarma(3);
			} else if (entity.getType().equals(EntityType.ZOMBIE)) {
				playerD.addKarma(3);
			//} else if (entity.getType().equals(EntityType.GUARDIAN)) {
			//	playerD.addKarma(2);
			} else if (entity.getType().equals(EntityType.WITCH)) {
				playerD.addKarma(4);
			} else if (entity.getType().equals(EntityType.GHAST)) {
				playerD.addKarma(4);
			//} else if (entity.getType().equals(EntityType.WITCH)) {
				//playerD.addKarma(4);
				//for spider jockey (not really necessary for our mobs
			//} else if (entity.getType().equals(EntityType.KILLERBUNNY)) {
			//	playerD.addKarma(4);
			//} else if (entity.getType().equals(EntityType.ELDERGAURDIAN)) {
			//	playerD.addKarma(4);
			} else if (entity.getType().equals(EntityType.WITHER)) {
				playerD.addKarma(8);
			} else if (entity.getType().equals(EntityType.ENDER_DRAGON)) {
				playerD.addKarma(8);
			}
		}
		return true;
	}
	
	
	public PlayerData startPlayer(Player player) {
		/*if (player==null) {
			Bukkit.getLogger().info("ERROR player is null");
		}*/
		PlayerData playerD = HGGodWars.findPlayerD(player.getUniqueId());
		playerD.assignPlayer(player);
		return playerD;
	}
	
	public void endPlayer(Player player) {
		//make sure the player file is saved and remove from players list
		//savePlayer(player);
		HGGodWars.savePlayer(player.getUniqueId());
		//PlayerData playerD = HGGodWars.findPlayerData(player);
		//HGGodWars.players.remove(playerD);
	}
	
	
}
