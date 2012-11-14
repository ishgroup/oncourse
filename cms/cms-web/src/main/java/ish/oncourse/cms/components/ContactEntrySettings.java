package ish.oncourse.cms.components;

import ish.oncourse.model.College;
import ish.oncourse.selectutils.StringSelectModel;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.preference.PreferenceController;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.ui.pages.internal.Page;
import org.apache.cayenne.ObjectContext;
import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import static ish.oncourse.services.preference.ContactFieldHelper.*;

public class ContactEntrySettings {
	
	@SuppressWarnings("all")
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
	private String enrolmentHomePhoneState;
	
	@Property
	private String enrolmentBusinessPhoneState;
	
	@Property
	private String enrolmentFaxState;
	
	@Property
	private String enrolmentMobileState;
	
	@Property
	private String enrolmentDateOfBirthState;
	
	@SuppressWarnings("all")
	@Property
	private String waitingListAddressState;
	
	@SuppressWarnings("all")
	@Property
	private String waitingListSuburbState;
	
	@SuppressWarnings("all")
	@Property
	private String waitingListStateState;
	
	@SuppressWarnings("all")
	@Property
	private String waitingListPostcodeState;
	
	@SuppressWarnings("all")
	@Property
	private String waitingListHomePhoneState;
	
	@SuppressWarnings("all")
	@Property
	private String waitingListBusinessPhoneState;
	
	@SuppressWarnings("all")
	@Property
	private String waitingListFaxState;
	
	@SuppressWarnings("all")
	@Property
	private String waitingListMobileState;
	
	@SuppressWarnings("all")
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
	private String mailingListHomePhoneState;
	
	@Property
	private String mailingListBusinessPhoneState;
	
	@Property
	private String mailingListFaxState;
	
	@Property
	private String mailingListMobileState;
	
	@Property
	private String mailingListDateOfBirthState;

	@Property
	private String enrolmentMinAge;

	@SuppressWarnings("all")
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
	
	@Inject
	private IWebSiteService webSiteService;
	
	@Inject
	private ICayenneService cayenneService;
	
	@SetupRender
	void beforeRender() {
		this.stateSelectModel = new StringSelectModel(VALUE_Show, VALUE_Hide, VALUE_Required);
		
		this.avetmissQuestionsEnabled = webSiteService.getCurrentCollege().getRequiresAvetmiss() != null ? 
				webSiteService.getCurrentCollege().getRequiresAvetmiss() : false;
		
		this.enrolmentAddressState = preferenceController.getRequireContactAddressEnrolment();
		this.enrolmentSuburbState = preferenceController.getRequireContactSuburbEnrolment();
		this.enrolmentStateState = preferenceController.getRequireContactStateEnrolment();
		this.enrolmentPostcodeState = preferenceController.getRequireContactPostcodeEnrolment();
		this.enrolmentHomePhoneState = preferenceController.getRequireContactHomePhoneEnrolment();
		this.enrolmentBusinessPhoneState = preferenceController.getRequireContactBusinessPhoneEnrolment();
		this.enrolmentFaxState = preferenceController.getRequireContactFaxEnrolment();
		this.enrolmentMobileState = preferenceController.getRequireContactMobileEnrolment();
		this.enrolmentDateOfBirthState = preferenceController.getRequireContactDateOfBirthEnrolment();
		this.enrolmentMinAge = preferenceController.getEnrolmentMinAge().toString();

		this.waitingListAddressState = preferenceController.getRequireContactAddressWaitingList();
		this.waitingListSuburbState = preferenceController.getRequireContactSuburbWaitingList();
		this.waitingListStateState = preferenceController.getRequireContactStateWaitingList();
		this.waitingListPostcodeState = preferenceController.getRequireContactPostcodeWaitingList();
		this.waitingListHomePhoneState = preferenceController.getRequireContactHomePhoneWaitingList();
		this.waitingListBusinessPhoneState = preferenceController.getRequireContactBusinessPhoneWaitingList();
		this.waitingListFaxState = preferenceController.getRequireContactFaxWaitingList();
		this.waitingListMobileState = preferenceController.getRequireContactMobileWaitingList();
		this.waitingListDateOfBirthState = preferenceController.getRequireContactDateOfBirthWaitingList();
		
		this.mailingListAddressState = preferenceController.getRequireContactAddressMailingList();
		this.mailingListSuburbState = preferenceController.getRequireContactSuburbMailingList();
		this.mailingListStateState = preferenceController.getRequireContactStateMailingList();
		this.mailingListPostcodeState = preferenceController.getRequireContactPostcodeMailingList();
		this.mailingListHomePhoneState = preferenceController.getRequireContactHomePhoneMailingList();
		this.mailingListBusinessPhoneState = preferenceController.getRequireContactBusinessPhoneMailingList();
		this.mailingListFaxState = preferenceController.getRequireContactFaxMailingList();
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
		
		ObjectContext context = cayenneService.newNonReplicatingContext();
		
		College college = (College) context.localObject(webSiteService.getCurrentCollege().getObjectId(), null);
		
		if (college != null) {
			college.setRequiresAvetmiss(this.avetmissQuestionsEnabled);
		}
		
		context.commitChanges();
		
		preferenceController.setRequireContactAddressEnrolment(this.enrolmentAddressState);
		preferenceController.setRequireContactSuburbEnrolment(this.enrolmentSuburbState);
		preferenceController.setRequireContactStateEnrolment(this.enrolmentStateState);
		preferenceController.setRequireContactPostcodeEnrolment(this.enrolmentPostcodeState);
		preferenceController.setRequireContactHomePhoneEnrolment(this.enrolmentHomePhoneState);
		preferenceController.setRequireContactBusinessPhoneEnrolment(this.enrolmentBusinessPhoneState);
		preferenceController.setRequireContactFaxEnrolment(this.enrolmentFaxState);
		preferenceController.setRequireContactMobileEnrolment(this.enrolmentMobileState);
		preferenceController.setRequireContactDateOfBirthEnrolment(this.enrolmentDateOfBirthState);


		if (!StringUtils.isNumeric(this.enrolmentMinAge))
			this.enrolmentMinAge = "0";
		preferenceController.setEnrolmentMinAge(Integer.valueOf(this.enrolmentMinAge));

		preferenceController.setRequireContactAddressWaitingList(this.waitingListAddressState);
		preferenceController.setRequireContactSuburbWaitingList(this.waitingListSuburbState);
		preferenceController.setRequireContactStateWaitingList(this.waitingListStateState);
		preferenceController.setRequireContactPostcodeWaitingList(this.waitingListPostcodeState);
		preferenceController.setRequireContactHomePhoneWaitingList(this.waitingListHomePhoneState);
		preferenceController.setRequireContactBusinessPhoneWaitingList(this.waitingListBusinessPhoneState);
		preferenceController.setRequireContactFaxWaitingList(this.waitingListFaxState);
		preferenceController.setRequireContactMobileWaitingList(this.waitingListMobileState);
		preferenceController.setRequireContactDateOfBirthWaitingList(this.waitingListDateOfBirthState);
		
		preferenceController.setRequireContactAddressMailingList(this.mailingListAddressState);
		preferenceController.setRequireContactSuburbMailingList(this.mailingListSuburbState);
		preferenceController.setRequireContactStateMailingList(this.mailingListStateState);
		preferenceController.setRequireContactPostcodeMailingList(this.mailingListPostcodeState);
		preferenceController.setRequireContactHomePhoneMailingList(this.mailingListHomePhoneState);
		preferenceController.setRequireContactBusinessPhoneMailingList(this.mailingListBusinessPhoneState);
		preferenceController.setRequireContactFaxMailingList(this.mailingListFaxState);
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
