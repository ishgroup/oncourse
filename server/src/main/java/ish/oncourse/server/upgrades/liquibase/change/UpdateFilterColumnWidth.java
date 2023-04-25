/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.upgrades.liquibase.change;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ish.liquibase.IshTaskChange;
import ish.oncourse.server.ICayenneService;
import ish.oncourse.server.api.v1.model.TableModelDTO;
import ish.oncourse.server.cayenne.Preference;
import ish.oncourse.server.db.SchemaUpdateService;
import liquibase.database.Database;
import liquibase.exception.CustomChangeException;
import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.query.ObjectSelect;

import java.util.List;

public class UpdateFilterColumnWidth extends IshTaskChange {
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void execute(Database database) throws CustomChangeException {
        ICayenneService cayenneService = SchemaUpdateService.sharedCayenneService;
        DataContext context = cayenneService.getNewContext();

        long lastPreferenceId = 0;

        List<Preference> preferences;

        do {
            preferences = ObjectSelect.query(Preference.class)
                    .where(Preference.NAME.startsWith("html.table")
                            .andExp(Preference.ID.gt(lastPreferenceId))
                    )
                    .limit(500)
                    .select(context);

            for (var preference : preferences) {
                if (preference.getValueString() != null) {
                    try {
                        TableModelDTO tableModel = mapper.readValue(preference.getValueString(), TableModelDTO.class);
                        if (tableModel.getFilterColumnWidth() < 240)
                            tableModel.setFilterColumnWidth(240);
                        preference.setValueString(mapper.writeValueAsString(tableModel));
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                }
            }
            context.commitChanges();
            lastPreferenceId = !preferences.isEmpty() ? preferences.get(preferences.size() - 1).getId() : 0L;
        } while (!preferences.isEmpty());
    }
}
