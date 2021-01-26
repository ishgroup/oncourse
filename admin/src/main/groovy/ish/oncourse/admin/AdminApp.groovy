package ish.oncourse.admin

import io.bootique.Bootique
import io.bootique.cayenne.CayenneModuleProvider
import io.bootique.jdbc.JdbcModuleProvider
import io.bootique.jdbc.tomcat.JdbcTomcatModuleProvider
import io.bootique.jetty.JettyModuleProvider
import ish.oncourse.configuration.Configuration

import static ish.oncourse.configuration.Configuration.AdminProperty.*

class AdminApp {
    static void main(String[] args) {
        System.setProperty("java.util.logging.manager", "org.apache.logging.log4j.jul.LogManager")
        System.setProperty("org.apache.cxf.stax.maxChildElements", "100000")
        Configuration.configure(S_ROOT, STORAGE_ACCESS_ID, STORAGE_ACCESS_KEY, DEPLOY_SCRIPT_PATH)
        Bootique bootique = init(args)
        bootique.exec()
    }

    static Bootique init(String[] args) {
        Bootique bootique = Bootique.app(args).args("--server", "--config=classpath:application.yml")
        bootique.module(new JdbcModuleProvider())
        bootique.module(new CayenneModuleProvider())
        bootique.module(new JettyModuleProvider())
        bootique.module(new JdbcTomcatModuleProvider())
        bootique.module(AdminModule.class)
        return bootique
    }
}