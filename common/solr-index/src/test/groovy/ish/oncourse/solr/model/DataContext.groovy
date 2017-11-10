package ish.oncourse.solr.model

import ish.oncourse.model.College
import org.apache.cayenne.ObjectContext

/**
 * User: akoiro
 * Date: 10/11/17
 */
class DataContext {
    ObjectContext objectContext

    Map<String, CollegeContext> colleges = new HashMap<>()

    CollegeContext college(String name, String timeZone) {
        objectContext.newObject(College).with {
            it.name = name
            it.timeZone = timeZone
            it.firstRemoteAuthentication = new Date()
            it.requiresAvetmiss = true

            CollegeContext collegeContext = new CollegeContext()
            collegeContext.objectContext = objectContext
            colleges.put(name, collegeContext)
            objectContext.commitChanges()
            collegeContext
        }
    }
}
