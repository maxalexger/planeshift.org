package main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import questObjectsNeedReward.Quest;

public class Main {

	/**
	 * This function: 
	 * (1.) creates an SQL file which is supposed to be uploaded to
	 * the kingdomofvalour.de database. This inserts data about the game Planeshift
	 * which is needed for the website.
	 * 
	 * (2.) we create '.html' quest files about the quests in the game Planeshift
	 * which will be accessed (once uploaded) through various links on the website.
	 * 
	 * 
	 * NOTE: We use the variable PATHforPHPoutput which has to be set to the
	 * standard output folder for our '.php' files. This has to be done in the
	 * 'utilities.Constants' class.
	 * 
	 * @param args
	 *            Nothing, as we don't use any input from the keyboard
	 * @throws IOException
	 *             If the file we want to write our content to, cannot be found.
	 */
	public static void main(String[] args) throws IOException, Exception {
				
		System.out.println("Reminder: Execute this file in the /ps-java-workspace/quests/ directory.\n");

		/* ############### QUESTS ######################### */

		// Create quest files
		
		PsqToObj psq_to_obj = new PsqToObj();
		ObjToPhp obj_to_php = new ObjToPhp();
		ObjToSql obj_to_sql = new ObjToSql();
		
		// Parses psq files into java objects
		final ArrayList<Quest> quests = psq_to_obj.parseAllQuestsDetails();
		
		/* ########### QUEST DETAILS ############ */
		
		obj_to_php.createAllQUestDetailsFiles(quests);
		System.out.println("Quest details Done! (All quest files have been rewritten).");
		
		/* GET OUTPUTFILE */

		File file = new File(utilities.Constants.PATHtoPHPoutput + "/fill_database/fill_database_eclipse.php");
		// if file doesn't exists, then create it
		if (!file.exists()) {
			file.createNewFile();
		}
		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);

		/* WRITE IN OUTPUTFILE */

		/* ########### QUEST OVERVIEW/REWARD ############ */

		// SQL text btw quest reward and quest overview 
		readFileIntoBufferedWriter(bw, "text_fill_database_1.sql");

		obj_to_sql.createSqlQuestOverviewFile(bw, quests);

		// SQL text btw quest overview and quest reward (new)
		readFileIntoBufferedWriter(bw, "text_fill_database_2.sql");

		obj_to_sql.createSqlQuestRewardFile(bw, quests);

		System.out.println("Quests overview/reward Done! (\'fill_database_eclipse.php\').\n");


		/* ############### Quest Requirements ###################### */

		// SQL text btw glyphs and prequests
		readFileIntoBufferedWriter(bw, "text_fill_database_3.sql");

		// TODO: Extract the quests and the skills that are required to get a quest
		// However, there is a questchain sql file. Maybe that is enough

		// SQL text btw prequests and file end
		readFileIntoBufferedWriter(bw, "text_fill_database_4.sql");

		bw.close(); // close buffered writer

		System.out.println("-----------------------------------------------");
		System.out.println("Upload fill_database_eclipse.php to the server.");
		System.out.println("Upload the new .php quest files that you desire to the server.");
	}

	/**
	 * This function adds every line of a given file to a given buffered writer.
	 * 
	 * @param bw
	 *            The buffered writer where we add the content of the input file.
	 * @param inputFile
	 *            The path from the standard path for SQL outputs (set in
	 *            utilites.Utilities) to the input file which contains the content
	 *            we want to add to our buffered writer.
	 * @throws IOException
	 *             Occurs if the file we want to read cannot be found.
	 */
	public static void readFileIntoBufferedWriter(BufferedWriter bw, String inputFile) throws IOException {

		/* Create buffered Reader for inputFile */
		File file = new File("assets/" + inputFile);
		BufferedReader reader = new BufferedReader(new FileReader(file));

		// Write all lines from inputFile into outputFile
		String line = "";
		while ((line = reader.readLine()) != null) {
			bw.write(line);
			bw.newLine();
		}

		// Close the reader
		reader.close();

	}

}
