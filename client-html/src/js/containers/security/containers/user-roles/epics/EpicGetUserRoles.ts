/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import * as EpicUtils from "../../../../../common/epics/EpicUtils";
import UserRolesService from "../services/UserRolesService";
import { GET_USER_ROLES_FULFILLED, GET_USER_ROLES_REQUEST } from "../../../actions";
import { UserRole } from "@api/model";

const request: EpicUtils.Request = {
  type: GET_USER_ROLES_REQUEST,
  getData: () => UserRolesService.getUserRoles(),
  processData: (userRoles: UserRole[]) => {
    userRoles.sort((a, b) => (a.name[0] > b.name[0] ? 1 : -1));

    return [
      {
        type: GET_USER_ROLES_FULFILLED,
        payload: { userRoles }
      }
    ];
  }
};

export const EpicGetUserRoles: Epic<any, any> = EpicUtils.Create(request);
