package ish.oncourse.model;

import ish.oncourse.model.auto._Session;
import ish.oncourse.utils.QueueableObjectUtils;
import org.joda.time.Period;
import org.joda.time.PeriodType;

public class Session extends _Session implements Queueable {
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
		String value =  super.getTimeZone();
		if (value == null)
		{
			if (this.getRoom() != null && this.getRoom().getSite() != null)
				value = this.getRoom().getSite().getTimeZone();
		}

		if (value == null)
			value = getCollege().getTimeZone();

		return value;
	}

}
