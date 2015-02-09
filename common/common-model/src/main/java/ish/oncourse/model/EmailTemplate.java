package ish.oncourse.model;

import ish.oncourse.model.auto._EmailTemplate;

public class EmailTemplate extends _EmailTemplate implements Queueable {

	@Override
	public Long getId() {
		return null;
	}

	@Override
	public boolean isAsyncReplicationAllowed() {
		return false;
	}
}
