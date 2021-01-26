package ish.oncourse.willow.billing

import io.bootique.Bootique
import ish.oncourse.configuration.Configuration

import static ish.oncourse.configuration.Configuration.AdminProperty.*

class BillingApp {

    static void main(String[] args) {
        Configuration.configure(S_ROOT, STORAGE_ACCESS_ID, STORAGE_ACCESS_KEY, DEPLOY_SCRIPT_PATH)
        Bootique.app(args).args('--server', '--config=classpath:application.yml')
                .module(BillingModule)
                .autoLoadModules().exec().exit()
    }

}
