/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.commercial.replication.services

import groovy.transform.CompileStatic

@CompileStatic
class AttendanceOutcomeDedupper extends DFADedupper {

    protected InternalState deleteAfterCreateState() {
        return InternalState.DFA_DELETE
    }

}
