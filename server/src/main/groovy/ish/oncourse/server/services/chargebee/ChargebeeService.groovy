/*
 * Copyright ish group pty ltd 2024.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.services.chargebee

import io.bootique.annotation.BQConfigProperty
import ish.common.chargebee.ChargebeePropertyType
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.PreferenceController
import ish.oncourse.server.cayenne.Preference
import org.apache.cayenne.query.ObjectSelect
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

class ChargebeeService {
    private Boolean localMode = null
    private static final Logger logger = LogManager.getLogger()


    @BQConfigProperty
    void setLocalMode(Boolean localMode) {
        this.localMode = localMode
    }

    Boolean getLocalMode() {
        return localMode
    }


    private ICayenneService cayenneService
    private PreferenceController preferenceController


    String getSubscriptionId(){
        return preferenceController.getChargebeeSubscriptionId()
    }

    List<String> getAllowedAddons() {
        def addons = preferenceController.getChargebeeAllowedAddons()
        if(addons == null)
            return new ArrayList<String>()

        return addons.split(ChargebeePropertyType.ADDONS_SEPARATOR)?.toList()
    }

    String configOf(ChargebeePropertyType type) {
        def preference = preferenceOf(type)
        if(preference == null) {
            logger.error("Attempt to upload $type property to chargebee, but config was not replicated for this college")
            throw new IllegalStateException("Attempt to upload $type property to chargebee, but config was not replicated for this college")
        }

        return preference.getValueString()
    }

    String nullableConfigOf(ChargebeePropertyType type) {
        def preference = preferenceOf(type)
        return preference?.getValueString()
    }

    private Preference preferenceOf(ChargebeePropertyType type) {
        ObjectSelect.query(Preference)
                .where(Preference.NAME.eq(type.getDbPropertyName()))
                .selectOne(cayenneService.newContext)
    }


    ChargebeeService createChargebeeService(ICayenneService cayenneService, PreferenceController preferenceController) {
        this.cayenneService = cayenneService
        this.preferenceController = preferenceController
        this
    }
}
