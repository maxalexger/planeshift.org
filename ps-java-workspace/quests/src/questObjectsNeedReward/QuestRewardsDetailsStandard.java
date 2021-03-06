package questObjectsNeedReward;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import utilities.Pair;

public class QuestRewardsDetailsStandard {
	
	private int xp;
	private int money;
	
	private boolean repeatable = false;
	
	// Combat Moves: first: category, second: special attack
	private ArrayList<Pair<String, String>> combatMoves = new ArrayList<Pair<String, String>>();
	private ArrayList<Pair<Integer, String>> items = new ArrayList<Pair<Integer, String>>();
	private ArrayList<Pair<Integer, String>> factions = new ArrayList<Pair<Integer, String>>();
	private ArrayList<Pair<Integer, String>> skillChange = new ArrayList<Pair<Integer, String>>();
	private ArrayList<String> specials = new ArrayList<String>();
	
	
	public void addItem(Pair<Integer, String> item) {
		items.add(item);
	}
	public void addFaction(Pair<Integer, String> faction) {
		factions.add(faction);
	}
	public void addSkillChange(Pair<Integer, String> skill) {
		skillChange.add(skill);
	}
	public void addSpecial(String special) {
		specials.add(special);
	}
	public void addCombatMove(Pair<String, String> combatMove) {
		combatMoves.add(combatMove);
	}
	
	
	
	
	
	/**
	 * @return the combatMoves
	 */
	public ArrayList<Pair<String, String>> getCombatMoves() {
		return combatMoves;
	}
	/**
	 * @param combatMoves the combatMoves to set
	 */
	public void setCombatMoves(ArrayList<Pair<String, String>> combatMoves) {
		this.combatMoves = combatMoves;
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
	 * @return the xp
	 */
	public int getXp() {
		return xp;
	}
	/**
	 * @param xp the xp to set
	 */
	public void setXp(int xp) {
		this.xp = xp;
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
	 * @return the repeatable
	 */
	public boolean isRepeatable() {
		return repeatable;
	}
	/**
	 * @param repeatable the repeatable to set
	 */
	public void setRepeatable(boolean repeatable) {
		this.repeatable = repeatable;
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
	 * @return the factions
	 */
	public ArrayList<Pair<Integer, String>> getFactions() {
		return factions;
	}
	/**
	 * @param factions the factions to set
	 */
	public void setFactions(ArrayList<Pair<Integer, String>> factions) {
		this.factions = factions;
	}
	/**
	 * @return the skillChange
	 */
	public ArrayList<Pair<Integer, String>> getSkillChange() {
		return skillChange;
	}
	/**
	 * @param skillChange the skillChange to set
	 */
	public void setSkillChange(ArrayList<Pair<Integer, String>> skillChange) {
		this.skillChange = skillChange;
	}
	
	

}
