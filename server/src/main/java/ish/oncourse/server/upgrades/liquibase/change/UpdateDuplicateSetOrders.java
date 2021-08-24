/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.upgrades.liquibase.change;

import ish.oncourse.server.cayenne.CustomFieldType;
import ish.oncourse.server.db.SchemaUpdateService;
import liquibase.database.Database;
import liquibase.exception.CustomChangeException;
import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.query.ObjectSelect;

import java.util.concurrent.atomic.AtomicLong;

public class UpdateDuplicateSetOrders extends IshTaskChange {

    @Override
    public void execute(Database database) throws CustomChangeException {

        DataContext context = SchemaUpdateService.sharedCayenneService.getNewContext();

        AtomicLong currentIndex = new AtomicLong();
        ObjectSelect.query(CustomFieldType.class)
                .orderBy(CustomFieldType.SORT_ORDER.asc())
                .select(context).forEach(customFieldType -> {
                    if (customFieldType.getSortOrder() != currentIndex.get()) {
                        customFieldType.setSortOrder(currentIndex.get());
                    }
                    currentIndex.incrementAndGet();
                });
        context.commitChanges();
    }
}
