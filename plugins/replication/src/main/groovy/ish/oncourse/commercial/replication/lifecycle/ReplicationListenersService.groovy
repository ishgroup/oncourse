/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.commercial.replication.lifecycle

import com.google.inject.Inject
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.PreferenceController
import ish.oncourse.server.license.LicenseService


class ReplicationListenersService {
    @Inject
    ReplicationListenersService(ICayenneService cayenneService, LicenseService licenseService) {
        cayenneService.addListener(new InvoiceLifecycleListener(cayenneService))
        cayenneService.addListener(new QueuedTransactionListener(cayenneService))
        cayenneService.addListener(new CourseClassLifecycleListener (cayenneService))
        cayenneService.addSyncFilter(new QueueableLifecycleListener(cayenneService, licenseService))
    }
}
