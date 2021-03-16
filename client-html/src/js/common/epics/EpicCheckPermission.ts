/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";
import { PermissionRequest, PermissionResponse } from "@api/model";
import { IAction } from "../actions/IshAction";
import * as EpicUtils from "./EpicUtils";
import AccessService from "../services/AccessService";
import { CHECK_PERMISSIONS_REQUEST, CHECK_PERMISSIONS_REQUEST_FULFILLED } from "../actions";

const request: EpicUtils.Request<PermissionResponse, { permissionRequest: PermissionRequest, onComplete?: IAction[] }> = {
  type: CHECK_PERMISSIONS_REQUEST,
  getData: payload => AccessService.checkPermissions(payload.permissionRequest),
  processData: (
    { hasAccess },
    state,
    { permissionRequest: { path, method, keyCode }, onComplete },
  ) => [
      {
        type: CHECK_PERMISSIONS_REQUEST_FULFILLED,
        payload: {
         path, method, keyCode, hasAccess
        }
      },
      ...hasAccess && onComplete ? onComplete : []
    ]
};

export const EpicCheckPermission: Epic<any, any> = EpicUtils.Create(request);
