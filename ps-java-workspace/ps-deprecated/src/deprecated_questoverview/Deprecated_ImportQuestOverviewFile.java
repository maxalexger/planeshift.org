package deprecated_questoverview;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import questRewardNeedObjects.Quest;
import questRewardNeedObjects.QuestNeedDetailsOR;
import questRewardNeedObjects.QuestNeedDetailsStandard;
import questRewardNeedObjects.QuestNeedStandard;
import questRewardNeedObjects.QuestNeedWay;
import questRewardNeedObjects.QuestRewardsDetailsSpecificOR;
import questRewardNeedObjects.QuestRewardsDetailsStandard;
import questRewardNeedObjects.QuestRewardsWaySpecific;
import questRewardNeedObjects.QuestRewardsWayStandard;
import utilities.Pair;

public class Deprecated_ImportQuestOverviewFile {

	public static void main(String[] args) {

		ArrayList<Quest> quests = new ArrayList<Quest>();

		String qOverviewFilePath = utilities.Constants.PATHforSQLoutput + "/new-quest-overview.psqview";

		ArrayList<String> rawQuestDetails = utilities.Helpfunctions.readLinesOfFile(qOverviewFilePath);

		Quest currQuest = new Quest();

		/* ##### BEGIN: Parse the Lines into Objects. ##### */
		for (int j = 0; j < rawQuestDetails.size(); j++) {

			String currLine = rawQuestDetails.get(j).trim();

			// SKIP, if the line is empty
			if (currLine.isEmpty())
				continue;

			// SKIP, if the line is a comment
			if (currLine.startsWith("//"))
				continue;

			// QUEST line
			Pattern pattern = Pattern.compile("(\\[Quest\\])(.*?)(,|;)(.*?)(,|;|$)");
			Matcher matcher = pattern.matcher(currLine);
			if (matcher.find()) {
				quests.add(currQuest); // Save old quest into array
				currQuest = new Quest(); // Create new quest

				currQuest.setNpc(matcher.group(2).trim()); // Set npc
				currQuest.setQuestname(matcher.group(4).trim()); // Set quest
				continue;
			}

			// Need
			if (currLine.startsWith("[Need]")) {
				j = readNeeds(rawQuestDetails, currQuest, j, currLine);
				continue;
			}

			// Reward
			if (currLine.startsWith("[Reward]")) {
				j = readRewards(rawQuestDetails, currQuest, j, currLine);
				continue;
			}

			// Repeatable
			if (currLine.startsWith("[Repeatable]")) {
				if (currLine.toLowerCase().contains("true")) {
					currQuest.setRepeatable(true);
				}
				continue;
			}
		}

	}
	
	public static int readNeeds(ArrayList<String> rawQuestDetails, Quest currQuest, int j, String currLine) {
	
		currLine = currLine.substring(6).trim();

		QuestNeedStandard needObject;
		Pattern rewardLinePattern = Pattern.compile("\\[Need\\](?!\\s*\\[Way\\s*\\d+\\s*\\])(.*)");
		Matcher rewardLineMatcher;

		Pattern pattern = Pattern.compile("\\s*(\\[Way)\\s*(\\d+)\\s*(\\])(.*)");
		Matcher matcher = pattern.matcher(currLine);

		if (matcher.find()) {
			needObject = new QuestNeedWay(Integer.valueOf(matcher.group(2)));
			currLine = matcher.group(4).trim();
			// System.out.println(currLine);
			rewardLinePattern = Pattern.compile("\\[Need\\]\\s*\\[Way\\s*" + matcher.group(2) + "\\s*\\](.*)");
		} else {
			needObject = new QuestNeedStandard();
		}

		/*
		 * It is possible to write the rewards in multiple lines in the file. Important
		 * is that every line has the reward tag ([Reward]) at the beginning.
		 * 
		 * Here, we connect all these lines to one string, so it is easier to search for
		 * patterns, in case a pattern spans over multiple lines in the file (for
		 * example the [OR Begin], [OR], [OR End] pattern).
		 * 
		 * We make sure that each reward line is concatenated with an semicolon.
		 */
		int otherRewardLines = 0;

		for (int k = j + 1; k < rawQuestDetails.size(); k++) {
			String nextLine = rawQuestDetails.get(k);

			rewardLineMatcher = rewardLinePattern.matcher(nextLine);

			if (rewardLineMatcher.find()) {
				if (!(currLine.substring(currLine.length() - 1).equals(";"))) {
					currLine = currLine.concat(";");
				}
				currLine = currLine.concat(" ").concat(rewardLineMatcher.group(1).trim());
				otherRewardLines += 1;
			} else {
				break;
			}
		}
		// We increase j, because we concatenated the following
		// reward lines to the current line.
		j += otherRewardLines;

		// If there is an OR statement, we need to save somewhere, which variant number
		// the next rewards details object will get,
		int or_variant = 1;

		boolean isOrConditionOpen = false;

		QuestNeedDetailsStandard need_details_std = new QuestNeedDetailsStandard();
		QuestNeedDetailsStandard curr_need_details = need_details_std;

		pattern = Pattern.compile("(.+?)(;|$|(?=\\[OR\\]|\\[OR End\\]))");
		matcher = pattern.matcher(currLine.trim());

		while (matcher.find()) {
			String element = matcher.group(1).trim();

			// Get correct details object
			if (element.startsWith("[OR Begin]")) {
				need_details_std = curr_need_details;
				curr_need_details = new QuestNeedDetailsOR(or_variant);
				or_variant += 1;
				element = element.substring(10).trim();
				isOrConditionOpen = true;
			} else if (element.startsWith("[OR]")) {
				needObject.addNeedDetails(curr_need_details);
				curr_need_details = new QuestNeedDetailsOR(or_variant);
				or_variant += 1;
				element = element.substring(4).trim();
			} else if (element.startsWith("[OR End]")) {
				needObject.addNeedDetails(curr_need_details);
				curr_need_details = need_details_std;
				or_variant += 1;
				element = element.substring(8).trim();
				isOrConditionOpen = false;
			}

			if (element.isEmpty()) { // Possible e.g. if [Or End] is the whole element
				continue;
			} else if (element.startsWith("[Quest]")) {
				element = element.substring(7).trim();
				curr_need_details.addQuest(element);

			} else if (element.startsWith("[Money]")) {
				element = element.substring(7).trim();
				int money = Integer.valueOf(element);
				curr_need_details.setMoney(money);

			} else if (element.startsWith("[Item]")) {
				element = element.substring(6);
				int amount = 1;
				String item = element;
				if (element.contains(",")) {
					amount = Integer.valueOf(element.substring(0, element.indexOf(",")).trim());
					item = element.substring(element.indexOf(",") + 1).trim();
				}
				curr_need_details.addItem(new Pair<Integer, String>(amount, item));

			} else if (element.startsWith("[Skill]")) {
				element = element.substring(7);
				int amount = 1;
				String skill = element;
				if (element.contains(",")) {
					amount = Integer.valueOf(element.substring(0, element.indexOf(",")).trim());
					skill = element.substring(element.indexOf(",") + 1).trim();
				}
				curr_need_details.addSkillLevel(new Pair<Integer, String>(amount, skill));

			} else {
				System.out.println("ERROR: The element '" + element
						+ "' in the reward section has no tag ('[..]') at the beginning.");
			}

			if (element.endsWith("[OR End]")) {
				needObject.addNeedDetails(curr_need_details);
				curr_need_details = need_details_std;
				or_variant += 1;
				isOrConditionOpen = false;
			}
		}

		if (isOrConditionOpen) {
			needObject.addNeedDetails(curr_need_details);
			curr_need_details = need_details_std;
		}

		needObject.addNeedDetails(curr_need_details);

		// Add the rewards object to the quest object
		currQuest.addNeed(needObject);

		return j;
	}

	/**
	 * 
	 * @param rawQuestDetails
	 * @param currQuest
	 * @param j
	 * @param currLine
	 * @return The new index for the for-loop of the lines of the quest file. We
	 *         read all the reward lines of the quest at once.
	 */
	public static int readRewards(ArrayList<String> rawQuestDetails, Quest currQuest, int j, String currLine) {
		currLine = currLine.substring(8).trim();

		QuestRewardsWayStandard rewardObject;
		Pattern rewardLinePattern = Pattern.compile("\\[Reward\\](?!\\s*\\[Way\\s*\\d+\\s*\\])(.*)");
		Matcher rewardLineMatcher;

		Pattern pattern = Pattern.compile("\\s*(\\[Way)\\s*(\\d+)\\s*(\\])(.*)");
		Matcher matcher = pattern.matcher(currLine);

		if (matcher.find()) {
			rewardObject = new QuestRewardsWaySpecific(Integer.valueOf(matcher.group(2)));
			currLine = matcher.group(4).trim();
			// System.out.println(currLine);
			rewardLinePattern = Pattern.compile("\\[Reward\\]\\s*\\[Way\\s*" + matcher.group(2) + "\\s*\\](.*)");
		} else {
			rewardObject = new QuestRewardsWayStandard();
		}

		/*
		 * It is possible to write the rewards in multiple lines in the file. Important
		 * is that every line has the reward tag ([Reward]) at the beginning.
		 * 
		 * Here, we connect all these lines to one string, so it is easier to search for
		 * patterns, in case a pattern spans over multiple lines in the file (for
		 * example the [OR Begin], [OR], [OR End] pattern).
		 * 
		 * We make sure that each reward line is concatenated with an semicolon.
		 */
		int otherRewardLines = 0;

		for (int k = j + 1; k < rawQuestDetails.size(); k++) {
			String nextLine = rawQuestDetails.get(k);

			rewardLineMatcher = rewardLinePattern.matcher(nextLine);

			if (rewardLineMatcher.find()) {
				if (!(currLine.substring(currLine.length() - 1).equals(";"))) {
					currLine = currLine.concat(";");
				}
				currLine = currLine.concat(" ").concat(rewardLineMatcher.group(1).trim());
				otherRewardLines += 1;
			} else {
				break;
			}
		}
		// We increase j, because we concatenated the following
		// reward lines to the current line.
		j += otherRewardLines;

		if (currLine.contains("OR"))
			System.out.println(currLine);

		// If there is an OR statement, we need to save somewhere, which variant number
		// the next rewards details object will get,
		int or_variant = 1;

		boolean isOrConditionOpen = false;

		QuestRewardsDetailsStandard rew_details_std = new QuestRewardsDetailsStandard();
		QuestRewardsDetailsStandard curr_rew_details = rew_details_std;

		pattern = Pattern.compile("(.+?)(;|$|(?=\\[OR\\]|\\[OR End\\]))");
		matcher = pattern.matcher(currLine.trim());

		while (matcher.find()) {
			String element = matcher.group(1).trim();

			// Get correct details object
			if (element.startsWith("[OR Begin]")) {
				rew_details_std = curr_rew_details;
				curr_rew_details = new QuestRewardsDetailsSpecificOR(or_variant);
				or_variant += 1;
				element = element.substring(10).trim();
				isOrConditionOpen = true;
			} else if (element.startsWith("[OR]")) {
				rewardObject.addRewardDetails(curr_rew_details);
				curr_rew_details = new QuestRewardsDetailsSpecificOR(or_variant);
				or_variant += 1;
				element = element.substring(4).trim();
			} else if (element.startsWith("[OR End]")) {
				rewardObject.addRewardDetails(curr_rew_details);
				curr_rew_details = rew_details_std;
				or_variant += 1;
				element = element.substring(8).trim();
				isOrConditionOpen = false;
			}

			if (element.isEmpty()) { // Possible e.g. if [Or End] is the whole element
				continue;
			} else if (element.startsWith("[Money]")) {
				element = element.substring(7).trim();
				int money = Integer.valueOf(element);
				curr_rew_details.setMoney(money);

			} else if (element.startsWith("[XP]")) {
				element = element.substring(4).trim();
				int xp = Integer.valueOf(element);
				curr_rew_details.setXp(xp);

			} else if (element.startsWith("[Faction]")) {
				element = element.substring(9).trim();
				int amount = 0;
				String faction = element;
				if (element.contains(",")) {
					amount = Integer.valueOf(element.substring(0, element.indexOf(",")).trim());
					faction = element.substring(element.indexOf(",") + 1).trim();
				}
				curr_rew_details.addFaction(new Pair<Integer, String>(amount, faction));

			} else if (element.startsWith("[Item]")) {
				element = element.substring(6);
				int amount = 1;
				String item = element;
				if (element.contains(",")) {
					amount = Integer.valueOf(element.substring(0, element.indexOf(",")).trim());
					item = element.substring(element.indexOf(",") + 1).trim();
				}
				curr_rew_details.addItem(new Pair<Integer, String>(amount, item));

			} else if (element.startsWith("[Skill]")) {
				element = element.substring(7);
				int amount = 1;
				String skill = element;
				if (element.contains(",")) {
					amount = Integer.valueOf(element.substring(0, element.indexOf(",")).trim());
					skill = element.substring(element.indexOf(",") + 1).trim();
				}
				curr_rew_details.addSkillChange(new Pair<Integer, String>(amount, skill));

			} else {
				System.out.println("ERROR: The element '" + element
						+ "' in the reward section has no tag ('[..]') at the beginning.");
			}

			if (element.endsWith("[OR End]")) {
				rewardObject.addRewardDetails(curr_rew_details);
				curr_rew_details = rew_details_std;
				or_variant += 1;
				isOrConditionOpen = false;
			}
		}

		if (isOrConditionOpen) {
			rewardObject.addRewardDetails(curr_rew_details);
			curr_rew_details = rew_details_std;
		}

		rewardObject.addRewardDetails(curr_rew_details);

		// Add the rewards object to the quest object
		currQuest.addReward(rewardObject);

		return j;

	}

}
