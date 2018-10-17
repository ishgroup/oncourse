package ish.oncourse.website

import io.bootique.Bootique
import io.bootique.cayenne.CayenneModuleProvider
import io.bootique.jdbc.JdbcModuleProvider
import io.bootique.jetty.JettyModuleProvider

import ish.oncourse.configuration.Configuration
import ish.oncourse.log4j.IshLog4jModule

import static ish.oncourse.ui.services.WebProperty.CHECKOUT_VERSION
import static ish.oncourse.ui.services.WebProperty.EDITOR_VERSION

/**
 * User: akoiro
 * Date: 16/9/17
 */
class WebApp {
    static void main(String[] args) {
        System.setProperty("java.util.logging.manager", "org.apache.logging.log4j.jul.LogManager")
        Configuration.configure(CHECKOUT_VERSION, EDITOR_VERSION)
        Bootique bootique = init(args)
        bootique.exec()
    }

    static Bootique init(String[] args) {
        Bootique bootique = Bootique.app(args).args("--server", "--config=classpath:application.yml")
        bootique.module(new JdbcModuleProvider())
        bootique.module(new CayenneModuleProvider())
        bootique.module(new JettyModuleProvider())
        bootique.module(WebModule.class)
        bootique.module(IshLog4jModule.class)
        return bootique
    }
}
