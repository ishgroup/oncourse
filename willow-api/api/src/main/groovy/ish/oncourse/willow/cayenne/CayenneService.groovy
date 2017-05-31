package ish.oncourse.willow.cayenne

import com.google.inject.Inject
import com.google.inject.Singleton
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.configuration.server.ServerRuntime

@Singleton
class CayenneService  {
    public static final String DISABLE_REPLICATION_PROPERTY = 'disableReplication'

    ServerRuntime runtime
    
    @Inject
    CayenneService(ServerRuntime runtime) {
        QueueableLifecycleListener listener = new QueueableLifecycleListener(cayenneService: this)
        this.runtime = runtime
        this.runtime.dataDomain.addFilter(listener)
        this.runtime.channel.entityResolver.callbackRegistry.addDefaultListener(listener)
    }

    ObjectContext newContext() {
        runtime.newContext()
    }

    ObjectContext getNewNonReplicatingContext() {
        ObjectContext objectContext = runtime.newContext()
        objectContext.setUserProperty(DISABLE_REPLICATION_PROPERTY, true)
        return objectContext
    }
}
