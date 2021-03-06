package psqExecutables;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import utilities.Pair;

/*
 * The PSExtended extension has the option to log the quests
 * which you do in game. This class converts these documents
 * to .psquest documents.
 * 
 * If a .psquest with the same name already exists, it is NOT
 * overwritten.
 * 
 * Also you can add other informations from the ps log files
 * (or by hand), which will be transformed into the .psquest
 * document as well. 
 * 
 * If something cannot be parsed, there should be an error
 * message.
 */

public class PSExtToPsq {
	
	static String directory = utilities.Constants.PATHtoDATA + "/convert-psextended-to-psquest/";


	public static void main(String[] args) {	
		
		ArrayList<String> filenames = readFileNames();
	    
		// Iterate over all raw .txt files
	    for (int i = 0; i < filenames.size(); i++) {
	    		// Only create .psquest file, if not already exists
	    		if(!(new File(directory + filenames.get(i).substring(0, filenames.get(i).length()-4) + ".psq")).exists()) {
	    			parsePSExtFile(filenames.get(i));
	    		}
	    }
		
		
		System.out.println("PS Extended Quest details converted to .psq!");
	}
		

	// #############################################
	
	public static ArrayList<String> readFileNames() {
		// Delete .DS_Store if it exists
		File DS_Store = new File(directory + ".DS_Store");
		if(DS_Store.exists()) {
			DS_Store.delete();
		}
		
		// Read the filenames in the folder with the raw details
		File folder = new File(directory);
		File[] listOfFiles = folder.listFiles();

		ArrayList<String> filenames = new ArrayList<String>();
		
	    for (int i = 0; i < listOfFiles.length; i++) {
		    	String currFileName = listOfFiles[i].getName();
		    	// Don't add the readme file
		    	if(currFileName.toLowerCase().equals("readme.txt")) {
		    		continue;
		    	// Don't add .psquest files that have already been converted
		    	} else if(currFileName.toLowerCase().endsWith(".psquest")) {
		    		continue;
		    	} else {
		    		filenames.add(currFileName);
		    	}
		}
	    
	    return filenames;
	}
	
	// #############################################
		
	public static void parsePSExtFile(String filename) {
		
		String filepath = directory + filename;
		ArrayList<String> lines = utilities.Helpfunctions.readLinesOfFile(filepath);
		ArrayList<String> linesNew = new ArrayList<String>();
		
		// Delete empty rows
		for(int l = 0; l < lines.size(); l++) {
			String currLine = lines.get(l);
			if(currLine.replaceAll("\\s+", "").equals("")) {
				// do nothing -> delete the row
			} else {
				linesNew.add(currLine);
			}
		}
		lines = linesNew;
		linesNew = new ArrayList<String>();
		
//		// Add empty rows [Give], [Time], [To]
//		for(int l = 0; l < lines.size(); l++) {
//			String currLine = lines.get(l);
//			if(currLine.contains("[Give]") || currLine.contains("[Time]") || currLine.contains("[To]") || currLine.contains("[Info]")) {
//				linesNew.add("");
//				linesNew.add(currLine);
//				linesNew.add("");
//			} else {
//				linesNew.add(currLine);
//			}
//		}
//		lines = linesNew;
//		linesNew = new ArrayList<String>();
		
		/*
		 * QUEST NPC
		 * The first NPC which appears will be 
		 * assigned as the Quest NPC
		 */
		String questNPC = "";
		for(int l = 0; l < lines.size(); l++) {
			String currLine = lines.get(l);
			if(currLine.matches("^[a-zA-Z\\s]*says:.*")) {
				questNPC = currLine.substring(0, currLine.indexOf("says:")).trim();
//				System.out.println(questNPC);
				break;
			}
		}
		

		
		/*
		 * INSERT [To] STATEMENT
		 * Herefor we need the timestamps at the
		 * bigging of each line, why it is im-
		 * portant to delete it after this step
		 */
		String currNPC =  "";
		String nextNPC = "";
		int nextNpcLine = 0;
			
		// Change the spoken lines
		for(int l = 0; l < lines.size(); l++) {
			String currLine = lines.get(l);
			// Change the lines your character says
			if(currLine.startsWith("You say: ")) {
				int endIndex = currLine.indexOf(" say: ") + 6;
				linesNew.add("[NPC Internal] " + currLine.substring(endIndex, currLine.length()));
			// Change the lines the npc says
			} else if(currLine.matches("^[a-zA-Z\\s]*says:.*")) {
				int spaceIndex = currLine.indexOf(" ");
				int endIndex = currLine.indexOf(" says: ") + 5;
				linesNew.add("[NPC] " + currLine.substring(0, spaceIndex).trim() + currLine.substring(endIndex, currLine.length()).trim());
			} else {
				linesNew.add(currLine);
			}
		}
		lines = linesNew;
		linesNew = new ArrayList<String>();
		
		
		
//		// ##########CONSOLE OUTPUT#############
//		for(int i = 0; i < lines.size(); i++) {
//			System.out.println(lines.get(i));
//		}
//		// ##########CONSOLE OUTPUT#############

		
		
//		
//
//		
//		// GET QUESTNAME
//		// [SYSTEM]
//		String questname = "";
//		ArrayList<String> rewardItems = new ArrayList<String>();
//		ArrayList<String> rewardFaction = new ArrayList<String>();
//		ArrayList<Pair<Integer, String>> rewardSkills = new ArrayList<Pair<Integer, String>>();
//		String rewardXP = "";
//		String rewardMoney = "";
//
//		for(int m = 0; m < lines.size(); m++) {
//			String currLine = lines.get(m);
//			// Questname
//			if(currLine.contains("[System] >You have completed the")) {
//				int startIndex = currLine.indexOf("[System] >You have completed the") + 33;
//				int endIndex = currLine.indexOf("quest") - 1;
//				questname = currLine.substring(startIndex, endIndex);
////					System.out.println(currLine.substring(startIndex, endIndex));
//				// Delete the line with the Questname (probably the last)
//			} else if (currLine.contains("Quest Completed!")){
//				// Do nothing -> deletes the line
//			// Items
//			} else if (currLine.contains("[System] >You have received ")){
//				int startIndex = currLine.indexOf("[System] >You have received ") + 28;
//				int endIndex = currLine.length() - 1;
//				rewardItems.add(currLine.substring(startIndex, endIndex));
////					System.out.println(currLine.substring(startIndex, endIndex));
//			// Faction / Association
//			} else if (currLine.contains("[System] >Your faction with ")){
//				int startIndex = currLine.indexOf("[System] >Your faction with ") + 28;
//				int endIndex = currLine.length() - 14;
//				rewardFaction.add(currLine.substring(startIndex, endIndex));
////					System.out.println(currLine.substring(startIndex, endIndex));
//			// XP
//			} else if (currLine.contains("experience points")){
//				int startIndex = currLine.indexOf("[System] >You gained ") + 21;
//				int endIndex = currLine.indexOf("experience points") - 1;
//				rewardXP = "[Reward XP] " + currLine.substring(startIndex, endIndex);
////					System.out.println(currLine.substring(startIndex, endIndex));
//			// Reward Skills
//			} else if (currLine.contains("[System] >Your ") && currLine.contains("skill") && currLine.contains("by")){
//				int startSkill = currLine.indexOf("[System] >Your ") + 15;
//				int endSkill = currLine.substring(startSkill).indexOf("skill") - 1 + startSkill;
//				int startAmount = currLine.indexOf("by") + 3;
//				int endAmount = currLine.substring(startAmount).indexOf(" ") + startAmount;
////					System.out.println(startSkill + " " + endSkill + " " + startAmount + " " + endAmount);
//				String skill = currLine.substring(startSkill, endSkill);
//				Integer amount = Integer.parseInt(currLine.substring(startAmount, endAmount));
//				rewardSkills.add(new Pair<Integer, String>(amount, skill));
////					System.out.println("Skill: \"" + amount + "\" \"" + skill + "\"");
//			// Money
//			} else if (currLine.contains("[System] >You received ")){
//				int startIndex = currLine.indexOf("[System] >You received ") + 23;
//				int endIndex = currLine.length();
//				String moneyString = currLine.substring(startIndex, endIndex);
//				int moneyAmount = 0;
//				
//				while(moneyString.contains(",") || moneyString.contains("and") || moneyString.contains(".")){
//					int spaceIndex = moneyString.indexOf(' ');
//					int amount = Integer.parseInt(moneyString.substring(0, spaceIndex));
////						System.out.println(moneyString.substring(0, spaceIndex));
//					String moneySize;
//					if(moneyString.contains(",")) {
//						int colIndex = moneyString.indexOf(',');
//						moneySize = moneyString.substring(spaceIndex+1, colIndex);
////							System.out.println(moneySize);
//						moneyString = moneyString.substring(colIndex+2);
////							System.out.println(moneyString);
//					} else if(moneyString.contains("and")) {
//						int andIndex = moneyString.indexOf("and");
//						moneySize = moneyString.substring(spaceIndex+1, andIndex-1);
////							System.out.println(moneySize);
//						moneyString = moneyString.substring(andIndex+4);
////							System.out.println(moneyString);
//
//					} else {
//						int pointIndex = moneyString.indexOf(".");
//						moneySize = moneyString.substring(spaceIndex+1, pointIndex);
////							System.out.println(moneySize);
//						// the moneyString here should always be empty
//						// so the while loop will end after this iteration
//						moneyString = moneyString.substring(pointIndex+1);
////							System.out.println(moneyString);
//					}
//					
//					if(moneySize.equals("circle")) {
//						moneyAmount += 250 * amount;
//					} else if(moneySize.equals("octa")) {
//						moneyAmount += 50 * amount;
//					} else if(moneySize.equals("hexa")) {
//						moneyAmount += 10 * amount;
//					} else if(moneySize.equals("tria")) {
//						moneyAmount += amount;
//					}
////						System.out.println(moneyAmount);
//				}
//				rewardMoney = "[Reward Money] " + moneyAmount;
////					System.out.println(currLine.substring(startIndex, endIndex));
//			} else {
//				linesNew.add(currLine);
//			}
//
//		}
//		// Add empty line + rewardXP + rewardMoeny
//		linesNew.add("");
//		linesNew.add(rewardXP);
//		linesNew.add(rewardMoney);
//		
//		// Add [Reward Skills]
//		StringBuffer sb = new StringBuffer();
//		sb.append("[Reward Skills]");
//		for(int j = 0; j < rewardSkills.size(); j++) {
//			if(j != 0) {
//				sb.append(";");
//			}
//			
//			Integer amount = rewardSkills.get(j).getFirst();
//			String skill = rewardSkills.get(j).getSecond();
//			
//			
//			sb.append(" " + amount);
//			sb.append(", " + skill);
//		}
//		linesNew.add(sb.toString());
//		
//		// Add [Reward Items]
//		sb = new StringBuffer();
//		sb.append("[Reward Items]");
//		for(int j = 0; j < rewardItems.size(); j++) {
//			if(j != 0) {
//				sb.append(";");
//			}
//			
//			String reward = rewardItems.get(j);
//			
//			int spaceIndex = reward.indexOf(" ");
//			
//			// replace an a with a 1
//			String amount;
//			if(reward.substring(0, spaceIndex).equals("a")) {
//				amount = "1";
//			} else {
//				amount = reward.substring(0, spaceIndex);
//			}
//			
//			sb.append(" " + amount);
//			sb.append(", " + reward.substring(spaceIndex + 1));
//		}
//		linesNew.add(sb.toString());
//		
//		
//		// Add [Reward Associations]
//		
//		sb = new StringBuffer();
//		sb.append("[Reward Associations]");
//		for(int j = 0; j < rewardFaction.size(); j++) {
//			if(j != 0) {
//				sb.append(";");
//			}
//			sb.append(" , " + rewardFaction.get(j));
//		}
//		linesNew.add(sb.toString());
//		
//		
//		lines = linesNew;
//		linesNew = new ArrayList<String>();	
//		
//		// Set Questname, NPC Name, Prequests, Need
//		linesNew = lines;
//		lines = new ArrayList<String>();
//		lines.add("[Questname] " + questname);
//		lines.add("[NPC Name] " + questNPC);
//		
//		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//		Date date = new Date();
//		lines.add("[Checkup] " + dateFormat.format(date));
//		lines.add("[Prequests]");
//		lines.add("[Need Items]");
//		lines.add("[Need Skills]");
//		lines.add("[Need Winch]");
//		lines.addAll(linesNew);
//		linesNew = new ArrayList<String>();	
//		
//		
//		
//		
//		
//		
		try {
			// Create new file
			System.out.println(filename);
			File file = new File(directory + filename.substring(0, filename.length()-4) + ".psquest");
			
			// if file doesn't exist, create it
			if (! file.exists()) {
				file.createNewFile();
			} else {
				// if it exists, throw exception, because we don't want to override it
				throw new IOException("The file \"" + file + "\" exists already! We don't want to override it.");
			}
			
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			
			for(int y = 0; y < lines.size(); y++) {
				bw.append(lines.get(y) + "\n");
			}
			
			bw.close();
			
			System.out.println("Created File: " + directory + filename.substring(0, filename.length()-4) + ".psquest");
			
			
			

		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}
}