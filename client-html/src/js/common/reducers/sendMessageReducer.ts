/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { SendMessageState } from '../../model/common/SendMessage';
import {
  CLOSE_SEND_MESSAGE,
  OPEN_SEND_MESSAGE,
  SEND_MESSAGE_FAILED,
  SET_SEND_MESSAGE_PROCESS,
  START_SEND_MESSAGE
} from '../actions';
import { IAction } from '../actions/IshAction';

const Initial: SendMessageState = {
  open: false,
  sending: false,
  processId: null
};

export const sendMessageReducer = (state: SendMessageState = {...Initial}, action: IAction<any>): SendMessageState => {
  switch (action.type) {
    case OPEN_SEND_MESSAGE: {
      return {
        ...state,
        open: true,
        sending: false,
        processId: null
      };
    }

    case CLOSE_SEND_MESSAGE: {
      return {
        ...state,
        open: false,
        sending: false,
        processId: null
      };
    }

    case START_SEND_MESSAGE: {
      return {
        ...state,
        sending: true,
        processId: null
      };
    }

    case SET_SEND_MESSAGE_PROCESS: {
      return {
        ...state,
        processId: action.payload.processId
      };
    }

    case SEND_MESSAGE_FAILED: {
      return {
        ...state,
        sending: false,
        processId: null
      };
    }

    default:
      return state;
  }
};
