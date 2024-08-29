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
import ish.oncourse.server.cayenne.Audit
import ish.oncourse.server.cayenne.Script
import ish.oncourse.server.cayenne.Settings
import ish.oncourse.server.scripting.api.EmailService
import ish.oncourse.server.services.AuditService
import ish.oncourse.server.services.chargebee.property.ChargebeePropertyProcessor
import ish.oncourse.server.services.chargebee.property.ChargeebeeProcessorFactory
import ish.oncourse.types.AuditAction
import org.apache.cayenne.query.ObjectSelect
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.quartz.DisallowConcurrentExecution
import org.quartz.Job
import org.quartz.JobExecutionContext
import org.quartz.JobExecutionException

import java.sql.Timestamp
import java.time.Instant

@DisallowConcurrentExecution
class ChargebeeUploadJob implements Job {
    private static final Logger logger = LogManager.getLogger()

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

        if (chargebeeService.site == null || chargebeeService.apiKey == null ||
                chargebeeService.smsItemId == null || chargebeeService.totalPaymentItemId == null ||
                chargebeeService.totalPaymentInItemId == null || chargebeeService.totalCorporatePassItemId == null ||
                chargebeeService.totalWebPaymentInItemId == null) {
            logger.error("Try to use chargebee, but its configs don't have necessary field")
            throw new RuntimeException("Try to use chargebee, but its configs don't have necessary field")
        }

        Calendar aCalendar = Calendar.getInstance()
        aCalendar.add(Calendar.MONTH, -1)
        aCalendar.set(Calendar.DATE, 1)
        def firstDateOfPreviousMonth = aCalendar.getTime()

        aCalendar.add(Calendar.MONTH, 1)
        aCalendar.set(Calendar.DATE, 1)
        def firstDateOfCurrentMonth = aCalendar.getTime()

        def propertiesToUpload = ChargebeeItemType.values()

        logger.warn("Chargebee start date including $firstDateOfPreviousMonth , end date $firstDateOfCurrentMonth")

        try {
            propertiesToUpload.each { type ->
                def property = ChargeebeeProcessorFactory.valueOf(type, firstDateOfPreviousMonth, firstDateOfCurrentMonth)
                uploadUsageToSite(property)
            }
        } catch (Exception e) {
            logger.catching(e)
            throw e
        }

        logger.warn("ChargeebeeUploadJob executed successfully")
    }


    private void uploadUsageToSite(ChargebeePropertyProcessor propertyProcessor) {
        if(propertyProcessor.type == null) {
            throw new IllegalArgumentException("Try to upload chargebee usage without item type")
        }

        def quantity = propertyProcessor.getValue(cayenneService.dataSource)
        String itemPriceId = chargebeeService.configOf(propertyProcessor.type)
        logger.warn("Try to upload to chargebee $propertyProcessor.type with id $itemPriceId value $quantity")

        if(Boolean.TRUE == chargebeeService.localMode)
            auditService.submit(ObjectSelect.query(Script).selectFirst(cayenneService.newReadonlyContext), AuditAction.SCRIPT_EXECUTED, "Try to upload to chargebee $propertyProcessor.type with id $itemPriceId value $quantity")
        else
            uploadToChargebee(itemPriceId, String.valueOf(quantity))
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
}
