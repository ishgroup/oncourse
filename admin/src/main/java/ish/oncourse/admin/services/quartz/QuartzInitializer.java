package ish.oncourse.admin.services.quartz;

import ish.oncourse.webservices.jobs.GenericQuartzInitializer;

import org.apache.tapestry5.ioc.ServiceResources;
import org.quartz.Scheduler;

/**
 * Initializes specific admin app quartz jobs.
 * @author anton
 *
 */
public class QuartzInitializer extends GenericQuartzInitializer {
	
	public QuartzInitializer(ServiceResources serviceResources) {
		super(serviceResources);
	}
	
	@Override
	protected void initJobs(Scheduler scheduler) throws Exception {
		
	}
}
