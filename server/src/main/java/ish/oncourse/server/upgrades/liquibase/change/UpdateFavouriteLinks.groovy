/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.upgrades.liquibase.change

import com.google.common.base.CaseFormat
import javax.inject.Inject
import ish.liquibase.IshTaskChange
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.cayenne.Preference
import ish.oncourse.server.db.SchemaUpdateService
import ish.oncourse.server.services.ISystemUserService
import liquibase.database.Database
import liquibase.exception.CustomChangeException
import org.apache.cayenne.access.DataContext
import org.apache.cayenne.query.ObjectSelect

import static ish.oncourse.server.preference.UserPreferenceService.JOIN_DELIMETER

class UpdateFavouriteLinks extends IshTaskChange {

    private static final String DASHBOARD_FAVORITE_CATEGORY = "html.dashboard.favorite"

    @Inject
    private ISystemUserService userService

    @Override
    void execute(Database database) throws CustomChangeException {
        ICayenneService cayenneService = SchemaUpdateService.sharedCayenneService
        DataContext context = cayenneService.getNewContext()
        def preferences = ObjectSelect.query(Preference)
                .where(Preference.NAME.eq(DASHBOARD_FAVORITE_CATEGORY))
                .select(context)
        preferences.each {
            it.setValueString(
                    it?.valueString?.split(JOIN_DELIMETER)?.collect {toLowerCamelCase(it)}?.join(JOIN_DELIMETER)
            )
        }
        context.commitChanges()
    }

    private static String toLowerCamelCase(String value){
        CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, value)
    }
}
