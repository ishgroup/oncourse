package ish.oncourse.usi;

import io.bootique.Bootique;
import io.bootique.jetty.JettyModuleProvider;
import ish.oncourse.api.cxf.CXFModule;
import ish.oncourse.api.cxf.CXFModuleProvider;
import ish.oncourse.configuration.Configuration;
import ish.oncourse.log4j.IshLog4jModule;

import java.util.Properties;

import static ish.oncourse.configuration.Configuration.AppProperty.*;

public class UsiApp {

    public static void main(String[] args) {
        Configuration.configureOnly(HOST, PORT, PATH);

        Bootique.app(args).args("--server", "--config=classpath:application.yml")
                .module(UsiApiModule.class)
                .module(IshLog4jModule.class)
                .module(new JettyModuleProvider())
                .module(new CXFModuleProvider())
                .autoLoadModules().exec().exit();
    }
}
