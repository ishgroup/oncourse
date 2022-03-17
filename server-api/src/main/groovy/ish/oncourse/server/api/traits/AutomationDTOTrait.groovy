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

import ish.oncourse.server.api.v1.model.AutomationStatusDTO
import ish.oncourse.server.api.v1.model.BindingDTO
import ish.util.LocalDateUtils

import java.time.LocalDateTime

trait AutomationDTOTrait implements _DTOTrait {

    abstract void setId(Long id)

    abstract void setName(String name)

    abstract void setKeyCode(String keyCode)

    abstract void setEntity(String entity)

    abstract void setBody(String body)

    abstract void setStatus(AutomationStatusDTO status)

    abstract void setDescription(String description)

    abstract void setOptions(List<BindingDTO> options)

    abstract void setVariables(List<BindingDTO> variables)

    abstract void setCreatedOn(LocalDateTime createdOn)

    abstract void setModifiedOn(LocalDateTime modifiedOn)

    abstract void setCategory(String category)

    abstract void setAutomationTags(String tags)

    abstract void setShortDescription(String shortDescription)




    void setCreatedOnDate(Date date) {
        setCreatedOn(LocalDateUtils.dateToTimeValue(date))
    }

    void setModifiedOnDate(Date date) {
        setModifiedOn(LocalDateUtils.dateToTimeValue(date))
    }

    abstract String getKeyCode()

    abstract String getName()

    abstract String getEntity()

    abstract String getBody()

    abstract Long getId()

    abstract AutomationStatusDTO getStatus()

    abstract String getDescription()

    abstract List<BindingDTO> getVariables()

    abstract List<BindingDTO> getOptions()

    abstract String getCategory()

    abstract String getAutomationTags()

    abstract String getShortDescription()
}
