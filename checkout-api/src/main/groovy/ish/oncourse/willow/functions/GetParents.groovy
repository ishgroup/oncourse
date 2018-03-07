package ish.oncourse.willow.functions

import ish.oncourse.model.College
import ish.oncourse.model.Contact
import ish.oncourse.model.ContactRelation
import org.apache.cayenne.ObjectContext

class GetParents {
    
    public static final long ANGEL_ID_ContactRelationType_ParentOrGuardian = -1

    private College college
    private ObjectContext context
    private Contact contact

    GetParents(College college, ObjectContext context, Contact contact) {
        this.college = college
        this.context = context
        this.contact = contact
    }

    List<Contact> get() {
        List<ContactRelation> parentRelations = contact.fromContacts.findAll() { it.relationType?.angelId  == ANGEL_ID_ContactRelationType_ParentOrGuardian }
        return parentRelations.collect {it.fromContact}
    }
}
