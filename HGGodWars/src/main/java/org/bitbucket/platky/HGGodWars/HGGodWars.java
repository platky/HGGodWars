package org.bitbucket.platky.HGGodWars;


import java.io.File;
import java.io.IOException;
//import java.sql.Connection;
//import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import com.herocraftonline.heroes.Heroes;


/*-quests give points
 *  - minigames give points
 *  - allied vs enemy religion killing
 *  - money/god specific sacrifice, donation etc
 *  - shrines to dungeons
 */

//TODO if god no longer exists the player should not be associated with it
//TODO on startup player is associated with all gods, multiple instances of player being added over time
public class HGGodWars extends JavaPlugin{
	public static Heroes heroPlugin;

	//public static HGGodWars hggw = new HGGodWars();
	public static File dataString;
	public static List<God> gods = new ArrayList<God>();
	public static List<PlayerData> players = new ArrayList<PlayerData>();
	public static scoreboard kBoard;
	public static Leaderboard leaders;
	public static Perks perks;
	public static String bgs = ChatColor.GOLD + "[GodWars] ";
	public static BukkitScheduler scheduler;
	
	
	public static int minMembers;
	public static int heroesXp;
	public static boolean useHeroes;
	
	public void onEnable() {
		Bukkit.getLogger().info("onEnable has been invoked");
		heroPlugin = (Heroes)Bukkit.getPluginManager().getPlugin("Heroes");
		
		File dataFolder = getDataFolder();
		dataString = getDataFolder();
		if(!dataFolder.exists()) {
			dataFolder.mkdir();
		}
		File godFolder = new File(dataFolder + File.separator + "Gods");
		if(!godFolder.exists()) {
			godFolder.mkdir();
		}
		File playerFolder = new File(dataFolder + File.separator + "Players");
		if(!playerFolder.exists()) {
			playerFolder.mkdir();
		}
        getServer().getPluginManager().registerEvents(new EListener(), this);
        kBoard = new scoreboard();
		loadConfig();
		initPlayers();
		initGods();
        leaders = new Leaderboard();
        perks = new Perks(heroesXp, useHeroes);
		scheduler = Bukkit.getServer().getScheduler();
		scheduler.scheduleSyncRepeatingTask(this, new Runnable() {
			public void run() {
				perks.runPerks();
			}
		}, 200L, 72000L); //72000 is once per hour
	}
	

	public void onDisable() {
		//cleanup any loose ends
		//save all gods using saveGod method
		for (int i=0; i<gods.size(); i++) {
			saveGod(gods.get(i).getGName());
		}
		for(int i=0; i<players.size(); i++) {
			savePlayer(players.get(i).getUUID());
		}
		gods.clear();
		players.clear();
	}
	
	public void loadConfig() {
		getConfig().addDefault("Restrictions.Cost", 10000);
		getConfig().addDefault("Restrictions.MinPlayers", 3);
		getConfig().addDefault("Restrictions.MaxPlayers", 0);
		getConfig().addDefault("Restrictions.MaxGods", 0);
		getConfig().addDefault("Variables.UseDungeons", true);
		getConfig().addDefault("Variables.EnableAlignment", true);
		getConfig().addDefault("Heroes.UseHeroes", false);//should default to false;
		getConfig().addDefault("Heroes.XpBuff", 1.2);
		minMembers = getConfig().getInt("Restrictions.MinPlayers");
		heroesXp = getConfig().getInt("Heroes.XpBuff");
		useHeroes = getConfig().getBoolean("Heroes.UseHeroes");
		getConfig().options().copyDefaults(true); //further review this
		saveConfig();
	}
	
	/*public void startSql() {
		Connection c = null;
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc::sqlite::players.db"); //test where this is actually created?
		} catch (Exception e) {
			
		}
	}*/
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player player = (Player) sender;
		if (cmd.getName().equalsIgnoreCase("godwars")) {
			if (args.length ==0) {
				if(player != null) {
					helpCmds(player);
				}
				return true;
			}
			if(args[0].equalsIgnoreCase("players")) { //for debugging
				for (int i=0; i<players.size(); i++) {
					sender.sendMessage(players.get(i).getName());
				}
				return true;
			}
			if(args[0].equalsIgnoreCase("create")) {
				if(!sender.hasPermission("godwars.create")) { //================================================
					sender.sendMessage(bgs+ ChatColor.RED + "You do not have permission");
					return false;
				}
				if (args.length != 4) {
					sender.sendMessage(bgs+ChatColor.RED + "try /godwars create <god name> <god type> <god alignment>");
					return false;
				} else {
					createGod(args[1], args[2], args[3], args, sender);
					return true;
				}
			} else if(args[0].equalsIgnoreCase("join")) { //join one of the existing gods ====================================
				if(!sender.hasPermission("godwars.main")) {
					sender.sendMessage(bgs+ChatColor.RED + "You do not have permission");
					return false;
				}
				if(args.length != 2) {
					sender.sendMessage(bgs+ChatColor.RED + "try /godwars join <god name>");
					return false;
				} else {
					joinGod(args[1], sender);
					return true;
				}
			} else if(args[0].equalsIgnoreCase("list")) { //========================================================
				if(!sender.hasPermission("godwars.main")) {
					sender.sendMessage(bgs+ChatColor.RED + "You do not have permission");
					return false;
				}
				if(args.length == 1) {
					listGods(sender, 1, false);
					return true;
				} else if(args.length ==2) {
					//TODO remove the sort?
					if (args[1].equalsIgnoreCase("sort") || args[1].equalsIgnoreCase("true")) {
						listGods(sender, 1, true);
						return true;
					} else if(args[1].equalsIgnoreCase("false")) {
						listGods(sender, 1, false);
						return true;
					} else {
						//check if second arg is a number
						try {
							int tempInt = Integer.parseInt(args[1]);
							listGods(sender, tempInt, false);
							return true;
						} catch(NumberFormatException e) {
							sender.sendMessage(bgs+ChatColor.RED +"try /godwars list [page number] [sort]");
							return false;
						}
					}
				} else if(args.length ==3) {
					try {
						int tempInt = Integer.parseInt(args[1]);
						if (args[2].equalsIgnoreCase("sort") || args[2].equalsIgnoreCase("true")) {
							listGods(sender, tempInt, true);
							return true;
						} else if(args[2].equalsIgnoreCase("false")) {
							listGods(sender, tempInt, false);
							return true;
						}
						return false;
					} catch(NumberFormatException e) {
						sender.sendMessage(bgs+ChatColor.RED +"try /godwars list [page number] [sort]");
						return false;
					}
				} else {

					sender.sendMessage(bgs+ChatColor.RED + "You do not have permission");
					return false;
				}
			} else if(args[0].equalsIgnoreCase("leave")) {//======================================
				if(!sender.hasPermission("godwars.main")) {
					sender.sendMessage(bgs+ChatColor.RED + "You do not have permission");
					return false;
				}
				if(args.length != 1){
					sender.sendMessage(bgs+ChatColor.RED + "try /godwars leave");
				} else {
					PlayerData curPlayer = findPlayerD(player.getUniqueId());
					leaveGod(findGod(curPlayer.getGodName()), sender);
					perks.justLeft(player);
					perks.removeAffected(curPlayer);
				}
			} else if(args[0].equalsIgnoreCase("types")) {//====================================
				if(!sender.hasPermission("godwars.main")) {
					sender.sendMessage(bgs+ChatColor.RED + "You do not have permission");
					return false;
				}
				if(args.length !=1) {
					sender.sendMessage(bgs+ChatColor.RED + "try /godwars types");
					return false;
				} else {
					listGodTypes(sender);
					return true;
				}
			} else if(args[0].equalsIgnoreCase("info")) {//==========================================
				if(!sender.hasPermission("godwars.main")) {
					sender.sendMessage(bgs+ChatColor.RED + "You do not have permission");
					return false;
				}
				if(args.length !=2) {
					sender.sendMessage(bgs+ChatColor.RED + "try /godwars info <godType|godName>");
					return false;
				} else {
					if (godTypeInfo(sender, args[1]) ==false) {
						godInfo(sender, args[1], true);
					}
					return true;
				}
			} else if(args[0].equalsIgnoreCase("leaderboard")) {//==========================================
				if(!sender.hasPermission("godwars.main")) {
					sender.sendMessage(bgs+ChatColor.RED + "You do not have permission");
					return false;
				}
				if(args.length !=1) {
					sender.sendMessage(bgs+ChatColor.RED + "try /godwars leaderboard");
					return false;
				} else {
					leaders.displayLeaders(player);
					return true;
				}
			} else if(args[0].equalsIgnoreCase("play")){
				sender.sendMessage("Current Players Count: " + players.size());
			}
		} else if(cmd.getName().equalsIgnoreCase("karma")) {
			if (args.length==0) {//base command
				if(!sender.hasPermission("godwars.karma")) {
					sender.sendMessage(bgs+ChatColor.RED + "You do not have permission");
				} else {
					PlayerData curPlayer = findPlayerD(player.getUniqueId());
					curPlayer.displayKarma();
					return true;
				}
			}
			if(args[0].equalsIgnoreCase("add")) {
				if (args.length==3) {
					Player changePlayer = playerOnline(args[1]);
					if (changePlayer==null) {
						sender.sendMessage(bgs+ChatColor.RED + "Player not found");
						sender.sendMessage(bgs+ChatColor.RED + "Try /karma add <player name> <amount>");
						return false;
					}
					if(!sender.hasPermission("godwars.karma.change")) {//TODO review this in application
						sender.sendMessage(bgs+ChatColor.RED + "You do not have permission");
						return false;
					}
					try {
						int tempInt = Integer.parseInt(args[2]);
						PlayerData changePlayerD = findPlayerD(player.getUniqueId());
						changePlayerD.addKarma(tempInt);
						return true;
					}  catch(NumberFormatException e) {
						sender.sendMessage(bgs+ChatColor.RED + "Try /karma add <player name> <amount>");
						return false;
					}
				} else {
					sender.sendMessage(bgs+ChatColor.RED + "Try /karma add <player name> <amount>");
					return false;
				}
			} else if(args[0].equalsIgnoreCase("subtract")) {
				if (args.length==3) {
					Player changePlayer = playerOnline(args[1]);
					if (changePlayer==null) {
						sender.sendMessage(bgs+ChatColor.RED + "Player not found");
						sender.sendMessage(bgs+ChatColor.RED + "Try /karma subtract <player name> <amount>");
						return false;
					}
					if(!sender.hasPermission("godwars.karma.change")) {//TODO review this in application
						sender.sendMessage(bgs+ChatColor.RED + "You do not have permission");
						return false;
					}
					try {
						int tempInt = Integer.parseInt(args[2]);
						PlayerData changePlayerD = findPlayerD(player.getUniqueId());
						changePlayerD.subKarma(tempInt);
						return true;
					}  catch(NumberFormatException e) {
						sender.sendMessage(bgs+ChatColor.RED + "Try /karma subtract <player name> <amount>");
						return false;
					}
				} else {
					sender.sendMessage(bgs+ChatColor.RED + "Try /karma subtract <player name> <amount>");
					return false;
				}
			} else if(args[0].equalsIgnoreCase("set")) {
				if (args.length==3) {
					Player changePlayer = playerOnline(args[1]);
					if (changePlayer==null) {
						sender.sendMessage(bgs+ChatColor.RED + "Player not found");
						sender.sendMessage(bgs+ChatColor.RED + "Try /karma set <player name> <amount>");
						return false;
					}
					if(!sender.hasPermission("godwars.karma.change")) {//TODO review this in application
						sender.sendMessage(bgs+ChatColor.RED + "You do not have permission");
						return false;
					}
					try {
						int tempInt = Integer.parseInt(args[2]);
						PlayerData changePlayerD = findPlayerD(player.getUniqueId());
						changePlayerD.setKarma(tempInt);
						return true;
					}  catch(NumberFormatException e) {
						sender.sendMessage(bgs+ChatColor.RED + "Try /karma set <player name> <amount>");
						return false;
					}
				} else {
					sender.sendMessage(bgs+ChatColor.RED + "Try /karma set <player name> <amount>");
					return false;
				}
			} else if(args[0].equalsIgnoreCase("check")) {
				if (args.length==2) {
					Player changePlayer = playerOnline(args[1]);
					if (changePlayer==null) {
						sender.sendMessage(bgs+ChatColor.RED + "Player not found");
						sender.sendMessage(bgs+ChatColor.RED + "Try /karma check <player name>");
						return false;
					}
					if(!sender.hasPermission("godwars.karma")) {
						sender.sendMessage(bgs+ChatColor.RED + "You do not have permission");
						return false;
					}
					PlayerData changePlayerD = findPlayerD(player.getUniqueId());
					sender.sendMessage(bgs+ChatColor.GREEN+changePlayerD.getName()+" has "+ changePlayerD.getKarma()+" karma");
					return true;
				} else {
					sender.sendMessage(bgs+ChatColor.RED + "Try /karma check <player name>");
					return false;
				}
			}
		}
		return false;
	}
	
	//get all the god files and initialize
	public void initGods() {
		File dataFolder = getDataFolder();
		File godFolder =  new File(dataFolder + File.separator + "Gods");
		File[] fileList = godFolder.listFiles();
		YamlConfiguration yamlFile;
		String godName;
		Boolean godActive;
		String godType;
		String godAlign;
		int godPoints;
		for (int i =0; i< fileList.length; i++) {
			List<String> idList = new ArrayList<String>();
			List<UUID> playerList=new ArrayList<UUID>();
			List<Shrine> shrineList = new ArrayList<Shrine>();
			if(fileList[i].getName() != "config.yml") { //verify this statement
				yamlFile = YamlConfiguration.loadConfiguration(fileList[i]);
				godName = yamlFile.getString("Name");
				godActive = yamlFile.getBoolean("Active");
				godType = yamlFile.getString("Type");
				godAlign = yamlFile.getString("Alignment");
				godPoints = yamlFile.getInt("Points");
				idList = yamlFile.getStringList("Members");
				for (int j=0; j < idList.size(); j++) {
					UUID pID =UUID.fromString(idList.get(j)); //convert from string back to uuid
					UUID curPlayer = pID;
					playerList.add(curPlayer); //holding a list of references to our playerData list
				}

				Bukkit.getLogger().info("God Player List " + godName);
				Bukkit.getLogger().info("Players: " + playerList);
				Bukkit.getLogger().info("  ");
				//officially create an instance of the god and throw it into our arraylist
				//shrinelists not currently implemented
				God newGod = new God(godName, godActive, godType, godAlign,  godPoints, playerList, shrineList);
				gods.add(newGod);
			}
		}
	}
	
	//get all the player files and initialize (when players come online remember to update data)
	public void initPlayers() {
		File dataFolder = getDataFolder();
		File playerFolder =  new File(dataFolder + File.separator + "Players");
		File[] fileList = playerFolder.listFiles();
		YamlConfiguration yamlFile;
		Player curPlayer=null;
		String godName;
		int karma;
		UUID id;
		for (int i=0; i<fileList.length; i++) {
			yamlFile = YamlConfiguration.loadConfiguration(fileList[i]);
			id = UUID.fromString(yamlFile.getString("UUID"));
			godName = yamlFile.getString("God");
			karma = yamlFile.getInt("Karma");
			PlayerData newPlayer = new PlayerData(curPlayer, godName, karma, id);
			players.add(newPlayer);
			Bukkit.getLogger().info("Player started: " + id);
		}
		
	}
	
	//create the gods file and initialize it
	public boolean createGod(String godName, String godType, String godAlign, String[] args, CommandSender sender) {
		
		Player player = (Player) sender;
		PlayerData curPlayer = findPlayerD(player.getUniqueId());
		if(!curPlayer.getGodName().equals("")){
			player.sendMessage(bgs+ChatColor.RED + "You are already following a god. Type /godwars leave before creating another");
			return false;
		}
		
		File dataFolder = getDataFolder();
		if(!dataFolder.exists()) {
			//error
			return false;
		}
		String godType2 = correctType(godType);
		String godAlign2 = correctAlign(godAlign);
		if (godType2==null) {
			sender.sendMessage(bgs+ChatColor.RED + godType +" is not an allowable type. Try /godwars types");
			return false;
		} else if(godAlign2==null) {
			sender.sendMessage(bgs+ChatColor.RED + godAlign +" is not an allowable alignment. Choose Good, Evil or Neutral");
			return false;
		}
		if(godName.equalsIgnoreCase("Agriculture") || godName.equalsIgnoreCase("Life")|| godName.equalsIgnoreCase("Knowledge")
			|| godName.equalsIgnoreCase("Mountains")|| godName.equalsIgnoreCase("Nature")|| godName.equalsIgnoreCase("Sea")
			|| godName.equalsIgnoreCase("Technology")|| godName.equalsIgnoreCase("Undead")|| godName.equalsIgnoreCase("War")
			|| godName.equalsIgnoreCase("Wealth")|| godName.equalsIgnoreCase("Evil")|| godName.equalsIgnoreCase("Good")|| godName.equalsIgnoreCase("Neutral")) {
			sender.sendMessage(bgs + ChatColor.RED + "You can not name your god "+ godName);
		}
		
		if (fileExist(godName)==false) {
			try {
				YamlConfiguration yamlFile = new YamlConfiguration();
				yamlFile.set("Name", godName);
				yamlFile.set("Active", false);
				yamlFile.set("Type", godType);
				yamlFile.set("Points", 0);
				yamlFile.set("Alignment", godAlign);
				List<String> memberList = new ArrayList<String>();
				memberList.add(player.getUniqueId().toString()); //stored uuid as string
				yamlFile.set("Members",memberList);
				//will probably have to list shrines here aswell
				
				yamlFile.save(getDataFolder()+ File.separator+"Gods"+File.separator+  godName+".yml");

				player.sendMessage(bgs+ChatColor.GREEN + "God Created!");
			} catch (IOException e) {
				e.printStackTrace();
			}

			List<UUID> playerList=new ArrayList<UUID>();
			List<Shrine> shrineList = new ArrayList<Shrine>();
			playerList.add(player.getUniqueId());
			God newGod = new God(godName, false, godType, godAlign, 0, playerList, shrineList);
			gods.add(newGod);
			curPlayer.setGodName(godName);
			savePlayer(curPlayer.getUUID());
			return true;
		} else {
			player.sendMessage(bgs+ChatColor.RED + "This god already exists. Choose another name...");
		}
		
		return false;
	}
	
	//have the player join a god
	public boolean joinGod(String godName, CommandSender sender) {
		Player player = (Player) sender;
		God curGod = findGod(godName);
		if(curGod==null) {
			sender.sendMessage(bgs+ChatColor.RED + "That god does not exist. Type /godwars list or create your own");
			return false;
		}
		PlayerData curPlayer = findPlayerD(player.getUniqueId());
		if(!curPlayer.getGodName().equals("")){
			player.sendMessage(bgs+ChatColor.RED + "You are already following a god. Type /godwars leave");
			return false;
		}
		curGod.addPlayer(player.getUniqueId());
		curPlayer.setGodName(godName);
		updateActive(curGod);
		saveGod(godName);
		savePlayer(curPlayer.getUUID());
		player.sendMessage(bgs+ChatColor.GREEN + "You are now following " + godName);
		return true;
	}
	
	//locate a god in our data list
	public static God findGod(String godName){
		for(int i=0; i<gods.size(); i++) {
			if(gods.get(i).getGName().equalsIgnoreCase(godName)) {
				return gods.get(i);
			}
		}
		return null; //god does not exist with that name
	}
	
	
	public int findGodInd(String godName) {

		for(int i=0; i<gods.size(); i++) {
			if(gods.get(i).getGName() == godName) {
				return i;
			}
		}
		return -1; //god does not exist with that name
	}
	//update the gods status as active or not
	public void updateActive(God givenGod) {
		if (givenGod.playerCount() >= minMembers) {
			if (givenGod.isActive()) {
				
			} else {
				givenGod.setActive(true);
				msgActive(givenGod, true);
			}
		} else {
			if (givenGod.isActive()) {
				givenGod.setActive(false);
				msgActive(givenGod, false);
			}
		}
	}
	
	//message all the players when the gods become active or not
	public static void msgActive(God givenGod, boolean way){
		List<UUID> curPlayers = givenGod.getPlayers();
		Player player;
		for (int i=0; i<givenGod.playerCount(); i++) {
			if (way == true) { //becoming active
				player = findPlayerD(curPlayers.get(i)).getPlayer();
				if (player==null){ //player is not online
					//break;
				} else {
					player.sendMessage(bgs+ChatColor.GREEN + "Your god "+givenGod.getGName()+" is now active in this world!");
				}
			} else if(way ==false){ //becoming inactive
				player = findPlayerD(curPlayers.get(i)).getPlayer();
				if (player==null){ //player is not online
					//break;
				} else {
					player.sendMessage(bgs+ChatColor.RED + "Your god "+givenGod.getGName()+" is no longer active in this world");
				}
			}
		}
	}
	
	//give the player a list of current gods
	public void listGods(CommandSender sender,int pageNum, boolean sortIt){
		List<God> curGods = gods;
		//if (sortIt == true){
		//	curGods=godSort(curGods);
		//}
		//list 10 gods at a time
		sender.sendMessage(ChatColor.GOLD + "List of Gods (Name - Type - Player Count)");
		sender.sendMessage(ChatColor.GOLD + "------------------------------------");
		int c = 10*pageNum -10;
		int pageMax = (int)(Math.ceil((double)(curGods.size())/10)); //not working
		for (int i=c; i<(c+10); i++) {
			if (i>=curGods.size()) {
				break;
			}
			sender.sendMessage(curGods.get(i).getGName() + " - " + curGods.get(i).getType() + " - " + curGods.get(i).playerCount());
		}
		sender.sendMessage(ChatColor.GOLD + "----------------"+pageNum+"/"+pageMax+"-----------------");
		if (pageNum < pageMax) {
			sender.sendMessage(bgs+ChatColor.GOLD + "Type \"/godwars list "+pageNum+1 +" [sort]\" for the next page");//double check the quotes work on this
		}
		
	}
	
	//simple bubblesort for gods based on player count
	public List<God> godSort (List<God> givenGods) {
		God tempGod;
		boolean swap=true;
		while(swap) {
			swap=false;
			for (int i=0; i<givenGods.size() -1; i++) {
				if(givenGods.get(i).playerCount() < givenGods.get(i+1).playerCount()) {
					tempGod = givenGods.get(i); //does a deep copy work here? VERIFY!!
					givenGods.set(i, givenGods.get(i+1));
					givenGods.set(i+1, tempGod);
					swap =true;
				}
			}
		}
		return givenGods;
	}
	
	//checks if provided type is correct and fixes spelling (not a great method...)
	public String correctType(String givenType) {
		if (givenType.equalsIgnoreCase("Agriculture")) {
			return "Agriculture";
		} else if (givenType.equalsIgnoreCase("Life")) {
			return "Life";
		} else if (givenType.equalsIgnoreCase("Knowledge")) {
			return "Knowledge";
		} else if (givenType.equalsIgnoreCase("Mountains")) {
			return "Mountains";
		} else if (givenType.equalsIgnoreCase("Nature")) {
			return "Nature";
		} else if (givenType.equalsIgnoreCase("Sea")) {
			return "Sea";
		} else if (givenType.equalsIgnoreCase("Technology")) {
			return "Technology";
		} else if (givenType.equalsIgnoreCase("Undead")) {
			return "Undead";
		} else if (givenType.equalsIgnoreCase("War")) {
			return "War";
		} else if (givenType.equalsIgnoreCase("Wealth")) {
			return "Wealth";
		}
		return null;
	}
	
	public String correctAlign(String givenAlign) {
		if (givenAlign.equalsIgnoreCase("Good")) {
			return "Good";
		} else if (givenAlign.equalsIgnoreCase("Evil")) {
			return "Evil";
		} else if (givenAlign.equalsIgnoreCase("Neutral")) {
			return "Neutral";
		} 
		return null;
	}
	
	//simply tell the player the available god types. (currently hardcoded but make customizable later
	public void listGodTypes(CommandSender sender) {

		sender.sendMessage(ChatColor.GOLD + "-------------List of God Types-----------");
		sender.sendMessage(ChatColor.GOLD + "---------------------------------------");
		//hardcoded
		sender.sendMessage("1. Agriculture");//flowers
		//sender.sendMessage("2. Crafting");//
		//sender.sendMessage("3. Deception");
		sender.sendMessage("2. Life");//eggs
		sender.sendMessage("3. Knowledge");//books
		sender.sendMessage("4. Mountains"); //stone
		sender.sendMessage("5. Nature");//logs
		sender.sendMessage("6. Sea");//fish or buckets of water
		sender.sendMessage("7. Technology");
		//sender.sendMessage("1. Time");//clocks
		sender.sendMessage("8. Undead");//rotten meat
		sender.sendMessage("9. War"); //diamonds
		sender.sendMessage("10. Wealth"); //heads
		sender.sendMessage(ChatColor.GOLD + "type /godwars info <godType>  for more information");
	}
	
	public boolean godTypeInfo(CommandSender sender, String type) {
		if (type.equalsIgnoreCase("Agriculture")) {
			sender.sendMessage(ChatColor.DARK_PURPLE + "Type: " + ChatColor.WHITE + "Agriculture");
			sender.sendMessage(ChatColor.DARK_PURPLE + "Gifts: " + ChatColor.WHITE + "flowers, wheat, melon, potatoes, carrots");
			sender.sendMessage(ChatColor.DARK_PURPLE + "Base Perks: " + ChatColor.WHITE + "Never hungry, Increased farming drops");
			return true;
		} else if (type.equalsIgnoreCase("Crafting")) {
			
		} else if (type.equalsIgnoreCase("Deception")) {
			
		} else if (type.equalsIgnoreCase("Life")) {
			sender.sendMessage(ChatColor.DARK_PURPLE + "Type: " + ChatColor.WHITE + "Life");
			sender.sendMessage(ChatColor.DARK_PURPLE + "Gifts: " + ChatColor.WHITE + "eggs, seeds");
			sender.sendMessage(ChatColor.DARK_PURPLE + "Base Perks: " + ChatColor.WHITE + "1.5x HP");//heroes dependency
			return true;
		} else if (type.equalsIgnoreCase("Knowledge")) {
			sender.sendMessage(ChatColor.DARK_PURPLE + "Type: " + ChatColor.WHITE + "Knowledge");
			sender.sendMessage(ChatColor.DARK_PURPLE + "Gifts: " + ChatColor.WHITE + "books");
			sender.sendMessage(ChatColor.DARK_PURPLE + "Base Perks: " + ChatColor.WHITE + "1.5x Mana");//heroes dependency
			return true;
		} else if (type.equalsIgnoreCase("Mountains")) {
			sender.sendMessage(ChatColor.DARK_PURPLE + "Type: " + ChatColor.WHITE + "Mountains");
			sender.sendMessage(ChatColor.DARK_PURPLE + "Gifts: " + ChatColor.WHITE + "stone, cobblestone");
			sender.sendMessage(ChatColor.DARK_PURPLE + "Base Perks: " + ChatColor.WHITE + "Increased stone and ore drops");
			return true;
		} else if (type.equalsIgnoreCase("Nature")) {
			sender.sendMessage(ChatColor.DARK_PURPLE + "Type: " + ChatColor.WHITE + "Nature");
			sender.sendMessage(ChatColor.DARK_PURPLE + "Gifts: " + ChatColor.WHITE + "logs, leaves, vines");
			sender.sendMessage(ChatColor.DARK_PURPLE + "Base Perks: " + ChatColor.WHITE + "Increased dig speed and soil drops");
			return true;
		} else if (type.equalsIgnoreCase("Sea")) {
			sender.sendMessage(ChatColor.DARK_PURPLE + "Type: " + ChatColor.WHITE + "Sea");
			sender.sendMessage(ChatColor.DARK_PURPLE + "Gifts: " + ChatColor.WHITE + "fish, buckets of water");
			sender.sendMessage(ChatColor.DARK_PURPLE + "Base Perks: " + ChatColor.WHITE + "Breath underwater, Fishing increase");
			return true;
		} else if (type.equalsIgnoreCase("Technology")) {
			sender.sendMessage(ChatColor.DARK_PURPLE + "Type: " + ChatColor.WHITE + "Technology");
			sender.sendMessage(ChatColor.DARK_PURPLE + "Gifts: " + ChatColor.WHITE + "stone, cobblestone");
			sender.sendMessage(ChatColor.DARK_PURPLE + "Base Perks: " + ChatColor.WHITE + "Night vision goggles");
			return true;
		} else if (type.equalsIgnoreCase("Time")) {
			
		} else if (type.equalsIgnoreCase("Undead")) {
			sender.sendMessage(ChatColor.DARK_PURPLE + "Type: " + ChatColor.WHITE + "Undead");
			sender.sendMessage(ChatColor.DARK_PURPLE + "Gifts: " + ChatColor.WHITE + "rotten flesh");
			sender.sendMessage(ChatColor.DARK_PURPLE + "Base Perks: " + ChatColor.WHITE + "Resist Fire, Resist Poison");
			return true;
		} else if (type.equalsIgnoreCase("War")) {
			sender.sendMessage(ChatColor.DARK_PURPLE + "Type: " + ChatColor.WHITE + "War");
			sender.sendMessage(ChatColor.DARK_PURPLE + "Gifts: " + ChatColor.WHITE + "player heads");
			sender.sendMessage(ChatColor.DARK_PURPLE + "Base Perks: " + ChatColor.WHITE + "Increased damage");
			return true;
		} else if (type.equalsIgnoreCase("Wealth")) {
			sender.sendMessage(ChatColor.DARK_PURPLE + "Type: " + ChatColor.WHITE + "Wealth");
			sender.sendMessage(ChatColor.DARK_PURPLE + "Gifts: " + ChatColor.WHITE + "diamond, emerald");
			sender.sendMessage(ChatColor.DARK_PURPLE + "Base Perks: " + ChatColor.WHITE + "Passive money increase");
			return true;
		} else {
			//sender.sendMessage(ChatColor.RED + "That is not one of the available types");
		}
		return false;
	}
	
	public boolean godInfo(CommandSender sender, String godName, boolean typeF) {
		God curGod = findGod(godName);
		if (curGod==null) {
			if (typeF ==true) {
				sender.sendMessage(bgs+ChatColor.RED + "That god or type does not exist");
				sender.sendMessage(bgs+ChatColor.RED + "Type \"/godwars types\" for a list of current types");
				sender.sendMessage(bgs+ChatColor.RED + "Type \"/godwars list\" for a list of current gods");
			} else {
				sender.sendMessage(bgs+ChatColor.RED + "That god does not exist");
				sender.sendMessage(bgs+ChatColor.RED + "Type \"/godwars list\" for a list of current gods");
			}
			return false;
		}else {
			sender.sendMessage(ChatColor.GOLD + curGod.getGName());
			sender.sendMessage(ChatColor.GOLD + "Active: " + ChatColor.WHITE + curGod.isActive());
			sender.sendMessage(ChatColor.GOLD + "Type: " + ChatColor.WHITE + curGod.getType());
			sender.sendMessage(ChatColor.GOLD + "Alignment: " + ChatColor.WHITE + curGod.getAlignment());
			sender.sendMessage(ChatColor.GOLD + "Points: " + ChatColor.WHITE + curGod.getPoints());
			sender.sendMessage(ChatColor.GOLD + "# of Shrines: " + ChatColor.WHITE + curGod.shrineCount());
			sender.sendMessage(ChatColor.GOLD + "Players: " + ChatColor.WHITE + curGod.playerCount() +" total");
			List<UUID> curPlayers = curGod.getPlayers();
			if (curGod.playerCount() < 10) {
				for (int i=0; i<curGod.playerCount(); i++) {
					sender.sendMessage("- "+ findPlayerD(curPlayers.get(i)).getName()); //error here
				}
			} else {
				for (int i=0; i<10; i++) {
					sender.sendMessage("- "+ findPlayerD(curPlayers.get(i)).getName());
				}
				sender.sendMessage("and more....");
			}
			return true;
		}
	}
	
	public void changePoints(String method, String godName, int numPoints) {
		God curGod = findGod(godName);
		if (method.equalsIgnoreCase("add")) {
			addPoints(curGod, numPoints);
		} else if(method.equalsIgnoreCase("subtract")) {
			removePoints(curGod, numPoints);
		}
	}
	
	public void addPoints(God godIndex, int points) {
		godIndex.setPoints(godIndex.getPoints()+points);
	}
	
	public void removePoints(God godIndex, int points) {
		godIndex.setPoints(godIndex.getPoints()-points);
		if (godIndex.getPoints() <0) {
			godIndex.setPoints(0);
		}
	}
	
	
	//save the current god to its respective file
	public boolean saveGod(String godName){
		if(!fileExist(godName)) {
			Bukkit.getLogger().info("no file for that god exists");
			//recreate file??
			return false;
		} else {
			try {
				File f = new File(dataString+ File.separator+"Gods"+File.separator+  godName+".yml");
				YamlConfiguration yamlFile = YamlConfiguration.loadConfiguration(f);
			
				God curGod = findGod(godName);
				yamlFile.set("Active", curGod.isActive());
				yamlFile.set("Type", curGod.getType());
				yamlFile.set("Points", curGod.getRegPoints());
				List<String> stringPlayers = new ArrayList<String>();
				List<UUID> curUUIDs = curGod.getPlayers();
				for(int i=0; i<curUUIDs.size(); i++) {
					stringPlayers.add(curUUIDs.get(i).toString());
				}
				yamlFile.set("Members", null);
				yamlFile.set("Members",stringPlayers);//TODO check this
				//will probably have to list shrines here aswell
			
				yamlFile.save(getDataFolder()+ File.separator+"Gods"+File.separator+  godName+".yml");
				return true;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
	}
	
	public static boolean savePlayer(UUID id){
		String idString =id.toString();
		if(!pFileExist(id)) {
			Bukkit.getLogger().info("no file for that player exists");
			//recreate file??
			return false;
		} else {
			try {
				File f = new File(dataString+ File.separator+"Players"+File.separator+  idString+".yml");
				YamlConfiguration yamlFile = YamlConfiguration.loadConfiguration(f);
			
				PlayerData curPlayer = findPlayerD(id);
				yamlFile.set("UUID", id.toString());
				yamlFile.set("God", curPlayer.getGodName());
				yamlFile.set("Karma", curPlayer.getKarma());
			
				yamlFile.save(dataString+ File.separator+  "Players" +File.separator+idString+".yml");
				return true;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
	}
	
	public boolean removeGod(String godName) {
		God curGod = findGod(godName);
		gods.remove(curGod);

		File f = new File(dataString+ File.separator+  godName+".yml");
		return f.delete();
	}
	
	//the current god manager can assign someone else
	/*@SuppressWarnings("deprecation")
	public boolean changeManager(CommandSender sender, String newPlayer) {
		//check if currently manager
		Player player = (Player) sender;
		Player newManager = getServer().getPlayer(newPlayer);
		if (newManager == null) {
			sender.sendMessage(ChatColor.RED + "That player was not found");
			return false;
		}
		God curGod = findPlayerGod(player);
		if (curGod==null) {
			sender.sendMessage(ChatColor.RED + "You do not belong to any god");
			return false;
		} else {
			if(curGod.getPlayers().get(0)==player) {
				List<Player> newPlayers = curGod.getPlayers();
				int playInd=newPlayers.indexOf(newManager);
				if (playInd == -1) {
					sender.sendMessage(ChatColor.RED + "That player does not belong to this god");
					return false;
				}
				newPlayers.set(0, newPlayers.get(playInd));
				newPlayers.remove(newPlayers.get(playInd));
				newPlayers.add(player);
				sender.sendMessage(ChatColor.GREEN + "You have switched " + newManager.getName() +" to manager of this god");
				return true;
			} else {
				sender.sendMessage(ChatColor.RED + "You are not the manager of this god");
				return false;
			}
		}
	}*/
	
	//admin can switch the manager of a god they dont manage
	//@SuppressWarnings("deprecation")
	/*public boolean adminChangeManager(CommandSender sender, String godName, String newPlayer) {
		God curGod = findGod(godName);
		Player newManager = getServer().getPlayer(newPlayer);
		if (newManager == null) {
			sender.sendMessage(ChatColor.RED + "That player was not found");
			return false;
		}
		if (curGod==null) {
			sender.sendMessage(ChatColor.RED + "That god does not exist");
			return false;
		} else {
				List<Player> newPlayers = curGod.getPlayers();
				Player player = newPlayers.get(0);
				int playInd=newPlayers.indexOf(newManager);
				if (playInd == -1) {
					sender.sendMessage(ChatColor.RED + "That player does not belong to this god");
					return false;
				}
				newPlayers.set(0, newPlayers.get(playInd));
				newPlayers.remove(newPlayers.get(playInd));
				newPlayers.add(player);
				sender.sendMessage(ChatColor.GREEN + "You have switched " + newManager.getName() +" to manager of this god");
				return true;
			
		}
	}*/
	
	//find the god which has the specific player in it, return null if not found (BROKEN)
	public God findPlayerGod(Player player) {
		for(int i=0; i< gods.size(); i++) {
			if (gods.get(i).getPlayers().contains(player)) {
				return gods.get(i);
			}
		}
		return null;
	}
	
	
	public Boolean fileExist(String fileName) {
		File file = new File(dataString+ File.separator+"Gods" + File.separator+ fileName+".yml");
		return file.exists();
	}
	
	public static Boolean pFileExist(UUID id) {
		String idString = id.toString();
		File file = new File(dataString+File.separator+"Players"+ File.separator+idString+".yml");
		return file.exists();
	}
	
	
	public boolean leaveGod(God curGod, CommandSender sender) {
		Player player = (Player) sender;
		PlayerData playerD = findPlayerD(player.getUniqueId());
		if(curGod==null) {
			if (!playerD.getGodName().equals("")) {
				playerD.setGodName("");
				savePlayer(playerD.getUUID());
				return true;
			}
			sender.sendMessage(bgs+ChatColor.RED + "You are not involved with any god currently");
			return false;
		}
		if (curGod.getPlayers().contains(playerD.getUUID())) {
			curGod.removePlayer(playerD.getUUID());
			playerD.clearGod();
			sender.sendMessage(bgs+ChatColor.GREEN + "You have successfully left " + curGod.getGName());
		}
		saveGod(curGod.getGName());//save the file after removing the player
		savePlayer(player.getUniqueId());
		return true;
	}
	
	
	public static PlayerData findPlayerD(UUID givId) {
		for (int i=0; i<players.size(); i++) {
			if (givId.equals(players.get(i).getUUID())) {
				return players.get(i);
			}
		}
		return null;
	}
	
	//used when running in game commands with player names
	public Player playerOnline(String givenName) {
		List<Player> onlinePlayers = (List)Bukkit.getServer().getOnlinePlayers();
		for (int i=0; i< onlinePlayers.size(); i++) {
			if (onlinePlayers.get(i).getName().equalsIgnoreCase(givenName)) {
				return onlinePlayers.get(i);
			}
		}
		
		return null;
	}
	
	public void helpCmds(Player player) {
		player.sendMessage(bgs+"Here are your available GodWars commands");
		if(player.hasPermission("godwars.main")) {
			player.sendMessage(ChatColor.GOLD+"/godwars join <godname> :"+ChatColor.WHITE+" To join an existing god");
			player.sendMessage(ChatColor.GOLD+"/godwars leave :"+ChatColor.WHITE+" To leave your god");
			player.sendMessage(ChatColor.GOLD+"/godwars list :"+ChatColor.WHITE+" To list the available gods");
			player.sendMessage(ChatColor.GOLD+"/godwars types :"+ChatColor.WHITE+" To list the possible types of gods");
			player.sendMessage(ChatColor.GOLD+"/godwars info <godtype/godname> :"+ChatColor.WHITE+" To get info on a selected god type or existing god");
			player.sendMessage(ChatColor.GOLD+"/godwars leaderboard :"+ChatColor.WHITE+" To check the current leaderboard");
		}
		
		if(player.hasPermission("godwars.create")) {
			player.sendMessage(ChatColor.GOLD+"/godwars create <godname> <godtype> <alignment> :"+ChatColor.WHITE+" To create a god");
		}
		
		if(player.hasPermission("godwars.karma")) {
			player.sendMessage(ChatColor.GOLD+"/karma :"+ChatColor.WHITE+" To check your karma");
			player.sendMessage(ChatColor.GOLD+"/karma check <playername>:"+ChatColor.WHITE+" To check another playeres karma");
		}
		
		if(player.hasPermission("godwars.karma.change")){
			player.sendMessage(ChatColor.GOLD+"/karma add <playername> <amount> :"+ChatColor.WHITE+" To add karma to a player");
			player.sendMessage(ChatColor.GOLD+"/karma subtract <playername> <amount> :"+ChatColor.WHITE+" To subtract karma from a player");
			player.sendMessage(ChatColor.GOLD+"/karma set <playername> <amount> :"+ChatColor.WHITE+" To set the karma of a player");
		}
	}
}


