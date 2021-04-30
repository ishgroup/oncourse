package ish.oncourse.server.accounting

import ish.CayenneIshTestCase
import ish.common.types.AccountTransactionType
import ish.common.types.AccountType
import ish.math.Money
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.cayenne.Account
import ish.oncourse.server.cayenne.AccountTransaction
import org.apache.cayenne.query.ObjectSelect
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import java.time.LocalDate
import java.time.Month

import static junit.framework.TestCase.*

class CreateAccountTransactionsTest extends CayenneIshTestCase {

    private ICayenneService cayenneService
    
    @BeforeEach
    void setup() {
        cayenneService = injector.getInstance(ICayenneService)
        super.setup()
    }
    
    @Test
    void test() {
        List<AccountTransaction> before = ObjectSelect.query(AccountTransaction)
                .select(cayenneService.newContext)
        assertTrue(before.empty)

        List<Account> accounts = ObjectSelect.query(Account)
                .select(cayenneService.newContext)
       
        List<Account> allAssets = accounts.findAll { it.type == AccountType.ASSET }
        assertTrue(allAssets.size() > 2)
        Account liabilityAccount = accounts.find { it.type == AccountType.LIABILITY }
        assertNotNull(liabilityAccount)
        
        Account primaryAccount = allAssets[0]
        Account secondaryAccount = allAssets[1]
        assertFalse(primaryAccount.id == secondaryAccount.id)
        
        Money amount = new Money(50)
        LocalDate transactionDate = LocalDate.of(2018, Month.MAY, 28)


        AccountTransactionDetail detail = AccountTransactionDetail.valueOf(primaryAccount, secondaryAccount, amount, AccountTransactionType.JOURNAL, 0L, transactionDate)

        CreateAccountTransactions.valueOf(cayenneService.newContext, detail).create()

        List<AccountTransaction> after = ObjectSelect.query(AccountTransaction)
                .select(cayenneService.newContext)
        assertEquals(2, after.size())
        
        AccountTransaction accountTransaction = after.find { it.account.id == primaryAccount.id }
        assertEquals(amount, accountTransaction.amount)
        assertEquals(transactionDate, accountTransaction.transactionDate)
        assertEquals(AccountTransactionType.JOURNAL, accountTransaction.tableName)
        assertEquals(0L, accountTransaction.foreignRecordId)

        accountTransaction = after.find { it.account.id == secondaryAccount.id }
        assertEquals(amount.negate(), accountTransaction.amount)
        assertEquals(transactionDate, accountTransaction.transactionDate)
        assertEquals(AccountTransactionType.JOURNAL, accountTransaction.tableName)
        assertEquals(0L, accountTransaction.foreignRecordId)

        
        
        Money amount2 = new Money(50)
        detail = AccountTransactionDetail.valueOf(primaryAccount, liabilityAccount, amount2, AccountTransactionType.INVOICE_LINE, 11L, transactionDate)
        CreateAccountTransactions.valueOf(cayenneService.newContext, detail).create()

        after = ObjectSelect.query(AccountTransaction)
                .select(cayenneService.newContext)
        assertEquals(4, after.size())

        accountTransaction = after.findAll { it.foreignRecordId == 11L }.find { it.account.id == primaryAccount.id }
        assertEquals(amount2, accountTransaction.amount)
        assertEquals(transactionDate, accountTransaction.transactionDate)
        assertEquals(AccountTransactionType.INVOICE_LINE, accountTransaction.tableName)
        assertEquals(11L, accountTransaction.foreignRecordId)

        accountTransaction = after.findAll { it.foreignRecordId == 11L }.find { it.account.id == liabilityAccount.id }
        assertEquals(amount2, accountTransaction.amount)
        assertEquals(transactionDate, accountTransaction.transactionDate)
        assertEquals(AccountTransactionType.INVOICE_LINE, accountTransaction.tableName)
        assertEquals(11L, accountTransaction.foreignRecordId)
    }
}
