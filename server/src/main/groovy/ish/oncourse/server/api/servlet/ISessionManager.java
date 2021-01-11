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

package ish.oncourse.server.api.servlet;

import ish.oncourse.server.cayenne.SystemUser;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.Date;


public interface ISessionManager {

    SystemUser getCurrentUser();

    boolean checkConcurrentUsersLimit(SystemUser user, Integer count, Date timeoutThreshold);

    Integer getLoggedInUsersCount(Date timeoutThreshold);

    void disableUserAfterIncorrectLoginAttempts(SystemUser user, Integer allowedNumberOfAttempts, HttpServletRequest request);

    void createUserSession(SystemUser user, Integer timeoutSec, HttpServletRequest request);

    void logout(HttpServletRequest request);

    void doKickOut(SystemUser user);

    void setSessionAttribute(String key, Serializable value);

    Serializable getSessionAttribute(String key);
}
