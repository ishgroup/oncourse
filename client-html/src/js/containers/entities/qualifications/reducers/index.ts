/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Qualification } from "@api/model";
import { IAction } from "../../../../common/actions/IshAction";
import {
  CLEAR_PLAIN_QUALIFICATION_ITEMS,
  GET_PLAIN_QUALIFICATION_ITEMS,
  GET_PLAIN_QUALIFICATION_ITEMS_FULFILLED,
  SET_PLAIN_QUALIFICATION_ITEMS_SEARCH
} from "../actions/index";
import { PlainEntityState } from "../../../../model/common/Plain";

export interface QualificationState extends PlainEntityState<Qualification> {}

const initial: QualificationState = {
  items: [],
  search: "",
  loading: false,
  rowsCount: 10000
};

export const qualificationReducer = (state: QualificationState = initial, action: IAction<any>): any => {
  switch (action.type) {
    case GET_PLAIN_QUALIFICATION_ITEMS: {
      return {
        ...state,
        loading: true
      };
    }

    case CLEAR_PLAIN_QUALIFICATION_ITEMS: {
      return {
        ...state,
        items: [],
        loading: false
      };
    }

    case GET_PLAIN_QUALIFICATION_ITEMS_FULFILLED: {
      const { items, offset, pageSize } = action.payload;

      const updated = offset ? state.items.concat(items) : items;

      return {
        ...state,
        loading: false,
        items: updated,
        rowsCount: pageSize < 100 ? pageSize : 5000
      };
    }

    case SET_PLAIN_QUALIFICATION_ITEMS_SEARCH: {
      return {
        ...state,
        ...action.payload
      };
    }

    default:
      return state;
  }
};
