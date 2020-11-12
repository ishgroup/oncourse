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

import ish.common.types.KeyCode;
import ish.common.types.Mask;
import ish.oncourse.server.security.api.IPermissionService;

/**
 * Permission instance which contains all data about permission to one path.
 * Masks described in {@code Mask}.
 * KeyCodes is names of {@code KeyCode} entry.
 */
public class ApiPermission extends ResourcePermission {

    protected int mask;
    protected KeyCode keyCode;
    protected String errorMessage;

    public ApiPermission(String path, String method, String mask, String keyCode, String errorMessage) {
        this.permissionKey = path;
        this.method = method;
        this.mask = getMaskByRepresentation(mask);
        if (keyCode != null) {
            this.keyCode = KeyCode.valueOf(keyCode);
        }
        this.errorMessage = errorMessage;
    }

    private int getMaskByRepresentation(String maskRepresentation) {
        switch (maskRepresentation) {
            case "none":
                return Mask.NONE;
            case "view":
                return Mask.VIEW;
            case "create":
                return Mask.CREATE;
            case "edit":
                return Mask.EDIT;
            case "delete":
                return Mask.DELETE;
            case "print":
                return Mask.PRINT;
            case "all":
                return Mask.ALL;
            default:
                throw new RuntimeException("Undefined mask: " + maskRepresentation);
        }
    }

    public String getPath() {
        return permissionKey;
    }

    public String getMethod() {
        return method;
    }

    public int getMask() {
        return mask;
    }

    public KeyCode getKeyCode(String query) {
        return getKeyCode();
    }

    public KeyCode getKeyCode() {
        return keyCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public PermissionCheckingResult check() {
        var permissionService = injector.getInstance(IPermissionService.class);
        if (permissionService == null) {
            return new PermissionCheckingResult(false, "Permission service isn't initialized!");
        }
        return new PermissionCheckingResult(permissionService.currentUserCan(getKeyCode(), getMask()), errorMessage);
    }
}
