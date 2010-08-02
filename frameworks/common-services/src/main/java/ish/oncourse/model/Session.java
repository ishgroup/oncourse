package ish.oncourse.model;

import ish.oncourse.model.auto._Session;

public class Session extends _Session {

	private final static int MILLISECONDS_PER_MINUTE = 1000 * 60;

	/**
	 * @return the total duration of this session in minutes or null if the
	 *         start or end date are not defined.
	 */
	public Long getDurationMinutes() {
		return (hasStartAndEndTimestamps()) ? (getEndTimestamp().getTime() - getStartTimestamp()
				.getTime())
				/ MILLISECONDS_PER_MINUTE
				: null;
	}

	public boolean hasStartAndEndTimestamps() {
		return getStartTimestamp() != null && getEndTimestamp() != null;
	}
}
