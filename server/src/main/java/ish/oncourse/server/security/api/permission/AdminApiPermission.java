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

package ish.oncourse.server.security.api.permission;

import ish.oncourse.server.cayenne.SystemUser;
import ish.oncourse.server.services.ISystemUserService;

/**
 * Special permission type for paths which only admins have access to.
 */
public class AdminApiPermission extends ResourcePermission {

    private static final String ADMIN_ACCES_ERROR_MESSAGE = "Only users with admin rights can do it. Please contact your administrator";

    @Override
    public PermissionCheckingResult check() {
        var systemUserService = injector.getInstance(ISystemUserService.class);
        var currentUser = systemUserService.getCurrentUser();
        if ((currentUser != null) && (currentUser.getIsAdmin())) {
            return new PermissionCheckingResult(true);
        }
        return new PermissionCheckingResult(false, ADMIN_ACCES_ERROR_MESSAGE);
    }
}
