package ish.oncourse.server.lifecycle

import groovy.transform.CompileStatic
import ish.CayenneIshTestCase
import ish.DatabaseSetup
import ish.oncourse.server.cayenne.AccountTransaction
import ish.oncourse.server.cayenne.Banking
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.SelectById
import org.apache.commons.lang3.time.DateUtils
import org.dbunit.dataset.ReplacementDataSet
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

import java.time.LocalDate

@CompileStatic
@DatabaseSetup(value = "ish/oncourse/server/lifecycle/bankingLifecycleListenerTestDataSet.xml")
class BankingLifecycleListenerTest extends CayenneIshTestCase {

    @Override
    protected void dataSourceReplaceValues(ReplacementDataSet rDataSet) {
        Date start1 = DateUtils.addDays(new Date(), -2)
        Date start2 = DateUtils.addDays(new Date(), -2)
        rDataSet.addReplacementObject("[start_date1]", start1)
        rDataSet.addReplacementObject("[start_date2]", start2)
        rDataSet.addReplacementObject("[end_date1]", DateUtils.addHours(start1, 2))
        rDataSet.addReplacementObject("[end_date2]", DateUtils.addHours(start2, 2))
        rDataSet.addReplacementObject("[null]", null)
    }
    
    @Test
    void testSettlementDateChanged() {
        List<AccountTransaction> accountTransactionsBefore = ObjectSelect.query(AccountTransaction.class)
                .select(cayenneContext)
        Assertions.assertTrue(accountTransactionsBefore.isEmpty())

        Banking banking = SelectById.query(Banking.class, 1)
                .selectOne(cayenneContext)

        LocalDate yesterday = LocalDate.now().minusDays(1)
        banking.setSettlementDate(yesterday)
        cayenneContext.commitChanges()

        List<AccountTransaction> accountTransactionsAfter = ObjectSelect.query(AccountTransaction.class)
                .select(cayenneContext)

        Assertions.assertEquals(16, accountTransactionsAfter.size())

        banking.setSettlementDate(LocalDate.now().minusDays(2))
        cayenneContext.commitChanges()

        List<AccountTransaction> accountTransactionsAfter2 = ObjectSelect.query(AccountTransaction.class)
                .select(cayenneContext)
        Assertions.assertEquals(32, accountTransactionsAfter2.size())
    }
}
