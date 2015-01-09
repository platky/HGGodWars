package org.bitbucket.platky.HGGodWars;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;


//TODO still need to check if scores show up below name
public class scoreboard {
	public ScoreboardManager sbManager;
	public Scoreboard karmaBoard;
	public Team neutral;
	public Team good1;
	public Team good2;
	public Team good3;
	public Team good4;
	public Team evil1;
	public Team evil2;
	public Team evil3;
	public Team evil4;
	public Objective karmaObj;
	
	public scoreboard() {
		sbManager = Bukkit.getScoreboardManager();
		karmaBoard = sbManager.getNewScoreboard();
		neutral = karmaBoard.registerNewTeam("Neutral");
		good1 = karmaBoard.registerNewTeam("Good1");
		good2 = karmaBoard.registerNewTeam("Good2");
		good3 = karmaBoard.registerNewTeam("Good3");
		good4 = karmaBoard.registerNewTeam("Good4");
		evil1 = karmaBoard.registerNewTeam("Evil1");
		evil2 = karmaBoard.registerNewTeam("Evil2");
		evil3 = karmaBoard.registerNewTeam("Evil3");
		evil4 = karmaBoard.registerNewTeam("Evil4");
		
		neutral.setPrefix(ChatColor.WHITE +"");
		good1.setPrefix(ChatColor.BLUE +"");
		good2.setPrefix(ChatColor.DARK_BLUE +"");
		good3.setPrefix(ChatColor.GREEN +"");
		good4.setPrefix(ChatColor.DARK_GREEN +"");
		evil1.setPrefix(ChatColor.GRAY +"");
		evil2.setPrefix(ChatColor.DARK_GRAY +"");
		evil3.setPrefix(ChatColor.RED +"");
		evil4.setPrefix(ChatColor.DARK_RED +"");
		
		karmaObj = karmaBoard.registerNewObjective("Karma", "dummy");
		karmaObj.setDisplaySlot(DisplaySlot.BELOW_NAME);
		karmaObj.setDisplayName("Karma");
	}
}
