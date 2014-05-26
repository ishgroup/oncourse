package ish.oncourse.model;

import ish.oncourse.model.auto._WebTemplate;

import java.util.Date;

public class WebTemplate extends _WebTemplate {

	@Override
	protected void onPostAdd() {
		Date now = new Date();
		
		if (getCreated() == null) {
			setCreated(now);
		}
		
		if (getModified() == null) {
			setModified(now);
		}
	}

	@Override
	protected void onPreUpdate() {
		setModified(new Date());
	}
}
