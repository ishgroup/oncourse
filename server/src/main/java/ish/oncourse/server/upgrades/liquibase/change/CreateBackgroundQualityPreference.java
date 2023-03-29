/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.upgrades.liquibase.change;

import ish.liquibase.IshTaskChange;
import ish.oncourse.server.ICayenneService;
import ish.oncourse.server.api.v1.model.PreferenceEnumDTO;
import ish.oncourse.server.cayenne.Preference;
import ish.oncourse.server.db.SchemaUpdateService;
import liquibase.database.Database;
import liquibase.exception.CustomChangeException;
import org.apache.cayenne.access.DataContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CreateBackgroundQualityPreference extends IshTaskChange {
    private static final Logger logger = LogManager.getLogger();

    @Override
    public void execute(Database database) throws CustomChangeException {
        ICayenneService cayenneService = SchemaUpdateService.sharedCayenneService;
        DataContext context = cayenneService.getNewContext();

        logger.warn("Running upgrade...");

        Preference preference = context.newObject(Preference.class);
        preference.setName(PreferenceEnumDTO.BACKGROUND_QUALITY_SCALE.toString());
        preference.setUniqueKey(PreferenceEnumDTO.BACKGROUND_QUALITY_SCALE.toString());
        preference.setValueString(String.valueOf(2));
        context.commitChanges();
    }
}
