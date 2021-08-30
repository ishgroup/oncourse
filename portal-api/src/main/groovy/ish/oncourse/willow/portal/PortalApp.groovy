package ish.oncourse.willow.portal

import io.bootique.Bootique
import ish.oncourse.configuration.Configuration


class PortalApp {

    static void main(String[] args) {
        Configuration.configure()

        Bootique.app(args).args('--server', '--config=classpath:application.yml')
                .module(PortalModule)
                .autoLoadModules().exec().exit()
    }

}
