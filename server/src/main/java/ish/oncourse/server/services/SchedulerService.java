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

package ish.oncourse.server.services;

import com.google.inject.Inject;
import ish.oncourse.server.services.ISchedulerService;
import ish.util.TimeZoneUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.*;
import org.quartz.impl.triggers.CronTriggerImpl;

import java.text.ParseException;

import static org.quartz.SimpleScheduleBuilder.simpleSchedule;

/**
 * Quartz scheduller service used to schedule triggers and cron tasks.
 */
public class SchedulerService implements ISchedulerService {

	private static Logger logger = LogManager.getLogger();

	private final Scheduler scheduler;

	@Inject
	public SchedulerService(Scheduler scheduler) {
		this.scheduler = scheduler;
	}


	private static Logger getLogger() {
		return logger;
	}

	@Override
	public void schedulePeriodicJob(Class<? extends Job> provider, String id, String groupId, int intervalInSeconds,
		boolean startNow, boolean preventWhenHeadlessStart) throws ParseException, SchedulerException {

		getLogger().debug("registering service:{}", provider);

		if (preventWhenHeadlessStart) {
			// dont create/schedule this job if headless start
			return;
		}

		var aJob = JobBuilder.newJob(provider).withIdentity(id, groupId).build();

		if(!scheduler.checkExists(aJob.getKey())) {
			var aTrigger = TriggerBuilder.newTrigger().withIdentity(id + TRIGGER_POSTFIX, groupId)
					.withSchedule(simpleSchedule().withIntervalInSeconds(intervalInSeconds).repeatForever()).build();
			scheduler.scheduleJob(aJob, aTrigger);
			if (startNow) {
				scheduler.triggerJob(aTrigger.getJobKey());
			}
		}
	}

	@Override
	public void scheduleCronJob(Class<? extends Job> provider, String id, String groupId, String cron, String timeZoneId,
								boolean startNow, boolean preventWhenHeadlessStart) throws ParseException, SchedulerException {

		getLogger().debug("registering service:{}", provider);

		if (preventWhenHeadlessStart) {
			// dont create/schedule this job if headless start
			return;
		}

		var aJob = JobBuilder.newJob(provider).withIdentity(id, groupId).build();

		if(!scheduler.checkExists(aJob.getKey())) {
			var aTrigger = TriggerBuilder.newTrigger().withIdentity(id + TRIGGER_POSTFIX, groupId)
					.withSchedule(CronScheduleBuilder.cronSchedule(cron).inTimeZone(TimeZoneUtil.getTimeZone(timeZoneId))).build();

			scheduler.scheduleJob(aJob, aTrigger);
			if (startNow) {
				scheduler.triggerJob(aTrigger.getJobKey());
			}
		}
	}

	@Override
	public void scheduleJob(JobDetail jobDetail, Trigger trigger) throws SchedulerException {
		var triggers = scheduler.getTriggersOfJob(jobDetail.getKey());

		if (!triggers.isEmpty()) {
			scheduler.deleteJob(jobDetail.getKey());
		}

		scheduler.scheduleJob(jobDetail, trigger);
	}

	@Override
	public void removeJob(JobKey jobKey) throws SchedulerException {
		scheduler.deleteJob(jobKey);
	}


	public String getCronExp(String name) throws SchedulerException {
		var trigger =  (CronTriggerImpl) scheduler.getTrigger(TriggerKey.triggerKey( name + "Trigger", CUSTOM_SCRIPT_JOBS_GROUP_ID));
		return trigger != null ? trigger.getCronExpression() : null;
	}


}
