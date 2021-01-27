package ish.oncourse.api.jetty

import com.google.inject.Module
import io.bootique.BQModuleProvider

class HealthCheckModuleProvider implements BQModuleProvider {
    @Override
    Module module() {
        return new HealthCheckModule()
    }
}
