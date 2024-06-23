/*
 * Copyright ish group pty ltd 2024.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.cayenne

import ish.oncourse.API
import ish.oncourse.server.api.v1.model.ModuleCreditPointsStatusDTO

import static ish.oncourse.server.api.v1.model.ModuleCreditPointsStatusDTO.ACTIVE
import static ish.oncourse.server.api.v1.model.ModuleCreditPointsStatusDTO.ACTIVE_BUT_NOT_VISIBLE_ONLINE
import static ish.oncourse.server.api.v1.model.ModuleCreditPointsStatusDTO.DISABLED

trait ModuleTrait {

    abstract Boolean getIsCreditPointsShownOnWeb()

    abstract Boolean getIsCreditPointsOffered()

    @API
    ModuleCreditPointsStatusDTO getCreditPointsStatus () {
        return !getIsCreditPointsShownOnWeb() && !getIsCreditPointsOffered() ? DISABLED : ! getIsCreditPointsShownOnWeb() ? ACTIVE_BUT_NOT_VISIBLE_ONLINE : ACTIVE
    }

}
