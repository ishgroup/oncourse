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

package ish.oncourse.server.services;

import ish.oncourse.server.cayenne.SystemUser;

import java.util.List;

/**
 * Created by anarut on 7/10/17.
 */
public interface ISystemUserService {

    List<SystemUser> getSystemUsersByRole(String name);

    SystemUser getCurrentUser();
}
