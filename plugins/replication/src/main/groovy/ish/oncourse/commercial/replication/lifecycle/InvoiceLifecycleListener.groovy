/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.commercial.replication.lifecycle

import ish.oncourse.commercial.replication.cayenne.QueuedRecord
import ish.oncourse.commercial.replication.cayenne.QueuedRecordAction
import ish.oncourse.commercial.replication.cayenne.QueuedTransaction
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.ISHDataContext
import ish.oncourse.server.cayenne.Invoice
import ish.oncourse.server.services.InvoiceOverdueUpdateJob
import org.apache.cayenne.annotation.PostPersist
import org.apache.cayenne.annotation.PostUpdate
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

class InvoiceLifecycleListener {

    private static final Logger logger = LogManager.logger
    private static final String PREFIX_updateAmountOwing = "updateAmountOwing"

    ICayenneService cayenneService
    
    InvoiceLifecycleListener(ICayenneService cayenneService) {
        this.cayenneService = cayenneService
    }
    
    @PostUpdate(value = Invoice.class)
    void postUpdate(Invoice entity) {
        createReplicationDataFor(entity)
    }

    @PostPersist(value = Invoice.class)
    void postPersist(Invoice entity) {
        createReplicationDataFor(entity)
    }

    /**
     * when we get an Invoice from willow we call updateAmoutOwing for the Invoice, but these changes are not replicated back to willow.
     * So this code creates queued recrord for the invoice, and actual amountOwing of the invoice is replicated back to willow
     * @param invoice
     */
    private void createReplicationDataFor(Invoice invoice)
    {
        try {
            ISHDataContext dataContext = (ISHDataContext) invoice.getObjectContext()
            //skip creation of Queued records when INVOICE_OVERDUE_UPDATE_JOB started (job updates 'overdue' property which is no stores on willow side)
            if (!dataContext.getIsRecordQueueingEnabled()
                    && (dataContext.getUserProperty(InvoiceOverdueUpdateJob.INVOICE_OVERDUE_UPDATE_JOB) == null
                    || dataContext.getUserProperty(InvoiceOverdueUpdateJob.INVOICE_OVERDUE_UPDATE_JOB) != "true"))
            {
                //the code creates QueuedTransaction for this invoice.
                ISHDataContext context = (ISHDataContext) cayenneService.getNewNonReplicatingContext()
                QueuedTransaction t = context.newObject(QueuedTransaction)
                Date today = new Date()

                String transactionKey =  PREFIX_updateAmountOwing + context.generateTransactionKey()

                t.setCreatedOn(today)
                t.setModifiedOn(today)
                t.setTransactionKey(transactionKey)

                QueuedRecord result = new QueuedRecord()
                result.setQueuedTransaction(t)
                result.setLastAttemptOn(today)
                result.setNumberOfAttempts(0)
                result.setTableName(invoice.getObjectId().getEntityName())
                result.setForeignRecordId(invoice.getId())
                result.setWillowId(invoice.getWillowId())
                result.setAction(QueuedRecordAction.UPDATE)
                context.registerNewObject(result)
                result.setQueuedTransaction(t)
                context.commitChanges()
            }
        } catch (Throwable e) {
            logger.error("Exception was thrown when we were trying to create replication entities for invoice" ,e)
        }
    }

}
