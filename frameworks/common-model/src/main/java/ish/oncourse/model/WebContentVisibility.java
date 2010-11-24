package ish.oncourse.model;

import ish.oncourse.model.auto._WebContentVisibility;

public class WebContentVisibility extends _WebContentVisibility implements
		Comparable<WebContentVisibility> {

	@Override
	protected void onPostAdd() {
		setWeight(Integer.MAX_VALUE);
	}

	public int compareTo(WebContentVisibility arg) {
		return arg.getWeight() - this.getWeight();
	}
}
