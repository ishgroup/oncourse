/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.server.services

import org.quartz.*

import java.text.ParseException

class TestSchedulerService implements ISchedulerService {

    private Map<JobDetail,Trigger> jobs = new HashMap<>()

    @Override
    void schedulePeriodicJob(Class<? extends Job> provider, String id, String groupId, int intervalInSeconds,
                             boolean startNow, boolean preventWhenHeadlessStart) throws ParseException, SchedulerException {
        jobs.put(JobBuilder.newJob(provider).withIdentity(id, groupId).build(), null)
    }

    @Override
    void scheduleCronJob(Class<? extends Job> provider, String id, String groupId, String cron, String timeZoneId,
                         boolean startNow, boolean preventWhenHeadlessStart) throws ParseException, SchedulerException {
        jobs.put(JobBuilder.newJob(provider).withIdentity(id, groupId).build(), null)
    }

    @Override
    void scheduleJob(JobDetail jobDetail, Trigger trigger) throws SchedulerException {
        jobs.put(jobDetail, trigger)
    }

    @Override
    void removeJob(JobKey jobKey) throws SchedulerException {
        List<JobDetail> clonJobs = new ArrayList<>(jobs.keySet())
        for (JobDetail jobDetail : clonJobs) {
            if (jobKey == jobDetail.getKey()) {
                jobs.remove(jobDetail)
            }
        }
    }

    @Override
    String getCronExp(String name) throws SchedulerException {
        return null
    }

    List<JobDetail> getJobs() {
        return new ArrayList<JobDetail>(jobs.keySet())
    }
    
    Trigger getTrigger(JobDetail detail) {
        jobs.get(detail)
    }
}
