package ish.oncourse.willow.portal

import io.bootique.Bootique

class PortalApp {

    static void main(String[] args) {
        Bootique.app(args).args('--server', '--config=classpath:application.yml')
                .module(PortalModule)
                .autoLoadModules().exec().exit()
    }

}
