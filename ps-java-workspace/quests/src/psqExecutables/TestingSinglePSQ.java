package psqExecutables;

import java.io.File;
import questObjectsNeedReward.Quest;


import main.PsqToObj;


/*
 * 
 */


class TestingPSQ {
	
	public static void main(String[] args) throws Exception {

		String filepath = "src/psqExecutables/A Day Off.psquest";
		String wdir = System.getProperty("user.dir");
		
		filepath = wdir + "/" + filepath;
		
		
		File f = new File(filepath);
		String filename = f.getName();
		String folder = f.getParent();
		
		System.out.println(folder);
		
		PsqToObj psq_to_obj = new PsqToObj();
		Quest currQuest = psq_to_obj.parseQuestsDetails(filepath, filename);
		main.ObjToPhp.createQuestDetailsFile(currQuest, folder + "/");

	}
	
	
	
}

