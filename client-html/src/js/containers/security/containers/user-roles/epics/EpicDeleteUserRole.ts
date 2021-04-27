/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import * as EpicUtils from "../../../../../common/epics/EpicUtils";
import UserRolesService from "../services/UserRolesService";
import { DELETE_USER_ROLES_FULFILLED, DELETE_USER_ROLES_REQUEST } from "../../../actions";
import { UserRole } from "@api/model";
import { FETCH_SUCCESS } from "../../../../../common/actions";
import FetchErrorHandler from "../../../../../common/api/fetch-errors-handlers/FetchErrorHandler";

const request: EpicUtils.Request = {
  type: DELETE_USER_ROLES_REQUEST,
  getData: (id: number) => UserRolesService.removeUserRole(id),
  retrieveData: () => UserRolesService.getUserRoles(),
  processData: (userRoles: UserRole[]) => {
    return [
      {
        type: DELETE_USER_ROLES_FULFILLED,
        payload: { userRoles }
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "User Role was successfully deleted" }
      }
    ];
  },
  processError: response => {
    return FetchErrorHandler(response, "Error. User Role was not deleted");
  }
};

export const EpicDeleteUserRole: Epic<any, any> = EpicUtils.Create(request);
