package questObjectsNeedReward;

public class QuestRewardsWaySpecific extends QuestRewardsWayStandard {
	
	int way;

	public QuestRewardsWaySpecific(int way) {
		this.way = way;
	}

	/**
	 * @return the way
	 */
	public int getWay() {
		return way;
	}

	/**
	 * @param way the way to set
	 */
	public void setWay(int way) {
		this.way = way;
	}
	
	
}
