package ish.oncourse.willow.functions

import ish.oncourse.model.College
import ish.oncourse.model.Contact
import ish.oncourse.model.ContactRelation
import ish.oncourse.model.ContactRelationType
import ish.oncourse.willow.checkout.functions.GetContact
import ish.oncourse.willow.model.checkout.CreateParentChildrenRequest
import ish.oncourse.willow.model.common.CommonError
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.QueryCacheStrategy
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class CreateParentChildrenRelation {

    final static Logger logger = LoggerFactory.getLogger(CreateParentChildrenRelation.class)

    private College college
    private ObjectContext context
    private CreateParentChildrenRequest request
    private ContactRelationType parentRelationType
    
    private CommonError error

    CreateParentChildrenRelation(College college, ObjectContext context, CreateParentChildrenRequest request) {
        this.college = college
        this.context = context
        this.request = request
        this.parentRelationType = getGuardianRelationType()
        if (!parentRelationType) {
            throw new IllegalStateException("College (id: ${college.id}) has not predefined guardian relation type")
        }
    }
    
    CreateParentChildrenRelation create() {
        
        Contact parent = new GetContact(context, college, request.parentId).get()
        
        if (new IsParentRequired(college, context, parent).get()) {
            logger.error("Parent or Guardian has wrong age, parent id: ${parent.id}, college id: ${college.id}")
            error = new CommonError(message: 'Parent or Guardian has wrong age')
            return this
        }
        
        for (String id: request.childrenIds) {
            Contact child = new GetContact(context, college, request.parentId).get()
            CheckParent checkParent = new CheckParent(college, context, child).perform()
            
            if (!checkParent.parentRequired || checkParent.parent != null) {
                logger.error("Can not create parent relation for, contact id: ${child.id}, college id: ${college.id}")
                error = new CommonError(message: "Can not create parent relation for ${child.fullName}")
                return this
            } else {
                createRelation(parent, child)
            }
        }
        
        return this
    }
    
    CommonError getError() {
        return error
    }

    void createRelation(Contact parent, Contact child) {
        ContactRelation contactRelation = context.newObject(ContactRelation)
        contactRelation.fromContact = parent
        contactRelation.toContact = child
        contactRelation.college = context.localObject(college)
        contactRelation.relationType = parentRelationType
    }

    private ContactRelationType getGuardianRelationType() {
        (ObjectSelect.query(ContactRelationType).where(ContactRelationType.COLLEGE.eq(college)) 
                & ContactRelationType.ANGEL_ID.eq(GetParent.ANGEL_ID_ContactRelationType_ParentOrGuardian))
                .cacheStrategy(QueryCacheStrategy.SHARED_CACHE)
                .cacheGroup(ContactRelationType.class.simpleName)
                .selectOne(context)
    }
}
