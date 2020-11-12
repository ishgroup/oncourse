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

package ish.oncourse.server.api.v1.function

import ish.common.types.ApplicationStatus
import ish.common.types.PaymentSource
import ish.oncourse.server.api.BidiMap
import ish.oncourse.server.api.v1.model.ApplicationStatusDTO
import ish.oncourse.server.api.v1.model.SourceDTO

class ApplicationFunctions {

    static final BidiMap<ApplicationStatus, ApplicationStatusDTO> APPLICATION_STATUS_MAP = new BidiMap<ApplicationStatus, ApplicationStatusDTO>() {{
        put(ApplicationStatus.NEW, ApplicationStatusDTO.NEW)
        put(ApplicationStatus.OFFERED, ApplicationStatusDTO.OFFERED)
        put(ApplicationStatus.ACCEPTED, ApplicationStatusDTO.ACCEPTED)
        put(ApplicationStatus.REJECTED, ApplicationStatusDTO.REJECTED)
        put(ApplicationStatus.WITHDRAWN, ApplicationStatusDTO.WITHDRAWN)
        put(ApplicationStatus.IN_PROGRESS, ApplicationStatusDTO.IN_PROGRESS)
    }}

    static final BidiMap<PaymentSource, SourceDTO> APPLICATION_SOURCE_MAP = new BidiMap<PaymentSource, SourceDTO>() {{
        put(PaymentSource.SOURCE_ONCOURSE, SourceDTO.OFFICE)
        put(PaymentSource.SOURCE_WEB, SourceDTO.WEB)
    }}
}
