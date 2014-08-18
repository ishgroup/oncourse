package ish.oncourse.enrol.checkout;

import ish.oncourse.enrol.checkout.contact.AddGuardianController;
import ish.oncourse.enrol.checkout.contact.GuardianEditorController;
import ish.oncourse.model.Contact;
import ish.oncourse.model.ContactRelation;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.PersistenceState;

import java.util.List;

import static ish.oncourse.enrol.checkout.PurchaseController.Message.guardianAgeIsWrong;
import static ish.oncourse.enrol.checkout.PurchaseController.State.addContact;
import static ish.oncourse.enrol.checkout.PurchaseController.State.editContact;
import static ish.oncourse.services.preference.PreferenceController.ContactFiledsSet.enrolment;

public class ActionAddGuardian extends AAddContactAction {

    private Contact childContact;

    @Override
    protected void initAddContact() {
        AddGuardianController addContactController = new AddGuardianController();
        addContactController.setPurchaseController(getController());
        addContactController.setAddAction(getAddAction());
        addContactController.setCancelAction(getCancelAction());
        addContactController.setChildContact(childContact);
        addContactController.setHeaderMessage(getHeaderMessage());
        addContactController.setHeaderTitle(getHeaderTitle());
        getController().setAddContactController(addContactController);
        getController().setState(addContact);

    }

    @Override
    protected void prepareContactEditor(boolean fillRequiredProperties) {
        GuardianEditorController contactEditorController = new GuardianEditorController();
        contactEditorController.setPurchaseController(getController());
        contactEditorController.setContact(getContact());
        contactEditorController.setObjectContext(getContact().getObjectContext());
        contactEditorController.setContactFiledsSet(enrolment);
        contactEditorController.setAddAction(getAddAction());
        contactEditorController.setCancelAction(getCancelAction());
        contactEditorController.setChildContact(childContact);
        if (!getContact().getObjectId().isTemporary() && fillRequiredProperties)
            contactEditorController.setFillRequiredProperties(fillRequiredProperties);
        getController().setContactEditorDelegate(contactEditorController);
    }

    @Override
    protected boolean shouldChangePayer() {
        //we need to change payer only when the payer is a child contact or is not set
        return getController().needGuardianFor(getModel().getPayer()) || getModel().getPayer() == null;
    }

    @Override
    protected boolean shouldAddEnrolments() {
        return true;
    }

    @Override
    protected boolean isApplyOwing() {
        return true;
    }

    @Override
    protected PurchaseController.State getFinalState() {
        return PurchaseController.State.editCheckout;
    }

    @Override
    protected PurchaseController.Action getAddAction() {
        return PurchaseController.Action.addGuardian;
    }

    @Override
    protected PurchaseController.Action getCancelAction() {
        return PurchaseController.Action.cancelAddGuardian;
    }

    public Contact getChildContact() {
        return childContact;
    }

    public void setChildContact(Contact childContact) {
        this.childContact = childContact;
    }

    @Override
    protected void commitContact() {

        ObjectContext objectContext = getContact().getObjectContext();
        objectContext.commitChanges();
        setContact(getModel().localizeObject(getContact()));

        updateGuardian();

        //if the contact was added to the process early we just create the relation.
        if (!getModel().getContacts().contains(getContact())) {
            getModel().addContact(getContact(), true);
        }

        //add the first contact
        if (shouldChangePayer()) {
            changePayer(getContact());
        }
        if (shouldAddEnrolments()) {
            initEnrolments();
            initProductItems();
        }

        getController().setState(getFinalState());
        getController().setContactEditorDelegate(null);
    }

    private void updateGuardian() {

        ObjectContext objectContext = getController().getCayenneService().newContext();

        if (childContact != null) {
            ContactRelation oldGuardianRelation = getModel().getGuardianRelationFor(childContact);
            if (!oldGuardianRelation.getFromContact().getId().equals(getContact().getId())) {
                oldGuardianRelation = objectContext.localObject(oldGuardianRelation);
                objectContext.deleteObjects(oldGuardianRelation);
                createGuardianRelation(childContact, objectContext);
            }
        } else {
            List<Contact> contacts = getModel().getContacts();
            for (Contact contact : contacts) {
                if (getController().needGuardianFor(contact) && getModel().getGuardianRelationFor(contact) == null) {
                    createGuardianRelation(contact, objectContext);
                }
            }
        }
        objectContext.commitChanges();
    }

    private ContactRelation createGuardianRelation(Contact childContact, ObjectContext objectContext) {
        ContactRelation contactRelation = objectContext.newObject(ContactRelation.class);
        contactRelation.setFromContact(objectContext.localObject(getContact()));
        contactRelation.setToContact(objectContext.localObject(childContact));
        contactRelation.setCollege(objectContext.localObject(getModel().getCollege()));
        contactRelation.setRelationType(objectContext.localObject(getController().getGuardianRelationType()));

        //we need set PersistenceState to HOLLOW to enforce reloading all relations for the contact.
        childContact.setPersistenceState(PersistenceState.HOLLOW);
        return contactRelation;
    }

    @Override
    protected void parse() {
        super.parse();

        if (childContact == null) {
            if (getController().getAddContactDelegate() != null) {
                childContact = ((AddGuardianController) getController().getAddContactDelegate()).getChildContact();
            } else if (getController().getContactEditorDelegate() != null) {
                childContact = ((GuardianEditorController) getController().getContactEditorDelegate()).getChildContact();
            }
        }
    }

    @Override
    protected boolean validate() {
        if (getContact() == null)
            return true;
        if (getController().getState() == editContact) {
            if (getController().needGuardianFor(getContact())) {
                getController().addError(guardianAgeIsWrong, getController().getPreferenceController().getContactAgeWhenNeedParent());
                return false;
            }
        }
        return true;
    }

    @Override
    protected String getHeaderMessage() {
        return getController().getMessages().format("message-enterDetailsForGuardian");
    }

    @Override
    protected String getHeaderTitle() {
        if (childContact != null) {
            return getController().getMessages().format("message-changeGuardian", childContact.getFullName());
        } else {
            return getController().getMessages().format("message-addGuardian");
        }
    }
}


