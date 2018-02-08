package ish.oncourse.api.cayenne

import com.google.inject.Inject
import com.google.inject.Singleton
import ish.oncourse.services.lifecycle.QueueableLifecycleListener
import ish.oncourse.services.persistence.ICayenneService
import ish.oncourse.services.persistence.ISHObjectContext
import org.apache.cayenne.DataChannel
import org.apache.cayenne.configuration.server.ServerRuntime
import org.apache.cayenne.tx.TransactionalOperation

@Singleton
class CayenneService implements ICayenneService {

    private ServerRuntime runtime
    private ISHObjectContext sharedContext
    
    @Inject
    CayenneService(ServerRuntime runtime) {
        QueueableLifecycleListener listener = new QueueableLifecycleListener(this)
        this.runtime = runtime
        this.runtime.dataDomain.addFilter(listener)
        this.runtime.channel.entityResolver.callbackRegistry.addDefaultListener(listener)
        this.sharedContext = newContext()
    }

    ISHObjectContext newContext() {
        runtime.newContext() as ISHObjectContext
    }
    
    @Override
    ISHObjectContext newNonReplicatingContext() {
        ISHObjectContext dc = newContext()
        dc.recordQueueingEnabled = false
        return dc
    }

    @Override
    ISHObjectContext newContext(DataChannel parentChannel) {
        throw new UnsupportedOperationException()
    }

    @Override
    ISHObjectContext sharedContext() {
        sharedContext
    }
    
    void performTransaction(TransactionalOperation op) {
        runtime.performInTransaction(op)
    }
}
