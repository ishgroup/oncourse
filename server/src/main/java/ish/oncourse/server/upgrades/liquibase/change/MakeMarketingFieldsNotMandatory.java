/*
 * Copyright ish group pty ltd 2025.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.upgrades.liquibase.change;

import ish.liquibase.IshTaskChange;
import ish.oncourse.common.field.FieldProperty;
import ish.oncourse.server.cayenne.Field;
import ish.oncourse.server.db.SchemaUpdateService;
import liquibase.database.Database;
import liquibase.exception.CustomChangeException;
import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MakeMarketingFieldsNotMandatory extends IshTaskChange {

    private static final Logger logger = LogManager.getLogger();

    @Override
    public void execute(Database database) throws CustomChangeException {

        DataContext context = SchemaUpdateService.sharedCayenneService.getNewContext();
        logger.warn("Running upgrade...");

        var marketingFields = ObjectSelect.query(Field.class)
                .where(Field.PROPERTY.in(FieldProperty.IS_MARKETING_VIA_POST_ALLOWED_PROPERTY.getKey(),
                        FieldProperty.IS_MARKETING_VIA_EMAIL_ALLOWED_PROPERTY.getKey(),
                        FieldProperty.IS_MARKETING_VIA_SMS_ALLOWED_PROPERTY.getKey()))
                .select(context);

        for(var field: marketingFields)
            field.setMandatory(false);

        context.commitChanges();
    }
}
