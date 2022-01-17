/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.willow.billing.service.impl

import com.google.inject.Inject
import ish.oncourse.api.request.RequestService
import ish.oncourse.willow.billing.env.EnvironmentService
import ish.oncourse.willow.billing.settings.SettingsService
import ish.oncourse.willow.billing.v1.model.SettingsDTO
import ish.oncourse.willow.billing.v1.service.SettingsApi


class SettingsApiImpl implements SettingsApi {
    
    @Inject
    private SettingsService settingsService
    
    @Inject
    private EnvironmentService environmentService

    @Inject
    private RequestService requestService

    
    @Override
    SettingsDTO getSettings() {
        SettingsDTO settings = new SettingsDTO()
        
        settings.billingPlan = settingsService.billingPlan
        settings.usersCount = settingsService.usersCount
        settings.invoiceReference = settingsService.invoiceReference
        settings.contactFullName = settingsService.contactFullName
        settings.contactEmail = settingsService.contactEmail
        settings.requestedUsersCount = settingsService.requestedUsersCount
        settings.requestedBillingPlan = settingsService.requestedBillingPlan

        return settings
    }

    @Override
    void updateSettings(SettingsDTO settings) {
        if (settings.invoiceReference) {
            settingsService.invoiceReference = settings.invoiceReference
        }
        settingsService.contactFullName = settings.contactFullName
        settingsService.contactEmail = settings.contactEmail

        //user requested billing plan update
        if (settings.requestedUsersCount && settings.requestedBillingPlan &&
                (settingsService.requestedUsersCount != settings.requestedUsersCount ||
                        settingsService.requestedBillingPlan != settings.requestedBillingPlan)) {
            settingsService.requestedUsersCount = settings.requestedUsersCount
            settingsService.requestedBillingPlan = settings.requestedBillingPlan
            environmentService.collegeBillingChanged(requestService.college.collegeKey)
        }
    }
}
