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

import ish.common.types.NodeType;
import ish.oncourse.server.cayenne.Checkout;
import ish.oncourse.server.cayenne.Tag;
import ish.oncourse.server.db.SchemaUpdateService;
import liquibase.database.Database;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.CustomChangeException;
import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class DeleteTagWebpages extends IshTaskChange {
    private static final Logger logger = LogManager.getLogger();

    @Override
    public void execute(Database database) throws CustomChangeException {
        DataContext context = SchemaUpdateService.sharedCayenneService.getNewContext();
        logger.warn("Running tags upgrade...");
        List<Tag> tags;
        do {
            tags = ObjectSelect.query(Tag.class)
                    .where(Tag.NODE_TYPE.eq(NodeType.CHECKLIST))
                    .limit(1000)
                    .select(context);
            if (tags != null) {
                for (var tag : tags) {
                    context.deleteObjects(tag.getTagRequirements());
                    context.deleteObjects(tag.getTagRelations());
                    var childTags = tag.getAllChildren().values();
                    for (var childTag : childTags) {
                        context.deleteObjects(childTag.getTagRelations());
                    }
                    context.deleteObjects(childTags);
                    context.deleteObjects(tag);
                    context.commitChanges();
                }
            }
        } while (tags != null && !tags.isEmpty());
    }
}
