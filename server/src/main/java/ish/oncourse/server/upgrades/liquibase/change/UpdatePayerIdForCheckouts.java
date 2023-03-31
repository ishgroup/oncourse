/*
 * Copyright ish group pty ltd 2023.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.upgrades.liquibase.change;

import groovy.json.JsonSlurper;
import ish.math.Money;
import ish.oncourse.server.ICayenneService;
import ish.oncourse.server.cayenne.Checkout;
import ish.oncourse.server.cayenne.Contact;
import ish.oncourse.server.db.SchemaUpdateService;
import liquibase.database.Database;
import liquibase.exception.CustomChangeException;
import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.query.ObjectSelect;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class UpdatePayerIdForCheckouts extends IshTaskChange {
    @Override
    public void execute(Database database) throws CustomChangeException {
        ICayenneService cayenneService = SchemaUpdateService.sharedCayenneService;
        DataContext context = cayenneService.getNewNonReplicatingContext();

        long lastCheckoutId = 0;

        List<Checkout> checkouts;

        do {
            checkouts = ObjectSelect.query(Checkout.class)
                    .where(Checkout.PAYER.isNull().andExp(Checkout.ID.gt(lastCheckoutId)))
                    .limit(100)
                    .select(context);
            for (var checkout : checkouts) {
                var slurper = new JsonSlurper();
                var parsedCart = (Map) slurper.parseText(checkout.getShoppingCart());
                Long payerId = Long.parseLong((String) parsedCart.get("payerId"));
                var contacts = ObjectSelect.query(Contact.class)
                        .where(Contact.WILLOW_ID.eq(payerId))
                        .select(context);
                if (!contacts.isEmpty()) {
                    checkout.setPayer(contacts.get(0));
                    if (checkout.getTotalValue() == null) {
                        checkout.setTotalValue(new Money((String) parsedCart.get("total")));
                    }
                } else
                    context.deleteObject(checkout);
                context.commitChanges();
            }

            lastCheckoutId = checkouts.isEmpty() ? lastCheckoutId : checkouts.get(checkouts.size() - 1).getId();
        } while (!checkouts.isEmpty());
    }
}
