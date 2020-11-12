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

package ish.oncourse.server.security.api;

import ish.common.types.KeyCode;
import ish.oncourse.server.cayenne.SystemUser;
import ish.oncourse.server.security.api.permission.PermissionCheckingResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Service for authorization and checking permissions for ish API methods.
 */
public interface IPermissionService {

    /**
     * Checks if the current system user can be authorized with some servlet request.
     * If user doesn't have permission for that path, put in response 403 error.
     * @param servletRequest request which will be used for authorizing.
     * @param servletResponse response which will be used for authorizing.
     * @return true if user has access for path, false if doesn't.
     */
    boolean authorize(HttpServletRequest servletRequest, HttpServletResponse servletResponse) throws IOException;

    /**
     * Checks if the current system user has access to some path.
     * @param path - path to check.
     * @param method - method to check
     * @return result of permissions checking.
     */
    PermissionCheckingResult hasAccess(String path, String method);

    /**
     * Checks that user which was returned from ISystemUserService implementation can do action with some
     * keyCode and mask.
     * @param keyCode of operation which will be checked
     * @param mask of operation which will be checked
     * @return true if user can do action with mask on some keyCode entity, false - if not.
     */
    boolean currentUserCan(KeyCode keyCode, int mask);

    boolean userCan(SystemUser user, KeyCode keyCode, int mask);

}
