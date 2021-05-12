package ish.oncourse.server.services

import com.google.inject.Inject
import groovy.transform.CompileStatic
import ish.oncourse.API
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.cayenne.ACLRole
import ish.oncourse.server.cayenne.SystemUser
import org.apache.cayenne.query.ObjectSelect
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

@CompileStatic
class TestSystemUserService implements ISystemUserService {

    static final Logger logger = LogManager.getLogger()

    private ICayenneService cayenneService

    @Inject
    TestSystemUserService(ICayenneService cayenneService) {
        this.cayenneService = cayenneService
    }

    /**
     *
     * @param name of role
     * @return a list of all system users with certain access role
     */
    @API
    List<SystemUser> getSystemUsersByRole(String name) {
        return ObjectSelect.query(SystemUser).where(SystemUser.ACL_ROLES.dot(ACLRole.NAME).eq(name)).select(cayenneService.newContext)
    }

    SystemUser getCurrentUser() {
        return ObjectSelect.query(SystemUser).orderBy(SystemUser.ID.asc()).selectFirst(cayenneService.newContext)
    }
}
