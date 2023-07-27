/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Fetch } from "../../model/common/Fetch";
import { IAction } from "../actions/IshAction";
import { FETCH_CLEAR, FETCH_FAIL, FETCH_FINISH, FETCH_START, FETCH_SUCCESS } from "../actions";

export const fetchReducer = (state: Fetch = {number: 0}, action: IAction<any>): any => {
  switch (action.type) {
    case FETCH_SUCCESS: {
      return {
        ...state,
        message: action.payload && action.payload.message,
        success: true,
        pending: true
      };
    }

    case FETCH_FAIL: {
      const {message, formError, persist} = action.payload;
      return {
        ...state,
        message,
        formError,
        persist,
        success: false,
        pending: true
      };
    }

    case FETCH_START: {
      const {hideIndicator} = action.payload;

      return {
        ...state,
        number: state.number + 1,
        pending: true,
        hideIndicator
      };
    }

    case FETCH_FINISH: {
      const updatedNumber: number = state.number - 1 < 0 ? 0 : state.number - 1;

      return {
        ...state,
        number: updatedNumber,
        pending: Boolean(updatedNumber)
      };
    }

    case FETCH_CLEAR: {
      return {number: 0};
    }

    default:
      return state;
  }
};
