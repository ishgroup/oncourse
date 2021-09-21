package ish.oncourse.services.preference

import ish.oncourse.model.College
import ish.oncourse.model.CorporatePass
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect

class IsCorporatePassEnabled  {
    College college
    ObjectContext objectContext
    
    IsCorporatePassEnabled(College college, ObjectContext objectContext) {
        this.college = college
        this.objectContext = objectContext
    }

    boolean get() {
        return ObjectSelect.query(CorporatePass)
                .where(CorporatePass.COLLEGE.eq(college))
                .and(CorporatePass.EXPIRY_DATE.isNull().orExp(CorporatePass.EXPIRY_DATE.gt(new Date())))
                .selectFirst(objectContext) != null
    }
}
