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
import ish.oncourse.cayenne.TaggableClasses;
import ish.oncourse.server.ICayenneService;
import ish.oncourse.server.cayenne.Tag;
import ish.oncourse.server.cayenne.TagRequirement;
import ish.oncourse.server.db.SchemaUpdateService;
import liquibase.database.Database;
import liquibase.exception.CustomChangeException;
import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.query.ObjectSelect;

import java.util.Date;
import java.util.List;

public class ExtendSubjectsTagForProducts extends IshTaskChange {
    @Override
    public void execute(Database database) throws CustomChangeException {
        ICayenneService cayenneService = SchemaUpdateService.sharedCayenneService;
        DataContext context = cayenneService.getNewContext();

        var subjectsRootTag = ObjectSelect.query(Tag.class).where(Tag.PARENT_TAG.isNull()
                .andExp(Tag.SPECIAL_TYPE.eq(NodeSpecialType.SUBJECTS)))
                .selectFirst(context);

        var newAllowedEntities = List.of(TaggableClasses.ARTICLE_PRODUCT,
                TaggableClasses.MEMBERSHIP_PRODUCT,
                TaggableClasses.VOUCHER_PRODUCT);

        for(var newAllowedEntity: newAllowedEntities) {
            var tagRequirement = context.newObject(TagRequirement.class);
            tagRequirement.setTag(subjectsRootTag);
            tagRequirement.setEntityIdentifier(newAllowedEntity);
            tagRequirement.setManyTermsAllowed(true);
            tagRequirement.setIsRequired(false);
            tagRequirement.setDisplayRule(null);
            tagRequirement.setCreatedOn(new Date());
            tagRequirement.setModifiedOn(new Date());
        }

        context.commitChanges();
    }
}
