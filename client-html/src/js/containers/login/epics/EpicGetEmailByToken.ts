/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";
import { FETCH_SUCCESS } from "../../../common/actions";
import * as EpicUtils from "../../../common/epics/EpicUtils";
import history from "../../../constants/History";
import { GET_EMAIL_BY_TOKEN_FULFILLED, GET_EMAIL_BY_TOKEN_REQUEST } from "../actions";
import LoginService from "../services/LoginService";

const request: EpicUtils.Request = {
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
  processError: () => {
    history.push('/login');
    return [];
  }
};

export const EpicGetEmailByToken: Epic<any, any> = EpicUtils.Create(request);
