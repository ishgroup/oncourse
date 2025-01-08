/*
 * Copyright ish group pty ltd 2020.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */
package ish.oncourse.server.modules;

import io.bootique.di.Injector;
import org.quartz.Job;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.spi.JobFactory;
import org.quartz.spi.TriggerFiredBundle;

/**
 * Creates Quartz jobs, taking job instances from injector.
 *
 */
public class AngelJobFactory implements JobFactory {

	private Injector injector;

	/**
	 * @param injector
	 */
	public AngelJobFactory(Injector injector) {
		super();
		this.injector = injector;
	}

	/**
	 * @see JobFactory#newJob(TriggerFiredBundle, Scheduler)
	 */
	public Job newJob(TriggerFiredBundle bundle, Scheduler scheduler) throws SchedulerException {
		try {
			var job = this.injector.getInstance(bundle.getJobDetail().getJobClass());
			return job;
		} catch (Exception e) {
			throw new SchedulerException(e);
		}
	}
}
