package ish.oncourse.enrol.services.quartz;

import ish.oncourse.webservices.jobs.GenericQuartzInitializer;

import org.apache.tapestry5.ioc.ServiceResources;
import org.quartz.Scheduler;

/**
 *  Initialize enrolment related quartz jobs.
 */
public class QuartzInitializer extends GenericQuartzInitializer {

	public QuartzInitializer(ServiceResources serviceResources) {
		super(serviceResources);
	}

	@Override
	protected void initJobs(Scheduler scheduler) throws Exception {
		//TODO: add application specific jobs
	}
}
