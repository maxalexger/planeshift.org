package questObjectsNeedReward;

import java.util.ArrayList;

import utilities.Pair;

public class QuestNeedStandard {
	
	ArrayList<QuestNeedDetailsStandard> needDetails = new ArrayList<QuestNeedDetailsStandard>();
	
	public void addNeedDetails(QuestNeedDetailsStandard needDetail) {
		needDetails.add(needDetail);
	}

	/**
	 * @return the needDetails
	 */
	public ArrayList<QuestNeedDetailsStandard> getNeedDetails() {
		return needDetails;
	}

	/**
	 * @param needDetails the needDetails to set
	 */
	public void setNeedDetails(ArrayList<QuestNeedDetailsStandard> needDetails) {
		this.needDetails = needDetails;
	}
	
	
	
}
