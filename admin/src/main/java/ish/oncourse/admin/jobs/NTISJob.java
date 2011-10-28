package ish.oncourse.admin.jobs;

import ish.oncourse.admin.services.ntis.INTISUpdater;
import ish.oncourse.admin.services.ntis.NTISResult;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

@DisallowConcurrentExecution
public class NTISJob implements Job {
	
	private final INTISUpdater ntisUpdater;
	
	public NTISJob(INTISUpdater ntisUpdater) {
		super();
		this.ntisUpdater = ntisUpdater;
	}

	/* (non-Javadoc)
	 * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
	 */
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		NTISResult result = ntisUpdater.doUpdate();
		//print result of update into log
	}
}
