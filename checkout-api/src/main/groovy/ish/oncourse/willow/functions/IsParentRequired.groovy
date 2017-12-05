package ish.oncourse.willow.functions

import ish.oncourse.model.College
import ish.oncourse.model.Contact
import ish.oncourse.services.preference.GetContactAgeWhenNeedParent
import ish.oncourse.services.preference.IsCollectParentDetails
import org.apache.cayenne.ObjectContext

import java.time.temporal.ChronoUnit

class IsParentRequired {

    private College college
    private ObjectContext context
    private Contact contact

    IsParentRequired(College college, ObjectContext context, Contact contact) {
        this.college = college
        this.context = context
        this.contact = contact
    }

    boolean get() {
        if (contact.dateOfBirth) {
            Long age = ChronoUnit.YEARS.between(contact.dateOfBirth.toInstant(), new Date().toInstant())
            return new IsCollectParentDetails(college, context).get() && new GetContactAgeWhenNeedParent(college, context).get() > age
        }
        return false
    }

}
