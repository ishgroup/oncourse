package ish.oncourse.cms.components;

import ish.oncourse.model.College;
import ish.oncourse.model.CustomFieldType;
import ish.oncourse.selectutils.StringSelectModel;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.preference.ContactFieldHelper;
import ish.oncourse.services.preference.PreferenceController;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.ui.pages.internal.Page;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.Ordering;
import org.apache.cayenne.query.SortOrder;
import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import java.util.Arrays;
import java.util.List;

import static ish.oncourse.services.preference.ContactFieldHelper.*;
import static ish.oncourse.services.preference.PreferenceController.ContactFiledsSet.*;
import static ish.oncourse.services.preference.PreferenceController.FieldDescriptor.*;

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
	private String enrolmentHomePhoneState;

	@Property
	private String enrolmentBusinessPhoneState;

	@Property
	private String enrolmentFaxState;

	@Property
	private String enrolmentMobileState;

	@Property
	private String enrolmentDateOfBirthState;

	@Property
	private String enrolmentCountryState;

	@Property
	private String waitingListAddressState;

	@Property
	private String waitingListSuburbState;

	@Property
	private String waitingListStateState;

	@Property
	private String waitingListPostcodeState;

	@Property
	private String waitingListHomePhoneState;

	@Property
	private String waitingListBusinessPhoneState;

	@Property
	private String waitingListFaxState;

	@Property
	private String waitingListMobileState;

	@Property
	private String waitingListDateOfBirthState;

	@Property
	private String waitingListCountryState;

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
	private String mailingListCountryState;
	
	@Property
	@Persist
	private List<CustomFieldType> customFieldTypes;
	
	@Property
	private Integer fieldIndex;

	@Property
	private String enrolmentMinAge;

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
		updateFields();
	}

	private void updateFields() {
		this.stateSelectModel = new StringSelectModel(VALUE_Show, VALUE_Hide, VALUE_Required);

		this.avetmissQuestionsEnabled = webSiteService.getCurrentCollege().getRequiresAvetmiss() != null ?
				webSiteService.getCurrentCollege().getRequiresAvetmiss() : false;

		this.enrolmentAddressState = preferenceController.getRequireContactField(enrolment, street);
		this.enrolmentSuburbState = preferenceController.getRequireContactField(enrolment, suburb);
		this.enrolmentStateState = preferenceController.getRequireContactField(enrolment, state);
		this.enrolmentPostcodeState = preferenceController.getRequireContactField(enrolment, postcode);
		this.enrolmentHomePhoneState = preferenceController.getRequireContactField(enrolment, homePhoneNumber);
		this.enrolmentBusinessPhoneState = preferenceController.getRequireContactField(enrolment, businessPhoneNumber);
		this.enrolmentFaxState = preferenceController.getRequireContactField(enrolment, faxNumber);
		this.enrolmentMobileState = preferenceController.getRequireContactField(enrolment, mobilePhoneNumber);
		this.enrolmentDateOfBirthState = preferenceController.getRequireContactField(enrolment, dateOfBirth);
		this.enrolmentCountryState = preferenceController.getRequireContactField(enrolment, country);
		this.enrolmentMinAge = preferenceController.getEnrolmentMinAge().toString();

		this.waitingListAddressState = preferenceController.getRequireContactField(waitinglist, street);
		this.waitingListSuburbState = preferenceController.getRequireContactField(waitinglist, suburb);
		this.waitingListStateState = preferenceController.getRequireContactField(waitinglist, state);
		this.waitingListPostcodeState = preferenceController.getRequireContactField(waitinglist, postcode);
		this.waitingListHomePhoneState = preferenceController.getRequireContactField(waitinglist, homePhoneNumber);
		this.waitingListBusinessPhoneState = preferenceController.getRequireContactField(waitinglist, businessPhoneNumber);
		this.waitingListFaxState = preferenceController.getRequireContactField(waitinglist, faxNumber);
		this.waitingListMobileState = preferenceController.getRequireContactField(waitinglist, mobilePhoneNumber);
		this.waitingListDateOfBirthState = preferenceController.getRequireContactField(waitinglist, dateOfBirth);
		this.waitingListCountryState = preferenceController.getRequireContactField(waitinglist, country);

		this.mailingListAddressState = preferenceController.getRequireContactField(mailinglist, street);
		this.mailingListSuburbState = preferenceController.getRequireContactField(mailinglist, suburb);
		this.mailingListStateState = preferenceController.getRequireContactField(mailinglist, state);
		this.mailingListPostcodeState = preferenceController.getRequireContactField(mailinglist, postcode);
		this.mailingListHomePhoneState = preferenceController.getRequireContactField(mailinglist, homePhoneNumber);
		this.mailingListBusinessPhoneState = preferenceController.getRequireContactField(mailinglist, businessPhoneNumber);
		this.mailingListFaxState = preferenceController.getRequireContactField(mailinglist, faxNumber);
		this.mailingListMobileState = preferenceController.getRequireContactField(mailinglist, mobilePhoneNumber);
		this.mailingListDateOfBirthState = preferenceController.getRequireContactField(mailinglist, dateOfBirth);
		this.mailingListCountryState = preferenceController.getRequireContactField(mailinglist, country);
		
		this.customFieldTypes = webSiteService.getCurrentCollege().getCustomFieldTypes();

		// order custom field types alphabetically by name
		Ordering.orderList(customFieldTypes, 
				Arrays.asList(new Ordering(CustomFieldType.NAME_PROPERTY, SortOrder.ASCENDING_INSENSITIVE)));
	}
	
	public CustomFieldType getCurrentField() {
		return customFieldTypes.get(fieldIndex);
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
		
		for (CustomFieldType customFieldType : customFieldTypes) {
			CustomFieldType localCustomFieldType = context.localObject(customFieldType);
			
			localCustomFieldType.setRequireForEnrolment(customFieldType.getRequireForEnrolment());
			localCustomFieldType.setRequireForWaitingList(customFieldType.getRequireForWaitingList());
			localCustomFieldType.setRequireForMailingList(customFieldType.getRequireForMailingList());
		}

		context.commitChanges();

		if (!StringUtils.isNumeric(this.enrolmentMinAge))
			this.enrolmentMinAge = "0";
		preferenceController.setEnrolmentMinAge(Integer.valueOf(this.enrolmentMinAge));


		preferenceController.setRequireContactField(enrolment, street, this.enrolmentAddressState);
		preferenceController.setRequireContactField(enrolment, suburb, this.enrolmentSuburbState);
		preferenceController.setRequireContactField(enrolment, state, this.enrolmentStateState);
		preferenceController.setRequireContactField(enrolment, postcode, this.enrolmentPostcodeState);
		preferenceController.setRequireContactField(enrolment, homePhoneNumber, this.enrolmentHomePhoneState);
		preferenceController.setRequireContactField(enrolment, businessPhoneNumber, this.enrolmentBusinessPhoneState);
		preferenceController.setRequireContactField(enrolment, faxNumber, this.enrolmentFaxState);
		preferenceController.setRequireContactField(enrolment, mobilePhoneNumber, this.enrolmentMobileState);
		preferenceController.setRequireContactField(enrolment, dateOfBirth, preferenceController.getEnrolmentMinAge() > 0 ? ContactFieldHelper.VALUE_Required:
			this.enrolmentDateOfBirthState);
		this.enrolmentDateOfBirthState = preferenceController.getRequireContactField(enrolment, dateOfBirth);
		preferenceController.setRequireContactField(enrolment, country, this.enrolmentCountryState);



		preferenceController.setRequireContactField(waitinglist, street, this.waitingListAddressState);
		preferenceController.setRequireContactField(waitinglist, suburb, this.waitingListSuburbState);
		preferenceController.setRequireContactField(waitinglist, state, this.waitingListStateState);
		preferenceController.setRequireContactField(waitinglist, postcode, this.waitingListPostcodeState);
		preferenceController.setRequireContactField(waitinglist, homePhoneNumber, this.waitingListHomePhoneState);
		preferenceController.setRequireContactField(waitinglist, businessPhoneNumber, this.waitingListBusinessPhoneState);
		preferenceController.setRequireContactField(waitinglist, faxNumber, this.waitingListFaxState);
		preferenceController.setRequireContactField(waitinglist, mobilePhoneNumber, this.waitingListMobileState);
		preferenceController.setRequireContactField(waitinglist, dateOfBirth, this.waitingListDateOfBirthState);
		preferenceController.setRequireContactField(waitinglist, country, this.waitingListCountryState);


		preferenceController.setRequireContactField(mailinglist, street, this.mailingListAddressState);
		preferenceController.setRequireContactField(mailinglist, suburb, this.mailingListSuburbState);
		preferenceController.setRequireContactField(mailinglist, state, this.mailingListStateState);
		preferenceController.setRequireContactField(mailinglist, postcode, this.mailingListPostcodeState);
		preferenceController.setRequireContactField(mailinglist, homePhoneNumber, this.mailingListHomePhoneState);
		preferenceController.setRequireContactField(mailinglist, businessPhoneNumber, this.mailingListBusinessPhoneState);
		preferenceController.setRequireContactField(mailinglist, faxNumber, this.mailingListFaxState);
		preferenceController.setRequireContactField(mailinglist, mobilePhoneNumber, this.mailingListMobileState);
		preferenceController.setRequireContactField(mailinglist, dateOfBirth, this.mailingListDateOfBirthState);
		preferenceController.setRequireContactField(mailinglist, country, this.mailingListCountryState);

	}

	public Zone getSettingsZone() {
		return settingsZone;
	}

	Object onActionFromCancelLink() {
		beforeRender();

		return settingsZone;
	}

}
