package ish.oncourse.willow.functions

import ish.oncourse.model.College
import ish.oncourse.model.Contact
import ish.oncourse.services.preference.GetPreference
import ish.oncourse.services.preference.Preferences
import ish.oncourse.willow.model.common.ValidationError
import ish.oncourse.willow.model.web.ContactId
import ish.oncourse.willow.model.web.CreateContactParams
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.commons.lang3.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import static ish.oncourse.model.auto._Contact.*
import static ish.oncourse.services.preference.Preferences.ConfigProperty.allowCreateContact

class CreateOrGetContact  {
    final static Logger logger = LoggerFactory.getLogger(CreateOrGetContact.class)

    private final
    static String NOT_ALLOW_CREATE_CONTACT = 'That person cannot be found and the system doesn\'t allow new students to be created here. Please check the details you are using are correct.'

    CreateContactParams params
    College college
    ObjectContext context
    ContactId contactId = new ContactId()
    ValidationError validationError = new ValidationError()


    CreateOrGetContact perform() {
        Contact contact = findContact()

        if (contact) {
            contactId.newContact = false
            if  (!contact.isCompany && !contact.student) {
                contact.createNewStudent()
                contact.objectContext.commitChanges()
            }
        } else if (contactCreationAllowed) {
            College localCollege = context.localObject(college)
            contact = context.newObject(Contact.class)
            contact.lastLoginTime = new Date()
            contact.setCollege(localCollege)

            contact.emailAddress = params.email
            contact.familyName = params.lastName
            
            if  (params.company) {
                contact.isCompany = true
            } else {
                contact.givenName = params.firstName
                contact.createNewStudent()
            }

            contact.isMarketingViaEmailAllowed = true
            contact.isMarketingViaPostAllowed = true
            contact.isMarketingViaSMSAllowed = true
            contact.objectContext.commitChanges()
            contactId.newContact = true
        } 
        
        if (contact) {
            contactId.id = contact.id.toString()
        } else {
            validationError.formErrors << NOT_ALLOW_CREATE_CONTACT
        }
        
        this
    }

   private boolean isContactCreationAllowed() {
        String value = new GetPreference(college, allowCreateContact.getPreferenceNameBy(Preferences.ContactFieldSet.valueOf(params.fieldSet.toString().toLowerCase())), context).getValue()
        StringUtils.isBlank(value) ? true : Boolean.valueOf(value)
    }
    
    private Contact findContact() {
        List<Contact> results
        ObjectSelect select = (ObjectSelect.query(Contact)
                .where(EMAIL_ADDRESS.eq(params.email)) & FAMILY_NAME.eq(params.lastName) & COLLEGE.eq(college))
        
        if (params.company) {
            results  = (select & IS_COMPANY.eq(true)).select(context)
        } else {
            results  = (select & GIVEN_NAME.eq(params.firstName)  & IS_COMPANY.eq(false)).select(context)
        }

        if (results.size() > 1) {
            logger.error("Duplicate student contact exists for {} {} ( {} ) with id {} used for this query.", params.firstName, params.lastName, params.email, results[0].id)
        }
        
        results.isEmpty() ? null : results.get(0)
    }
}