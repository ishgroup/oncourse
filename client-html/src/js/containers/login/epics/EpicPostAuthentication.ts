/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { LoginRequest } from "@api/model";
import { Epic } from "redux-observable";
import * as EpicUtils from "../../../common/epics/EpicUtils";
import { bugsnagClient } from "../../../constants/Bugsnag";
import LoginService from "../services/LoginService";
import {
  CLEAR_LAST_LOCATION,
  FETCH_SUCCESS,
  POST_AUTHENTICATION_FULFILLED,
  POST_AUTHENTICATION_REQUEST
} from "../../../common/actions";
import LoginServiceErrorsHandler from "../services/LoginServiceErrorsHandler";
import history from "../../../constants/History";

const request: EpicUtils.Request<any, {body: LoginRequest, host, port}> = {
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
      }
    ];
  },
  processError: response => LoginServiceErrorsHandler(response)
};

export const EpicPostAuthentication: Epic<any, any> = EpicUtils.Create(request);
