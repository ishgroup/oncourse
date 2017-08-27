/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices;

import io.bootique.Bootique;
import io.bootique.cayenne.CayenneModuleProvider;
import io.bootique.jdbc.JdbcModuleProvider;
import io.bootique.jetty.JettyModuleProvider;
import ish.oncourse.configuration.Configuration;
import org.apache.xmlbeans.SystemProperties;

/**
 * User: akoiro
 * Date: 20/8/17
 */
public class ServicesApp {
	public static void main(String[] args) {
		Configuration.configure();
		Bootique bootique = init(args);
		bootique.exec();
	}

	public static Bootique init(String[] args) {
		SystemProperties.getProperty("java.util.logging.manager", "org.apache.logging.log4j.jul.LogManager");
		Bootique bootique = Bootique.app(args).args("--server", "--config=classpath:application.yml");
		bootique.module(new JdbcModuleProvider());
		bootique.module(new CayenneModuleProvider());
		bootique.module(new JettyModuleProvider());
		bootique.module(ServicesModule.class);
		return bootique;
	}
}


