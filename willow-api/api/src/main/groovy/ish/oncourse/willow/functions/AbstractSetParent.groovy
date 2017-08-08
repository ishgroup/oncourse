package ish.oncourse.willow.functions

import ish.oncourse.model.College
import ish.oncourse.model.Contact
import ish.oncourse.model.ContactRelation
import ish.oncourse.model.ContactRelationType
import ish.oncourse.willow.checkout.functions.GetContact
import ish.oncourse.willow.model.common.CommonError
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.QueryCacheStrategy
import org.slf4j.Logger
import org.slf4j.LoggerFactory

abstract class AbstractSetParent {

    final static Logger logger = LoggerFactory.getLogger(CreateParentChildrenRelation.class)

    protected College college
    protected ObjectContext context
    protected ContactRelationType parentRelationType
    protected CommonError error


    AbstractSetParent(College college, ObjectContext context) {
        this.college = college
        this.context = context
        this.parentRelationType = getGuardianRelationType()
        if (!parentRelationType) {
            throw new IllegalStateException("College (id: ${college.id}) has not predefined guardian relation type")
        }
    }
    
    protected void createRelation(Contact parent, Contact child) {
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

    protected abstract String getParentId()

    Contact getParent() {
        Contact parent = new GetContact(context, college, getParentId()).get()

        if (new IsParentRequired(college, context, parent).get()) {
            logger.error("Parent or Guardian has wrong age, parent id: ${parent.id}, college id: ${college.id}")
            error = new CommonError(message: 'Parent or Guardian has wrong age')
            return null
        }
        return parent
    }
}
