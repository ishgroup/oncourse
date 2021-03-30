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

import ish.oncourse.server.api.v1.model.TableModelDTO

trait TableModelDTOTrait {

    boolean equalsByColumns(TableModelDTO otherModelDto) {
        boolean isEqualColumns = true
        if (((TableModelDTO) this).columns.size() == otherModelDto.columns.size()) {

            if (!((TableModelDTO) this).columns.collect { column -> column.title }
                    .containsAll(otherModelDto.columns.collect { otherColumn -> otherColumn.title })) {
                return false
            }

            ((TableModelDTO) this).columns.each {  entry ->
                def otherColumn = otherModelDto.columns.find { it.title == entry.title }
                if (entry.attribute != otherColumn.attribute) {
                    isEqualColumns = false
                    return
                }
                if (entry.sortable != otherColumn.sortable) {
                    isEqualColumns = false
                    return
                }
                if ((entry.type == null && otherColumn.type != null) || (entry.type != otherColumn.type)) {
                    isEqualColumns = false
                    return
                }
            }

            return isEqualColumns
        }

        return false
    }

}
