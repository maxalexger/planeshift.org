package main;

import java.io.File;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import questObjectsNeedReward.Quest;
import questObjectsNeedReward.QuestNeedDetailsOR;
import questObjectsNeedReward.QuestNeedDetailsStandard;
import questObjectsNeedReward.QuestNeedStandard;
import questObjectsNeedReward.QuestNeedWay;
import questObjectsNeedReward.QuestRewardsDetailsSpecificOR;
import questObjectsNeedReward.QuestRewardsDetailsStandard;
import questObjectsNeedReward.QuestRewardsWaySpecific;
import questObjectsNeedReward.QuestRewardsWayStandard;
import questObjectsTextLines.Questtext;
import questObjectsTextLines.QuesttextDiffWays;
import questObjectsTextLines.QuesttextGive;
import questObjectsTextLines.QuesttextInfo;
import questObjectsTextLines.QuesttextNpc;
import questObjectsTextLines.QuesttextNpcInternal;
import questObjectsTextLines.QuesttextNpcMe;
import questObjectsTextLines.QuesttextNpcMy;
import questObjectsTextLines.QuesttextNpcNarrate;
import questObjectsTextLines.QuesttextTime;
import questObjectsTextLines.QuesttextTo;
import questObjectsTextLines.QuesttextWaysHeadline;
import utilities.Pair;

import java.util.Collections;
import java.lang.Exception;
public class PsqToObj {
	
	/**
	 * This function parses all ".psquest" files in the folder utilities.Constants.PATHtoQUESTfiles
	 * and transforms the content into java objects. The files have a specific format (invented
	 * by Huawar Eruera), which needs to be obeyed, otherwise the parser will not work.
	 * 
	 * A detailed description how these files have to look can soon be found somewhere.
	 * @return
	 */
	public ArrayList<Quest> parseAllQuestsDetails() throws Exception {
		// In this Object we save all the quests,
		// which we return in the end
		ArrayList<Quest> quests = new ArrayList<Quest>();
		
		// Delete .DS_Store if it exists
		File DS_Store = new File(utilities.Constants.PATHtoQUESTfiles + ".DS_Store");
		if(DS_Store.exists()) {
			DS_Store.delete();
		}
		
		// Read the filenames in the folder with the Quests
		File folder = new File(utilities.Constants.PATHtoQUESTfiles);
		File[] listOfFiles = folder.listFiles();

		ArrayList<String> filenames = new ArrayList<String>();
		
	    for (int i = 0; i < listOfFiles.length; i++) {
		    	String currFileName = listOfFiles[i].getName();
		        filenames.add(currFileName);
		}
	    
	    Collections.sort(filenames);
		
		// Iterate over all the quests
		for(int i = 0; i < filenames.size(); i++) {
			
			
			// Get the current quest file we are investigating
			String currFileName = filenames.get(i);
			
			String questFileFullPath = utilities.Constants.PATHtoQUESTfiles + currFileName;
			Quest currQuest = parseQuestsDetails(questFileFullPath, currFileName);
			quests.add(currQuest);
			
		}
		
		return quests;
	}
	
	public Quest parseQuestsDetails(String questFileFullPath, String currFileName) throws Exception {
		
		// Delete possible white spaces (user error)
		currFileName = currFileName.trim();
		
		// Check if filename ends with .psquest
		if(! currFileName.endsWith(".psquest")) {
			throw new Exception("File '" + currFileName + "' does not have '.psquest' suffix.");
		}
		
		// Create a new Quest Object
		Quest currQuest = new Quest();
					
		// Here we save all the things that the NPC and you say during the quest
		// It will be added to the quest object later
		ArrayList<Questtext> questDetails = new ArrayList<Questtext>();
		
		ArrayList<String> rawQuestDetails = utilities.Helpfunctions.readLinesOfFile(questFileFullPath);
		
		
			
		// Check that some of the identifier are exactly present 1 time in the file
		boolean hadQuestname = false;
		boolean hadNpcName = false;
		boolean hadCheckup = false;
		boolean hadAuthors = false;
		boolean hadRepeatable = false; // Can be present 0 or 1 time
		
		 /* +++++++++++++++++++++++ PARSE THE LINES INTO OBJECTS +++++++++++++++++++++++ */
		
		for(int j = 0; j < rawQuestDetails.size(); j++) {
			
			String currLine = rawQuestDetails.get(j).trim();
			
			/* +++++++++++++++++++++++ DO NOTHING ++++++++++++++++++++++ */
			
			// SKIP, if the line is empty
			if (currLine.isEmpty()) {
				continue;
			}
			// Comments are just for people who read the .psquest file
			else if(currLine.startsWith("[Comments]") || currLine.startsWith("[Comment]")) {
				continue;
			}
			
			/* ++++++++++++++++++ JUST ALLOWED 1 TIME ++++++++++++++++++ */

			else if(currLine.startsWith("[Questname]")) {
				// Check if there is only one line with [Questname]
				if(hadQuestname) throwItentifierExistsException("[Questname]", currFileName, j, currLine);
				else hadQuestname = true;
				String questname = currLine.substring(11).trim();
				// Check if filename is the same as Questname
				if(! questname.contentEquals(currFileName.substring(0, currFileName.indexOf(".psquest")))) {
					throw new Exception("Questname in '" + currFileName + "' does not equal filename.");
				}
				currQuest.setQuestname(questname);
			} else if(currLine.startsWith("[NPC Name]")) {
				String npcname = currLine.substring(10).trim();
				
				//TODO: checkNpcExistenceOrThrowExc(npcname, currFileName, j, currLine);
				
				currQuest.setNpc(npcname);
				if(hadNpcName) throwItentifierExistsException("[NPC Name]", currFileName, j, currLine);
				else hadNpcName = true;
			} else if(currLine.startsWith("[Checkup]")) {
				String date = currLine.substring(9).trim();
				// Check correct date format
				String regex = "^((19|20)\\d\\d)-(0?[1-9]|1[012])-(0?[1-9]|[12][0-9]|3[01])$";
				if(! Pattern.matches(regex, date)) {
					throw new Exception("In the file \"" + currFileName + 
										"\", the " + ++j + ". line (\"" + currLine + 
										"\"), the date is incorrect. Please provide a date in 'YYYY-MM-DD' format.");
				}
				currQuest.setCheckup(date);
				if(hadCheckup) throwItentifierExistsException("[Checkup]", currFileName, j, currLine);
				else hadCheckup = true;
			} else if(currLine.startsWith("[Authors]")) {
				currQuest.setAuthors(currLine.substring(9).trim());
				if(hadAuthors) throwItentifierExistsException("[Authors]", currFileName, j, currLine);
				else hadAuthors = true;
			} 
			else if(currLine.startsWith("[Repeatable]")) {
				String repeat = currLine.substring(12).trim().toLowerCase();
				if(repeat.equals("yes"))
					currQuest.setRepeatable(true);
				else if(!repeat.equals("no"))
					throw new Exception("In the file \"" + currFileName + 
										"\", the " + ++j + ". line (\"" + currLine + 
										"\"), [Repeatable] must either be yes or no.");
				if(hadRepeatable) throwItentifierExistsException("[Repeatable]", currFileName, j, currLine);
				else hadRepeatable = true;
			}

			/* ++++++++++++++++++ ALLOWED MULTIPLE TIMES ++++++++++++++++++ */
			// Need
			else if (currLine.startsWith("[Need]")) {
				j = readNeeds(rawQuestDetails, currQuest, j, currLine);
				continue;
			} else if(currLine.startsWith("[Info]")) {
				questDetails.add(new QuesttextInfo(currLine.substring(6).trim()));
			} else if(currLine.startsWith("[To]")) {
				String npcname = currLine.substring(4).trim();
				//TODO: checkNpcExistenceOrThrowExc(npcname, currFileName, j, currLine);
				questDetails.add(new QuesttextTo(npcname));					
			} else if(currLine.startsWith("[Give]")) {
				String currLineBackup = currLine;
				try{
					int semiIndex = currLine.indexOf(';');
					String NPCgive = currLine.substring(6, semiIndex).trim();
					currLine = currLine.substring(semiIndex + 1).trim();
					
					ArrayList<Pair<Integer, String>> items = new ArrayList<Pair<Integer, String>>();
					
					while(currLine.contains(";")) {
						int colIndex = currLine.indexOf(',');
						semiIndex = currLine.indexOf(';');
						Integer amount = Integer.parseInt(currLine.substring(0, colIndex).trim());
						String item = currLine.substring(colIndex + 1, semiIndex).trim();
						items.add(new Pair<Integer, String>(amount, item));
						currLine = currLine.substring(semiIndex+1).trim();
					}
					
					int colIndex = currLine.indexOf(',');
					Integer amount = Integer.parseInt(currLine.substring(0, colIndex).trim());
					String item = currLine.substring(colIndex + 1).trim();
					items.add(new Pair<Integer, String>(amount, item));
					questDetails.add(new QuesttextGive("", NPCgive, items));
				} catch (Exception e) {
					throw new StringIndexOutOfBoundsException(
							"\n\tWrong [Give] statement in file \"" + currFileName + 
							"\", the " + ++j + ". line (\"" + currLineBackup + 
							"\").\n\tReminder: The [Give]-Statement has the form " +
							"\"[Give] npc-name; amount, item-name; amount, item-name; ...\"");
				}
				
			} else if(currLine.startsWith("[Time]")) {
				// At the moment like info block: string will be taken and printed
				questDetails.add(new QuesttextTime(currLine.substring(6).trim()));

			/* ##### NPC ##### */
			} else if(currLine.startsWith("[NPC]")) {
				questDetails.add(new QuesttextNpc(currLine.substring(5).trim()));
			} else if(currLine.startsWith("[NPC ME]")) {
				questDetails.add(new QuesttextNpcMe(currLine.substring(8).trim()));
			} else if(currLine.startsWith("[NPC MY]")) {
				questDetails.add(new QuesttextNpcMy(currLine.substring(8).trim()));
			} else if(currLine.startsWith("[NPC Internal]")) {	
				questDetails.add(new QuesttextNpcInternal(currLine.substring(14).trim()));
			} else if(currLine.startsWith("[NPC Narrate]")) {
				questDetails.add(new QuesttextNpcNarrate(currLine.substring(13).trim()));
			} else if(currLine.startsWith("[Possible Ways]")) {
				String ways = currLine.substring(15).trim();
				// Must be a number
				if(! ways.matches("-?(0|[1-9]\\d*)")) {
					throw new Exception(
						"\n\tIn file \"" + currFileName + 
						"\", the " + ++j + ". line (\"" + currLine + 
						"\"), please provide only a number for [Possible Ways].");
				}
				questDetails.add(new QuesttextWaysHeadline(ways));
			} else if(currLine.startsWith("[Way]")) {
				// Can be a number, a list of numbers separated by comma, or "All"
				String way = currLine.substring(5).trim();
				if(way.toLowerCase().equals("all")) {}
				else {
					if(! way.matches("[0-9, /,]+")) {
						throw new Exception(
								"\n\tIn file \"" + currFileName + 
								"\", the " + ++j + ". line (\"" + currLine + 
								"\"), please provide a number, a list of numbers separated by comma, or \"All\".");
					}
				}
				questDetails.add(new QuesttextDiffWays(way));
				
			/* +++++ REWARDS +++++ */
			} else if (currLine.startsWith("[Reward]")) {
					j = readRewards(rawQuestDetails, currQuest, j, currLine);
			
			/*
			 * There shouldn't be anymore cases, except empty lines
			 * But if, it shall throw an exception.
			 */
			} else if(! currLine.equals("")) {
				throw new IllegalArgumentException(
						"In the file \"" + currFileName + 
						"\", the " + ++j + ". line (\"" + currLine + 
						"\") cannot be parsed! Wrong identifier?");
			}
			
			
		}
		
		// Check if we had the descriptive identifier in the file
		if(!hadQuestname || !hadNpcName || !hadCheckup || !hadAuthors)
			throw new Exception("In file '" + currFileName + "' at least one of the obligatory " +
								"identifiers ([Quest Name], [NPC Name], [Checkup], [Authors]) " +
								"is missing.");
		
		currQuest.setQuestDetails(questDetails);
		return currQuest;
		
	}
	
	public void checkNpcExistenceOrThrowExc(String npcname, String currFileName, int j, String currLine) {
		if(! utilities.Helpfunctions.doesNpcExist(npcname)) {
			throw new IllegalArgumentException(
					"In the file \"" + currFileName + 
					"\", in the " + ++j + ". line (\"" + currLine + 
					"\"), the NPC name '" + npcname + "' is incorrect " +
					"or missing in the npc.csv file.");
		}
	}
	
	public void throwItentifierExistsException(String identifier, String currFileName, int j, String currLine) {
		throw new IllegalArgumentException(
				"In the file \"" + currFileName + 
				"\", the " + ++j + ". line (\"" + currLine + 
				"\") cannot be parsed, because the identifier " +
				identifier + " already exists in another line!");
	}
	
	public static int readNeeds(ArrayList<String> rawQuestDetails, Quest currQuest, int j, String currLine) throws Exception {
		
		currLine = currLine.substring(6).trim();

		QuestNeedStandard needObject;
		Pattern needLinePattern = Pattern.compile("\\[Need\\](?!\\s*\\[Way\\s*\\d+\\s*\\])(.*)");
		Matcher needLineMatcher;

		Pattern wayPattern = Pattern.compile("\\s*(\\[Way)\\s*(\\d+)\\s*(\\])(.*)");
		Matcher wayMatcher = wayPattern.matcher(currLine);

		if (wayMatcher.find()) {
			needObject = new QuestNeedWay(Integer.valueOf(wayMatcher.group(2)));
			currLine = wayMatcher.group(4).trim();
			// System.out.println(currLine);
			needLinePattern = Pattern.compile("\\[Need\\]\\s*\\[Way\\s*" + wayMatcher.group(2) + "\\s*\\](.*)");
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
		int otherNeedLines = 0;

		for (int k = j + 1; k < rawQuestDetails.size(); k++) {
			String nextLine = rawQuestDetails.get(k);

			needLineMatcher = needLinePattern.matcher(nextLine);

			if (needLineMatcher.find()) {
				// Add semicolon at end of line if it does not exist
				if (!(currLine.substring(currLine.length() - 1).equals(";"))) {
					currLine = currLine.concat(";");
				}
				currLine = currLine.concat(" ").concat(needLineMatcher.group(1).trim());
				otherNeedLines += 1;
			} else {
				break;
			}
		}
		// We increase j, because we concatenated the following
		// reward lines to the current line.
		j += otherNeedLines;

		// If there is an OR statement, we need to save somewhere, which variant number
		// the next rewards details object will get,
		int or_variant = 1;

		boolean isOrConditionOpen = false;

		QuestNeedDetailsStandard need_details_std = new QuestNeedDetailsStandard();
		QuestNeedDetailsStandard curr_need_details = need_details_std;

		Pattern pattern = Pattern.compile("(.+?)(;|$|(?=\\[OR\\]|\\[OR End\\]|\\[OR Begin\\]))");
		Matcher matcher = pattern.matcher(currLine.trim());

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
			} else if (element.toLowerCase().trim().equals("nothing")) {
				continue;
			} else if(element.startsWith("[Winch]")) {
				if(element.substring(7).trim().toLowerCase().equals("yes"))
					curr_need_details.setWinchaccess(true);	
			} else if (element.startsWith("[Quest]")) {
				element = element.substring(7).trim();
				if(!element.toLowerCase().equals("none")) {
					curr_need_details.addQuest(element);
				}
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

			} else if (element.startsWith("[Special]")) {
				curr_need_details.addSpecial(element.substring(9).trim());
			} else {
				throw new Exception(
						"In file " + currQuest.getQuestname() + ".psquest in line " +
						j + " the Element " + element + " cannot be parsed." +
						"\n\tAllowed tags are: Winch, Quest, Money, Item, Skill, Special");
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


		// Add the need object to the quest object
		needObject.addNeedDetails(curr_need_details);
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
	public static int readRewards(ArrayList<String> rawQuestDetails, Quest currQuest, int j, String currLine) throws Exception {
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

//		if (currLine.contains("OR"))
//			System.out.println(currLine);

		// If there is an OR statement, we need to save somewhere, which variant number
		// the next rewards details object will get,
		int or_variant = 1;

		boolean isOrConditionOpen = false;

		QuestRewardsDetailsStandard rew_details_std = new QuestRewardsDetailsStandard();
		QuestRewardsDetailsStandard curr_rew_details = rew_details_std;

		pattern = Pattern.compile("(.+?)(;|$|(?=\\[OR\\]|\\[OR End\\]|\\[OR Begin\\]))");
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
			} else if(element.toLowerCase().trim().equals("nothing")) {
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
				element = element.substring(7).trim();
				int amount = 1;
				String skill = element;
				if (element.contains(",")) {
					amount = Integer.valueOf(element.substring(0, element.indexOf(",")).trim());
					skill = element.substring(element.indexOf(",") + 1).trim();
				}
				curr_rew_details.addSkillChange(new Pair<Integer, String>(amount, skill));
				
			} else if(element.startsWith("[Combat Move]")) {
				element = element.substring(13);
				String category = element.substring(0, element.indexOf(",")).trim();
				String attack = element.substring(element.indexOf(",")+1).trim();
				
				curr_rew_details.addCombatMove(new Pair<String, String>(category, attack));

			} else if (element.startsWith("[Special]")) {
				curr_rew_details.addSpecial(element.substring(9).trim());
				
			} else {
				throw new Exception(
						"In file " + currQuest.getQuestname() + ".psquest in line " +
						j + " the Element " + element + " cannot be parsed." +
						"\n\tAllowed tags are: Money, XP, Faction, Item, Skill, Combat Move, Special");
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
