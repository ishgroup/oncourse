/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { ModulesState } from "./state";
import { IAction } from "../../../../common/actions/IshAction";
import { CLEAR_MODULE_ITEMS, GET_MODULE_ITEMS, GET_MODULE_ITEMS_FULFILLED, SET_MODULE_SEARCH } from "../actions";

const initial: ModulesState = {
  items: [],
  search: "",
  loading: false,
  rowsCount: 5000
};

export const modulesReducer = (state: ModulesState = initial, action: IAction<any>): any => {
  switch (action.type) {
    case GET_MODULE_ITEMS: {
      return {
        ...state,
        loading: true
      };
    }

    case CLEAR_MODULE_ITEMS: {
      return {
        ...state,
        items: [],
        loading: action.payload.loading
      };
    }

    case SET_MODULE_SEARCH: {
      return {
        ...state,
        ...action.payload
      };
    }

    case GET_MODULE_ITEMS_FULFILLED: {
      const { items, offset, pageSize } = action.payload;

      const updated = offset ? state.items.concat(items) : items;

      return {
        ...state,
        loading: false,
        items: updated,
        rowsCount: pageSize < 100 ? pageSize : 5000
      };
    }

    default:
      return state;
  }
};
