package ish.oncourse.model;

import java.util.Date;

import ish.oncourse.model.auto._WaitingListSite;
import ish.oncourse.utils.QueueableObjectUtils;

public class WaitingListSite extends _WaitingListSite implements Queueable {
	private static final long serialVersionUID = -4096249550669641954L;

	public Long getId() {
		return QueueableObjectUtils.getId(this);
	}

	public void setCreated(Date created) {
	}

	public void setModified(Date modified) {
		getWaitingList().setModified(modified);
	}
}
