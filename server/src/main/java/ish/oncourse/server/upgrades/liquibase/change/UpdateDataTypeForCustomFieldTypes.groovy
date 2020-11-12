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

package ish.oncourse.server.upgrades.liquibase.change

import ish.common.types.DataType
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.cayenne.CustomFieldType
import ish.oncourse.server.db.SchemaUpdateService
import liquibase.database.Database
import liquibase.exception.CustomChangeException
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.commons.lang3.StringUtils

class UpdateDataTypeForCustomFieldTypes extends IshTaskChange {

    void execute(Database database) throws CustomChangeException {
        ICayenneService cayenneService = SchemaUpdateService.sharedCayenneService

        ObjectContext context = cayenneService.newContext
        ObjectSelect.query(CustomFieldType).select(context).each { customField ->

            String defaultValue = StringUtils.trimToNull(customField.defaultValue)

            if (defaultValue) {
                String[] choices = defaultValue.split(';')
                if (choices.size() > 1) {
                    customField.dataType = DataType.LIST
                    customField.defaultValue = "[${choices.collect {choice -> "{\"value\" :\"$choice\"}"}.join(', ')}]"
                } else {
                    customField.dataType = DataType.TEXT
                }
            } else {
                customField.dataType = DataType.TEXT
            }

        }
        context.commitChanges()

    }
}
