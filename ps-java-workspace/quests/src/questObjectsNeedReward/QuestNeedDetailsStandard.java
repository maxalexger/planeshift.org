package questObjectsNeedReward;

import java.util.ArrayList;

import utilities.Pair;

public class QuestNeedDetailsStandard {
	
	private int money;
	
	boolean winchaccess = false;
	
	private ArrayList<String> quests = new ArrayList<String>();
	private ArrayList<Pair<Integer, String>> items = new ArrayList<Pair<Integer, String>>();
	private ArrayList<Pair<Integer, String>> skillLevel = new ArrayList<Pair<Integer, String>>();
	private ArrayList<String> specials = new ArrayList<String>();
	
	public void addQuest(String quest) {
		quests.add(quest);
	}
	public void addItem(Pair<Integer, String> item) {
		items.add(item);
	}
	public void addSkillLevel(Pair<Integer, String> skill) {
		skillLevel.add(skill);
	}
	public void addSpecial(String special) {
		specials.add(special);
	}
	
	
	
	
	/**
	 * @return the specials
	 */
	public ArrayList<String> getSpecials() {
		return specials;
	}
	/**
	 * @param specials the specials to set
	 */
	public void setSpecials(ArrayList<String> specials) {
		this.specials = specials;
	}
	/**
	 * @return the money
	 */
	public int getMoney() {
		return money;
	}
	/**
	 * @param money the money to set
	 */
	public void setMoney(int money) {
		this.money = money;
	}
	/**
	 * @return the winchaccess
	 */
	public boolean isWinchaccess() {
		return winchaccess;
	}
	/**
	 * @param winchaccess the winchaccess to set
	 */
	public void setWinchaccess(boolean winchaccess) {
		this.winchaccess = winchaccess;
	}
	/**
	 * @return the quests
	 */
	public ArrayList<String> getQuests() {
		return quests;
	}
	/**
	 * @param quests the quests to set
	 */
	public void setQuests(ArrayList<String> quests) {
		this.quests = quests;
	}
	/**
	 * @return the items
	 */
	public ArrayList<Pair<Integer, String>> getItems() {
		return items;
	}
	/**
	 * @param items the items to set
	 */
	public void setItems(ArrayList<Pair<Integer, String>> items) {
		this.items = items;
	}
	/**
	 * @return the skillLevel
	 */
	public ArrayList<Pair<Integer, String>> getSkillLevel() {
		return skillLevel;
	}
	/**
	 * @param skillLevel the skillLevel to set
	 */
	public void setSkillLevel(ArrayList<Pair<Integer, String>> skillLevel) {
		this.skillLevel = skillLevel;
	}

}
