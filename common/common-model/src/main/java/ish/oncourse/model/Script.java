package ish.oncourse.model;

import ish.oncourse.model.auto._Script;

public class Script extends _Script implements Queueable {

	@Override
	public Long getId() {
		return null;
	}

	@Override
	public boolean isAsyncReplicationAllowed() {
		return false;
	}
}
