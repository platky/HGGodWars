package org.bitbucket.platky.HGGodWars;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Leaderboard {
	List<String> rankedGods = new ArrayList<String>();
	//gets initialized once on startup
	public Leaderboard() {
		updateGods();
	}
	
	public void updateGods() {
		rankedGods.clear();
		for (int i=0; i< HGGodWars.gods.size(); i++) {
			if(HGGodWars.gods.get(i).isActive()){
				rankedGods.add(HGGodWars.gods.get(i).getGName());
			}
		}
		sortGods2();
		//sortGods(0, rankedGods.size()-1);
	}
	
	//TODO verify this works as intended
	//Using quicksort
	public void sortGods(int low, int high) {
		int i=low;
		int j=high;
		int pivot = HGGodWars.findGod(rankedGods.get((low+(high-low)) /2)).getPoints();
		
		while(i<=j) {
			while(HGGodWars.findGod(rankedGods.get(i)).getPoints() > pivot) {
				i++;
			}
			while(HGGodWars.findGod(rankedGods.get(i)).getPoints() < pivot) {
				j--;
			}
			if(i <= j) {
				String tempGod = rankedGods.get(i);
				rankedGods.set(i, rankedGods.get(j));
				rankedGods.set(j, tempGod);
				i++;
				j--;
			}
		}
		if (low < j) {
			sortGods(low, j);
		}
		if (i < high) {
			sortGods(i, high);
		}
	}
	
	public void sortGods2() {
		boolean flag =true;
		String tempS;
		while (flag) {
			flag=false;
			for (int i=0; i<rankedGods.size()-1; i++) {
				if (HGGodWars.findGod(rankedGods.get(i)).getPoints() < HGGodWars.findGod(rankedGods.get(i+1)).getPoints()) {
					//swap
					tempS = rankedGods.get(i);
					rankedGods.set(i, rankedGods.get(i+1));
					rankedGods.set(i+1, tempS);
					flag=true;
				}
			}
		}
	}
	
	
	public int godRank(God givenGod) {
		//+1 since its an index and we want ranks to start at 1
		return (rankedGods.indexOf(givenGod.getGName())+1); 
	}
	
	public God firstPlace() {
		updateGods();
		if(rankedGods.isEmpty()) {
			return null;
		}
		if (HGGodWars.findGod(rankedGods.get(0)).getPoints() == HGGodWars.findGod(rankedGods.get(1)).getPoints()) {
			return null;
		}
		
		return HGGodWars.findGod(rankedGods.get(0));
	}
	
	public void displayLeaders(Player player) {
		updateGods();
		player.sendMessage(ChatColor.GOLD + "---------------------");
		player.sendMessage(HGGodWars.bgs + "Current Leaders");
		player.sendMessage(ChatColor.GOLD + "---------------------");
		for (int i=0; i< 10; i++) {
			if (i == rankedGods.size()) {
				break;
			}
			player.sendMessage("" +ChatColor.GOLD + (i+1) +". " +ChatColor.WHITE + rankedGods.get(i)+" - "+ 
			HGGodWars.findGod(rankedGods.get(i)).getPoints());
		}
	}
}
