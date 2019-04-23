/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.cayenne.adapter;

import org.apache.cayenne.configuration.server.DbAdapterDetector;
import org.apache.cayenne.dba.DbAdapter;
import org.apache.cayenne.dba.mysql.MySQLAdapter;
import org.apache.cayenne.di.AdhocObjectFactory;
import org.apache.cayenne.di.Inject;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;

public class MariaDbSniffer implements DbAdapterDetector {
    
    private AdhocObjectFactory objectFactory;

    public MariaDbSniffer(@Inject AdhocObjectFactory objectFactory) {
        this.objectFactory = objectFactory;
    }

    @Override
    public DbAdapter createAdapter(DatabaseMetaData md) throws SQLException {
        String dbName = md.getDatabaseProductName();
        if (dbName == null || !dbName.toUpperCase().contains("MARIADB")) {
            return null;
        }

        return objectFactory.newInstance(MySQLAdapter.class, MySQLAdapter.class.getName());
    }
}
