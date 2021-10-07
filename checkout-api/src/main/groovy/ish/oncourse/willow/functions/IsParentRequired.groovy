package ish.oncourse.willow.functions

import ish.oncourse.model.College
import ish.oncourse.model.Contact
import ish.oncourse.model.WebSite
import ish.oncourse.services.preference.GetContactAgeWhenNeedParent
import org.apache.cayenne.ObjectContext

import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

class IsParentRequired {

    private College college
    private ObjectContext context
    private Contact contact
    private WebSite site

    IsParentRequired(College college, ObjectContext context, Contact contact,  WebSite site) {
        this.college = college
        this.context = context
        this.contact = contact
        this.site = site
        
    }

    boolean get() {
        if (contact.dateOfBirth) {
            Integer age = ChronoUnit.YEARS.between(ZonedDateTime.ofInstant(contact.dateOfBirth.toInstant(), ZoneId.systemDefault()),
                    ZonedDateTime.ofInstant(new Date().toInstant(), ZoneId.systemDefault())).toInteger()
            return new GetContactAgeWhenNeedParent(college, context, site).integerValue > age
        }
        return false
    }

}
