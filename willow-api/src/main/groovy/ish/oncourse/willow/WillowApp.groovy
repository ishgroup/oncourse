package ish.oncourse.willow

import io.bootique.Bootique

class WillowApp {
    static void main(String[] args) {
        Bootique.app(args).args('--server')
                .module(new WillowApiModule())
                .autoLoadModules()
                .run()
    }
}
