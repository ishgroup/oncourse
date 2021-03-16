/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import * as EpicUtils from "../../../../../common/epics/EpicUtils";
import UserService from "../services/UsersService";
import { User } from "@api/model";
import { POST_USER_REQUEST, POST_USER_REQUEST_FULFILLED } from "../../../actions";
import { FETCH_SUCCESS } from "../../../../../common/actions";
import FetchErrorHandler from "../../../../../common/api/fetch-errors-handlers/FetchErrorHandler";

const request: EpicUtils.Request = {
  type: POST_USER_REQUEST,
  getData: (user: User) => UserService.updateUser(user),
  retrieveData: () => UserService.getUsers(),
  processData: (users: User[]) => {
    return [
      {
        type: POST_USER_REQUEST_FULFILLED,
        payload: { users }
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "User settings was successfully updated" }
      }
    ];
  },
  processError: response => {
    return FetchErrorHandler(response, "Error. User settings was not updated");
  }
};

export const EpicUpdateUser: Epic<any, any> = EpicUtils.Create(request);
