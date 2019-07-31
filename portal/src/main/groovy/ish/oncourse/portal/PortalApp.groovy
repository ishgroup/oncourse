package ish.oncourse.portal

import io.bootique.Bootique
import io.bootique.cayenne.CayenneModuleProvider
import io.bootique.jdbc.JdbcModuleProvider
import io.bootique.jdbc.tomcat.JdbcTomcatModuleProvider
import io.bootique.jetty.JettyModuleProvider
import ish.oncourse.configuration.Configuration

/**
 * User: akoiro
 * Date: 16/9/17
 */
class PortalApp {
    static void main(String[] args) {
        System.setProperty("java.util.logging.manager", "org.apache.logging.log4j.jul.LogManager")
        Configuration.configure()
        Bootique bootique = init(args)
        bootique.exec()
    }

    static Bootique init(String[] args) {
        Bootique bootique = Bootique.app(args).args("--server", "--config=classpath:application.yml")
        bootique.module(new JdbcModuleProvider())
        bootique.module(new JdbcTomcatModuleProvider())
        bootique.module(new CayenneModuleProvider())
        bootique.module(new JettyModuleProvider())
        bootique.module(PortalModule.class)
        return bootique
    }
}
