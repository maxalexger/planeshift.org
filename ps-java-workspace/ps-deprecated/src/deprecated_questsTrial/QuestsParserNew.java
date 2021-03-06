package deprecated_questsTrial;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import questObjectsTextLines.Questtext;
import questObjectsTextLines.QuesttextGive;
import questObjectsTextLines.QuesttextInfo;
import questObjectsTextLines.QuesttextNpc;
import questObjectsTextLines.QuesttextNpcInternal;
import questObjectsTextLines.QuesttextNpcMe;
import questObjectsTextLines.QuesttextNpcMy;
import questObjectsTextLines.QuesttextNpcNarrate;
import questObjectsTextLines.QuesttextTime;
import questObjectsTextLines.QuesttextTo;
import utilities.Pair;


public class QuestsParserNew {
	public ArrayList<QuestTrial> parseQuestsDetails() {
		
		// In this Object we save all the quests,
		// which we return in the end
		ArrayList<QuestTrial> quests = new ArrayList<QuestTrial>();
		
		// Delete .DS_Store if it exists
		File DS_Store = new File(utilities.Constants.PATHtoTRIALquestFILES + ".DS_Store");
		if(DS_Store.exists()) {
			DS_Store.delete();
		}
		
		// Read the filenames in the folder with the Quests
		File folder = new File(utilities.Constants.PATHtoTRIALquestFILES);
		File[] listOfFiles = folder.listFiles();

		ArrayList<String> filenames = new ArrayList<String>();
		
	    for (int i = 0; i < listOfFiles.length; i++) {
		    	String currFileName = listOfFiles[i].getName();
		        filenames.add(currFileName);
		}
		
		// Iterate over all the quests
		for(int i = 0; i < filenames.size(); i++) {
			
			
			// Get the current quest file we are investigating
			String currFileName = filenames.get(i);

			// Create a new Quest Object
			QuestTrial currQuest = new QuestTrial();
						
			// Here we save all the things that the NPC and you say during the quest
			// It will be added to the quest object later
			ArrayList<Questtext> questDetails = new ArrayList<Questtext>();
			
			
			/* ##### BEGIN: Extract the pure lines from input file ##### */
			ArrayList<String> rawQuestDetails = new ArrayList<String>();
			File file = new File(utilities.Constants.PATHtoTRIALquestFILES + currFileName);

			BufferedReader reader = null;
			try {
				reader = new BufferedReader(new FileReader(file));
				String text = null;
				
				while ((text = reader.readLine()) != null) {
					rawQuestDetails.add(text);
					//System.out.println(text);
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if (reader != null) {
						reader.close();
					}
				} catch (IOException e) {}
			}
			/* ##### END: Extract the pure lines from input file ##### */

			
			/* 
			 * ##### BEGIN: Parse the Lines into Objects. #####
			 */
			for(int j = 0; j < rawQuestDetails.size(); j++) {
				
				String currLine= rawQuestDetails.get(j);
				
				if(currLine.startsWith("[Questname]")) {
					currQuest.setName(currLine.substring(12));
					//System.out.println(currLine.substring(12));
				} else if(currLine.startsWith("[NPC Name]")) {
					currQuest.setNpc(currLine.substring(11));
					//System.out.println(currLine.substring(11));
				} else if(currLine.startsWith("[Checkup]")) {
					if(currLine.length() > 10) {
						currQuest.setCheckup(currLine.substring(10));
//						System.out.println(currLine.substring(10));
					}
				} else if(currLine.startsWith("[Prequests]")) {
					if(currLine.length() > 12) {
						currLine = currLine.substring(12);
						ArrayList<String> preQuests = new ArrayList<String>();
						while(currLine.contains(";")) {
							int semiIndex = currLine.indexOf(';');
							preQuests.add(currLine.substring(0, semiIndex));
							//System.out.println(currLine.substring(0, semiIndex));
							
							currLine = currLine.substring(semiIndex+2);
						}
						preQuests.add(currLine);
						//System.out.println(currLine);
						currQuest.setPreQuests(preQuests);
					}
			
				} else if(currLine.startsWith("[Need Items]")) {
					if(currLine.length() > 13) {
						currLine = currLine.substring(13);
						ArrayList<Pair<Integer, String>> needItems = new ArrayList<Pair<Integer, String>>();
						while(currLine.contains(";")) {
							int colIndex = currLine.indexOf(',');
							int semiIndex = currLine.indexOf(';');
							Integer amount = Integer.parseInt(currLine.substring(0, colIndex));
							String item = currLine.substring(colIndex + 2, semiIndex);
							needItems.add(new Pair<Integer, String>(amount, item));
							//System.out.println("Amount: \"" + amount + "\", Item: \"" + item + "\"");
							
							currLine = currLine.substring(semiIndex+2);
						}
						int colIndex = currLine.indexOf(',');
						Integer amount = Integer.parseInt(currLine.substring(0, colIndex));
						String item = currLine.substring(colIndex + 2);
						needItems.add(new Pair<Integer, String>(amount, item));
						//System.out.println("Amount: \"" + amount + "\", Item: \"" + item + "\"");
						currQuest.setNeedItems(needItems);
					}
					
				} else if(currLine.startsWith("[Need Skills]")) {
					if(currLine.length() > 14) {
						currLine = currLine.substring(14);
						ArrayList<Pair<Integer, String>> needSkills = new ArrayList<Pair<Integer, String>>();
						while(currLine.contains(";")) {
							int colIndex = currLine.indexOf(',');
							int semiIndex = currLine.indexOf(';');
							Integer amount = Integer.parseInt(currLine.substring(0, colIndex));
							String skill = currLine.substring(colIndex + 2, semiIndex);
							needSkills.add(new Pair<Integer, String>(amount, skill));
							//System.out.println("Amount: \"" + amount + "\", skill: \"" + skill + "\"");
							
							currLine = currLine.substring(semiIndex+2);
						}
						int colIndex = currLine.indexOf(',');
						Integer amount = Integer.parseInt(currLine.substring(0, colIndex));
						String skill = currLine.substring(colIndex + 2);
						needSkills.add(new Pair<Integer, String>(amount, skill));
						//System.out.println("Amount: \"" + amount + "\", skill: \"" + skill + "\"");
						currQuest.setNeedSkills(needSkills);
					}
					
				} else if(currLine.startsWith("[Need Winch]")) {
					if(currLine.startsWith("[Need Winch] Yes")) {
						currQuest.setWinchAccess(true);
					}
					
				} else if(currLine.startsWith("[Info]")) {
					questDetails.add(new QuesttextInfo(currLine.substring(7)));
					//System.out.println(currLine.substring(7));
				} else if(currLine.startsWith("[To]")) {
					questDetails.add(new QuesttextTo(currLine.substring(5)));
					//System.out.println(currLine.substring(5));
					
				} else if(currLine.startsWith("[Give]")) {
					try{
						int semiIndex = currLine.indexOf(';');
						String NPCgive = currLine.substring(7, semiIndex);
						currLine = currLine.substring(semiIndex + 2);
						
						ArrayList<Pair<Integer, String>> items = new ArrayList<Pair<Integer, String>>();
						
						while(currLine.contains(";")) {
							int colIndex = currLine.indexOf(',');
							semiIndex = currLine.indexOf(';');
							Integer amount = Integer.parseInt(currLine.substring(0, colIndex));
							String item = currLine.substring(colIndex + 2, semiIndex);
							items.add(new Pair<Integer, String>(amount, item));
	//						System.out.println("NPC: \"" + NPCgive + "\", Amount: \"" + amount + "\", item: \"" + item + "\"");
							
							currLine = currLine.substring(semiIndex+2);
						}
						int colIndex = currLine.indexOf(',');
						Integer amount = Integer.parseInt(currLine.substring(0, colIndex));
						String item = currLine.substring(colIndex + 2);
						items.add(new Pair<Integer, String>(amount, item));
	//					System.out.println("NPC: \"" + NPCgive + "\", Amount: \"" + amount + "\", item: \"" + item + "\"");
						questDetails.add(new QuesttextGive("", NPCgive, items));
					} catch (StringIndexOutOfBoundsException e) {
						throw new StringIndexOutOfBoundsException(
								"In the file \"" + currFileName + 
								"\", the " + ++j + ". line (\"" + currLine + 
								"\").\n\tReminder: The [Give]-Statement has the form \"NPC; amount, name; amount,name ...\"");
					}
					
				} else if(currLine.startsWith("[Time]")) {
					questDetails.add(new QuesttextTime(currLine.substring(7)));
//					System.out.println(currLine.substring(7));

				/* ##### NPC ##### */
				} else if(currLine.startsWith("[NPC]")) {
					questDetails.add(new QuesttextNpc(currLine.substring(6)));
//					System.out.println("\"" + currLine.substring(6) + "\"");
					
				} else if(currLine.startsWith("[NPC ME]")) {
					questDetails.add(new QuesttextNpcMe(currLine.substring(9)));
//					System.out.println("\"" + currLine.substring(9) + "\"");
				} else if(currLine.startsWith("[NPC MY]")) {
					questDetails.add(new QuesttextNpcMy(currLine.substring(9)));
//					System.out.println("\"" + currLine.substring(9) + "\"");
				} else if(currLine.startsWith("[NPC Internal]")) {	
					questDetails.add(new QuesttextNpcInternal(currLine.substring(15)));
//					System.out.println("\"" + currLine.substring(15) + "\"");
				} else if(currLine.startsWith("[NPC Narrate]")) {
					questDetails.add(new QuesttextNpcNarrate(currLine.substring(14)));
//					System.out.println("\"" + currLine.substring(14) + "\"");
					
				/* ##### REWARDS ##### */
				} else if(currLine.startsWith("[Reward Money]")) {
					currQuest.setRewardMoney(Integer.parseInt(currLine.substring(15)));
//					System.out.println("Reward Money: \"" + currLine.substring(15) + "\"");
					
				} else if(currLine.startsWith("[Reward XP]")) {
					currQuest.setRewardXP(Integer.parseInt(currLine.substring(12)));
//					System.out.println("Reward XP: \"" + currLine.substring(12) + "\"");
				} else if(currLine.startsWith("[Reward Combat Move]")) {
					if(currLine.length() > 21) {
						currLine = currLine.substring(21);
						ArrayList<Pair<String, String>> rewardCombatMove = new ArrayList<Pair<String, String>>();
						while(currLine.contains(";")) {
							int colIndex = currLine.indexOf(',');
							int semiIndex = currLine.indexOf(';');
							String combatMoveType = currLine.substring(0, colIndex);
							String combatMove = currLine.substring(colIndex + 2, semiIndex);
							rewardCombatMove.add(new Pair<String, String>(combatMoveType, combatMove));
	//						System.out.println(rewardCombatMove: \"" + combatMove + "\"");
							
							currLine = currLine.substring(semiIndex+2);
						}
						int colIndex = currLine.indexOf(',');
						String combatMoveType = currLine.substring(0, colIndex);
						String combatMove = currLine.substring(colIndex + 2);
						rewardCombatMove.add(new Pair<String, String>(combatMoveType, combatMove));
//						System.out.println("rewardCombatMove: \"" + combatMove + "\", Type: \"" + combatMoveType + "\"");
						currQuest.setRewardCombatMove(rewardCombatMove);
					}
					
				} else if(currLine.startsWith("[Reward Skills]")) {
					if(currLine.length() > 16) {
						currLine = currLine.substring(16);
						ArrayList<Pair<Integer, String>> rewardSkills = new ArrayList<Pair<Integer, String>>();
						while(currLine.contains(";")) {
							int colIndex = currLine.indexOf(',');
							int semiIndex = currLine.indexOf(';');
							Integer amount = Integer.parseInt(currLine.substring(0, colIndex));
							String item = currLine.substring(colIndex + 2, semiIndex);
							rewardSkills.add(new Pair<Integer, String>(amount, item));
	//						System.out.println("Amount: \"" + amount + "\", Item: \"" + item + "\"");
							
							currLine = currLine.substring(semiIndex+2);
						}
						int colIndex = currLine.indexOf(',');
						Integer amount = Integer.parseInt(currLine.substring(0, colIndex));
						String item = currLine.substring(colIndex + 2);
						rewardSkills.add(new Pair<Integer, String>(amount, item));
	//					System.out.println("Amount: \"" + amount + "\", Item: \"" + item + "\"");
						currQuest.setRewardSkills(rewardSkills);
					}
					
				} else if(currLine.startsWith("[Reward Items]")) {
					if(currLine.length() > 15) {
						currLine = currLine.substring(15);
						ArrayList<Pair<Integer, String>> rewardItems = new ArrayList<Pair<Integer, String>>();
						while(currLine.contains(";")) {
							int colIndex = currLine.indexOf(',');
							int semiIndex = currLine.indexOf(';');
							Integer amount = Integer.parseInt(currLine.substring(0, colIndex));
							String item = currLine.substring(colIndex + 2, semiIndex);
							rewardItems.add(new Pair<Integer, String>(amount, item));
	//						System.out.println("Amount: \"" + amount + "\", Item: \"" + item + "\"");
							
							currLine = currLine.substring(semiIndex+2);
						}
						int colIndex = currLine.indexOf(',');
						Integer amount = Integer.parseInt(currLine.substring(0, colIndex));
						String item = currLine.substring(colIndex + 2);
						rewardItems.add(new Pair<Integer, String>(amount, item));
	//					System.out.println("Amount: \"" + amount + "\", Item: \"" + item + "\"");
						currQuest.setRewardItems(rewardItems);
					}
					
				} else if(currLine.startsWith("[Reward Associations]")) {
					if(currLine.length() > 22) {
						currLine = currLine.substring(22);
						ArrayList<Pair<String, String>> rewardItems = new ArrayList<Pair<String, String>>();
						while(currLine.contains(";")) {
							int colIndex = currLine.indexOf(',');
							int semiIndex = currLine.indexOf(';');
							String amount = currLine.substring(0, colIndex);
							if(amount.replaceAll("\\s+","").equals("")) {
								throw new IllegalArgumentException(
										"In the file \"" + currFileName + 
										"\", the " + ++j + ". line (\"" + currLine + 
										"\") cannot be parsed!\n\tMissing number or +/- before the ','.");
							}
							String item = currLine.substring(colIndex + 2, semiIndex);
							rewardItems.add(new Pair<String, String>(amount, item));
	//						System.out.println("Amount: \"" + amount + "\", Association: \"" + item + "\"");
							
							currLine = currLine.substring(semiIndex+2);
						}
						int colIndex = currLine.indexOf(',');
						String amount = currLine.substring(0, colIndex);
//						System.out.println(amount);
						if(amount.replaceAll("\\s+","").equals("")) {
							throw new IllegalArgumentException(
									"In the file \"" + currFileName + 
									"\", the " + ++j + ". line (\"" + currLine + 
									"\") cannot be parsed!\n\tMissing number or +/- before the ','.");
						}
						String item = currLine.substring(colIndex + 2);
						rewardItems.add(new Pair<String, String>(amount, item));
	//					System.out.println("Amount: \"" + amount + "\", Association: \"" + item + "\"");
						currQuest.setRewardAssociation(rewardItems);
					}
				
				/*
				 * There shouldn't be anymore cases, except empty lines
				 * But if, it shall throw an exception.
				 */
				} else if(! currLine.equals("")) {
					throw new IllegalArgumentException(
							"In the file \"" + currFileName + 
							"\", the " + ++j + ". line (\"" + currLine + 
							"\") cannot be parsed!");
				}
				
				
			}
			
			currQuest.setQuestDetails(questDetails);
			quests.add(currQuest);
		
			
			
			
		}
		
		return quests;
	}


}
