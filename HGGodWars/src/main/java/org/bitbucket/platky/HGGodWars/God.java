package org.bitbucket.platky.HGGodWars;

import java.util.List;
import java.util.UUID;



public class God {
	private String name = null;
	private boolean active = false;
	private String type = null;
	private String align=null;
	private int points =0;
	private int karmaPoints=0;
	private List<UUID> players;
	private List<Shrine> shrines;
	
	public God(String godName, boolean godActive, String godType, String godAlign, int godPoints, List<UUID> playerList, List<Shrine> shrineList) {
		this.name=godName;
		this.active=godActive;
		this.type= godType;
		this.align=godAlign;
		this.points=godPoints;
		this.players=playerList;
		this.shrines=shrineList;
	}
	
	public String getGName(){
		return this.name;
	}
	
	public String getType() {
		return this.type;
	}
	
	public boolean isActive() {
		return this.active;
	}
	
	public void setActive(boolean val) {
		this.active=val;
	}
	
	public int getPoints() {
		return this.points +this.karmaPoints;
	}
	
	public void setPoints(int givPoints) {
		this.points=givPoints;
	}
	
	public List<UUID> getPlayers () {
		return this.players;
	}
	
	public void addPlayer(UUID newPlayer) {
		this.players.add(newPlayer);
	}
	
	public void removePlayer(UUID oldPlayer) {
		this.players.remove(oldPlayer);
	}
	
	public int playerCount(){
		return this.players.size();
	}
	
	public String getAlignment() {
		return this.align;
	}
	
	public int shrineCount() {
		return this.shrines.size();
	}
	
	//calculates karma of all the members associated with this god
	public void calcKarma() {
		this.karmaPoints=0;
		int tempInt;
		for(int i=0; i<players.size(); i++) {
			tempInt= HGGodWars.findPlayerD(players.get(i)).getKarma();
			if(align.equalsIgnoreCase("Good")){
				if(tempInt>1000) {
					karmaPoints+=10;
				} else if(tempInt>900){
					karmaPoints+=9;
				} else if(tempInt>800){
					karmaPoints+=8;
				} else if(tempInt>700){
					karmaPoints+=7;
				} else if(tempInt>600){
					karmaPoints+=6;
				} else if(tempInt>500){
					karmaPoints+=5;
				} else if(tempInt>400){
					karmaPoints+=4;
				} else if(tempInt>300){
					karmaPoints+=3;
				} else if(tempInt>200){
					karmaPoints+=2;
				} else if(tempInt>100){
					karmaPoints+=1;
				}
			} else if(align.equalsIgnoreCase("Evil")){
				if(tempInt<-1000) {
					karmaPoints+=10;
				} else if(tempInt<-900){
					karmaPoints+=9;
				} else if(tempInt<-800){
					karmaPoints+=8;
				} else if(tempInt<-700){
					karmaPoints+=7;
				} else if(tempInt<-600){
					karmaPoints+=6;
				} else if(tempInt<-500){
					karmaPoints+=5;
				} else if(tempInt<-400){
					karmaPoints+=4;
				} else if(tempInt<-300){
					karmaPoints+=3;
				} else if(tempInt<-200){
					karmaPoints+=2;
				} else if(tempInt<-100){
					karmaPoints+=1;
				}
			} else if(align.equalsIgnoreCase("Neutral")){
				if( tempInt>500 || tempInt <-500) {
					//nothing
				} else if(tempInt>450 || tempInt <-450) {
					karmaPoints+=1;
				} else if(tempInt>400 || tempInt <-400) {
					karmaPoints+=2;
				} else if(tempInt>350 || tempInt <-350) {
					karmaPoints+=3;
				} else if(tempInt>300 || tempInt <-300) {
					karmaPoints+=4;
				} else if(tempInt>250 || tempInt <-250) {
					karmaPoints+=5;
				} else if(tempInt>200 || tempInt <-200) {
					karmaPoints+=6;
				} else if(tempInt>150 || tempInt <-150) {
					karmaPoints+=7;
				} else if(tempInt>100 || tempInt <-100) {
					karmaPoints+=8;
				} else if(tempInt>50 || tempInt <-50) {
					karmaPoints+=9;
				} else{
					karmaPoints+=10;
				}
			}
		}
	}
}
