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

package ish.oncourse.server.api.traits

import ish.common.types.DeliveryMode
import ish.oncourse.server.api.v1.model.DeliveryModeDTO

trait DeliveryModeDTOTrait {
    DeliveryMode getDbType() {
        switch (this as DeliveryModeDTO) {
            case DeliveryModeDTO.CLASSROOM:
                return DeliveryMode.CLASSROOM
            case DeliveryModeDTO.CLASSROOM_AND_ONLINE:
                return DeliveryMode.CLASSROOM_AND_ONLINE
            case DeliveryModeDTO.CLASSROOM_AND_WORKPLACE:
                return DeliveryMode.CLASSROOM_AND_WORKSPACE
            case DeliveryModeDTO.CLASSROOM_ONLINE_WORKPLACE:
                return DeliveryMode.CLASSROOM_ONLINE_AND_WORKSPACE
            case DeliveryModeDTO.NOT_APPLICABLE_RECOGNITION_OF_PRIOR_LEARNING_CREDIT_TRANSFER:
                return DeliveryMode.NA
            case DeliveryModeDTO.NOT_SET:
                return DeliveryMode.NOT_SET
            case DeliveryModeDTO.ONLINE:
                return DeliveryMode.ONLINE
            case DeliveryModeDTO.ONLINE_AND_WORKPLACE:
                return DeliveryMode.ONLINE_AND_WORKSPACE
            case DeliveryModeDTO.OTHER_DELIVERY:
                return DeliveryMode.OTHER
            case DeliveryModeDTO.WA_INTERNET_SITE_ONLINE_LEARNING_9_:
                return DeliveryMode.WA_INTERNET_SITE
            case DeliveryModeDTO.WA_LOCAL_CLASS_1_:
                return DeliveryMode.WA_LOCAL_CLASS
            case DeliveryModeDTO.WA_REMOTE_CLASS_LIVE_CONFERENCING_2_:
                return DeliveryMode.WA_REMOTE_CLASS
            case DeliveryModeDTO.WA_SELF_PACED_SCHEDULED_3_:
                return DeliveryMode.WA_SELF_PACED_SCHEDULED
            case DeliveryModeDTO.WA_SELF_PACED_UNSCHEDULED_4_:
                return DeliveryMode.WA_SELF_PACED_UNSCHEDULED
            case DeliveryModeDTO.WA_EXTERNAL_CORRESPONDENCE_5_:
                return DeliveryMode.WA_EXTERNAL
            case DeliveryModeDTO.WA_WORKPLACE_6_:
                return DeliveryMode.WA_WORKPLACE
            case DeliveryModeDTO.WA_VIDEO_TELEVISION_LEARNING_8_:
                return DeliveryMode.WA_VIDEO_LEARNING
            case DeliveryModeDTO.WORKPLACE:
                return DeliveryMode.WORKPLACE
            default:
                throw new IllegalArgumentException("${toString()}")
        }
    }

    DeliveryModeDTO fromDbType(DeliveryMode dataType) {
        switch (dataType) {
            case DeliveryMode.CLASSROOM:
                return DeliveryModeDTO.CLASSROOM
            case DeliveryMode.CLASSROOM_AND_ONLINE:
                return DeliveryModeDTO.CLASSROOM_AND_ONLINE
            case DeliveryMode.CLASSROOM_AND_WORKSPACE:
                return DeliveryModeDTO.CLASSROOM_AND_WORKPLACE
            case  DeliveryMode.CLASSROOM_ONLINE_AND_WORKSPACE:
                return DeliveryModeDTO.CLASSROOM_ONLINE_WORKPLACE
            case DeliveryMode.NA:
                return  DeliveryModeDTO.NOT_APPLICABLE_RECOGNITION_OF_PRIOR_LEARNING_CREDIT_TRANSFER
            case DeliveryMode.NOT_SET:
                return DeliveryModeDTO.NOT_SET
            case DeliveryMode.ONLINE:
                return DeliveryModeDTO.ONLINE
            case DeliveryMode.ONLINE_AND_WORKSPACE:
                return DeliveryModeDTO.ONLINE_AND_WORKPLACE
            case DeliveryMode.OTHER:
                return DeliveryModeDTO.OTHER_DELIVERY
            case DeliveryMode.WA_INTERNET_SITE:
                return DeliveryModeDTO.WA_INTERNET_SITE_ONLINE_LEARNING_9_
            case DeliveryMode.WA_LOCAL_CLASS:
                return DeliveryModeDTO.WA_LOCAL_CLASS_1_
            case DeliveryMode.WA_REMOTE_CLASS:
                return DeliveryModeDTO.WA_REMOTE_CLASS_LIVE_CONFERENCING_2_
            case DeliveryMode.WA_SELF_PACED_SCHEDULED:
                return DeliveryModeDTO.WA_SELF_PACED_SCHEDULED_3_
            case DeliveryMode.WA_SELF_PACED_UNSCHEDULED:
                return DeliveryModeDTO.WA_SELF_PACED_UNSCHEDULED_4_
            case DeliveryMode.WA_EXTERNAL:
                return DeliveryModeDTO.WA_EXTERNAL_CORRESPONDENCE_5_
            case DeliveryMode.WA_WORKPLACE:
                return DeliveryModeDTO.WA_WORKPLACE_6_
            case DeliveryMode.WA_VIDEO_LEARNING:
                return DeliveryModeDTO.WA_VIDEO_TELEVISION_LEARNING_8_
            case DeliveryMode.WORKPLACE:
                return DeliveryModeDTO.WORKPLACE
            default:
                throw new IllegalArgumentException("$dataType.displayName")
        }
    }
}
