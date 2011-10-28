package ish.oncourse.admin.services.ntis;


import org.apache.tapestry5.ioc.annotations.Inject;

import au.gov.training.services.trainingcomponent.ITrainingComponentService;

public class NTISUpdaterImpl implements INTISUpdater {

	@Inject
	private ITrainingComponentService trainingService;
	
	/* (non-Javadoc)
	 * @see ish.oncourse.admin.services.ntis.INTISUpdateService#doUpdate()
	 */
	@Override
	public NTISResult doUpdate() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
