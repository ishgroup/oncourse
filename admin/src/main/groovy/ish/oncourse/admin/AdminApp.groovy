package ish.oncourse.admin

import io.bootique.Bootique
import io.bootique.cayenne.CayenneModuleProvider
import io.bootique.jdbc.JdbcModuleProvider
import io.bootique.jetty.JettyModuleProvider
import ish.oncourse.configuration.Configuration

import static ish.oncourse.admin.AdminProperty.S_ROOT;

class AdminApp {
    static void main(String[] args) {
        System.setProperty("java.util.logging.manager", "org.apache.logging.log4j.jul.LogManager")
        Configuration.configure(S_ROOT)
        Bootique bootique = init(args)
        bootique.exec()
    }

    static Bootique init(String[] args) {
        Bootique bootique = Bootique.app(args).args("--server", "--config=classpath:application.yml")
        bootique.module(new JdbcModuleProvider())
        bootique.module(new CayenneModuleProvider())
        bootique.module(new JettyModuleProvider())
        bootique.module(AdminModule.class)
        return bootique
    }
}