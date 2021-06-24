/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.commercial.replication.event

import com.google.inject.Inject
import ish.oncourse.commercial.replication.builders.IAngelStubBuilder
import ish.oncourse.commercial.replication.modules.ISoapPortLocator
import ish.oncourse.common.SystemEvent
import ish.oncourse.server.cayenne.SystemUser
import ish.oncourse.server.integration.OnCourseEventListener
import ish.oncourse.webservices.soap.v23.ReplicationPortType
import ish.oncourse.webservices.util.GenericReplicationResult
import ish.oncourse.webservices.util.GenericReplicationStub
import ish.oncourse.webservices.v23.stubs.replication.ReplicationRecords
import ish.oncourse.webservices.v23.stubs.replication.ReplicationStub
import ish.oncourse.webservices.v23.stubs.replication.TransactionGroup
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

class SystemUserEventListener implements OnCourseEventListener {
    
    private static final String TRANSACTION_KEY = 'system_user_event'
    
    @Inject
    ISoapPortLocator soapPortLocator
    
    @Inject
    IAngelStubBuilder stubBuilder
    private static final Logger logger = LogManager.getLogger(WillowValidator)

    @Override
    void dispatchEvent(SystemEvent systemEvent) {
        try {
            
            SystemUser user = systemEvent.getValue() as SystemUser
            var group = new TransactionGroup()
            group.transactionKeys << TRANSACTION_KEY

            var replicationRequest = new ReplicationRecords()
            group.replicationStub << (stubBuilder.convert(user) as ReplicationStub)

            replicationRequest.genericGroups << group
            
            ReplicationPortType port = this.soapPortLocator.replicationPort()
            port.sendRecords(replicationRequest)
        } catch (Exception e) {
            logger.catching(e)
        }
    }
}
