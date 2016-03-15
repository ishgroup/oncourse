package ish.oncourse.enrol.checkout;

import ish.oncourse.enrol.checkout.contact.ContactCredentials;
import ish.oncourse.enrol.services.student.IStudentService;
import ish.oncourse.model.College;
import ish.oncourse.model.Contact;
import org.apache.cayenne.ObjectContext;

public class ContactCredentialsEncoder {

    private ContactCredentials contactCredentials;
    private boolean isCompany = false;
    private College college;
    private ObjectContext objectContext;
    private IStudentService studentService;
    private boolean allowCreateContact;

    private Contact contact;

    private ContactCredentialsEncoder() {
    }

    public Contact encode() {
        contact = studentService.getContact(contactCredentials.getFirstName(), contactCredentials.getLastName(), contactCredentials.getEmail(), isCompany);

        /**
         * The following changes can be canceled, so we needs child context to do it
         */

        if (contact != null) {
            initContact();
        } else if (allowCreateContact) {
            newContact();
        }
        return contact;
    }

    private void initContact() {
        contact = objectContext.localObject(contact);
        if (contact.getStudent() == null && !isCompany) {
            contact.createNewStudent();
        }
    }

    private void newContact() {
        College localCollege = objectContext.localObject(college);

        contact = objectContext.newObject(Contact.class);

        contact.setCollege(localCollege);

        contact.setGivenName(contactCredentials.getFirstName());
        contact.setFamilyName(contactCredentials.getLastName());
        contact.setEmailAddress(contactCredentials.getEmail());
        if (!isCompany) {
            contact.createNewStudent();
        } else {
            contact.setIsCompany(true);
        }
        contact.setIsMarketingViaEmailAllowed(true);
        contact.setIsMarketingViaPostAllowed(true);
        contact.setIsMarketingViaSMSAllowed(true);
    }
    public boolean isAllowCreateContact() {
        return allowCreateContact;
    }


    public static ContactCredentialsEncoder valueOf(ContactCredentials contactCredentials,
                                                    boolean isCompany,
                                                    College college, ObjectContext objectContext,
                                                    IStudentService studentService,
                                                    boolean allowCreateContact) {
        ContactCredentialsEncoder result = new ContactCredentialsEncoder();
        result.contactCredentials = contactCredentials;
        result.isCompany = isCompany;
        result.college = college;
        result.objectContext = objectContext;
        result.studentService = studentService;
        result.allowCreateContact = allowCreateContact;
        return result;
    }
}
