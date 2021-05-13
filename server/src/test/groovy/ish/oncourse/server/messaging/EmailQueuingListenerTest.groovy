/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.server.messaging

import groovy.transform.CompileStatic
import ish.DatabaseSetup
import ish.TestWithDatabase
import ish.common.types.PaymentSource
import ish.common.types.ProductStatus
import ish.math.Money
import ish.oncourse.server.cayenne.*
import ish.oncourse.server.scripting.GroovyScriptService
import ish.oncourse.server.upgrades.DataPopulation
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.SelectById
import org.apache.cayenne.query.SelectQuery
import org.apache.commons.lang3.time.DateUtils
import org.dbunit.dataset.ReplacementDataSet
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@CompileStatic
@DatabaseSetup(value = "ish/oncourse/server/cayenne/voucherTest.xml")
class EmailQueuingListenerTest extends TestWithDatabase {

    @Override
    void dataSourceReplaceValues(ReplacementDataSet rDataSet) {
        Date start1 = DateUtils.addDays(new Date(), -4)
        Date start2 = DateUtils.addDays(new Date(), -2)
        Date start3 = DateUtils.addDays(new Date(), 2)
        Date start4 = DateUtils.addDays(new Date(), 4)
        rDataSet.addReplacementObject("[start_date1]", start1)
        rDataSet.addReplacementObject("[start_date2]", start2)
        rDataSet.addReplacementObject("[start_date3]", start3)
        rDataSet.addReplacementObject("[start_date4]", start4)
        rDataSet.addReplacementObject("[end_date1]", DateUtils.addHours(start1, 2))
        rDataSet.addReplacementObject("[end_date2]", DateUtils.addHours(start2, 2))
        rDataSet.addReplacementObject("[end_date3]", DateUtils.addHours(start3, 2))
        rDataSet.addReplacementObject("[end_date4]", DateUtils.addHours(start4, 2))
        rDataSet.addReplacementObject("[null]", null)
    }

    @BeforeEach
    void injectors() throws Exception {
        super.setup()
        injector.getInstance(GroovyScriptService.class).initTriggers()
        DataPopulation dataPopulation = injector.getInstance(DataPopulation.class)

        try {
            dataPopulation.run()
        } catch (UnsupportedEncodingException ignored) {
        }
    }
    
    @Test
    void testVoucherPurchaseConfirmation() throws Exception {

        ObjectContext context = cayenneService.getNewNonReplicatingContext()

        Product vp = SelectById.query(Product.class, 1L).selectOne(context)
        InvoiceLine il = SelectById.query(InvoiceLine.class, 1).selectOne(context)

        Voucher voucher = context.newObject(Voucher.class)

        voucher.setProduct(vp)
        voucher.setCode("test")
        voucher.setInvoiceLine(il)
        voucher.setExpiryDate(new Date())
        voucher.setRedemptionValue(new Money("100.0"))
        voucher.setStatus(ProductStatus.NEW)
        voucher.setSource(PaymentSource.SOURCE_ONCOURSE)

        context.commitChanges()

        // give the script running in separate thread some time to queue emails
        Thread.sleep(3000)

        Assertions.assertTrue(context.select(SelectQuery.query(MessagePerson.class)).isEmpty())
        Assertions.assertTrue(context.select(SelectQuery.query(Message.class)).isEmpty())

        voucher.setStatus(ProductStatus.ACTIVE)
        context.commitChanges()

        // give the script running in separate thread some time to queue emails
        Thread.sleep(3000)

        Assertions.assertEquals(1, context.select(SelectQuery.query(MessagePerson.class)).size())
        Assertions.assertEquals(1, context.select(SelectQuery.query(Message.class)).size())

        voucher.setRedeemedCourseCount(1)
        voucher.setStatus(ProductStatus.ACTIVE)
        context.commitChanges()

        // give the script running in separate thread some time to queue emails
        Thread.sleep(3000)

        Assertions.assertEquals(1, context.select(SelectQuery.query(MessagePerson.class)).size())
        Assertions.assertEquals(1, context.select(SelectQuery.query(Message.class)).size())

    }

    
    @Test
    void testVoucherPersistedActivePurchaseConfirmation() throws Exception {
        Product vp = SelectById.query(Product.class, 1L).selectOne(cayenneContext)
        InvoiceLine il = SelectById.query(InvoiceLine.class, 1).selectOne(cayenneContext)

        Voucher voucher = cayenneContext.newObject(Voucher.class)

        voucher.setProduct(vp)
        voucher.setCode("test")
        voucher.setInvoiceLine(il)
        voucher.setExpiryDate(new Date())
        voucher.setRedemptionValue(new Money("100.0"))
        voucher.setStatus(ProductStatus.NEW)
        voucher.setSource(PaymentSource.SOURCE_ONCOURSE)
        voucher.setStatus(ProductStatus.ACTIVE)

        cayenneContext.commitChanges()

        // give the script running in separate thread some time to queue emails
        Thread.sleep(3000)

        Assertions.assertEquals(1, cayenneContext.select(SelectQuery.query(MessagePerson.class)).size())
        Assertions.assertEquals(1, cayenneContext.select(SelectQuery.query(Message.class)).size())

        voucher.setRedeemedCourseCount(1)
        voucher.setStatus(ProductStatus.ACTIVE)
        cayenneContext.commitChanges()

        // give the script running in separate thread some time to queue emails
        Thread.sleep(3000)

        Assertions.assertEquals(1, cayenneContext.select(SelectQuery.query(MessagePerson.class)).size())
        Assertions.assertEquals(1, cayenneContext.select(SelectQuery.query(Message.class)).size())

    }

}
