/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.commercial.plugin.nsw

import ish.oncourse.server.cayenne.Voucher
import ish.oncourse.server.scripting.ScriptClosure
import ish.oncourse.server.scripting.ScriptClosureTrait

@ScriptClosure(key = "service_nsw", integration = ServiceNswIntegration)
class ServiceNswScriptClosure implements ScriptClosureTrait<ServiceNswIntegration>  {

    private String action
    private Voucher voucher

    void action(String action) {
        this.action = action
    }

    void voucher(Voucher voucher) {
        this.voucher = voucher
    }

    @Override
    Object execute(ServiceNswIntegration integration) {
        if (integration.init(voucher)) {
            switch (action) {
                case "validate":
                    integration.validate()
                    break
                case "redeem":
                    try {
                        integration.redeem()
                    } catch (ServiceNswException ignored) { }
                    break
                default:
                    throw new IllegalArgumentException("Unsupported NSW action")
            }
        }
        return null
    }
}