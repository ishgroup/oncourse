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

package ish.oncourse.server.services;

import javax.inject.Inject;
import ish.oncourse.server.ICayenneService;
import ish.oncourse.server.cayenne.CustomFieldType;
import ish.oncourse.server.function.DeleteCustomFieldTypeWithRelatedFields;
import org.apache.cayenne.query.SelectById;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * Created by anarut on 6/30/16.
 */
public class CustomFieldTypeService {

    private static final Logger logger = LogManager.getLogger(CustomFieldTypeService.class);

    @Inject
    private ICayenneService cayenneService;

    public void deleteCustomFieldTypes(List<Long> ids) {

        var context = cayenneService.getNewContext();

        try {
            ids.stream().forEach( id -> {
                var cft = SelectById.query(CustomFieldType.class, id).selectOne(context);
                if (cft != null) {
                    DeleteCustomFieldTypeWithRelatedFields.valueOf(cft, context).deleteFieldTypeWithRelatedFields();
                }
            });

            context.commitChanges();
        } catch (Exception e) {
            logger.error("An exception was thrown when trying to delete CustomFieldTypes.", e);
            context.rollbackChanges();
        }
    }
}
