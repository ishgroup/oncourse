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
        boolean isEqualTitles = false
        boolean isEqualAttributes = false
        boolean isEqualSortable = true
        if (((TableModelDTO) this).columns.size() == otherModelDto.columns.size()) {

            if (((TableModelDTO) this).columns.collect { column -> column.title }
                    .containsAll(otherModelDto.columns.collect { otherColumn -> otherColumn.title })) {
                isEqualTitles =  true
            }

            if (((TableModelDTO) this).columns.collect { column -> column.attribute }
                    .containsAll(otherModelDto.columns.collect { otherColumn -> otherColumn.attribute })) {
                isEqualAttributes = true
            }

            def current = ((TableModelDTO) this).columns.collect { column -> column.sortable }
            def other =  otherModelDto.columns.collect { column -> column.sortable }
            current.eachWithIndex{ boolean entry, int i ->
                if (entry != other[i]) {
                    isEqualSortable = false
                    return
                }
            }

            if (isEqualTitles && isEqualAttributes && isEqualSortable) {
                return true
            }
        }

        return false
    }

}
