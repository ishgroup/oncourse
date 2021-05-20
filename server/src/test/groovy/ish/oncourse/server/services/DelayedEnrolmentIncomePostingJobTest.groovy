/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.server.services

import groovy.transform.CompileStatic
import ish.TestWithDatabase
import ish.DatabaseSetup
import ish.common.types.AccountTransactionType
import ish.common.types.PaymentSource
import ish.math.Money
import ish.oncourse.server.PreferenceController
import ish.oncourse.server.accounting.AccountTransactionService
import ish.oncourse.server.cayenne.*
import ish.oncourse.server.cayenne.glue._AccountTransaction
import ish.persistence.Preferences
import org.apache.cayenne.access.DataContext
import org.apache.cayenne.exp.Expression
import org.apache.cayenne.exp.ExpressionFactory
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.SelectById
import org.apache.cayenne.query.SelectQuery
import org.apache.commons.lang3.time.DateUtils
import org.dbunit.dataset.ReplacementDataSet
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.quartz.JobExecutionException

import static org.junit.jupiter.api.Assertions.fail

@CompileStatic
@DatabaseSetup(value = "ish/oncourse/server/services/delayedEnrolmentIncomePostingJobTestDataSet.xml")
class DelayedEnrolmentIncomePostingJobTest extends TestWithDatabase {
    // creating date, the object cannot be exactly the same as system time to allow safe comparison of time by delayed Income posting job
    Date date = DateUtils.addHours(DateUtils.truncate(new Date(), Calendar.DATE), 12)
    Date start1 = DateUtils.addDays(date, -4)
    Date start2 = DateUtils.addDays(date, -2)
    Date start3 = DateUtils.addDays(date, 2)
    Date start4 = DateUtils.addDays(date, 4)

    @Override
    protected void dataSourceReplaceValues(ReplacementDataSet rDataSet) {
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
	void testGetListOfInvoiceLinesToProcess() {
        DelayedEnrolmentIncomePostingJob delayedEnrolmentIncomePostingJob = new DelayedEnrolmentIncomePostingJob(cayenneService, injector.getInstance(AccountTransactionService.class))

        List<InvoiceLine> list = delayedEnrolmentIncomePostingJob.getListOfInvoiceLinesToProcess()
        Assertions.assertEquals(13, list.size())
    }

	
	@Test
	void testProcessInvoiceLine0() {
        DelayedEnrolmentIncomePostingJob delayedEnrolmentIncomePostingJob = new DelayedEnrolmentIncomePostingJob(cayenneService, injector.getInstance(AccountTransactionService.class))

        PreferenceController.getController().setAccountPrepaidFeesPostAt(null)
        delayedEnrolmentIncomePostingJob.setDelayedIncomePreference(null)

        try {
            delayedEnrolmentIncomePostingJob.executeWithDate(new Date())
        } catch (Exception e) {
            fail(e.getMessage())
        }
        Assertions.assertEquals(Preferences.ACCOUNT_PREPAID_FEES_POST_AT_EVERY_SESSION, delayedEnrolmentIncomePostingJob.getDelayedIncomePreference())
    }

	
	@Test
	void testGetPercentageOfDeliveredScheduledHours() {
        cayenneContext.deleteObjects(cayenneContext.select(SelectQuery.query(AccountTransaction.class)))
        cayenneContext.deleteObjects(cayenneContext.select(SelectQuery.query(Outcome.class)))
        cayenneContext.deleteObjects(cayenneContext.select(SelectQuery.query(Enrolment.class)))
        cayenneContext.commitChanges()

        CourseClass cc1 = getRecordWithId(cayenneContext, CourseClass.class, 100L)
        CourseClass cc2 = getRecordWithId(cayenneContext, CourseClass.class, 200L)
        CourseClass cc3 = getRecordWithId(cayenneContext, CourseClass.class, 300L)
        CourseClass cc4 = getRecordWithId(cayenneContext, CourseClass.class, 400L)

        Assertions.assertEquals(new BigDecimal("0.00"), cc1.getPercentageOfDeliveredScheduledHoursBeforeDate(DateUtils.addHours(start1, -1)))
        Assertions.assertEquals(new BigDecimal("0.00"), cc2.getPercentageOfDeliveredScheduledHoursBeforeDate(DateUtils.addHours(start1, -1)))
        Assertions.assertEquals(new BigDecimal("0.00"), cc3.getPercentageOfDeliveredScheduledHoursBeforeDate(DateUtils.addHours(start1, -1)))
        Assertions.assertEquals(new BigDecimal("0.00"), cc4.getPercentageOfDeliveredScheduledHoursBeforeDate(DateUtils.addHours(start1, -1)))

        Assertions.assertEquals(new BigDecimal("0.33"), cc1.getPercentageOfDeliveredScheduledHoursBeforeDate(DateUtils.addHours(start1, 1)))
        Assertions.assertEquals(new BigDecimal("0.00"), cc2.getPercentageOfDeliveredScheduledHoursBeforeDate(DateUtils.addHours(start1, 1)))
        Assertions.assertEquals(new BigDecimal("0.50"), cc3.getPercentageOfDeliveredScheduledHoursBeforeDate(DateUtils.addHours(start1, 1)))
        Assertions.assertEquals(new BigDecimal("0.00"), cc4.getPercentageOfDeliveredScheduledHoursBeforeDate(DateUtils.addHours(start1, 1)))

        Assertions.assertEquals(new BigDecimal("0.33"), cc1.getPercentageOfDeliveredScheduledHoursBeforeDate(DateUtils.addHours(start2, -1)))
        Assertions.assertEquals(new BigDecimal("0.00"), cc2.getPercentageOfDeliveredScheduledHoursBeforeDate(DateUtils.addHours(start2, -1)))
        Assertions.assertEquals(new BigDecimal("0.50"), cc3.getPercentageOfDeliveredScheduledHoursBeforeDate(DateUtils.addHours(start2, -1)))
        Assertions.assertEquals(new BigDecimal("0.00"), cc4.getPercentageOfDeliveredScheduledHoursBeforeDate(DateUtils.addHours(start2, -1)))

        Assertions.assertEquals(new BigDecimal("0.67"), cc1.getPercentageOfDeliveredScheduledHoursBeforeDate(DateUtils.addHours(start2, 1)))
        Assertions.assertEquals(new BigDecimal("0.33"), cc2.getPercentageOfDeliveredScheduledHoursBeforeDate(DateUtils.addHours(start2, 1)))
        Assertions.assertEquals(new BigDecimal("1.00"), cc3.getPercentageOfDeliveredScheduledHoursBeforeDate(DateUtils.addHours(start2, 1)))
        Assertions.assertEquals(new BigDecimal("0.00"), cc4.getPercentageOfDeliveredScheduledHoursBeforeDate(DateUtils.addHours(start2, 1)))

        Assertions.assertEquals(new BigDecimal("0.67"), cc1.getPercentageOfDeliveredScheduledHoursBeforeDate(DateUtils.addHours(start3, -1)))
        Assertions.assertEquals(new BigDecimal("0.33"), cc2.getPercentageOfDeliveredScheduledHoursBeforeDate(DateUtils.addHours(start3, -1)))
        Assertions.assertEquals(new BigDecimal("1.00"), cc3.getPercentageOfDeliveredScheduledHoursBeforeDate(DateUtils.addHours(start3, -1)))
        Assertions.assertEquals(new BigDecimal("0.00"), cc4.getPercentageOfDeliveredScheduledHoursBeforeDate(DateUtils.addHours(start3, -1)))

        Assertions.assertEquals(new BigDecimal("1.00"), cc1.getPercentageOfDeliveredScheduledHoursBeforeDate(DateUtils.addHours(start3, 1)))
        Assertions.assertEquals(new BigDecimal("0.67"), cc2.getPercentageOfDeliveredScheduledHoursBeforeDate(DateUtils.addHours(start3, 1)))
        Assertions.assertEquals(new BigDecimal("1.00"), cc3.getPercentageOfDeliveredScheduledHoursBeforeDate(DateUtils.addHours(start3, 1)))
        Assertions.assertEquals(new BigDecimal("0.50"), cc4.getPercentageOfDeliveredScheduledHoursBeforeDate(DateUtils.addHours(start3, 1)))

        Assertions.assertEquals(new BigDecimal("1.00"), cc1.getPercentageOfDeliveredScheduledHoursBeforeDate(DateUtils.addHours(start4, -1)))
        Assertions.assertEquals(new BigDecimal("0.67"), cc2.getPercentageOfDeliveredScheduledHoursBeforeDate(DateUtils.addHours(start4, -1)))
        Assertions.assertEquals(new BigDecimal("1.00"), cc3.getPercentageOfDeliveredScheduledHoursBeforeDate(DateUtils.addHours(start4, -1)))
        Assertions.assertEquals(new BigDecimal("0.50"), cc4.getPercentageOfDeliveredScheduledHoursBeforeDate(DateUtils.addHours(start4, -1)))

        Assertions.assertEquals(new BigDecimal("1.00"), cc1.getPercentageOfDeliveredScheduledHoursBeforeDate(DateUtils.addHours(start4, 1)))
        Assertions.assertEquals(new BigDecimal("1.00"), cc2.getPercentageOfDeliveredScheduledHoursBeforeDate(DateUtils.addHours(start4, 1)))
        Assertions.assertEquals(new BigDecimal("1.00"), cc3.getPercentageOfDeliveredScheduledHoursBeforeDate(DateUtils.addHours(start4, 1)))
        Assertions.assertEquals(new BigDecimal("1.00"), cc4.getPercentageOfDeliveredScheduledHoursBeforeDate(DateUtils.addHours(start4, 1)))

        Assertions.assertEquals(new BigDecimal("0.67"), cc1.getPercentageOfDeliveredScheduledHoursBeforeDate(new Date()))
        Assertions.assertEquals(new BigDecimal("0.33"), cc2.getPercentageOfDeliveredScheduledHoursBeforeDate(new Date()))
        Assertions.assertEquals(new BigDecimal("1.00"), cc3.getPercentageOfDeliveredScheduledHoursBeforeDate(new Date()))
        Assertions.assertEquals(new BigDecimal("0.00"), cc4.getPercentageOfDeliveredScheduledHoursBeforeDate(new Date()))
    }

	
	@Test
	void testProcessInvoiceLine1() {
        DelayedEnrolmentIncomePostingJob delayedEnrolmentIncomePostingJob = new DelayedEnrolmentIncomePostingJob(cayenneService, injector.getInstance(AccountTransactionService.class))

        delayedEnrolmentIncomePostingJob.setDelayedIncomePreference(Preferences.ACCOUNT_PREPAID_FEES_POST_AT_EVERY_SESSION)

        InvoiceLine invoiceLine = getRecordWithId(cayenneContext, InvoiceLine.class, 800L)
        invoiceLine.setModifiedOn(new Date())
        cayenneContext.commitChanges()

        delayedEnrolmentIncomePostingJob.processInvoiceLine(invoiceLine, new Date())

        AccountTransaction at = getLastAccountTransactionForInvoiceLineWithId(cayenneContext, 800L, getRecordWithId(cayenneContext, Account.class, 200L))
        Assertions.assertEquals(new Money("33.00"), at.getAmount())
        at = getLastAccountTransactionForInvoiceLineWithId(cayenneContext, 800L, getRecordWithId(cayenneContext, Account.class, 500L))
        Assertions.assertEquals(new Money("-33.00"), at.getAmount())

        invoiceLine = ObjectSelect.query(InvoiceLine.class).where(InvoiceLine.ID.eq(800L)).selectFirst(cayenneContext)
        Assertions.assertEquals(new Money("67.0"), invoiceLine.getPrepaidFeesRemaining())
    }

	
	@Test
	void testProcessInvoiceLine2() {
        DelayedEnrolmentIncomePostingJob delayedEnrolmentIncomePostingJob = new DelayedEnrolmentIncomePostingJob(cayenneService, injector.getInstance(AccountTransactionService.class))

        delayedEnrolmentIncomePostingJob.setDelayedIncomePreference(Preferences.ACCOUNT_PREPAID_FEES_POST_AT_FIRST_SESSION)

        InvoiceLine invoiceLine = getRecordWithId(cayenneContext, InvoiceLine.class, 800L)
        invoiceLine.setModifiedOn(new Date())
        cayenneContext.commitChanges()

        delayedEnrolmentIncomePostingJob.processInvoiceLine(invoiceLine, new Date())

        AccountTransaction at = getLastAccountTransactionForInvoiceLineWithId(cayenneContext, 800L, getRecordWithId(cayenneContext, Account.class, 200L))
        Assertions.assertEquals(new Money("100.00"), at.getAmount())
        at = getLastAccountTransactionForInvoiceLineWithId(cayenneContext, 800L, getRecordWithId(cayenneContext, Account.class, 500L))
        Assertions.assertEquals(new Money("-100.00"), at.getAmount())

        Assertions.assertEquals(Money.ZERO, invoiceLine.getPrepaidFeesRemaining())
        List<AccountTransaction> att = cayenneContext.select(SelectQuery.query(AccountTransaction.class))
    }

	
	@Test
	void testPostAtFirstSessionNoSessionsStarted() {
        DelayedEnrolmentIncomePostingJob delayedEnrolmentIncomePostingJob = new DelayedEnrolmentIncomePostingJob(cayenneService, injector.getInstance(AccountTransactionService.class))

        PreferenceController.getController().setAccountPrepaidFeesPostAt(Preferences.ACCOUNT_PREPAID_FEES_POST_AT_FIRST_SESSION)

        try {
            delayedEnrolmentIncomePostingJob.executeWithDate(DateUtils.addHours(start1, -1))
        } catch (JobExecutionException e) {
            fail(e.getMessage())
        }
        cayenneContext.deleteObjects(cayenneContext.select(SelectQuery.query(AccountTransaction.class)))
        cayenneContext.deleteObjects(cayenneContext.select(SelectQuery.query(Outcome.class)))
        cayenneContext.deleteObjects(cayenneContext.select(SelectQuery.query(Enrolment.class)))

        List<AccountTransaction> at = getAccountTransactionsForInvoiceLineWithId(cayenneContext, 100L, getRecordWithId(cayenneContext, Account.class, 200L))
        Assertions.assertTrue(at.isEmpty())
        at = getAccountTransactionsForInvoiceLineWithId(cayenneContext, 100L, getRecordWithId(cayenneContext, Account.class, 500L))
        Assertions.assertTrue(at.isEmpty())
        InvoiceLine invoiceLine = SelectById.query(InvoiceLine.class, 100).selectOne(cayenneContext)
        Assertions.assertEquals(Money.ZERO, invoiceLine.getPrepaidFeesRemaining())

        at = getAccountTransactionsForInvoiceLineWithId(cayenneContext, 200L, getRecordWithId(cayenneContext, Account.class, 200L))
        Assertions.assertTrue(at.isEmpty())
        at = getAccountTransactionsForInvoiceLineWithId(cayenneContext, 200L, getRecordWithId(cayenneContext, Account.class, 500L))
        Assertions.assertEquals(2, at.size())
        invoiceLine = SelectById.query(InvoiceLine.class, 200).selectOne(cayenneContext)
        Assertions.assertEquals(new Money("33.0"), invoiceLine.getPrepaidFeesRemaining())

        at = getAccountTransactionsForInvoiceLineWithId(cayenneContext, 300L, getRecordWithId(cayenneContext, Account.class, 200L))
        Assertions.assertTrue(at.isEmpty())
        at = getAccountTransactionsForInvoiceLineWithId(cayenneContext, 300L, getRecordWithId(cayenneContext, Account.class, 500L))
        Assertions.assertTrue(at.isEmpty())
        invoiceLine = SelectById.query(InvoiceLine.class, 300).selectOne(cayenneContext)
        Assertions.assertEquals(Money.ZERO, invoiceLine.getPrepaidFeesRemaining())

        at = getAccountTransactionsForInvoiceLineWithId(cayenneContext, 400L, getRecordWithId(cayenneContext, Account.class, 200L))
        Assertions.assertTrue(at.isEmpty())
        at = getAccountTransactionsForInvoiceLineWithId(cayenneContext, 400L, getRecordWithId(cayenneContext, Account.class, 500L))
        Assertions.assertEquals(1, at.size())
        invoiceLine = SelectById.query(InvoiceLine.class, 400).selectOne(cayenneContext)
        Assertions.assertEquals(new Money("100.0"), invoiceLine.getPrepaidFeesRemaining())

        at = getAccountTransactionsForInvoiceLineWithId(cayenneContext, 500L, getRecordWithId(cayenneContext, Account.class, 200L))
        Assertions.assertTrue(at.isEmpty())
        at = getAccountTransactionsForInvoiceLineWithId(cayenneContext, 500L, getRecordWithId(cayenneContext, Account.class, 500L))
        Assertions.assertEquals(2, at.size())
        invoiceLine = SelectById.query(InvoiceLine.class, 500).selectOne(cayenneContext)
        Assertions.assertEquals(new Money("67.0"), invoiceLine.getPrepaidFeesRemaining())

        at = getAccountTransactionsForInvoiceLineWithId(cayenneContext, 600L, getRecordWithId(cayenneContext, Account.class, 200L))
        Assertions.assertTrue(at.isEmpty())
        at = getAccountTransactionsForInvoiceLineWithId(cayenneContext, 600L, getRecordWithId(cayenneContext, Account.class, 500L))
        Assertions.assertEquals(2, at.size())
        invoiceLine = SelectById.query(InvoiceLine.class, 600).selectOne(cayenneContext)
        Assertions.assertEquals(new Money("10.0"), invoiceLine.getPrepaidFeesRemaining())

        at = getAccountTransactionsForInvoiceLineWithId(cayenneContext, 700L, getRecordWithId(cayenneContext, Account.class, 200L))
        Assertions.assertTrue(at.isEmpty())
        at = getAccountTransactionsForInvoiceLineWithId(cayenneContext, 700L, getRecordWithId(cayenneContext, Account.class, 500L))
        Assertions.assertEquals(2, at.size())
        invoiceLine = SelectById.query(InvoiceLine.class, 700).selectOne(cayenneContext)
        Assertions.assertEquals(new Money("10.0"), invoiceLine.getPrepaidFeesRemaining())

        at = getAccountTransactionsForInvoiceLineWithId(cayenneContext, 800L, getRecordWithId(cayenneContext, Account.class, 200L))
        Assertions.assertTrue(at.isEmpty())
        at = getAccountTransactionsForInvoiceLineWithId(cayenneContext, 800L, getRecordWithId(cayenneContext, Account.class, 500L))
        Assertions.assertEquals(1, at.size())
        invoiceLine = SelectById.query(InvoiceLine.class, 800).selectOne(cayenneContext)
        Assertions.assertEquals(new Money("100.0"), invoiceLine.getPrepaidFeesRemaining())

        at = getAccountTransactionsForInvoiceLineWithId(cayenneContext, 900L, getRecordWithId(cayenneContext, Account.class, 200L))
        Assertions.assertTrue(at.isEmpty())
        at = getAccountTransactionsForInvoiceLineWithId(cayenneContext, 900L, getRecordWithId(cayenneContext, Account.class, 500L))
        Assertions.assertEquals(1, at.size())
        invoiceLine = SelectById.query(InvoiceLine.class, 900).selectOne(cayenneContext)
        Assertions.assertEquals(new Money("80.0"), invoiceLine.getPrepaidFeesRemaining())

        at = getAccountTransactionsForInvoiceLineWithId(cayenneContext, 1700L, getRecordWithId(cayenneContext, Account.class, 200L))
        Assertions.assertTrue(at.isEmpty())
        at = getAccountTransactionsForInvoiceLineWithId(cayenneContext, 1700L, getRecordWithId(cayenneContext, Account.class, 500L))
        Assertions.assertEquals(1, at.size())
        invoiceLine = SelectById.query(InvoiceLine.class, 1700).selectOne(cayenneContext)
        Assertions.assertEquals(new Money("100.0"), invoiceLine.getPrepaidFeesRemaining())

        at = getAccountTransactionsForInvoiceLineWithId(cayenneContext, 1800L, getRecordWithId(cayenneContext, Account.class, 200L))
        Assertions.assertTrue(at.isEmpty())
        at = getAccountTransactionsForInvoiceLineWithId(cayenneContext, 1800L, getRecordWithId(cayenneContext, Account.class, 500L))
        Assertions.assertEquals(1, at.size())
        invoiceLine = SelectById.query(InvoiceLine.class, 1800).selectOne(cayenneContext)
        Assertions.assertEquals(new Money("-100.0"), invoiceLine.getPrepaidFeesRemaining())
    }

	
	@Test
	void testPostAtFirstSessionSomeSessionsStarted() {
        DelayedEnrolmentIncomePostingJob delayedEnrolmentIncomePostingJob = new DelayedEnrolmentIncomePostingJob(cayenneService, injector.getInstance(AccountTransactionService.class))

        PreferenceController.getController().setAccountPrepaidFeesPostAt(Preferences.ACCOUNT_PREPAID_FEES_POST_AT_FIRST_SESSION)

        try {
            delayedEnrolmentIncomePostingJob.executeWithDate(new Date())
        } catch (JobExecutionException e) {
            fail(e.getMessage())
        }

        cayenneContext.deleteObjects(cayenneContext.select(SelectQuery.query(AccountTransaction.class)))
        cayenneContext.deleteObjects(cayenneContext.select(SelectQuery.query(Outcome.class)))
        cayenneContext.deleteObjects(cayenneContext.select(SelectQuery.query(Enrolment.class)))

        List<AccountTransaction> at = getAccountTransactionsForInvoiceLineWithId(cayenneContext, 100L, getRecordWithId(cayenneContext, Account.class, 200L))
        Assertions.assertTrue(at.isEmpty())
        at = getAccountTransactionsForInvoiceLineWithId(cayenneContext, 100L, getRecordWithId(cayenneContext, Account.class, 500L))
        Assertions.assertTrue(at.isEmpty())
        InvoiceLine invoiceLine = SelectById.query(InvoiceLine.class, 100).selectOne(cayenneContext)
        Assertions.assertEquals(Money.ZERO, invoiceLine.getPrepaidFeesRemaining())

        at = getAccountTransactionsForInvoiceLineWithId(cayenneContext, 200L, getRecordWithId(cayenneContext, Account.class, 200L))
        Assertions.assertEquals(new Money("33.00"), at.get(0).getAmount())
        at = getAccountTransactionsForInvoiceLineWithId(cayenneContext, 200L, getRecordWithId(cayenneContext, Account.class, 500L))
        Assertions.assertEquals(new Money("-33.00"), at.get(0).getAmount())
        invoiceLine = SelectById.query(InvoiceLine.class, 200).selectOne(cayenneContext)
        Assertions.assertEquals(Money.ZERO, invoiceLine.getPrepaidFeesRemaining())

        at = getAccountTransactionsForInvoiceLineWithId(cayenneContext, 300L, getRecordWithId(cayenneContext, Account.class, 200L))
        Assertions.assertTrue(at.isEmpty())
        at = getAccountTransactionsForInvoiceLineWithId(cayenneContext, 300L, getRecordWithId(cayenneContext, Account.class, 500L))
        Assertions.assertTrue(at.isEmpty())
        invoiceLine = SelectById.query(InvoiceLine.class, 300).selectOne(cayenneContext)
        Assertions.assertEquals(Money.ZERO, invoiceLine.getPrepaidFeesRemaining())

        at = getAccountTransactionsForInvoiceLineWithId(cayenneContext, 400L, getRecordWithId(cayenneContext, Account.class, 200L))
        Assertions.assertEquals(new Money("100.00"), at.get(0).getAmount())
        at = getAccountTransactionsForInvoiceLineWithId(cayenneContext, 400L, getRecordWithId(cayenneContext, Account.class, 500L))
        Assertions.assertEquals(new Money("-100.00"), at.get(0).getAmount())
        invoiceLine = SelectById.query(InvoiceLine.class, 400).selectOne(cayenneContext)
        Assertions.assertEquals(Money.ZERO, invoiceLine.getPrepaidFeesRemaining())

        at = getAccountTransactionsForInvoiceLineWithId(cayenneContext, 500L, getRecordWithId(cayenneContext, Account.class, 200L))
        Assertions.assertEquals(new Money("67.00"), at.get(0).getAmount())
        at = getAccountTransactionsForInvoiceLineWithId(cayenneContext, 500L, getRecordWithId(cayenneContext, Account.class, 500L))
        Assertions.assertEquals(new Money("-67.00"), at.get(0).getAmount())
        invoiceLine = SelectById.query(InvoiceLine.class, 500).selectOne(cayenneContext)
        Assertions.assertEquals(Money.ZERO, invoiceLine.getPrepaidFeesRemaining())

        at = getAccountTransactionsForInvoiceLineWithId(cayenneContext, 600L, getRecordWithId(cayenneContext, Account.class, 200L))
        Assertions.assertEquals(new Money("10.00"), at.get(0).getAmount())
        at = getAccountTransactionsForInvoiceLineWithId(cayenneContext, 600L, getRecordWithId(cayenneContext, Account.class, 500L))
        Assertions.assertEquals(new Money("-10.00"), at.get(0).getAmount())
        invoiceLine = SelectById.query(InvoiceLine.class, 600).selectOne(cayenneContext)
        Assertions.assertEquals(Money.ZERO, invoiceLine.getPrepaidFeesRemaining())

        at = getAccountTransactionsForInvoiceLineWithId(cayenneContext, 700L, getRecordWithId(cayenneContext, Account.class, 200L))
        Assertions.assertTrue(at.isEmpty())
        at = getAccountTransactionsForInvoiceLineWithId(cayenneContext, 700L, getRecordWithId(cayenneContext, Account.class, 500L))
        Assertions.assertEquals(2, at.size())
        invoiceLine = SelectById.query(InvoiceLine.class, 700).selectOne(cayenneContext)
        Assertions.assertEquals(new Money("10.0"), invoiceLine.getPrepaidFeesRemaining())

        at = getAccountTransactionsForInvoiceLineWithId(cayenneContext, 800L, getRecordWithId(cayenneContext, Account.class, 200L))
        Assertions.assertEquals(new Money("100.00"), at.get(0).getAmount())
        at = getAccountTransactionsForInvoiceLineWithId(cayenneContext, 800L, getRecordWithId(cayenneContext, Account.class, 500L))
        Assertions.assertEquals(new Money("-100.00"), at.get(0).getAmount())
        invoiceLine = SelectById.query(InvoiceLine.class, 800).selectOne(cayenneContext)
        Assertions.assertEquals(Money.ZERO, invoiceLine.getPrepaidFeesRemaining())

        at = getAccountTransactionsForInvoiceLineWithId(cayenneContext, 900L, getRecordWithId(cayenneContext, Account.class, 200L))
        Assertions.assertEquals(new Money("80.00"), at.get(0).getAmount())
        at = getAccountTransactionsForInvoiceLineWithId(cayenneContext, 900L, getRecordWithId(cayenneContext, Account.class, 500L))
        Assertions.assertEquals(new Money("-80.00"), at.get(0).getAmount())
        invoiceLine = SelectById.query(InvoiceLine.class, 900).selectOne(cayenneContext)
        Assertions.assertEquals(Money.ZERO, invoiceLine.getPrepaidFeesRemaining())

        at = getAccountTransactionsForInvoiceLineWithId(cayenneContext, 1700L, getRecordWithId(cayenneContext, Account.class, 200L))
        Assertions.assertEquals(new Money("100.00"), at.get(0).getAmount())
        at = getAccountTransactionsForInvoiceLineWithId(cayenneContext, 1700L, getRecordWithId(cayenneContext, Account.class, 500L))
        Assertions.assertEquals(new Money("-100.00"), at.get(0).getAmount())
        invoiceLine = SelectById.query(InvoiceLine.class, 1700).selectOne(cayenneContext)
        Assertions.assertEquals(Money.ZERO, invoiceLine.getPrepaidFeesRemaining())

        at = getAccountTransactionsForInvoiceLineWithId(cayenneContext, 1800L, getRecordWithId(cayenneContext, Account.class, 200L))
        Assertions.assertEquals(new Money("-100.00"), at.get(0).getAmount())
        at = getAccountTransactionsForInvoiceLineWithId(cayenneContext, 1800L, getRecordWithId(cayenneContext, Account.class, 500L))
        Assertions.assertEquals(new Money("100.00"), at.get(0).getAmount())
        invoiceLine = SelectById.query(InvoiceLine.class, 1800).selectOne(cayenneContext)
        Assertions.assertEquals(Money.ZERO, invoiceLine.getPrepaidFeesRemaining())
    }

	
	@Test
	void testPostAtFirstSessionAllSessionsStarted() {
        DelayedEnrolmentIncomePostingJob delayedEnrolmentIncomePostingJob = new DelayedEnrolmentIncomePostingJob(cayenneService, injector.getInstance(AccountTransactionService.class))

        PreferenceController.getController().setAccountPrepaidFeesPostAt(Preferences.ACCOUNT_PREPAID_FEES_POST_AT_FIRST_SESSION)

        try {
            delayedEnrolmentIncomePostingJob.executeWithDate(DateUtils.addHours(start4, 1))
        } catch (JobExecutionException e) {
            fail(e.getMessage())
        }

        cayenneContext.deleteObjects(cayenneContext.select(SelectQuery.query(AccountTransaction.class)))
        cayenneContext.deleteObjects(cayenneContext.select(SelectQuery.query(Outcome.class)))
        cayenneContext.deleteObjects(cayenneContext.select(SelectQuery.query(Enrolment.class)))

        AccountTransaction at = getLastAccountTransactionForInvoiceLineWithId(cayenneContext, 100L, getRecordWithId(cayenneContext, Account.class, 200L))
        Assertions.assertNull(at)
        at = getLastAccountTransactionForInvoiceLineWithId(cayenneContext, 100L, getRecordWithId(cayenneContext, Account.class, 500L))
        Assertions.assertNull(at)
        InvoiceLine invoiceLine = SelectById.query(InvoiceLine.class, 100).selectOne(cayenneContext)
        Assertions.assertEquals(Money.ZERO, invoiceLine.getPrepaidFeesRemaining())

        at = getLastAccountTransactionForInvoiceLineWithId(cayenneContext, 200L, getRecordWithId(cayenneContext, Account.class, 200L))
        Assertions.assertEquals(new Money("33.00"), at.getAmount())
        at = getLastAccountTransactionForInvoiceLineWithId(cayenneContext, 200L, getRecordWithId(cayenneContext, Account.class, 500L))
        Assertions.assertEquals(new Money("-33.00"), at.getAmount())
        invoiceLine = SelectById.query(InvoiceLine.class, 200).selectOne(cayenneContext)
        Assertions.assertEquals(Money.ZERO, invoiceLine.getPrepaidFeesRemaining())

        at = getLastAccountTransactionForInvoiceLineWithId(cayenneContext, 300L, getRecordWithId(cayenneContext, Account.class, 200L))
        Assertions.assertNull(at)
        at = getLastAccountTransactionForInvoiceLineWithId(cayenneContext, 300L, getRecordWithId(cayenneContext, Account.class, 500L))
        Assertions.assertNull(at)
        invoiceLine = SelectById.query(InvoiceLine.class, 300).selectOne(cayenneContext)
        Assertions.assertEquals(Money.ZERO, invoiceLine.getPrepaidFeesRemaining())

        at = getLastAccountTransactionForInvoiceLineWithId(cayenneContext, 400L, getRecordWithId(cayenneContext, Account.class, 200L))
        Assertions.assertEquals(new Money("100.00"), at.getAmount())
        at = getLastAccountTransactionForInvoiceLineWithId(cayenneContext, 400L, getRecordWithId(cayenneContext, Account.class, 500L))
        Assertions.assertEquals(new Money("-100.00"), at.getAmount())
        invoiceLine = SelectById.query(InvoiceLine.class, 400).selectOne(cayenneContext)
        Assertions.assertEquals(Money.ZERO, invoiceLine.getPrepaidFeesRemaining())

        at = getLastAccountTransactionForInvoiceLineWithId(cayenneContext, 500L, getRecordWithId(cayenneContext, Account.class, 200L))
        Assertions.assertEquals(new Money("67.00"), at.getAmount())
        at = getLastAccountTransactionForInvoiceLineWithId(cayenneContext, 500L, getRecordWithId(cayenneContext, Account.class, 500L))
        Assertions.assertEquals(new Money("-67.00"), at.getAmount())
        invoiceLine = SelectById.query(InvoiceLine.class, 500).selectOne(cayenneContext)
        Assertions.assertEquals(Money.ZERO, invoiceLine.getPrepaidFeesRemaining())

        at = getLastAccountTransactionForInvoiceLineWithId(cayenneContext, 600L, getRecordWithId(cayenneContext, Account.class, 200L))
        Assertions.assertEquals(new Money("10.00"), at.getAmount())
        at = getLastAccountTransactionForInvoiceLineWithId(cayenneContext, 600L, getRecordWithId(cayenneContext, Account.class, 500L))
        Assertions.assertEquals(new Money("-10.00"), at.getAmount())
        invoiceLine = SelectById.query(InvoiceLine.class, 600).selectOne(cayenneContext)
        Assertions.assertEquals(Money.ZERO, invoiceLine.getPrepaidFeesRemaining())

        at = getLastAccountTransactionForInvoiceLineWithId(cayenneContext, 700L, getRecordWithId(cayenneContext, Account.class, 200L))
        Assertions.assertEquals(new Money("10.00"), at.getAmount())
        at = getLastAccountTransactionForInvoiceLineWithId(cayenneContext, 700L, getRecordWithId(cayenneContext, Account.class, 500L))
        Assertions.assertEquals(new Money("-10.00"), at.getAmount())
        invoiceLine = SelectById.query(InvoiceLine.class, 700).selectOne(cayenneContext)
        Assertions.assertEquals(Money.ZERO, invoiceLine.getPrepaidFeesRemaining())

        at = getLastAccountTransactionForInvoiceLineWithId(cayenneContext, 800L, getRecordWithId(cayenneContext, Account.class, 200L))
        Assertions.assertEquals(new Money("100.00"), at.getAmount())
        at = getLastAccountTransactionForInvoiceLineWithId(cayenneContext, 800L, getRecordWithId(cayenneContext, Account.class, 500L))
        Assertions.assertEquals(new Money("-100.00"), at.getAmount())
        invoiceLine = SelectById.query(InvoiceLine.class, 800).selectOne(cayenneContext)
        Assertions.assertEquals(Money.ZERO, invoiceLine.getPrepaidFeesRemaining())

        at = getLastAccountTransactionForInvoiceLineWithId(cayenneContext, 900L, getRecordWithId(cayenneContext, Account.class, 200L))
        Assertions.assertEquals(new Money("80.00"), at.getAmount())
        at = getLastAccountTransactionForInvoiceLineWithId(cayenneContext, 900L, getRecordWithId(cayenneContext, Account.class, 500L))
        Assertions.assertEquals(new Money("-80.00"), at.getAmount())
        invoiceLine = SelectById.query(InvoiceLine.class, 900).selectOne(cayenneContext)
        Assertions.assertEquals(Money.ZERO, invoiceLine.getPrepaidFeesRemaining())

        at = getLastAccountTransactionForInvoiceLineWithId(cayenneContext, 1700L, getRecordWithId(cayenneContext, Account.class, 200L))
        Assertions.assertEquals(new Money("100.00"), at.getAmount())
        at = getLastAccountTransactionForInvoiceLineWithId(cayenneContext, 1700L, getRecordWithId(cayenneContext, Account.class, 500L))
        Assertions.assertEquals(new Money("-100.00"), at.getAmount())
        invoiceLine = SelectById.query(InvoiceLine.class, 1700).selectOne(cayenneContext)
        Assertions.assertEquals(Money.ZERO, invoiceLine.getPrepaidFeesRemaining())

        at = getLastAccountTransactionForInvoiceLineWithId(cayenneContext, 1800L, getRecordWithId(cayenneContext, Account.class, 200L))
        Assertions.assertEquals(new Money("-100.00"), at.getAmount())
        at = getLastAccountTransactionForInvoiceLineWithId(cayenneContext, 1800L, getRecordWithId(cayenneContext, Account.class, 500L))
        Assertions.assertEquals(new Money("100.00"), at.getAmount())
        invoiceLine = SelectById.query(InvoiceLine.class, 1800).selectOne(cayenneContext)
        Assertions.assertEquals(Money.ZERO, invoiceLine.getPrepaidFeesRemaining())
    }

	
	@Test
	void testPostAtEverySessionNoSessionsStarted() {
        DelayedEnrolmentIncomePostingJob delayedEnrolmentIncomePostingJob = new DelayedEnrolmentIncomePostingJob(cayenneService, injector.getInstance(AccountTransactionService.class))

        PreferenceController.getController().setAccountPrepaidFeesPostAt(Preferences.ACCOUNT_PREPAID_FEES_POST_AT_EVERY_SESSION)
        try {
            delayedEnrolmentIncomePostingJob.executeWithDate(DateUtils.addHours(start1, -1))
        } catch (JobExecutionException e) {
            fail(e.getMessage())
        }

        DataContext newContext = cayenneService.getNewNonReplicatingContext()
        newContext.deleteObjects(newContext.select(SelectQuery.query(AccountTransaction.class)))
        newContext.deleteObjects(newContext.select(SelectQuery.query(Outcome.class)))
        newContext.deleteObjects(newContext.select(SelectQuery.query(Enrolment.class)))

        List<AccountTransaction> transactions = getAccountTransactionsForInvoiceLineWithId(newContext, 100L, getRecordWithId(newContext, Account.class, 200L))
        Assertions.assertTrue(transactions.isEmpty())
        transactions = getAccountTransactionsForInvoiceLineWithId(newContext, 100L, getRecordWithId(newContext, Account.class, 500L))
        Assertions.assertTrue(transactions.isEmpty())
        InvoiceLine invoiceLine = SelectById.query(InvoiceLine.class, 100).selectOne(newContext)
        Assertions.assertEquals(Money.ZERO, invoiceLine.getPrepaidFeesRemaining())

        transactions = getAccountTransactionsForInvoiceLineWithId(newContext, 200L, getRecordWithId(newContext, Account.class, 200L))
        Assertions.assertTrue(transactions.isEmpty())
        transactions = getAccountTransactionsForInvoiceLineWithId(newContext, 200L, getRecordWithId(newContext, Account.class, 500L))
        Assertions.assertEquals(2, transactions.size())
        invoiceLine = SelectById.query(InvoiceLine.class, 200).selectOne(newContext)
        Assertions.assertEquals(new Money("33.0"), invoiceLine.getPrepaidFeesRemaining())

        transactions = getAccountTransactionsForInvoiceLineWithId(newContext, 300L, getRecordWithId(newContext, Account.class, 200L))
        Assertions.assertTrue(transactions.isEmpty())
        transactions = getAccountTransactionsForInvoiceLineWithId(newContext, 300L, getRecordWithId(newContext, Account.class, 500L))
        Assertions.assertTrue(transactions.isEmpty())
        invoiceLine = SelectById.query(InvoiceLine.class, 300).selectOne(newContext)
        Assertions.assertEquals(Money.ZERO, invoiceLine.getPrepaidFeesRemaining())

        transactions = getAccountTransactionsForInvoiceLineWithId(newContext, 400L, getRecordWithId(newContext, Account.class, 200L))
        Assertions.assertTrue(transactions.isEmpty())
        transactions = getAccountTransactionsForInvoiceLineWithId(newContext, 400L, getRecordWithId(newContext, Account.class, 500L))
        Assertions.assertEquals(1, transactions.size())
        invoiceLine = SelectById.query(InvoiceLine.class, 400).selectOne(newContext)
        Assertions.assertEquals(new Money("100.0"), invoiceLine.getPrepaidFeesRemaining())

        transactions = getAccountTransactionsForInvoiceLineWithId(newContext, 500L, getRecordWithId(newContext, Account.class, 200L))
        Assertions.assertTrue(transactions.isEmpty())
        transactions = getAccountTransactionsForInvoiceLineWithId(newContext, 500L, getRecordWithId(newContext, Account.class, 500L))
        Assertions.assertEquals(2, transactions.size())
        invoiceLine = SelectById.query(InvoiceLine.class, 500).selectOne(newContext)
        Assertions.assertEquals(new Money("67.0"), invoiceLine.getPrepaidFeesRemaining())

        transactions = getAccountTransactionsForInvoiceLineWithId(newContext, 600L, getRecordWithId(newContext, Account.class, 200L))
        Assertions.assertTrue(transactions.isEmpty())
        transactions = getAccountTransactionsForInvoiceLineWithId(newContext, 600L, getRecordWithId(newContext, Account.class, 500L))
        Assertions.assertEquals(2, transactions.size())
        invoiceLine = SelectById.query(InvoiceLine.class, 600).selectOne(newContext)
        Assertions.assertEquals(new Money("10.0"), invoiceLine.getPrepaidFeesRemaining())

        transactions = getAccountTransactionsForInvoiceLineWithId(newContext, 700L, getRecordWithId(newContext, Account.class, 200L))
        Assertions.assertTrue(transactions.isEmpty())
        transactions = getAccountTransactionsForInvoiceLineWithId(newContext, 700L, getRecordWithId(newContext, Account.class, 500L))
        Assertions.assertEquals(2, transactions.size())
        invoiceLine = SelectById.query(InvoiceLine.class, 700).selectOne(newContext)
        Assertions.assertEquals(new Money("10.0"), invoiceLine.getPrepaidFeesRemaining())

        transactions = getAccountTransactionsForInvoiceLineWithId(newContext, 800L, getRecordWithId(newContext, Account.class, 200L))
        Assertions.assertTrue(transactions.isEmpty())
        transactions = getAccountTransactionsForInvoiceLineWithId(newContext, 800L, getRecordWithId(newContext, Account.class, 500L))
        Assertions.assertEquals(1, transactions.size())
        invoiceLine = SelectById.query(InvoiceLine.class, 800).selectOne(newContext)
        Assertions.assertEquals(new Money("100.0"), invoiceLine.getPrepaidFeesRemaining())

        Assertions.assertEquals(new Money("80.00"), getRecordWithId(newContext, InvoiceLine.class, 900L).getFinalPriceToPayExTax())

        transactions = getAccountTransactionsForInvoiceLineWithId(newContext, 900L, getRecordWithId(newContext, Account.class, 200L))
        Assertions.assertTrue(transactions.isEmpty())
        transactions = getAccountTransactionsForInvoiceLineWithId(newContext, 900L, getRecordWithId(newContext, Account.class, 500L))
        Assertions.assertEquals(1, transactions.size())
        invoiceLine = SelectById.query(InvoiceLine.class, 900).selectOne(newContext)
        Assertions.assertEquals(new Money("80.0"), invoiceLine.getPrepaidFeesRemaining())

        transactions = getAccountTransactionsForInvoiceLineWithId(newContext, 1700L, getRecordWithId(newContext, Account.class, 200L))
        Assertions.assertTrue(transactions.isEmpty())
        transactions = getAccountTransactionsForInvoiceLineWithId(newContext, 1700L, getRecordWithId(newContext, Account.class, 500L))
        Assertions.assertEquals(1, transactions.size())
        invoiceLine = SelectById.query(InvoiceLine.class, 1700).selectOne(newContext)
        Assertions.assertEquals(new Money("100.0"), invoiceLine.getPrepaidFeesRemaining())

        transactions = getAccountTransactionsForInvoiceLineWithId(newContext, 1800L, getRecordWithId(newContext, Account.class, 200L))
        Assertions.assertTrue(transactions.isEmpty())
        transactions = getAccountTransactionsForInvoiceLineWithId(newContext, 1800L, getRecordWithId(newContext, Account.class, 500L))
        Assertions.assertEquals(1, transactions.size())
        invoiceLine = SelectById.query(InvoiceLine.class, 1800).selectOne(newContext)
        Assertions.assertEquals(new Money("-100.0"), invoiceLine.getPrepaidFeesRemaining())
    }

	
	@Test
	void testPostAtEverySessionSomeSessionsStarted() {
        DelayedEnrolmentIncomePostingJob delayedEnrolmentIncomePostingJob = new DelayedEnrolmentIncomePostingJob(cayenneService, injector.getInstance(AccountTransactionService.class))

        PreferenceController.getController().setAccountPrepaidFeesPostAt(Preferences.ACCOUNT_PREPAID_FEES_POST_AT_EVERY_SESSION)
        try {
            delayedEnrolmentIncomePostingJob.executeWithDate(new Date())
        } catch (JobExecutionException e) {
            fail(e.getMessage())
        }

        cayenneContext.deleteObjects(cayenneContext.select(SelectQuery.query(AccountTransaction.class)))
        cayenneContext.deleteObjects(cayenneContext.select(SelectQuery.query(Outcome.class)))
        cayenneContext.deleteObjects(cayenneContext.select(SelectQuery.query(Enrolment.class)))

        List<AccountTransaction> at = getAccountTransactionsForInvoiceLineWithId(cayenneContext, 100L, getRecordWithId(cayenneContext, Account.class, 200L))
        Assertions.assertTrue(at.isEmpty())
        at = getAccountTransactionsForInvoiceLineWithId(cayenneContext, 100L, getRecordWithId(cayenneContext, Account.class, 500L))
        Assertions.assertTrue(at.isEmpty())
        InvoiceLine invoiceLine = SelectById.query(InvoiceLine.class, 100).selectOne(cayenneContext)
        Assertions.assertEquals(Money.ZERO, invoiceLine.getPrepaidFeesRemaining())

        at = getAccountTransactionsForInvoiceLineWithId(cayenneContext, 200L, getRecordWithId(cayenneContext, Account.class, 200L))
        Assertions.assertTrue(at.isEmpty())
        at = getAccountTransactionsForInvoiceLineWithId(cayenneContext, 200L, getRecordWithId(cayenneContext, Account.class, 500L))
        Assertions.assertEquals(2, at.size())
        invoiceLine = SelectById.query(InvoiceLine.class, 200).selectOne(cayenneContext)
        Assertions.assertEquals(new Money("33.0"), invoiceLine.getPrepaidFeesRemaining())

        at = getAccountTransactionsForInvoiceLineWithId(cayenneContext, 300L, getRecordWithId(cayenneContext, Account.class, 200L))
        Assertions.assertTrue(at.isEmpty())
        at = getAccountTransactionsForInvoiceLineWithId(cayenneContext, 300L, getRecordWithId(cayenneContext, Account.class, 500L))
        Assertions.assertTrue(at.isEmpty())
        invoiceLine = SelectById.query(InvoiceLine.class, 300).selectOne(cayenneContext)
        Assertions.assertEquals(Money.ZERO, invoiceLine.getPrepaidFeesRemaining())

        at = getAccountTransactionsForInvoiceLineWithId(cayenneContext, 400L, getRecordWithId(cayenneContext, Account.class, 200L))
        Assertions.assertEquals(new Money("33.00"), at.get(0).getAmount())
        at = getAccountTransactionsForInvoiceLineWithId(cayenneContext, 400L, getRecordWithId(cayenneContext, Account.class, 500L))
        Assertions.assertEquals(new Money("-33.00"), at.get(0).getAmount())
        invoiceLine = SelectById.query(InvoiceLine.class, 400).selectOne(cayenneContext)
        Assertions.assertEquals(new Money("67.0"), invoiceLine.getPrepaidFeesRemaining())

        at = getAccountTransactionsForInvoiceLineWithId(cayenneContext, 500L, getRecordWithId(cayenneContext, Account.class, 200L))
        Assertions.assertTrue(at.isEmpty())
        at = getAccountTransactionsForInvoiceLineWithId(cayenneContext, 500L, getRecordWithId(cayenneContext, Account.class, 500L))
        Assertions.assertEquals(2, at.size())
        invoiceLine = SelectById.query(InvoiceLine.class, 500).selectOne(cayenneContext)
        Assertions.assertEquals(new Money("67.0"), invoiceLine.getPrepaidFeesRemaining())

        at = getAccountTransactionsForInvoiceLineWithId(cayenneContext, 600L, getRecordWithId(cayenneContext, Account.class, 200L))
        Assertions.assertEquals(new Money("10.00"), at.get(0).getAmount())
        at = getAccountTransactionsForInvoiceLineWithId(cayenneContext, 600L, getRecordWithId(cayenneContext, Account.class, 500L))
        Assertions.assertEquals(new Money("-10.00"), at.get(0).getAmount())
        invoiceLine = SelectById.query(InvoiceLine.class, 600).selectOne(cayenneContext)
        Assertions.assertEquals(Money.ZERO, invoiceLine.getPrepaidFeesRemaining())

        at = getAccountTransactionsForInvoiceLineWithId(cayenneContext, 700L, getRecordWithId(cayenneContext, Account.class, 200L))
        Assertions.assertTrue(at.isEmpty())
        at = getAccountTransactionsForInvoiceLineWithId(cayenneContext, 700L, getRecordWithId(cayenneContext, Account.class, 500L))
        Assertions.assertEquals(2, at.size())
        invoiceLine = SelectById.query(InvoiceLine.class, 700).selectOne(cayenneContext)
        Assertions.assertEquals(new Money("10.0"), invoiceLine.getPrepaidFeesRemaining())

        at = getAccountTransactionsForInvoiceLineWithId(cayenneContext, 800L, getRecordWithId(cayenneContext, Account.class, 200L))
        Assertions.assertEquals(new Money("33.00"), at.get(0).getAmount())
        at = getAccountTransactionsForInvoiceLineWithId(cayenneContext, 800L, getRecordWithId(cayenneContext, Account.class, 500L))
        Assertions.assertEquals(new Money("-33.00"), at.get(0).getAmount())
        invoiceLine = SelectById.query(InvoiceLine.class, 800).selectOne(cayenneContext)
        Assertions.assertEquals(new Money("67.0"), invoiceLine.getPrepaidFeesRemaining())

        Assertions.assertEquals(new Money("80.00"), getRecordWithId(cayenneContext, InvoiceLine.class, 900L).getFinalPriceToPayExTax())

        at = getAccountTransactionsForInvoiceLineWithId(cayenneContext, 900L, getRecordWithId(cayenneContext, Account.class, 200L))
        Assertions.assertEquals(new Money("26.40"), at.get(0).getAmount())
        at = getAccountTransactionsForInvoiceLineWithId(cayenneContext, 900L, getRecordWithId(cayenneContext, Account.class, 500L))
        Assertions.assertEquals(new Money("-26.40"), at.get(0).getAmount())
        invoiceLine = SelectById.query(InvoiceLine.class, 900).selectOne(cayenneContext)
        Assertions.assertEquals(new Money("53.60"), invoiceLine.getPrepaidFeesRemaining())

        at = getAccountTransactionsForInvoiceLineWithId(cayenneContext, 1700L, getRecordWithId(cayenneContext, Account.class, 200L))
        Assertions.assertEquals(new Money("33.00"), at.get(0).getAmount())
        at = getAccountTransactionsForInvoiceLineWithId(cayenneContext, 1700L, getRecordWithId(cayenneContext, Account.class, 500L))
        Assertions.assertEquals(new Money("-33.00"), at.get(0).getAmount())
        invoiceLine = SelectById.query(InvoiceLine.class, 1700).selectOne(cayenneContext)
        Assertions.assertEquals(new Money("67.0"), invoiceLine.getPrepaidFeesRemaining())

        at = getAccountTransactionsForInvoiceLineWithId(cayenneContext, 1800L, getRecordWithId(cayenneContext, Account.class, 200L))
        Assertions.assertEquals(new Money("-33.00"), at.get(0).getAmount())
        at = getAccountTransactionsForInvoiceLineWithId(cayenneContext, 1800L, getRecordWithId(cayenneContext, Account.class, 500L))
        Assertions.assertEquals(new Money("33.00"), at.get(0).getAmount())
        invoiceLine = SelectById.query(InvoiceLine.class, 1800).selectOne(cayenneContext)
        Assertions.assertEquals(new Money("-67.0"), invoiceLine.getPrepaidFeesRemaining())
    }

	
	@Test
	void testPostAtEverySessionAllSessionsStarted() {
        DelayedEnrolmentIncomePostingJob delayedEnrolmentIncomePostingJob = new DelayedEnrolmentIncomePostingJob(cayenneService, injector.getInstance(AccountTransactionService.class))

        PreferenceController.getController().setAccountPrepaidFeesPostAt(Preferences.ACCOUNT_PREPAID_FEES_POST_AT_EVERY_SESSION)
        try {
            delayedEnrolmentIncomePostingJob.executeWithDate(DateUtils.addHours(start4, 1))
        } catch (JobExecutionException e) {
            fail(e.getMessage())
        }
        cayenneContext.deleteObjects(cayenneContext.select(SelectQuery.query(AccountTransaction.class)))
        cayenneContext.deleteObjects(cayenneContext.select(SelectQuery.query(Outcome.class)))
        cayenneContext.deleteObjects(cayenneContext.select(SelectQuery.query(Enrolment.class)))

        AccountTransaction at = getLastAccountTransactionForInvoiceLineWithId(cayenneContext, 100L, getRecordWithId(cayenneContext, Account.class, 200L))
        Assertions.assertNull(at)
        at = getLastAccountTransactionForInvoiceLineWithId(cayenneContext, 100L, getRecordWithId(cayenneContext, Account.class, 500L))
        Assertions.assertNull(at)
        InvoiceLine invoiceLine = SelectById.query(InvoiceLine.class, 100).selectOne(cayenneContext)
        Assertions.assertEquals(Money.ZERO, invoiceLine.getPrepaidFeesRemaining())

        at = getLastAccountTransactionForInvoiceLineWithId(cayenneContext, 200L, getRecordWithId(cayenneContext, Account.class, 200L))
        Assertions.assertEquals(new Money("33.00"), at.getAmount())
        at = getLastAccountTransactionForInvoiceLineWithId(cayenneContext, 200L, getRecordWithId(cayenneContext, Account.class, 500L))
        Assertions.assertEquals(new Money("-33.00"), at.getAmount())
        invoiceLine = SelectById.query(InvoiceLine.class, 200).selectOne(cayenneContext)
        Assertions.assertEquals(Money.ZERO, invoiceLine.getPrepaidFeesRemaining())

        at = getLastAccountTransactionForInvoiceLineWithId(cayenneContext, 300L, getRecordWithId(cayenneContext, Account.class, 200L))
        Assertions.assertNull(at)
        at = getLastAccountTransactionForInvoiceLineWithId(cayenneContext, 300L, getRecordWithId(cayenneContext, Account.class, 500L))
        Assertions.assertNull(at)
        invoiceLine = SelectById.query(InvoiceLine.class, 300).selectOne(cayenneContext)
        Assertions.assertEquals(Money.ZERO, invoiceLine.getPrepaidFeesRemaining())

        at = getLastAccountTransactionForInvoiceLineWithId(cayenneContext, 400L, getRecordWithId(cayenneContext, Account.class, 200L))
        Assertions.assertEquals(new Money("67.00"), at.getAmount())
        at = getLastAccountTransactionForInvoiceLineWithId(cayenneContext, 400L, getRecordWithId(cayenneContext, Account.class, 500L))
        Assertions.assertEquals(new Money("-67.00"), at.getAmount())
        invoiceLine = SelectById.query(InvoiceLine.class, 400).selectOne(cayenneContext)
        Assertions.assertEquals(new Money("33.0"), invoiceLine.getPrepaidFeesRemaining())

        at = getLastAccountTransactionForInvoiceLineWithId(cayenneContext, 500L, getRecordWithId(cayenneContext, Account.class, 200L))
        Assertions.assertEquals(new Money("34.00"), at.getAmount())
        at = getLastAccountTransactionForInvoiceLineWithId(cayenneContext, 500L, getRecordWithId(cayenneContext, Account.class, 500L))
        Assertions.assertEquals(new Money("-34.00"), at.getAmount())
        invoiceLine = SelectById.query(InvoiceLine.class, 500).selectOne(cayenneContext)
        Assertions.assertEquals(new Money("33.0"), invoiceLine.getPrepaidFeesRemaining())

        at = getLastAccountTransactionForInvoiceLineWithId(cayenneContext, 600L, getRecordWithId(cayenneContext, Account.class, 200L))
        Assertions.assertEquals(new Money("10.00"), at.getAmount())
        at = getLastAccountTransactionForInvoiceLineWithId(cayenneContext, 600L, getRecordWithId(cayenneContext, Account.class, 500L))
        Assertions.assertEquals(new Money("-10.00"), at.getAmount())
        invoiceLine = SelectById.query(InvoiceLine.class, 600).selectOne(cayenneContext)
        Assertions.assertEquals(Money.ZERO, invoiceLine.getPrepaidFeesRemaining())

        at = getLastAccountTransactionForInvoiceLineWithId(cayenneContext, 700L, getRecordWithId(cayenneContext, Account.class, 200L))
        Assertions.assertEquals(new Money("-40.00"), at.getAmount())
        at = getLastAccountTransactionForInvoiceLineWithId(cayenneContext, 700L, getRecordWithId(cayenneContext, Account.class, 500L))
        Assertions.assertEquals(new Money("40.00"), at.getAmount())
        invoiceLine = SelectById.query(InvoiceLine.class, 700).selectOne(cayenneContext)
        Assertions.assertEquals(new Money("50.0"), invoiceLine.getPrepaidFeesRemaining())

        at = getLastAccountTransactionForInvoiceLineWithId(cayenneContext, 800L, getRecordWithId(cayenneContext, Account.class, 200L))
        Assertions.assertEquals(new Money("67.00"), at.getAmount())
        at = getLastAccountTransactionForInvoiceLineWithId(cayenneContext, 800L, getRecordWithId(cayenneContext, Account.class, 500L))
        Assertions.assertEquals(new Money("-67.00"), at.getAmount())
        invoiceLine = SelectById.query(InvoiceLine.class, 800).selectOne(cayenneContext)
        Assertions.assertEquals(new Money("33.0"), invoiceLine.getPrepaidFeesRemaining())

        Assertions.assertEquals(new Money("80.00"), getRecordWithId(cayenneContext, InvoiceLine.class, 900L).getFinalPriceToPayExTax())

        at = getLastAccountTransactionForInvoiceLineWithId(cayenneContext, 900L, getRecordWithId(cayenneContext, Account.class, 200L))
        Assertions.assertEquals(new Money("53.60"), at.getAmount())
        at = getLastAccountTransactionForInvoiceLineWithId(cayenneContext, 900L, getRecordWithId(cayenneContext, Account.class, 500L))
        Assertions.assertEquals(new Money("-53.60"), at.getAmount())
        invoiceLine = SelectById.query(InvoiceLine.class, 900).selectOne(cayenneContext)
        Assertions.assertEquals(new Money("26.40"), invoiceLine.getPrepaidFeesRemaining())

        at = getLastAccountTransactionForInvoiceLineWithId(cayenneContext, 1700L, getRecordWithId(cayenneContext, Account.class, 200L))
        Assertions.assertEquals(new Money("67.00"), at.getAmount())
        at = getLastAccountTransactionForInvoiceLineWithId(cayenneContext, 1700L, getRecordWithId(cayenneContext, Account.class, 500L))
        Assertions.assertEquals(new Money("-67.00"), at.getAmount())
        invoiceLine = SelectById.query(InvoiceLine.class, 1700).selectOne(cayenneContext)
        Assertions.assertEquals(new Money("33.0"), invoiceLine.getPrepaidFeesRemaining())

        at = getLastAccountTransactionForInvoiceLineWithId(cayenneContext, 1800L, getRecordWithId(cayenneContext, Account.class, 200L))
        Assertions.assertEquals(new Money("-67.00"), at.getAmount())
        at = getLastAccountTransactionForInvoiceLineWithId(cayenneContext, 1800L, getRecordWithId(cayenneContext, Account.class, 500L))
        Assertions.assertEquals(new Money("67.00"), at.getAmount())
        invoiceLine = SelectById.query(InvoiceLine.class, 1800).selectOne(cayenneContext)
        Assertions.assertEquals(new Money("-33.0"), invoiceLine.getPrepaidFeesRemaining())
    }

	
	@Test
	void testPostAtEverySessionAllSessionsEnded() {
        DelayedEnrolmentIncomePostingJob delayedEnrolmentIncomePostingJob = new DelayedEnrolmentIncomePostingJob(cayenneService, injector.getInstance(AccountTransactionService.class))

        PreferenceController.getController().setAccountPrepaidFeesPostAt(Preferences.ACCOUNT_PREPAID_FEES_POST_AT_EVERY_SESSION)
        try {
            delayedEnrolmentIncomePostingJob.executeWithDate(DateUtils.addDays(start4, 1))
        } catch (JobExecutionException e) {
            fail(e.getMessage())
        }
        cayenneContext.deleteObjects(cayenneContext.select(SelectQuery.query(AccountTransaction.class)))
        cayenneContext.deleteObjects(cayenneContext.select(SelectQuery.query(Outcome.class)))
        cayenneContext.deleteObjects(cayenneContext.select(SelectQuery.query(Enrolment.class)))

        AccountTransaction at = getLastAccountTransactionForInvoiceLineWithId(cayenneContext, 100L, getRecordWithId(cayenneContext, Account.class, 200L))
        Assertions.assertNull(at)
        at = getLastAccountTransactionForInvoiceLineWithId(cayenneContext, 100L, getRecordWithId(cayenneContext, Account.class, 500L))
        Assertions.assertNull(at)
        InvoiceLine invoiceLine = SelectById.query(InvoiceLine.class, 100).selectOne(cayenneContext)
        Assertions.assertEquals(Money.ZERO, invoiceLine.getPrepaidFeesRemaining())

        at = getLastAccountTransactionForInvoiceLineWithId(cayenneContext, 200L, getRecordWithId(cayenneContext, Account.class, 200L))
        Assertions.assertEquals(new Money("33.00"), at.getAmount())
        at = getLastAccountTransactionForInvoiceLineWithId(cayenneContext, 200L, getRecordWithId(cayenneContext, Account.class, 500L))
        Assertions.assertEquals(new Money("-33.00"), at.getAmount())
        invoiceLine = SelectById.query(InvoiceLine.class, 200).selectOne(cayenneContext)
        Assertions.assertEquals(Money.ZERO, invoiceLine.getPrepaidFeesRemaining())

        at = getLastAccountTransactionForInvoiceLineWithId(cayenneContext, 300L, getRecordWithId(cayenneContext, Account.class, 200L))
        Assertions.assertNull(at)
        at = getLastAccountTransactionForInvoiceLineWithId(cayenneContext, 300L, getRecordWithId(cayenneContext, Account.class, 500L))
        Assertions.assertNull(at)
        invoiceLine = SelectById.query(InvoiceLine.class, 300).selectOne(cayenneContext)
        Assertions.assertEquals(Money.ZERO, invoiceLine.getPrepaidFeesRemaining())

        at = getLastAccountTransactionForInvoiceLineWithId(cayenneContext, 400L, getRecordWithId(cayenneContext, Account.class, 200L))
        Assertions.assertEquals(new Money("100.00"), at.getAmount())
        at = getLastAccountTransactionForInvoiceLineWithId(cayenneContext, 400L, getRecordWithId(cayenneContext, Account.class, 500L))
        Assertions.assertEquals(new Money("-100.00"), at.getAmount())
        invoiceLine = SelectById.query(InvoiceLine.class, 400).selectOne(cayenneContext)
        Assertions.assertEquals(Money.ZERO, invoiceLine.getPrepaidFeesRemaining())

        at = getLastAccountTransactionForInvoiceLineWithId(cayenneContext, 500L, getRecordWithId(cayenneContext, Account.class, 200L))
        Assertions.assertEquals(new Money("67.00"), at.getAmount())
        at = getLastAccountTransactionForInvoiceLineWithId(cayenneContext, 500L, getRecordWithId(cayenneContext, Account.class, 500L))
        Assertions.assertEquals(new Money("-67.00"), at.getAmount())
        invoiceLine = SelectById.query(InvoiceLine.class, 500).selectOne(cayenneContext)
        Assertions.assertEquals(Money.ZERO, invoiceLine.getPrepaidFeesRemaining())

        at = getLastAccountTransactionForInvoiceLineWithId(cayenneContext, 600L, getRecordWithId(cayenneContext, Account.class, 200L))
        Assertions.assertEquals(new Money("10.00"), at.getAmount())
        at = getLastAccountTransactionForInvoiceLineWithId(cayenneContext, 600L, getRecordWithId(cayenneContext, Account.class, 500L))
        Assertions.assertEquals(new Money("-10.00"), at.getAmount())
        invoiceLine = SelectById.query(InvoiceLine.class, 600).selectOne(cayenneContext)
        Assertions.assertEquals(Money.ZERO, invoiceLine.getPrepaidFeesRemaining())

        at = getLastAccountTransactionForInvoiceLineWithId(cayenneContext, 700L, getRecordWithId(cayenneContext, Account.class, 200L))
        Assertions.assertEquals(new Money("10.00"), at.getAmount())
        at = getLastAccountTransactionForInvoiceLineWithId(cayenneContext, 700L, getRecordWithId(cayenneContext, Account.class, 500L))
        Assertions.assertEquals(new Money("-10.00"), at.getAmount())
        invoiceLine = SelectById.query(InvoiceLine.class, 700).selectOne(cayenneContext)
        Assertions.assertEquals(Money.ZERO, invoiceLine.getPrepaidFeesRemaining())

        at = getLastAccountTransactionForInvoiceLineWithId(cayenneContext, 800L, getRecordWithId(cayenneContext, Account.class, 200L))
        Assertions.assertEquals(new Money("100.00"), at.getAmount())
        at = getLastAccountTransactionForInvoiceLineWithId(cayenneContext, 800L, getRecordWithId(cayenneContext, Account.class, 500L))
        Assertions.assertEquals(new Money("-100.00"), at.getAmount())
        invoiceLine = SelectById.query(InvoiceLine.class, 800).selectOne(cayenneContext)
        Assertions.assertEquals(Money.ZERO, invoiceLine.getPrepaidFeesRemaining())

        Assertions.assertEquals(new Money("80.00"), getRecordWithId(cayenneContext, InvoiceLine.class, 900L).getFinalPriceToPayExTax())

        at = getLastAccountTransactionForInvoiceLineWithId(cayenneContext, 900L, getRecordWithId(cayenneContext, Account.class, 200L))
        Assertions.assertEquals(new Money("80.00"), at.getAmount())
        at = getLastAccountTransactionForInvoiceLineWithId(cayenneContext, 900L, getRecordWithId(cayenneContext, Account.class, 500L))
        Assertions.assertEquals(new Money("-80.00"), at.getAmount())
        invoiceLine = SelectById.query(InvoiceLine.class, 900).selectOne(cayenneContext)
        Assertions.assertEquals(Money.ZERO, invoiceLine.getPrepaidFeesRemaining())

        at = getLastAccountTransactionForInvoiceLineWithId(cayenneContext, 1700L, getRecordWithId(cayenneContext, Account.class, 200L))
        Assertions.assertEquals(new Money("100.00"), at.getAmount())
        at = getLastAccountTransactionForInvoiceLineWithId(cayenneContext, 1700L, getRecordWithId(cayenneContext, Account.class, 500L))
        Assertions.assertEquals(new Money("-100.00"), at.getAmount())
        invoiceLine = SelectById.query(InvoiceLine.class, 1700
        ).selectOne(cayenneContext)
        Assertions.assertEquals(Money.ZERO, invoiceLine.getPrepaidFeesRemaining())

        at = getLastAccountTransactionForInvoiceLineWithId(cayenneContext, 1800L, getRecordWithId(cayenneContext, Account.class, 200L))
        Assertions.assertEquals(new Money("-100.00"), at.getAmount())
        at = getLastAccountTransactionForInvoiceLineWithId(cayenneContext, 1800L, getRecordWithId(cayenneContext, Account.class, 500L))
        Assertions.assertEquals(new Money("100.00"), at.getAmount())
        invoiceLine = SelectById.query(InvoiceLine.class, 1800).selectOne(cayenneContext)
        Assertions.assertEquals(Money.ZERO, invoiceLine.getPrepaidFeesRemaining())
    }

	
	@Test
	void testSelfPacedEnrolment() {
        DelayedEnrolmentIncomePostingJob delayedEnrolmentIncomePostingJob = new DelayedEnrolmentIncomePostingJob(cayenneService, injector.getInstance(AccountTransactionService.class))

        PreferenceController.getController().setAccountPrepaidFeesPostAt(Preferences.ACCOUNT_PREPAID_FEES_POST_AT_EVERY_SESSION)
        try {
            delayedEnrolmentIncomePostingJob.executeWithDate(new Date())
        } catch (JobExecutionException e) {
            fail(e.getMessage())
        }
        cayenneContext.deleteObjects(cayenneContext.select(SelectQuery.query(AccountTransaction.class)))
        cayenneContext.deleteObjects(cayenneContext.select(SelectQuery.query(Outcome.class)))
        cayenneContext.deleteObjects(cayenneContext.select(SelectQuery.query(Enrolment.class)))

        // testing self paced class with no fees remaining
        AccountTransaction at = getLastAccountTransactionForInvoiceLineWithId(cayenneContext, 1400L, getRecordWithId(cayenneContext, Account.class, 200L))
        Assertions.assertNull(at)
        at = getLastAccountTransactionForInvoiceLineWithId(cayenneContext, 1400L, getRecordWithId(cayenneContext, Account.class, 500L))
        Assertions.assertNull(at)
        InvoiceLine invoiceLine = SelectById.query(InvoiceLine.class, 1400).selectOne(cayenneContext)
        Assertions.assertEquals(Money.ZERO, invoiceLine.getPrepaidFeesRemaining())

        // testing self paced class with some fees remaining
        at = getLastAccountTransactionForInvoiceLineWithId(cayenneContext, 1500L, getRecordWithId(cayenneContext, Account.class, 200L))
        Assertions.assertEquals(new Money("50.00"), at.getAmount())
        at = getLastAccountTransactionForInvoiceLineWithId(cayenneContext, 1500L, getRecordWithId(cayenneContext, Account.class, 500L))
        Assertions.assertEquals(new Money("-50.00"), at.getAmount())
        invoiceLine = SelectById.query(InvoiceLine.class, 1500).selectOne(cayenneContext)
        Assertions.assertEquals(Money.ZERO, invoiceLine.getPrepaidFeesRemaining())

        // testing self paced class with all fees remaining
        at = getLastAccountTransactionForInvoiceLineWithId(cayenneContext, 1600L, getRecordWithId(cayenneContext, Account.class, 200L))
        Assertions.assertEquals(new Money("100.00"), at.getAmount())
        at = getLastAccountTransactionForInvoiceLineWithId(cayenneContext, 1600L, getRecordWithId(cayenneContext, Account.class, 500L))
        Assertions.assertEquals(new Money("-100.00"), at.getAmount())
        invoiceLine = SelectById.query(InvoiceLine.class, 1600).selectOne(cayenneContext)
        Assertions.assertEquals(Money.ZERO, invoiceLine.getPrepaidFeesRemaining())
    }

	
	@Test
	void testCancelledEnrolment() {
        DelayedEnrolmentIncomePostingJob delayedEnrolmentIncomePostingJob = new DelayedEnrolmentIncomePostingJob(cayenneService, injector.getInstance(AccountTransactionService.class))

        PreferenceController.getController().setAccountPrepaidFeesPostAt(Preferences.ACCOUNT_PREPAID_FEES_POST_AT_EVERY_SESSION)
        try {
            delayedEnrolmentIncomePostingJob.executeWithDate(new Date())
        } catch (JobExecutionException e) {
            fail(e.getMessage())
        }
        // testing cancelled class already started
        AccountTransaction at = getLastAccountTransactionForInvoiceLineWithId(cayenneContext, 1200L, getRecordWithId(cayenneContext, Account.class, 200L))
        Assertions.assertEquals(new Money("100.00"), at.getAmount())
        at = getLastAccountTransactionForInvoiceLineWithId(cayenneContext, 1200L, getRecordWithId(cayenneContext, Account.class, 500L))
        Assertions.assertEquals(new Money("-100.00"), at.getAmount())
        InvoiceLine invoiceLine = SelectById.query(InvoiceLine.class, 1200).selectOne(cayenneContext)
        Assertions.assertEquals(Money.ZERO, invoiceLine.getPrepaidFeesRemaining())

        // testing cancelled class not started yet.
        at = getLastAccountTransactionForInvoiceLineWithId(cayenneContext, 1300L, getRecordWithId(cayenneContext, Account.class, 200L))
        Assertions.assertEquals(new Money("100.00"), at.getAmount())
        at = getLastAccountTransactionForInvoiceLineWithId(cayenneContext, 1300L, getRecordWithId(cayenneContext, Account.class, 500L))
        Assertions.assertEquals(new Money("-100.00"), at.getAmount())
        invoiceLine = SelectById.query(InvoiceLine.class, 1300).selectOne(cayenneContext)
        Assertions.assertEquals(Money.ZERO, invoiceLine.getPrepaidFeesRemaining())
    }

	
	@Test
	void testCreateNewInvoiceLine() {
        DelayedEnrolmentIncomePostingJob delayedEnrolmentIncomePostingJob = new DelayedEnrolmentIncomePostingJob(cayenneService, injector.getInstance(AccountTransactionService.class))
        CourseClass cc = getRecordWithId(cayenneContext, CourseClass.class, 200L)

        Contact c = cayenneContext.newObject(Contact.class)
        c.setFirstName("aName")
        c.setLastName("aName")

        Student s = cayenneContext.newObject(Student.class)

        Enrolment e = cayenneContext.newObject(Enrolment.class)
        e.setSource(PaymentSource.SOURCE_ONCOURSE)

        Invoice i = cayenneContext.newObject(Invoice.class)
        i.setAmountOwing(Money.ZERO)
        i.setDebtorsAccount(getRecordWithId(cayenneContext, Account.class, 100L))

        InvoiceLine il = cayenneContext.newObject(InvoiceLine.class)
        il.setAccount(getRecordWithId(cayenneContext, Account.class, 200L))
        il.setPrepaidFeesAccount(getRecordWithId(cayenneContext, Account.class, 500L))
        il.setTax(getRecordWithId(cayenneContext, Tax.class, 100L))
        il.setTitle("some title")
        il.setDiscountEachExTax(new Money("20.00"))
        il.setPriceEachExTax(new Money("100.00"))
        il.setTaxEach(new Money("8"))
        il.setPrepaidFeesRemaining(new Money("80.00"))
        il.setQuantity(BigDecimal.ONE)

        s.setContact(c)

        i.setContact(c)
        il.setInvoice(i)

        e.setStudent(s)
        e.addToInvoiceLines(il)
        e.setCourseClass(cc)

        cayenneContext.commitChanges()

        //check that no transaction from income to  asset/discount accounts
        AccountTransaction at = getLastAccountTransactionForInvoiceLineWithId(cayenneContext, il.getId(), getRecordWithId(cayenneContext, Account.class, 200L))
        Assertions.assertNull(at)
        at = getLastAccountTransactionForInvoiceLineWithId(cayenneContext, il.getId(), getRecordWithId(cayenneContext, Account.class, 600L))
        Assertions.assertNull(at)


        at = getLastAccountTransactionForInvoiceLineWithId(cayenneContext, il.getId(), getRecordWithId(cayenneContext, Account.class, 500L))
        Assertions.assertEquals(new Money("80.00"), at.getAmount())
        at = getLastAccountTransactionForInvoiceLineWithId(cayenneContext, il.getId(), getRecordWithId(cayenneContext, Account.class, 400L))
        Assertions.assertEquals(new Money("8.00"), at.getAmount())

        List<AccountTransaction> list = getAccountTransactionsForInvoiceLineWithId(cayenneContext, il.getId(), getRecordWithId(cayenneContext, Account.class, 100L))
        if (!(list.get(0).getAmount() == new Money("80.00") && list.get(1).getAmount() == new Money("8.00"))) {
            if (!(list.get(1).getAmount() == new Money("80.00") && list.get(0).getAmount() == new Money("8.00"))) {
                fail("tansactions towards trade debtors are incorrect")
            }
        }

        delayedEnrolmentIncomePostingJob.setDelayedIncomePreference(Preferences.ACCOUNT_PREPAID_FEES_POST_AT_EVERY_SESSION)

        delayedEnrolmentIncomePostingJob.processInvoiceLine(il, new Date())

        list = getAccountTransactionsForInvoiceLineWithId(cayenneContext, il.getId(), getRecordWithId(cayenneContext, Account.class, 200L))
        Assertions.assertEquals(1, list.size())
        Assertions.assertEquals(new Money("26.40"), list.get(0).getAmount(), "after prepaid fees job tansactions towards liability account transactions are incorrect")


        list = getAccountTransactionsForInvoiceLineWithId(cayenneContext, il.getId(), getRecordWithId(cayenneContext, Account.class, 500L))
        if (!(list.get(0).getAmount() == new Money("80.00") && list.get(1).getAmount() == new Money("-26.40"))) {
            if (!(list.get(0).getAmount() == new Money("-26.40") && list.get(1).getAmount() == new Money("80.00"))) {
                fail("after prepaid fees job tansactions towards income account tranasctions are incorrect " + list.get(0).getAmount() + "," +
                        list.get(1).getAmount())
            }
        }

    }

    // -------- util methods --------

	
	private static AccountTransaction getLastAccountTransactionForInvoiceLineWithId(DataContext context, Long invoiceLineId, Account account) {
        Expression expression = ExpressionFactory.matchExp(_AccountTransaction.TABLE_NAME_PROPERTY, AccountTransactionType.INVOICE_LINE)
        expression = expression.andExp(ExpressionFactory.matchExp(_AccountTransaction.FOREIGN_RECORD_ID_PROPERTY, invoiceLineId))
        expression = expression.andExp(ExpressionFactory.matchExp(AccountTransaction.ACCOUNT_PROPERTY, account))
        List<AccountTransaction> list = context.select(SelectQuery.query(AccountTransaction.class, expression, Arrays.asList(AccountTransaction.CREATED_ON.desc())))
        if (!list.isEmpty()) {
            return list.get(0)
        }
        return null
    }

	
	private static List<AccountTransaction> getAccountTransactionsForInvoiceLineWithId(DataContext context, Long invoiceLineId, Account account) {
        Expression expression = ExpressionFactory.matchExp(_AccountTransaction.TABLE_NAME_PROPERTY, AccountTransactionType.INVOICE_LINE)
        expression = expression.andExp(ExpressionFactory.matchExp(_AccountTransaction.FOREIGN_RECORD_ID_PROPERTY, invoiceLineId))
        expression = expression.andExp(ExpressionFactory.matchExp(AccountTransaction.ACCOUNT_PROPERTY, account))
        List<AccountTransaction> list = context.select(SelectQuery.query(AccountTransaction.class, expression, Arrays.asList(AccountTransaction.CREATED_ON.desc())))
        return list
    }

}
