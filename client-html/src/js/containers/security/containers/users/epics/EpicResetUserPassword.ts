/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import * as EpicUtils from "../../../../../common/epics/EpicUtils";
import UserService from "../services/UsersService";
import { FETCH_SUCCESS } from "../../../../../common/actions";
import { RESET_USER_PASSWORD, RESET_USER_PASSWORD_FULFILLED } from "../../../actions";
import FetchErrorHandler from "../../../../../common/api/fetch-errors-handlers/FetchErrorHandler";

const request: EpicUtils.Request<any, any, any> = {
  type: RESET_USER_PASSWORD,
  hideLoadIndicator: true,
  getData: (id: number) => UserService.resetPassword(id),
  processData: (newPassword: string) => {
    return [
      {
        type: RESET_USER_PASSWORD_FULFILLED,
        payload: { newPassword }
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "New password was successfully generated" }
      }
    ];
  },
  processError: response => {
    return FetchErrorHandler(response, "Error. Password was not changed");
  }
};

export const EpicResetUserPassword: Epic<any, any> = EpicUtils.Create(request);
