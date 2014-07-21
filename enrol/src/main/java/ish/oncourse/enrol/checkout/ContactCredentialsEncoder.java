package ish.oncourse.enrol.checkout;

import ish.oncourse.enrol.checkout.contact.ContactCredentials;
import ish.oncourse.enrol.services.student.IStudentService;
import ish.oncourse.model.College;
import ish.oncourse.model.Contact;
import ish.oncourse.model.CustomField;
import ish.oncourse.model.CustomFieldType;
import org.apache.cayenne.ObjectContext;

public class ContactCredentialsEncoder {

	private ContactCredentials contactCredentials;
	private College college;
	private ObjectContext objectContext;
	private IStudentService studentService;

	private Contact contact;


	public void encode()
	{
		contact = studentService.getStudentContact(contactCredentials.getFirstName(), contactCredentials.getLastName(), contactCredentials.getEmail());

		/**
		 * The following changes can be canceled, so we needs child context to do it
		 */

		if (contact != null) {
			contact = objectContext.localObject(contact);
			if (contact.getStudent() == null) {
				contact.createNewStudent();
			}
		} else {
			College localCollege = objectContext.localObject(college);
			
			contact = objectContext.newObject(Contact.class);

			contact.setCollege(localCollege);

			contact.setGivenName(contactCredentials.getFirstName());
			contact.setFamilyName(contactCredentials.getLastName());
			contact.setEmailAddress(contactCredentials.getEmail());
			contact.createNewStudent();
			contact.setIsMarketingViaEmailAllowed(true);
			contact.setIsMarketingViaPostAllowed(true);
			contact.setIsMarketingViaSMSAllowed(true);
			
			for (CustomFieldType customFieldType : localCollege.getCustomFieldTypes()) {
				CustomField customField = objectContext.newObject(CustomField.class);

				customField.setCollege(localCollege);
				customField.setCustomFieldType(customFieldType);
				
				customField.setRelatedObject(contact);
				customField.setValue(customFieldType.getDefaultValue());
			}
		}
	}

	public void setContactCredentials(ContactCredentials contactCredentials) {
		this.contactCredentials = contactCredentials;
	}

	public Contact getContact() {
		return contact;
	}

	public void setObjectContext(ObjectContext objectContext) {
		this.objectContext = objectContext;
	}

	public void setStudentService(IStudentService studentService) {
		this.studentService = studentService;
	}

	public void setCollege(College college) {
		this.college = college;
	}
}
