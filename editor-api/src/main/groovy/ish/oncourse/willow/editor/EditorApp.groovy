package ish.oncourse.willow.editor

import io.bootique.Bootique
import ish.oncourse.configuration.Configuration
import ish.oncourse.log4j.IshLog4jModule

import static ish.oncourse.willow.editor.EditorProperty.*
import static ish.oncourse.configuration.Configuration.AdminProperty.DEPLOY_SCRIPT_PATH
import static ish.oncourse.configuration.Configuration.AdminProperty.S_ROOT

/**
 * User: akoiro
 * Date: 13/9/17
 */
class EditorApp {

    static void main(String[] args) {
        Configuration.configure(EDIT_SCRIPT_PATH, DEPLOY_SCRIPT_PATH, S_ROOT, SERVICES_LOCATION)

        Bootique.app(args).args('--server', '--config=classpath:application.yml')
                .module(EditorApiModule.class)
                .module(IshLog4jModule.class)
                .autoLoadModules().exec().exit()
    }
}
