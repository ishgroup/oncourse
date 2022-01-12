/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.upgrades.liquibase.change;

import ish.oncourse.server.ICayenneService;
import ish.oncourse.server.cayenne.Message;
import ish.oncourse.server.db.SchemaUpdateService;
import liquibase.database.Database;
import liquibase.exception.CustomChangeException;
import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class UpdateMessageBody extends IshTaskChange {

    private static final Logger logger = LogManager.getLogger();

    @Override
    public void execute(Database database) throws CustomChangeException {
        ICayenneService cayenneService = SchemaUpdateService.sharedCayenneService;
        DataContext context = cayenneService.getNewContext();
        logger.warn("Running upgrade...");

        List<Message> emptyBodyMessages = ObjectSelect.query(Message.class)
                .where(Message.EMAIL_BODY.isNull()
                        .orExp(Message.EMAIL_HTML_BODY.isNull())
                        .orExp(Message.SMS_TEXT.isNull())
                        .orExp(Message.POST_DESCRIPTION.isNull()))
                .select(context);

        emptyBodyMessages.forEach(message -> {
            if (message.getEmailBody() == null) {
                message.setEmailBody(StringUtils.EMPTY);
            }
            if (message.getEmailHtmlBody() == null) {
                message.setEmailHtmlBody(StringUtils.EMPTY);
            }
            if (message.getSmsText() == null) {
                message.setSmsText(StringUtils.EMPTY);
            }
            if (message.getPostDescription() == null) {
                message.setPostDescription(StringUtils.EMPTY);
            }
        });

        context.commitChanges();
    }
}
