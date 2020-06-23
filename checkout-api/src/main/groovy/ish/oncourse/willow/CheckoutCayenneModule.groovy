package ish.oncourse.willow

import org.apache.cayenne.access.ValidationFilter
import org.apache.cayenne.configuration.server.ServerModule
import org.apache.cayenne.di.Binder
import org.apache.cayenne.di.Module

class CheckoutCayenneModule  implements Module {

    @Override
    void configure(Binder binder) {
        ServerModule.contributeDomainSyncFilters(binder).add(new ValidationFilter())
    }
}
