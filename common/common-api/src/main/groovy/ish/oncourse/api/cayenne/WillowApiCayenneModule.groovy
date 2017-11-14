package ish.oncourse.api.cayenne

import ish.math.MoneyType
import org.apache.cayenne.configuration.ObjectContextFactory
import org.apache.cayenne.configuration.server.ServerModule
import org.apache.cayenne.di.Binder
import org.apache.cayenne.di.Module

class WillowApiCayenneModule implements Module {
    @Override
    void configure(Binder binder) {
        binder.bind(ObjectContextFactory).to(ISHObjectContextFactory)
        ServerModule.contributeUserTypes(binder).add(MoneyType)
    }
}
