package ish.oncourse.server.lifecycle

import ish.CayenneIshTestCase
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.cayenne.AccountTransaction
import ish.oncourse.server.cayenne.PaymentIn
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.SelectById
import org.dbunit.dataset.ReplacementDataSet
import org.dbunit.dataset.xml.FlatXmlDataSet
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import java.util.concurrent.ExecutionException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future

import static ish.common.types.PaymentStatus.SUCCESS
import static junit.framework.Assert.assertEquals
import static org.junit.Assert.assertTrue

class TransactionsLifecycleListenerTest extends CayenneIshTestCase {

    ICayenneService cayenneService

    @BeforeEach
    void setup() throws Exception {
        wipeTables()
        cayenneService = injector.getInstance(ICayenneService.class)
        InputStream st = TransactionsLifecycleListenerTest.classLoader.getResourceAsStream("ish/oncourse/server/lifecycle/transactionsLifecycleListenerTestDataSet.xml")
        FlatXmlDataSet dataSet = new FlatXmlDataSetBuilder().build(st)
        ReplacementDataSet rDataSet = new ReplacementDataSet(dataSet)
        rDataSet.addReplacementObject("[null]", null)
        executeDatabaseOperation(rDataSet)
        super.setup()
    }


    @Test
    void testCayenneTransaction() {
        PaymentIn paymentIn = SelectById.query(PaymentIn, 1L).selectOne(cayenneService.newContext)
        PaymentIn paymentIn1 = SelectById.query(PaymentIn, 1L).selectOne(cayenneService.newContext)

        paymentIn.status = SUCCESS
        paymentIn1.status = SUCCESS


        ExecutorService asyncThreadExecutor = Executors.newFixedThreadPool(2)


        Future<?> future1 = asyncThreadExecutor.submit {
            paymentIn.objectContext.commitChanges()
        }

        Future<?> future2 = asyncThreadExecutor.submit {
            paymentIn1.objectContext.commitChanges()
        }

        try {
            while (future1.get() && future2.get()) {
                //do nothing, need to keep main thread
            }
        } catch (InterruptedException e) {
            assertTrue(false)
        } catch (ExecutionException e) {
            assertTrue(false)
        } finally {
            asyncThreadExecutor.shutdown()
        }

        List<AccountTransaction> lines = ObjectSelect.query(AccountTransaction).select(cayenneService.newContext)
        assertEquals(2, lines.size())
    }
}
