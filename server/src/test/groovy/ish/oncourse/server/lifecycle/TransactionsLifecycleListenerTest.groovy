package ish.oncourse.server.lifecycle

import groovy.transform.CompileStatic
import ish.CayenneIshTestCase
import ish.DatabaseSetup
import ish.oncourse.server.cayenne.AccountTransaction
import ish.oncourse.server.cayenne.PaymentIn
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.SelectById
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

import java.util.concurrent.ExecutionException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future

import static ish.common.types.PaymentStatus.SUCCESS

@CompileStatic
@DatabaseSetup(value = "ish/oncourse/server/lifecycle/transactionsLifecycleListenerTestDataSet.xml")
class TransactionsLifecycleListenerTest extends CayenneIshTestCase {
    
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
            Assertions.assertTrue(false)
        } catch (ExecutionException e) {
            Assertions.assertTrue(false)
        } finally {
            asyncThreadExecutor.shutdown()
        }

        List<AccountTransaction> lines = ObjectSelect.query(AccountTransaction).select(cayenneService.newContext)
        Assertions.assertEquals(2, lines.size())
    }
}
