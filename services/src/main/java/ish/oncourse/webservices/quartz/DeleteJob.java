/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices.quartz;

import org.quartz.*;

/**
 * This function deletes and unschedules a quartz job
 */
public class DeleteJob {
	private Class<? extends Job> jobClass;
	private String jobName;
	private String groupName;


	private JobDetail jobDetail;
	private Trigger trigger;


	public DeleteJob jobClass(Class<? extends Job> jobClass) {
		this.jobClass = jobClass;
		return this;
	}

	public DeleteJob jobName(String jobName) {
		this.jobName = jobName;
		return this;
	}

	public DeleteJob groupName(String groupName) {
		this.groupName = groupName;
		return this;
	}


	public DeleteJob delete(Scheduler scheduler) {
		try {
			if (jobName == null)
				jobName = jobClass.getName();

			if (groupName == null)
				groupName = jobClass.getPackage().getName();

			JobKey jobKey = new JobKey(jobName, groupName);

			TriggerKey triggerKey = new TriggerKey(jobName, groupName);

			if (scheduler.checkExists(triggerKey))
				scheduler.unscheduleJob(triggerKey);

			if (scheduler.checkExists(jobKey))
				scheduler.deleteJob(jobKey);

			return this;

		} catch (SchedulerException e) {
			throw new IllegalArgumentException(e);
		}
	}
}
