/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.upgrades.liquibase.change;

import ish.common.types.AccountType;
import ish.liquibase.IshTaskChange;
import ish.oncourse.server.ICayenneService;
import ish.oncourse.server.cayenne.Account;
import ish.oncourse.server.cayenne.Preference;
import ish.oncourse.server.db.SchemaUpdateService;
import liquibase.database.Database;
import liquibase.exception.CustomChangeException;
import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CreateInvoiceDefaultAccountPreference extends IshTaskChange {
    private static final Logger logger = LogManager.getLogger();

    @Override
    public void execute(Database database) throws CustomChangeException {
        ICayenneService cayenneService = SchemaUpdateService.sharedCayenneService;
        DataContext context = cayenneService.getNewContext();

        logger.warn("Running upgrade...");

        var accountId = ObjectSelect.columnQuery(Account.class, Account.ID)
                .where(Account.TYPE.eq(AccountType.INCOME).andExp(Account.IS_ENABLED.eq(true)))
                .orderBy(Account.DESCRIPTION.getName())
                .selectFirst(context);

        Preference preference = context.newObject(Preference.class);
        preference.setName("account.default.invoiceline.id");
        preference.setValueString(accountId != null ? String.valueOf(accountId) : null);
        preference.setUniqueKey("account.default.invoiceline.id");
        context.commitChanges();

        logger.warn("Preference account.default.invoiceline.id with value " + accountId + " added");
    }
}
