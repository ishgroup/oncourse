/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { AssessmentsState } from "./state";
import { IAction } from "../../../../common/actions/IshAction";
import {
 CLEAR_ASSESSMENT_ITEMS, GET_ASSESSMENT_ITEMS, GET_ASSESSMENT_ITEMS_FULFILLED, SET_ASSESSMENT_SEARCH
} from "../actions";

const initial: AssessmentsState = {
  items: [],
  search: "",
  loading: false,
  rowsCount: 5000
};

export const assessmentsReducer = (state: AssessmentsState = initial, action: IAction<any>): any => {
  switch (action.type) {
    case GET_ASSESSMENT_ITEMS: {
      return {
        ...state,
        loading: true
      };
    }

    case CLEAR_ASSESSMENT_ITEMS: {
      return {
        ...state,
        items: [],
        loading: false
      };
    }

    case SET_ASSESSMENT_SEARCH: {
      return {
        ...state,
        ...action.payload
      };
    }

    case GET_ASSESSMENT_ITEMS_FULFILLED: {
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
