package ish.oncourse.test.context

import com.github.javafaker.Faker
import ish.common.types.EntityRelationCartAction
import ish.oncourse.model.College
import ish.oncourse.model.EntityRelationType
import ish.oncourse.model.PaymentGatewayType
import ish.oncourse.model.Preference
import ish.oncourse.model.WillowUser
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.SelectById

/**
 * User: akoiro
 * Date: 10/11/17
 */
class DataContext {
    public static String DEFAULT_SERVICES_SECURITY_CODE = '345ttn44$%9'
    public static final Faker faker = new Faker()

    ObjectContext objectContext

    List<CCollege> colleges = new LinkedList<>()

    DataContext withObjectContext(ObjectContext objectContext) {
        this.objectContext = objectContext
        return this
    }

    CCollege newCollege(String name = "College-Australia/Sydney", String timeZone = "Australia/Sydney", String servicesSecurityCode = DEFAULT_SERVICES_SECURITY_CODE) {
        College c = objectContext.newObject(College).with {
            it.name = name
            it.timeZone = timeZone
            it.firstRemoteAuthentication = new Date()
            it.requiresAvetmiss = true
            it.webServicesSecurityCode = servicesSecurityCode
            it
        }

        CCollege college = new CCollege()
        college.objectContext = objectContext
        college.college = c
        colleges.add(college)

        Preference preference = objectContext.newObject(Preference)
        preference.setCollege(c)
        preference.setCollege(college.college)
        preference.setName("payment.gateway.type")
        preference.setValueString(PaymentGatewayType.TEST.name())

        objectContext.commitChanges()
        return college
    }

    WillowUser createWillowUser(String email = "willow@willow.com",
                                String firstName = faker.name().firstName(),
                                String lastName = faker.name().lastName(),
                                String password = "password"
    ) {
        WillowUser user = objectContext.newObject(WillowUser.class)
        user.setEmail(email)
        user.setPassword(password)
        user.setFirstName(firstName)
        user.setLastName(lastName)
        objectContext.commitChanges()
        return user
    }

    CCollege collegeById(Long id) {
        CCollege college = this.colleges.find { it.college.id == id }
        if (college == null) {
            college = new CCollege()
            college.college = SelectById.query(College.class, id).selectOne(objectContext)
            if (college.college == null) {
                throw new IllegalArgumentException()
            }
        }
        return college
    }

    DataContext load() {
        ObjectSelect.query(College).select(objectContext).forEach({
            colleges.add(new CCollege(objectContext: this.objectContext, college: it).load())
        })
        return this
    }

    EntityRelationType newEntityRelationType(College college) {
        EntityRelationType relationType = objectContext.newObject(EntityRelationType)
        relationType.college = college
        relationType.name = "Default"
        relationType.toName = "Similar"
        relationType.fromName = "Similar"
        relationType.isShownOnWeb = Boolean.TRUE
        relationType.shoppingCart = EntityRelationCartAction.NO_ACTION
        objectContext.commitChanges()
        return relationType
    }
}
