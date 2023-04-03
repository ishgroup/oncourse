/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.configs

import com.fasterxml.jackson.annotation.JsonInclude
import ish.oncourse.server.cayenne.Script


@JsonInclude(JsonInclude.Include.NON_NULL)
class ScriptModel extends AutomationModel{
    private String entityClass
    private String entityAttribute
    private String entityEventType
    private String triggerType
    private String outputType
    private String onCourseEventType

    ScriptModel(Script script) {
        super(script)

        this.entityClass = script.entityClass
        this.entityAttribute = script.entityAttribute
        this.entityEventType = script.entityEventType?.name()
        this.triggerType = script.triggerType?.name()
        this.outputType = script.outputType?.name()
        this.onCourseEventType = script.systemEventType?.name()
    }

    String getEntityClass() {
        return entityClass
    }

    String getEntityAttribute() {
        return entityAttribute
    }

    String getEntityEventType() {
        return entityEventType
    }

    String getTriggerType() {
        return triggerType
    }

    String getOutputType() {
        return outputType
    }

    String getOnCourseEventType() {
        return onCourseEventType
    }
}
