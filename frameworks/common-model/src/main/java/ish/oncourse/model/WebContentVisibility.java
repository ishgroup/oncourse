package ish.oncourse.model;

import ish.oncourse.model.auto._WebContentVisibility;

public class WebContentVisibility extends _WebContentVisibility {
	public static final String DEFAULT_REGION_KEY = "content";

	@Override
	protected void onPostAdd() {
		setWeight(Integer.MAX_VALUE);
	}
}
