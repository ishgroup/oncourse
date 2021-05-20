/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import * as EpicUtils from "../../../common/epics/EpicUtils";
import { GET_COMPLEX_PASS_FILLED, GET_COMPLEX_PASS_REQUEST } from "../actions";
import UsersService from "../../security/containers/users/services/UsersService";

const request: EpicUtils.Request = {
  type: GET_COMPLEX_PASS_REQUEST,
  getData: () => UsersService.requireComplexPass(),
  processData: (complexPass: boolean) => {
    return [
      {
        type: GET_COMPLEX_PASS_FILLED,
        payload: complexPass
      }
    ];
  }
};

export const EpicGetComplexPath: Epic<any, any> = EpicUtils.Create(request);
