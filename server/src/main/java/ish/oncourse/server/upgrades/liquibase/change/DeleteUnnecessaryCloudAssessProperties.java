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
package ish.oncourse.server.upgrades.liquibase.change;

import ish.oncourse.server.cayenne.IntegrationConfiguration;
import ish.oncourse.server.cayenne.IntegrationProperty;
import ish.oncourse.server.db.SchemaUpdateService;
import liquibase.database.Database;
import liquibase.exception.CustomChangeException;
import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class DeleteUnnecessaryCloudAssessProperties extends IshTaskChange {
    private static final Logger logger = LogManager.getLogger();

    @Override
    public void execute(Database database) throws CustomChangeException {
        DataContext context = SchemaUpdateService.sharedCayenneService.getNewContext();
        logger.warn("Running upgrade DeleteUnnecessaryCloudAssessProperties...");
        List<IntegrationProperty> integrationProperties;
        do {
            integrationProperties = ObjectSelect.query(IntegrationProperty.class)
                    .where(IntegrationProperty.INTEGRATION_CONFIGURATION.dot(IntegrationConfiguration.TYPE).eq(7)
                    .andExp(IntegrationProperty.KEY_CODE.in("orgId","username")))
                    .limit(1000)
                    .select(context);
            if (integrationProperties != null) {
                context.deleteObjects(integrationProperties);
                context.commitChanges();
            }
        } while (integrationProperties != null && !integrationProperties.isEmpty());
    }
}
