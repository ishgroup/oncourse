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

package ish.oncourse.server.security.api.permission.custom

import io.bootique.jetty.servlet.AngelServletEnvironment
import ish.common.types.KeyCode
import ish.common.types.Mask
import ish.oncourse.server.security.api.IPermissionService
import ish.oncourse.server.security.api.permission.PermissionCheckingResult
import ish.oncourse.server.security.api.permission.ResourcePermission
import static org.apache.commons.lang3.StringUtils.trimToEmpty

class EmailMassCheckPermission extends ResourcePermission {

    private static final String PARAM_NAME = 'entityIds'
    String permissionKey
    String method

    @Override
    String getPermissionKey() {
        return super.getPermissionKey()
    }

    EmailMassCheckPermission(String path, String method) {
        this.permissionKey = path
        this.method = method
    }

    @Override
    PermissionCheckingResult check() {
        IPermissionService permissionService = injector.getInstance(IPermissionService)
        if (!permissionService.currentUserCan(KeyCode.SPECIAL_EMAIL_MASS, Mask.VIEW)) {
            String queryString = injector.getInstance(AngelServletEnvironment)?.request()?.get()?.queryString
            if (trimToEmpty(queryString).contains(PARAM_NAME)) {
                String values = queryString.split('&').find{ it.contains(PARAM_NAME) }.split('=')[1]
                if (values.split(',').size() > 50) {
                    return new PermissionCheckingResult(false, "Cannot send more then 50 emails at once.")
                }
            }
        }

        PermissionCheckingResult.successful()
    }
}
