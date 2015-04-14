package ish.oncourse.admin.services.ntis;

import ish.oncourse.model.Module;
import ish.oncourse.model.Organisation;
import ish.oncourse.model.Qualification;
import ish.oncourse.model.TrainingPackage;
import ish.oncourse.services.mail.IMailService;
import ish.oncourse.services.preference.PreferenceController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class NTISTask implements Runnable {

	private static final String EMAIL_FROM = "support@ish.com.au";
	private static final String EMAIL_TO = "support@ish.com.au";
	private static final String EMAIL_SUBJECT = "NTIS Data Update";
	private static Logger logger = LogManager.getLogger();
	protected List<String> ntisData;
	private Date from;
	private Date to;
	private INTISUpdater ntisUpdater;
	private PreferenceController preferenceController;
	private IMailService mailService;

	public NTISTask(Date from, Date to, INTISUpdater ntisUpdater, PreferenceController preferenceController, IMailService mailService) {
		super();
		this.from = from;
		this.to = to;
		this.ntisUpdater = ntisUpdater;
		this.preferenceController = preferenceController;
		this.mailService = mailService;

		this.ntisData = new LinkedList<>();
	}

	/*
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {

		Calendar cal = Calendar.getInstance();
		cal.setTime(from);

		while (cal.getTime().before(to)) {

			Date fromDate = cal.getTime();
			cal.add(Calendar.DATE, 1);
			Date toDate = cal.getTime();
			if (toDate.after(to)) {
				toDate = to;
			}

			SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

			String message = String.format("Updating records from %s to %s", dateFormat.format(fromDate), dateFormat.format(toDate));
			logger.debug(message);
			ntisData.add(message);

			try {

				NTISResult trainingPackageResult = ntisUpdater.doUpdate(fromDate, toDate, TrainingPackage.class);
				ntisData.add("Training Packages: " + trainingPackageResult.getNumberOfNew() + " new, " + trainingPackageResult.getNumberOfUpdated() + " updated.");

				NTISResult moduleResult = ntisUpdater.doUpdate(fromDate, toDate, Module.class);
				ntisData.add("Modules: " + moduleResult.getNumberOfNew() + " new, " + moduleResult.getNumberOfUpdated() + " updated.");

				NTISResult qualificationResult = ntisUpdater.doUpdate(fromDate, toDate, Qualification.class);
				ntisData.add("Qualifications: " + qualificationResult.getNumberOfNew() + " new, " + qualificationResult.getNumberOfUpdated() + " updated.");

				NTISResult organisationResult = ntisUpdater.doUpdate(fromDate, toDate, Organisation.class);
				ntisData.add(String.format("Organisations: %d new, %d updated.", organisationResult.getNumberOfNew(), organisationResult.getNumberOfUpdated()));

				preferenceController.setNTISLastUpdate(dateFormat.format(toDate));

			} catch (Exception e) {
				logger.error("NTIS update failed with exception.", e);
				ntisData.add(String.format("NTIS update failed with exception:%s", e.getMessage()));

				// error occured stop advancing dates and report failure.
				break;
			}
		}

		ntisData.add("Update finished.");
		ntisData.add(String.format("END_%s", preferenceController.getNTISLastUpdate()));

		sendResultEmail();
	}

	private void sendResultEmail() {
		StringBuilder emailBody = new StringBuilder();

		for (String line : ntisData) {
			emailBody.append(line);
			emailBody.append("\n");
		}

		mailService.sendMail(EMAIL_FROM, EMAIL_TO, EMAIL_SUBJECT, emailBody.toString());
	}
}