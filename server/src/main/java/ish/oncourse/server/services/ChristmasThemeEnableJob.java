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
import ish.oncourse.server.cayenne.SystemUser;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.ObjectSelect;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.List;
import java.util.stream.Collectors;

@DisallowConcurrentExecution
public class ChristmasThemeEnableJob implements Job {

    private static final String CHRISTMAS_THEME_NAME = "christmas";

    @Inject
    private ICayenneService cayenneService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {

        ObjectContext objectContext = cayenneService.getNewContext();

        var allActiveSystemUsers = ObjectSelect.query(SystemUser.class).where(SystemUser.IS_ACTIVE.isTrue()).select(objectContext);

        var allThemePreferences = ObjectSelect.query(Preference.class)
                .where(Preference.NAME.eq(PreferenceEnumDTO.HTML_GLOBAL_THEME.toString()))
                .select(objectContext);

        var allUsedBeforeThemes = ObjectSelect.query(Preference.class)
                .where(Preference.NAME.eq("html.global.usedBeforeTheme"))
                .select(objectContext);

        for(var user : allActiveSystemUsers) {

            var userThemePreferences = allThemePreferences.stream().filter(t -> t.getUser() != null && user.getId().equals(t.getUser().getId())).collect(Collectors.toList());

            Preference currentThemePreference = null;

            if (userThemePreferences.size() == 0) {
                currentThemePreference = objectContext.newObject(Preference.class);
                currentThemePreference.setUser(user);
                currentThemePreference.setName(PreferenceEnumDTO.HTML_GLOBAL_THEME.toString());
                currentThemePreference.setValueString("default");
            } else {
                currentThemePreference = userThemePreferences.get(0);
                objectContext.deleteObjects(userThemePreferences.subList(1, userThemePreferences.size()));
            }

            var userUsedBeforeThemes = allUsedBeforeThemes.stream().filter(t -> t.getUser() != null && user.getId().equals(t.getUser().getId())).collect(Collectors.toList());

            Preference usedBeforePreference = null;

            if (userUsedBeforeThemes.size() == 0) {
                usedBeforePreference = objectContext.newObject(Preference.class);
                usedBeforePreference.setUser(user);
                usedBeforePreference.setName("html.global.usedBeforeTheme");
            } else {
                usedBeforePreference = userThemePreferences.get(0);
                objectContext.deleteObjects(userUsedBeforeThemes.subList(1, userUsedBeforeThemes.size()));
            }
            usedBeforePreference.setValueString(currentThemePreference.getValueString());

            currentThemePreference.setValueString(CHRISTMAS_THEME_NAME);
        }

        objectContext.commitChanges();
    }
}
