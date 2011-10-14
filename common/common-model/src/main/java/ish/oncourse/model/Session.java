package ish.oncourse.model;

import ish.oncourse.model.auto._Session;

import org.joda.time.Period;
import org.joda.time.PeriodType;

public class Session extends _Session implements Queueable {

	public Long getId() {
		return (getObjectId() != null && !getObjectId().isTemporary()) ? (Long) getObjectId().getIdSnapshot().get(
				ID_PK_COLUMN) : null;
	}

	/**
	 * @return the total duration of this session in minutes or null if the
	 *         start or end date are not defined.
	 */
	public Long getDurationMinutes() {
		Long minutes = null;
		if ((getStartDate() != null) && (getEndDate() != null)) {
			Period duration = new Period(getStartDate().getTime(), getEndDate().getTime(), PeriodType.minutes());
			minutes = new Long(duration.getMinutes());
		}
		return minutes;
	}

	/**
	 * returns timezone for the session, or default timezone from college.
	 */
	@Override
	public String getTimeZone() {
		//check if the timezone is overriden
		if (super.getTimeZone() != null) {
			return super.getTimeZone();
		}
		//else return default college timezone
		return getCollege().getTimeZone();
	}

}
