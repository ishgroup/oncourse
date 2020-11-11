/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.commercial.replication.handler

import com.google.inject.Inject
import ish.oncourse.commercial.replication.modules.ISoapPortLocator
import ish.oncourse.webservices.ITransactionGroupProcessor
import ish.oncourse.webservices.soap.v22.ReplicationFault
import ish.oncourse.webservices.util.GenericReplicatedRecord
import ish.oncourse.webservices.util.GenericReplicationRecords
import ish.oncourse.webservices.util.GenericTransactionGroup
import ish.oncourse.webservices.v22.stubs.replication.ReplicationResult

class InboundReplicationHandler implements ReplicationHandler {
    /**
     * SoapPortLocator
     */
    private final ISoapPortLocator soapPortLocator

    /**
     * TransactionGroupProcessor
     */
    private final ITransactionGroupProcessor transactionGroupAtomicUpdater

    @Inject
    InboundReplicationHandler(ISoapPortLocator soapPortLocator, ITransactionGroupProcessor transactionGroupAtomicUpdater) {
        super()
        this.soapPortLocator = soapPortLocator
        this.transactionGroupAtomicUpdater = transactionGroupAtomicUpdater
    }

    /**
     * @see ReplicationHandler#replicate()
     */
    @Override
    void replicate() {

        try {

            def port = this.soapPortLocator.replicationPort()
            GenericReplicationRecords records = port.getRecords()

            List<GenericReplicatedRecord> replicatedRecords = new ArrayList<>()

            for (GenericTransactionGroup group : records.getGenericGroups()) {
                replicatedRecords.addAll(this.transactionGroupAtomicUpdater.processGroup(group))
            }

            if (replicatedRecords.size() > 0) {
                ReplicationResult replResult = new ReplicationResult()
                replResult.getGenericReplicatedRecord().addAll(replicatedRecords)
                port.sendResults(replResult)
            }

        } catch (ReplicationFault e) {
            logger.error("Inbound replication failed. Message from server: {}", e.getFaultInfo().getDetailMessage())
        }
    }
}
