package questObjectsNeedReward;

import java.util.ArrayList;

public class QuestRewardsWayStandard {

	ArrayList<QuestRewardsDetailsStandard> rewardDetails = new ArrayList<QuestRewardsDetailsStandard>();
	
	public void addRewardDetails(QuestRewardsDetailsStandard rewardDetail) {
		rewardDetails.add(rewardDetail);
	}

	/**
	 * @return the rewards
	 */
	public ArrayList<QuestRewardsDetailsStandard> getRewards() {
		return rewardDetails;
	}

	/**
	 * @param rewards the rewards to set
	 */
	public void setRewards(ArrayList<QuestRewardsDetailsStandard> rewards) {
		this.rewardDetails = rewards;
	}
	
	
	
}
