/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.server.services

import groovy.transform.CompileStatic
import ish.TestWithDatabase
import ish.common.types.AccountTransactionType
import ish.common.types.ProductStatus
import ish.math.Money
import ish.oncourse.server.accounting.AccountTransactionService
import ish.oncourse.server.cayenne.Account
import ish.oncourse.server.cayenne.AccountTransaction
import ish.oncourse.server.cayenne.Voucher
import org.apache.cayenne.query.SelectById
import org.apache.cayenne.query.SelectQuery
import org.apache.commons.lang3.time.DateUtils
import org.dbunit.dataset.ReplacementDataSet
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

@CompileStatic
class VoucherExpiryJobTest extends TestWithDatabase {
    private AccountTransactionService accountTransactionService = injector.getInstance(AccountTransactionService.class)

    @Override
    protected void dataSourceReplaceValues(ReplacementDataSet rDataSet) {
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

    @Test
    void testVoucherExpiry() {
        Voucher expiredMoneyVoucher = SelectById.query(Voucher.class, 4).selectOne(cayenneContext)
        Voucher expiredCourseVoucher = SelectById.query(Voucher.class, 1).selectOne(cayenneContext)
        Voucher unexpiredVoucher = SelectById.query(Voucher.class, 5).selectOne(cayenneContext)

        Assertions.assertEquals(ProductStatus.ACTIVE, expiredMoneyVoucher.getStatus())
        Assertions.assertEquals(ProductStatus.ACTIVE, expiredCourseVoucher.getStatus())
        Assertions.assertEquals(ProductStatus.ACTIVE, unexpiredVoucher.getStatus())

        VoucherExpiryJob voucherExpiryJob = new VoucherExpiryJob(cayenneService, accountTransactionService)

        voucherExpiryJob.executeWithDate(new Date())


        Account voucherLiabilityAccount = SelectById.query(Account.class, 8).selectOne(cayenneContext)
        Account vouchersExpiredAccount = SelectById.query(Account.class, 10).selectOne(cayenneContext)

        expiredMoneyVoucher = SelectById.query(Voucher.class, 4).selectOne(cayenneContext)
        expiredCourseVoucher = SelectById.query(Voucher.class, 1).selectOne(cayenneContext)
        unexpiredVoucher = SelectById.query(Voucher.class, 5).selectOne(cayenneContext)

        Assertions.assertEquals(ProductStatus.EXPIRED, expiredMoneyVoucher.getStatus())
        Assertions.assertEquals(ProductStatus.EXPIRED, expiredCourseVoucher.getStatus())
        Assertions.assertEquals(ProductStatus.ACTIVE, unexpiredVoucher.getStatus())

        SelectQuery<AccountTransaction> transactionQuery = SelectQuery.query(AccountTransaction.class)

        // sorting transactions by account id and amount... although this is not a chronological order
        // this is the only way to get predictable transaction order to compare with the expected result
        transactionQuery.addOrdering(AccountTransaction.ACCOUNT.dot(Account.ID).asc())
        transactionQuery.addOrdering(AccountTransaction.AMOUNT.asc())

        List<AccountTransaction> transactions = cayenneContext.select(transactionQuery)

        Assertions.assertEquals(4, transactions.size())

        // voucher for 4 enrolments sold for $10, 2 enrolments are currently redeemed
        Assertions.assertEquals(voucherLiabilityAccount, transactions.get(0).getAccount())
        Assertions.assertEquals(new Money("-5.0"), transactions.get(0).getAmount())
        Assertions.assertEquals(AccountTransactionType.INVOICE_LINE, transactions.get(0).getTableName())

        Assertions.assertEquals(vouchersExpiredAccount, transactions.get(3).getAccount())
        Assertions.assertEquals(new Money("5.0"), transactions.get(3).getAmount())
        Assertions.assertEquals(AccountTransactionType.INVOICE_LINE, transactions.get(3).getTableName())

        // voucher with $200 value sold for $11, $90 is remaining
        Assertions.assertEquals(voucherLiabilityAccount, transactions.get(1).getAccount())
        Assertions.assertEquals(new Money("-4.95"), transactions.get(1).getAmount())
        Assertions.assertEquals(AccountTransactionType.INVOICE_LINE, transactions.get(1).getTableName())

        Assertions.assertEquals(vouchersExpiredAccount, transactions.get(2).getAccount())
        Assertions.assertEquals(new Money("4.95"), transactions.get(2).getAmount())
        Assertions.assertEquals(AccountTransactionType.INVOICE_LINE, transactions.get(2).getTableName())
    }

    
    @Test
    void testVoucherProductPriceChange() {
        Voucher expiredMoneyVoucher = SelectById.query(Voucher.class, 4).selectOne(cayenneContext)
        Voucher expiredCourseVoucher = SelectById.query(Voucher.class, 1).selectOne(cayenneContext)
        Voucher unexpiredVoucher = SelectById.query(Voucher.class, 5).selectOne(cayenneContext)

        Assertions.assertEquals(ProductStatus.ACTIVE, expiredMoneyVoucher.getStatus())
        Assertions.assertEquals(ProductStatus.ACTIVE, expiredCourseVoucher.getStatus())
        Assertions.assertEquals(ProductStatus.ACTIVE, unexpiredVoucher.getStatus())

        // change voucher product price
        expiredMoneyVoucher.getVoucherProduct().setPriceExTax(new Money("1.00"))
        expiredCourseVoucher.getVoucherProduct().setPriceExTax(new Money("1.00"))

        cayenneContext.commitChanges()

        VoucherExpiryJob voucherExpiryJob = new VoucherExpiryJob(cayenneService, accountTransactionService)

        voucherExpiryJob.executeWithDate(new Date())

        Account voucherLiabilityAccount = SelectById.query(Account.class, 8).selectOne(cayenneContext)
        Account vouchersExpiredAccount = SelectById.query(Account.class, 10).selectOne(cayenneContext)

        expiredMoneyVoucher = SelectById.query(Voucher.class, 4).selectOne(cayenneContext)
        expiredCourseVoucher = SelectById.query(Voucher.class, 1).selectOne(cayenneContext)
        unexpiredVoucher = SelectById.query(Voucher.class, 5).selectOne(cayenneContext)

        Assertions.assertEquals(ProductStatus.EXPIRED, expiredMoneyVoucher.getStatus())
        Assertions.assertEquals(ProductStatus.EXPIRED, expiredCourseVoucher.getStatus())
        Assertions.assertEquals(ProductStatus.ACTIVE, unexpiredVoucher.getStatus())

        SelectQuery<AccountTransaction> transactionQuery = SelectQuery.query(AccountTransaction.class)

        // sorting transactions by account id and amount... although this is not a chronological order
        // this is the only way to get predictable transaction order to compare with the expected result
        transactionQuery.addOrdering(AccountTransaction.ACCOUNT.dot(Account.ID).asc())
        transactionQuery.addOrdering(AccountTransaction.AMOUNT.asc())

        List<AccountTransaction> transactions = cayenneContext.select(transactionQuery)

        Assertions.assertEquals(4, transactions.size())

        // voucher for 4 enrolments sold for $10, 2 enrolments are currently redeemed
        Assertions.assertEquals(voucherLiabilityAccount, transactions.get(0).getAccount())
        Assertions.assertEquals(new Money("-5.0"), transactions.get(0).getAmount())
        Assertions.assertEquals(AccountTransactionType.INVOICE_LINE, transactions.get(0).getTableName())

        Assertions.assertEquals(vouchersExpiredAccount, transactions.get(3).getAccount())
        Assertions.assertEquals(new Money("5.0"), transactions.get(3).getAmount())
        Assertions.assertEquals(AccountTransactionType.INVOICE_LINE, transactions.get(3).getTableName())

        // voucher with $200 value sold for $11, $90 is remaining
        Assertions.assertEquals(voucherLiabilityAccount, transactions.get(1).getAccount())
        Assertions.assertEquals(new Money("-4.95"), transactions.get(1).getAmount())
        Assertions.assertEquals(AccountTransactionType.INVOICE_LINE, transactions.get(1).getTableName())

        Assertions.assertEquals(vouchersExpiredAccount, transactions.get(2).getAccount())
        Assertions.assertEquals(new Money("4.95"), transactions.get(2).getAmount())
        Assertions.assertEquals(AccountTransactionType.INVOICE_LINE, transactions.get(2).getTableName())
    }

}
