package ish.oncourse.enrol.waitinglist;

import ish.oncourse.enrol.checkout.ConcessionDelegate;
import ish.oncourse.enrol.checkout.ContactCredentialsEncoder;
import ish.oncourse.enrol.checkout.contact.AddContactDelegate;
import ish.oncourse.enrol.checkout.contact.ContactCredentials;
import ish.oncourse.enrol.checkout.contact.ContactEditorDelegate;
import ish.oncourse.enrol.services.student.IStudentService;
import ish.oncourse.model.College;
import ish.oncourse.model.Contact;
import ish.oncourse.services.preference.ContactFieldHelper;
import ish.oncourse.services.preference.PreferenceController;
import org.apache.cayenne.ObjectContext;
import org.apache.tapestry5.ioc.Messages;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ish.oncourse.services.preference.PreferenceController.ContactFieldSet;


public abstract class AContactController implements AddContactDelegate, ContactEditorDelegate {


	private IStudentService studentService;
	private ObjectContext objectContext;
	private PreferenceController preferenceController;
	private ContactFieldHelper contactFieldHelper;
	private ContactFieldSet contactFieldSet;

	private College college;

	private ContactCredentials contactCredentials = new ContactCredentials();
	private Contact contact;

	private Map<String, String> errors = new HashMap<>();
	private Map<String, String> warnings = new HashMap<>();

	private List<String> visibleFields;

	private boolean isFillRequiredProperties = false;
	private State state = State.INIT;

	private Messages messages;

	private String specialNeeds;

	/**
	 * AddContactDelegate implementation
	 */
	@Override
	public void resetContact() {
	}

    /**
     * Default value for this fields should be true
     * because mailing-list and waiting-list features
     * mean to use some kind of notification
     */
    protected void applyMarketingValues()
    {
        contact.setIsMarketingViaEmailAllowed(Boolean.TRUE);
        contact.setIsMarketingViaPostAllowed(Boolean.TRUE);
        contact.setIsMarketingViaSMSAllowed(Boolean.TRUE);
    }

	@Override
	public void addContact() {
		ContactCredentialsEncoder contactCredentialsEncoder = new ContactCredentialsEncoder();
		contactCredentialsEncoder.setContactCredentials(contactCredentials);
		contactCredentialsEncoder.setCollege(college);
		contactCredentialsEncoder.setObjectContext(objectContext);
		contactCredentialsEncoder.setStudentService(studentService);
		contactCredentialsEncoder.encode();
		contact = contactCredentialsEncoder.getContact();
		specialNeeds = contact.getStudent() != null ? contact.getStudent().getSpecialNeeds(): null;

		if (contact.getObjectId().isTemporary()) {
			if (contactFieldHelper.hasVisibleFields(contact)) {
				visibleFields = contactFieldHelper.getVisibleFields(contact, false);
				state = State.EDIT_CONTACT;
			} else {
				saveContact();
			}
		} else {
			isFillRequiredProperties = !(contactFieldHelper.isAllRequiredFieldFilled(contact));
			if (isFillRequiredProperties) {
				visibleFields = contactFieldHelper.getVisibleFields(contact, true);
				state = State.EDIT_CONTACT;
			} else {
				saveContact();
			}
		}
	}

	public void init() {
		contactFieldHelper = new ContactFieldHelper(preferenceController, contactFieldSet);
		state = State.ADD_CONTACT;
	}

	@Override
	public ContactCredentials getContactCredentials() {
		return contactCredentials;
	}

	//ContactEditorDelegate implementation
	@Override
	public Contact getContact() {
		return contact;
	}

	protected void setContact(Contact contact) {
		this.contact = contact;
		this.specialNeeds = this.contact.getStudent() != null ? this.getContact().getStudent().getSpecialNeeds(): null;
	}

	@Override
	public boolean isFillRequiredProperties() {
		return isFillRequiredProperties;
	}

	protected void setFillRequiredProperties(boolean fillRequiredProperties) {
		isFillRequiredProperties = fillRequiredProperties;
	}


	@Override
	public void saveContact() {
        applyMarketingValues();
		objectContext.commitChanges();
		state = State.FINISHED;
	}

	@Override
	public List<String> getVisibleFields() {
		return visibleFields;
	}

	@Override
	public void cancelContact() {
		objectContext.rollbackChanges();
	}

	public void addError(String key, String error) {
		this.errors.put(key, error);
	}

	public void addWarning(String key, String warning) {
		this.warnings.put(key, warning);
	}

	@Override
	public void setErrors(Map<String, String> errors) {
		this.errors.clear();
		this.errors.putAll(errors);
	}

	@Override
	public Map<String, String> getErrors() {
		return Collections.unmodifiableMap(errors);
	}

	public Map<String, String> getWarnings() {
		return Collections.unmodifiableMap(warnings);
	}


	public void setPreferenceController(PreferenceController preferenceController) {
		this.preferenceController = preferenceController;
	}

	public void setObjectContext(ObjectContext objectContext) {
		this.objectContext = objectContext;
	}

	public void setCollege(College college) {
		this.college = college;
	}

	public void setStudentService(IStudentService studentService) {
		this.studentService = studentService;
	}

	public boolean isFinished() {
		return state == State.FINISHED;
	}

	public boolean isAddContact() {
		return state == State.ADD_CONTACT;
	}

	public boolean isEditContact() {
		return state == State.EDIT_CONTACT;
	}

	public Messages getMessages() {
		return messages;
	}

	public void setMessages(Messages messages) {
		this.messages = messages;
	}

	public ContactFieldHelper getContactFieldHelper() {
		return this.contactFieldHelper;
	}

	public ObjectContext getObjectContext() {
		return objectContext;
	}


	protected State getState() {
		return state;
	}

	protected void setState(State state) {
		this.state = state;
	}

	protected void setVisibleFields(List<String> visibleFields) {
		this.visibleFields = visibleFields;
	}


	public College getCollege() {
		return college;
	}

	public IStudentService getStudentService() {
		return studentService;
	}

	public ContactFieldSet getContactFieldSet() {
		return contactFieldSet;
	}

	public void setContactFieldSet(ContactFieldSet contactFieldSet) {
		this.contactFieldSet = contactFieldSet;
	}

	@Override
	public boolean isActiveConcessionTypes() {
		return false;
	}

	@Override
	public ConcessionDelegate getConcessionDelegate() {
		return null;
	}

	public String getSpecialNeeds() {
		return specialNeeds;
	}

	public static enum State {
		INIT,
		ADD_CONTACT,
		EDIT_CONTACT,
		FINISHED
	}

}
