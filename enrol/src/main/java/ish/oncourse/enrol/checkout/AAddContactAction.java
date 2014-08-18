package ish.oncourse.enrol.checkout;

import ish.math.Money;
import ish.oncourse.enrol.checkout.contact.AddContactController;
import ish.oncourse.enrol.checkout.contact.ContactCredentials;
import ish.oncourse.enrol.checkout.contact.ContactEditorController;
import ish.oncourse.model.*;
import ish.oncourse.services.preference.ContactFieldHelper;
import org.apache.cayenne.ObjectContext;

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
        boolean isAllRequiredFieldFilled = new ContactFieldHelper(getController().getPreferenceController(), enrolment).isAllRequiredFieldFilled(contact);
        if (contact.getObjectId().isTemporary() || !isAllRequiredFieldFilled) {
            prepareContactEditor(!isAllRequiredFieldFilled);
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
        contactEditorController.setContactFiledsSet(enrolment);
        contactEditorController.setAddAction(getAddAction());
        contactEditorController.setCancelAction(getCancelAction());
        if (!contact.getObjectId().isTemporary() && fillRequiredProperties)
            contactEditorController.setFillRequiredProperties(fillRequiredProperties);
        getController().setContactEditorDelegate(contactEditorController);
    }


    protected void commitContact() {

        contact.getObjectContext().commitChanges();
        tryToRelateToGuardian();

        contact = getModel().localizeObject(contact);
        getModel().addContact(contact);

        Contact guardian = getGuardian();
        if (guardian != null && !getModel().getContacts().contains(guardian)) {
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
