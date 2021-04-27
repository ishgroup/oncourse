/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import * as EpicUtils from "../../../common/epics/EpicUtils";
import { GET_IS_LOGGED_FULFILLED, GET_IS_LOGGED_REQUEST } from "../actions";
import UsersService from "../../security/containers/users/services/UsersService";

const request: EpicUtils.Request = {
  type: GET_IS_LOGGED_REQUEST,
  getData: () => UsersService.isLoggedIn(),
  processData: () => {
    return [
      {
        type: GET_IS_LOGGED_FULFILLED,
        payload: true
      }
    ];
  }
};

export const EpicGetIsLogged: Epic<any, any> = EpicUtils.Create(request);
