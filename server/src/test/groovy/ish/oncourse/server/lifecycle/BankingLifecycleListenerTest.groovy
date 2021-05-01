package ish.oncourse.server.lifecycle


import groovy.transform.CompileStatic
import ish.CayenneIshTestCase
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.cayenne.AccountTransaction
import ish.oncourse.server.cayenne.Banking
import org.apache.cayenne.access.DataContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.SelectById
import org.apache.commons.lang3.time.DateUtils
import org.dbunit.dataset.ReplacementDataSet
import org.dbunit.dataset.xml.FlatXmlDataSet
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import java.time.LocalDate

@CompileStatic
class BankingLifecycleListenerTest extends CayenneIshTestCase {

    private ICayenneService cayenneService

    
    @BeforeEach
    void setup() throws Exception {
        wipeTables()
        cayenneService = injector.getInstance(ICayenneService.class)

        InputStream st = BankingLifecycleListenerTest.class.getClassLoader().getResourceAsStream("ish/oncourse/server/lifecycle/bankingLifecycleListenerTestDataSet.xml")
        FlatXmlDataSet dataSet = new FlatXmlDataSetBuilder().build(st)

        ReplacementDataSet rDataSet = new ReplacementDataSet(dataSet)
        Date start1 = DateUtils.addDays(new Date(), -2)
        Date start2 = DateUtils.addDays(new Date(), -2)
        rDataSet.addReplacementObject("[start_date1]", start1)
        rDataSet.addReplacementObject("[start_date2]", start2)
        rDataSet.addReplacementObject("[end_date1]", DateUtils.addHours(start1, 2))
        rDataSet.addReplacementObject("[end_date2]", DateUtils.addHours(start2, 2))
        rDataSet.addReplacementObject("[null]", null)

        executeDatabaseOperation(rDataSet)
        super.setup()
    }

    @AfterEach
    void tearDown() {
        wipeTables()
    }


    
    @Test
    void testSettlementDateChanged() {
        DataContext context = cayenneService.getNewContext()

        List<AccountTransaction> accountTransactionsBefore = ObjectSelect.query(AccountTransaction.class)
                .select(context)
        Assertions.assertTrue(accountTransactionsBefore.isEmpty())

        Banking banking = SelectById.query(Banking.class, 1)
                .selectOne(context)

        LocalDate yesterday = LocalDate.now().minusDays(1)
        banking.setSettlementDate(yesterday)
        context.commitChanges()

        List<AccountTransaction> accountTransactionsAfter = ObjectSelect.query(AccountTransaction.class)
                .select(context)

        Assertions.assertEquals(16, accountTransactionsAfter.size())

        banking.setSettlementDate(LocalDate.now().minusDays(2))
        context.commitChanges()

        List<AccountTransaction> accountTransactionsAfter2 = ObjectSelect.query(AccountTransaction.class)
                .select(context)
        Assertions.assertEquals(32, accountTransactionsAfter2.size())
    }
}
