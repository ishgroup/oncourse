package ish.oncourse.webservices.quartz;

import ish.oncourse.webservices.jobs.GenericQuartzInitializer;

import org.apache.tapestry5.ioc.ServiceResources;
import org.quartz.Scheduler;

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
		//TODO: add application specific jobs
	}
}
