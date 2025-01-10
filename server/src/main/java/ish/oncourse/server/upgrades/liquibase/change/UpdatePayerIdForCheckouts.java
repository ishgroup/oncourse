/*
 * Copyright ish group pty ltd 2023.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.upgrades.liquibase.change;

import ish.liquibase.IshTaskChange;
import liquibase.database.Database;
import liquibase.exception.CustomChangeException;

public class UpdatePayerIdForCheckouts extends IshTaskChange {
    @Override
    public void execute(Database database) throws CustomChangeException {
        /**
         * This upgrade updated payerId column of Checkout from payerId property in shoppingCart json column.
         * After shoppingCart column is deleted from database, this upgrade is useless and cannot be compiled, but
         * also cannot be deleted due to name of its class into upgrades.yml
         */
    }
}
