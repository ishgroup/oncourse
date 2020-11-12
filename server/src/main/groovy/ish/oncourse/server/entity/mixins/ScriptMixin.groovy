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

import static ish.common.types.TriggerType.CRON
import static ish.common.types.TriggerType.ENTITY_EVENT
import static ish.common.types.TriggerType.ONCOURSE_EVENT
import static ish.common.types.TriggerType.ON_DEMAND
import ish.oncourse.server.cayenne.Script
import ish.scripting.CronExpressionType
import ish.util.DateTimeFormatter
import org.apache.commons.lang3.StringUtils
import org.quartz.CronScheduleBuilder
import org.quartz.TriggerBuilder

class ScriptMixin {

    static String getNextRun(Script self) {
        if (self.enabled) {
            switch (self.triggerType) {
                case CRON:
                    return CronExpressionType.values().find { it.databaseValue == self.cronSchedule }?.displayName?:
                            (DateTimeFormatter.formatDateTime(
                                    TriggerBuilder.newTrigger()
                                            .withSchedule(CronScheduleBuilder.cronSchedule(self.cronSchedule))
                                            .build()
                                            .getFireTimeAfter(new Date()),
                                    TimeZone.getDefault()))
                case ENTITY_EVENT:
                    return "$self.entityClass ${self.entityEventType.displayName.toLowerCase()}"
                case ONCOURSE_EVENT:
                    return "on ${self.systemEventType.displayName.toLowerCase()} event"
                case ON_DEMAND:
                    return ON_DEMAND.displayName
                default:
                    break
            }
        }
        return StringUtils.EMPTY
    }
}
