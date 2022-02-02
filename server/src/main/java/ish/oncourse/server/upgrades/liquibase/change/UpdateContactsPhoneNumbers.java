/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.upgrades.liquibase.change;

import ish.oncourse.server.cayenne.Contact;
import ish.oncourse.server.db.SchemaUpdateService;
import liquibase.database.Database;
import liquibase.exception.CustomChangeException;
import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.exp.Property;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class UpdateContactsPhoneNumbers extends IshTaskChange {
    private static final Logger logger = LogManager.getLogger();
    private static final List<Property<String>> PHONE_PROPERTIES = List.of(Contact.WORK_PHONE, Contact.HOME_PHONE, Contact.MOBILE_PHONE);

    @Override
    public void execute(Database database) throws CustomChangeException {
        DataContext context = SchemaUpdateService.sharedCayenneService.getNewContext();
        logger.warn("Running upgrade...");

        Long currentLastId = 0L;
        List<Contact> contacts;
        do {
            contacts = ObjectSelect.query(Contact.class)
                    .limit(500)
                    .where(Contact.ID.gt(currentLastId))
                    .select(context);
            if (contacts.isEmpty())
                break;
            currentLastId = contacts.get(contacts.size() - 1).getId();
            for (Contact contact : contacts) {
                for (Property<String> phoneProperty : PHONE_PROPERTIES) {
                    processPhoneProperty(contact, phoneProperty);
                }
            }
            context.commitChanges();
            logger.info("Next batch of contact phones updated, last updated record id = " + currentLastId);
        } while (!contacts.isEmpty());
    }

    private static void processPhoneProperty(Contact contact, Property<String> phoneProperty) {
        String phone = (String) contact.getValueForKey(phoneProperty.getName());
        if (phone == null)
            return;
        StringBuilder stringBuilder = new StringBuilder();
        for (char symbol : phone.toCharArray()) {
            if ((symbol >= '0' && symbol <= '9') || symbol == '+')
                stringBuilder.append(symbol);
        }
        contact.setValueForKey(phoneProperty.getName(), stringBuilder.toString());
    }
}
