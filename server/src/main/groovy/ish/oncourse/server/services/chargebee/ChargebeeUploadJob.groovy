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
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.PreferenceController
import ish.oncourse.server.scripting.api.EmailService
import ish.oncourse.server.util.DbConnectionUtils
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

    private static final String PAYMENT_AMOUNT_QUERY_FORMAT = "SELECT SUM(pi.amount) AS value" +
            "          FROM PaymentIn pi JOIN PaymentMethod pm on pi.paymentMethodId = pm.id" +
            "          WHERE pm.type = 2 " +
            "          AND pi.createdOn >= '%s'" +
            "          AND pi.createdOn <= '%s'" +
            "          AND pi.status IN (3, 6)"

    private static final String SMS_LENGTH_QUERY_FORMAT = "SELECT COALESCE(SUM((length(m.smsText) DIV $SMS_LENGTH) + 1), 0) AS credits" +
            "          FROM Message m" +
            "          WHERE m.smsText IS NOT NULL AND m.smsText <> ''" +
            "          AND m.createdOn >= '%s'" +
            "          AND m.createdOn <= '%s'"

    private static final String BILLING_USERS_QUERY = "SELECT s.value" +
            "          FROM Settings s" +
            "          WHERE s.name = 'billing.users'"

    @Inject
    private ICayenneService cayenneService

    @Inject
    private ChargebeeService chargebeeService

    @Inject
    private EmailService emailService

    @Inject
    private PreferenceController preferenceController

    @Override
    void execute(JobExecutionContext context) throws JobExecutionException {
        Calendar aCalendar = Calendar.getInstance()
        aCalendar.add(Calendar.MONTH, -1)
        aCalendar.set(Calendar.DATE, 1)
        String firstDateOfPreviousMonth = SQL_DATE_FORMAT.format(aCalendar.getTime())

        aCalendar.add(Calendar.MONTH, 1)
        aCalendar.set(Calendar.DATE, 1)
        String firstDateOfCurrentMonth = SQL_DATE_FORMAT.format(aCalendar.getTime())

        try {
            uploadUsage(ChargebeeItemType.PAYMENT, String.format(PAYMENT_AMOUNT_QUERY_FORMAT, firstDateOfPreviousMonth, firstDateOfCurrentMonth))
            uploadUsage(ChargebeeItemType.SMS, String.format(SMS_LENGTH_QUERY_FORMAT, firstDateOfPreviousMonth, firstDateOfCurrentMonth))
            uploadUsage(ChargebeeItemType.BILLING_USERS, BILLING_USERS_QUERY)
        } catch (Exception e) {
            logger.error(e.getMessage())
            throw e
        }
    }


    private void uploadUsage(ChargebeeItemType type, String query) {
        def getValue = { Statement statement ->
            return getNumberForQueryFromDb(statement, query)
        }

        def value = DbConnectionUtils.executeWithClose(getValue, cayenneService.getDataSource())
        uploadUsageToSite(type, String.valueOf(value))
    }

    private void uploadUsageToSite(ChargebeeItemType chargebeeItemType, String quantity) {
        if (chargebeeService.site == null || chargebeeService.apiKey == null ||
                chargebeeService.smsItemId == null || chargebeeService.paymentItemId == null || chargebeeService.billingUsersItemId == null) {
            logger.error("Try to use chargebee, but its configs don't have necessary field")
            throw new RuntimeException("Try to use chargebee, but its configs don't have necessary field")
        }

        if(chargebeeItemType == null) {
            throw new IllegalArgumentException("Try to upload chargebee usage without item type")
        }

        String itemPriceId
        switch (chargebeeItemType) {
            case ChargebeeItemType.SMS:
                itemPriceId = chargebeeService.smsItemId
                break
            case ChargebeeItemType.PAYMENT:
                itemPriceId = chargebeeService.paymentItemId
                break
            case ChargebeeItemType.BILLING_USERS:
                itemPriceId = chargebeeService.billingUsersItemId
                break
            default:
                throw new IllegalArgumentException("Unexpected chargebee usage item type")
        }

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

    private static Long getNumberForQueryFromDb(Statement statement, String query) {
        def resultSet = statement.executeQuery(query)
        resultSet.last()
        return resultSet.getLong(1)
    }
}
