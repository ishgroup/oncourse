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
import ish.oncourse.services.threading.ThreadSource;

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

	@Inject
	private INTISUpdater ntisUpdater;

	@Inject
	private Request request;

	@Inject
	private ThreadSource threadSource;

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

	@OnEvent(component = "updateForm", value = "success")
	void submitted() throws Exception {

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
		
		final Date from = dateFormat.parse(dateFrom);
		final Date to = dateFormat.parse(dateTo);
		final Session session = request.getSession(false);
		final INTISUpdater updater = ntisUpdater;
		
		threadSource.runInThread(new NTISTask(from, to, session, updater));
	}
	
	private static class NTISTask implements Runnable {

		private Date from;
		private Date to;
		private Session session;
		private INTISUpdater ntisUpdater;
		
		public NTISTask(Date from, Date to, Session session, INTISUpdater ntisUpdater) {
			super();
			this.from = from;
			this.to = to;
			this.session = session;
			this.ntisUpdater = ntisUpdater;
		}

		/*
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			
			List<String> ntisData = new LinkedList<String>();
			
			synchronized (session) {
				session.setAttribute(NTIS_DATA_ATTR, ntisData);
			}

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
				ntisData.add("Updating records from " + fromDate + " to " + toDate);

				try {

					NTISResult moduleResult = ntisUpdater.doUpdate(fromDate, toDate, Module.class);

					LOGGER.debug("Modules: " + moduleResult.getNumberOfNew() + " new, " + moduleResult.getNumberOfUpdated()
							+ " updated.");
					ntisData.add("Modules: " + moduleResult.getNumberOfNew() + " new, " + moduleResult.getNumberOfUpdated()
							+ " updated.");

					NTISResult qualificationResult = ntisUpdater.doUpdate(fromDate, toDate, Qualification.class);

					LOGGER.debug("Qualifications: " + qualificationResult.getNumberOfNew() + " new, "
							+ qualificationResult.getNumberOfUpdated() + " updated.");

					ntisData.add("Qualifications: " + qualificationResult.getNumberOfNew() + " new, "
							+ qualificationResult.getNumberOfUpdated() + " updated.");

					NTISResult trainingPackageResult = ntisUpdater.doUpdate(fromDate, toDate, TrainingPackage.class);

					LOGGER.debug("Training Packages: " + trainingPackageResult.getNumberOfNew() + " new, "
							+ trainingPackageResult.getNumberOfUpdated() + " updated.");

					ntisData.add("Training Packages: " + trainingPackageResult.getNumberOfNew() + " new, "
							+ trainingPackageResult.getNumberOfUpdated() + " updated.");

				} catch (Exception e) {
					LOGGER.error("NTIS update failed with exception.", e);
					ntisData.add(String.format("NTIS update failed with exception:%s", e.getMessage()));
				}
			}

			ntisData.add("Update finished.");
		}
	}
}
