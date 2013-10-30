package ish.oncourse.model;

import ish.oncourse.model.auto._StudentConcession;
import ish.oncourse.utils.QueueableObjectUtils;


public class StudentConcession extends _StudentConcession implements Queueable {
	private static final long serialVersionUID = 1333077459218332553L;

	public Long getId() {
		return QueueableObjectUtils.getId(this);
	}

	@Override
	public boolean isAsyncReplicationAllowed() {
		return true;
	}
}
