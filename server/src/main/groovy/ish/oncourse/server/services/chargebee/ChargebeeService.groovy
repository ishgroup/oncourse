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
import io.bootique.annotation.BQConfigProperty
import ish.oncourse.server.PreferenceController
import ish.oncourse.server.scripting.api.EmailService
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import java.sql.Timestamp

class ChargebeeService {
    private static final Logger logger = LogManager.getLogger()

    private String site = null
    private String apiKey = null
    private String subscriptionId = null
    private String smsItemId = null
    private String paymentItemId = null

    @BQConfigProperty
    void setSite(String site) {
        this.site = site
    }

    @BQConfigProperty
    void setApiKey(String apiKey) {
        this.apiKey = apiKey
    }

    @BQConfigProperty
    void setSubscriptionId(String subscriptionId) {
        this.subscriptionId = subscriptionId
    }

    @BQConfigProperty
    void setSmsItemId(String smsItemId) {
        this.smsItemId = smsItemId
    }

    @BQConfigProperty
    void setPaymentItemId(String paymentItemId) {
        this.paymentItemId = paymentItemId
    }

    @Inject
    private EmailService emailService

    @Inject
    private PreferenceController preferenceController


    //cbdemo_sample_plan - item id? Sms-charge ?  BTLyazUIcA0R518yH - subscription id
    void uploadUsage(ChargebeeItemType chargebeeItemType, String quantity) {
        if (site == null || apiKey == null) {
            logger.error("Try to use chargebee, but its configs don't have site or api key")
            throw new RuntimeException("Try to use chargebee, but its configs don't have site or api key")
        }

        if(chargebeeItemType == null) {
            throw new IllegalArgumentException("Try to upload chargebee usage without item type")
        }

        def itemPriceId = chargebeeItemType == ChargebeeItemType.SMS ? smsItemId : paymentItemId

        try {
            Environment.configure(site, apiKey)
            Usage.create(subscriptionId)
                    .itemPriceId(itemPriceId)
                    .quantity(quantity)
                    .usageDate(new Timestamp(System.currentTimeMillis()))
                    .request()
        } catch (Exception e) {
            logger.error("Chargebee usage upload error: " + e.getMessage())
            emailService.email {
                subject('onCourse->Chargebee usage upload error. Contact ish support')
                content("\n Reason: $e.message")
                from (preferenceController.emailFromAddress)
                to (preferenceController.emailAdminAddress)
            }
        }
    }
}
