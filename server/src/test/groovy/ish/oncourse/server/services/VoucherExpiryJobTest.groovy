/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.server.services

import groovy.transform.CompileStatic
import ish.CayenneIshTestCase
import ish.common.types.AccountTransactionType
import ish.common.types.ProductStatus
import ish.math.Money
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.accounting.AccountTransactionService
import ish.oncourse.server.cayenne.Account
import ish.oncourse.server.cayenne.AccountTransaction
import ish.oncourse.server.cayenne.Voucher
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.SelectById
import org.apache.cayenne.query.SelectQuery
import org.apache.commons.lang3.time.DateUtils
import org.dbunit.dataset.ReplacementDataSet
import org.dbunit.dataset.xml.FlatXmlDataSet
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import static org.junit.Assert.assertEquals

@CompileStatic
class VoucherExpiryJobTest extends CayenneIshTestCase {
	
	private ICayenneService cayenneService
	private AccountTransactionService accountTransactionService

	@BeforeEach
	void setup() throws Exception {
		wipeTables()

		this.cayenneService = injector.getInstance(ICayenneService.class)
		this.accountTransactionService = injector.getInstance(AccountTransactionService.class)

		InputStream st = VoucherExpiryJobTest.class.getClassLoader().getResourceAsStream(
				"ish/oncourse/server/services/voucherExpiryJobTestDataSet.xml")
		FlatXmlDataSetBuilder builder = new FlatXmlDataSetBuilder()
		builder.setColumnSensing(true)

		FlatXmlDataSet dataSet = builder.build(st)
		ReplacementDataSet rDataSet = new ReplacementDataSet(dataSet)
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

		executeDatabaseOperation(rDataSet)
		super.setup()
	}
	
	@Test
	void testVoucherExpiry() {
		ObjectContext context = cayenneService.getNewContext()
		
		Voucher expiredMoneyVoucher = SelectById.query(Voucher.class, 4).selectOne(context)
		Voucher expiredCourseVoucher = SelectById.query(Voucher.class, 1).selectOne(context)
		Voucher unexpiredVoucher = SelectById.query(Voucher.class, 5).selectOne(context)

		assertEquals(ProductStatus.ACTIVE, expiredMoneyVoucher.getStatus())
		assertEquals(ProductStatus.ACTIVE, expiredCourseVoucher.getStatus())
		assertEquals(ProductStatus.ACTIVE, unexpiredVoucher.getStatus())

		VoucherExpiryJob voucherExpiryJob = new VoucherExpiryJob(cayenneService, accountTransactionService)

		voucherExpiryJob.executeWithDate(new Date())

		//create new context to avoid data cache 
		context = cayenneService.getNewContext()

		Account voucherLiabilityAccount = SelectById.query(Account.class, 8).selectOne(context)
		Account vouchersExpiredAccount = SelectById.query(Account.class, 10).selectOne(context)

		expiredMoneyVoucher = SelectById.query(Voucher.class, 4).selectOne(context)
		expiredCourseVoucher = SelectById.query(Voucher.class, 1).selectOne(context)
		unexpiredVoucher = SelectById.query(Voucher.class, 5).selectOne(context)

		assertEquals(ProductStatus.EXPIRED, expiredMoneyVoucher.getStatus())
		assertEquals(ProductStatus.EXPIRED, expiredCourseVoucher.getStatus())
		assertEquals(ProductStatus.ACTIVE, unexpiredVoucher.getStatus())

		SelectQuery<AccountTransaction> transactionQuery = SelectQuery.query(AccountTransaction.class)

		// sorting transactions by account id and amount... although this is not a chronological order
		// this is the only way to get predictable transaction order to compare with the expected result
		transactionQuery.addOrdering(AccountTransaction.ACCOUNT.dot(Account.ID).asc())
		transactionQuery.addOrdering(AccountTransaction.AMOUNT.asc())

		List<AccountTransaction> transactions = context.select(transactionQuery)

		assertEquals(4, transactions.size())

		// voucher for 4 enrolments sold for $10, 2 enrolments are currently redeemed
		assertEquals(voucherLiabilityAccount, transactions.get(0).getAccount())
		assertEquals(new Money("-5.0"), transactions.get(0).getAmount())
		assertEquals(AccountTransactionType.INVOICE_LINE, transactions.get(0).getTableName())

		assertEquals(vouchersExpiredAccount, transactions.get(3).getAccount())
		assertEquals(new Money("5.0"), transactions.get(3).getAmount())
		assertEquals(AccountTransactionType.INVOICE_LINE, transactions.get(3).getTableName())

		// voucher with $200 value sold for $11, $90 is remaining
		assertEquals(voucherLiabilityAccount, transactions.get(1).getAccount())
		assertEquals(new Money("-4.95"), transactions.get(1).getAmount())
		assertEquals(AccountTransactionType.INVOICE_LINE, transactions.get(1).getTableName())

		assertEquals(vouchersExpiredAccount, transactions.get(2).getAccount())
		assertEquals(new Money("4.95"), transactions.get(2).getAmount())
		assertEquals(AccountTransactionType.INVOICE_LINE, transactions.get(2).getTableName())
	}

	@Test
	void testVoucherProductPriceChange() {
		ObjectContext context = cayenneService.getNewContext()

		Voucher expiredMoneyVoucher = SelectById.query(Voucher.class, 4).selectOne(context)
		Voucher expiredCourseVoucher = SelectById.query(Voucher.class, 1).selectOne(context)
		Voucher unexpiredVoucher = SelectById.query(Voucher.class, 5).selectOne(context)

		assertEquals(ProductStatus.ACTIVE, expiredMoneyVoucher.getStatus())
		assertEquals(ProductStatus.ACTIVE, expiredCourseVoucher.getStatus())
		assertEquals(ProductStatus.ACTIVE, unexpiredVoucher.getStatus())

		// change voucher product price
		expiredMoneyVoucher.getVoucherProduct().setPriceExTax(new Money("1.00"))
		expiredCourseVoucher.getVoucherProduct().setPriceExTax(new Money("1.00"))

		context.commitChanges()

		VoucherExpiryJob voucherExpiryJob = new VoucherExpiryJob(cayenneService, accountTransactionService)

		voucherExpiryJob.executeWithDate(new Date())

		//create new context to avoid data cache
		context = cayenneService.getNewContext()

		Account voucherLiabilityAccount = SelectById.query(Account.class, 8).selectOne(context)
		Account vouchersExpiredAccount = SelectById.query(Account.class, 10).selectOne(context)

		expiredMoneyVoucher = SelectById.query(Voucher.class, 4).selectOne(context)
		expiredCourseVoucher = SelectById.query(Voucher.class, 1).selectOne(context)
		unexpiredVoucher = SelectById.query(Voucher.class, 5).selectOne(context)

		assertEquals(ProductStatus.EXPIRED, expiredMoneyVoucher.getStatus())
		assertEquals(ProductStatus.EXPIRED, expiredCourseVoucher.getStatus())
		assertEquals(ProductStatus.ACTIVE, unexpiredVoucher.getStatus())

		SelectQuery<AccountTransaction> transactionQuery = SelectQuery.query(AccountTransaction.class)

		// sorting transactions by account id and amount... although this is not a chronological order
		// this is the only way to get predictable transaction order to compare with the expected result
		transactionQuery.addOrdering(AccountTransaction.ACCOUNT.dot(Account.ID).asc())
		transactionQuery.addOrdering(AccountTransaction.AMOUNT.asc())

		List<AccountTransaction> transactions = context.select(transactionQuery)

		assertEquals(4, transactions.size())

		// voucher for 4 enrolments sold for $10, 2 enrolments are currently redeemed
		assertEquals(voucherLiabilityAccount, transactions.get(0).getAccount())
		assertEquals(new Money("-5.0"), transactions.get(0).getAmount())
		assertEquals(AccountTransactionType.INVOICE_LINE, transactions.get(0).getTableName())

		assertEquals(vouchersExpiredAccount, transactions.get(3).getAccount())
		assertEquals(new Money("5.0"), transactions.get(3).getAmount())
		assertEquals(AccountTransactionType.INVOICE_LINE, transactions.get(3).getTableName())

		// voucher with $200 value sold for $11, $90 is remaining
		assertEquals(voucherLiabilityAccount, transactions.get(1).getAccount())
		assertEquals(new Money("-4.95"), transactions.get(1).getAmount())
		assertEquals(AccountTransactionType.INVOICE_LINE, transactions.get(1).getTableName())

		assertEquals(vouchersExpiredAccount, transactions.get(2).getAccount())
		assertEquals(new Money("4.95"), transactions.get(2).getAmount())
		assertEquals(AccountTransactionType.INVOICE_LINE, transactions.get(2).getTableName())
	}

}
