package ish.oncourse.admin.pages.ntis;

import ish.oncourse.admin.services.ntis.INTISUpdater;
import ish.oncourse.admin.services.ntis.NTISTask;
import ish.oncourse.services.mail.IMailService;
import ish.oncourse.services.preference.PreferenceController;
import ish.oncourse.services.threading.ThreadSource;
import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.util.TextStreamResponse;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class Update {
	
	private static final String INITIAL_FROM_DATE = "01/01/1990";
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
	
	@Inject
	private IMailService mailService;
	
	@Inject
	private INTISUpdater ntisUpdater;
	
	@Inject
	private ThreadSource threadSource;
	
	@Inject
	private PreferenceController preferenceController;
	
	public StreamResponse onActivate() {
		
		String lastUpdate = preferenceController.getNTISLastUpdate();
		Date from = null;
		
		try {
			from = lastUpdate != null ? DATE_FORMAT.parse(lastUpdate) : DATE_FORMAT.parse(INITIAL_FROM_DATE);
		} catch (ParseException e) {
			throw new IllegalStateException("Illegal date format for last NTIS update preference: " + lastUpdate, e);
		}
		
		Date to = new Date();
		
		threadSource.runInThread(new NTISTask(from, to, ntisUpdater, preferenceController, mailService));
		
		return new TextStreamResponse("text/html", "OK");
	}

}
