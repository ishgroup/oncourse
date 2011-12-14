package ish.oncourse.webservices.quartz;

import static org.quartz.TriggerBuilder.newTrigger;
import ish.oncourse.webservices.jobs.ContactStudentDataFixJob;
import ish.oncourse.webservices.jobs.PaymentInExpireJob;
import ish.oncourse.webservices.jobs.SMSJob;

import java.text.ParseException;
import java.util.Calendar;

import org.apache.log4j.Logger;
import org.apache.tapestry5.ioc.ServiceResources;
import org.apache.tapestry5.ioc.services.RegistryShutdownListener;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.spi.JobFactory;
import org.quartz.spi.TriggerFiredBundle;

public class QuartzInitializer implements RegistryShutdownListener {

	private static final Logger LOGGER = Logger.getLogger(QuartzInitializer.class);

	private Scheduler scheduler;

	public QuartzInitializer(ServiceResources serviceResources) {
		try {
			// disable check for quartz updates on startup
			System.setProperty("org.quartz.scheduler.skipUpdateCheck", "true");

			StdSchedulerFactory sf = new StdSchedulerFactory();

			scheduler = sf.getScheduler();
			scheduler.setJobFactory(new QuartzJobFactory(serviceResources));
			scheduler.getListenerManager().addTriggerListener(new PreventConcurrentRunListener());

			scheduler.start();

			initJobs();

		} catch (SchedulerException e) {
			LOGGER.error("Error during scheduler initialization, aborting start up.", e);
			throw new RuntimeException("Error during scheduler initialization, aborting start up.", e);
		} catch (ParseException pe) {
			LOGGER.error("Error during scheduler initialization, aborting start up.", pe);
			throw new RuntimeException("Error during scheduler initialization, aborting start up.", pe);
		}
	}

	/**
	 * Initialize quartz jobs and triggers, if they're not loaded into database
	 * already.
	 */
	private void initJobs() throws SchedulerException, ParseException {

		JobKey smsJobKey = new JobKey("SmsJob", "willowServicesJobs");

		if (scheduler.checkExists(smsJobKey)) {
			scheduler.deleteJob(smsJobKey);
		}

		JobDetail smsJobDetails = JobBuilder.newJob(SMSJob.class).withIdentity(smsJobKey).build();

		CronTrigger smsJobTrigger = TriggerBuilder.newTrigger().withIdentity("SmsJobTrigger", "willowServicesTriggers").startNow()
				.withSchedule(CronScheduleBuilder.cronSchedule("0 */3 * * * ?")).build();

		scheduler.scheduleJob(smsJobDetails, smsJobTrigger);

		JobKey expireJobKey = new JobKey("PaymentInExpireJob", "willowServicesJobs");

		if (scheduler.checkExists(expireJobKey)) {
			scheduler.deleteJob(expireJobKey);
		}

		JobDetail expireJobDetails = JobBuilder.newJob(PaymentInExpireJob.class).withIdentity(expireJobKey).build();

		CronTrigger expireJobTrigger = TriggerBuilder.newTrigger().withIdentity("PaymentInExpireTrigger", "willowServicesTriggers")
				.startNow().withSchedule(CronScheduleBuilder.cronSchedule("0 */2 * * * ?")).build();

		scheduler.scheduleJob(expireJobDetails, expireJobTrigger);

		JobKey duplicateFixJobKey = new JobKey("ContactStudentDataFixJob1", "willowServicesJobs");

		if (scheduler.checkExists(duplicateFixJobKey)) {
			scheduler.deleteJob(duplicateFixJobKey);
		}

		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, 2);

		JobDetail duplicateFixJobDetails = JobBuilder.newJob(ContactStudentDataFixJob.class).withIdentity(duplicateFixJobKey).build();

		SimpleTrigger duplicateFixJobTrigger = (SimpleTrigger) newTrigger()
				.withIdentity("ContactStudentDataFixJobTrigger", "willowServicesTriggers").startAt(cal.getTime())
				.forJob(duplicateFixJobDetails).build();

		scheduler.scheduleJob(duplicateFixJobDetails, duplicateFixJobTrigger);
	}

	public void registryDidShutdown() {

		if (scheduler != null) {
			try {
				scheduler.shutdown();
			} catch (SchedulerException e) {
				LOGGER.error("Error during scheduler shutdown.", e);
			}
		}

		scheduler = null;
	}

	private static class QuartzJobFactory implements JobFactory {

		private ServiceResources serviceResources;

		private QuartzJobFactory(ServiceResources serviceResources) {
			this.serviceResources = serviceResources;
		}

		public Job newJob(TriggerFiredBundle bundle, Scheduler scheduler) throws SchedulerException {
			Job job = serviceResources.getService(bundle.getJobDetail().getJobClass());
			return job;
		}
	}
}
