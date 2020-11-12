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

import com.google.inject.Inject;
import ish.oncourse.server.ICayenneService;
import ish.oncourse.server.api.v1.model.PreferenceEnumDTO;
import ish.oncourse.server.cayenne.Preference;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.commons.lang.StringUtils;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.List;

@DisallowConcurrentExecution
public class ChristmasThemeDisableJob implements Job {

    @Inject
    private ICayenneService cayenneService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        ObjectContext objectContext = cayenneService.getNewContext();

        var usedBeforeThemePrefs = ObjectSelect.query(Preference.class)
                .where(Preference.NAME.eq("html.global.usedBeforeTheme"))
                .select(objectContext);

        var allNotChangedChristmasPrefs = ObjectSelect.query(Preference.class)
                .where(Preference.NAME.eq(PreferenceEnumDTO.HTML_GLOBAL_THEME.toString())
                        .andExp(Preference.VALUE_STRING.eq("christmas"))
                        .andExp(Preference.USER.isNotNull()))
                .select(objectContext);

        for (var christmasPref : allNotChangedChristmasPrefs) {

            var oldUserTheme = usedBeforeThemePrefs.stream().filter(old -> old.getUser() != null && old.getUser().getId().equals(christmasPref.getUser().getId())).findFirst().orElse(null);

            if (oldUserTheme != null && oldUserTheme.getValueString() != null && !StringUtils.EMPTY.equals(oldUserTheme.getValueString())) {
                christmasPref.setValueString(oldUserTheme.getValueString());
                oldUserTheme.setValueString(null);
            } else {
                christmasPref.setValueString("default");
            }
        }

        objectContext.commitChanges();
    }
}
