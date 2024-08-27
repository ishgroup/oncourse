/*
 * Copyright ish group pty ltd 2024.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.services.chargebee


import io.bootique.annotation.BQConfigProperty

class ChargebeeService {

    private String site = null
    private String apiKey = null
    private String subscriptionId = null
    private String smsItemId = null
    private String totalPaymentInItemId = null
    private String totalPaymentItemId = null
    private String totalWebPaymentInItemId = null
    private String totalCorporatePassItemId = null

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
    void setTotalPaymentItemId(String paymentItemId) {
        this.totalPaymentInItemId = paymentItemId
    }

    @BQConfigProperty
    void setTotalPaymentInItemId(String totalPaymentInItemId) {
        this.totalPaymentInItemId = totalPaymentInItemId
    }

    @BQConfigProperty
    void setTotalWebPaymentInItemId(String totalWebPaymentItemId) {
        this.totalWebPaymentInItemId = totalWebPaymentItemId
    }

    @BQConfigProperty
    void setTotalCorporatePassItemId(String totalCorporatePassItemId) {
        this.totalCorporatePassItemId = totalCorporatePassItemId
    }

    String getSite() {
        return site
    }

    String getApiKey() {
        return apiKey
    }

    String getSubscriptionId() {
        return subscriptionId
    }

    String getSmsItemId() {
        return smsItemId
    }

    String getTotalPaymentItemId() {
        return totalPaymentItemId
    }

    String getTotalPaymentInItemId() {
        return totalPaymentInItemId
    }

    String getTotalWebPaymentInItemId() {
        return totalWebPaymentInItemId
    }

    String getTotalCorporatePassItemId() {
        return totalCorporatePassItemId
    }
}
