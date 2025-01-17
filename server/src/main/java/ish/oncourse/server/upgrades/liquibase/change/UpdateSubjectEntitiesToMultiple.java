/*
 * Copyright ish group pty ltd 2024.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.upgrades.liquibase.change;

import ish.common.types.NodeSpecialType;
import ish.liquibase.IshTaskChange;
import ish.oncourse.server.cayenne.Preference;
import ish.oncourse.server.cayenne.Tag;
import ish.oncourse.server.cayenne.TagRequirement;
import ish.oncourse.server.db.SchemaUpdateService;
import ish.persistence.Preferences;
import liquibase.database.Database;
import liquibase.exception.CustomChangeException;
import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class UpdateSubjectEntitiesToMultiple extends IshTaskChange {
    private static final Logger logger = LogManager.getLogger();

    @Override
    public void execute(Database database) throws CustomChangeException {
        DataContext context = SchemaUpdateService.sharedCayenneService.getNewContext();
        logger.warn("Running tag entities updating...");

        Preference preference = ObjectSelect.query(Preference.class)
                .where(Preference.NAME.eq(Preferences.EXTENDED_SEARCH_TYPES))
                .selectFirst(context);

        if(preference != null && Boolean.parseBoolean(preference.getValueString())) {
            List<TagRequirement> subjectRequirements = ObjectSelect.query(TagRequirement.class)
                    .where(TagRequirement.TAG.dot(Tag.SPECIAL_TYPE).eq(NodeSpecialType.SUBJECTS)
                            .andExp(TagRequirement.TAG.dot(Tag.PARENT_TAG).isNull()))
                    .select(context);

            for (TagRequirement requirement : subjectRequirements) {
                if(!requirement.getManyTermsAllowed())
                    requirement.setManyTermsAllowed(true);
            }

            context.commitChanges();
        }
    }
}
