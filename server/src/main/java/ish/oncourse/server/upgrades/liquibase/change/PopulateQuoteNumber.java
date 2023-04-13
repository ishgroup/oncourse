/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.upgrades.liquibase.change;

import ish.liquibase.IshTaskChange;
import ish.oncourse.server.ICayenneService;
import ish.oncourse.server.cayenne.Quote;
import ish.oncourse.server.db.SchemaUpdateService;
import liquibase.database.Database;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.CustomChangeException;
import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class PopulateQuoteNumber extends IshTaskChange {

    private static final Logger logger = LogManager.getLogger();

    @Override
    public void execute(Database database) throws CustomChangeException {
        ICayenneService cayenneService = SchemaUpdateService.sharedCayenneService;
        DataContext context = cayenneService.getNewContext();
        logger.warn("Running upgrade...");

        AtomicReference<Long> nextNumber = new AtomicReference<>(1L);
        List<Quote> quotes = ObjectSelect.query(Quote.class).where(Quote.QUOTE_NUMBER.isNull()).select(context);
        if (!quotes.isEmpty()) {
            logger.warn("Upgrade quotes");
            quotes.forEach( q -> q.setQuoteNumber(nextNumber.getAndSet(nextNumber.get() + 1)));
        }
        context.commitChanges();

        JdbcConnection connection = (JdbcConnection) database.getConnection();
        try (var statement = connection.createStatement()) {
            logger.warn("Upgrade SequenceSupport");
            statement.execute(String.format("INSERT INTO SequenceSupport (tableName, nextId) VALUES('quote', %d)", nextNumber.get()));
            connection.commit();

        } catch (Exception e) {
            logger.catching(e);
            throw new RuntimeException(e);
        }
    }
    
}
