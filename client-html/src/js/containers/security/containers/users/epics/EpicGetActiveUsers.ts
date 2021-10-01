/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { Epic } from "redux-observable";
import * as EpicUtils from "../../../../../common/epics/EpicUtils";
import UserService from "../services/UsersService";
import { GET_ACTIVE_USERS_REQUEST, GET_ACTIVE_USERS_REQUEST_FULFILLED } from "../../../actions";

const request: EpicUtils.Request = {
  type: GET_ACTIVE_USERS_REQUEST,
  getData: () => UserService.getActiveUsers(),
  processData: activeUsers => [
      {
        type: GET_ACTIVE_USERS_REQUEST_FULFILLED,
        payload: { activeUsers }
      }
    ]
};

export const EpicGetActiveUsers: Epic<any, any> = EpicUtils.Create(request);
