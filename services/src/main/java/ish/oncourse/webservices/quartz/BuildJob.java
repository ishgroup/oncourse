/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices.quartz;

import org.quartz.*;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * This function build and schedule new quartz job
 */
public class BuildJob {
	private Class<? extends Job> jobClass;
	private String jobName;
	private String groupName;

	private String cron;

	private JobDetail jobDetail;
	private Trigger trigger;

	public BuildJob jobClass(Class<? extends Job> jobClass) {
		this.jobClass = jobClass;
		return this;
	}

	public BuildJob cronString(String cron) {
		this.cron = cron;
		return this;
	}

	public BuildJob jobName(String jobName) {
		this.jobName = jobName;
		return this;
	}

	public BuildJob groupName(String groupName) {
		this.groupName = groupName;
		return this;
	}


	public BuildJob build(Scheduler scheduler) {
		try {
			if (jobName == null)
				jobName = jobClass.getName();

			if (groupName == null)
				groupName = jobClass.getPackage().getName();

			JobKey jobKey = new JobKey(jobName, groupName);

			TriggerKey triggerKey = new TriggerKey(jobName, groupName);

			jobDetail = newJob(jobClass)
					.withIdentity(jobKey)
					.build();

			trigger = newTrigger()
					.withIdentity(triggerKey)
					.withSchedule(CronScheduleBuilder.cronSchedule(cron))
					.forJob(jobDetail)
					.build();

			if (!(scheduler.checkExists(jobKey) || scheduler.checkExists(triggerKey)))
				scheduler.scheduleJob(jobDetail, trigger);

			return this;

		} catch (SchedulerException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public JobDetail getJobDetail() {
		return jobDetail;
	}

	public Trigger getTrigger() {
		return trigger;
	}


}
