/*
 * Copyright ish group pty ltd 2020.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.entity.mixins

import ish.common.types.FieldConfigurationType
import ish.oncourse.API
import ish.oncourse.server.cayenne.ApplicationFieldConfiguration
import ish.oncourse.server.cayenne.EnrolmentFieldConfiguration
import ish.oncourse.server.cayenne.FieldConfigurationScheme
import ish.oncourse.server.cayenne.WaitingListFieldConfiguration

class FieldConfigurationSchemeMixin {

    /**
     * @return the field configuration used for applications
     */
    @API
    static ApplicationFieldConfiguration getApplicationFieldConfiguration(FieldConfigurationScheme self) {
        (ApplicationFieldConfiguration) self.fieldConfigurationLinks.find{ it.fieldConfiguration.type == FieldConfigurationType.APPLICATION }?.fieldConfiguration
    }

    /**
     * @return the field configuration used for enrolments
     */
    @API
    static EnrolmentFieldConfiguration getEnrolFieldConfiguration(FieldConfigurationScheme self) {
        (EnrolmentFieldConfiguration) self.fieldConfigurationLinks.find{ it.fieldConfiguration.type == FieldConfigurationType.ENROLMENT }?.fieldConfiguration
    }

    /**
     * @return the field configuration used for waiting lists
     */
    @API
    static WaitingListFieldConfiguration getWaitingListFieldConfiguration(FieldConfigurationScheme self) {
        (WaitingListFieldConfiguration) self.fieldConfigurationLinks.find{ it.fieldConfiguration.type == FieldConfigurationType.WAITING_LIST }?.fieldConfiguration
    }
}
