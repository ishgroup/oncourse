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

package ish.oncourse.server.api.v1.function.export

import ish.oncourse.server.cayenne.SystemUser
import ish.oncourse.server.security.api.IPermissionService
import org.apache.cayenne.CayenneDataObject

/**
 * PrintFilter provides possibility to filter entities before they will go to print
 */
abstract class PrintFilter {

    abstract String getApplyableName()

    abstract boolean apply(CayenneDataObject dataObject, IPermissionService permissionService, SystemUser user)
}
