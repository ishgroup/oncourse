package ish.oncourse.willow.cayenne

import ish.oncourse.services.persistence.ISHObjectContext
import org.apache.cayenne.DataChannel
import org.apache.cayenne.access.DataContext
import org.apache.cayenne.access.ObjectStore
import org.apache.cayenne.configuration.server.DataContextFactory

class ISHObjectContextFactory extends DataContextFactory {
    
    protected DataContext newInstance(DataChannel parent, ObjectStore objectStore) {
        return new ISHObjectContext(parent, objectStore)
    }
}
