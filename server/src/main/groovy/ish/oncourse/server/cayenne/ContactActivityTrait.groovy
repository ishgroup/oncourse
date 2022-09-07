/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.cayenne

import ish.oncourse.server.api.v1.model.ContactInteractionDTO
import ish.util.LocalDateUtils

import java.time.LocalDate

trait ContactActivityTrait {
    abstract Date getCreatedOn()

    abstract Long getId()

    abstract String getEntityName()

    String getInteractionDescription(){
        null
    }

    abstract String getInteractionName()

    Date getInteractionDate(){
        createdOn
    }

    ContactInteractionDTO toInteraction(){
        ContactInteractionDTO contactInteractionDTO = new ContactInteractionDTO()
        contactInteractionDTO.setDate(LocalDateUtils.dateToTimeValue(interactionDate))
        contactInteractionDTO.setEntity(entityName)
        contactInteractionDTO.setId(id)
        contactInteractionDTO.setDescription(interactionDescription)
        contactInteractionDTO.setName(interactionName)
        contactInteractionDTO
    }
}