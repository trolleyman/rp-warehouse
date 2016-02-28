package warehouse.pc.job;

import java.util.Comparator;

public class RewardComparator implements Comparator<Job> {
	@Override
	public int compare(Job o1, Job o2) {
		int i;
		if(o1.getTotalReward() > o2.getTotalReward()) i = -1; //The values -1 and 1 are the other way around as list should be descending
		else if (o1.getTotalReward() < o2.getTotalReward()) i = 1;
		else i = 0;
		return i;
	}
}
