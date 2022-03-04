/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.integration.canvas

import groovy.transform.CompileStatic
import ish.oncourse.API
import ish.oncourse.server.scripting.ScriptClosure
import ish.oncourse.server.scripting.ScriptClosureTrait


@API
@CompileStatic
@ScriptClosure(key = "canvas_expire", integration = CanvasIntegration)
class CanvasExpireScriptClosure implements ScriptClosureTrait<CanvasIntegration>{
    Date ends

    def ends(Date ends){
        this.ends = ends
    }

    @Override
    Object execute(CanvasIntegration integration) {
        integration.initAuthHeader()

        return null
    }
}
