package ish.oncourse.server.security.api

import io.bootique.di.Binder
import io.bootique.di.BQModule
import groovy.transform.CompileStatic
import ish.oncourse.server.services.ISystemUserService

@CompileStatic
class MockModule implements BQModule {
    
    @Override
    void configure(Binder binder) {
        binder.bind(ISystemUserService.class).to(MockSystemUserService.class).initOnStartup()
    }
}
