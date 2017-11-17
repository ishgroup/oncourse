package ish.oncourse.solr.model

import ish.oncourse.model.College
import ish.oncourse.test.context.CCollege
import org.apache.cayenne.ObjectContext

/**
 * User: akoiro
 * Date: 10/11/17
 */
class DataContext {
    ObjectContext objectContext

    Map<String, CCollege> colleges = new HashMap<>()

    CCollege college(String name, String timeZone) {
        objectContext.newObject(College).with {
            it.name = name
            it.timeZone = timeZone
            it.firstRemoteAuthentication = new Date()
            it.requiresAvetmiss = true

            CCollege collegeContext = new CCollege()
            collegeContext.objectContext = objectContext
            collegeContext.college = it
            colleges.put(name, collegeContext)
            objectContext.commitChanges()
            collegeContext
        }
    }
}
