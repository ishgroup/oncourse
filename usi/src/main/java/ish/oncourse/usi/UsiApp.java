package ish.oncourse.usi;

import io.bootique.Bootique;
import io.bootique.jetty.JettyModuleProvider;
import ish.oncourse.api.cxf.CXFModule;
import ish.oncourse.api.cxf.CXFModuleProvider;
import ish.oncourse.configuration.Configuration;
import ish.oncourse.log4j.IshLog4jModule;

import static ish.oncourse.configuration.Configuration.AppProperty.CREDENTIAL_STORE;
import static ish.oncourse.configuration.Configuration.AppProperty.CREDENTIAL_STORE_PASSWORD;

public class UsiApp {

    public static void main(String[] args) {
        Configuration.configure(CREDENTIAL_STORE_PASSWORD, CREDENTIAL_STORE);

        Bootique.app(args).args("--server", "--config=classpath:application.yml")
                .module(UsiApiModule.class)
                .module(IshLog4jModule.class)
                .module(new JettyModuleProvider())
                .module(new CXFModuleProvider())
                .autoLoadModules().exec().exit();
    }
}
