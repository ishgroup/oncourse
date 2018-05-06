/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.scheduler.job;

import java.util.concurrent.TimeUnit;

/**
 * User: akoiro
 * Date: 6/5/18
 */
public class Config {
	private long initialDelay;
	private long period;
	private TimeUnit timeUnit;

	public Config(long initialDelay, long period, TimeUnit timeUnit) {
		this.initialDelay = initialDelay;
		this.period = period;
		this.timeUnit = timeUnit;
	}

	public long getInitialDelay() {
		return initialDelay;
	}

	public long getPeriod() {
		return period;
	}

	public TimeUnit getTimeUnit() {
		return timeUnit;
	}
}
