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

package ish.oncourse.server;

import io.bootique.Bootique;
import io.bootique.cayenne.CayenneModule;
import io.bootique.jdbc.JdbcModule;
import io.bootique.jdbc.hikaricp.JdbcHikariCPModule;
import ish.oncourse.server.display.DisplayModule;
import ish.oncourse.server.jetty.AngelJettyModule;
import ish.oncourse.server.api.ServerApiModule;
import ish.oncourse.server.api.cxf.CXFModule;
import ish.oncourse.server.bugsnag.BugsnagModule;
import ish.oncourse.server.db.DbModule;
import ish.oncourse.server.document.DocumentModule;
import ish.oncourse.server.http.HttpModule;
import ish.oncourse.server.license.LicenseModule;
import ish.oncourse.server.messaging.SMTPModule;
import ish.oncourse.server.modules.*;
import ish.oncourse.server.security.api.PermissionModule;

public class AngelServer {
    public static final String UTF_8 = "UTF-8";

    public static final String ONCOURSE_CONFIG_PATH = "onCourse.yml";

    public static void main(String[] args) {

        System.getProperty("file.encodin", UTF_8);
        System.getProperty("sun.jnu.encoding", UTF_8);

        Bootique.app()
                //--server command to run jetty server
                .args("--server", "--config=classpath:application.yml", "--config=" + ONCOURSE_CONFIG_PATH)
                .module(AngelModule.class)
                .module(PermissionModule.class)
                .module(CayenneModule.class)
                .module(JdbcModule.class)
                .module(JdbcHikariCPModule.class)
                .module(ServiceModule.class)
                .module(ApiCayenneLayerModule.class)
                .module(ApiServiceModule.class)
                .module(ApiImplementationModule.class)
                .module(CustomServicesModule.class)
                .module(AngelJettyModule.class)
                .module(CXFModule.class)
                .module(ServerApiModule.class)
                .module(BugsnagModule.class)
                .module(HttpModule.class)
                .module(DbModule.class)
                .module(LicenseModule.class)
                .module(DisplayModule.class)
                .module(SMTPModule.class)
                .module(DocumentModule.class)
                .module(JasperReportsModule.class)
                .module(MonitoringModule.class)
                .exec()
                .exit();
    }

}
