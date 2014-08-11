package ish.oncourse.enrol.checkout.contact;

import ish.oncourse.model.Contact;

public class AddGuardianController extends AddContactController {
    private Contact childContact;

    public Contact getChildContact() {
        return childContact;
    }

    public void setChildContact(Contact childContact) {
        this.childContact = childContact;
    }
}
