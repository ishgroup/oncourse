package ish.oncourse.admin.pages;

import ish.oncourse.admin.services.ntis.INTISUpdater;
import ish.oncourse.admin.services.ntis.NTISTask;
import ish.oncourse.services.mail.IMailService;
import ish.oncourse.services.preference.PreferenceController;
import ish.oncourse.services.threading.ThreadSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.Response;
import org.apache.tapestry5.services.Session;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class NTIS {

	private static final Logger logger = LogManager.getLogger();

	private static final String NTIS_DATA_ATTR = "NTISData";
	private static final String NTIS_UPDATE_STARTED_ATTR = "isNTISStarted";

	@Inject
	private INTISUpdater ntisUpdater;

	@Inject
	private PreferenceController preferenceController;

	@Inject
	private Request request;
	
	@Inject
	private Response response;

	@Inject
	private ThreadSource threadSource;
	
	@Inject
	private IMailService mailService;
	
	@InjectComponent
	private Form updateForm;

	@Property
	private String dateFrom;

	@Property
	private String dateTo;

	@Property
	private String ntisResultUrl;

	@Property
	private String lastUpdateDate;

	@SetupRender
	void setupRender() {
		this.ntisResultUrl = response.encodeURL(request.getContextPath() + "/NTISJson");
		String lastUpdate = preferenceController.getNTISLastUpdate();
		
		if (lastUpdate != null) {
			this.lastUpdateDate = preferenceController.getNTISLastUpdate();
			this.dateFrom = lastUpdate;
		} else {
			this.lastUpdateDate = "NEVER";
		}
	}
	
	/**
	 * Checks if already running update.
	 * @return true/false
	 */
	public boolean isUpdateInProgress() {
		Session session = request.getSession(false);
		Boolean started = (Boolean) session.getAttribute(NTIS_UPDATE_STARTED_ATTR);
		return Boolean.TRUE.equals(started);
	}
	
	@OnEvent(component = "updateForm", value = "validate")
	void validate() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		try {
			dateFormat.parse(dateFrom);
			dateFormat.parse(dateTo);
		} catch (ParseException e) {
			updateForm.recordError("Date format doesn't match dd/MM/yyyy");
		}
	}

	@OnEvent(component = "updateForm", value = "success")
	void submitted() throws Exception {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

		final Date from = dateFormat.parse(dateFrom);
		final Date to = dateFormat.parse(dateTo);
		final Session session = request.getSession(false);
		final INTISUpdater updater = ntisUpdater;
		final PreferenceController preferenceController = this.preferenceController;

		synchronized (session) {
			Boolean started = (Boolean) session.getAttribute(NTIS_UPDATE_STARTED_ATTR);
			if (started == null) {
				session.setAttribute(NTIS_UPDATE_STARTED_ATTR, true);
				threadSource.runInThread(new SessionBoundNTISTask(from, to, session, updater, preferenceController, mailService));
			}
		}
	}

	private static class SessionBoundNTISTask extends NTISTask {

		private Session session;

		public SessionBoundNTISTask(Date from, Date to, Session session, INTISUpdater ntisUpdater, 
				PreferenceController preferenceController, IMailService mailService) {
			super(from, to, ntisUpdater, preferenceController, mailService);
			this.session = session;
		}

		/*
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			try {
				synchronized (session) {
					session.setAttribute(NTIS_DATA_ATTR, ntisData);
				}
				
				super.run();
				
			} finally {
				synchronized (session) {
					session.setAttribute(NTIS_UPDATE_STARTED_ATTR, null);
				}
			}
		}
	}
}
