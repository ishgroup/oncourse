package ish.oncourse.server.security.api

import com.google.inject.Binder
import com.google.inject.Module
import ish.oncourse.server.services.ISystemUserService

class MockModule implements Module {

    @Override
    void configure(Binder binder) {
        binder.bind(ISystemUserService.class).to(MockSystemUserService.class).asEagerSingleton()
    }
}
