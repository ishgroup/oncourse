/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.commercial.replication.services

import groovy.transform.CompileStatic
import ish.oncourse.server.cayenne.Queueable
import ish.oncourse.webservices.util.GenericReplicatedRecord
import ish.oncourse.webservices.util.GenericReplicationStub
import ish.oncourse.webservices.util.StubUtils
import ish.oncourse.webservices.v23.stubs.replication.HollowStub
import ish.oncourse.webservices.v23.stubs.replication.ReplicatedRecord
import org.apache.cayenne.ObjectContext

@CompileStatic
class ReplicationUtils {

    @SuppressWarnings("unchecked")
    static Collection<Queueable> toCollection(Object object) {

        if (object == null) {
            return Collections.emptyList()
        } else if (object instanceof Collection) {
            return new ArrayList<>((Collection<Queueable>) object)
        } else {
            def q = (Queueable) object
            return Collections.singleton(q)
        }
    }

    static Class<? extends Queueable> getEntityClass(ObjectContext objectContext, String entityIdentifier) {
        @SuppressWarnings("unchecked")
        def entityClass = (Class<? extends Queueable>) objectContext.getEntityResolver().getObjEntity(entityIdentifier).getJavaClass()
        return entityClass
    }


    static GenericReplicatedRecord toReplicatedRecord(GenericReplicationStub stub) {
        def replRecord = new ReplicatedRecord()
        StubUtils.setSuccessStatus(replRecord)
        replRecord.setStub(toHollow(stub))
        return replRecord
    }

    static HollowStub toHollow(GenericReplicationStub stub) {
        def hollowStub = new HollowStub()
        hollowStub.setEntityIdentifier(stub.getEntityIdentifier())
        hollowStub.setWillowId(stub.getWillowId())
        hollowStub.setAngelId(stub.getAngelId())
        def today = new Date()
        hollowStub.setModified(today)
        hollowStub.setCreated(today)
        return hollowStub
    }
}
