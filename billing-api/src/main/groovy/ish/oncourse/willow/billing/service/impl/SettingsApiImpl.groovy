/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.willow.billing.service.impl

import ish.oncourse.willow.billing.settings.SettingsService
import ish.oncourse.willow.billing.v1.model.SettingsDTO
import ish.oncourse.willow.billing.v1.service.SettingsApi

import javax.inject.Inject

class SettingsApiImpl implements SettingsApi {
    
    @Inject
    private SettingsService service
    
    
    @Override
    List<SettingsDTO> getSettings() {
        return null
    }

    @Override
    void updateSettings(SettingsDTO setting) {

    }
}
