/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { ConfirmState } from "ish-ui";
import { SendMessageState } from "../../model/common/SendMessage";
import { CLOSE_SEND_MESSAGE, OPEN_SEND_MESSAGE } from "../actions";
import { IAction } from "../actions/IshAction";

const Initial: SendMessageState = {
  open: false
};

export const sendMessageReducer = (state: SendMessageState = {...Initial}, action: IAction<any>): ConfirmState => {
  switch (action.type) {
    case OPEN_SEND_MESSAGE: {
      return {
        ...state,
        open: true
      };
    }

    case CLOSE_SEND_MESSAGE: {
      return {
        ...state,
        open: false
      };
    }

    default:
      return state;
  }
};
