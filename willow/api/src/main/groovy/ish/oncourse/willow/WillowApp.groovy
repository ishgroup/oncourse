package ish.oncourse.willow

import io.bootique.Bootique
import org.apache.cayenne.configuration.server.ServerRuntime

class WillowApp {
    static void main(String[] args) {
        Bootique bootique = Bootique.app(args).args('--server')
                .module(new WillowApiModule())
                .autoLoadModules()
        WillowApiModule.registerTypes(bootique.createRuntime().getInstance(ServerRuntime))
        bootique.run()
    }
}
