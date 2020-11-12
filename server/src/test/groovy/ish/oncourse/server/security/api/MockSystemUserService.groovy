package ish.oncourse.server.security.api

import ish.oncourse.server.cayenne.SystemUser
import ish.oncourse.server.services.ISystemUserService

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
