/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { IAction } from "../../../../common/actions/IshAction";
import {
  GET_ADMINISTRATION_SITES_FULFILLED,
  GET_VIRTUAL_SITES_FULFILLED
} from "../actions";
import { SiteState } from "./state";

const Initial: SiteState = {
  adminSites: [],
  virualSites: []
};

export const siteReducer = (state: SiteState = { ...Initial }, action: IAction): any => {
  switch (action.type) {
    case GET_VIRTUAL_SITES_FULFILLED:
    case GET_ADMINISTRATION_SITES_FULFILLED: {
      return {
        ...state,
        ...action.payload
      };
    }

    default:
      return state;
  }
};
