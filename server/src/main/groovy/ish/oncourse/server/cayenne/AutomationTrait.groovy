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

package ish.oncourse.server.cayenne

import ish.common.types.AutomationStatus
import org.apache.cayenne.Persistent

trait AutomationTrait implements Persistent {

    abstract Long getId()

    abstract String getKeyCode()

    abstract String getName()

    abstract String getEntity()

    abstract String getBody()

    abstract AutomationStatus getAutomationStatus()

    abstract Date getCreatedOn()

    abstract Date getModifiedOn()

    abstract String getDescription()

    abstract String getShortDescription()

    abstract String getAutomationTags()

    abstract String getCategory()

    abstract List<? extends AutomationBinding> getAutomationBindings()

    abstract Class<? extends AutomationBinding> getAutomationBindingClass()

    List<? extends AutomationBinding> getOptions() {
        automationBindings.findAll {it.value != null}
    }

    List<? extends AutomationBinding> getVariables() {
        automationBindings.findAll {it.value == null}
    }

    abstract void setAutomationStatus(AutomationStatus status)

    abstract void setName(String name)

    abstract void setKeyCode(String keyCode)

    abstract void setEntity(String entity)

    abstract void setBody(String body)

    abstract void setDescription(String description)

    abstract void setShortDescription(String shortDescription)

    abstract void setCategory(String category)

    abstract void setAutomationTags(String automationTags)


}
