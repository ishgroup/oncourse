package ish.oncourse.cms.components;

import ish.oncourse.selectutils.StringSelectModel;
import ish.oncourse.services.preference.PreferenceController;
import ish.oncourse.ui.pages.internal.Page;

import org.apache.tapestry5.ajax.MultiZoneUpdate;
import org.apache.tapestry5.annotations.AfterRender;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.corelib.components.Hidden;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

public class ContactEntrySettings {
	
	@Property
	@Persist
	private StringSelectModel stateSelectModel;
	
	@Property
	private boolean avetmissQuestionsEnabled;
	
	@Property
	private String enrolmentAddressState;
	
	@Property
	private String enrolmentSuburbState;
	
	@Property
	private String enrolmentStateState;
	
	@Property
	private String enrolmentPostcodeState;
	
	@Property
	private String enrolmentPhoneState;
	
	@Property
	private String enrolmentMobileState;
	
	@Property
	private String enrolmentDateOfBirthState;
	
	@Property
	private String waitingListAddressState;
	
	@Property
	private String waitingListSuburbState;
	
	@Property
	private String waitingListStateState;
	
	@Property
	private String waitingListPostcodeState;
	
	@Property
	private String waitingListPhoneState;
	
	@Property
	private String waitingListMobileState;
	
	@Property
	private String waitingListDateOfBirthState;
	
	@Property
	private String mailingListAddressState;
	
	@Property
	private String mailingListSuburbState;
	
	@Property
	private String mailingListStateState;
	
	@Property
	private String mailingListPostcodeState;
	
	@Property
	private String mailingListPhoneState;
	
	@Property
	private String mailingListMobileState;
	
	@Property
	private String mailingListDateOfBirthState;
	
	@Property
	private boolean saved;
	
	@Property
	@InjectComponent
	private Form settingsForm;
	
	@Component
	private Zone settingsZone;
	
	@Inject
	private Request request;
	
	@InjectPage
	private Page page;
	
	@Inject
	private PreferenceController preferenceController;
	
	@SetupRender
	void beforeRender() {
		this.stateSelectModel = new StringSelectModel(new String[] {"Hide", "Show", "Required"});
		
		this.avetmissQuestionsEnabled = preferenceController.getAvetmissOptionalQuestionsEnabled();
		
		this.enrolmentAddressState = preferenceController.getRequireContactAddressEnrolment();
		this.enrolmentSuburbState = preferenceController.getRequireContactSuburbEnrolment();
		this.enrolmentStateState = preferenceController.getRequireContactStateEnrolment();
		this.enrolmentPostcodeState = preferenceController.getRequireContactPostcodeEnrolment();
		this.enrolmentPhoneState = preferenceController.getRequireContactPhoneEnrolment();
		this.enrolmentMobileState = preferenceController.getRequireContactMobileEnrolment();
		this.enrolmentDateOfBirthState = preferenceController.getRequireContactDateOfBirthEnrolment();
		
		this.waitingListAddressState = preferenceController.getRequireContactAddressWaitingList();
		this.waitingListSuburbState = preferenceController.getRequireContactSuburbWaitingList();
		this.waitingListStateState = preferenceController.getRequireContactStateWaitingList();
		this.waitingListPostcodeState = preferenceController.getRequireContactPostcodeWaitingList();
		this.waitingListPhoneState = preferenceController.getRequireContactPhoneWaitingList();
		this.waitingListMobileState = preferenceController.getRequireContactMobileWaitingList();
		this.waitingListDateOfBirthState = preferenceController.getRequireContactDateOfBirthWaitingList();
		
		this.mailingListAddressState = preferenceController.getRequireContactAddressMailingList();
		this.mailingListSuburbState = preferenceController.getRequireContactSuburbMailingList();
		this.mailingListStateState = preferenceController.getRequireContactStateMailingList();
		this.mailingListPostcodeState = preferenceController.getRequireContactPostcodeMailingList();
		this.mailingListPhoneState = preferenceController.getRequireContactPhoneMailingList();
		this.mailingListMobileState = preferenceController.getRequireContactMobileMailingList();
		this.mailingListDateOfBirthState = preferenceController.getRequireContactDateOfBirthMailingList();
	}
	
	@AfterRender
	void afterRender() {
		saved = false;
	}
	
	Object onSuccessFromSettingsForm() {
		if (request.getSession(false) == null) {
			return page.getReloadPageBlock();
		}
		
		settingsForm.clearErrors();
		
		savePreferences();
		saved = true;
		
		return settingsZone.getBody();
	}
	
	private void savePreferences() {
		
		preferenceController.setAvetmissOptionalQuestionsEnabled(avetmissQuestionsEnabled);
		
		preferenceController.setRequireContactAddressEnrolment(this.enrolmentAddressState);
		preferenceController.setRequireContactSuburbEnrolment(this.enrolmentSuburbState);
		preferenceController.setRequireContactStateEnrolment(this.enrolmentStateState);
		preferenceController.setRequireContactPostcodeEnrolment(this.enrolmentPostcodeState);
		preferenceController.setRequireContactPhoneEnrolment(this.enrolmentPhoneState);
		preferenceController.setRequireContactMobileEnrolment(this.enrolmentMobileState);
		preferenceController.setRequireContactDateOfBirthEnrolment(this.enrolmentDateOfBirthState);
		
		preferenceController.setRequireContactAddressWaitingList(this.waitingListAddressState);
		preferenceController.setRequireContactSuburbWaitingList(this.waitingListSuburbState);
		preferenceController.setRequireContactStateWaitingList(this.waitingListStateState);
		preferenceController.setRequireContactPostcodeWaitingList(this.waitingListPostcodeState);
		preferenceController.setRequireContactPhoneWaitingList(this.waitingListPhoneState);
		preferenceController.setRequireContactMobileWaitingList(this.waitingListMobileState);
		preferenceController.setRequireContactDateOfBirthWaitingList(this.waitingListDateOfBirthState);
		
		preferenceController.setRequireContactAddressMailingList(this.mailingListAddressState);
		preferenceController.setRequireContactSuburbMailingList(this.mailingListSuburbState);
		preferenceController.setRequireContactStateMailingList(this.mailingListStateState);
		preferenceController.setRequireContactPostcodeMailingList(this.mailingListPostcodeState);
		preferenceController.setRequireContactPhoneMailingList(this.mailingListPhoneState);
		preferenceController.setRequireContactMobileMailingList(this.mailingListMobileState);
		preferenceController.setRequireContactDateOfBirthMailingList(this.mailingListDateOfBirthState);
	}
	
	public Zone getSettingsZone() {
		return settingsZone;
	}
	
	Object onActionFromCancelLink() {
		beforeRender();
		
		return settingsZone;
	}
	
}
