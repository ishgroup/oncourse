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

package ish.oncourse.server.integration.tcsi

import groovy.transform.CompileStatic
import ish.oncourse.API
import ish.oncourse.server.integration.PluginTrait
import ish.oncourse.server.scripting.ScriptClosureTrait
import ish.oncourse.server.scripting.ScriptClosure
/**
 *
 * ```
 * tcsi {
 *     name "VET Student Loans integration"
 *      action UPLOAD_STUDENTS
 * }
 * ```
 */
@API
@CompileStatic
@ScriptClosure(key = "tcsi", integration = TCSIIntegration)
class TCSIScriptClosure implements ScriptClosureTrait<TCSIIntegration> {

    Action action

    @Override
    Object execute(TCSIIntegration integration) {
        return null
    }

    static enum Action {
        UPLOAD_STUDENTS;
    }

}
