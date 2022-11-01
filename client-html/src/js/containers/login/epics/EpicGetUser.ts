/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { Epic } from "redux-observable";
import * as EpicUtils from "../../../common/epics/EpicUtils";
import LoginService from "../services/LoginService";
import {
  GET_SYSTEM_USER_DATA,
  setSystemUserData
} from "../../../common/actions";
import LoginServiceErrorsHandler from "../services/LoginServiceErrorsHandler";

const request: EpicUtils.Request = {
  type: GET_SYSTEM_USER_DATA,
  getData: () => LoginService.getUser(),
  processData: user => [setSystemUserData(user)],
  processError: response => LoginServiceErrorsHandler(response, "Failed to get system user")
};

export const EpicGetUser: Epic<any, any> = EpicUtils.Create(request);
