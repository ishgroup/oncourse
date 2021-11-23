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
import com.google.inject.Injector;
import io.bootique.annotation.BQConfig;
import io.bootique.jdbc.managed.ManagedDataSourceStarter;
import io.bootique.jdbc.tomcat.TomcatManagedDataSourceFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@BQConfig("Pooling Tomcat JDBC DataSource configuration.")
@JsonTypeName("uri")
public class URIDataSourceFactory extends TomcatManagedDataSourceFactory {
    private static final Logger logger = LogManager.getLogger();

    public ManagedDataSourceStarter create(String dataSourceName, Injector injector) {
        DbUriProvider provider = injector.getInstance(DbUriProvider.class);
        setUrl(provider.getUri());
        logger.warn("server will use database " + getUrl());
        return super.create(dataSourceName, injector);
    }

}
