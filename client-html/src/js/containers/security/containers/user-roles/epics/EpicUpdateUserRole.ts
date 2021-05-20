/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import * as EpicUtils from "../../../../../common/epics/EpicUtils";
import UserRolesService from "../services/UserRolesService";
import { POST_USER_ROLES_FULFILLED, POST_USER_ROLES_REQUEST } from "../../../actions";
import { UserRole } from "@api/model";
import { FETCH_SUCCESS } from "../../../../../common/actions";
import FetchErrorHandler from "../../../../../common/api/fetch-errors-handlers/FetchErrorHandler";

const request: EpicUtils.Request = {
  type: POST_USER_ROLES_REQUEST,
  getData: (userRole: UserRole) => UserRolesService.updateUserRole(userRole),
  retrieveData: () => UserRolesService.getUserRoles(),
  processData: (userRoles: UserRole[]) => {
    return [
      {
        type: POST_USER_ROLES_FULFILLED,
        payload: { userRoles }
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "User Role was successfully saved" }
      }
    ];
  },
  processError: response => {
    return FetchErrorHandler(response, "Error. User Role was not saved");
  }
};

export const EpicUpdateUserRole: Epic<any, any> = EpicUtils.Create(request);
