/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
import { IAction } from "../actions/IshAction";
import { CLEAR_MESSAGE, SHOW_MESSAGE } from "../actions";
import { AppMessage } from "../../model/common/Message";

const initial: AppMessage = {
  success: false,
  message: null
};

export const messageReducer = (state: AppMessage = {...initial}, action: IAction<any>): any => {
  switch (action.type) {
    case SHOW_MESSAGE: {
      return {
        ...action.payload
      };
    }
    case CLEAR_MESSAGE: {
      return {};
    }

    default:
      return state;
  }
};
