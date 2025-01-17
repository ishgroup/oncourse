/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";
import { FETCH_SUCCESS } from "../../../common/actions";
import * as EpicUtils from "../../../common/epics/EpicUtils";
import history from "../../../constants/History";
import { POST_CREATE_PASSWORD_FULFILLED, POST_CREATE_PASSWORD_REQUEST } from "../actions";
import LoginService from "../services/LoginService";
import LoginServiceErrorsHandler from "../services/LoginServiceErrorsHandler";

const request: EpicUtils.Request = {
  type: POST_CREATE_PASSWORD_REQUEST,
  getData: payload => LoginService.createPasswordByToken(payload.token, payload.password),
  processData: () => {
    history.push("/login");

    return [
      {
        type: POST_CREATE_PASSWORD_FULFILLED
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "Password has been successfully created" }
      },
    ];
  },
  processError: response => LoginServiceErrorsHandler(response, "Failed to create password")
};

export const EpicCreatePassword: Epic<any, any> = EpicUtils.Create(request);
