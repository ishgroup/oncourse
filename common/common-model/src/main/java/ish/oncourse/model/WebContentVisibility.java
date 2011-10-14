package ish.oncourse.model;

import ish.oncourse.model.auto._WebContentVisibility;

public class WebContentVisibility extends _WebContentVisibility implements Comparable<WebContentVisibility> {

	public Long getId() {
		return (getObjectId() != null && !getObjectId().isTemporary()) ? (Long) getObjectId().getIdSnapshot().get(
				ID_PK_COLUMN) : null;
	}

	@Override
	protected void onPostAdd() {
		setWeight(Integer.MAX_VALUE);
	}

	public int compareTo(WebContentVisibility arg) {
		int result = 0;
		if (arg != null) {
			result = this.getWeight() - arg.getWeight();
		}
		if (result != 0) {
			return result;
		}

		// if weights for the same webNodeType are equal, check the content of
		// block for comparing
		WebContent o1 = getWebContent();
		WebContent o2 = arg.getWebContent();
		return o1.compareTo(o2);
	}
}
