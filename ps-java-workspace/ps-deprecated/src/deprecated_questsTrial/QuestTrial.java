package deprecated_questsTrial;

import java.util.ArrayList;

import questObjectsTextLines.Questtext;
import utilities.Pair;

public class QuestTrial {
	
	/*
	 * General Variables
	 */
	public String name;
	public String npc;
	public String checkup = "";

	/*
	 * Need Variables
	 */
	public ArrayList<String> preQuests = new ArrayList<String>();
	public ArrayList<Pair<Integer, String>> needItems = new ArrayList<Pair<Integer, String>>();
	public ArrayList<Pair<Integer, String>> needSkills = new ArrayList<Pair<Integer, String>>();
	public boolean winchAccess = false; 
	
	/*
	 * Questdetails !!!
	 */
	public ArrayList<Questtext> questDetails;
	
	/*
	 * Reward Variables
	 */
	public int rewardMoney = 0;
	public int rewardXP = 0;
	public ArrayList<Pair<Integer, String>> rewardItems = new ArrayList<Pair<Integer, String>>();
	public ArrayList<Pair<String, String>> rewardAssociation = new ArrayList<Pair<String, String>>();
	public ArrayList<Pair<Integer, String>> rewardSkills = new ArrayList<Pair<Integer, String>>();
	public ArrayList<Pair<String, String>> rewardCombatMove = new ArrayList<Pair<String, String>>();

	 
	
	// GETTERS AND SETTERS
	 
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the preQuests
	 */
	public ArrayList<String> getPreQuests() {
		return preQuests;
	}
	/**
	 * @param preQuests the preQuests to set
	 */
	public void setPreQuests(ArrayList<String> preQuests) {
		this.preQuests = preQuests;
	}
	/**
	 * @return the questDetails
	 */
	public ArrayList<Questtext> getQuestDetails() {
		return questDetails;
	}
	/**
	 * @param questDetails the questDetails to set
	 */
	public void setQuestDetails(ArrayList<Questtext> questDetails) {
		this.questDetails = questDetails;
	}
	/**
	 * @return the needItems
	 */
	public ArrayList<Pair<Integer, String>> getNeedItems() {
		return needItems;
	}
	/**
	 * @param needItems the needItems to set
	 */
	public void setNeedItems(ArrayList<Pair<Integer, String>> needItems) {
		this.needItems = needItems;
	}
	/**
	 * @return the needSkills
	 */
	public ArrayList<Pair<Integer, String>> getNeedSkills() {
		return needSkills;
	}
	/**
	 * @param needSkills the needSkills to set
	 */
	public void setNeedSkills(ArrayList<Pair<Integer, String>> needSkills) {
		this.needSkills = needSkills;
	}
	/**
	 * @return the winchAccess
	 */
	public boolean isWinchAccess() {
		return winchAccess;
	}
	/**
	 * @param winchAccess the winchAccess to set
	 */
	public void setWinchAccess(boolean winchAccess) {
		this.winchAccess = winchAccess;
	}
	/**
	 * @return the rewardMoney
	 */
	public int getRewardMoney() {
		return rewardMoney;
	}
	/**
	 * @param rewardMoney the rewardMoney to set
	 */
	public void setRewardMoney(int rewardMoney) {
		this.rewardMoney = rewardMoney;
	}
	/**
	 * @return the rewardXP
	 */
	public int getRewardXP() {
		return rewardXP;
	}
	/**
	 * @param rewardXP the rewardXP to set
	 */
	public void setRewardXP(int rewardXP) {
		this.rewardXP = rewardXP;
	}
	/**
	 * @return the rewardItems
	 */
	public ArrayList<Pair<Integer, String>> getRewardItems() {
		return rewardItems;
	}
	/**
	 * @param rewardItems the rewardItems to set
	 */
	public void setRewardItems(ArrayList<Pair<Integer, String>> rewardItems) {
		this.rewardItems = rewardItems;
	}
	/**
	 * @return the rewardAssociation
	 */
	public ArrayList<Pair<String, String>> getRewardAssociation() {
		return rewardAssociation;
	}
	/**
	 * @param rewardAssociation the rewardAssociation to set
	 */
	public void setRewardAssociation(ArrayList<Pair<String, String>> rewardAssociation) {
		this.rewardAssociation = rewardAssociation;
	}
	/**
	 * @return the npc
	 */
	public String getNpc() {
		return npc;
	}
	/**
	 * @param npc the npc to set
	 */
	public void setNpc(String npc) {
		this.npc = npc;
	}
	/**
	 * @return the checkup
	 */
	public String getCheckup() {
		return checkup;
	}
	/**
	 * @param checkup the checkup to set
	 */
	public void setCheckup(String checkup) {
		this.checkup = checkup;
	}
	/**
	 * @return the rewardSkills
	 */
	public ArrayList<Pair<Integer, String>> getRewardSkills() {
		return rewardSkills;
	}
	/**
	 * @param rewardSkills the rewardSkills to set
	 */
	public void setRewardSkills(ArrayList<Pair<Integer, String>> rewardSkills) {
		this.rewardSkills = rewardSkills;
	}
	/**
	 * @return the rewardCombatMove
	 */
	public ArrayList<Pair<String, String>> getRewardCombatMove() {
		return rewardCombatMove;
	}
	/**
	 * @param rewardCombatMove the rewardCombatMove to set
	 */
	public void setRewardCombatMove(ArrayList<Pair<String, String>> rewardCombatMove) {
		this.rewardCombatMove = rewardCombatMove;
	}

}
