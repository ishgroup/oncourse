package ish.oncourse.admin.pages;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

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
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.Session;

public class NTIS {
	
	private static final Logger LOGGER = Logger.getLogger(NTIS.class);
	
	private static final String NTIS_DATA_ATTR = "NTISData";
	
	private boolean isUpdate;
	
	@Inject
	private INTISUpdater ntisUpdater;
	
	@Inject
	private Request request;
	
	@Property
	private String dateFrom;
	
	@Property
	private String dateTo;
	
	@Property
	private String ntisResultUrl;

	@SetupRender
	void setupRender() {
		this.ntisResultUrl = request.getContextPath() + "/NTISJson";
	}
	
	@OnEvent(component = "update", value = "selected")
	void onSelectedUpdate() {
		this.isUpdate = true;
	}
	
	@OnEvent(component = "updateForm", value = "success")
	void submitted() throws Exception {
		
		List<String> ntisData = new LinkedList<String>();
		Session session = request.getSession(false);
		session.setAttribute(NTIS_DATA_ATTR, ntisData);
		
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
			ntisData = (LinkedList<String>) session.getAttribute(NTIS_DATA_ATTR);
			ntisData.add("Updating records from " + fromDate + " to " + toDate);
			request.getSession(false).setAttribute(NTIS_DATA_ATTR, ntisData);
			
			NTISResult moduleResult = ntisUpdater.doUpdate(fromDate, toDate, Module.class);
			LOGGER.debug("Modules: " + moduleResult.getNumberOfNew() + " new, " + moduleResult.getNumberOfUpdated() + " updated.");
			ntisData = (LinkedList<String>) session.getAttribute(NTIS_DATA_ATTR);
			ntisData.add("Modules: " + moduleResult.getNumberOfNew() + " new, " + moduleResult.getNumberOfUpdated() + " updated.");
			request.getSession(false).setAttribute(NTIS_DATA_ATTR, ntisData);
			
			NTISResult qualificationResult = ntisUpdater.doUpdate(fromDate, toDate, Qualification.class);
			LOGGER.debug("Qualifications: " + qualificationResult.getNumberOfNew() + 
					" new, " + qualificationResult.getNumberOfUpdated() + " updated.");
			ntisData = (LinkedList<String>) session.getAttribute(NTIS_DATA_ATTR);
			ntisData.add("Qualifications: " + qualificationResult.getNumberOfNew() + 
					" new, " + qualificationResult.getNumberOfUpdated() + " updated.");
			request.getSession(false).setAttribute(NTIS_DATA_ATTR, ntisData);
			
			NTISResult trainingPackageResult = ntisUpdater.doUpdate(fromDate, toDate, TrainingPackage.class);
			LOGGER.debug("Training Packages: " + trainingPackageResult.getNumberOfNew() + " new, " + 
					trainingPackageResult.getNumberOfUpdated() + " updated.");
			ntisData = (LinkedList<String>) session.getAttribute(NTIS_DATA_ATTR);
			ntisData.add("Training Packages: " + trainingPackageResult.getNumberOfNew() + " new, " + 
					trainingPackageResult.getNumberOfUpdated() + " updated.");
			request.getSession(false).setAttribute(NTIS_DATA_ATTR, ntisData);
		}
		
		ntisData = (LinkedList<String>) session.getAttribute(NTIS_DATA_ATTR);
		ntisData.add("Update finished.");
		request.getSession(false).setAttribute(NTIS_DATA_ATTR, ntisData);
	}
}
