package ish.oncourse.enrol.checkout;

import ish.oncourse.enrol.checkout.contact.*;
import ish.oncourse.model.*;
import ish.oncourse.services.preference.ContactFieldHelper;
import ish.oncourse.services.preference.Preferences;
import org.apache.cayenne.ObjectContext;

import static ish.oncourse.enrol.checkout.PurchaseController.Action.addCourseClass;
import static ish.oncourse.enrol.checkout.PurchaseController.Action.changePayer;
import static ish.oncourse.enrol.checkout.PurchaseController.Message.contactAlreadyAdded;
import static ish.oncourse.enrol.checkout.PurchaseController.Message.notAllowCreateContact;
import static ish.oncourse.enrol.checkout.PurchaseController.State.addContact;
import static ish.oncourse.enrol.checkout.PurchaseController.State.editContact;
import static ish.oncourse.services.preference.Preferences.ContactFieldSet.enrolment;

public abstract class AAddContactAction extends APurchaseAction {
    private Contact contact;
    protected ContactCredentials contactCredentials;


    protected abstract boolean shouldChangePayer();

    protected abstract boolean shouldAddEnrolments();

    protected abstract boolean isApplyOwing();

    protected abstract PurchaseController.State getFinalState();

    protected abstract PurchaseController.Action getAddAction();

    protected abstract PurchaseController.Action getCancelAction();

    protected abstract String getHeaderMessage();

    protected abstract String getHeaderTitle();


    @Override
    protected void makeAction() {

        if (contact == null)
            initAddContact();
        else if (getController().getState().equals(addContact))
            initEditContact();
        else if (getController().getState().equals(editContact))
            commitContact();
        else
            throw new IllegalStateException();

    }

    protected void initAddContact() {
        AddContactController addContactController = new AddContactController();
        addContactController.setPurchaseController(getController());
        addContactController.setAddAction(getAddAction());
        addContactController.setCancelAction(getCancelAction());
        addContactController.setHeaderMessage(getHeaderMessage());
        addContactController.setHeaderTitle(getHeaderTitle());
        getController().setAddContactController(addContactController);
        getController().setState(addContact);

    }

    private void initEditContact() {
		ContactFieldHelper fieldHelper = new ContactFieldHelper(getController().getPreferenceController(), enrolment);
		
        boolean isAllRequiredFieldFilled = fieldHelper.isAllRequiredFieldFilled(contact);
		boolean isAllRequiredCustomFieldFilled = fieldHelper.isAllRequiredCustomFieldFilled(contact);
		boolean hasVisibleFields = fieldHelper.hasVisibleFields(contact);
		boolean hasVisibleCustomFields =  fieldHelper.hasVisibleCustomFields(contact);
		
        if ((contact.getObjectId().isTemporary() && (hasVisibleFields || hasVisibleCustomFields))
				|| !isAllRequiredFieldFilled || !isAllRequiredCustomFieldFilled) {
            prepareContactEditor(!(isAllRequiredFieldFilled && isAllRequiredCustomFieldFilled));
            getController().setState(editContact);
        } else {
            commitContact();
        }
        getController().setAddContactController(null);
    }

    /**
     * @param fillRequiredProperties if true we show only required properties where value is null
     */
    protected void prepareContactEditor(boolean fillRequiredProperties) {
        ContactEditorController contactEditorController = new ContactEditorController();
        contactEditorController.setPurchaseController(getController());
        contactEditorController.setContact(contact);
        contactEditorController.setObjectContext(contact.getObjectContext());
        contactEditorController.setContactFieldSet(enrolment);
        contactEditorController.setAddAction(getAddAction());
        contactEditorController.setCancelAction(getCancelAction());
        if (!contact.getObjectId().isTemporary() && (fillRequiredProperties))
            contactEditorController.setFillRequiredProperties(fillRequiredProperties);
        getController().setContactEditorDelegate(contactEditorController);
    }


    protected void commitContact() {

		if (getParameter().hasValue(ContactCustomFieldHolder.class)) {
			CustomFieldsBuilder.valueOf(getParameter().getValue(ContactCustomFieldHolder.class), contact).build();
		}

        contact.getObjectContext().commitChanges();
        tryToRelateToGuardian();

        contact = getModel().localizeObject(contact);
        getModel().addContact(contact);

        Contact guardian = getGuardian();
        if (guardian != null && !getModel().getContacts().contains(guardian)) {
            if (guardian.getStudent() == null)
            {
                //we need to create student for any contact which we add to web enroll process.
                guardian.createNewStudent();
            }
            getModel().addContact(guardian, true);
        }

        //add the first contact
        if (shouldChangePayer()) {
            if (guardian != null)
                getController().addWarning(PurchaseController.Message.payerWasChangedToGuardian, contact.getFullName(), guardian.getFullName());
            changePayer(guardian != null ? guardian : contact);
        }
        if (shouldAddEnrolments()) {
            initEnrolments();
            initProductItems();
        }

        getController().setState(getFinalState());
        getController().setContactEditorDelegate(null);
    }

    /**
     * the code relates already add guardian to the contact if it needs
     */
    private void tryToRelateToGuardian() {
        /*
            we need to check:
             the payer is not null (add first contact),
             payer does not need guardian (the first added contact needs guardian),
         */
        if (getModel().getPayer() != null &&
                !getController().needGuardianFor(getModel().getPayer()) &&
                getController().needGuardianFor(contact) &&
                getController().getGuardianFor(contact) == null) {
            {
                ObjectContext context = getController().getCayenneService().newContext();
                ContactRelation contactRelation = context.newObject(ContactRelation.class);
                contactRelation.setFromContact(context.localObject(getModel().getPayer()));
                contactRelation.setToContact(context.localObject(contact));
                contactRelation.setCollege(context.localObject(getModel().getCollege()));
                contactRelation.setRelationType(context.localObject(getController().getGuardianRelationType()));
                context.commitChanges();
                getController().addWarning(PurchaseController.Message.payerSetAsGuardian, contact.getFullName(), getModel().getPayer().getFullName());
            }
        }
    }

    protected void changePayer(Contact payer) {
        ActionChangePayer actionChangePayer = changePayer.createAction(getController());
        actionChangePayer.setContact(payer);
        actionChangePayer.action();
        //we don't need to apply owing/credit to total on checkout page
        if (!isApplyOwing())
            getModel().setApplyPrevOwing(false);
    }


    protected Contact getGuardian() {
        if (getController().needGuardianFor(contact)) {
            Contact guardian = getController().getGuardianFor(contact);
            return guardian;
        } else {
            return null;
        }
    }


    protected void initEnrolments() {
        for (CourseClass cc : getModel().getClasses()) {
            ActionAddCourseClass actionAddCourseClass = addCourseClass.createAction(getController());
            actionAddCourseClass.setCourseClass(cc);
            actionAddCourseClass.action();
        }
    }

    protected void initProductItems() {
        for (Product product : getController().getModel().getProducts()) {
            if (!(product instanceof VoucherProduct)) {
                ActionEnableProductItemBuilder.valueOf(contact, product, getController()).build().action();
            }
        }
    }

    @Override
    protected void parse() {
        if (getParameter() != null) {
            if (!getParameter().hasValues())
                contact = null;
            else if (getController().getState() == addContact) {
                contactCredentials = getParameter().getValue(ContactCredentials.class);
				ContactCredentialsEncoder contactCredentialsEncoder = buildContactCredentialsEncoder();
                contact = contactCredentialsEncoder.encode();
            } else {
                contact = getParameter().getValue(Contact.class);
            }
        }
    }

	protected ContactCredentialsEncoder buildContactCredentialsEncoder() {
		return ContactCredentialsEncoder.valueOf(contactCredentials,
                false,
                getModel().getCollege(),
                getController().getCayenneService().newContext(),
                getController().getStudentService(),
                isAllowCreateContact());
	}

    protected boolean isAllowCreateContact() {
        return getController().getPreferenceController().getAllowCreateContact(Preferences.ContactFieldSet.enrolment);
    }

	@Override
    protected boolean validate() {
        if (contact == null && contactCredentials == null)
            return true;
        if (getController().getState() == addContact) {
            if (getModel().containsContactWith(contactCredentials)) {
                getController().addError(contactAlreadyAdded);
                return false;
            }
            if (!isAllowCreateContact() && contact == null) {
                getController().addError(notAllowCreateContact);
                return false;
            }
            return true;
        } else
            return getController().getState() == editContact;
    }

    Contact getContact() {
        return contact;
    }


    protected void setContact(Contact contact) {
        this.contact = contact;
    }

    ContactCredentials getContactCredentials() {
        return contactCredentials;
    }

    protected void setContactCredentials(ContactCredentials contactCredentials) {
        this.contactCredentials = contactCredentials;
    }
}
