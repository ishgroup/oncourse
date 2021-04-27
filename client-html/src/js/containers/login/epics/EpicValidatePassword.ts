/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import * as EpicUtils from "../../../common/epics/EpicUtils";
import LoginService from "../services/LoginService";
import { CHECK_PASSWORD_FULFILLED, CHECK_PASSWORD_REQUEST, FETCH_FAIL, FETCH_SUCCESS } from "../../../common/actions";
import { PasswordComplexity } from "@api/model";

const request: EpicUtils.Request = {
  type: CHECK_PASSWORD_REQUEST,
  hideLoadIndicator: true,
  getData: payload => LoginService.checkPassword(payload.value, payload.host, payload.port),
  processData: (passwordComplexity: PasswordComplexity) => {
    return [
      {
        type: CHECK_PASSWORD_FULFILLED,
        payload: {
          passwordComplexity
        }
      },
      {
        type: FETCH_SUCCESS
      }
    ];
  },
  processError: () => {
    return [
      {
        type: CHECK_PASSWORD_FULFILLED,
        payload: {
          passwordComplexity: {}
        }
      },
      {
        type: FETCH_FAIL,
        payload: {
          message: "Failed to check password"
        }
      }
    ];
  }
};

export const EpicValidatePassword: Epic<any, any> = EpicUtils.Create(request);
