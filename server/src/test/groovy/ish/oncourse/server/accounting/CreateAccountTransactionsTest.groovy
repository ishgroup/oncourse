package ish.oncourse.server.accounting

import groovy.transform.CompileStatic
import ish.DatabaseSetup
import ish.TestWithDatabase
import ish.common.types.AccountTransactionType
import ish.common.types.AccountType
import ish.math.Money
import ish.oncourse.server.cayenne.Account
import ish.oncourse.server.cayenne.AccountTransaction
import org.apache.cayenne.query.ObjectSelect
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

import java.time.LocalDate
import java.time.Month

@CompileStatic
@DatabaseSetup
class CreateAccountTransactionsTest extends TestWithDatabase {

    @Test
    void test() {
        List<AccountTransaction> before = ObjectSelect.query(AccountTransaction)
                .select(cayenneContext)
        Assertions.assertTrue(before.empty)

        List<Account> accounts = ObjectSelect.query(Account)
                .select(cayenneContext)

        List<Account> allAssets = accounts.findAll { it.type == AccountType.ASSET }
        Assertions.assertTrue(allAssets.size() > 2)
        Account liabilityAccount = accounts.find { it.type == AccountType.LIABILITY }
        Assertions.assertNotNull(liabilityAccount)

        Account primaryAccount = allAssets[0]
        Account secondaryAccount = allAssets[1]
        Assertions.assertFalse(primaryAccount.id == secondaryAccount.id)

        Money amount = new Money(50 as BigDecimal)
        LocalDate transactionDate = LocalDate.of(2018, Month.MAY, 28)


        AccountTransactionDetail detail = AccountTransactionDetail.valueOf(primaryAccount, secondaryAccount, amount, AccountTransactionType.JOURNAL, 0L, transactionDate)

        CreateAccountTransactions.valueOf(cayenneContext, detail).create()

        List<AccountTransaction> after = ObjectSelect.query(AccountTransaction)
                .select(cayenneContext)
        Assertions.assertEquals(2, after.size())

        AccountTransaction accountTransaction = after.find { it.account.id == primaryAccount.id }
        Assertions.assertEquals(amount, accountTransaction.amount)
        Assertions.assertEquals(transactionDate, accountTransaction.transactionDate)
        Assertions.assertEquals(AccountTransactionType.JOURNAL, accountTransaction.tableName)
        Assertions.assertEquals(0L, accountTransaction.foreignRecordId)

        accountTransaction = after.find { it.account.id == secondaryAccount.id }
        Assertions.assertEquals(amount.negate(), accountTransaction.amount)
        Assertions.assertEquals(transactionDate, accountTransaction.transactionDate)
        Assertions.assertEquals(AccountTransactionType.JOURNAL, accountTransaction.tableName)
        Assertions.assertEquals(0L, accountTransaction.foreignRecordId)


        Money amount2 = new Money(50 as BigDecimal)
        detail = AccountTransactionDetail.valueOf(primaryAccount, liabilityAccount, amount2, AccountTransactionType.INVOICE_LINE, 11L, transactionDate)
        CreateAccountTransactions.valueOf(cayenneContext, detail).create()

        after = ObjectSelect.query(AccountTransaction)
                .select(cayenneContext)
        Assertions.assertEquals(4, after.size())

        accountTransaction = after.findAll { it.foreignRecordId == 11L }.find { it.account.id == primaryAccount.id }
        Assertions.assertEquals(amount2, accountTransaction.amount)
        Assertions.assertEquals(transactionDate, accountTransaction.transactionDate)
        Assertions.assertEquals(AccountTransactionType.INVOICE_LINE, accountTransaction.tableName)
        Assertions.assertEquals(11L, accountTransaction.foreignRecordId)

        accountTransaction = after.findAll { it.foreignRecordId == 11L }.find { it.account.id == liabilityAccount.id }
        Assertions.assertEquals(amount2, accountTransaction.amount)
        Assertions.assertEquals(transactionDate, accountTransaction.transactionDate)
        Assertions.assertEquals(AccountTransactionType.INVOICE_LINE, accountTransaction.tableName)
        Assertions.assertEquals(11L, accountTransaction.foreignRecordId)
    }
}
