package ish.oncourse.admin.services.quartz;

import java.text.ParseException;

import org.apache.log4j.Logger;
import org.apache.tapestry5.ioc.ServiceResources;
import org.apache.tapestry5.ioc.services.RegistryShutdownListener;
import org.quartz.Job;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
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
		// TODO: add job definition here
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
