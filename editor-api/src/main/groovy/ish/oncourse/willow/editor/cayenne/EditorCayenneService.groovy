package ish.oncourse.willow.editor.cayenne

import com.google.inject.Inject
import com.google.inject.Singleton
import ish.oncourse.services.lifecycle.QueueableLifecycleListener
import ish.oncourse.services.persistence.ICayenneService
import ish.oncourse.services.persistence.ISHObjectContext
import ish.oncourse.services.persistence.ISHObjectContextFactory
import org.apache.cayenne.DataChannel
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.configuration.server.ServerRuntime

@Singleton
class EditorCayenneService implements ICayenneService {

    private ServerRuntime runtime
    private ObjectContext sharedContext


    @Inject
    CayenneService(ServerRuntime runtime) {
        QueueableLifecycleListener listener = new QueueableLifecycleListener(this)
        this.runtime = runtime
        this.runtime.dataDomain.addFilter(listener)
        this.runtime.channel.entityResolver.callbackRegistry.addDefaultListener(listener)
        this.sharedContext = newContext()
    }

    ObjectContext newContext() {
        runtime.newContext()
    }

    @Override
    ObjectContext newNonReplicatingContext() {
        ObjectContext dc = newContext()
        if (dc instanceof ISHObjectContext) {
            (dc as ISHObjectContext).recordQueueingEnabled = false
        } else {
            throw new IllegalStateException("$ISHObjectContextFactory.name +  not installed as DataContext factory")
        }
        return dc
    }

    @Override
    ObjectContext newContext(DataChannel parentChannel) {
        throw new UnsupportedOperationException()
    }

    @Override
    ObjectContext sharedContext() {
        return sharedContext
    }
}
