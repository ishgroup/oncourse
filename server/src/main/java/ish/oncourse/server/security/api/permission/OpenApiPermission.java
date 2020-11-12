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

/**
 * ResourcePermission implementation for API which doesn't check permissions.
 * Defines API that has UNDEFINED KeyCode.
 */
public final class OpenApiPermission extends ApiPermission {

    private static final String NONE_MASK = "none";

    public OpenApiPermission(String path, String method) {
        super(path, method, NONE_MASK, null, PermissionCheckingResult.EMPTY_ERROR_MESSAGE);
    }

    @Override
    public PermissionCheckingResult check() {
        return new PermissionCheckingResult(true);
    }
}
