/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import { setSubmitSucceeded } from "redux-form";
import * as EpicUtils from "../../../../../common/epics/EpicUtils";
import UserService from "../services/UsersService";
import { DISABLE_USER_2FA, DISABLE_USER_2FA_FULFILLED } from "../../../actions";
import { FETCH_SUCCESS } from "../../../../../common/actions";
import FetchErrorHandler from "../../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import { User } from "@api/model";

const request: EpicUtils.Request = {
  type: DISABLE_USER_2FA,
  hideLoadIndicator: true,
  getData: (id: number) => UserService.disableTFA(id),
  retrieveData: () => UserService.getUsers(),
  processData: (users: User[]) => {
    return [
      {
        type: DISABLE_USER_2FA_FULFILLED,
        payload: { users }
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "2FA was successfully disabled" }
      },
      setSubmitSucceeded("UsersForm")
    ];
  },
  processError: response => {
    return FetchErrorHandler(response, "Error. 2FA was not disabled");
  }
};

export const EpicDisableUser2FA: Epic<any, any> = EpicUtils.Create(request);
