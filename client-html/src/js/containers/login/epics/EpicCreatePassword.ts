/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import * as EpicUtils from "../../../common/epics/EpicUtils";
import LoginService from "../services/LoginService";
import {
  FETCH_SUCCESS,
  POST_CREATE_PASSWORD_REQUEST,
  POST_CREATE_PASSWORD_FULFILLED,
} from "../../../common/actions";
import LoginServiceErrorsHandler from "../services/LoginServiceErrorsHandler";
import history from "../../../constants/History";

const request: EpicUtils.Request<any, any, any> = {
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
