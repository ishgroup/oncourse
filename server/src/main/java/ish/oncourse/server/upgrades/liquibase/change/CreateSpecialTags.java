/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.upgrades.liquibase.change;

import ish.common.types.NodeSpecialType;
import ish.common.types.NodeType;
import ish.oncourse.cayenne.TaggableClasses;
import ish.oncourse.server.ICayenneService;
import ish.oncourse.server.cayenne.Tag;
import ish.oncourse.server.cayenne.TagRequirement;
import ish.oncourse.server.db.SchemaUpdateService;
import liquibase.database.Database;
import liquibase.exception.CustomChangeException;
import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.Map;

/**
 *  This task is used to create new special tag groups for existing colleges websites
 *  For new colleges use `resources/database/data/node.csv`
 *  Available special tag groups is described in {@link ish.common.types.NodeSpecialType}
 */
public class CreateSpecialTags extends IshTaskChange {

    private static final Logger logger = LogManager.getLogger();

    private static final String DEFAULT_COLOUR = "ebeff5";

    private final Map<NodeSpecialType, TaggableClasses> specialTags =
            Collections.singletonMap(NodeSpecialType.TERMS, TaggableClasses.COURSE_CLASS);

    @Override
    public void execute(Database database) throws CustomChangeException {
        ICayenneService cayenneService = SchemaUpdateService.sharedCayenneService;
        DataContext context = cayenneService.getNewContext();

        logger.warn("Running upgrade...");


        specialTags.forEach( (specialTag, taggableClasses) -> {
            Tag tag = ObjectSelect.query(Tag.class)
                    .where(Tag.NAME.eq(specialTag.getDisplayName())
                            .andExp(Tag.SPECIAL_TYPE.eq(specialTag))
                            .andExp(Tag.NODE_TYPE.eq(NodeType.TAG))
                    )
                    .selectFirst(context);

            if (tag == null) {
                Tag newSpecialTag = context.newObject(Tag.class);
                newSpecialTag.setName(specialTag.getDisplayName());
                newSpecialTag.setNodeType(NodeType.TAG);
                newSpecialTag.setIsWebVisible(true);
                newSpecialTag.setWeight(1);
                newSpecialTag.setSpecialType(specialTag);
                newSpecialTag.setColour(DEFAULT_COLOUR);

                TagRequirement requirement = new TagRequirement();
                requirement.setEntityIdentifier(taggableClasses);
                requirement.setIsRequired(false);
                requirement.setManyTermsAllowed(true);

                newSpecialTag.addToTagRequirements(requirement);

                logger.warn("Create special tag " + newSpecialTag.getName());


            }
        });

        context.commitChanges();
    }
}
