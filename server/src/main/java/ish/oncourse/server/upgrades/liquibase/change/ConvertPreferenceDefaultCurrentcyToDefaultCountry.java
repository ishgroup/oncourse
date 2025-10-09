/*
 * Copyright ish group pty ltd 2025.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.upgrades.liquibase.change;

import ish.liquibase.IshTaskChange;
import ish.math.Country;
import ish.oncourse.server.cayenne.Preference;
import ish.oncourse.server.db.SchemaUpdateService;
import ish.persistence.Preferences;
import liquibase.database.Database;
import liquibase.exception.CustomChangeException;
import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ConvertPreferenceDefaultCurrentcyToDefaultCountry extends IshTaskChange {

    private static final Logger logger = LogManager.getLogger();

    @Override
    public void execute(Database database) throws CustomChangeException {

        DataContext context = SchemaUpdateService.sharedCayenneService.getNewContext();
        logger.warn("Running upgrade...");

        Preference pref = ObjectSelect.query(Preference.class)
                .where(Preference.NAME.eq("default.currency"))
                .selectOne(context);

        if (pref != null && StringUtils.isNotBlank(pref.getValueString())) {
            String val = pref.getValueString();
            for (Country c : Country.values()) {
                if (c.currencyCode().equals(val.trim()) || c.currencySymbol() != null && c.currencySymbol().equals(val.trim())) {
                    Preference newPreference = context.newObject(Preference.class);
                    newPreference.setName(Preferences.ACCOUNT_COUNTRY);
                    newPreference.setValueString(c.getDatabaseValue().toString());
                    newPreference.setUniqueKey(Preferences.ACCOUNT_COUNTRY);

                    context.deleteObject(pref);
                    break;
                }
            }
            context.commitChanges();
        }
    }
}
