package ish.oncourse.model;

import ish.oncourse.model.auto._WebContentVisibility;

public class WebContentVisibility extends _WebContentVisibility {

	@Override
	protected void onPostAdd() {
		setWeight(Integer.MAX_VALUE);
	}
}
