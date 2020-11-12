/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { IAction } from "../../../../common/actions/IshAction";
import {
  GET_ADMINISTRATION_SITES_FULFILLED,
  GET_SITES_REQUEST,
  GET_VIRTUAL_SITES_FULFILLED,
  SET_PLAIN_SITES,
  SET_PLAIN_SITES_SEARCH
} from "../actions";
import { SiteState } from "./state";

const Initial: SiteState = {
  adminSites: [],
  virualSites: [],
  items: [],
  search: "",
  loading: false,
  rowsCount: 10000
};

export const siteReducer = (state: SiteState = { ...Initial }, action: IAction<any>): any => {
  switch (action.type) {
    case SET_PLAIN_SITES_SEARCH:
    case GET_VIRTUAL_SITES_FULFILLED:
    case GET_ADMINISTRATION_SITES_FULFILLED: {
      return {
        ...state,
        ...action.payload
      };
    }

    case GET_SITES_REQUEST: {
      return {
        ...state,
        loading: true
      };
    }

    case SET_PLAIN_SITES: {
      const {
 items, offset, pageSize, loading
} = action.payload;

      const updated = offset ? state.items.concat(items) : items;

      return {
        ...state,
        loading: Boolean(loading),
        items: updated,
        rowsCount: pageSize < 100 ? pageSize : 5000
      };
    }

    default:
      return state;
  }
};
