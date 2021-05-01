package ish.oncourse.server.security.api

import groovy.transform.CompileStatic
import ish.oncourse.server.cayenne.SystemUser
import ish.oncourse.server.services.ISystemUserService

@CompileStatic
class MockSystemUserService implements ISystemUserService {

    @Override
    List<SystemUser> getSystemUsersByRole(String name) {
        return null
    }


    @Override
    SystemUser getCurrentUser() {
        SystemUser systemUser = new SystemUser()
        systemUser.setIsAdmin(true)
        return systemUser
    }
}
