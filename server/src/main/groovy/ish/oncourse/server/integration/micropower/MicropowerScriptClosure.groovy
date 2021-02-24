/*
 * Copyright ish group pty ltd 2020.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.integration.micropower


import ish.oncourse.API
import ish.oncourse.server.cayenne.MembershipProduct
import ish.oncourse.server.scripting.ScriptClosureTrait
import ish.oncourse.server.scripting.ScriptClosure
/**
 * Retrieves new and recently changed profiles from Micropower and adds them to onCourse. Automatically creates
 * memeberships and invoices for the specifiec MemberProduct sku
 *
 * ```
 * micropower {
 *     name "Micropower"
 * }
 * ```
 */

@API
@ScriptClosure(key = "micropower", integration = MicropowerIntegration)
class MicropowerScriptClosure implements ScriptClosureTrait<MicropowerIntegration> {


    @Override
    Object execute(MicropowerIntegration integration) {

        MembershipProduct membershipProduct = integration.getMembershipProduct()

        if (membershipProduct) {
            String lastRun = integration.getLastRunDate()

            def memberChanges = integration.getMemberIds(lastRun)

            memberChanges.each { id ->
                integration.processMember(id, membershipProduct)
            }

        }
        return null
    }
}
