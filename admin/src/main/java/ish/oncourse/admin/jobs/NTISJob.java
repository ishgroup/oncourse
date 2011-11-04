package ish.oncourse.admin.jobs;

import java.text.SimpleDateFormat;
import java.util.Date;

import ish.oncourse.admin.services.ntis.INTISUpdater;
import ish.oncourse.admin.services.ntis.NTISResult;
import ish.oncourse.model.Module;
import ish.oncourse.model.Qualification;
import ish.oncourse.model.TrainingPackage;

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
		try {
			
			// TODO: date should be picked from preference controller
			
			//SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
			//Date from = dateFormat.parse("2011/04/01");
			//Date to = dateFormat.parse("2011/05/01");
			
			//NTISResult qualificationResult = ntisUpdater.doUpdate(from, to, Module.class);
			//NTISResult moduleResult = ntisUpdater.doUpdate(from, to, Qualification.class);
			//NTISResult trainingPackageResult = ntisUpdater.doUpdate(from, to, TrainingPackage.class);
			
			//print result of update into log
		} catch (Exception e) {
			throw new JobExecutionException("NTIS update exception.");
		}
	}
}
