package warehouse.nxt.utils.localisation;

import warehouse.nxt.utils.navigation.GridPose;

public interface GridPoseProvider {

	GridPose getGridPose();

	void setGridPose(GridPose _pose);

}
