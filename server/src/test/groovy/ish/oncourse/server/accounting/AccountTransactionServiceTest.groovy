package ish.oncourse.server.accounting

import ish.CayenneIshTestCase
import ish.common.types.AccountTransactionType
import ish.math.Money
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.accounting.builder.TransactionsBuilder
import ish.oncourse.server.cayenne.Account
import ish.oncourse.server.cayenne.AccountTransaction
import ish.request.AccountTransactionRequest
import org.apache.cayenne.query.ObjectSelect
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import java.time.LocalDate

import static junit.framework.TestCase.assertEquals
import static junit.framework.TestCase.assertTrue

class AccountTransactionServiceTest extends CayenneIshTestCase {

    private AccountTransactionService accountTransactionService
    private ICayenneService cayenneService

    @BeforeEach
    void setup() {
        cayenneService = injector.getInstance(ICayenneService)
        accountTransactionService = injector.getInstance(AccountTransactionService)
        super.setup()
        
        
        
    }

    @Test
    void test() {
        List<AccountTransaction> before = ObjectSelect.query(AccountTransaction)
                .select(cayenneService.newContext)
        assertTrue(before.empty)

        List<Account> accounts = ObjectSelect.query(Account)
                .select(cayenneService.newContext)
        assertTrue(accounts.size() > 2)
        
        AccountTransactionRequest request = AccountTransactionRequest.valueOf(new Money(70), accounts[0].id, accounts[1].id, LocalDate.now())
        accountTransactionService.createManualTransactions(request)

        List<AccountTransaction> after = ObjectSelect.query(AccountTransaction)
                .select(cayenneService.newContext)
        assertEquals(2, after.size())


        TransactionsBuilder builder = new TransactionsBuilder() {
            TransactionSettings build() {
                AccountTransactionDetail detail1 = AccountTransactionDetail.valueOf(accounts[0], accounts[1], new Money(35), AccountTransactionType.PAYMENT_IN_LINE, 7L, LocalDate.now())
                AccountTransactionDetail detail2 = AccountTransactionDetail.valueOf(accounts[0], accounts[1], new Money(50), AccountTransactionType.PAYMENT_IN_LINE, 7L, LocalDate.now())
                TransactionSettings.valueOf(detail1, detail2)
                        .initialTransaction()
            }
        }
        
        accountTransactionService.createTransactions(builder)

        after = ObjectSelect.query(AccountTransaction)
                .select(cayenneService.newContext)
        assertEquals(6, after.size())

        builder = new TransactionsBuilder() {
            TransactionSettings build() {
                AccountTransactionDetail detail1 = AccountTransactionDetail.valueOf(accounts[1], accounts[0], new Money(90), AccountTransactionType.PAYMENT_IN_LINE, 7L, LocalDate.now())
                AccountTransactionDetail detail2 = AccountTransactionDetail.valueOf(accounts[1], accounts[0], new Money(70), AccountTransactionType.PAYMENT_IN_LINE, 7L, LocalDate.now())
                TransactionSettings.valueOf(detail1, detail2)
                        .initialTransaction()
            }
        }

        accountTransactionService.createTransactions(builder)

        after = ObjectSelect.query(AccountTransaction)
                .select(cayenneService.newContext)
        assertEquals(6, after.size())



        builder = new TransactionsBuilder() {
            TransactionSettings build() {
                AccountTransactionDetail detail1 = AccountTransactionDetail.valueOf(accounts[1], accounts[0], new Money(190), AccountTransactionType.PAYMENT_IN_LINE, 7L, LocalDate.now())
                TransactionSettings.valueOf(detail1)
            }
        }

        accountTransactionService.createTransactions(builder)

        after = ObjectSelect.query(AccountTransaction)
                .select(cayenneService.newContext)
        assertEquals(8, after.size())


        builder = new TransactionsBuilder() {
            TransactionSettings build() {
                AccountTransactionDetail detail1 = AccountTransactionDetail.valueOf(accounts[1], accounts[0], new Money(120), AccountTransactionType.INVOICE_LINE, 7L, LocalDate.now())
                TransactionSettings.valueOf(detail1)
                        .initialTransaction()
            }
        }

        accountTransactionService.createTransactions(builder)

        after = ObjectSelect.query(AccountTransaction)
                .select(cayenneService.newContext)
        assertEquals(10, after.size())
    }
    
    
    
}
