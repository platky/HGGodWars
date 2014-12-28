package org.bitbucket.platky.HGGodWars;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;


/* -quests give points
 *  - minigames give points
 *  - allied vs enemy religion killing
 *  - money/god specific sacrifice, donation etc
 *  - shrines to dungeons
 */

public class HGGodWars extends JavaPlugin{


	public static HGGodWars hggw = new HGGodWars();
	public static File dataString;
	public static List<God> gods = new ArrayList<God>();
	
	public void onEnable() {
		Bukkit.getLogger().info("onEnable has been invoked");
		File dataFolder = getDataFolder();
		dataString = getDataFolder();
		if(!dataFolder.exists()) {
			dataFolder.mkdir();
		}
		loadConfig();
		initGods();
	}
	

	public void onDisable() {
		//cleanup any loose ends7
	}
	
	public void loadConfig() {
		getConfig().addDefault("Restrictions.Cost", 10000);
		getConfig().addDefault("Restrictions.MinPlayers", 3);
		getConfig().addDefault("Restrictions.MaxGods", 0);
		getConfig().addDefault("UseDungeons", true);
		
		getConfig().options().copyDefaults(true); //further review this
		saveConfig();
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("godwars")) {
			if(args[0].equalsIgnoreCase("create")) {
				createGod(args[1], args[2], args, sender);
				return true;
			} else if(args[0].equalsIgnoreCase("join")) {
				
				return true;
			}
			
			return true;
		}
		return false;
	}
	
	//for now do dungeon generation in this plugin (MOVE TO SEPERATE CLASS)
	public boolean dungeonGenerate() {
		
		return false;
	}
	
	//get all the god files and initialize
	public void initGods() {
		File dataFolder = getDataFolder();
		File[] fileList = dataFolder.listFiles();
		YamlConfiguration yamlFile;
		String godName;
		Boolean godActive;
		String godType;
		int godPoints;
		List<String> pnameList = new ArrayList<String>();
		List<Player> playerList=new ArrayList<Player>();
		List<Shrine> shrineList = new ArrayList<Shrine>();
		for (int i =0; i< fileList.length; i++) {
			if(fileList[i].getName() != "config.yml") { //verify this statement
				yamlFile = YamlConfiguration.loadConfiguration(fileList[i]);
				godName = yamlFile.getString("Name");
				godActive = yamlFile.getBoolean("Active");
				godType = yamlFile.getString("Type");
				godPoints = yamlFile.getInt("Points");
				pnameList = yamlFile.getStringList("Members");
				for (int j=0; j < pnameList.size(); j++) {
					UUID pID =UUID.fromString(pnameList.get(j)); //convert from string back to uuid
					playerList.add(getServer().getPlayer(pID)); //find the player by uuid and add it to list
				}
				//officially create an instance of the god and throw it into our arraylist
				//shrinelists not currently implemented
				God newGod = new God(godName, godActive, godType, godPoints, playerList, shrineList);
				gods.add(newGod);
			}
		}
	}
	
	
	//create the gods file and initialize it
	public boolean createGod(String godName, String godType, String[] args, CommandSender sender) {

		Player player = (Player) sender;
		File dataFolder = getDataFolder();
		if(!dataFolder.exists()) {
			//error
			return false;
		}
		
		if (fileExist(godName)==false) {
			try {

				YamlConfiguration yamlFile = new YamlConfiguration();
				yamlFile.set("Name", godName);
				yamlFile.set("Active", "false");
				yamlFile.set("Type", godType);
				yamlFile.set("Points", 0);
				List<String> memberList = new ArrayList<String>();
				memberList.add(player.getUniqueId().toString()); //stored uuid as string
				yamlFile.set("Members",memberList);
				//will probably have to list shrines here aswell
				
				yamlFile.save(getDataFolder()+ File.separator+  godName+".yml");

				player.sendMessage(ChatColor.GREEN + "God Created!");
			} catch (IOException e) {
				e.printStackTrace();
			}

			List<Player> playerList=new ArrayList<Player>();
			List<Shrine> shrineList = new ArrayList<Shrine>();
			playerList.add(player);
			God newGod = new God(godName, false, godType, 0, playerList, shrineList);
			gods.add(newGod);
			
			return true;
		} else {
			player.sendMessage(ChatColor.RED + "This god already exists. Choose another name...");
		}
		
		return false;
	}
	
	
	
	public Boolean fileExist(String fileName) {
		File file = new File(dataString+ File.separator+ fileName+".yml");
		return file.exists();
	}
	
}