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

import ish.oncourse.types.OutputType
import ish.oncourse.server.api.v1.model.OutputTypeDTO

trait OutputTypeDTOTrait {

    OutputType getDbType() {
        switch (this as OutputTypeDTO) {
            case OutputTypeDTO.CSV:
                return OutputType.CSV
            case OutputTypeDTO.JSON:
                return OutputType.JSON
            case OutputTypeDTO.XML:
                return OutputType.XML
            case OutputTypeDTO.ICS:
                return OutputType.ICAL
            case OutputTypeDTO.TXT:
                return OutputType.TEXT
            case OutputTypeDTO.PDF:
                return OutputType.PDF
            default:
                throw new IllegalArgumentException("${toString()}")
        }
    }

    OutputTypeDTO fromDbType(OutputType outputType) {
        if(!outputType) {
            return null
        }
        switch(outputType) {
            case OutputType.CSV:
                return OutputTypeDTO.CSV
            case OutputType.JSON:
                return OutputTypeDTO.JSON
            case OutputType.XML:
                return OutputTypeDTO.XML
            case OutputType.ICAL:
                return OutputTypeDTO.ICS
            case OutputType.TEXT:
                return OutputTypeDTO.TXT
            case OutputType.PDF:
                return OutputTypeDTO.PDF
            default:
                throw new IllegalArgumentException("$outputType.displayName")
        }

    }

}
