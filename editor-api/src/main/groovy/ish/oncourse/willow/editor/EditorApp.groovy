package ish.oncourse.willow.editor

import io.bootique.Bootique
import ish.oncourse.configuration.Configuration

/**
 * User: akoiro
 * Date: 13/9/17
 */
class EditorApp {

    static void main(String[] args) {
        Configuration.configure()

        Bootique.app(args).args('--server', '--config=classpath:application.yml')
                .module(EditorApiModule)
                .autoLoadModules().exec().exit()
    }
}
