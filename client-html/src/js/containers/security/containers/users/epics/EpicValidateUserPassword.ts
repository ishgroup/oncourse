/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import { stopAsyncValidation } from "redux-form";
import * as EpicUtils from "../../../../../common/epics/EpicUtils";
import LoginService from "../../../../login/services/LoginService";
import { VALIDATE_USER_PASSWORD, VALIDATE_USER_PASSWORD_FULFILLED } from "../../../actions";
import { PasswordComplexity } from "@api/model";

const request: EpicUtils.Request = {
  type: VALIDATE_USER_PASSWORD,
  hideLoadIndicator: true,
  getData: (value: string) => LoginService.checkPassword(value),
  processData: (userPasswordComplexity: PasswordComplexity) => {
    return [
      {
        type: VALIDATE_USER_PASSWORD_FULFILLED,
        payload: { userPasswordComplexity }
      },
      stopAsyncValidation(
        "UsersForm",
        userPasswordComplexity.score < 2 ? { password: userPasswordComplexity.feedback } : undefined
      )
    ];
  }
};

export const EpicValidateUserPassword: Epic<any, any> = EpicUtils.Create(request);
