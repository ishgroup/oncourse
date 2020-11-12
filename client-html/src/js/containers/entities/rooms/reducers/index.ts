/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { IAction } from "../../../../common/actions/IshAction";
import { GET_PLAIN_ROOMS, SET_PLAIN_ROOMS, SET_PLAIN_ROOMS_SEARCH } from "../actions";
import { PlainEntityState } from "../../../../model/common/Plain";
import { SelectItemDefault } from "../../../../model/entities/common";

const Initial: PlainEntityState<SelectItemDefault> = {
  items: [],
  search: "",
  loading: false,
  rowsCount: 10000
};

export const roomReducer = (state: PlainEntityState<SelectItemDefault> = { ...Initial }, action: IAction<any>): any => {
  switch (action.type) {
    case GET_PLAIN_ROOMS: {
      return {
        ...state,
        loading: true
      };
    }

    case SET_PLAIN_ROOMS_SEARCH: {
      return {
        ...state,
        ...action.payload
      };
    }

    case SET_PLAIN_ROOMS: {
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
