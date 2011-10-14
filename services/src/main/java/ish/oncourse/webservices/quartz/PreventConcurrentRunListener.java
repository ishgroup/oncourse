package ish.oncourse.webservices.quartz;

import java.util.List;

import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.Trigger.CompletedExecutionInstruction;

public class PreventConcurrentRunListener implements org.quartz.TriggerListener {

	private static final Logger logger = Logger.getLogger(PreventConcurrentRunListener.class);
	
	/**
	 * @see org.quartz.TriggerListener#getName()
	 */
	@Override
	public String getName() {
		return "PreventConcurrentRunListener";
	}

	
	/**
	 * @see org.quartz.TriggerListener#triggerFired(org.quartz.Trigger, org.quartz.JobExecutionContext)
	 */
	@Override
	public void triggerFired(Trigger trigger, JobExecutionContext context) {
		
		
	}

	/**
	 * @see org.quartz.TriggerListener#vetoJobExecution(org.quartz.Trigger, org.quartz.JobExecutionContext)
	 */
	@Override
	public boolean vetoJobExecution(Trigger trigger, JobExecutionContext context) {
		
		try {
			List<JobExecutionContext> jobs = context.getScheduler().getCurrentlyExecutingJobs();
			
			for (JobExecutionContext job : jobs) {
				
				if (job.getTrigger().equals(context.getTrigger()) && !job.getJobInstance().equals(this)) {
					logger.info("There's another instance running, so leaving " + job);
					return true;
				}

			}
			
		} catch (SchedulerException e) {
			logger.warn("Unable to veto Quartz job.", e);
		}
		
		return false;
	}

	/**
	 * @see org.quartz.TriggerListener#triggerMisfired(org.quartz.Trigger)
	 */
	@Override
	public void triggerMisfired(Trigger trigger) {
		
	}

	/**
	 * @see org.quartz.TriggerListener#triggerComplete(org.quartz.Trigger, org.quartz.JobExecutionContext, org.quartz.Trigger.CompletedExecutionInstruction)
	 */
	@Override
	public void triggerComplete(Trigger trigger, JobExecutionContext context, CompletedExecutionInstruction triggerInstructionCode) {
		
	}
}
