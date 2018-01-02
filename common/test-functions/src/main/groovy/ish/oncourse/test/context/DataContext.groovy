package ish.oncourse.test.context

import com.github.javafaker.Faker
import ish.oncourse.model.College
import org.apache.cayenne.ObjectContext

/**
 * User: akoiro
 * Date: 10/11/17
 */
class DataContext {
    public static String DEFAULT_SERVICES_SECURITY_CODE = '345ttn44$%9'

    ObjectContext objectContext

    List<CCollege> colleges = new ArrayList<>()

    Faker faker = new Faker()

    DataContext withObjectContext(ObjectContext objectContext) {
        this.objectContext = objectContext
        return this
    }

    CCollege newCollege(String servicesSecurityCode = DEFAULT_SERVICES_SECURITY_CODE) {
        return newCollege("College-Australia/Sydney", "Australia/Sydney", servicesSecurityCode)
    }

    CCollege newCollege(String name, String timeZone, String servicesSecurityCode = DEFAULT_SERVICES_SECURITY_CODE) {
        objectContext.newObject(College).with {
            it.name = name
            it.timeZone = timeZone
            it.firstRemoteAuthentication = new Date()
            it.requiresAvetmiss = true
            it.webServicesSecurityCode = servicesSecurityCode

            CCollege college = new CCollege()
            college.objectContext = objectContext
            college.college = it
            colleges.add(college)
            objectContext.commitChanges()
            college
        }
    }
}
