package ish.oncourse.willow.billing

import com.google.inject.Binder
import io.bootique.ConfigModule
import io.bootique.jetty.JettyModule
import ish.oncourse.configuration.ISHHealthCheckServlet

class BillingModule extends ConfigModule {
    
    @Override
    void configure(Binder binder) {
        JettyModule.extend(binder).addServlet(ISHHealthCheckServlet)

    }
}

