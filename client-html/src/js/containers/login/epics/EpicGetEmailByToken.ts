/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";
import * as EpicUtils from "../../../common/epics/EpicUtils";
import LoginService from "../services/LoginService";
import {
  FETCH_SUCCESS,
  GET_EMAIL_BY_TOKEN_REQUEST,
  GET_EMAIL_BY_TOKEN_FULFILLED,
} from "../../../common/actions";
import LoginServiceErrorsHandler from "../services/LoginServiceErrorsHandler";
import history from "../../../constants/History";

const request: EpicUtils.Request<any, any, any> = {
  type: GET_EMAIL_BY_TOKEN_REQUEST,
  getData: payload => LoginService.getEmailByToken(payload.value),
  processData: (responce: string) => {
    return [
      {
        type: GET_EMAIL_BY_TOKEN_FULFILLED,
        payload: { email: responce }
      },
      {
        type: FETCH_SUCCESS,
      }
    ];
  },
  processError: response => {
    history.push('/login');
    return LoginServiceErrorsHandler(response, "Failed to get Email");
  }
};

export const EpicGetEmailByToken: Epic<any, any> = EpicUtils.Create(request);
