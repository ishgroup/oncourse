/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { ApiMethods } from "../../model/common/apiHandlers";
import { CHECK_PERMISSIONS_REQUEST_FULFILLED } from "../actions";
import { IAction } from "../actions/IshAction";

export type AccessItem = {
  [method in keyof ApiMethods]: boolean;
};

export interface AccessState {
  [path: string]: AccessItem | boolean;
}

export const accessReducer = (state: AccessState = {}, action: IAction): AccessState => {
  switch (action.type) {
    case CHECK_PERMISSIONS_REQUEST_FULFILLED: {
      const {
        path, method, keyCode, hasAccess
      } = action.payload;

      return {
        ...state,
        ...(path && method ? {[path]: {[method]: hasAccess}} : {}),
        ...(keyCode ? {[keyCode]: hasAccess} : {})
      };
    }

    default:
      return state;
  }
};
