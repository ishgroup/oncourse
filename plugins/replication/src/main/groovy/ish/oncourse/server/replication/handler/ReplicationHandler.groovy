/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.server.replication.handler

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

interface ReplicationHandler {

    Logger logger = LogManager.getLogger()

    void replicate()
}