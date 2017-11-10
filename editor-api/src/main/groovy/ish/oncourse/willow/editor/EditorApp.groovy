package ish.oncourse.willow.editor

import io.bootique.Bootique
import ish.oncourse.configuration.Configuration

import static ish.oncourse.willow.editor.EditorProperty.*

/**
 * User: akoiro
 * Date: 13/9/17
 */
class EditorApp {

    static void main(String[] args) {
        Configuration.configure(EDIT_SCRIPT_PATH, DEPLOY_SCRIPT_PATH, S_ROOT)

        Bootique.app(args).args('--server', '--config=classpath:application.yml')
                .module(EditorApiModule)
                .autoLoadModules().exec().exit()
    }
}
