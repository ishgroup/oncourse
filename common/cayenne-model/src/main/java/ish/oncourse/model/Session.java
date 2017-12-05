package ish.oncourse.model;

import ish.oncourse.cayenne.SessionInterface;
import ish.oncourse.model.auto._Session;
import ish.oncourse.utils.QueueableObjectUtils;

import java.time.Duration;
import java.util.Date;

public class Session extends _Session implements Queueable, SessionInterface {
	private static final long serialVersionUID = 5495296857845632418L;

	public Long getId() {
		return QueueableObjectUtils.getId(this);
	}

	/**
	 * @return the total duration of this session in minutes or null if the
	 *         start or end date are not defined.
	 */
	public Long getDurationMinutes() {
		Long minutes = null;
		if ((getStartDate() != null) && (getEndDate() != null)) {

			Duration duration = Duration.between(getStartDate().toInstant(),getEndDate().toInstant());
			minutes = duration.toMinutes();
		}
		return minutes;
	}

	/**
	 * returns timezone for the session, or default timezone from college.
	 */
	@Override
	public String getTimeZone() {
		String value =  super.getTimeZone();
		if (value == null) {
			if (this.getRoom() != null && this.getRoom().getSite() != null && !this.getRoom().getSite().getIsVirtual()) {
				value = this.getRoom().getSite().getTimeZone();
			}
		}

		if (value == null) {
			value = getCollege().getTimeZone();
		}
		return value;
	}

	public boolean isVirtualSiteUsed() {
		return getRoom() != null && getRoom().getSite() != null && getRoom().getSite().getIsVirtual();
	}

	@Override
	public boolean isAsyncReplicationAllowed() {
		return false;
	}

	@Override
	public Date getStartDatetime() {
		return super.getStartDate();
	}

	@Override
	public Date getEndDatetime() {
		return super.getEndDate();
	}
}
