package ish.oncourse.enrol.services.quartz;

import ish.oncourse.services.jobs.GenericQuartzInitializer;
import ish.oncourse.services.jobs.PaymentInExpireJob;

import org.apache.tapestry5.ioc.ServiceResources;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.TriggerBuilder;

/**
 *  Initialize enrolment related quartz jobs.
 */
public class QuartzInitializer extends GenericQuartzInitializer {

	public QuartzInitializer(ServiceResources serviceResources) {
		super(serviceResources);
	}

	@Override
	protected void initJobs(Scheduler scheduler) throws Exception {
		JobKey expireJobKey = new JobKey("PaymentInExpireJob", "willowServicesJobs");

		if (!scheduler.checkExists(expireJobKey)) {
			JobDetail expireJobDetails = JobBuilder.newJob(PaymentInExpireJob.class).withIdentity(expireJobKey).build();

			CronTrigger expireJobTrigger = TriggerBuilder.newTrigger().withIdentity("PaymentInExpireTrigger", "willowServicesTriggers")
					.startNow().withSchedule(CronScheduleBuilder.cronSchedule("0 */2 * * * ?")).build();

			scheduler.scheduleJob(expireJobDetails, expireJobTrigger);
		}
	}
}
