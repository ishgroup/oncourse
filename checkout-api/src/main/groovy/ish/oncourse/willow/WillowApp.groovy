package ish.oncourse.willow

import groovy.transform.CompileStatic
import io.bootique.Bootique
import ish.oncourse.configuration.Configuration
import ish.oncourse.log4j.IshLog4jModule

@CompileStatic
class WillowApp {
    static void main(String[] args) {

        Configuration.configure()
        
        Bootique.app(args).args('--server', '--config=classpath:application.yml')
                .module(new WillowApiModule())
                .module(IshLog4jModule.class)
                .autoLoadModules().exec().exit()
    }
}
