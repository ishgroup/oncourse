/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices;

import io.bootique.BQModuleProvider;
import io.bootique.Bootique;
import io.bootique.ConfigModule;
import io.bootique.cayenne.CayenneModuleProvider;
import io.bootique.jdbc.JdbcModuleProvider;
import io.bootique.jdbc.tomcat.JdbcTomcatModuleProvider;
import io.bootique.jetty.JettyModuleProvider;
import ish.oncourse.configuration.Configuration;
import ish.oncourse.webservices.quartz.QuartzModule;

import java.util.LinkedList;
import java.util.List;

import static ish.oncourse.configuration.Configuration.AppProperty.USI_LOCATION;
import static ish.oncourse.configuration.Configuration.ServicesProperty.*;
import static ish.oncourse.solr.SolrProperty.WEBAPP_LOCATION;

/**
 * User: akoiro
 * Date: 20/8/17
 */
public class ServicesApp {
	public static void main(String[] args) {
		Configuration.configure(WEBAPP_LOCATION, USI_LOCATION, SMS_USER, SMS_API, SMS_URL, SMS_PASS);
		Bootique bootique = new BuildBootique().args(args).build();
		bootique.exec();
	}

	public static class BuildBootique {
		private String[] args;
		private List<BQModuleProvider> providers = new LinkedList<>();
		private List<Class<? extends ConfigModule>> modules = new LinkedList<>();

		public BuildBootique() {
			providers.add(new JdbcModuleProvider());
			providers.add(new JdbcTomcatModuleProvider());
			providers.add(new CayenneModuleProvider());
			providers.add(new JettyModuleProvider());
			modules.add(ServicesModule.class);
			modules.add(QuartzModule.class);
		}

		public BuildBootique args(String[] args) {
			this.args = args;
			return this;
		}

		public BuildBootique exclude(Class<? extends ConfigModule> module) {
			modules.remove(module);
			return this;
		}

		public BuildBootique add(Class<? extends ConfigModule> module) {
			modules.add(module);
			return this;
		}

		public Bootique build() {
			System.setProperty("java.util.logging.manager", "org.apache.logging.log4j.jul.LogManager");
			Bootique bootique = Bootique.app(args).args("--server", "--config=classpath:application.yml");
			providers.forEach(bootique::module);
			modules.forEach(bootique::module);
			return bootique;
		}
	}
}


