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
 * Result of permission checking.
 * Contains isSuccessful and errorMessage.
 */
public class PermissionCheckingResult {

    public static final String EMPTY_ERROR_MESSAGE = "";

    private String errorMessage;
    private boolean successful;

    public PermissionCheckingResult(boolean successful, String errorMessage) {
        this.successful = successful;
        this.errorMessage = errorMessage;
    }

    public PermissionCheckingResult(boolean successful) {
        this.successful = successful;
        this.errorMessage = EMPTY_ERROR_MESSAGE;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public boolean isSuccessful() {
        return successful;
    }

    public static PermissionCheckingResult successful() {
        return new PermissionCheckingResult(true);
    }
}
