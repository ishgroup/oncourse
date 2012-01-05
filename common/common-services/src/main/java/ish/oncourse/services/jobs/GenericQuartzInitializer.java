package ish.oncourse.services.jobs;

import org.apache.log4j.Logger;
import org.apache.tapestry5.ioc.ServiceResources;
import org.apache.tapestry5.ioc.services.RegistryShutdownListener;
import org.quartz.Job;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.spi.JobFactory;
import org.quartz.spi.TriggerFiredBundle;

/**
 * The main purpose is to simplify quartz initialization. Derived class in particular application is supposed to 
 * provide its own initJobs() implementation.
 * @author anton
 *
 */
public abstract class GenericQuartzInitializer implements RegistryShutdownListener {

	/**
	 * Logger
	 */
	private static final Logger LOGGER = Logger.getLogger(GenericQuartzInitializer.class);

	/**
	 * Scheduler instance
	 */
	private Scheduler scheduler;

	/**
	 * Constructor, service Resources needed for injection into quartz jobs.
	 * 
	 * @param serviceResources
	 */
	public GenericQuartzInitializer(ServiceResources serviceResources) {
		try {
			// disable check for quartz updates on startup
			System.setProperty("org.quartz.scheduler.skipUpdateCheck", "true");

			StdSchedulerFactory sf = new StdSchedulerFactory();

			scheduler = sf.getScheduler();
			scheduler.setJobFactory(new QuartzJobFactory(serviceResources));
			scheduler.getListenerManager().addTriggerListener(new PreventConcurrentRunListener());

			scheduler.start();

			initJobs(scheduler);

		} catch (Exception e) {
			LOGGER.error("Error during scheduler initialization, aborting start up.", e);
			throw new RuntimeException("Error during scheduler initialization, aborting start up.", e);
		}
	}
	
	/**
	 * 
	 */
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

	/**
	 * Quartz job factory, used primary to supply DI to quartz jobs.
	 * @author anton
	 *
	 */
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

	/**
	 * Initialize quartz jobs and triggers, if they're not loaded into database
	 * already.
	 */
	protected abstract void initJobs(Scheduler scheduler) throws Exception;
}
