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

import ish.oncourse.server.api.v1.model.DataTypeDTO
import ish.common.types.DataType

trait DataTypeDTOTrait {

    DataType getDbType() {
        switch (this as DataTypeDTO) {
            case DataTypeDTO.TEXT:
                return DataType.TEXT
            case DataTypeDTO.DATE:
                return DataType.DATE
            case DataTypeDTO.DATE_TIME:
                return DataType.DATE_TIME
            case DataTypeDTO.CHECKBOX:
                return DataType.BOOLEAN
            case DataTypeDTO.RECORD:
                return DataType.ENTITY
            case DataTypeDTO.FILE:
                return DataType.FILE
            case DataTypeDTO.MONEY:
                return DataType.MONEY
            case DataTypeDTO.LONG_TEXT:
                return DataType.LONG_TEXT
            case DataTypeDTO.LIST:
                return DataType.LIST
            case DataTypeDTO.MAP:
                return DataType.MAP
            case DataTypeDTO.URL:
                return DataType.URL
            case DataTypeDTO.EMAIL:
                return DataType.EMAIL
            case DataTypeDTO.MESSAGE_TEMPLATE:
                return DataType.MESSAGE_TEMPLATE
            case DataTypeDTO.OBJECT:
                return DataType.OBJECT
            case DataTypeDTO.PATTERN_TEXT:
                return DataType.PATTERN_TEXT
            default:
                throw new IllegalArgumentException("${toString()}")
        }
    }

    DataTypeDTO fromDbType(DataType dataType) {
        if(!dataType) {
            return null
        }
        switch(dataType) {
            case DataType.TEXT:
                return DataTypeDTO.TEXT
            case DataType.DATE:
                return DataTypeDTO.DATE
            case DataType.DATE_TIME:
                return DataTypeDTO.DATE_TIME
            case DataType.BOOLEAN:
                return DataTypeDTO.CHECKBOX
            case DataType.ENTITY:
                return DataTypeDTO.RECORD
            case DataType.FILE:
                return DataTypeDTO.FILE
            case DataType.MONEY:
                return DataTypeDTO.MONEY
            case DataType.LONG_TEXT:
                return DataTypeDTO.LONG_TEXT
            case DataType.LIST:
                return DataTypeDTO.LIST
            case DataType.MAP:
                return DataTypeDTO.MAP
            case DataType.URL:
                return DataTypeDTO.URL
            case DataType.EMAIL:
                return DataTypeDTO.EMAIL
            case DataType.MESSAGE_TEMPLATE:
                return DataTypeDTO.MESSAGE_TEMPLATE
            case DataType.OBJECT:
                return DataTypeDTO.OBJECT
            case DataType.PATTERN_TEXT:
                return DataTypeDTO.PATTERN_TEXT
            default:
                throw new IllegalArgumentException("$dataType.displayName")
        }
    }

}
