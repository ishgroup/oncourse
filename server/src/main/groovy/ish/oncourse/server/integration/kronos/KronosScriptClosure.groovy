/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.integration.kronos

import groovy.transform.CompileStatic
import ish.oncourse.API
import ish.oncourse.server.scripting.ScriptClosure
import ish.oncourse.server.scripting.ScriptClosureTrait

@API
@CompileStatic
@ScriptClosure(key = "kronos", integration = KronosIntegration)
class KronosScriptClosure implements ScriptClosureTrait<KronosIntegration> {


    @Override
    Object execute(KronosIntegration integration) {
        integration.initAuthHeader()

        def employeesV1 = integration.getAllEmployeesV1()
        def employeesV2 = integration.getAllEmployeesV2()

        def employee = integration.getEmployee(29065659)
        def employeeNotFound = integration.getEmployee(29065659111)

        return null
    }
}
