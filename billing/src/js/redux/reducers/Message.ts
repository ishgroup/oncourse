/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { IAction } from '../../models/IshAction';
import {
  CLEAR_MESSAGE,
  SHOW_MESSAGE,
  FETCH_FAIL
} from '../actions';
import { MessageState } from '../../models/Message';

const Initial: MessageState = {
  message: '',
  success: false
};

export const messageReducer = (state: MessageState = Initial, action: IAction): MessageState => {
  switch (action.type) {
    case FETCH_FAIL:
    case SHOW_MESSAGE:
      return {
        ...state,
        ...action.payload
      };

    case CLEAR_MESSAGE:
      return {
        ...Initial
      };
    default:
      return {
        ...state
      };
  }
};
