/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { User } from "@api/model";
import { Epic } from "redux-observable";

import * as EpicUtils from "../../../../../common/epics/EpicUtils";
import { GET_USERS_REQUEST, GET_USERS_REQUEST_FULFILLED } from "../../../actions";
import UserService from "../services/UsersService";

const request: EpicUtils.Request = {
  type: GET_USERS_REQUEST,
  getData: () => UserService.getUsers(),
  processData: (users: User[]) => {
    return [
      {
        type: GET_USERS_REQUEST_FULFILLED,
        payload: { users }
      }
    ];
  }
};

export const EpicGetUsers: Epic<any, any> = EpicUtils.Create(request);
