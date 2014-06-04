package ish.oncourse.model;

import ish.oncourse.model.auto._CustomField;
import ish.oncourse.utils.QueueableObjectUtils;

import java.util.Date;

public class CustomField extends _CustomField implements Queueable {

	@Override
	public Long getId() {
		return QueueableObjectUtils.getId(this);
	}

	@Override
	public boolean isAsyncReplicationAllowed() {
		return true;
	}

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
}
