/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.willow.billing.settings

import com.google.inject.Inject
import groovy.transform.CompileStatic
import ish.oncourse.api.request.RequestService
import ish.oncourse.model.Settings
import ish.oncourse.services.persistence.ICayenneService
import ish.oncourse.willow.billing.v1.model.BillingPlan
import ish.oncourse.willow.billing.v1.model.Currency
import org.apache.cayenne.query.ObjectSelect

@CompileStatic
class SettingsService {
    
    @Inject
    private ICayenneService cayenneService
    
    @Inject
    private RequestService requestService

    String getInvoiceReference() {
        return getSettingsValue(Settings.BILLING_INVOICE_REFERENCE)
    }

    void setInvoiceReference(String invoiceReference) {
        setSettings(Settings.BILLING_INVOICE_REFERENCE, invoiceReference)
    }

    Currency getCurrency() {
        return Currency.fromValue(getSettingsValue(Settings.BILLING_CURRENCY))
    }
    
    void setCurrency(Currency currency) {
        setSettings(Settings.BILLING_INVOICE_REFERENCE, currency.toString())
    }
    
    String getContactFullName() {
        return getSettingsValue(Settings.BILLING_CONTACT_NAME)
    }

    void setContactFullName(String contactFullName) {
        setSettings(Settings.BILLING_CONTACT_NAME, contactFullName)
    }

    String getContactEmail() {
        return getSettingsValue(Settings.BILLING_CONTACT_EMAIL)
    }

    void setContactEmail(String contactEmail) {
        setSettings(Settings.BILLING_CONTACT_EMAIL, contactEmail)
    }

    String getContactPhone() {
        return getSettingsValue(Settings.BILLING_CONTACT_PHONE)
    }

    void setContactPhone(String contactPhone) {
        setSettings(Settings.BILLING_CONTACT_PHONE, contactPhone)
    }

    Integer getRequestedUsersCount() {
        String value = getSettingsValue(Settings.BILLING_REQUEST_USERS)
        return value ? Integer.valueOf(value) : null
    }
    
    void setRequestedUsersCount(Integer requestedUsersCount) {
        setSettings(Settings.BILLING_REQUEST_USERS, requestedUsersCount.toString())
    }

    BillingPlan getRequestedBillingPlan() {
        String value = BillingPlan.fromValue(getSettingsValue(Settings.BILLING_REQUEST_PLAN))
        return value ? BillingPlan.fromValue(value) : null

    }

    void setRequestedBillingPlan(BillingPlan requestedBillingPlan) {
        setSettings(Settings.BILLING_REQUEST_PLAN, requestedBillingPlan.toString()) 
    }


    /**
     * Read only settings, can be changed from outside enviroment only
     * @return
     */
    Integer getUsersCount() {
        String value = getSettingsValue(Settings.BILLING_USERS)
        return value ? Integer.valueOf(value) : null
    }

    BillingPlan getBillingPlan() {
        return BillingPlan.fromValue(getSettingsValue(Settings.BILLING_PLAN))
    }
    

    private Settings getSettings(String key) {
        ObjectSelect.query(Settings)
                .where(Settings.COLLEGE.eq(requestService.college))
                .and(Settings.NAME.eq(key))
                .selectOne(cayenneService.newContext())
        
    }
    
    private String getSettingsValue(String key) {
        getSettings(key)?.value
    }

    private void setSettings(String key, String value) {
        Settings settings = getSettings(key)
        if (!settings) {
            settings = cayenneService.newContext().newObject(Settings)
            settings.name = key
            settings.college = settings.objectContext.localObject(requestService.college)
            settings.created = new Date()
        }
        settings.value = value
        settings.modified = new Date()
        settings.objectContext.commitChanges()
    }
}
