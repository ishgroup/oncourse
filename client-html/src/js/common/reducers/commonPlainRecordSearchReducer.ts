/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { IAction } from "../actions/IshAction";
import {
  GET_COMMON_PLAIN_RECORDS,
  GET_COMMON_PLAIN_RECORDS_FULFILLED,
  SET_COMMON_PLAIN_RECORD_SEARCH
} from "../actions/CommonPlainRecordsActions";

export interface CommonPlainSearchEntity {
  entity?: string;
  items?: any[];
  search?: string;
  loading?: boolean;
  rowsCount?: number;
  actions?: (items: any[], offset?: number, pageSize?: number) => any;
}

export interface CommonPlainRecordSearchState {
  [key: string]: CommonPlainSearchEntity;
}

const initial = {
  "": {
    entity: "",
    items: [],
    search: "",
    loading: false,
    rowsCount: 0,
    actions: null
  }
};

export const commonPlainRecordSearchReducer = (
  state: CommonPlainRecordSearchState = initial,
  action: IAction<any>
): any => {
  switch (action.type) {
    case SET_COMMON_PLAIN_RECORD_SEARCH: {
      const entity = action.payload.entity ? action.payload.entity : action.payload.key;
      return {
        ...state,
        ...{
          [action.payload.key]: {
            entity,
            items: [],
            search: action.payload.search,
            loading: false,
            rowsCount: 0,
            actions: action.payload.actions
          }
        }
      };
    }
    case GET_COMMON_PLAIN_RECORDS: {
      const { key } = action.payload;
      return {
        ...state,
        ...{
          [key]: {
            ...state[key],
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
    default:
      return state;
  }
};
