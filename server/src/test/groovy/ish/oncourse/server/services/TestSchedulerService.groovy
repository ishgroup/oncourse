/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.server.services


import org.quartz.Job
import org.quartz.JobBuilder
import org.quartz.JobDetail
import org.quartz.JobKey
import org.quartz.SchedulerException
import org.quartz.Trigger

import java.text.ParseException

class TestSchedulerService implements ISchedulerService {
	
	private List<JobDetail> jobs = new ArrayList<>()

    @Override
    void schedulePeriodicJob(Class<? extends Job> provider, String id, String groupId, int intervalInSeconds,
                             boolean startNow, boolean preventWhenHeadlessStart) throws ParseException, SchedulerException {
		jobs.add(JobBuilder.newJob(provider).withIdentity(id, groupId).build())
    }

	@Override
    void scheduleCronJob(Class<? extends Job> provider, String id, String groupId, String cron, String timeZoneId,
                         boolean startNow, boolean preventWhenHeadlessStart) throws ParseException, SchedulerException {
		jobs.add(JobBuilder.newJob(provider).withIdentity(id, groupId).build())
    }

	@Override
    void scheduleJob(JobDetail jobDetail, Trigger trigger) throws SchedulerException {
		jobs.add(jobDetail)
    }

	@Override
    void removeJob(JobKey jobKey) throws SchedulerException {
		for (JobDetail jobDetail : new ArrayList<>(jobs)) {
			if (jobKey.equals(jobDetail.getKey())) {
				jobs.remove(jobDetail)
            }
		}
	}

	@Override
    String getCronExp(String name) throws SchedulerException {
		return null
    }

    List<JobDetail> getJobs() {
		return jobs
    }
}
