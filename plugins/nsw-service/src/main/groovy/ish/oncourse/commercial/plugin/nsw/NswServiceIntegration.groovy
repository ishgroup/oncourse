/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.commercial.plugin.nsw

import ish.oncourse.server.integration.Plugin
import ish.oncourse.server.integration.PluginTrait

@Plugin(type = 18)
class NswServiceIntegration implements PluginTrait {

    String voucherType
    String channelCode
    String posTerminalId
    String programme
    String apiKey

    NswServiceIntegration(Map args) {
        loadConfig(args)
    }
}
