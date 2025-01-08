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

package ish.oncourse.server.db;

import com.fasterxml.jackson.annotation.JsonTypeName;
import io.bootique.di.Injector;
import io.bootique.annotation.BQConfig;
import io.bootique.jdbc.managed.ManagedDataSourceStarter;
import io.bootique.jdbc.hikaricp.HikariCPManagedDataSourceFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@BQConfig("Pooling Hikari JDBC DataSource configuration.")
@JsonTypeName("uri")
public class URIDataSourceFactory extends HikariCPManagedDataSourceFactory {

    private static final Logger logger = LogManager.getLogger();

    public ManagedDataSourceStarter create(String dataSourceName, Injector injector) {
        DbUriProvider provider = injector.getInstance(DbUriProvider.class);
        setJdbcUrl(provider.getUri());
        logger.warn("Server will use database " + getJdbcUrl());
        return super.create(dataSourceName, injector);
    }

}
