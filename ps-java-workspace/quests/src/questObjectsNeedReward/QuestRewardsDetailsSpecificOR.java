package questObjectsNeedReward;

public class QuestRewardsDetailsSpecificOR extends QuestRewardsDetailsStandard {

	private int variant;
	
	public QuestRewardsDetailsSpecificOR(int variant) {
		this.variant = variant;
	}

	/**
	 * @return the variant
	 */
	public int getVariant() {
		return variant;
	}

	/**
	 * @param variant the variant to set
	 */
	public void setVariant(int variant) {
		this.variant = variant;
	}
	
	
}
