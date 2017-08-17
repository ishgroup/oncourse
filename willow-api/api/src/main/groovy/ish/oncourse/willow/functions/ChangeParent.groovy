package ish.oncourse.willow.functions

import ish.oncourse.model.College
import ish.oncourse.model.Contact
import ish.oncourse.model.ContactRelation
import ish.oncourse.willow.checkout.functions.GetContact
import ish.oncourse.willow.model.checkout.ChangeParentRequest
import ish.oncourse.willow.model.common.CommonError
import org.apache.cayenne.ObjectContext

class ChangeParent extends AbstractSetParent {
    
    private ChangeParentRequest request
    
    ChangeParent(College college, ObjectContext context, ChangeParentRequest request) {
        super(college, context)
        this.request = request
    }

    ChangeParent perform() {
        Contact parent = getParent()
        if (!parent) {
            return this
        }
        
        Contact child = new GetContact(context, college, request.childId).get()
        ContactRelation oldRelation = child.fromContacts.find { it.relationType == parentRelationType }
        if (oldRelation) {
            context.deleteObjects(oldRelation)
        }
        createRelation(parent, child)
        this
    }

    @Override
    protected String getParentId() {
        return request.parentId
    }
}
