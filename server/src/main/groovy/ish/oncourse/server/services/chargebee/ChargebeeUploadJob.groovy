/*
 * Copyright ish group pty ltd 2024.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.services.chargebee

import com.chargebee.Environment
import com.chargebee.models.Usage
import com.google.inject.Inject
import ish.common.types.PaymentSource
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.PreferenceController
import ish.oncourse.server.cayenne.Audit
import ish.oncourse.server.cayenne.PaymentIn
import ish.oncourse.server.cayenne.PaymentInLine
import ish.oncourse.server.cayenne.PaymentOut
import ish.oncourse.server.cayenne.PaymentOutLine
import ish.oncourse.server.scripting.api.EmailService
import ish.oncourse.server.services.AuditService
import ish.oncourse.server.util.DbConnectionUtils
import ish.oncourse.types.AuditAction
import org.apache.commons.lang3.StringUtils
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.quartz.DisallowConcurrentExecution
import org.quartz.Job
import org.quartz.JobExecutionContext
import org.quartz.JobExecutionException

import java.sql.Statement
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.time.Instant

@DisallowConcurrentExecution
class ChargebeeUploadJob implements Job {
    private static final int SMS_LENGTH = 160
    private static final SimpleDateFormat SQL_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    private static final Logger logger = LogManager.getLogger()

    private static final String TOTAL_CREDIT_PAYMENT_AMOUNT_QUERY_FORMAT = "SELECT SUM(p.amount) AS value" +
            "          FROM %s p JOIN PaymentMethod pm on p.paymentMethodId = pm.id" +
            "          WHERE pm.type = 2 " +
            "          AND p.createdOn >= '%s'" +
            "          AND p.createdOn < '%s'" +
            "          AND p.status IN (3, 6)"

    private static final String WEB_CREDIT_AMOUNT_QUERY_FORMAT = "SELECT SUM(pi.amount) AS value" +
            "          FROM PaymentIn pi JOIN PaymentMethod pm on pi.paymentMethodId = pm.id" +
            "          WHERE pm.type = 2 " +
            "          AND pi.createdOn >= '%s'" +
            "          AND pi.createdOn < '%s'" +
            "          AND pi.status IN (3, 6)" +
            "          AND pi.source = '$PaymentSource.SOURCE_WEB.databaseValue'"

    private static final String SMS_LENGTH_QUERY_FORMAT = "SELECT COALESCE(SUM((length(m.smsText) DIV $SMS_LENGTH) + 1), 0) AS credits" +
            "          FROM Message m" +
            "          WHERE m.smsText IS NOT NULL AND m.smsText <> ''" +
            "          AND m.createdOn >= '%s'" +
            "          AND m.createdOn < '%s'"

    @Inject
    private ICayenneService cayenneService

    @Inject
    private ChargebeeService chargebeeService

    @Inject
    private EmailService emailService

    @Inject
    private PreferenceController preferenceController

    @Inject
    private AuditService auditService


    @Override
    void execute(JobExecutionContext context) throws JobExecutionException {
        logger.warn("ChargebeeUploadJob started")

        Calendar aCalendar = Calendar.getInstance()
        aCalendar.add(Calendar.MONTH, -1)
        aCalendar.set(Calendar.DATE, 1)
        String firstDateOfPreviousMonth = SQL_DATE_FORMAT.format(aCalendar.getTime())

        aCalendar.add(Calendar.MONTH, 1)
        aCalendar.set(Calendar.DATE, 1)
        String firstDateOfCurrentMonth = SQL_DATE_FORMAT.format(aCalendar.getTime())

        logger.warn("Chargebee Start date including $firstDateOfPreviousMonth , end date $firstDateOfCurrentMonth")

        try {
            uploadUsage(ChargebeeItemType.TOTAL_CREDIT_PAYMENT_IN, String.format(TOTAL_CREDIT_PAYMENT_AMOUNT_QUERY_FORMAT, PaymentIn.simpleName, firstDateOfPreviousMonth, firstDateOfCurrentMonth))
            uploadUsageToSite(ChargebeeItemType.TOTAL_CREDIT_PAYMENT, String.valueOf(getTotalAmount(firstDateOfPreviousMonth, firstDateOfCurrentMonth)))
            uploadUsageToSite(ChargebeeItemType.TOTAL_CORPORATE_PASS, String.valueOf(getTotalCorporatePassAmount(firstDateOfPreviousMonth, firstDateOfCurrentMonth)))
            uploadUsage(ChargebeeItemType.SMS, String.format(SMS_LENGTH_QUERY_FORMAT, firstDateOfPreviousMonth, firstDateOfCurrentMonth))
            uploadUsage(ChargebeeItemType.TOTAL_CREDIT_WEB_PAYMENT_IN, String.format(WEB_CREDIT_AMOUNT_QUERY_FORMAT, firstDateOfPreviousMonth, firstDateOfCurrentMonth))
        } catch (Exception e) {
            logger.error(e.getMessage())
            throw e
        }

        logger.warn("ChargeebeeUploadJob executed successfully")
    }


    private void uploadUsage(ChargebeeItemType type, String query) {
        def value = getLongForDbQuery(query)
        uploadUsageToSite(type, String.valueOf(value))
    }

    private void uploadUsageToSite(ChargebeeItemType chargebeeItemType, String quantity) {

        if (chargebeeService.site == null || chargebeeService.apiKey == null ||
                chargebeeService.smsItemId == null || chargebeeService.totalPaymentItemId == null ||
                chargebeeService.totalPaymentInItemId == null || chargebeeService.totalCorporatePassItemId == null ||
                chargebeeService.totalWebPaymentInItemId == null) {
            logger.error("Try to use chargebee, but its configs don't have necessary field")
            throw new RuntimeException("Try to use chargebee, but its configs don't have necessary field")
        }

        if(chargebeeItemType == null) {
            throw new IllegalArgumentException("Try to upload chargebee usage without item type")
        }

        String itemPriceId = chargebeeService.configOf(chargebeeItemType)
        logger.warn("Try to upload to chargebee $chargebeeItemType with id $itemPriceId value $quantity")

        if(Boolean.TRUE == chargebeeService.localMode)
            auditService.submit(cayenneService.newContext.newObject(Audit), AuditAction.CREATE, "$quantity $chargebeeItemType")
        else
            uploadToChargebee(itemPriceId, quantity)
    }

    private void uploadToChargebee(String itemPriceId, String quantity) {
        try {
            Environment.configure(chargebeeService.site, chargebeeService.apiKey)
            Usage.create(chargebeeService.subscriptionId)
                    .itemPriceId(itemPriceId)
                    .quantity(quantity)
                    .usageDate(new Timestamp(Instant.now().toEpochMilli()))
                    .request()
        } catch (Exception e) {
            logger.error("Chargebee usage upload error: " + e.getMessage())
            emailService.email {
                subject('onCourse->Chargebee usage upload error. Contact ish support')
                content("\n Reason: $e.message")
                from (preferenceController.emailFromAddress)
                to ("accounts@ish.com.au")
            }
        }
    }

    private Long getLongForDbQuery(String query) {
        def getValue = { Statement statement ->
            return getNumberForQueryFromDb(statement, query)
        }

        return DbConnectionUtils.executeWithClose(getValue, cayenneService.getDataSource()) as Long
    }

    private static Long getNumberForQueryFromDb(Statement statement, String query) {
        def resultSet = statement.executeQuery(query)
        resultSet.last()
        return resultSet.getLong(1)
    }

    private Long getTotalAmount(String startDate, String endDate) {
        String paymentsInQuery = String.format(TOTAL_CREDIT_PAYMENT_AMOUNT_QUERY_FORMAT, PaymentIn.simpleName, startDate, endDate)
        def paymentsInTotal = getLongForDbQuery(paymentsInQuery)

        String paymentsOutQuery = String.format(TOTAL_CREDIT_PAYMENT_AMOUNT_QUERY_FORMAT, PaymentOut.simpleName, startDate, endDate)
        def paymentsOutTotal = getLongForDbQuery(paymentsOutQuery)

        return paymentsInTotal + paymentsOutTotal
    }

    private Long getTotalCorporatePassAmount(String startDate, String endDate) {
        String query = "SELECT SUM(p.amount) AS value" +
                "          FROM %s pil JOIN %s p ON pil.%sId = p.id JOIN PaymentMethod pm on p.paymentMethodId = pm.id JOIN Invoice i ON pil.invoiceId = i.id" +
                "          WHERE pm.type = 2 " +
                "          AND p.createdOn >= '$startDate'" +
                "          AND p.createdOn < '$endDate'" +
                "          AND i.corporatePassId IS NOT NULL" +
                "          AND p.status IN (3, 6)"

        String paymentsInQuery = String.format(query, PaymentInLine.simpleName, PaymentIn.simpleName, StringUtils.toRootLowerCase(PaymentIn.simpleName))
        def paymentsInTotal = getLongForDbQuery(paymentsInQuery)

        String paymentsOutQuery = String.format(query, PaymentOutLine.simpleName, PaymentOut.simpleName, StringUtils.toRootLowerCase(PaymentOut.simpleName))
        def paymentsOutTotal = getLongForDbQuery(paymentsOutQuery)

        return paymentsInTotal + paymentsOutTotal
    }
}
