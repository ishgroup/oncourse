package ish.oncourse.cms.components;

import ish.oncourse.services.preference.PreferenceController;
import ish.oncourse.ui.pages.internal.Page;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

public class PortalSettings {
	
	@Property
	private boolean hideDetails;
	
	@Property
	private String feedbackEmail;
	
	@Property
	private boolean outcomeMarkingViaPortal;
	
	@SuppressWarnings("all")
	@Property
	private boolean saved;
	
	@InjectComponent
	@Property
	private Form portalSettingsForm;
	
	@Component
	private Zone portalSettingsZone;
	
	@Inject
	private Request request;
	
	@Inject
	private PreferenceController preferenceController;
	
	@InjectPage
	private Page page;
	
	@SetupRender
	void beforeRender() {
		hideDetails = preferenceController.getHideStudentDetailsFromTutor();
		feedbackEmail = preferenceController.getTutorFeedbackEmail();
		outcomeMarkingViaPortal = preferenceController.getOutcomeMarkingViaPortal();
	}
	
	@AfterRender
	void afterRender() {
		saved = false;
	}
	
	Object onSuccessFromPortalSettingsForm() {
		if (request.getSession(false) == null) {
			return page.getReloadPageBlock();
		}
		portalSettingsForm.clearErrors();
		
		preferenceController.setHideStudentDetailsFromTutor(hideDetails);
		preferenceController.setTutorFeedbackEmail(feedbackEmail);
		preferenceController.setOutcomeMarkingViaPortal(outcomeMarkingViaPortal);
		
		saved = true;
		
		return portalSettingsZone.getBody();
	}
	
	public Zone getPortalSettingsZone() {
		return portalSettingsZone;
	}

}
