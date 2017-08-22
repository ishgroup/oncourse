package ish.oncourse.willow.functions

import ish.oncourse.model.College
import ish.oncourse.model.Contact
import ish.oncourse.model.ContactRelation
import org.apache.cayenne.ObjectContext

class GetParent {
    
    public static final long ANGEL_ID_ContactRelationType_ParentOrGuardian = -1

    private College college
    private ObjectContext context
    private Contact contact

    GetParent(College college, ObjectContext context, Contact contact) {
        this.college = college
        this.context = context
        this.contact = contact
    }

    ish.oncourse.willow.model.web.Contact get() {
        ContactRelation parentRelation = contact.fromContacts.find { it.relationType?.angelId  == ANGEL_ID_ContactRelationType_ParentOrGuardian }
        if (parentRelation) {
            return new ish.oncourse.willow.model.web.Contact().with { c ->
                c.id = parentRelation.fromContact.id.toString()
                c.firstName = parentRelation.fromContact.givenName
                c.lastName = parentRelation.fromContact.familyName
                c.company = parentRelation.fromContact.isCompany
                c.email = parentRelation.fromContact.emailAddress
                c
            }
        }
        
        return null
    }
}
