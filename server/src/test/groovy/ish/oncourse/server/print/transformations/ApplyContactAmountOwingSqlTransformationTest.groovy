package ish.oncourse.server.print.transformations

import groovy.time.TimeCategory
import ish.CayenneIshTestCase
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.cayenne.Invoice
import ish.oncourse.server.cayenne.PaymentIn
import ish.oncourse.server.cayenne.PaymentOut
import ish.util.LocalDateUtils
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.SQLSelect
import org.apache.cayenne.query.SelectById
import org.apache.commons.lang3.time.DateUtils
import org.dbunit.dataset.ReplacementDataSet
import org.dbunit.dataset.xml.FlatXmlDataSet
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder
import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Created by akoiro on 8/06/2016.
 */
class ApplyContactAmountOwingSqlTransformationTest extends CayenneIshTestCase {
    private ICayenneService cayenneService

    private today
    private yesterday
    private fiveDaysAgo
    private tenDaysAgo

    @Before
    void before() throws Exception {
        wipeTables()
        cayenneService = injector.getInstance(ICayenneService.class)

        use(TimeCategory, DateUtils) {
            today = new Date()
            yesterday = (today - 1.days).truncate(Calendar.DAY_OF_MONTH)
            fiveDaysAgo = (today - 5.days).truncate(Calendar.DAY_OF_MONTH)
            tenDaysAgo = (today - 10.days).truncate(Calendar.DAY_OF_MONTH)
        }

        //import dbunit dataset
        InputStream st = ApplyContactAmountOwingSqlTransformationTest.class.getClassLoader().getResourceAsStream("ish/oncourse/server/print/ApplyInvoiceAmountOwingSqlTransformationTest.xml")
        FlatXmlDataSet dataSet = new FlatXmlDataSetBuilder().build(st)
        ReplacementDataSet rDataSet = new ReplacementDataSet(dataSet)
        rDataSet.addReplacementObject("[today]", today)
        rDataSet.addReplacementObject("[yesterday]", yesterday)
        rDataSet.addReplacementObject("[fiveDaysAgo]", fiveDaysAgo)
        rDataSet.addReplacementObject("[tenDaysAgo]", tenDaysAgo)
        executeDatabaseOperation(rDataSet)

    }

    /**
     * Invoice.invoiceDate = 10 days ago
     * PaymentIn.paymentDate = 5 days ago
     * PaymentOut.paymentDate = 1 day ago
     *
     * expected result for 11 days ago: 0 - no invoices
     * expected result for 9 days ago: 1 - one invoice, no payments
     * expected result for 4 days ago: 0 - one invoice, one payment
     * expected result for today: 1 - one invoice, one paymentIn, one paymentOut
     */
    @Test
    void test() {
        ObjectContext context = cayenneService.newContext

        Invoice invoice = SelectById.query(Invoice, 1).selectOne(context)
        assertEquals(LocalDateUtils.dateToValue(tenDaysAgo), invoice.invoiceDate)

        PaymentIn paymentIn = SelectById.query(PaymentIn, 1).selectOne(context)
        Date.use(LocalDateUtils) {
            assertEquals(LocalDateUtils.dateToValue(fiveDaysAgo), paymentIn.paymentDate)
        }

        PaymentOut paymentIOut = SelectById.query(PaymentOut, 1).selectOne(context)
        assertEquals(LocalDateUtils.dateToValue(yesterday), paymentIOut.paymentDate)

        ApplyContactAmountOwingSqlTransformation applyTransformation =  new ApplyContactAmountOwingSqlTransformation(context: context)

        use(TimeCategory) {
            def date = new Date() - 11.days
            assertTrue(invoice.invoiceDate.isAfter(LocalDateUtils.dateToValue(date)))
            assertEquals(0, SQLSelect.dataRowQuery(applyTransformation.sql.sqlInvoice).params("date", date).select(context).size())
            applyTransformation.date = date
            assertEquals("11 days ago", 0, applyTransformation.apply().size())

            date = new Date() - 9.days
            applyTransformation.date = date
            assertEquals("11 days ago", 1, applyTransformation.apply().size())

            date = new Date() - 4.days
            applyTransformation.date = date
            assertEquals("4 days ago", 0, applyTransformation.apply().size())

            date = new Date()
            applyTransformation.date = date
            assertEquals("today", 1, applyTransformation.apply().size())
        }
    }
}
