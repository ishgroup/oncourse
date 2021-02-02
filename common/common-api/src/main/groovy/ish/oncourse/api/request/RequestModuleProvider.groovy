package ish.oncourse.api.request

import com.google.inject.Module
import io.bootique.BQModuleProvider 

class RequestModuleProvider implements BQModuleProvider {
    @Override
    Module module() {
        return new RequestModule()
    }
}