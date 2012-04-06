package ish.oncourse.model;

import java.util.Calendar;
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

    @Override
    public Date getCreated() {
        return Calendar.getInstance().getTime();
    }

    public void setModified(Date modified) {
        if (getWaitingList() != null)
        {
		    getWaitingList().setModified(modified);
        }
	}
}
