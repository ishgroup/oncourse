package ish.oncourse.willow.cms

import io.bootique.Bootique
import ish.oncourse.configuration.Configuration

/**
 * User: akoiro
 * Date: 13/9/17
 */
class CmsApp {

    static void main(String[] args) {
        Configuration.configure()

        Bootique.app(args).args('--server', '--config=classpath:application.yml')
                .module(CmsApiModule)
                .autoLoadModules().exec().exit()
    }
}
