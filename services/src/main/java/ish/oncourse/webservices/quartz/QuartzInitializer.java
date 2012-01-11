package ish.oncourse.webservices.quartz;

import ish.oncourse.webservices.jobs.GenericQuartzInitializer;
import ish.oncourse.webservices.jobs.SMSJob;

import org.apache.tapestry5.ioc.ServiceResources;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.TriggerBuilder;

/**
 * Initialize specific services quartz jobs.
 * @author anton
 *
 */
public class QuartzInitializer extends GenericQuartzInitializer {
	
	public QuartzInitializer(ServiceResources serviceResources) {
		super(serviceResources);
	}

	/**
	 * Initialize quartz jobs and triggers, if they're not loaded into database
	 * already.
	 */
	@Override
	protected void initJobs(Scheduler scheduler) throws Exception {
		JobKey smsJobKey = new JobKey("SmsJob", "willowServicesJobs");

		if (scheduler.checkExists(smsJobKey)) {
			scheduler.deleteJob(smsJobKey);
		}

		JobDetail smsJobDetails = JobBuilder.newJob(SMSJob.class).withIdentity(smsJobKey).build();

		CronTrigger smsJobTrigger = TriggerBuilder.newTrigger().withIdentity("SmsJobTrigger", "willowServicesTriggers").startNow()
				.withSchedule(CronScheduleBuilder.cronSchedule("0 */3 * * * ?")).build();

		scheduler.scheduleJob(smsJobDetails, smsJobTrigger);
	}
}
