package ish.oncourse.server.accounting

import groovy.transform.CompileStatic
import ish.TestWithDatabase
import ish.common.types.AccountTransactionType
import ish.math.Money
import ish.oncourse.server.accounting.builder.TransactionsBuilder
import ish.oncourse.server.cayenne.Account
import ish.oncourse.server.cayenne.AccountTransaction
import ish.request.AccountTransactionRequest
import org.apache.cayenne.query.ObjectSelect
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import java.time.LocalDate

@CompileStatic
class AccountTransactionServiceTest extends TestWithDatabase {
    
    private AccountTransactionService accountTransactionService

    @BeforeEach
    void setup() throws Exception {
        accountTransactionService = injector.getInstance(AccountTransactionService.class)
    }
    
    @Test
    void test() {
        List<AccountTransaction> before = ObjectSelect.query(AccountTransaction)
                .select(cayenneContext)
        Assertions.assertTrue(before.empty)

        List<Account> accounts = ObjectSelect.query(Account)
                .select(cayenneContext)
        Assertions.assertTrue(accounts.size() > 2)

        AccountTransactionRequest request = AccountTransactionRequest.valueOf(new Money(70,0), accounts[0].id, accounts[1].id, LocalDate.now())
        accountTransactionService.createManualTransactions(request)

        List<AccountTransaction> after = ObjectSelect.query(AccountTransaction)
                .select(cayenneContext)
        Assertions.assertEquals(2, after.size())


        TransactionsBuilder builder = new TransactionsBuilder() {
            TransactionSettings build() {
                AccountTransactionDetail detail1 = AccountTransactionDetail.valueOf(accounts[0], accounts[1], new Money(35 as BigDecimal), AccountTransactionType.PAYMENT_IN_LINE, 7L, LocalDate.now())
                AccountTransactionDetail detail2 = AccountTransactionDetail.valueOf(accounts[0], accounts[1], new Money(50 as BigDecimal), AccountTransactionType.PAYMENT_IN_LINE, 7L, LocalDate.now())
                TransactionSettings.valueOf(detail1, detail2)
                        .initialTransaction()
            }
        }

        accountTransactionService.createTransactions(builder)

        after = ObjectSelect.query(AccountTransaction)
                .select(cayenneContext)
        Assertions.assertEquals(6, after.size())

        builder = new TransactionsBuilder() {
            TransactionSettings build() {
                AccountTransactionDetail detail1 = AccountTransactionDetail.valueOf(accounts[1], accounts[0], new Money(90 as BigDecimal), AccountTransactionType.PAYMENT_IN_LINE, 7L, LocalDate.now())
                AccountTransactionDetail detail2 = AccountTransactionDetail.valueOf(accounts[1], accounts[0], new Money(70 as BigDecimal), AccountTransactionType.PAYMENT_IN_LINE, 7L, LocalDate.now())
                TransactionSettings.valueOf(detail1, detail2)
                        .initialTransaction()
            }
        }

        accountTransactionService.createTransactions(builder)

        after = ObjectSelect.query(AccountTransaction)
                .select(cayenneContext)
        Assertions.assertEquals(6, after.size())


        builder = new TransactionsBuilder() {
            TransactionSettings build() {
                AccountTransactionDetail detail1 = AccountTransactionDetail.valueOf(accounts[1], accounts[0], new Money(190 as BigDecimal), AccountTransactionType.PAYMENT_IN_LINE, 7L, LocalDate.now())
                TransactionSettings.valueOf(detail1)
            }
        }

        accountTransactionService.createTransactions(builder)

        after = ObjectSelect.query(AccountTransaction)
                .select(cayenneContext)
        Assertions.assertEquals(8, after.size())


        builder = new TransactionsBuilder() {
            TransactionSettings build() {
                AccountTransactionDetail detail1 = AccountTransactionDetail.valueOf(accounts[1], accounts[0], new Money(120 as BigDecimal), AccountTransactionType.INVOICE_LINE, 7L, LocalDate.now())
                TransactionSettings.valueOf(detail1)
                        .initialTransaction()
            }
        }

        accountTransactionService.createTransactions(builder)

        after = ObjectSelect.query(AccountTransaction)
                .select(cayenneContext)
        Assertions.assertEquals(10, after.size())
    }


}
