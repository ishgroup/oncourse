package ish.oncourse.usi;

import io.bootique.Bootique;
import io.bootique.jetty.JettyModuleProvider;
import ish.oncourse.api.cxf.CXFModuleProvider;
import ish.oncourse.configuration.Configuration;
import ish.oncourse.log4j.IshLog4jModule;


import static ish.oncourse.configuration.Configuration.AppProperty.*;

public class UsiApp {

    public static void main(String[] args) {
        Configuration.configureOnly(HOST, PORT, PATH);
        System.setProperty("com.sun.xml.ws.transport.http.client.HttpTransportPipe.dump", "true");
        System.setProperty("com.sun.xml.internal.ws.transport.http.client.HttpTransportPipe.dump", "true");
        System.setProperty("com.sun.xml.ws.transport.http.HttpAdapter.dump", "true");
        System.setProperty("com.sun.xml.internal.ws.transport.http.HttpAdapter.dump", "true");
        System.setProperty("com.sun.xml.internal.ws.transport.http.HttpAdapter.dumpTreshold", "999999");
        System.setProperty("com.sun.xml.ws.transport.http.HttpAdapter.dumpTreshold", "999999");
        Bootique.app(args).args("--server", "--config=classpath:application.yml")
                .module(UsiApiModule.class)
                .module(IshLog4jModule.class)
                .module(new JettyModuleProvider())
                .module(new CXFModuleProvider())
                .autoLoadModules().exec().exit();
    }
}
