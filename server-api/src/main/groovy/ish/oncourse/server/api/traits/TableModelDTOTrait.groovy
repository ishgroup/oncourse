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
            ((TableModelDTO) this).columns.eachWithIndex{  entry, int i ->
                if (entry.title != otherModelDto.columns[i].title) {
                    isEqualColumns = false
                    return
                }
                if (entry.attribute != otherModelDto.columns[i].attribute) {
                    isEqualColumns = false
                    return
                }
                if (entry.sortable != otherModelDto.columns[i].sortable) {
                    isEqualColumns = false
                    return
                }
                if ((entry.type == null && otherModelDto.columns[i].type != null) || (entry.type != otherModelDto.columns[i].type)) {
                    isEqualColumns = false
                    return
                }
            }

            return isEqualColumns
        }

        return false
    }

}
