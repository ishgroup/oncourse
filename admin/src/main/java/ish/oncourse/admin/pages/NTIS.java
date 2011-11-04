package ish.oncourse.admin.pages;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import ish.oncourse.admin.services.ntis.INTISUpdater;
import ish.oncourse.admin.services.ntis.NTISResult;
import ish.oncourse.model.Module;
import ish.oncourse.model.Qualification;
import ish.oncourse.model.TrainingPackage;

import org.apache.log4j.Logger;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

public class NTIS {
	
	private static final Logger LOGGER = Logger.getLogger(NTIS.class);
	
	private boolean isUpdate;
	
	@Inject
	private INTISUpdater ntisUpdater;
	
	@Property
	private String dateFrom;
	
	@Property
	private String dateTo;

	@SetupRender
	void setupRender() {
		
	}
	
	@OnEvent(component = "update", value = "selected")
	void onSelectedUpdate() {
		this.isUpdate = true;
	}
	
	@OnEvent(component = "updateForm", value = "success")
	void submitted() throws Exception {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
		Date from = dateFormat.parse(dateFrom);
		Date to = dateFormat.parse(dateTo);
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(from);
		
		while (cal.getTime().before(to)) {
			
			Date fromDate = cal.getTime();
			cal.add(Calendar.MONTH, 1);
			Date toDate = cal.getTime();
			if (toDate.after(to)) {
				toDate = to;
			}
			
			LOGGER.debug("Updating records from " + fromDate + " to " + toDate);
			
			NTISResult moduleResult = ntisUpdater.doUpdate(fromDate, toDate, Module.class);
			LOGGER.debug("Modules: " + moduleResult.getNumberOfNew() + " new, " + moduleResult.getNumberOfUpdated() + " updated.");
			
			NTISResult qualificationResult = ntisUpdater.doUpdate(fromDate, toDate, Qualification.class);
			LOGGER.debug("Qualifications: " + qualificationResult.getNumberOfNew() + 
					" new, " + qualificationResult.getNumberOfUpdated() + " updated.");
			
			NTISResult trainingPackageResult = ntisUpdater.doUpdate(fromDate, toDate, TrainingPackage.class);
			LOGGER.debug("Training Packages: " + trainingPackageResult.getNumberOfNew() + " new, " + 
					trainingPackageResult.getNumberOfUpdated() + " updated.");
		}
	}
}
