package ish.oncourse.willow

import io.bootique.Bootique
import ish.oncourse.configuration.Configuration

class WillowApp {
    static void main(String[] args) {

        Configuration.configure()
        
        Bootique.app(args).args('--server', '--config=classpath:application.yml')
                .module(new WillowApiModule())
                .autoLoadModules().exec().exit()
    }
}
