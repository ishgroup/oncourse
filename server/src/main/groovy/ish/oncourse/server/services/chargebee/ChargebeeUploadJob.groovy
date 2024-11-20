/*
 * Copyright ish group pty ltd 2024.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.services.chargebee

import com.chargebee.Environment
import com.chargebee.models.Subscription
import com.chargebee.models.Usage
import com.google.inject.Inject
import ish.common.chargebee.ChargebeePropertyType
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.PreferenceController
import ish.oncourse.server.cayenne.Script
import ish.oncourse.server.messaging.MessageService
import ish.oncourse.server.scripting.api.EmailService
import ish.oncourse.server.scripting.api.EmailSpec
import ish.oncourse.server.scripting.api.MessageSpec
import ish.oncourse.server.services.AuditService
import ish.oncourse.server.services.chargebee.property.ChargebeePropertyProcessor
import ish.oncourse.server.services.chargebee.property.ChargeebeeProcessorFactory
import ish.oncourse.types.AuditAction
import ish.util.LocalDateUtils
import org.apache.cayenne.query.ObjectSelect
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.quartz.DisallowConcurrentExecution
import org.quartz.Job
import org.quartz.JobExecutionContext
import org.quartz.JobExecutionException

import java.sql.Timestamp
import java.time.Instant
import java.time.temporal.ChronoUnit

@DisallowConcurrentExecution
class ChargebeeUploadJob implements Job {
    private static final Logger logger = LogManager.getLogger()

    @Inject
    private ICayenneService cayenneService

    @Inject
    private ChargebeeService chargebeeService

    @Inject
    private MessageService messageService

    @Inject
    private PreferenceController preferenceController

    @Inject
    private AuditService auditService

    private static Subscription subscription = null


    @Override
    void execute(JobExecutionContext context) throws JobExecutionException {
        logger.warn("ChargebeeUploadJob started")

        def addons = chargebeeService.getAllowedAddons()
        if(addons.isEmpty()) {
            logger.warn("ChargebeeUploadJob is rejected due to allowed addons not configured for this college")
            return
        }

        def site = chargebeeService.configOf(ChargebeePropertyType.SITE)
        def apiKey = chargebeeService.configOf(ChargebeePropertyType.API_KEY)


        if (site == null || apiKey == null) {
            String error = "Try to use chargebee, but its configs don't have necessary field (site or api key)"
            logger.error(error)
            throw new RuntimeException(error)
        }

        Calendar aCalendar = Calendar.getInstance()
        def startOfCurrentDay = LocalDateUtils.calendarToValue(aCalendar).toDate()

        aCalendar.add(Calendar.DAY_OF_YEAR, -1)
        def startOfPreviousDay = LocalDateUtils.calendarToValue(aCalendar).toDate()

        def propertiesToUpload = ChargebeePropertyType.getItems()
                .findAll {addons.contains(it.getDbPropertyName())}

        logger.warn("Chargebee start date including $startOfPreviousDay , end date $startOfCurrentDay")

        try {
            if(!chargebeeService.localMode)
                Environment.configure(site, apiKey)

            propertiesToUpload.each { type ->
                def property = ChargeebeeProcessorFactory.valueOf(type, startOfPreviousDay, startOfCurrentDay)
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

        String itemPriceId = chargebeeService.configOf(propertyProcessor.type)
        if(itemPriceId == null)
            throw new IllegalArgumentException("Try to upload usage $propertyProcessor.type without configured item id")

        def quantity = propertyProcessor.getValue(cayenneService.dataSource)
        logger.warn("Try to upload to chargebee $propertyProcessor.type with id $itemPriceId value $quantity")

        if(Boolean.TRUE == chargebeeService.localMode)
            auditService.submit(ObjectSelect.query(Script).selectFirst(cayenneService.newReadonlyContext), AuditAction.SCRIPT_EXECUTED, "Try to upload to chargebee $propertyProcessor.type with id $itemPriceId value " + quantity.toPlainString())
        else {
            def subscription = getSubscription()
            if(!subscription.subscriptionItems().find {it.itemPriceId() == itemPriceId}) {
                logger.warn("Item price id $itemPriceId not allowed for subscription $chargebeeService.subscriptionId and will be ignored")
                return
            }

            if(!quantity.equals(BigDecimal.ZERO))
                uploadToChargebee(itemPriceId, quantity.toPlainString())
        }
    }

    private void uploadToChargebee(String itemPriceId, String quantity) {
        try {
            Usage.create(chargebeeService.subscriptionId)
                    .itemPriceId(itemPriceId)
                    .quantity(quantity)
                    .usageDate(new Timestamp(Instant.now().minus(1L, ChronoUnit.DAYS).toEpochMilli()))
                    .request()
        } catch (Exception e) {
            logger.error("Chargebee usage upload error: " + e.getMessage())
            messageService.sendMessage(new MessageSpec().with {
                it.subject = 'onCourse->Chargebee usage upload error. Contact ish support'
                it.content ="\n$itemPriceId upload error for college $preferenceController.collegeName. Reason: $e.message"
                it.from(preferenceController.emailFromAddress)
                it.to("accounts@ish.com.au")
                it
            })
        }
    }

    private Subscription getSubscription(){
        if(subscription != null)
            return subscription


        def subscriptions = Subscription.list().id().is(chargebeeService.subscriptionId).request()
        if(subscriptions.empty) {
            throw new IllegalArgumentException("Subscription with id $chargebeeService.subscriptionId not found!")
        }

        subscription = subscriptions.first().subscription()
        return subscription
    }
}
