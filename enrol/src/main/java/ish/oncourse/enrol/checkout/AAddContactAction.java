package ish.oncourse.enrol.checkout;

import ish.math.Money;
import ish.oncourse.enrol.checkout.contact.AddContactController;
import ish.oncourse.enrol.checkout.contact.ContactCredentials;
import ish.oncourse.model.*;
import ish.oncourse.services.preference.ContactFieldHelper;

import static ish.oncourse.enrol.checkout.PurchaseController.Action.addCourseClass;
import static ish.oncourse.enrol.checkout.PurchaseController.Action.changePayer;
import static ish.oncourse.enrol.checkout.PurchaseController.Message.contactAlreadyAdded;
import static ish.oncourse.enrol.checkout.PurchaseController.State.addContact;
import static ish.oncourse.enrol.checkout.PurchaseController.State.editContact;
import static ish.oncourse.services.preference.PreferenceController.ContactFiledsSet.enrolment;

public abstract class AAddContactAction extends APurchaseAction {
    private Contact contact;
    private ContactCredentials contactCredentials;


    protected abstract boolean shouldChangePayer();

    protected abstract boolean shouldAddEnrolments();

    protected abstract boolean isApplyOwing();

    protected abstract PurchaseController.State getFinalState();

    protected abstract PurchaseController.Action getAddAction();

    protected abstract PurchaseController.Action getCancelAction();

    @Override
    protected void makeAction() {

        if (contact == null)
            initAddContact();
        else if (getController().getState().equals(addContact))
            initEditContact();
        else if (getController().getState().equals(editContact))
            addContact();
        else
            throw new IllegalStateException();

    }

    private void initAddContact() {
        AddContactController addContactController = new AddContactController();
        addContactController.setPurchaseController(getController());
        addContactController.setAddAction(getAddAction());
        addContactController.setCancelAction(getCancelAction());
        getController().setAddContactController(addContactController);
        getController().setState(addContact);
    }

    private void initEditContact() {
        boolean isAllRequiredFieldFilled = new ContactFieldHelper(getController().getPreferenceController(), enrolment).isAllRequiredFieldFilled(contact);
        if (contact.getObjectId().isTemporary() || !isAllRequiredFieldFilled) {
            getController().prepareContactEditor(contact, !isAllRequiredFieldFilled,
                    getAddAction(), getCancelAction());
            getController().setState(editContact);
        } else {
            addContact();
        }
        getController().setAddContactController(null);
    }

    protected void addContact() {
        contact.getObjectContext().commitChanges();
        contact = getModel().localizeObject(contact);
        getModel().addContact(contact);
        //add the first contact
        if (shouldChangePayer())
            changePayer();
        if (shouldAddEnrolments()){
            initEnrolments();
			initProductItems();
		}

        getController().setState(getFinalState());
        getController().resetContactEditorController();
    }

	protected void changePayer() {
        ActionChangePayer actionChangePayer = changePayer.createAction(getController());
        actionChangePayer.setContact(contact);
        actionChangePayer.action();
        //we don't need to apply owing/credit to total on checkout page
        if (!isApplyOwing())
            getModel().setApplyPrevOwing(false);
    }


    protected void initEnrolments() {
        for (CourseClass cc : getModel().getClasses()) {
            ActionAddCourseClass actionAddCourseClass = addCourseClass.createAction(getController());
            actionAddCourseClass.setCourseClass(cc);
            actionAddCourseClass.action();
        }
    }

	private void initProductItems() {
		for (Product product : getController().getModel().getProducts()) {
			if(!(product instanceof VoucherProduct)) {
				ProductItem productItem = getController().createProductItem(contact, product);
				getController().getModel().addProductItem(productItem);
				ActionEnableProductItem actionEnableProductItem = PurchaseController.Action.enableProductItem.createAction(getController());
				actionEnableProductItem.setProductItem(productItem);
				actionEnableProductItem.setPrice(Money.ZERO);
				actionEnableProductItem.action();
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
                ContactCredentialsEncoder contactCredentialsEncoder = new ContactCredentialsEncoder();
                contactCredentialsEncoder.setContactCredentials(contactCredentials);
                contactCredentialsEncoder.setCollege(getModel().getCollege());
                contactCredentialsEncoder.setObjectContext(getController().getCayenneService().newContext());
                contactCredentialsEncoder.setStudentService(getController().getStudentService());
                contactCredentialsEncoder.encode();
                contact = contactCredentialsEncoder.getContact();
            } else {
                contact = getParameter().getValue(Contact.class);
            }
        }
    }

    @Override
    protected boolean validate() {
        if (contact == null)
            return true;
        if (getController().getState() == addContact) {
            ContactCredentials contactCredentials = getParameter().getValue(ContactCredentials.class);
            if (getModel().containsContactWith(contactCredentials)) {

                getController().addError(contactAlreadyAdded);

                return false;
            }
            return true;
        } else
            return getController().getState() == editContact;
    }

    Contact getContact() {
        return contact;
    }

    ContactCredentials getContactCredentials() {
        return contactCredentials;
    }
}
