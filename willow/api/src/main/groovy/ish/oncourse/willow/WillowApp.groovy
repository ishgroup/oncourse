package ish.oncourse.willow

import io.bootique.Bootique

class WillowApp {
    static void main(String[] args) {
        Bootique.app(args).args('--server', '--config=classpath:application.yml')
                .module(new WillowApiModule())
                .autoLoadModules()
                .run()
    }
}
