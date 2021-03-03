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

package ish.oncourse.commercial.plugin.tcsi

import groovy.transform.CompileStatic
import ish.oncourse.API
import ish.oncourse.server.cayenne.Enrolment
import ish.oncourse.server.scripting.ScriptClosure
import ish.oncourse.server.scripting.ScriptClosureTrait

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

    private Enrolment enrolment
    
    void enrolment(Enrolment enrolment) {
        this.enrolment = enrolment
    }

    @Override
    Object execute(TCSIIntegration integration) {
        try {
            integration.export(enrolment)
        } catch (TCSIException ignore) {
            //	ignored and allow proceed with other units of study 
        }
        return null
    }
}
