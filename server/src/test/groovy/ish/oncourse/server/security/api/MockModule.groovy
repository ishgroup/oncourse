package ish.oncourse.server.security.api

import com.google.inject.Binder
import com.google.inject.Module
import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import ish.oncourse.server.services.ISystemUserService

@CompileStatic
class MockModule implements Module {

    
    @Override
    void configure(Binder binder) {
        binder.bind(ISystemUserService.class).to(MockSystemUserService.class).asEagerSingleton()
    }
}
