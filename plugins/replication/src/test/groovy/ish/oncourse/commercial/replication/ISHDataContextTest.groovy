/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.commercial.replication

import groovy.transform.CompileStatic
import ish.DatabaseSetup
import ish.TestWithDatabase
import ish.common.types.PaymentSource
import ish.common.types.PaymentType
import ish.math.Money
import ish.oncourse.commercial.replication.cayenne.QueuedRecord
import ish.oncourse.commercial.replication.cayenne.QueuedTransaction
import ish.oncourse.entity.services.SetPaymentMethod
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.cayenne.*
import ish.util.PaymentMethodUtil
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.SelectById
import org.apache.cayenne.query.SelectQuery
import org.apache.commons.lang3.time.DateUtils
import org.dbunit.dataset.ReplacementDataSet
import org.junit.jupiter.api.Test

import java.time.LocalDate
import static org.junit.jupiter.api.Assertions.assertEquals

/**
 */
@CompileStatic
@DatabaseSetup(value = 'ish/oncourse/commercial/replication/ishDataContextTestDataSet.xml')
class ISHDataContextTest extends TestWithDatabase {

	private ICayenneService cayenneService


    protected void dataSourceReplaceValues(ReplacementDataSet rDataSet) {
        Date start1 = DateUtils.addDays(new Date(), -2)
        Date start2 = DateUtils.addDays(new Date(), -2)
        rDataSet.addReplacementObject("[start_date1]", start1)
        rDataSet.addReplacementObject("[start_date2]", start2)
        rDataSet.addReplacementObject("[end_date1]", DateUtils.addHours(start1, 2))
        rDataSet.addReplacementObject("[end_date2]", DateUtils.addHours(start2, 2))
    }

	@Test
    void testTransactionKey() {

		ObjectContext context = cayenneService.getNewContext()

        context.deleteObjects(context.select(SelectQuery.query(QueuedRecord.class)))
        context.deleteObjects(context.select(SelectQuery.query(QueuedTransaction.class)))
        context.commitChanges()

        Account accountIn = SelectById.query(Account.class, 50).selectOne(context)
        Contact payer = SelectById.query(Contact.class, 1).selectOne(context)
        Invoice invoice1 = SelectById.query(Invoice.class, 1).selectOne(context)
        Invoice invoice2 = SelectById.query(Invoice.class, 2).selectOne(context)

        PaymentIn payment = context.newObject(PaymentIn.class)
        payment.setPaymentDate(LocalDate.now())
        payment.setAccountIn(accountIn)
        payment.setAmount(new Money(new BigDecimal(50)))
        payment.setPayer(payer)
        payment.setReconciled(false)
        payment.setSource(PaymentSource.SOURCE_ONCOURSE)
        SetPaymentMethod.valueOf(PaymentMethodUtil.getCustomPaymentMethod(context, PaymentMethod.class, PaymentType.EFT), payment).set()

        PaymentInLine pil1 = context.newObject(PaymentInLine.class)
        pil1.setInvoice(invoice1)
        pil1.setAccountOut(accountIn)
        pil1.setAmount(invoice1.getAmountOwing())
        pil1.setPaymentIn(payment)

        PaymentInLine pil2 = context.newObject(PaymentInLine.class)
        pil2.setInvoice(invoice2)
        pil2.setAccountOut(accountIn)
        pil2.setAmount(invoice2.getAmountOwing())
        pil2.setPaymentIn(payment)

        context.commitChanges()

        List<QueuedRecord> queuedRecords = context.select(SelectQuery.query(QueuedRecord.class))
        assertEquals(3, queuedRecords.size())
        Long transactionId = queuedRecords.get(0).getTransactionId()

        for (QueuedRecord r : queuedRecords) {
			assertEquals(transactionId, r.getTransactionId())
        }
	}
}
