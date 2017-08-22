package ish.oncourse.willow.functions

import ish.oncourse.model.College
import ish.oncourse.model.Contact
import ish.oncourse.willow.checkout.functions.GetContact
import ish.oncourse.willow.model.checkout.CreateParentChildrenRequest
import ish.oncourse.willow.model.common.CommonError
import org.apache.cayenne.ObjectContext

class CreateParentChildrenRelation extends AbstractSetParent {
    
    private CreateParentChildrenRequest request
    
    CreateParentChildrenRelation(College college, ObjectContext context, CreateParentChildrenRequest request) {
        super(college, context)
        this.request = request
    }
    
    CreateParentChildrenRelation create() {
        Contact parent = getParent()
        if (!parent) {
            return this
        }
        
        for (String id: request.childrenIds) {
            Contact child = new GetContact(context, college, id).get()
            CheckParent checkParent = new CheckParent(college, context, child).perform()
            
            if (!checkParent.parentRequired || checkParent.parent != null) {
                logger.error("Can not create parent relation for, contact id: ${child.id}, college id: ${college.id}")
                setError(new CommonError(message: "Can not create parent relation for ${child.fullName}"))
                return this
            } else {
                createRelation(parent, child)
            }
        }
        
        return this
    }

    @Override
    protected String getParentId() {
        return request.parentId
    }
}
