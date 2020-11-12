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

import ish.common.types.ClientOccupationIdentifierType
import ish.oncourse.server.api.v1.model.ClientOccupationIdentifierTypeDTO

trait ClientOccupationIdentifierTypeDTOTrait {

    ClientOccupationIdentifierType getDbType() {
        switch (this as ClientOccupationIdentifierTypeDTO) {
            case ClientOccupationIdentifierTypeDTO.NOT_STATED:
                return ClientOccupationIdentifierType.NOT_SET
            case ClientOccupationIdentifierTypeDTO.MANAGER_1_:
                return ClientOccupationIdentifierType.MANAGER
            case ClientOccupationIdentifierTypeDTO.PROFESSIONALS_2_:
                return ClientOccupationIdentifierType.PROFESSIONALS
            case ClientOccupationIdentifierTypeDTO.TECHNICIANS_AND_TRADES_WORKERS_3_:
                return ClientOccupationIdentifierType.TECHNICIANS
            case ClientOccupationIdentifierTypeDTO.COMMUNITY_AND_PERSONAL_SERVICE_WORKERS_4_:
                return ClientOccupationIdentifierType.COMMUNITY
            case ClientOccupationIdentifierTypeDTO.CLERICAL_AND_ADMINISTRATIVE_WORKERS_5_:
                return ClientOccupationIdentifierType.CLERICAL
            case ClientOccupationIdentifierTypeDTO.SALES_WORKERS_6_:
                return ClientOccupationIdentifierType.SALES
            case ClientOccupationIdentifierTypeDTO.MACHINERY_OPERATORS_AND_DRIVERS_7_:
                return ClientOccupationIdentifierType.MACHINERY
            case ClientOccupationIdentifierTypeDTO.LABOURERS_8_:
                return ClientOccupationIdentifierType.LABOURERS
            default:
                throw new IllegalArgumentException("${toString()}")
        }
    }

    ClientOccupationIdentifierTypeDTO fromDbType(ClientOccupationIdentifierType dataType) {
        if(!dataType) {
            return null
        }
        switch (dataType) {
            case ClientOccupationIdentifierType.NOT_SET:
                return ClientOccupationIdentifierTypeDTO.NOT_STATED
            case ClientOccupationIdentifierType.MANAGER:
                return ClientOccupationIdentifierTypeDTO.MANAGER_1_
            case ClientOccupationIdentifierType.PROFESSIONALS:
                return ClientOccupationIdentifierTypeDTO.PROFESSIONALS_2_
            case ClientOccupationIdentifierType.TECHNICIANS:
                return ClientOccupationIdentifierTypeDTO.TECHNICIANS_AND_TRADES_WORKERS_3_
            case ClientOccupationIdentifierType.COMMUNITY:
                return ClientOccupationIdentifierTypeDTO.COMMUNITY_AND_PERSONAL_SERVICE_WORKERS_4_
            case ClientOccupationIdentifierType.CLERICAL:
                return ClientOccupationIdentifierTypeDTO.CLERICAL_AND_ADMINISTRATIVE_WORKERS_5_
            case ClientOccupationIdentifierType.SALES:
                return ClientOccupationIdentifierTypeDTO.SALES_WORKERS_6_
            case ClientOccupationIdentifierType.MACHINERY:
                return ClientOccupationIdentifierTypeDTO.MACHINERY_OPERATORS_AND_DRIVERS_7_
            case ClientOccupationIdentifierType.LABOURERS:
                return ClientOccupationIdentifierTypeDTO.LABOURERS_8_
            default:
                throw new IllegalArgumentException("$dataType.displayName")
        }
    }

}
