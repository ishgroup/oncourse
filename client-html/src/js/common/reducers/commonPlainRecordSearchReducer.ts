/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { IAction } from "../actions/IshAction";
import {
  CLEAR_COMMON_PLAIN_RECORDS,
  GET_COMMON_PLAIN_RECORDS,
  GET_COMMON_PLAIN_RECORDS_FULFILLED,
  SET_COMMON_PLAIN_RECORD_SEARCH
} from "../actions/CommonPlainRecordsActions";
import { availableEntities, CommonPlainRecordSearchState } from "../../model/common/Plain";

const initial = availableEntities.reduce((p, c) => {
  p[c] = {
    items: [],
    search: "",
    loading: false,
    rowsCount: 5000,
  };
  return p;
}, {});

export const commonPlainRecordSearchReducer = (
  state: CommonPlainRecordSearchState = initial,
  action: IAction
): any => {
  switch (action.type) {
    case SET_COMMON_PLAIN_RECORD_SEARCH: {
      return {
        ...state,
        ...{
          [action.payload.key]: {
            ...state[action.payload.key],
            search: action.payload.search
          }
        }
      };
    }

    case GET_COMMON_PLAIN_RECORDS: {
      return {
        ...state,
        ...{
          [action.payload.key]: {
            ...state[action.payload.key],
            loading: true
          }
        }
      };
    }

    case GET_COMMON_PLAIN_RECORDS_FULFILLED: {
      const {
        key, items, offset, pageSize
      } = action.payload;
      const updated = offset ? state[key].items.concat(items) : items;

      return {
        ...state,
        ...{
          [key]: {
            ...state[key],
            loading: false,
            items: updated,
            rowsCount: pageSize < 100 ? pageSize : 5000
          }
        }
      };
    }

    case CLEAR_COMMON_PLAIN_RECORDS: {
      return {
        ...state,
        ...{
          [action.payload.key]: {
            ...state[action.payload.key],
            items: [],
            search: "",
            loading: action.payload.loading || false,
            rowsCount: 5000
          }
        }
      };
    }

    default:
      return state;
  }
};
