/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { LoginRequest, LoginResponse } from "@api/model";
import { Epic } from "redux-observable";
import {
  CLEAR_LAST_LOCATION,
  FETCH_SUCCESS
} from "../../../common/actions";
import * as EpicUtils from "../../../common/epics/EpicUtils";
import { bugsnagClient } from "../../../constants/Bugsnag";
import history from "../../../constants/History";
import { POST_AUTHENTICATION_FULFILLED, POST_AUTHENTICATION_REQUEST } from "../actions";
import LoginService from "../services/LoginService";
import LoginServiceErrorsHandler from "../services/LoginServiceErrorsHandler";

const request: EpicUtils.Request<LoginResponse, { body: LoginRequest, host, port }> = {
  type: POST_AUTHENTICATION_REQUEST,
  getData: payload => LoginService.postLoginRequest(payload.body, payload.host, payload.port),
  processData: (data, state, { body }) => {
    if (bugsnagClient) {
      bugsnagClient.setUser(null, null, body.login);
    }

    if (state.lastLocation && !state.lastLocation.includes("Quit")) {
      // Navigate back
      history.push(state.lastLocation);
    } else {
      history.push("/");
    }

    if (data.lastLoginOn) localStorage.setItem("lastLoginOn", data.lastLoginOn);

    return [
      ...(state.lastLocation
        ? [
            {
              type: CLEAR_LAST_LOCATION
            }
          ]
        : []),
      {
        type: POST_AUTHENTICATION_FULFILLED,
        payload: true
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "You have logged in" }
      },
    ];
  },
  processError: response => LoginServiceErrorsHandler(response)
};

export const EpicPostAuthentication: Epic<any, any> = EpicUtils.Create(request);
