package ish.oncourse.willow.service.impl

import ish.oncourse.model.College
import ish.oncourse.model.Contact
import ish.oncourse.services.preference.GetPreference
import ish.oncourse.services.preference.Preferences
import ish.oncourse.willow.model.CreateContactParams
import ish.oncourse.willow.model.ValidationError
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.commons.lang3.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import static ish.oncourse.model.auto._Contact.COLLEGE
import static ish.oncourse.model.auto._Contact.EMAIL_ADDRESS
import static ish.oncourse.model.auto._Contact.FAMILY_NAME
import static ish.oncourse.model.auto._Contact.GIVEN_NAME
import static ish.oncourse.services.preference.Preferences.ConfigProperty.allowCreateContact

class CreateOrGetContact {
    final static Logger logger = LoggerFactory.getLogger(ContactApiServiceImpl.class)

    private final
    static String NOT_ALLOW_CREATE_CONTACT = 'That person cannot be found and the system doesn\'t allow new students to be created here. Please check the details you are using are correct.'

    CreateContactParams params
    College college
    ObjectContext context
    Preferences.ContactFieldSet fieldSet
    String contactId
    ValidationError validationError = new ValidationError()


    def perform() {
        Contact contact = findContact()

        if (contact) {
            if  (!contact.student) {
                contact.createNewStudent()
                contact.objectContext
            }
        } else if (contactCreationAllowed) {
            College localCollege = context.localObject(college)
            contact = context.newObject(Contact.class)
            contact.setCollege(localCollege)

            contact.givenName = params.firstName
            contact.familyName = params.lastName
            contact.emailAddress = params.email
            contact.createNewStudent()

            contact.isMarketingViaEmailAllowed = true
            contact.isMarketingViaPostAllowed = true
            contact.isMarketingViaSMSAllowed = true
            contact.objectContext
        } 
        
        if (contact) {
            contactId = contact.id.toString()
        } else {
            validationError.formErrors << NOT_ALLOW_CREATE_CONTACT
        }
    }

   private boolean isContactCreationAllowed() {
        String value = new GetPreference(college, allowCreateContact.getPreferenceNameBy(fieldSet), context)
        StringUtils.isBlank(value) ? true : Boolean.valueOf(value)
    }
    
    private Contact findContact() {
        List<Contact> results = (ObjectSelect.query(Contact)
                .where(EMAIL_ADDRESS.eq(params.email)) & GIVEN_NAME.eq(params.firstName) & FAMILY_NAME.eq(params.lastName) & COLLEGE.eq(college))
                .select(context)

        if (results.size() > 1) {
            logger.error("Duplicate student contact exists for {} {} ( {} ) with id {} used for this query.", params.firstName, params.lastName, params.email, results[0].id)
        }
        
        results.isEmpty() ? null : results.get(0)
    }
}