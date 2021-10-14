package ish.oncourse.willow.billing

import io.bootique.Bootique
import ish.oncourse.configuration.Configuration

import static ish.oncourse.configuration.Configuration.AdminProperty.*

class BillingApp {

    static void main(String[] args) {
        Configuration.configure(S_ROOT, STORAGE_ACCESS_ID, STORAGE_ACCESS_KEY, DEPLOY_SCRIPT_PATH, BILLING_UPDATE, CLIENT_ID, API_KEY, IPV4_DIAPASON, IPV6_DIAPASON)

        Bootique.app(args).args('--server', '--config=classpath:application.yml')
                .module(BillingModule)
                .autoLoadModules().exec().exit()
    }

}
