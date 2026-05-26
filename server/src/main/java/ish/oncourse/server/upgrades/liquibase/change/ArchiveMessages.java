/*
 * Copyright ish group pty ltd 2025.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.upgrades.liquibase.change;

import ish.liquibase.IshTaskChange;
import ish.oncourse.server.db.SchemaUpdateService;
import liquibase.database.Database;
import liquibase.exception.CustomChangeException;

import java.time.LocalDate;

public class ArchiveMessages extends IshTaskChange {
    @Override
    public void execute(Database database) throws CustomChangeException {
        SchemaUpdateService.archivingMessagesService.archiveMessages(LocalDate.of(2023, 11, 1), true);
    }
}
